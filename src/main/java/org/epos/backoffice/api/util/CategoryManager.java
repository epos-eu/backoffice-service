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
import org.epos.eposdatamodel.Category;
import org.epos.eposdatamodel.LinkedEntity;
import org.epos.eposdatamodel.State;
import org.epos.eposdatamodel.WebService;
import org.epos.handler.dbapi.DBAPIClient;
import org.epos.handler.dbapi.DBAPIClient.DeleteQuery;
import org.epos.handler.dbapi.DBAPIClient.SaveQuery;
import org.epos.handler.dbapi.DBAPIClient.UpdateQuery;
import org.epos.handler.dbapi.dbapiimplementation.DistributionDBAPI;

public class CategoryManager {

	protected static DBAPIClient dbapi = new DBAPIClient();

	public static ApiResponseMessage getCategories(String meta_id, String instance_id, User user) {
		//dbapi.setMetadataMode(false);
		dbapi.setMetadataMode(false);
		if (meta_id == null)
			return new ApiResponseMessage(1, "The [meta_id] field can't be left blank");
		if(instance_id == null) {
			instance_id = "all";
		}

		BackofficeOperationType operationType = new BackofficeOperationType()
				.operationType(meta_id.equals("all") ? GET_ALL : GET_SINGLE)
				.entityType(Category.class)
				.userRole(user.getRole());


		ComputePermissionAbstract computePermission = new ComputePermissionNoGroup(operationType);
		if (!computePermission.isAuthorized())
			return new ApiResponseMessage(1, computePermission.generateErrorMessage());

		System.out.println(meta_id+" "+instance_id);

		List<Category> list;
		if (meta_id.equals("all")) {
			list = dbapi.retrieve(Category.class, new DBAPIClient.GetQuery());	
		} else {
			if(instance_id.equals("all")) {
				list = dbapi.retrieve(Category.class, new DBAPIClient.GetQuery());	
				list = list.stream()
						.filter(
								elem -> elem.getMetaId().equals(meta_id)
								)
						.collect(Collectors.toList());

			}else {
				list = dbapi.retrieve(Category.class, new DBAPIClient.GetQuery().instanceId(instance_id));
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

		List<Category> revertedList = new ArrayList<>();
		list.forEach(e -> revertedList.add(0, e));

		if (list.isEmpty())
			return new ApiResponseMessage(ApiResponseMessage.OK, new ArrayList<Category>());

		return new ApiResponseMessage(ApiResponseMessage.OK, list);
	}

	/**
	 * 
	 * @param Category
	 * @param user
	 * @return
	 */
	public static ApiResponseMessage createCategory(Category category, User user, boolean parents, boolean sons) {
		/** ID MANAGEMENT 
		 * if UID == NULL --> Generate a new UID
		 * Brand new Category? --> InstanceId = null && InstanceChangeId == null
		 * New Category from existing one? --> InstanceChangeId == OLD InstanceId
		 * 
		 **/
		if(category.getUid()==null) {
			System.err.println("UID undefined, generating a new one");
			category.setUid(category.getClass().getSimpleName().toLowerCase()+"/"+UUID.randomUUID());
		}
		category.setInstanceId(null);
		category.setInstanceChangedId(null);

		// Check if exists a version PUBLISHED or ARCHIVED if MetaId!=null
		if (category.getMetaId() != null) {
			List<Category> retrieved = dbapi.retrieve(Category.class, new DBAPIClient.GetQuery().state(State.PUBLISHED).metaId(category.getMetaId()));
			if(retrieved.isEmpty())
				retrieved = dbapi.retrieve(Category.class, new DBAPIClient.GetQuery().state(State.ARCHIVED).metaId(category.getMetaId()));
			if(retrieved.isEmpty())
				retrieved = dbapi.retrieve(Category.class, new DBAPIClient.GetQuery().state(State.SUBMITTED).metaId(category.getMetaId()));
			if(!retrieved.isEmpty()) {
				category.setInstanceChangedId(retrieved.get(0).getInstanceId());
			}	
		}

		category.setState(State.DRAFT);
		category.setEditorId(user.getMetaId());
		category.setFileProvenance("instance created with the backoffice");

		if(!ManagePermissions.checkPermissions(category, EntityTypeEnum.CATEGORY, user)) 
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "You don't have auth on the groups of this instance");

		dbapi.setTransactionModeAuto(true);
		dbapi.startTransaction();

		LinkedEntity reference;
		try {

			// save the entity and get the reference to it
			reference = dbapi.create(category);
			category.setInstanceId(reference.getInstanceId());
			category.setMetaId(reference.getMetaId());
			//TODO: Parents?
		} catch (Exception e) {
			e.printStackTrace();
			dbapi.rollbackTransaction();
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "Something went wrong during the persisting of the new instance: "+e.getMessage());
		}

		dbapi.closeTransaction(true);
		dbapi.setTransactionModeAuto(true);


		return new ApiResponseMessage(ApiResponseMessage.OK, reference);
	}

	/**
	 * 
	 * @param Category
	 * @param user
	 * @return
	 */
	public static ApiResponseMessage updateCategory(Category category, User user, boolean parents, boolean sons) {

		if(category.getState()!=null && (category.getState().equals(State.ARCHIVED) || category.getState().equals(State.PUBLISHED))) {
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "Unable to update a ARCHIVED or PUBLISHED instance");
		}
		if (category.getInstanceId() == null) {
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "InstanceId required");
		}
		if(category.getInstanceChangedId() == null || category.getInstanceChangedId().isEmpty())
			category.setInstanceChangedId(null);

		category.setEditorId(user.getMetaId());
		category.setFileProvenance("instance created with the backoffice");

		if(!ManagePermissions.checkPermissions(category, EntityTypeEnum.CATEGORY, user)) 
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "You don't have auth on the groups of this instance");

		dbapi.setTransactionModeAuto(true);
		dbapi.startTransaction();
		LinkedEntity reference = null;
		try {
			reference = dbapi.createUpdate(category, new SaveQuery().setInstanceId(category.getInstanceId()));//dbapi.hardUpdate(Category);
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
	 * @param Category
	 * @param user
	 * @return
	 */
	public static ApiResponseMessage updateStateCategory(Category category, User user, State newState, boolean parents, boolean sons) {

		category.setEditorId(user.getMetaId());
		category.setFileProvenance("instance created with the backoffice");
		category.setState(newState);

		if(!ManagePermissions.checkPermissions(category, EntityTypeEnum.CATEGORY, user)) 
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "You don't have auth on the groups of this instance");

		dbapi.setTransactionModeAuto(true);
		dbapi.startTransaction();
		LinkedEntity reference = null;
		try {
			reference = dbapi.createUpdate(category, new SaveQuery().setInstanceId(category.getInstanceId()));//dbapi.hardUpdate(Category);
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
	 * @param Category
	 * @return
	 */
	public static boolean deleteCategory(String instance_id, User user) {
		List<Category> list = dbapi.retrieve(Category.class, new DBAPIClient.GetQuery().instanceId(instance_id));

		if (list.isEmpty()) return false;
		Category instance = list.get(0);
		dbapi.setTransactionModeAuto(true);
		dbapi.startTransaction();

		//TODO Manage relations

		dbapi.delete(Category.class, new DeleteQuery().instanceId(instance.getInstanceId()));

		dbapi.closeTransaction(true);
		dbapi.setTransactionModeAuto(true);

		return true;
	}
}
