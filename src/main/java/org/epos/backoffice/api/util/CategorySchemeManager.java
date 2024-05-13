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
import org.epos.eposdatamodel.CategoryScheme;
import org.epos.eposdatamodel.LinkedEntity;
import org.epos.eposdatamodel.State;
import org.epos.eposdatamodel.WebService;
import org.epos.handler.dbapi.DBAPIClient;
import org.epos.handler.dbapi.DBAPIClient.DeleteQuery;
import org.epos.handler.dbapi.DBAPIClient.SaveQuery;
import org.epos.handler.dbapi.DBAPIClient.UpdateQuery;
import org.epos.handler.dbapi.dbapiimplementation.DistributionDBAPI;

public class CategorySchemeManager {

	protected static DBAPIClient dbapi = new DBAPIClient();

	public static ApiResponseMessage getCategorySchemes(String meta_id, String instance_id, User user) {
		//dbapi.setMetadataMode(false);
		dbapi.setMetadataMode(false);
		if (meta_id == null)
			return new ApiResponseMessage(1, "The [meta_id] field can't be left blank");
		if(instance_id == null) {
			instance_id = "all";
		}

		BackofficeOperationType operationType = new BackofficeOperationType()
				.operationType(meta_id.equals("all") ? GET_ALL : GET_SINGLE)
				.entityType(CategoryScheme.class)
				.userRole(user.getRole());


		ComputePermissionAbstract computePermission = new ComputePermissionNoGroup(operationType);
		if (!computePermission.isAuthorized())
			return new ApiResponseMessage(1, computePermission.generateErrorMessage());

		System.out.println(meta_id+" "+instance_id);

		List<CategoryScheme> list;
		if (meta_id.equals("all")) {
			list = dbapi.retrieve(CategoryScheme.class, new DBAPIClient.GetQuery());	
		} else {
			if(instance_id.equals("all")) {
				list = dbapi.retrieve(CategoryScheme.class, new DBAPIClient.GetQuery());	
				list = list.stream()
						.filter(
								elem -> elem.getMetaId().equals(meta_id)
								)
						.collect(Collectors.toList());

			}else {
				list = dbapi.retrieve(CategoryScheme.class, new DBAPIClient.GetQuery().instanceId(instance_id));
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

		List<CategoryScheme> revertedList = new ArrayList<>();
		list.forEach(e -> revertedList.add(0, e));

		if (list.isEmpty())
			return new ApiResponseMessage(ApiResponseMessage.OK, new ArrayList<CategoryScheme>());

		return new ApiResponseMessage(ApiResponseMessage.OK, list);
	}

	/**
	 * 
	 * @param Category
	 * @param user
	 * @return
	 */
	public static ApiResponseMessage createCategoryScheme(CategoryScheme categoryscheme, User user, boolean parents, boolean sons) {
		/** ID MANAGEMENT 
		 * if UID == NULL --> Generate a new UID
		 * Brand new Category? --> InstanceId = null && InstanceChangeId == null
		 * New Category from existing one? --> InstanceChangeId == OLD InstanceId
		 * 
		 **/
		if(categoryscheme.getUid()==null) {
			System.err.println("UID undefined, generating a new one");
			categoryscheme.setUid(categoryscheme.getClass().getSimpleName().toLowerCase()+"/"+UUID.randomUUID());
		}
		categoryscheme.setInstanceId(null);
		categoryscheme.setInstanceChangedId(null);

		// Check if exists a version PUBLISHED or ARCHIVED if MetaId!=null
		if (categoryscheme.getMetaId() != null) {
			List<CategoryScheme> retrieved = dbapi.retrieve(CategoryScheme.class, new DBAPIClient.GetQuery().state(State.PUBLISHED).metaId(categoryscheme.getMetaId()));
			if(retrieved.isEmpty())
				retrieved = dbapi.retrieve(CategoryScheme.class, new DBAPIClient.GetQuery().state(State.ARCHIVED).metaId(categoryscheme.getMetaId()));
			if(retrieved.isEmpty())
				retrieved = dbapi.retrieve(CategoryScheme.class, new DBAPIClient.GetQuery().state(State.SUBMITTED).metaId(categoryscheme.getMetaId()));
			if(!retrieved.isEmpty()) {
				categoryscheme.setInstanceChangedId(retrieved.get(0).getInstanceId());
			}	
		}

		categoryscheme.setState(State.DRAFT);
		categoryscheme.setEditorId(user.getMetaId());
		categoryscheme.setFileProvenance("instance created with the backoffice");

		if(!ManagePermissions.checkPermissions(categoryscheme, EntityTypeEnum.CATEGORYSCHEME, user)) 
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "You don't have auth on the groups of this instance");

		dbapi.setTransactionModeAuto(true);
		dbapi.startTransaction();

		LinkedEntity reference;
		try {

			// save the entity and get the reference to it
			reference = dbapi.create(categoryscheme);
			categoryscheme.setInstanceId(reference.getInstanceId());
			categoryscheme.setMetaId(reference.getMetaId());
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
	public static ApiResponseMessage updateCategoryScheme(CategoryScheme categoryscheme, User user, boolean parents, boolean sons) {

		if(categoryscheme.getState()!=null && (categoryscheme.getState().equals(State.ARCHIVED) || categoryscheme.getState().equals(State.PUBLISHED))) {
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "Unable to update a ARCHIVED or PUBLISHED instance");
		}
		if (categoryscheme.getInstanceId() == null) {
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "InstanceId required");
		}
		if(categoryscheme.getInstanceChangedId() == null || categoryscheme.getInstanceChangedId().isEmpty())
			categoryscheme.setInstanceChangedId(null);

		categoryscheme.setEditorId(user.getMetaId());
		categoryscheme.setFileProvenance("instance created with the backoffice");

		if(!ManagePermissions.checkPermissions(categoryscheme, EntityTypeEnum.CATEGORYSCHEME, user)) 
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "You don't have auth on the groups of this instance");

		dbapi.setTransactionModeAuto(true);
		dbapi.startTransaction();
		LinkedEntity reference = null;
		try {
			reference = dbapi.createUpdate(categoryscheme, new SaveQuery().setInstanceId(categoryscheme.getInstanceId()));//dbapi.hardUpdate(Category);
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
	public static boolean deleteCategoryScheme(String instance_id, User user) {
		List<CategoryScheme> list = dbapi.retrieve(CategoryScheme.class, new DBAPIClient.GetQuery().instanceId(instance_id));

		if (list.isEmpty()) return false;
		CategoryScheme instance = list.get(0);
		dbapi.setTransactionModeAuto(true);
		dbapi.startTransaction();
		
	//TODO MANAGE RELATIONS

		dbapi.delete(Category.class, new DeleteQuery().instanceId(instance.getInstanceId()));

		dbapi.closeTransaction(true);
		dbapi.setTransactionModeAuto(true);

		return true;
	}
}
