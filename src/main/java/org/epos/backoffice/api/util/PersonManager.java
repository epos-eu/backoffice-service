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
import org.epos.eposdatamodel.Person;
import org.epos.eposdatamodel.LinkedEntity;
import org.epos.eposdatamodel.State;
import org.epos.handler.dbapi.DBAPIClient;
import org.epos.handler.dbapi.DBAPIClient.DeleteQuery;
import org.epos.handler.dbapi.DBAPIClient.SaveQuery;

public class PersonManager {

	protected static DBAPIClient dbapi = new DBAPIClient();

	public static ApiResponseMessage getPerson(String meta_id, String instance_id, User user) {
		//dbapi.setMetadataMode(false);
		dbapi.setMetadataMode(false);
		
		if (meta_id == null)
			return new ApiResponseMessage(1, "The [meta_id] field can't be left blank");
		if(instance_id == null) {
			instance_id = "all";
		}

		BackofficeOperationType operationType = new BackofficeOperationType()
				.operationType(meta_id.equals("all") ? GET_ALL : GET_SINGLE)
				.entityType(Person.class)
				.userRole(user.getRole());


		ComputePermissionAbstract computePermission = new ComputePermissionNoGroup(operationType);
		if (!computePermission.isAuthorized())
			return new ApiResponseMessage(1, computePermission.generateErrorMessage());
		
		System.out.println(meta_id+" "+instance_id);

		List<Person> list;
		if (meta_id.equals("all")) {
			list = dbapi.retrieve(Person.class, new DBAPIClient.GetQuery());	
		} else {
			if(instance_id.equals("all")) {
				list = dbapi.retrieve(Person.class, new DBAPIClient.GetQuery());	
				list = list.stream()
						.filter(
								elem -> elem.getMetaId().equals(meta_id)
								)
						.collect(Collectors.toList());
				
			}else {
				list = dbapi.retrieve(Person.class, new DBAPIClient.GetQuery().instanceId(instance_id));
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

		List<Person> revertedList = new ArrayList<>();
		list.forEach(e -> revertedList.add(0, e));
		
		if (list.isEmpty())
			return new ApiResponseMessage(ApiResponseMessage.OK, new ArrayList<Person>());
		
		return new ApiResponseMessage(ApiResponseMessage.OK, list);
	}
	
	public static ApiResponseMessage getPersonInternal(String meta_id, String instance_id) {
		//dbapi.setMetadataMode(false);
		dbapi.setMetadataMode(false);
		
		if (meta_id == null)
			return new ApiResponseMessage(1, "The [meta_id] field can't be left blank");
		if(instance_id == null) {
			instance_id = "all";
		}

		List<Person> list;
		if (meta_id.equals("all")) {
			list = dbapi.retrieve(Person.class, new DBAPIClient.GetQuery());	
		} else {
			if(instance_id.equals("all")) {
				list = dbapi.retrieve(Person.class, new DBAPIClient.GetQuery());	
				list = list.stream()
						.filter(
								elem -> elem.getMetaId().equals(meta_id)
								)
						.collect(Collectors.toList());
				
			}else {
				list = dbapi.retrieve(Person.class, new DBAPIClient.GetQuery().instanceId(instance_id));
			}
		}

		List<Person> revertedList = new ArrayList<>();
		list.forEach(e -> revertedList.add(0, e));
		
		if (list.isEmpty())
			return new ApiResponseMessage(ApiResponseMessage.OK, new ArrayList<Person>());
		
		return new ApiResponseMessage(ApiResponseMessage.OK, list);
	}

	/**
	 * 
	 * @param Person
	 * @param user
	 * @return
	 */
	public static ApiResponseMessage createPerson(Person person, User user, boolean parents, boolean sons) {
		/** ID MANAGEMENT 
		 * if UID == NULL --> Generate a new UID
		 * Brand new Person? --> InstanceId = null && InstanceChangeId == null
		 * New Person from existing one? --> InstanceChangeId == OLD InstanceId
		 * 
		 **/
		if(person.getUid()==null) {
			System.err.println("UID undefined, generating a new one");
			person.setUid(person.getClass().getSimpleName().toLowerCase()+"/"+UUID.randomUUID());
		}
		person.setInstanceId(null);
		person.setInstanceChangedId(null);

		// Check if exists a version PUBLISHED or ARCHIVED if MetaId!=null
		if (person.getMetaId() != null) {
			List<Person> retrieved = dbapi.retrieve(Person.class, new DBAPIClient.GetQuery().state(State.PUBLISHED).metaId(person.getMetaId()));
			if(retrieved.isEmpty())
				retrieved = dbapi.retrieve(Person.class, new DBAPIClient.GetQuery().state(State.ARCHIVED).metaId(person.getMetaId()));
			if(retrieved.isEmpty())
				retrieved = dbapi.retrieve(Person.class, new DBAPIClient.GetQuery().state(State.SUBMITTED).metaId(person.getMetaId()));
			if(!retrieved.isEmpty()) {
				person.setInstanceChangedId(retrieved.get(0).getInstanceId());
			}	
		}

		person.setState(State.DRAFT);
		person.setEditorId(user.getMetaId());
		person.setFileProvenance("instance created with the backoffice");
		
		if(!ManagePermissions.checkPermissions(person, EntityTypeEnum.PERSON, user)) 
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "You don't have auth on the groups of this instance");

		dbapi.setTransactionModeAuto(true);
		dbapi.startTransaction();

		LinkedEntity reference;
		try {

			// save the entity and get the reference to it
			reference = dbapi.create(person);
			person.setInstanceId(reference.getInstanceId());
			person.setMetaId(reference.getMetaId());
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
	 * @param Person
	 * @param user
	 * @return
	 */
	public static ApiResponseMessage updatePerson(Person person, User user, boolean parents, boolean sons) {
		if (person.getInstanceId() == null) {
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "InstanceId required");
		}
		if(person.getInstanceChangedId() == null || person.getInstanceChangedId().isEmpty())
			person.setInstanceChangedId(null);

		person.setEditorId(user.getMetaId());
		person.setFileProvenance("instance created with the backoffice");
		
		if(!ManagePermissions.checkPermissions(person, EntityTypeEnum.PERSON, user)) 
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "You don't have auth on the groups of this instance");

		dbapi.setTransactionModeAuto(true);
		dbapi.startTransaction();

		System.out.println("PERSON: "+person);
		
		LinkedEntity reference = null;
		try {
			reference = dbapi.createUpdate(person, new SaveQuery().setInstanceId(person.getInstanceId()));//dbapi.hardUpdate(Person);
		} catch (Exception e) {
			//e.printStackTrace();
			dbapi.rollbackTransaction();
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "Something went wrong during the persisting of the new instance: "+e.getMessage());
		}

		dbapi.closeTransaction(true);
		dbapi.setTransactionModeAuto(true);
		
		return new ApiResponseMessage(ApiResponseMessage.OK, reference);
	}
	
	/**
	 * 
	 * @param Person
	 * @param user
	 * @return
	 */
	public static ApiResponseMessage updateStatePerson(Person person, User user, State newState, boolean parents, boolean sons) {

		person.setEditorId(user.getMetaId());
		person.setFileProvenance("instance created with the backoffice");
		person.setState(newState);
		
		if(!ManagePermissions.checkPermissions(person, EntityTypeEnum.PERSON, user)) 
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "You don't have auth on the groups of this instance");

		dbapi.setTransactionModeAuto(true);
		dbapi.startTransaction();
		LinkedEntity reference = null;
		try {
			reference = dbapi.createUpdate(person, new SaveQuery().setInstanceId(person.getInstanceId()));//dbapi.hardUpdate(Person);
		} catch (Exception e) {
			//e.printStackTrace();
			dbapi.rollbackTransaction();
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "Something went wrong during the persisting of the new instance: "+e.getMessage());
		}

		dbapi.closeTransaction(true);
		dbapi.setTransactionModeAuto(true);
		return new ApiResponseMessage(ApiResponseMessage.OK, reference);
	}


	/**
	 * 
	 * @param Person
	 * @return
	 */
	public static boolean deletePerson(String instance_id, User user) {
		List<Person> list = dbapi.retrieve(Person.class, new DBAPIClient.GetQuery().instanceId(instance_id));

		if (list.isEmpty()) return false;
		Person instance = list.get(0);
		dbapi.setTransactionModeAuto(true);
		dbapi.startTransaction();

		dbapi.delete(Person.class, new DeleteQuery().instanceId(instance.getInstanceId()));

		dbapi.closeTransaction(true);
		dbapi.setTransactionModeAuto(true);

		return true;
	}

}
