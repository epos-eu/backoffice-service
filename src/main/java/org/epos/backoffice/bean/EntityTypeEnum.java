package org.epos.backoffice.bean;

import org.epos.eposdatamodel.*;
import org.springframework.lang.NonNull;

import java.util.*;

public enum EntityTypeEnum {
    USER("User"),
    CONTACTPOINT("ContactPoint"),
    DATAPRODUCT("DataProduct"),
    DISTRIBUTION("Distribution"),
    WEBSERVICE("WebService"),
    OPERATION("Operation"),
    ORGANIZATION("Organization"),
    CATEGORY("Category"),
    CATEGORYSCHEME("CategoryScheme"),
    PERSON("Person");

    private static final Map<String, Class<? extends EPOSDataModelEntity>> mapEntityNameToClass;
    static {
        mapEntityNameToClass = Map.ofEntries(
                Map.entry("contactpoint", ContactPoint.class),
                Map.entry("dataproduct", DataProduct.class),
                Map.entry("distribution", Distribution.class),
                Map.entry("equipment", Equipment.class),
                Map.entry("facility", Facility.class),
                Map.entry("operation", Operation.class),
                Map.entry("organization", Organization.class),
                Map.entry("person", Person.class),
                Map.entry("service", Service.class),
                Map.entry("softwaresourceapplication", SoftwareApplication.class),
                Map.entry("softwaresourcecode", SoftwareSourceCode.class),
                Map.entry("webservice", WebService.class),
                Map.entry("category", Category.class),
                Map.entry("categoryscheme", CategoryScheme.class)
        );
    }

    public static Class<? extends EPOSDataModelEntity> EposDataModelClassFromString(String className){
        Objects.requireNonNull(className, "Class name String cannot be null");
        return  mapEntityNameToClass.get(className.toLowerCase().trim());
    }

    public static Class<? extends EPOSDataModelEntity> EposDataModelClassFromEntityTypeEnum(EntityTypeEnum entityTypeEnum){
        Objects.requireNonNull(entityTypeEnum, "Entity Type enum cannot be null");
        return  mapEntityNameToClass.get(entityTypeEnum.text.toLowerCase());
    }


    private final String text;

    EntityTypeEnum(String text) {
        this.text = text;
    }

    public static EntityTypeEnum fromString(String text) {
        for (EntityTypeEnum b : EntityTypeEnum.values()) {
            if (b.text.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }


}