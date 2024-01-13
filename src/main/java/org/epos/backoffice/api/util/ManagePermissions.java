package org.epos.backoffice.api.util;

import static org.epos.backoffice.bean.OperationTypeEnum.MANAGE_DRAFT;
import static org.epos.backoffice.bean.OperationTypeEnum.MANAGE_PUBLISHED;
import static org.epos.backoffice.bean.OperationTypeEnum.OTHER;

import org.epos.backoffice.bean.BackofficeOperationType;
import org.epos.backoffice.bean.ComputePermissionAbstract;
import org.epos.backoffice.bean.EntityTypeEnum;
import org.epos.backoffice.bean.OperationTypeEnum;
import org.epos.backoffice.bean.User;
import org.epos.backoffice.service.ComputePermissionNoGroup;
import org.epos.eposdatamodel.EPOSDataModelEntity;
import org.epos.eposdatamodel.State;

public class ManagePermissions {

	public static Boolean checkPermissions(EPOSDataModelEntity body, EntityTypeEnum entityType, User user) {
		OperationTypeEnum operationTypeEnum;
		if (body.getState().equals(State.DRAFT)) operationTypeEnum = MANAGE_DRAFT;
		else if (body.getState().equals(State.PUBLISHED)) operationTypeEnum = MANAGE_PUBLISHED;
		else operationTypeEnum = OTHER;

		BackofficeOperationType operationType = new BackofficeOperationType()
				.operationType(operationTypeEnum)
				.entityType(entityType)
				.userRole(user.getRole());

		ComputePermissionAbstract computePermission = new ComputePermissionNoGroup(operationType);
		if (!computePermission.isAuthorized())
			return false;
		return true;
	}
}
