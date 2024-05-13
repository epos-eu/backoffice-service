package org.epos.backoffice.api.util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.epos.backoffice.api.exception.ApiResponseMessage;
import org.epos.backoffice.api.exception.ConceptSchemeApiResponseMessage;
import org.epos.backoffice.bean.User;
import org.epos.eposdatamodel.CategoryScheme;
import org.epos.eposdatamodel.LinkedEntity;
import org.epos.eposdatamodel.State;
import org.epos.handler.dbapi.dbapiimplementation.CategorySchemeDBAPI;
import org.epos.handler.dbapi.service.DBService;

public class CategorySchemeManager {

	protected static CategorySchemeDBAPI dbapi = new CategorySchemeDBAPI();

	public static ConceptSchemeApiResponseMessage getCategorySchemes(String meta_id, String instance_id, User user) {
		//dbapi.setMetadataMode(false);
		dbapi.setMetadataMode(false);
		if (meta_id == null)
			return new ConceptSchemeApiResponseMessage(1, "The [meta_id] field can't be left blank");
		if(instance_id == null) {
			instance_id = "all";
		}
/*
		BackofficeOperationType operationType = new BackofficeOperationType()
				.operationType(meta_id.equals("all") ? GET_ALL : GET_SINGLE)
				.entityType(CategoryScheme.class)
				.userRole(user.getRole());


		ComputePermissionAbstract computePermission = new ComputePermissionNoGroup(operationType);
		if (!computePermission.isAuthorized())
			return new ApiResponseMessage(1, computePermission.generateErrorMessage());*/

		System.out.println(meta_id+" "+instance_id);

		List<CategoryScheme> list;
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
				list = dbapi.getAll();
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

		List<CategoryScheme> revertedList = new ArrayList<>();
		list.forEach(e -> revertedList.add(0, e));

		if (list.isEmpty())
			return new ConceptSchemeApiResponseMessage(ConceptSchemeApiResponseMessage.OK, new ArrayList<CategoryScheme>());

		return new ConceptSchemeApiResponseMessage(ConceptSchemeApiResponseMessage.OK, list);
	}

	/**
	 * 
	 * @param Category
	 * @param user
	 * @return
	 */
	public static ConceptSchemeApiResponseMessage createCategoryScheme(CategoryScheme categoryscheme, User user, boolean parents, boolean sons) {
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

		categoryscheme.setState(State.DRAFT);
		//categoryscheme.setEditorId(user.getMetaId());
		categoryscheme.setFileProvenance("instance created with the backoffice");

		/*if(!ManagePermissions.checkPermissions(categoryscheme, EntityTypeEnum.CATEGORYSCHEME, user)) 
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "You don't have auth on the groups of this instance");*/


        EntityManager em = new DBService().getEntityManager();
        em.getTransaction().begin();

        LinkedEntity reference = dbapi.save(categoryscheme, em);

        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
            em.getEntityManagerFactory().getCache().evictAll();
        }
        em.close();


		return new ConceptSchemeApiResponseMessage(ConceptSchemeApiResponseMessage.OK, reference);
	}

	/**
	 * 
	 * @param Category
	 * @param user
	 * @return
	 */
	public static ConceptSchemeApiResponseMessage updateCategoryScheme(CategoryScheme categoryscheme, User user, boolean parents, boolean sons) {

		if(categoryscheme.getState()!=null && (categoryscheme.getState().equals(State.ARCHIVED) || categoryscheme.getState().equals(State.PUBLISHED))) {
			return new ConceptSchemeApiResponseMessage(ConceptSchemeApiResponseMessage.ERROR, "Unable to update a ARCHIVED or PUBLISHED instance");
		}
		if (categoryscheme.getInstanceId() == null) {
			return new ConceptSchemeApiResponseMessage(ConceptSchemeApiResponseMessage.ERROR, "InstanceId required");
		}
		if(categoryscheme.getInstanceChangedId() == null || categoryscheme.getInstanceChangedId().isEmpty())
			categoryscheme.setInstanceChangedId(null);

		//categoryscheme.setEditorId(user.getMetaId());
		categoryscheme.setFileProvenance("instance created with the backoffice");

		/*if(!ManagePermissions.checkPermissions(categoryscheme, EntityTypeEnum.CATEGORYSCHEME, user)) 
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "You don't have auth on the groups of this instance");*/
		
		

        EntityManager em = new DBService().getEntityManager();
        em.getTransaction().begin();

        LinkedEntity reference = dbapi.hardUpdateWithLink(categoryscheme.getInstanceId(), categoryscheme, em);

        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
            em.getEntityManagerFactory().getCache().evictAll();
        }
        em.close();

		return new ConceptSchemeApiResponseMessage(ConceptSchemeApiResponseMessage.OK, reference);
	}
	

	/**
	 * 
	 * @param Category
	 * @return
	 */
	public static boolean deleteCategoryScheme(String instance_id, User user) {

		List<CategoryScheme> list = List.of( dbapi.getByInstanceId(instance_id));

		if (list.isEmpty()) return false;
		CategoryScheme instance = list.get(0);
		
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
