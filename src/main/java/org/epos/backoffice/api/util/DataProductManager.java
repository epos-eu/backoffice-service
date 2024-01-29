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
import org.epos.eposdatamodel.LinkedEntity;
import org.epos.eposdatamodel.State;
import org.epos.handler.dbapi.DBAPIClient;
import org.epos.handler.dbapi.DBAPIClient.DeleteQuery;
import org.epos.handler.dbapi.DBAPIClient.SaveQuery;

public class DataProductManager {

	protected static DBAPIClient dbapi = new DBAPIClient();

	public static ApiResponseMessage getDataProduct(String meta_id, String instance_id, User user) {
		//dbapi.setMetadataMode(false);
		dbapi.setMetadataMode(false);
		
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

		List<DataProduct> list;
		if (meta_id.equals("all")) {
			list = dbapi.retrieve(DataProduct.class, new DBAPIClient.GetQuery());	
		} else {
			if(instance_id.equals("all")) {
				list = dbapi.retrieve(DataProduct.class, new DBAPIClient.GetQuery());	
				list = list.stream()
						.filter(
								elem -> elem.getMetaId().equals(meta_id)
								)
						.collect(Collectors.toList());
				
			}else {
				list = dbapi.retrieve(DataProduct.class, new DBAPIClient.GetQuery().instanceId(instance_id));
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

		List<DataProduct> revertedList = new ArrayList<>();
		list.forEach(e -> revertedList.add(0, e));
		
		System.out.println("THE LIST: "+list);
		
		if (list.isEmpty())
			return new ApiResponseMessage(ApiResponseMessage.OK, new ArrayList<DataProduct>());
		
		return new ApiResponseMessage(ApiResponseMessage.OK, list);
	}

	/**
	 * 
	 * @param dataProduct
	 * @param user
	 * @return
	 */
	public static ApiResponseMessage createDataProduct(DataProduct dataProduct, User user, boolean parents, boolean sons) {
		/** ID MANAGEMENT 
		 * if UID == NULL --> Generate a new UID
		 * Brand new DataProduct? --> InstanceId = null && InstanceChangeId == null
		 * New DataProduct from existing one? --> InstanceChangeId == OLD InstanceId
		 * 
		 **/
		if(dataProduct.getUid()==null) {
			System.err.println("UID undefined, generating a new one");
			dataProduct.setUid(dataProduct.getClass().getSimpleName().toLowerCase()+"/"+UUID.randomUUID());
		}
		dataProduct.setInstanceId(null);
		dataProduct.setInstanceChangedId(null);

		// Check if exists a version PUBLISHED or ARCHIVED if MetaId!=null
		if (dataProduct.getMetaId() != null) {
			List<DataProduct> retrieved = dbapi.retrieve(DataProduct.class, new DBAPIClient.GetQuery().state(State.PUBLISHED).metaId(dataProduct.getMetaId()));
			if(retrieved.isEmpty())
				retrieved = dbapi.retrieve(DataProduct.class, new DBAPIClient.GetQuery().state(State.ARCHIVED).metaId(dataProduct.getMetaId()));
			if(retrieved.isEmpty())
				retrieved = dbapi.retrieve(DataProduct.class, new DBAPIClient.GetQuery().state(State.SUBMITTED).metaId(dataProduct.getMetaId()));
			if(!retrieved.isEmpty()) {
				dataProduct.setInstanceChangedId(retrieved.get(0).getInstanceId());
			}	
		}

		dataProduct.setState(State.DRAFT);
		dataProduct.setEditorId(user.getMetaId());
		dataProduct.setFileProvenance("instance created with the backoffice");
		
		if(!ManagePermissions.checkPermissions(dataProduct, EntityTypeEnum.DATAPRODUCT, user)) 
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "You don't have auth on the groups of this instance");

		dbapi.setTransactionModeAuto(true);
		dbapi.startTransaction();

		LinkedEntity reference;
		try {

			// save the entity and get the reference to it
			reference = dbapi.create(dataProduct);
			dataProduct.setInstanceId(reference.getInstanceId());
			dataProduct.setMetaId(reference.getMetaId());
			//TODO: Parents?
		} catch (Exception e) {
			e.printStackTrace();
			dbapi.rollbackTransaction();
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "Something went wrong during the persisting of the new instance: "+e.getMessage());
		}

		dbapi.closeTransaction(true);
		dbapi.setTransactionModeAuto(true);

		manageNewDraftRelations(dataProduct, reference, user, parents, sons);

		return new ApiResponseMessage(ApiResponseMessage.OK, reference);
	}

	/**
	 * 
	 * @param dataProduct
	 * @param user
	 * @return
	 */
	public static ApiResponseMessage updateDataProduct(DataProduct dataProduct, User user, boolean parents, boolean sons) {

		if(dataProduct.getState()!=null && (dataProduct.getState().equals(State.ARCHIVED) || dataProduct.getState().equals(State.PUBLISHED))) {
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "Unable to update a ARCHIVED or PUBLISHED instance");
		}
		if (dataProduct.getInstanceId() == null) {
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "InstanceId required");
		}
		if(dataProduct.getInstanceChangedId() == null || dataProduct.getInstanceChangedId().isEmpty())
			dataProduct.setInstanceChangedId(null);

		dataProduct.setEditorId(user.getMetaId());
		dataProduct.setFileProvenance("instance created with the backoffice");
		
		if(!ManagePermissions.checkPermissions(dataProduct, EntityTypeEnum.DATAPRODUCT, user)) 
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "You don't have auth on the groups of this instance");

		dbapi.setTransactionModeAuto(true);
		dbapi.startTransaction();

		System.out.println("DATAPROD: "+dataProduct);
		
		LinkedEntity reference = null;
		try {
			reference = dbapi.createUpdate(dataProduct, new SaveQuery().setInstanceId(dataProduct.getInstanceId()));//dbapi.hardUpdate(dataProduct);
		} catch (Exception e) {
			//e.printStackTrace();
			dbapi.rollbackTransaction();
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "Something went wrong during the persisting of the new instance: "+e.getMessage());
		}

		dbapi.closeTransaction(true);
		dbapi.setTransactionModeAuto(true);

		manageNewDraftRelations(dataProduct, reference, user, parents, sons);
		
		return new ApiResponseMessage(ApiResponseMessage.OK, reference);
	}
	
	/**
	 * 
	 * @param dataProduct
	 * @param user
	 * @return
	 */
	public static ApiResponseMessage updateStateDataProduct(DataProduct dataProduct, User user, State newState, boolean parents, boolean sons) {

		dataProduct.setEditorId(user.getMetaId());
		dataProduct.setFileProvenance("instance created with the backoffice");
		dataProduct.setState(newState);
		
		if(!ManagePermissions.checkPermissions(dataProduct, EntityTypeEnum.DATAPRODUCT, user)) 
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "You don't have auth on the groups of this instance");

		dbapi.setTransactionModeAuto(true);
		dbapi.startTransaction();
		LinkedEntity reference = null;
		try {
			reference = dbapi.createUpdate(dataProduct, new SaveQuery().setInstanceId(dataProduct.getInstanceId()));//dbapi.hardUpdate(dataProduct);
		} catch (Exception e) {
			//e.printStackTrace();
			dbapi.rollbackTransaction();
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "Something went wrong during the persisting of the new instance: "+e.getMessage());
		}

		dbapi.closeTransaction(true);
		dbapi.setTransactionModeAuto(true);
		
		manageNewStateRelations(dataProduct, reference, user, newState, parents, sons);

		return new ApiResponseMessage(ApiResponseMessage.OK, reference);
	}


	/**
	 * 
	 * @param dataProduct
	 * @return
	 */
	public static boolean deleteDataProduct(String instance_id, User user) {
		List<DataProduct> list = dbapi.retrieve(DataProduct.class, new DBAPIClient.GetQuery().instanceId(instance_id));

		if (list.isEmpty()) return false;
		DataProduct instance = list.get(0);
		dbapi.setTransactionModeAuto(true);
		dbapi.startTransaction();

		dbapi.delete(DataProduct.class, new DeleteQuery().instanceId(instance.getInstanceId()));

		dbapi.closeTransaction(true);
		dbapi.setTransactionModeAuto(true);

		return true;
	}


	private static void manageNewDraftRelations(DataProduct dataProduct, LinkedEntity relation, User user, boolean parents, boolean sons) {

		if(sons) {
			if(dataProduct.getDistribution()!=null)
				for(LinkedEntity le : dataProduct.getDistribution()) {
					Distribution distribution = (Distribution) DistributionManager.getDistribution(le.getMetaId(), le.getInstanceId(), user).getListOfEntities().get(0);
					System.out.println("DEST --> "+distribution);
					distribution.getDataProduct().add(relation);
					DistributionManager.updateDistribution(distribution, user, false, true);
				}
		}
	
	}
	
	private static void manageNewStateRelations(DataProduct dataProduct, LinkedEntity relation, User user, State newState, boolean parents, boolean sons) {

		if(sons) {
			if(dataProduct.getDistribution()!=null)
				for(LinkedEntity le : dataProduct.getDistribution()) {
					Distribution distribution = (Distribution) DistributionManager.getDistribution(le.getMetaId(), le.getInstanceId(), user).getListOfEntities().get(0);
					DistributionManager.updateStateDistribution(distribution, user, newState, false, true);
				}
		}
	
	}

}
