package org.epos.backoffice.bean;

import org.epos.eposdatamodel.EPOSDataModelEntity;

public class BackofficeOperationType {
    private OperationTypeEnum operationType;
    private EntityTypeEnum entityType;
    private RoleEnum userRole;

    public BackofficeOperationType operationType(OperationTypeEnum operationType) {
        this.operationType = operationType;
        return this;
    }

    public BackofficeOperationType entityType(EntityTypeEnum entityType) {
        this.entityType = entityType;
        return this;
    }

    public BackofficeOperationType entityType(Class<? extends EPOSDataModelEntity> entityTypeClass) {
        this.entityType = EntityTypeEnum.fromString(entityTypeClass.getSimpleName());
        return this;
    }

    public BackofficeOperationType userRole(RoleEnum userRole) {
        this.userRole = userRole;
        return this;
    }


    public OperationTypeEnum getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationTypeEnum operationType) {
        this.operationType = operationType;
    }

    public EntityTypeEnum getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityTypeEnum entityType) {
        this.entityType = entityType;
    }

    public RoleEnum getUserRole() {
        return userRole;
    }

    public void setUserRole(RoleEnum userRole) {
        this.userRole = userRole;
    }
}
