package org.epos.backoffice.service;

import org.epos.backoffice.bean.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.epos.backoffice.bean.EntityTypeEnum.*;
import static org.epos.backoffice.bean.OperationTypeEnum.*;
import static org.epos.backoffice.bean.RoleEnum.*;

public class ComputePermissionNoGroup extends ComputePermissionAbstract {

    public static final Map<RoleEnum, Map<OperationTypeEnum, List<EntityTypeEnum>>> trueMap;
    private static final List<EntityTypeEnum> all = List.of(USER, CONTACTPOINT, DATAPRODUCT, DISTRIBUTION, WEBSERVICE, OPERATION, ORGANIZATION, PERSON);
    private static final List<EntityTypeEnum> allScientificMetadata = List.of(CONTACTPOINT, DATAPRODUCT, DISTRIBUTION, WEBSERVICE, OPERATION);
    private static final List<EntityTypeEnum> personAndOrganization = List.of(CONTACTPOINT, DATAPRODUCT, DISTRIBUTION, WEBSERVICE, OPERATION);

    static {
        Map<RoleEnum, Map<OperationTypeEnum, List<EntityTypeEnum>>> map = new HashMap<>();


        Map<OperationTypeEnum, List<EntityTypeEnum>> admin = new HashMap<>();
        admin.put(GET_SINGLE, all);
        admin.put(GET_ALL, all);
        admin.put(MANAGE_DRAFT, all);
        admin.put(MANAGE_PUBLISHED, all);
        admin.put(DATAPRODUCT__CHANGE_STATUS__DRAFT_SUBMITTED, all);
        admin.put(DATAPRODUCT__CHANGE_STATUS__SUBMITTED_DRAFT, all);
        admin.put(DATAPRODUCT__CHANGE_STATUS__SUBMITTED_PUBLISHED, all);
        admin.put(DATAPRODUCT__CHANGE_STATUS__SUBMITTED_DISCARDED, all);
        admin.put(OTHER, all);


        Map<OperationTypeEnum, List<EntityTypeEnum>> editor = new HashMap<>();
        editor.put(GET_SINGLE, all);
        editor.put(GET_ALL, List.of(DATAPRODUCT));
        editor.put(MANAGE_DRAFT, allScientificMetadata);
        editor.put(DATAPRODUCT__CHANGE_STATUS__DRAFT_SUBMITTED, allScientificMetadata);
        editor.put(DATAPRODUCT__CHANGE_STATUS__SUBMITTED_DRAFT, allScientificMetadata);


        Map<OperationTypeEnum, List<EntityTypeEnum>> reviewer = new HashMap<>();
        reviewer.put(GET_SINGLE, all);
        reviewer.put(GET_ALL, List.of(DATAPRODUCT, ORGANIZATION, PERSON));
        reviewer.put(MANAGE_DRAFT, personAndOrganization);
        reviewer.put(DATAPRODUCT__CHANGE_STATUS__DRAFT_SUBMITTED, personAndOrganization);
        reviewer.put(DATAPRODUCT__CHANGE_STATUS__SUBMITTED_DRAFT, personAndOrganization);
        reviewer.put(DATAPRODUCT__CHANGE_STATUS__SUBMITTED_PUBLISHED, all);
        reviewer.put(DATAPRODUCT__CHANGE_STATUS__SUBMITTED_DISCARDED, all);


        Map<OperationTypeEnum, List<EntityTypeEnum>> viewer = new HashMap<>();
        viewer.put(GET_SINGLE, allScientificMetadata);
        viewer.put(GET_ALL, List.of(DATAPRODUCT));


        map.put(ADMIN, admin);
        map.put(EDITOR, editor);
        map.put(REVIEWER, reviewer);
        map.put(VIEWER, viewer);

        trueMap = Collections.unmodifiableMap(map);
    }

    public ComputePermissionNoGroup(BackofficeOperationType op) {
        super(op);
    }

    @Override
    public boolean isAuthorized() {
        try {
            return trueMap.get(op.getUserRole()).get(op.getOperationType()).contains(op.getEntityType());
        } catch (NullPointerException ignored) {
            return false;
        }
    }
}
