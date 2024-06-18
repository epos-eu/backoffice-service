package org.epos.backoffice.api.util;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.epos.backoffice.api.exception.ApiResponseMessage;
import org.epos.eposdatamodel.Person;
import org.epos.eposdatamodel.User;
import usermanagementapis.UserGroupManagementAPI;

public class UserManager {



	public static ApiResponseMessage getUser(String instance_id, User user, Boolean available_section) {

		if (instance_id == null)
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "The [instance_id] field can't be left blank");

		List<model.User> personList;
		if (!instance_id.equals("self")) {

			if (instance_id.equals("all")) {
				personList = (List<model.User>) UserGroupManagementAPI.retrieveUser(null);
			} else {
				personList = (List<model.User>) UserGroupManagementAPI.retrieveUser(user);
			}
		} else {
			personList = Collections.singletonList(UserGroupManagementAPI.retrieveUser(null));
		}

		List<model.User> userStream = personList.stream()
				.filter(x -> x.getAuthIdentifier() != null && !x.getAuthIdentifier().isEmpty()).collect(Collectors.toList());
		
		System.out.println("STREAM: "+userStream.toString());


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



		inputUser.setGivenname(inputUser.getGivenname() == null ? user.getGivenname() : inputUser.getGivenname());
		inputUser.setFamilyname(inputUser.getFamilyname() == null ? user.getFamilyname() : inputUser.getFamilyname());
		inputUser.setEmail(inputUser.getEmail() == null ? user.getEmail() : inputUser.getEmail());
		inputUser.setAuthIdentifier(inputUser.getAuthIdentifier() == null ? user.getAuthIdentifier() : inputUser.getAuthIdentifier());

		System.out.println(inputUser);

		UserGroupManagementAPI.createUser(inputUser);

		return new ApiResponseMessage(ApiResponseMessage.ERROR, "You can't register other user");
	}

	/**
	 *
	 * @param user
	 * @return
	 */
	public static ApiResponseMessage updateUser(User inputUser, User user) {

		UserGroupManagementAPI.createUser(inputUser);

		return new ApiResponseMessage(4, "User successfully modified");
	}

	public static ApiResponseMessage deleteUser(String instance_id, User user) {

		UserGroupManagementAPI.deleteUser(instance_id);

		return new ApiResponseMessage(4, "User successfully deleted");
	}

}
