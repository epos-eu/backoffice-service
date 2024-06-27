package org.epos.backoffice.api.util;


import model.MetadataGroup;
import model.RequestStatusType;
import model.RoleType;
import org.epos.eposdatamodel.Group;
import org.epos.eposdatamodel.User;
import usermanagementapis.UserGroupManagementAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GroupManager {


	public static ApiResponseMessage getGroup(String instance_id, User user, Boolean available_section) {

		if (instance_id == null)
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "The [instance_id] field can't be left blank");

		List<Group> groupList;
		if (instance_id.equals("all")) {
			groupList = UserGroupManagementAPI.retrieveAllGroups();
		} else {
			groupList = List.of(UserGroupManagementAPI.retrieveGroupById(instance_id));
		}


		List<Group> groupStream = groupList.stream()
				.filter(x -> x.getId() != null && !x.getId().isEmpty()).collect(Collectors.toList());

		if (groupStream.isEmpty())
			return new ApiResponseMessage(ApiResponseMessage.OK, false, true, new ArrayList<Group>());

		return new ApiResponseMessage(ApiResponseMessage.OK, false, true, groupStream);
	}

	/**
	 *
	 * @param user
	 * @return
	 */
	public static ApiResponseMessage createGroup(Group inputGroup, User user) {

		if(UserGroupManagementAPI.createGroup(inputGroup)) return new ApiResponseMessage(ApiResponseMessage.OK, "Group created successfully");

		return new ApiResponseMessage(ApiResponseMessage.ERROR, "You can't create a group");
	}

	/**
	 *
	 * @param user
	 * @return
	 */
	public static ApiResponseMessage updateGroup(Group inputGroup, User user) {

		if(UserGroupManagementAPI.createGroup(inputGroup)) return new ApiResponseMessage(ApiResponseMessage.OK, "Group updated successfully");

		return new ApiResponseMessage(ApiResponseMessage.ERROR, "You can't update other group");
	}

	public static ApiResponseMessage deleteGroup(String instance_id, User user) {

		if(UserGroupManagementAPI.deleteGroup(instance_id)) return new ApiResponseMessage(ApiResponseMessage.OK, "Group deleted successfully");

		return new ApiResponseMessage(ApiResponseMessage.ERROR, "You can't delete other group");
	}

	public static ApiResponseMessage addEntityToGroup(AddEntityToGroupBean entityGroup, User user) {
		Boolean result = UserGroupManagementAPI.addMetadataElementToGroup(
				entityGroup.getMetaid(),
				entityGroup.getGroupid());
		if(result!=null && result)
			return new ApiResponseMessage(ApiResponseMessage.OK, "User added successfully");

		return new ApiResponseMessage(ApiResponseMessage.ERROR, "You can't add the user to group");
	}

}
