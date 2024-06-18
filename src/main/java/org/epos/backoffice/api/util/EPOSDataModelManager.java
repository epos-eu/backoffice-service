package org.epos.backoffice.api.util;

import abstractapis.AbstractAPI;
import commonapis.*;
import metadataapis.*;
import model.*;
import org.epos.eposdatamodel.User;
import org.epos.backoffice.api.exception.ApiResponseMessage;
import org.epos.eposdatamodel.EPOSDataModelEntity;
import org.epos.eposdatamodel.LinkedEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class EPOSDataModelManager {

    public static ApiResponseMessage getEPOSDataModelEposDataModelEntity(String meta_id, String instance_id, User user, EntityNames entityNames, Class clazz) {
        AbstractAPI dbapi = retrieveAPI(entityNames.name(),clazz);
        if (meta_id == null)
            return new ApiResponseMessage(1, "The [meta_id] field can't be left blank");
        if(instance_id == null) {
            instance_id = "all";
        }

		/*BackofficeOperationType operationType = new BackofficeOperationType()
				.operationType(meta_id.equals("all") ? GET_ALL : GET_SINGLE)
				.entityType(Category.class)
				.userRole(user.getRole());


		ComputePermissionAbstract computePermission = new ComputePermissionNoGroup(operationType);
		if (!computePermission.isAuthorized())
			return new ApiResponseMessage(1, computePermission.generateErrorMessage());*/

        System.out.println(meta_id+" "+instance_id);

        List<EPOSDataModelEntity> list;
        if (meta_id.equals("all")) {
            list = dbapi.getDbaccess().getAllFromDB(clazz);
        } else {
            if(instance_id.equals("all")) {
                list = dbapi.getDbaccess().getAllFromDB(clazz);
                list = list.stream()
                        .filter(
                                elem -> elem.getMetaId().equals(meta_id)
                        )
                        .collect(Collectors.toList());

            }else {
                list = dbapi.getDbaccess().getOneFromDBByInstanceId(instance_id, clazz);
            }
        }


        List<EPOSDataModelEntity> revertedList = new ArrayList<>();
        list.forEach(e -> revertedList.add(0, e));

        if (list.isEmpty())
            return new ApiResponseMessage(ApiResponseMessage.OK, new ArrayList<EPOSDataModelEntity>());

        return new ApiResponseMessage(ApiResponseMessage.OK, list);
    }

    public static ApiResponseMessage createEposDataModelEntity(EPOSDataModelEntity obj, User user, EntityNames entityNames, Class clazz) {
        AbstractAPI dbapi = retrieveAPI(entityNames.name(),clazz);
        /** ID MANAGEMENT
         * if UID == NULL --> Generate a new UID
         * Brand new Category? --> InstanceId = null && InstanceChangeId == null
         * New Category from existing one? --> InstanceChangeId == OLD InstanceId
         *
         **/
        if(obj.getUid()==null) {
            System.err.println("UID undefined, generating a new one");
            obj.setUid(obj.getClass().getSimpleName().toLowerCase()+"/"+ UUID.randomUUID());
        }
        obj.setInstanceId(null);
        obj.setInstanceChangedId(null);

        obj.setStatus(StatusType.DRAFT);
        obj.setEditorId(user.getAuthIdentifier());
        obj.setFileProvenance("instance created with the backoffice");

		/*if(!ManagePermissions.checkPermissions(category, EntityTypeEnum.CATEGORY, user))
			return new ApiResponseMessage(ApiResponseMessage.ERROR, "You don't have auth on the groups of this instance");*/

        LinkedEntity reference = dbapi.create(obj);

        return new ApiResponseMessage(ApiResponseMessage.OK, reference);
    }

    public static ApiResponseMessage updateEposDataModelEntity(EPOSDataModelEntity obj, User user, EntityNames entityNames, Class clazz) {
        AbstractAPI dbapi = retrieveAPI(entityNames.name(),clazz);

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
        AbstractAPI dbapi = retrieveAPI(entityNames.name(),clazz);
        List<EPOSDataModelEntity> list = dbapi.getDbaccess().getOneFromDBByInstanceId(instance_id, clazz);

        if (list.isEmpty()) return false;
        EPOSDataModelEntity instance = list.get(0);

        dbapi.getDbaccess().deleteObject(instance.getInstanceId());

        return true;
    }

    private static AbstractAPI retrieveAPI(String entityType, Class<?> edmClass){
        AbstractAPI api = null;

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
}
