package org.epos.backoffice.api.util;

import static org.epos.backoffice.bean.OperationTypeEnum.GET_ALL;
import static org.epos.backoffice.bean.OperationTypeEnum.GET_SINGLE;
import static org.epos.backoffice.bean.RoleEnum.ADMIN;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.epos.backoffice.api.exception.ApiResponseMessage;
import org.epos.backoffice.api.exception.ConceptApiResponseMessage;
import org.epos.backoffice.bean.BackofficeOperationType;
import org.epos.backoffice.bean.ComputePermissionAbstract;
import org.epos.backoffice.bean.EntityTypeEnum;
import org.epos.backoffice.bean.User;
import org.epos.eposdatamodel.Category;
import org.epos.eposdatamodel.LinkedEntity;
import org.epos.eposdatamodel.State;
import org.epos.handler.dbapi.dbapiimplementation.CategoryDBAPI;
import org.epos.handler.dbapi.service.DBService;

public class CategoryManager {

	//protected static DBAPIClient dbapi = new DBAPIClient();
	protected static CategoryDBAPI dbapi = new CategoryDBAPI();

	public static ConceptApiResponseMessage getCategories(String meta_id, String instance_id, User user) {
		//dbapi.setMetadataMode(false);
		dbapi.setMetadataMode(false);
		if (meta_id == null)
			return new ConceptApiResponseMessage(1, "The [meta_id] field can't be left blank");
		if(instance_id == null) {
			instance_id = "all";
		}

		/*BackofficeOperationType operationType = new BackofficeOperationType()
				.operationType(meta_id.equals("all") ? GET_ALL : GET_SINGLE)
				.entityType(Category.class)
				.userRole(user.getRole());


		ComputePermissionAbstract computePermission = new ComputePermissionNoGroup(operationType);
		if (!computePermission.isAuthorized())
			return new ApiResponseMessage(1, computePermission.generateErrorMessage());*/

		System.out.println(meta_id+" "+instance_id);

		List<Category> list;
		if (meta_id.equals("all")) {
			list = dbapi.getAll();	
		} else {
			if(instance_id.equals("all")) {
				list = dbapi.getAll();
				list = list.stream()
						.filter(
								elem -> elem.getMetaId().equals(meta_id)
								)
						.collect(Collectors.toList());

			}else {
				list = List.of(dbapi.getByInstanceId(instance_id));
			}
		}

		/*list = list.stream()
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
				.collect(Collectors.toList());*/

		List<Category> revertedList = new ArrayList<>();
		list.forEach(e -> revertedList.add(0, e));

		if (list.isEmpty())
			return new ConceptApiResponseMessage(ConceptApiResponseMessage.OK, new ArrayList<Category>());

		return new ConceptApiResponseMessage(ConceptApiResponseMessage.OK, list);
	}

	/**
	 * 
	 * @param Category
	 * @param user
	 * @return
	 */
	public static ConceptApiResponseMessage createCategory(Category category, User user, boolean parents, boolean sons) {
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

		category.setState(State.DRAFT);
		//category.setEditorId(user.getMetaId());
		category.setFileProvenance("instance created with the backoffice");

		/*if(!ManagePermissions.checkPermissions(category, EntityTypeEnum.CATEGORY, user)) 
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "You don't have auth on the groups of this instance");*/
		

        EntityManager em = new DBService().getEntityManager();
        em.getTransaction().begin();

        LinkedEntity reference = dbapi.save(category, em);

        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
            em.getEntityManagerFactory().getCache().evictAll();
        }
        em.close();


		return new ConceptApiResponseMessage(ConceptApiResponseMessage.OK, reference);
	}

	/**
	 * 
	 * @param Category
	 * @param user
	 * @return
	 */
	public static ConceptApiResponseMessage updateCategory(Category category, User user, boolean parents, boolean sons) {

		if(category.getState()!=null && (category.getState().equals(State.ARCHIVED) || category.getState().equals(State.PUBLISHED))) {
			return new ConceptApiResponseMessage(ConceptApiResponseMessage.ERROR, "Unable to update a ARCHIVED or PUBLISHED instance");
		}
		if (category.getInstanceId() == null) {
			return new ConceptApiResponseMessage(ConceptApiResponseMessage.ERROR, "InstanceId required");
		}
		if(category.getInstanceChangedId() == null || category.getInstanceChangedId().isEmpty())
			category.setInstanceChangedId(null);

		//category.setEditorId(user.getMetaId());
		category.setFileProvenance("instance created with the backoffice");

		/*if(!ManagePermissions.checkPermissions(category, EntityTypeEnum.CATEGORY, user)) 
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "You don't have auth on the groups of this instance");*/
		

        EntityManager em = new DBService().getEntityManager();
        em.getTransaction().begin();

        LinkedEntity reference = dbapi.hardUpdateWithLink(category.getInstanceId(), category, em);

        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
            em.getEntityManagerFactory().getCache().evictAll();
        }
        em.close();


		return new ConceptApiResponseMessage(ConceptApiResponseMessage.OK, reference);
	}

	/**
	 * 
	 * @param Category
	 * @return
	 */
	public static boolean deleteCategory(String instance_id, User user) {
		List<Category> list = List.of( dbapi.getByInstanceId(instance_id));

		if (list.isEmpty()) return false;
		Category instance = list.get(0);
		
		EntityManager em = new DBService().getEntityManager();
        em.getTransaction().begin();

        dbapi.delete(instance.getInstanceId(), em);

        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
            em.getEntityManagerFactory().getCache().evictAll();
        }
        em.close();

		return true;
	}
}
