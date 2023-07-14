package org.epos.backoffice.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.epos.backoffice.api.exception.ApiResponseMessage;
import org.epos.backoffice.api.util.GroupFilter;
import org.epos.backoffice.bean.BackofficeOperationType;
import org.epos.backoffice.bean.ComputePermissionAbstract;
import org.epos.backoffice.bean.User;
import org.epos.backoffice.service.ComputePermissionNoGroup;
import org.epos.eposdatamodel.EPOSDataModelEntity;
import org.epos.eposdatamodel.State;
import org.epos.handler.dbapi.DBAPIClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.epos.backoffice.bean.OperationTypeEnum.GET_ALL;
import static org.epos.backoffice.bean.OperationTypeEnum.GET_SINGLE;
import static org.epos.backoffice.bean.RoleEnum.ADMIN;

public abstract class BackofficeAbstractController<T extends EPOSDataModelEntity> {

    protected final ObjectMapper objectMapper;
    private final HttpServletRequest request;
    protected final Class<T> entityType;
    @Autowired
    protected DBAPIClient dbapi;


    public BackofficeAbstractController(ObjectMapper objectMapper, HttpServletRequest request, Class<T> entityType) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.entityType = entityType;
    }

    protected User getUserFromSession() {
        return (User) request.getSession().getAttribute("user");
    }


    protected ResponseEntity<?> getMethod(String instance_id) {
        if (instance_id == null)
            return ResponseEntity
                    .status(400)
                    .body(new ApiResponseMessage(1, "The [instance_id] field can't be left blank"));

        User user = getUserFromSession();

        BackofficeOperationType operationType = new BackofficeOperationType()
                .operationType(instance_id.equals("all") ? GET_ALL : GET_SINGLE)
                .entityType(entityType)
                .userRole(user.getRole());


        ComputePermissionAbstract computePermission = new ComputePermissionNoGroup(operationType);
        if (!computePermission.isAuthorized())
            return ResponseEntity
                    .status(403)
                    .body(new ApiResponseMessage(1, computePermission.generateErrorMessage()));

        List<T> list;
        if (instance_id.equals("all")) {
            list = dbapi.retrieve(entityType, new DBAPIClient.GetQuery());
        } else {
            list = dbapi.retrieve(entityType, new DBAPIClient.GetQuery().instanceId(instance_id));
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

        List<T> revertedList = new ArrayList<>();
        list.forEach(e -> revertedList.add(0, e));

        if (list.isEmpty())
            return ResponseEntity.status(404).body("[]");

        return ResponseEntity
                .status(200)
                .body(revertedList);
    }
}
