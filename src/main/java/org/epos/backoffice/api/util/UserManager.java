package org.epos.backoffice.api.util;

import static org.epos.backoffice.bean.EntityTypeEnum.USER;
import static org.epos.backoffice.bean.OperationTypeEnum.GET_ALL;
import static org.epos.backoffice.bean.OperationTypeEnum.GET_SINGLE;
import static org.epos.backoffice.bean.RoleEnum.ADMIN;
import static org.epos.backoffice.bean.RoleEnum.VIEWER;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.epos.backoffice.api.exception.ApiResponseMessage;
import org.epos.backoffice.bean.BackofficeOperationType;
import org.epos.backoffice.bean.ComputePermissionAbstract;
import org.epos.backoffice.bean.EntityTypeEnum;
import org.epos.backoffice.bean.RoleEnum;
import org.epos.backoffice.bean.User;
import org.epos.backoffice.service.ComputePermissionNoGroup;
import org.epos.eposdatamodel.DataProduct;
import org.epos.eposdatamodel.Distribution;
import org.epos.eposdatamodel.LinkedEntity;
import org.epos.eposdatamodel.Person;
import org.epos.eposdatamodel.Role;
import org.epos.eposdatamodel.State;
import org.epos.handler.dbapi.DBAPIClient;
import org.epos.handler.dbapi.DBAPIClient.DeleteQuery;
import org.epos.handler.dbapi.DBAPIClient.SaveQuery;
import org.epos.handler.dbapi.dbapiimplementation.PersonDBAPI;
import org.springframework.http.ResponseEntity;

public class UserManager {

	protected static DBAPIClient dbapi = new DBAPIClient();

	public static ApiResponseMessage getUser(String meta_id, String instance_id, User user, Boolean available_section) {
		//dbapi.setMetadataMode(false);
		dbapi.setMetadataMode(false);

		if (instance_id == null)
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "The [instance_id] field can't be left blank");

		List<Person> personList;
		if (!instance_id.equals("self")) {

			BackofficeOperationType operationType = new BackofficeOperationType()
					.operationType(instance_id.equals("all") ? GET_ALL : GET_SINGLE)
					.entityType(USER)
					.userRole(user.getRole());


			ComputePermissionAbstract computePermission = new ComputePermissionNoGroup(operationType);
			if (!computePermission.isAuthorized())
				return new ApiResponseMessage(ApiResponseMessage.UNAUTHORIZED, computePermission.generateErrorMessage());

			if (instance_id.equals("all")) {
				personList = dbapi.retrieve(Person.class, new DBAPIClient.GetQuery());
			} else {
				personList = dbapi.retrieve(Person.class, new DBAPIClient.GetQuery().instanceId(instance_id));
			}
		} else {
			PersonDBAPI personDBAPI = new PersonDBAPI();
			personDBAPI.setMetadataMode(false);
			personList = Collections.singletonList(personDBAPI.getByAuthId(user.getEduPersonUniqueId()));
		}

		List<User> userStream = personList.stream()
				.filter(x -> x.getAuthIdentifier() != null && !x.getAuthIdentifier().isEmpty())
				.map(e->mapFromPersonToUser(e)).collect(Collectors.toList());
		
		System.out.println("STREAM: "+userStream.toString());

		if (available_section) userStream.forEach(User::generateAccessibleSection);

		if (userStream.isEmpty())
			return new ApiResponseMessage(ApiResponseMessage.OK, new ArrayList<Person>());

		return new ApiResponseMessage(ApiResponseMessage.OK, true, userStream);
	}

	/**
	 * 
	 * @param dataProduct
	 * @param user
	 * @return
	 */
	public static ApiResponseMessage createUser(User inputUser, User user) {

		inputUser.setFirstName(inputUser.getFirstName() == null ? user.getFirstName() : inputUser.getFirstName());		
		inputUser.setLastName(inputUser.getLastName() == null ? user.getLastName() : inputUser.getLastName());
		inputUser.setEmail(inputUser.getEmail() == null ? user.getEmail() : inputUser.getEmail());
		inputUser.setEduPersonUniqueId(inputUser.getEduPersonUniqueId() == null ? user.getEduPersonUniqueId() : inputUser.getEduPersonUniqueId());

		System.out.println(inputUser);


		if (inputUser.isRegistered()) {
			return new ApiResponseMessage(2, "User already registered");
		}

		if (user.isRegistered()) {
			user.signIn();
			if (user.getRole().equals(ADMIN)) {
				inputUser.signUp();
				return new ApiResponseMessage(ApiResponseMessage.OK, "User successfully registered");
			}
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "You can't register other user");
		}


		if (user.getEduPersonUniqueId().equals(inputUser.getEduPersonUniqueId())) {
			try {
				Objects.requireNonNull(inputUser.getEmail(), "missing email");
				Objects.requireNonNull(inputUser.getFirstName(), "missing first name");
				Objects.requireNonNull(inputUser.getLastName(), "missing last name");
			} catch (NullPointerException e) {
				return new ApiResponseMessage(ApiResponseMessage.ERROR, "Error during the user registration: " + e.getMessage());
			}
			inputUser.setRole(VIEWER);
			inputUser.signUp();
			return new ApiResponseMessage(ApiResponseMessage.OK, "User successfully registered");
		}

		return new ApiResponseMessage(ApiResponseMessage.ERROR, "You can't register other user");
	}

	/**
	 * 
	 * @param dataProduct
	 * @param user
	 * @return
	 */
	public static ApiResponseMessage updateUser(User newUser, User user) {

		//ONLY CHANGE ROLE WORKING ATM

		if (newUser.getInstanceId() == null)
			return new ApiResponseMessage(1, "missing instanceId in the body");
		
		Person person = null;
		List<Person> userPeople = (List<Person>) PersonManager.getPerson("all", null, user).getListOfEntities();
		for(Person p : userPeople) {
			if(p.getAuthIdentifier()!=null && p.getAuthIdentifier().equals(user.getEduPersonUniqueId())) person = p;
		}
		System.out.println("HOLY: "+person);
		if(person!=null) {
			user.setMetaId(person.getMetaId());
			user.setEmail(person.getEmail().get(0));
			user.setFirstName(person.getGivenName());
			user.setLastName(person.getFamilyName());
			user.setRole(RoleEnum.valueOf(person.getRole().toString()));
			user.setInstanceId(person.getInstanceId());
		}
		System.out.println("HOLY: "+user);

		//check admissibility of the operation
		//DBAPIClient.GetQuery query = new DBAPIClient.GetQuery().instanceId(newUser.getInstanceId());
		//List<Person> people = dbapi.retrieve(Person.class, query);
		List<Person> people = (List<Person>) PersonManager.getPerson("single", newUser.getInstanceId(), user).getListOfEntities();
		

		if (people.isEmpty()) {
			return new ApiResponseMessage(1, "user not found");
		}
		if (newUser.getEduPersonUniqueId() != null && !people.get(0).getAuthIdentifier().equals(newUser.getEduPersonUniqueId()))
			return new ApiResponseMessage(1, "The user instanceId and authIdentifier doesn't correspond");

		if (!user.getRole().equals(ADMIN)) return new ApiResponseMessage(6, "You can't update your role");

		System.out.println(people.size());
		Person toBeUpdated = people.get(0);

		//User toBeUpdated = mapFromPersonToUser(people.get(0));

		toBeUpdated.setRole(Role.valueOf(newUser.getRole().toString()));
		System.out.println(toBeUpdated);

		//toBeUpdated.update();
		PersonManager.updatePerson(toBeUpdated, user, false, false);


		return new ApiResponseMessage(4, "User successfully modified");
		/*if (!user.getRole().equals(ADMIN)) {
			if (user.getEduPersonUniqueId().equals(newUser.getEduPersonUniqueId())) {
				if (newUser.getRole() == null || user.getRole().equals(newUser.getRole())) {
					newUser.setEduPersonUniqueId(people.get(0).getAuthIdentifier());
					newUser.update();
					return new ApiResponseMessage(4, "User successfully modified");
				} else {
					return new ApiResponseMessage(1, "You can't update your role");
				}
			} else {
				return new ApiResponseMessage(1, "You can't update other user");
			}
		} else {
			System.out.println(newUser);
			newUser.update();
			return new ApiResponseMessage(4, "User successfully modified");
		}*/

	}

	public static ApiResponseMessage deleteUser(String instance_id, User user) {
		if (!user.getRole().equals(ADMIN)) {
			return new ApiResponseMessage(1, "You can't delete a user");
		}

		dbapi.delete(Person.class, new DBAPIClient.DeleteQuery().instanceId(instance_id));

		return new ApiResponseMessage(4, "User successfully deleted");
	}


	private static User mapFromPersonToUser(Person person) {
		User u = new User();
		u.setEduPersonUniqueId(person.getAuthIdentifier());
		u.setLastName(person.getFamilyName());
		u.setFirstName(person.getGivenName());
		u.setEmail(person.getEmail().get(0));
		u.setMetaId(person.getMetaId());
		u.setInstanceId(person.getInstanceId());
		u.setRole(RoleEnum.valueOf(person.getRole().toString()));

		return u;
	}
}
