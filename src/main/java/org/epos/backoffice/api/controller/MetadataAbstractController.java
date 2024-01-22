package org.epos.backoffice.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.epos.backoffice.api.exception.ApiResponseMessage;
import org.epos.backoffice.api.util.DataProductManager;
import org.epos.backoffice.api.util.DistributionManager;
import org.epos.backoffice.api.util.GroupFilter;
import org.epos.backoffice.api.util.OperationManager;
import org.epos.backoffice.api.util.WebServiceManager;
import org.epos.backoffice.bean.BackofficeOperationType;
import org.epos.backoffice.bean.ComputePermissionAbstract;
import org.epos.backoffice.bean.OperationTypeEnum;
import org.epos.backoffice.bean.User;
import org.epos.backoffice.service.ComputePermissionNoGroup;
import org.epos.eposdatamodel.*;
import org.epos.handler.dbapi.DBAPIClient;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

import static org.epos.backoffice.api.util.EPOSDataModelHelper.getParentClass;
import static org.epos.backoffice.api.util.EPOSDataModelHelper.getParents;
import static org.epos.backoffice.api.util.EPOSDataModelHelper.getSonClass;
import static org.epos.backoffice.api.util.EPOSDataModelHelper.getSons;
import static org.epos.backoffice.bean.OperationTypeEnum.DATAPRODUCT__CHANGE_STATUS__DRAFT_SUBMITTED;
import static org.epos.backoffice.bean.OperationTypeEnum.DATAPRODUCT__CHANGE_STATUS__SUBMITTED_DISCARDED;
import static org.epos.backoffice.bean.OperationTypeEnum.DATAPRODUCT__CHANGE_STATUS__SUBMITTED_DRAFT;
import static org.epos.backoffice.bean.OperationTypeEnum.DATAPRODUCT__CHANGE_STATUS__SUBMITTED_PUBLISHED;
import static org.epos.backoffice.bean.OperationTypeEnum.OTHER;
import static org.epos.eposdatamodel.State.ARCHIVED;
import static org.epos.eposdatamodel.State.PUBLISHED;

import java.util.*;

public abstract class MetadataAbstractController<T extends EPOSDataModelEntity> extends AbstractController<T> {
	
	protected static DBAPIClient dbapi = new DBAPIClient();

	public MetadataAbstractController(ObjectMapper objectMapper, HttpServletRequest request, Class<T> entityType) {
		super(objectMapper, request, entityType);
	}
	protected ResponseEntity<?> getMethod(String meta_id, String instance_id) {
		if (meta_id == null)
			return ResponseEntity
					.status(400)
					.body(new ApiResponseMessage(1, "The [meta_id] field can't be left blank"));
		if(instance_id == null) {
			instance_id = "all";
		}

		User user = getUserFromSession();

		List<T> revertedList = new ArrayList<>();
		List<T> list = new ArrayList<T>();
		
		if(entityType.equals(DataProduct.class)) list.addAll((Collection<? extends T>) DataProductManager.getDataProduct(meta_id,instance_id, user).getListOfEntities());
		if(entityType.equals(Distribution.class)) list.addAll((Collection<? extends T>) DistributionManager.getDistribution(meta_id,instance_id, user).getListOfEntities());
		if(entityType.equals(WebService.class)) list.addAll((Collection<? extends T>) WebServiceManager.getWebService(meta_id,instance_id, user).getListOfEntities());
		if(entityType.equals(Operation.class)) list.addAll((Collection<? extends T>) OperationManager.getOperation(meta_id,instance_id, user).getListOfEntities());

		list.forEach(e -> revertedList.add(0, e));
		
		if (list.isEmpty())
			return ResponseEntity.status(404).body("[]");

		return ResponseEntity
				.status(200)
				.body(gson.toJson(revertedList));
	}


	protected ResponseEntity<?> deleteMethod(String instance_id) {
		if (instance_id == null)
			return ResponseEntity
					.status(400)
					.body(new ApiResponseMessage(ApiResponseMessage.ERROR, "The [instance_id] field can't be left blank"));

		User user = getUserFromSession();

		boolean done = false;
		if(entityType.equals(DataProduct.class)) done = DataProductManager.deleteDataProduct(instance_id, user);
		if(entityType.equals(Distribution.class)) done = DistributionManager.deleteDistribution(instance_id, user);
		if(entityType.equals(WebService.class)) done = WebServiceManager.deleteWebService(instance_id, user);
		if(entityType.equals(Operation.class)) done = OperationManager.deleteOperation(instance_id, user);

		if(done) return ResponseEntity.status(200).body(new ApiResponseMessage(ApiResponseMessage.OK, "Instance deleted"));
		else return ResponseEntity.status(400).body(new ApiResponseMessage(ApiResponseMessage.ERROR, "Instance not deleted"));
	}

	protected ResponseEntity<?> postMethod(EPOSDataModelEntity body, boolean takeCareOfTheParent) {

		User user = getUserFromSession();
		
		if(entityType.equals(DataProduct.class)) {
			ApiResponseMessage response = DataProductManager.createDataProduct((DataProduct) body, user, true, true);
			if(response.getCode()==ApiResponseMessage.OK) return ResponseEntity.status(201).body(response.getEntity());
			else return ResponseEntity.status(400).body(response.getMessage());
		}
		
		if(entityType.equals(Distribution.class)) {
			ApiResponseMessage response = DistributionManager.createDistribution((Distribution) body, user, true, true);
			if(response.getCode()==ApiResponseMessage.OK) return ResponseEntity.status(201).body(response.getEntity());
			else return ResponseEntity.status(400).body(response.getMessage());
		}
		
		if(entityType.equals(WebService.class)) {
			ApiResponseMessage response = WebServiceManager.createWebService((WebService) body, user, true, true);
			if(response.getCode()==ApiResponseMessage.OK) return ResponseEntity.status(201).body(response.getEntity());
			else return ResponseEntity.status(400).body(response.getMessage());
		}
		
		if(entityType.equals(Operation.class)) {
			ApiResponseMessage response = OperationManager.createOperation((Operation) body, user, true, true);
			if(response.getCode()==ApiResponseMessage.OK) return ResponseEntity.status(201).body(response.getEntity());
			else return ResponseEntity.status(400).body(response.getMessage());
		}

		return ResponseEntity.status(400).body(null);
	}

	protected ResponseEntity<?> updateMethod(EPOSDataModelEntity body, boolean takeCareOfTheParent) {

		User user = getUserFromSession();

		if(entityType.equals(DataProduct.class)) {
			ApiResponseMessage response = DataProductManager.updateDataProduct((DataProduct) body, user, true, true);
			if(response.getCode()==ApiResponseMessage.OK) return ResponseEntity.status(201).body(response.getEntity());
			else return ResponseEntity.status(400).body(response.getMessage());
		}
		
		if(entityType.equals(Distribution.class)) {
			ApiResponseMessage response = DistributionManager.updateDistribution((Distribution) body, user, true, true);
			if(response.getCode()==ApiResponseMessage.OK) return ResponseEntity.status(201).body(response.getEntity());
			else return ResponseEntity.status(400).body(response.getMessage());
		}
		if(entityType.equals(WebService.class)) {
			ApiResponseMessage response = WebServiceManager.updateWebService((WebService) body, user, true, true);
			if(response.getCode()==ApiResponseMessage.OK) return ResponseEntity.status(201).body(response.getEntity());
			else return ResponseEntity.status(400).body(response.getMessage());
		}
		if(entityType.equals(Operation.class)) {
			ApiResponseMessage response = OperationManager.updateOperation((Operation) body, user, true, true);
			if(response.getCode()==ApiResponseMessage.OK) return ResponseEntity.status(201).body(response.getEntity());
			else return ResponseEntity.status(400).body(response.getMessage());
		}

		return ResponseEntity.status(400).body(null);
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