package org.epos.backoffice.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.epos.backoffice.api.exception.ApiResponseMessage;
import org.epos.backoffice.api.util.GroupFilter;
import org.epos.backoffice.bean.BackofficeOperationType;
import org.epos.backoffice.bean.ComputePermissionAbstract;
import org.epos.backoffice.bean.OperationTypeEnum;
import org.epos.backoffice.bean.User;
import org.epos.backoffice.service.ComputePermissionNoGroup;
import org.epos.backoffice.service.ComputePermissionWithGroup;
import org.epos.eposdatamodel.*;
import org.epos.handler.dbapi.DBAPIClient;
import org.epos.handler.dbapi.DBAPIClient.DeleteQuery;
import org.epos.handler.dbapi.DBAPIClient.UpdateQuery;
import org.epos.handler.dbapi.dbapiimplementation.CategoryDBAPI;
import org.epos.handler.dbapi.dbapiimplementation.CategorySchemeDBAPI;
import org.epos.handler.dbapi.dbapiimplementation.ContactPointDBAPI;
import org.epos.handler.dbapi.dbapiimplementation.ContractDBAPI;
import org.epos.handler.dbapi.dbapiimplementation.DataProductDBAPI;
import org.epos.handler.dbapi.dbapiimplementation.DistributionDBAPI;
import org.epos.handler.dbapi.dbapiimplementation.EquipmentDBAPI;
import org.epos.handler.dbapi.dbapiimplementation.FacilityDBAPI;
import org.epos.handler.dbapi.dbapiimplementation.OperationDBAPI;
import org.epos.handler.dbapi.dbapiimplementation.OrganizationDBAPI;
import org.epos.handler.dbapi.dbapiimplementation.PersonDBAPI;
import org.epos.handler.dbapi.dbapiimplementation.PublicationDBAPI;
import org.epos.handler.dbapi.dbapiimplementation.ServiceDBAPI;
import org.epos.handler.dbapi.dbapiimplementation.SoftwareApplicationDBAPI;
import org.epos.handler.dbapi.dbapiimplementation.SoftwareSourceCodeDBAPI;
import org.epos.handler.dbapi.dbapiimplementation.WebServiceDBAPI;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static org.epos.backoffice.api.util.EPOSDataModelHelper.*;
import static org.epos.backoffice.bean.OperationTypeEnum.*;
import static org.epos.backoffice.bean.RoleEnum.ADMIN;
import static org.epos.eposdatamodel.State.*;

public abstract class ScientificMetadataAbstractController<T extends EPOSDataModelEntity> extends BackofficeAbstractController<T> {

	public ScientificMetadataAbstractController(ObjectMapper objectMapper, HttpServletRequest request, Class<T> entityType) {
		super(objectMapper, request, entityType);
	}


	protected ResponseEntity<?> deleteMethod(String instance_id) {
		if (instance_id == null)
			return ResponseEntity
					.status(400)
					.body(new ApiResponseMessage(ApiResponseMessage.ERROR, "The [instance_id] field can't be left blank"));

		User user = getUserFromSession();

		List<T> list = dbapi.retrieve(entityType, new DBAPIClient.GetQuery().instanceId(instance_id));

		if (list.isEmpty())
			return ResponseEntity
					.status(400)
					.body(new ApiResponseMessage(ApiResponseMessage.ERROR, "No instance for this instanceId"));

		T instance = list.get(0);

		BackofficeOperationType operationType = new BackofficeOperationType()
				.operationType(instance.getState().equals(DRAFT) ? MANAGE_DRAFT : OTHER)
				.entityType(entityType)
				.userRole(user.getRole());

		GroupFilter groupFilter = new GroupFilter()
				.instanceGroup(instance.getGroups())
				.userGroup(user.getGroups())
				.operationType(operationType.getOperationType());

		ComputePermissionAbstract computePermission = new ComputePermissionWithGroup(operationType, groupFilter);
		if (!computePermission.isAuthorized() || groupFilter.notOk())
			return ResponseEntity
					.status(403)
					.body(new ApiResponseMessage(ApiResponseMessage.ERROR, "You don't have auth on the groups of this instance"));

		//dbapi.delete(entityType, new DBAPIClient.DeleteQuery().instanceId(instance_id));

		dbapi.delete(entityType, new DeleteQuery().instanceId(instance_id));

		return ResponseEntity
				.status(200)
				.body(new ApiResponseMessage(ApiResponseMessage.OK, "Instance deleted"));
	}


	/**
	 * It updates the parents of the entity passed as parameter
	 *
	 * @param son          the entity that is being updated
	 * @param user         the user that is editing the entity
	 * @param parentsClass the class of the parent entity
	 */
	protected <S extends EPOSDataModelEntity, P extends EPOSDataModelEntity> void updateParents(S son, User user, Class<P> parentsClass) {
		for (LinkedEntity l : getParents(son)) {
			P parent;

			List<P> d = new ArrayList<P>();
			try {
				d = dbapi.retrieve(parentsClass,
						new DBAPIClient.GetQuery()
						.metaId(l.getMetaId())
						.state(DRAFT)	
						.specificParameters(Map.of("editorId", user.getMetaId())));
			}catch(Exception e) {
				System.err.println(e.getLocalizedMessage());
			}

			//if the there isn't an already drafted father it just take the Published version of it
			if (d.isEmpty()) {
				try {
					d = dbapi.retrieve(parentsClass, new DBAPIClient.GetQuery().metaId(l.getMetaId()).state(PUBLISHED));
				}catch(Exception e) {
					System.err.println(e.getLocalizedMessage());
				}
			}
			// if there isn't again any dataproduct father then it means it will be created in the future, so it will create a Placeholder
			if (d.isEmpty()) {
				continue;
			}
			parent = d.get(0);

			//if the son already exist we need to edit the parent draft to point to it
			boolean done = false;
			if (son.getInstanceChangedId() != null) {
				for (LinkedEntity ld : getSons(parent)) {
					if (ld.getInstanceId().equals(son.getInstanceChangedId())) {
						ld.setInstanceId(son.getInstanceId());
						ld.setMetaId(son.getMetaId());
						ld.setUid(son.getUid());
						done = true;
					}
				}
				// if the parent wasn't linked to the son old entity or the son is totaly new
				// we need to explicitly link the new entity
			}
			if (!done) {
				addSon(parent, son);
			}

			parent.setEditorId(user.getMetaId());
			parent.setFileProvenance("instance created/modified with the backoffice");
			parent.setChangeComment(son.getChangeComment());

			State originalState = parent.getState();
			if (originalState.equals(PUBLISHED)) {
				// the parent draft doesn't exist before so there is the need to create it
				parent.setInstanceChangedId(parent.getInstanceId());
				parent.setState(DRAFT);
				LinkedEntity parentReference = dbapi.create(parent);
				parent.setInstanceId(parentReference.getInstanceId());
			} else if (originalState.equals(DRAFT)) {
				// the parent draft exist so we need to update it
				dbapi.update(parent, new DBAPIClient.UpdateQuery().hardUpdate(true));
			} else {
				System.err.println("Inconsistance of one entity state");
				//throw new RuntimeException("Inconsistance of one entity state");
			}

			Class<? extends EPOSDataModelEntity> grannyClass = getParentClass(parent);
			if (grannyClass != null)
				updateParents(parent, user, grannyClass);

		}

	}

	protected <P extends EPOSDataModelEntity, D extends EPOSDataModelEntity> void addSon(P parent, D son) {
		LinkedEntity linkedEntitySon = new LinkedEntity().uid(son.getUid()).entityType(son.getClass().getSimpleName()).metaId(son.getMetaId()).instanceId(son.getInstanceId());

		if (parent instanceof Distribution) {
			((Distribution) parent).setAccessService(linkedEntitySon);
			return;
		}
		if (parent instanceof WebService) {
			if (((WebService) parent).getSupportedOperation() == null)
				((WebService) parent).setSupportedOperation(new ArrayList<>());
			((WebService) parent).getSupportedOperation().add(linkedEntitySon);
			return;
		}
		if (parent instanceof DataProduct) {
			if (((DataProduct) parent).getDistribution() == null)
				((DataProduct) parent).setDistribution(new ArrayList<>());
			((DataProduct) parent).getDistribution().add(linkedEntitySon);
			return;
		}

		System.err.println("...just... Why?");
		//throw new IllegalArgumentException("...just... Why?");
	}

	protected ResponseEntity<?> postMethod(EPOSDataModelEntity body, boolean takeCareOfTheParent) {

		User user = getUserFromSession();

		if(body.getUid()==null) {
			return ResponseEntity.status(400).body(new ApiResponseMessage(ApiResponseMessage.ERROR, "UID undefined"));
		}

		body.setState(body.getState() == null ? State.DRAFT : body.getState());
		body.setEditorId(user.getMetaId());
		body.setFileProvenance("instance created with the backoffice");
		body.setInstanceId(null);

		OperationTypeEnum operationTypeEnum;
		if (body.getState().equals(State.DRAFT)) operationTypeEnum = MANAGE_DRAFT;
		else if (body.getState().equals(State.PUBLISHED)) operationTypeEnum = MANAGE_PUBLISHED;
		else operationTypeEnum = OTHER;

		BackofficeOperationType operationType = new BackofficeOperationType()
				.operationType(operationTypeEnum)
				.entityType(entityType)
				.userRole(user.getRole());

		ComputePermissionAbstract computePermission = new ComputePermissionNoGroup(operationType);
		if (!computePermission.isAuthorized())
			return ResponseEntity
					.status(403)
					.body(new ApiResponseMessage(ApiResponseMessage.ERROR, computePermission.generateErrorMessage()));

		if (body.getMetaId() != null &&
				!dbapi.retrieve(entityType, new DBAPIClient.GetQuery().state(State.DRAFT).metaId(body.getMetaId()).specificParameters(Map.of("editorId", user.getMetaId()))).isEmpty())
			return ResponseEntity
					.status(400)
					.body(new ApiResponseMessage(ApiResponseMessage.ERROR, "There is already a draft with the same metaId for this user"));

		if (Objects.nonNull(body.getInstanceChangedId()) && !body.getInstanceChangedId().isBlank()){
			List<T> retrieved = dbapi.retrieve(entityType, new DBAPIClient.GetQuery().instanceId(body.getInstanceChangedId()));

			if (retrieved.isEmpty())
				return ResponseEntity
						.status(400)
						.body(new ApiResponseMessage(ApiResponseMessage.ERROR, "Non existing instance with that instaceChangedId"));

			GroupFilter groupFilter = new GroupFilter()
					.instanceGroup(retrieved.get(0).getGroups())
					.userGroup(user.getGroups())
					.operationType(operationType.getOperationType());

			if (groupFilter.notOk())
				return ResponseEntity
						.status(403)
						.body(new ApiResponseMessage(ApiResponseMessage.ERROR, computePermission.generateErrorMessage()));
		}

		// deletion phaseee
		if(Boolean.parseBoolean(body.getToBeDelete())){
			try {

				List<? extends EPOSDataModelEntity> originalInstanceList = dbapi.retrieve(body.getClass(), new DBAPIClient.GetQuery().instanceId(body.getInstanceChangedId()));
				if(originalInstanceList.isEmpty()){
					return ResponseEntity
							.status(400)
							.body(new ApiResponseMessage(ApiResponseMessage.ERROR, "The instanceChangedId doesn't correspond to any existing instance"));
				}

				EPOSDataModelEntity originalInstance = originalInstanceList.get(0);
				if (!Objects.equals(originalInstance.getState(),PUBLISHED)){
					return ResponseEntity
							.status(400)
							.body(new ApiResponseMessage(ApiResponseMessage.ERROR, "You can only delete in this way a published instance"));
				}

				originalInstance.setInstanceId(null);

				originalInstance.setMetaId(Objects.requireNonNull(body.getMetaId(), "\"metaId\""));
				originalInstance.setInstanceChangedId(Objects.requireNonNull(body.getInstanceChangedId(), "\"instanceChangedId\""));


				originalInstance.setToBeDelete("true");
				originalInstance.setState(body.getState());
				originalInstance.setEditorId(user.getMetaId());
				originalInstance.setFileProvenance("instance created with the backoffice");

				body = originalInstance;
			} catch (NullPointerException e) {
				return ResponseEntity
						.status(400)
						.body(new ApiResponseMessage(ApiResponseMessage.ERROR, "Instance miss this field: " + e.getMessage()));
			}
		}

		dbapi.setTransactionModeAuto(true);
		dbapi.startTransaction();


		LinkedEntity reference;
		try {

			// save the entity and get the reference to it
			reference = dbapi.create(body);
			body.setInstanceId(reference.getInstanceId());
			body.setMetaId(reference.getMetaId());
			// take care of the parents
			if (takeCareOfTheParent) {
				Class<? extends EPOSDataModelEntity> parentClass = getParentClass(body);
				if (parentClass != null)
					updateParents(body, user, parentClass);
			}

		} catch (Exception e) {
			dbapi.rollbackTransaction();
			return ResponseEntity.status(400).body(new ApiResponseMessage(ApiResponseMessage.ERROR, "Something went wrong during the persisting of the new instance: "+e.getMessage()));
		}

		dbapi.closeTransaction(true);
		dbapi.setTransactionModeAuto(true);

		return ResponseEntity.status(201).body(reference);
	}

	protected ResponseEntity<?> updateMethod(EPOSDataModelEntity body, boolean takeCareOfTheParent) {
		
		User user = getUserFromSession();

		body.setState(body.getState() == null ? State.DRAFT : body.getState());
		body.setEditorId(user.getMetaId());
		body.setFileProvenance("instance created with the backoffice");

		OperationTypeEnum operationTypeEnum;
		if (body.getState().equals(State.DRAFT)) operationTypeEnum = MANAGE_DRAFT;
		else if (body.getState().equals(State.PUBLISHED)) operationTypeEnum = MANAGE_PUBLISHED;
		else operationTypeEnum = OTHER;

		BackofficeOperationType operationType = new BackofficeOperationType()
				.operationType(operationTypeEnum)
				.entityType(entityType)
				.userRole(user.getRole());

		ComputePermissionAbstract computePermission = new ComputePermissionNoGroup(operationType);
		if (!computePermission.isAuthorized())
			return ResponseEntity
					.status(403)
					.body(new ApiResponseMessage(ApiResponseMessage.ERROR, computePermission.generateErrorMessage()));

		if (body.getInstanceId() == null)
			return ResponseEntity
					.status(400)
					.body(new ApiResponseMessage(ApiResponseMessage.ERROR, "instanceId required."));


		List<T> retrieved = dbapi.retrieve(entityType, new DBAPIClient.GetQuery().state(DRAFT).instanceId(body.getInstanceId()));
		if (retrieved.isEmpty()) {
			retrieved = dbapi.retrieve(entityType, new DBAPIClient.GetQuery().state(SUBMITTED).instanceId(body.getInstanceId()));
			if (retrieved.isEmpty()) {
				retrieved = dbapi.retrieve(entityType, new DBAPIClient.GetQuery().state(PUBLISHED).instanceId(body.getInstanceId()));
				if (retrieved.isEmpty()) {
					retrieved = dbapi.retrieve(entityType, new DBAPIClient.GetQuery().state(ARCHIVED).instanceId(body.getInstanceId()));
					if (retrieved.isEmpty()) {
						return ResponseEntity
								.status(400)
								.body(new ApiResponseMessage(ApiResponseMessage.ERROR, "There isn't any existing entry with this instanceId"));
					}
				}
			}

		}

		T instance = retrieved.get(0);


		if(instance.getState().equals(PUBLISHED)) body.setState(DRAFT);
		if(body.getInstanceChangedId()!=null && body.getInstanceChangedId().isBlank()) body.setInstanceChangedId(null);
		if(body.getInstanceChangedId()!=null) body.setInstanceChangedId(null);
		body.setInstanceId(instance.getInstanceId());

		GroupFilter groupFilter = new GroupFilter()
				.instanceGroup(instance.getGroups())
				.userGroup(user.getGroups())
				.operationType(operationType.getOperationType());
		if (groupFilter.notOk())
			return ResponseEntity
					.status(403)
					.body(new ApiResponseMessage(ApiResponseMessage.ERROR, computePermission.generateErrorMessage()));


		dbapi.setTransactionModeAuto(true);
		dbapi.startTransaction();

		ResponseEntity<?> response = null;
		LinkedEntity reference = null;
		
		try {
			//instanceId = body.getInstanceId();
			body.setMetaId(instance.getMetaId());

			// TODO: temporary solution
			if(!instance.getState().equals(State.PUBLISHED)) {
				//dbapi.update(body, new UpdateQuery().hardUpdate(true));

				try {
					// save the entity and get the reference to it
					reference = dbapi.create(body);
					body.setInstanceId(reference.getInstanceId());
					body.setMetaId(reference.getMetaId());
					// take care of the parents
					if (takeCareOfTheParent) {
						Class<? extends EPOSDataModelEntity> parentClass = getParentClass(body);
						if (parentClass != null)
							updateParents(body, user, parentClass);
					}
					deleteMethod(instance.getInstanceId());
				}catch (Exception e) {
					dbapi.rollbackTransaction();
					return ResponseEntity.status(400).body(new ApiResponseMessage(ApiResponseMessage.ERROR, "Something went wrong during the persisting of the new instance: "+e.getLocalizedMessage()));
				}
			}
			else {
				response = postMethod(body, takeCareOfTheParent);

				// take care of the dataproducts parents
				if (takeCareOfTheParent) {
					Class<? extends EPOSDataModelEntity> parentClass = getParentClass(body);
					if (parentClass != null)
						updateParents(body, user, parentClass);
				}
			}

		} catch (Exception e) {
			dbapi.rollbackTransaction();
			return ResponseEntity.status(400).body(new ApiResponseMessage(ApiResponseMessage.ERROR, "Something went wrong during the persisting of the new instance: "+e.getLocalizedMessage()));
		}

		dbapi.closeTransaction(true);
		dbapi.setTransactionModeAuto(true);

		if(response != null) return response;
		else return ResponseEntity.status(201).body(reference);
	}

	protected ResponseEntity<?> updateStateMethod(String instance_id, State newState, Boolean justThisOne) {

		User user = getUserFromSession();

		List<T> instanceWithStateToBeUpdatedList = dbapi.retrieve(entityType, new DBAPIClient.GetQuery().instanceId(instance_id));

		if (instanceWithStateToBeUpdatedList.isEmpty()) {
			return ResponseEntity
					.status(404)
					.body(new ApiResponseMessage(ApiResponseMessage.ERROR, "No instance with that instanceId"));
		}
		T instanceWithStateToBeUpdated = instanceWithStateToBeUpdatedList.get(0);
		State originalState = instanceWithStateToBeUpdated.getState();

		OperationTypeEnum operationTypeEnum;
		if (newState.equals(State.SUBMITTED) && originalState.equals(State.DRAFT))
			operationTypeEnum = DATAPRODUCT__CHANGE_STATUS__DRAFT_SUBMITTED;
		else if (newState.equals(State.DRAFT) && originalState.equals(State.SUBMITTED))
			operationTypeEnum = DATAPRODUCT__CHANGE_STATUS__SUBMITTED_DRAFT;
		else if (newState.equals(State.PUBLISHED) && originalState.equals(State.SUBMITTED))
			operationTypeEnum = DATAPRODUCT__CHANGE_STATUS__SUBMITTED_PUBLISHED;
		else if (newState.equals(State.DISCARDED) && originalState.equals(State.SUBMITTED))
			operationTypeEnum = DATAPRODUCT__CHANGE_STATUS__SUBMITTED_DISCARDED;
		else operationTypeEnum = OTHER;

		BackofficeOperationType operationType = new BackofficeOperationType()
				.operationType(operationTypeEnum)
				.entityType(entityType)
				.userRole(user.getRole());

		GroupFilter groupFilter = new GroupFilter()
				.instanceGroup(instanceWithStateToBeUpdated.getGroups())
				.userGroup(user.getGroups())
				.operationType(operationType.getOperationType());

		ComputePermissionAbstract computePermission = new ComputePermissionNoGroup(operationType);
		if (!computePermission.isAuthorized() || groupFilter.notOk())
			return ResponseEntity
					.status(403)
					.body(new ApiResponseMessage(ApiResponseMessage.ERROR, computePermission.generateErrorMessage()));




		dbapi.setTransactionModeAuto(true);
		dbapi.startTransaction();

		try {

			if(justThisOne != null && justThisOne)
				dbapi.update(instanceWithStateToBeUpdated, new DBAPIClient.UpdateQuery().state(newState));
			else
				updateInstanceStateAndItsSonState(instanceWithStateToBeUpdated, user, getSonClass(entityType), newState);

		} catch (Exception e) {
			e.printStackTrace();
			dbapi.rollbackTransaction();
			return ResponseEntity.status(400).body(new ApiResponseMessage(ApiResponseMessage.ERROR, "Something went wrong during the persisting of the new instance: "+e.getMessage()));
		}
		//dbapi.rollbackTransaction();


		dbapi.closeTransaction(true);
		dbapi.setTransactionModeAuto(true);

		return ResponseEntity.status(201).body(
				new ApiResponseMessage(ApiResponseMessage.OK, "Correctly updated the state of the instance")
				);

	}

	@SuppressWarnings("unchecked")
	protected <S extends EPOSDataModelEntity, P extends EPOSDataModelEntity> void updateInstanceStateAndItsSonState(S instance, User user, Class<P> sonClass, State newState)  {

		//archive the old instance if exist
		if (instance.getInstanceChangedId() != null && !instance.getInstanceChangedId().isBlank()){
			S instanceOriginal = (S) dbapi.retrieve(instance.getClass(), new DBAPIClient.GetQuery().instanceId(instance.getInstanceChangedId())).get(0);

			if (instanceOriginal.getState().equals(PUBLISHED)) {
				dbapi.update(instanceOriginal, new DBAPIClient.UpdateQuery().state(ARCHIVED));
				dbapi.flush();
			}
		}

		if(Boolean.parseBoolean(instance.getToBeDelete()) && Objects.equals(newState, PUBLISHED)) {
			dbapi.delete(instance.getClass(), new DBAPIClient.DeleteQuery().instanceId(instance.getInstanceId()));
			Class<? extends EPOSDataModelEntity> parentClass = getParentClass(instance);
			if (parentClass != null)
				removeArchivedInstanceFromNewParentInstance(instance,parentClass);

		} else {
			dbapi.update(instance, new DBAPIClient.UpdateQuery().state(newState));
		}


		for (LinkedEntity l : getSons(instance)) {

			P instanceSon = dbapi.retrieve(sonClass, new DBAPIClient.GetQuery().instanceId(l.getInstanceId())).get(0);

			if(instanceSon.getState().equals(PUBLISHED)) continue;

			try {
				updateInstanceStateAndItsSonState(instanceSon, user, getSonClass(instanceSon), newState);
			} catch(IllegalArgumentException ignored){
				updateInstanceStateAndItsSonState(instanceSon, user, null, newState);
			}
		}
	}

	private <S extends EPOSDataModelEntity, P extends EPOSDataModelEntity> void removeArchivedInstanceFromNewParentInstance(S instance, Class<P> parentClass) {
		for (LinkedEntity lp : getParents(instance)) {
			P parent = dbapi.retrieve(parentClass, new DBAPIClient.GetQuery().instanceId(lp.getInstanceId())).get(0);

			if(parent.getState().equals(ARCHIVED)) continue;

			getSons(parent).removeIf(linkedEntity -> Objects.equals(linkedEntity.getInstanceId(), instance.getInstanceId()));
		}
	}


}