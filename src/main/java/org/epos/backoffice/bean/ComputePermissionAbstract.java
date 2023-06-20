package org.epos.backoffice.bean;

public abstract class ComputePermissionAbstract {
    protected BackofficeOperationType op;

    protected ComputePermissionAbstract(BackofficeOperationType op) {
        this.op = op;
    }

    public abstract boolean isAuthorized();

    public String generateErrorMessage() {
        return "A user with role [" + op.getUserRole() + "] can't do the operation [" + op.getOperationType() + "] for the entityType [" + op.getEntityType() + "].";
    }
}
