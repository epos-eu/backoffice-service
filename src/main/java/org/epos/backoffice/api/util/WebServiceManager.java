package org.epos.backoffice.api.util;

import static org.epos.backoffice.bean.OperationTypeEnum.GET_ALL;
import static org.epos.backoffice.bean.OperationTypeEnum.GET_SINGLE;
import static org.epos.backoffice.bean.RoleEnum.ADMIN;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.epos.backoffice.api.exception.ApiResponseMessage;
import org.epos.backoffice.bean.BackofficeOperationType;
import org.epos.backoffice.bean.ComputePermissionAbstract;
import org.epos.backoffice.bean.EntityTypeEnum;
import org.epos.backoffice.bean.User;
import org.epos.backoffice.service.ComputePermissionNoGroup;
import org.epos.eposdatamodel.DataProduct;
import org.epos.eposdatamodel.Distribution;
import org.epos.eposdatamodel.WebService;
import org.epos.eposdatamodel.LinkedEntity;
import org.epos.eposdatamodel.Operation;
import org.epos.eposdatamodel.State;
import org.epos.handler.dbapi.DBAPIClient;
import org.epos.handler.dbapi.DBAPIClient.DeleteQuery;
import org.epos.handler.dbapi.DBAPIClient.SaveQuery;
import org.epos.handler.dbapi.DBAPIClient.UpdateQuery;

public class WebServiceManager {

	protected static DBAPIClient dbapi = new DBAPIClient();

	public static ApiResponseMessage getWebService(String meta_id, String instance_id, User user) {
		//dbapi.setMetadataMode(false);
		if (meta_id == null)
			return new ApiResponseMessage(1, "The [meta_id] field can't be left blank");
		if(instance_id == null) {
			instance_id = "all";
		}

		BackofficeOperationType operationType = new BackofficeOperationType()
				.operationType(meta_id.equals("all") ? GET_ALL : GET_SINGLE)
				.entityType(DataProduct.class)
				.userRole(user.getRole());


		ComputePermissionAbstract computePermission = new ComputePermissionNoGroup(operationType);
		if (!computePermission.isAuthorized())
			return new ApiResponseMessage(1, computePermission.generateErrorMessage());

		System.out.println(meta_id+" "+instance_id);

		List<WebService> list;
		if (meta_id.equals("all")) {
			list = dbapi.retrieve(WebService.class, new DBAPIClient.GetQuery());	
		} else {
			if(instance_id.equals("all")) {
				list = dbapi.retrieve(WebService.class, new DBAPIClient.GetQuery());	
				list = list.stream()
						.filter(
								elem -> elem.getMetaId().equals(meta_id)
								)
						.collect(Collectors.toList());

			}else {
				list = dbapi.retrieve(WebService.class, new DBAPIClient.GetQuery().instanceId(instance_id));
			}
		}

		list = list.stream()
				.filter(
						elem -> user.getRole().equals(ADMIN) || elem.getState().equals(State.PUBLISHED) ||
						(elem.getState().equals(State.DRAFT) && user.getMetaId().equals(elem.getEditorId()))
						)
				.filter(
						elem -> {
							GroupFilter groupFilter = new GroupFilter()
									.instanceGroup(elem.getGroups())
									.userGroup(user.getGroups())
									.operationType(operationType.getOperationType());
							return groupFilter.isOk();
						}
						)
				.collect(Collectors.toList());

		List<WebService> revertedList = new ArrayList<>();
		list.forEach(e -> revertedList.add(0, e));

		if (list.isEmpty())
			return new ApiResponseMessage(ApiResponseMessage.OK, new ArrayList<WebService>());

		return new ApiResponseMessage(ApiResponseMessage.OK, list);
	}

	/**
	 * 
	 * @param WebService
	 * @param user
	 * @return
	 */
	public static ApiResponseMessage createWebService(WebService webservice, User user, boolean parents, boolean sons) {
		/** ID MANAGEMENT 
		 * if UID == NULL --> Generate a new UID
		 * Brand new WebService? --> InstanceId = null && InstanceChangeId == null
		 * New WebService from existing one? --> InstanceChangeId == OLD InstanceId
		 * 
		 **/
		if(webservice.getUid()==null) {
			System.err.println("UID undefined, generating a new one");
			webservice.setUid(webservice.getClass().getSimpleName().toLowerCase()+"/"+UUID.randomUUID());
		}
		webservice.setInstanceId(null);
		webservice.setInstanceChangedId(null);

		// Check if exists a version PUBLISHED or ARCHIVED if MetaId!=null
		if (webservice.getMetaId() != null) {
			List<WebService> retrieved = dbapi.retrieve(WebService.class, new DBAPIClient.GetQuery().state(State.PUBLISHED).metaId(webservice.getMetaId()));
			if(retrieved.isEmpty())
				retrieved = dbapi.retrieve(WebService.class, new DBAPIClient.GetQuery().state(State.ARCHIVED).metaId(webservice.getMetaId()));
			if(retrieved.isEmpty())
				retrieved = dbapi.retrieve(WebService.class, new DBAPIClient.GetQuery().state(State.SUBMITTED).metaId(webservice.getMetaId()));
			if(!retrieved.isEmpty()) {
				webservice.setInstanceChangedId(retrieved.get(0).getInstanceId());
			}	
		}

		webservice.setState(State.DRAFT);
		webservice.setEditorId(user.getMetaId());
		webservice.setFileProvenance("instance created with the backoffice");

		if(!ManagePermissions.checkPermissions(webservice, EntityTypeEnum.WEBSERVICE, user)) 
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "You don't have auth on the groups of this instance");

		dbapi.setTransactionModeAuto(true);
		dbapi.startTransaction();

		LinkedEntity reference;
		try {

			// save the entity and get the reference to it
			reference = dbapi.create(webservice);
			webservice.setInstanceId(reference.getInstanceId());
			webservice.setMetaId(reference.getMetaId());
			//TODO: Parents?
		} catch (Exception e) {
			e.printStackTrace();
			dbapi.rollbackTransaction();
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "Something went wrong during the persisting of the new instance: "+e.getMessage());
		}

		dbapi.closeTransaction(true);
		dbapi.setTransactionModeAuto(true);

		manageNewDraftRelations(webservice, reference, user, parents, sons);

		return new ApiResponseMessage(ApiResponseMessage.OK, reference);
	}

	/**
	 * 
	 * @param webservice
	 * @param user
	 * @return
	 */
	public static ApiResponseMessage updateWebService(WebService webservice, User user, boolean parents, boolean sons) {

		if(webservice.getState()!=null && (webservice.getState().equals(State.ARCHIVED) || webservice.getState().equals(State.PUBLISHED))) {
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "Unable to update a ARCHIVED or PUBLISHED instance");
		}
		if (webservice.getInstanceId() == null) {
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "InstanceId required");
		}
		if(webservice.getInstanceChangedId() == null || webservice.getInstanceChangedId().isEmpty())
			webservice.setInstanceChangedId(null);

		webservice.setEditorId(user.getMetaId());
		webservice.setFileProvenance("instance created with the backoffice");

		if(!ManagePermissions.checkPermissions(webservice, EntityTypeEnum.WEBSERVICE, user)) 
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "You don't have auth on the groups of this instance");

		dbapi.setTransactionModeAuto(true);
		dbapi.startTransaction();
		LinkedEntity reference = null;
		try {
			reference = dbapi.createUpdate(webservice, new SaveQuery().setInstanceId(webservice.getInstanceId()));//dbapi.hardUpdate(webservice);
		} catch (Exception e) {
			dbapi.rollbackTransaction();
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "Something went wrong during the persisting of the new instance: "+e.getMessage());
		}

		dbapi.closeTransaction(true);
		dbapi.setTransactionModeAuto(true);

		return new ApiResponseMessage(ApiResponseMessage.OK, reference);
	}

	/**
	 * 
	 * @param webservice
	 * @return
	 */
	public static boolean deleteWebService(String instance_id, User user) {
		List<WebService> list = dbapi.retrieve(WebService.class, new DBAPIClient.GetQuery().instanceId(instance_id));

		if (list.isEmpty()) return false;
		WebService instance = list.get(0);
		dbapi.setTransactionModeAuto(true);
		dbapi.startTransaction();

		dbapi.delete(WebService.class, new DeleteQuery().instanceId(instance.getInstanceId()));

		dbapi.closeTransaction(true);
		dbapi.setTransactionModeAuto(true);

		return true;
	}

	private static void manageNewDraftRelations(WebService webservice, LinkedEntity relation, User user, boolean parents, boolean sons) {

		if(parents) {
			if(webservice.getDistribution()!=null)
				for(LinkedEntity le : webservice.getDistribution()) {
					Distribution distribution = (Distribution) DistributionManager.getDistribution(le.getMetaId(), le.getInstanceId(), user).getListOfEntities().get(0);
					distribution.setAccessService(relation);
					DistributionManager.createDistribution(distribution, user, true, false);
				}
		}
		if(sons) {
			if(webservice.getSupportedOperation()!=null)
				for(LinkedEntity le : webservice.getSupportedOperation()) {
					Operation operation = (Operation) OperationManager.getOperation(le.getMetaId(), le.getInstanceId(), user).getListOfEntities().get(0);
					operation.getWebservice().add(relation);
					OperationManager.createOperation(operation, user, false, true);
				}
		}
	}


}
