package org.epos.backoffice.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

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

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.epos.backoffice.bean.OperationTypeEnum.GET_ALL;
import static org.epos.backoffice.bean.OperationTypeEnum.GET_SINGLE;
import static org.epos.backoffice.bean.RoleEnum.ADMIN;

public abstract class AbstractController<T extends EPOSDataModelEntity> {

	protected final ObjectMapper objectMapper;
	private final HttpServletRequest request;
	protected final Class<T> entityType;

	protected Gson gson = new GsonBuilder()
	        .setPrettyPrinting()
	        .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
	        .create();

	public AbstractController(ObjectMapper objectMapper, HttpServletRequest request, Class<T> entityType) {
		this.objectMapper = objectMapper;
		this.request = request;
		this.entityType = entityType;
	}

	protected User getUserFromSession() {
		return (User) request.getSession().getAttribute("user");
	}
	class LocalDateAdapter implements JsonSerializer<LocalDateTime> {

	    public JsonElement serialize(LocalDateTime date, Type typeOfSrc, JsonSerializationContext context) {
	        return new JsonPrimitive(date.format(DateTimeFormatter.ISO_DATE_TIME)); // "yyyy-mm-dd"
	    }
	}

}
