package org.epos.backoffice.bean;

public enum OperationTypeEnum {
    GET_SINGLE,
    GET_ALL,
    MANAGE_PUBLISHED,
    MANAGE_DRAFT,
    DATAPRODUCT__CHANGE_STATUS__DRAFT_SUBMITTED,
    DATAPRODUCT__CHANGE_STATUS__SUBMITTED_DRAFT,
    DATAPRODUCT__CHANGE_STATUS__SUBMITTED_PUBLISHED,
    DATAPRODUCT__CHANGE_STATUS__SUBMITTED_DISCARDED,
    DATAPRODUCT__CHANGE_STATUS__DRAFT_DISCARDED,
    OTHER
}