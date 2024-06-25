package org.epos.backoffice.api.util;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import model.RequestStatusType;
import model.RoleType;
import org.epos.eposdatamodel.Person;
import org.epos.eposdatamodel.User;
import org.epos.eposdatamodel.UserGroup;
import usermanagementapis.UserGroupManagementAPI;

public class UserManager {


	public static ApiResponseMessage getUser(String instance_id, User user, Boolean available_section) {

		if (instance_id == null)
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "The [instance_id] field can't be left blank");

		List<User> personList;
		if (!instance_id.equals("self")) {

			if (instance_id.equals("all")) {
				personList = UserGroupManagementAPI.retrieveAllUsers();
			} else {
				personList = (List<User>) UserGroupManagementAPI.retrieveUser(user);
			}
		} else {
			personList = Collections.singletonList(UserGroupManagementAPI.retrieveUser(user));
		}

		List<User> userStream = personList.stream()
				.filter(x -> x.getAuthIdentifier() != null && !x.getAuthIdentifier().isEmpty()).collect(Collectors.toList());

		if (userStream.isEmpty())
			return new ApiResponseMessage(ApiResponseMessage.OK, new ArrayList<Person>());

		return new ApiResponseMessage(ApiResponseMessage.OK, true, userStream);
	}

	/**
	 *
	 * @param user
	 * @return
	 */
	public static ApiResponseMessage createUser(User inputUser, User user) {

		inputUser.setFirstName(inputUser.getFirstName() == null ? user.getFirstName() : inputUser.getFirstName());
		inputUser.setLastName(inputUser.getLastName() == null ? user.getLastName() : inputUser.getLastName());
		inputUser.setEmail(inputUser.getEmail() == null ? user.getEmail() : inputUser.getEmail());
		inputUser.setAuthIdentifier(inputUser.getAuthIdentifier() == null ? user.getAuthIdentifier() : inputUser.getAuthIdentifier());
		inputUser.setIsAdmin(inputUser.getIsAdmin() == null ? user.getIsAdmin() : inputUser.getIsAdmin());

		if(UserGroupManagementAPI.createUser(inputUser)) return new ApiResponseMessage(ApiResponseMessage.OK, "User created successfully");

		return new ApiResponseMessage(ApiResponseMessage.ERROR, "You can't register other user");
	}

	public static ApiResponseMessage addUserToGroup(String userId, String groupId, RoleType role, RequestStatusType statusType, User user) {

		if(UserGroupManagementAPI.addUserToGroup(groupId,userId,role, statusType))
			return new ApiResponseMessage(ApiResponseMessage.OK, "User added successfully");

		return new ApiResponseMessage(ApiResponseMessage.ERROR, "You can't add the user to group");
	}

	/**
	 *
	 * @param user
	 * @return
	 */
	public static ApiResponseMessage updateUser(User inputUser, User user) {

		inputUser.setFirstName(inputUser.getFirstName() == null ? user.getFirstName() : inputUser.getFirstName());
		inputUser.setLastName(inputUser.getLastName() == null ? user.getLastName() : inputUser.getLastName());
		inputUser.setEmail(inputUser.getEmail() == null ? user.getEmail() : inputUser.getEmail());
		inputUser.setAuthIdentifier(inputUser.getAuthIdentifier() == null ? user.getAuthIdentifier() : inputUser.getAuthIdentifier());
		inputUser.setIsAdmin(inputUser.getIsAdmin() == null ? user.getIsAdmin() : inputUser.getIsAdmin());

		if(UserGroupManagementAPI.createUser(inputUser)) return new ApiResponseMessage(ApiResponseMessage.OK, "User updated successfully");

		return new ApiResponseMessage(ApiResponseMessage.ERROR, "You can't update other user");
	}

	public static ApiResponseMessage deleteUser(String instance_id, User user) {

		if(UserGroupManagementAPI.deleteUser(instance_id)) return new ApiResponseMessage(ApiResponseMessage.OK, "User deleted successfully");

		return new ApiResponseMessage(ApiResponseMessage.ERROR, "You can't delete other user");
	}

}
