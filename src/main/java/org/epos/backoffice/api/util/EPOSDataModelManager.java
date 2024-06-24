package org.epos.backoffice.api.util;

import abstractapis.AbstractAPI;
import commonapis.*;
import metadataapis.*;
import model.*;
import model.Address;
import model.Category;
import model.CategoryScheme;
import model.Distribution;
import model.Element;
import model.Equipment;
import model.Facility;
import model.Identifier;
import model.Mapping;
import model.Operation;
import model.Organization;
import model.Person;
import model.QuantitativeValue;
import model.SoftwareApplication;
import model.SoftwareSourceCode;
import org.epos.eposdatamodel.*;
import org.epos.backoffice.api.exception.ApiResponseMessage;
import org.epos.eposdatamodel.User;
import usermanagementapis.UserGroupManagementAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class EPOSDataModelManager {

    public static ApiResponseMessage getEPOSDataModelEposDataModelEntity(String meta_id, String instance_id, User user, EntityNames entityNames, Class clazz) {

        AbstractAPI dbapi = retrieveAPI(entityNames.name());
        clazz = retrieveClass(entityNames.name());
        if (meta_id == null)
            return new ApiResponseMessage(ApiResponseMessage.ERROR, "The [meta_id] field can't be left blank");
        if(instance_id == null) {
            instance_id = "all";
        }

        /**
         * GET OPERATIONS ARE FREE FOR ALL ENTITIES EXCEPT PERSON CONTACTPOINT AND ORGANIZATIONS
         * WHICH ARE ACCESSIBLE ONLY FOR ADMINS (isAdmin)
         **/
        System.out.println(user);
        if((entityNames.equals(EntityNames.PERSON)
                || entityNames.equals(EntityNames.ORGANIZATION)
                || entityNames.equals(EntityNames.CONTACTPOINT)) && !user.getIsAdmin()){
            return new ApiResponseMessage(ApiResponseMessage.UNAUTHORIZED, "A user which is not Admin can't access PERSON/ORGANIZATION/CONTACTPOINT entities due to privacy settings");
        }

        System.out.println(meta_id+" "+instance_id+" "+clazz);

        List<EPOSDataModelEntity> list;
        if (meta_id.equals("all")) {
            list = dbapi.retrieveAll();
        } else {
            if(instance_id.equals("all")) {
                list = dbapi.retrieveAll();
                list = list.stream()
                        .filter(
                                elem -> elem.getMetaId().equals(meta_id)
                        )
                        .collect(Collectors.toList());

            }else {
                list = List.of((EPOSDataModelEntity)dbapi.retrieve(instance_id));
            }
        }

        List<EPOSDataModelEntity> revertedList = new ArrayList<>();
        list.forEach(e -> revertedList.add(0, e));

        if (list.isEmpty())
            return new ApiResponseMessage(ApiResponseMessage.OK, new ArrayList<EPOSDataModelEntity>());

        return new ApiResponseMessage(ApiResponseMessage.OK, list);
    }

    public static ApiResponseMessage createEposDataModelEntity(EPOSDataModelEntity obj, User user, EntityNames entityNames, Class clazz) {
        /** CHECK PERMISSIONS **/
        Boolean isAccessibleByUser = false;

        System.out.println(user.toString());
        System.out.println(user.getIsAdmin());
        if(!user.getIsAdmin()){
            if(obj.getGroups()!=null && !obj.getGroups().isEmpty()){
                for(Group group : obj.getGroups()){
                    for(UserGroup group1 : user.getGroups()){
                        if(group.getId().equals(group1.getGroupId())
                                && (
                                group1.getRole().equals(RoleType.ADMIN)
                                        ||group1.getRole().equals(RoleType.REVIEWER)
                                        ||group1.getRole().equals(RoleType.EDITOR))){
                            isAccessibleByUser = true;
                        }
                    }
                }
            }
        }else{
            isAccessibleByUser = true;
        }
        if(isAccessibleByUser) {
            AbstractAPI dbapi = retrieveAPI(entityNames.name());
            clazz = retrieveClass(entityNames.name());

            obj.setInstanceId(null);
            obj.setInstanceChangedId(null);

            obj.setStatus(StatusType.DRAFT);
            obj.setEditorId(user.getAuthIdentifier());
            obj.setFileProvenance("instance created with the backoffice");

            LinkedEntity reference = dbapi.create(obj);

            return new ApiResponseMessage(ApiResponseMessage.OK, reference);
        }
        return new ApiResponseMessage(ApiResponseMessage.UNAUTHORIZED, "The user can't manage this action");
    }

    public static ApiResponseMessage updateEposDataModelEntity(EPOSDataModelEntity obj, User user, EntityNames entityNames, Class clazz) {
        AbstractAPI dbapi = retrieveAPI(entityNames.name());
        clazz = retrieveClass(entityNames.name());

        if(obj.getStatus()!=null && (obj.getStatus().equals(StatusType.ARCHIVED) || obj.getStatus().equals(StatusType.PUBLISHED))) {
            return new ApiResponseMessage(ApiResponseMessage.ERROR, "Unable to update a ARCHIVED or PUBLISHED instance");
        }
        if (obj.getInstanceId() == null) {
            return new ApiResponseMessage(ApiResponseMessage.ERROR, "InstanceId required");
        }
        if(obj.getInstanceChangedId() == null || obj.getInstanceChangedId().isEmpty())
            obj.setInstanceChangedId(null);

        //category.setEditorId(user.getMetaId());
        obj.setFileProvenance("instance created with the backoffice");

		/*if(!ManagePermissions.checkPermissions(category, EntityTypeEnum.CATEGORY, user))
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "You don't have auth on the groups of this instance");*/


        LinkedEntity reference = dbapi.create(obj);


        return new ApiResponseMessage(ApiResponseMessage.OK, reference);
    }

    public static boolean deleteEposDataModelEntity(String instance_id, User user, EntityNames entityNames, Class clazz) {
        AbstractAPI dbapi = retrieveAPI(entityNames.name());
        clazz = retrieveClass(entityNames.name());
        List<EPOSDataModelEntity> list = dbapi.getDbaccess().getOneFromDBByInstanceId(instance_id, clazz);

        if (list.isEmpty()) return false;
        EPOSDataModelEntity instance = list.get(0);

        dbapi.getDbaccess().deleteObject(instance.getInstanceId());

        return true;
    }


    private static AbstractAPI retrieveAPI(String entityType){
        AbstractAPI api = null;
        Class<?> edmClass = null;

        switch(EntityNames.valueOf(entityType)){
            case PERSON:
                edmClass = Person.class;
                api = new PersonAPI(entityType, edmClass);
                break;
            case MAPPING:
                edmClass = Mapping.class;
                api = new MappingAPI(entityType, edmClass);
                break;
            case CATEGORY:
                edmClass = Category.class;
                api = new CategoryAPI(entityType, edmClass);
                break;
            case FACILITY:
                edmClass = Facility.class;
                api = new FacilityAPI(entityType, edmClass);
                break;
            case EQUIPMENT:
                edmClass = Equipment.class;
                api = new EquipmentAPI(entityType, edmClass);
                break;
            case OPERATION:
                edmClass = Operation.class;
                api = new OperationAPI(entityType, edmClass);
                break;
            case WEBSERVICE:
                edmClass = Webservice.class;
                api = new WebServiceAPI(entityType, edmClass);
                break;
            case DATAPRODUCT:
                edmClass = Dataproduct.class;
                api = new DataProductAPI(entityType, edmClass);
                break;
            case CONTACTPOINT:
                edmClass = Contactpoint.class;
                api = new ContactPointAPI(entityType, edmClass);
                break;
            case DISTRIBUTION:
                edmClass = Distribution.class;
                api = new DistributionAPI(entityType, edmClass);
                break;
            case ORGANIZATION:
                edmClass = Organization.class;
                api = new OrganizationAPI(entityType, edmClass);
                break;
            case CATEGORYSCHEME:
                edmClass = CategoryScheme.class;
                api = new CategorySchemeAPI(entityType, edmClass);
                break;
            case SOFTWARESOURCECODE:
                edmClass = SoftwareSourceCode.class;
                api = new SoftwareSourceCodeAPI(entityType, edmClass);
                break;
            case SOFTWAREAPPLICATION:
                edmClass = SoftwareApplication.class;
                api = new SoftwareApplicationAPI(entityType, edmClass);
                break;
            case ADDRESS:
                edmClass = Address.class;
                api = new AddressAPI(entityType, edmClass);
                break;
            case ELEMENT:
                edmClass = Element.class;
                api = new ElementAPI(entityType, edmClass);
                break;
            case LOCATION:
                edmClass = Spatial.class;
                api = new SpatialAPI(entityType, edmClass);
                break;
            case PERIODOFTIME:
                edmClass = Temporal.class;
                api = new TemporalAPI(entityType, edmClass);
                break;
            case IDENTIFIER:
                edmClass = Identifier.class;
                api = new IdentifierAPI(entityType, edmClass);
                break;
            case QUANTITATIVEVALUE:
                edmClass = QuantitativeValue.class;
                api = new QuantitativeValueAPI(entityType, edmClass);
                break;
            case DOCUMENTATION:
                edmClass = Element.class;
                api = new DocumentationAPI(entityType, edmClass);
                break;
            case PARAMETER:
                edmClass = SoftwareapplicationParameters.class;
                api = new ParameterAPI(entityType, edmClass);
                break;
            case RELATION:
                System.out.println("Relation empty case");
                break;
        }
        return api;
    }

    private static Class retrieveClass(String entityType){

        Class<?> edmClass = null;

        switch(EntityNames.valueOf(entityType)){
            case PERSON:
                edmClass = Person.class;
                break;
            case MAPPING:
                edmClass = Mapping.class;
                break;
            case CATEGORY:
                edmClass = Category.class;
                break;
            case FACILITY:
                edmClass = Facility.class;
                break;
            case EQUIPMENT:
                edmClass = Equipment.class;
                break;
            case OPERATION:
                edmClass = Operation.class;
                break;
            case WEBSERVICE:
                edmClass = Webservice.class;
                break;
            case DATAPRODUCT:
                edmClass = Dataproduct.class;
                break;
            case CONTACTPOINT:
                edmClass = Contactpoint.class;
                break;
            case DISTRIBUTION:
                edmClass = Distribution.class;
                break;
            case ORGANIZATION:
                edmClass = Organization.class;
                break;
            case CATEGORYSCHEME:
                edmClass = CategoryScheme.class;
                break;
            case SOFTWARESOURCECODE:
                edmClass = SoftwareSourceCode.class;
                break;
            case SOFTWAREAPPLICATION:
                edmClass = SoftwareApplication.class;
                break;
            case ADDRESS:
                edmClass = Address.class;
                break;
            case ELEMENT:
                edmClass = Element.class;
                break;
            case LOCATION:
                edmClass = Spatial.class;
                break;
            case PERIODOFTIME:
                edmClass = Temporal.class;
                break;
            case IDENTIFIER:
                edmClass = Identifier.class;
                break;
            case QUANTITATIVEVALUE:
                edmClass = QuantitativeValue.class;
                break;
            case DOCUMENTATION:
                edmClass = Element.class;
                break;
            case PARAMETER:
                edmClass = SoftwareapplicationParameters.class;
                break;
            case RELATION:
                System.out.println("Relation empty case");
                break;
        }
        return edmClass;
    }
}
