package org.epos.backoffice.api.exception;

import java.util.List;

import org.epos.backoffice.bean.User;
import org.epos.eposdatamodel.Category;
import org.epos.eposdatamodel.CategoryScheme;
import org.epos.eposdatamodel.LinkedEntity;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2022-02-11T09:12:11.707Z[GMT]")
public class ConceptSchemeApiResponseMessage {
    public static final int ERROR = 1;
    public static final int WARNING = 2;
    public static final int INFO = 3;
    public static final int OK = 4;
    public static final int TOO_BUSY = 5;
    public static final int UNAUTHORIZED = 6;

    int code;
    String type;
    String message;
    LinkedEntity entity;
    List<CategoryScheme> listOfEntities;
    List<User> listOfUsers;

	public ConceptSchemeApiResponseMessage() {
    }

    public ConceptSchemeApiResponseMessage(int code, String message) {
        this.code = code;
        switch (code) {
            case ERROR:
                setType("error");
                break;
            case WARNING:
                setType("warning");
                break;
            case INFO:
                setType("info");
                break;
            case OK:
                setType("ok");
                break;
            case TOO_BUSY:
                setType("too busy");
                break;
            case UNAUTHORIZED:
                setType("unauthorized");
                break;
            default:
                setType("unknown");
                break;
        }
        this.message = message;
    }

    public ConceptSchemeApiResponseMessage(int code, LinkedEntity entity) {
    	this.code = code;
        switch (code) {
            case ERROR:
                setType("error");
                break;
            case WARNING:
                setType("warning");
                break;
            case INFO:
                setType("info");
                break;
            case OK:
                setType("ok");
                break;
            case TOO_BUSY:
                setType("too busy");
                break;
            case UNAUTHORIZED:
                setType("unauthorized");
                break;
            default:
                setType("unknown");
                break;
        }
        this.entity = entity;
	}

	public ConceptSchemeApiResponseMessage(int code, List<CategoryScheme> list) {
		 this.code = code;
	        switch (code) {
	            case ERROR:
	                setType("error");
	                break;
	            case WARNING:
	                setType("warning");
	                break;
	            case INFO:
	                setType("info");
	                break;
	            case OK:
	                setType("ok");
	                break;
	            case TOO_BUSY:
	                setType("too busy");
	                break;
	            case UNAUTHORIZED:
	                setType("unauthorized");
	                break;
	            default:
	                setType("unknown");
	                break;
	        }
	        this.listOfEntities = list;
	}
	
	public ConceptSchemeApiResponseMessage(int code, boolean userManagement, List<User> list) {
		 this.code = code;
	        switch (code) {
	            case ERROR:
	                setType("error");
	                break;
	            case WARNING:
	                setType("warning");
	                break;
	            case INFO:
	                setType("info");
	                break;
	            case OK:
	                setType("ok");
	                break;
	            case TOO_BUSY:
	                setType("too busy");
	                break;
	            case UNAUTHORIZED:
	                setType("unauthorized");
	                break;
	            default:
	                setType("unknown");
	                break;
	        }
	        this.listOfUsers = list;
	}


	public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    public LinkedEntity getEntity() {
        return entity;
    }

    public void setEntity(LinkedEntity entity) {
        this.entity = entity;
    }
    
    public List<CategoryScheme> getListOfEntities() {
        return listOfEntities;
    }

    public void setEntity(List<CategoryScheme> listOfEntities) {
        this.listOfEntities = listOfEntities;
    }
    

    public List<User> getListOfUsers() {
		return listOfUsers;
	}

	public void setListOfUsers(List<User> listOfUsers) {
		this.listOfUsers = listOfUsers;
	}

	@Override
	public String toString() {
		return "ApiResponseMessage [code=" + code + ", type=" + type + ", message=" + message + ", entity=" + entity
				+ ", listOfEntities=" + listOfEntities + ", listOfUsers=" + listOfUsers + "]";
	}


}
