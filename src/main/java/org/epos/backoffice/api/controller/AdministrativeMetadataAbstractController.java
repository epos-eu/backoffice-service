package org.epos.backoffice.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.epos.eposdatamodel.EPOSDataModelEntity;

import javax.servlet.http.HttpServletRequest;

public abstract class AdministrativeMetadataAbstractController<T extends EPOSDataModelEntity> extends BackofficeAbstractController<T> {
    public AdministrativeMetadataAbstractController(ObjectMapper objectMapper, HttpServletRequest request, Class<T> entityType) {
        super(objectMapper, request, entityType);
    }
}
