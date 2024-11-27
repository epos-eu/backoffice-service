package org.epos.backoffice.api.util;

import abstractapis.AbstractAPI;
import dao.EposDataModelDAO;
import metadataapis.*;
import model.*;
import org.epos.eposdatamodel.*;
import org.epos.eposdatamodel.User;
import org.epos.handler.dbapi.service.EntityManagerService;
import usermanagementapis.UserGroupManagementAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EPOSDataModelManager {

    public static ApiResponseMessage getEPOSDataModelEposDataModelEntity(String meta_id, String instance_id, User user, EntityNames entityNames, Class clazz) {

        AbstractAPI dbapi = AbstractAPI.retrieveAPI(entityNames.name());
        if (meta_id == null)
            return new ApiResponseMessage(ApiResponseMessage.ERROR, "The [meta_id] field can't be left blank");
        if(instance_id == null) {
            instance_id = "all";
        }

        /**
         * GET OPERATIONS ARE FREE FOR ALL ENTITIES EXCEPT PERSON CONTACTPOINT AND ORGANIZATIONS
         * WHICH ARE ACCESSIBLE ONLY FOR ADMINS (isAdmin)
         **/
        if((entityNames.equals(EntityNames.PERSON)
                || entityNames.equals(EntityNames.ORGANIZATION)
                || entityNames.equals(EntityNames.CONTACTPOINT)) && !user.getIsAdmin()){
            return new ApiResponseMessage(ApiResponseMessage.UNAUTHORIZED, "A user which is not Admin can't access PERSON/ORGANIZATION/CONTACTPOINT entities due to privacy settings");
        }

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
                list = new ArrayList<>();
                EPOSDataModelEntity entity = (EPOSDataModelEntity) dbapi.retrieve(instance_id);
                if(entity!=null) list.add(entity);
            }
        }

        List<EPOSDataModelEntity> revertedList = new ArrayList<>();
        list.forEach(e -> {
            for (Group group : UserGroupManagementAPI.retrieveAllGroups()) {
                if(group.getEntities().contains(e.getMetaId())){
                    e.getGroups().add(group);
                }
            }
            revertedList.add(0, e);
        });

        if (list.isEmpty())
            return new ApiResponseMessage(ApiResponseMessage.OK, new ArrayList<EPOSDataModelEntity>());

        return new ApiResponseMessage(ApiResponseMessage.OK, list);
    }

    public static ApiResponseMessage createEposDataModelEntity(EPOSDataModelEntity obj, User user, EntityNames entityNames, Class clazz) {
        /** CHECK PERMISSIONS **/
        Boolean isAccessibleByUser = false;

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
            AbstractAPI dbapi = AbstractAPI.retrieveAPI(entityNames.name());
            clazz = AbstractAPI.retrieveClass(entityNames.name());

            if(obj.getInstanceId() != null)
                obj.setInstanceChangedId(obj.getInstanceId());

            obj.setStatus(obj.getStatus()==null? StatusType.DRAFT : obj.getStatus());
            obj.setEditorId(user.getAuthIdentifier());
            obj.setFileProvenance("instance created with the backoffice");

            LinkedEntity reference = dbapi.create(obj, null,null,null);

                EntityManagerService.getInstance().getCache().evictAll();

            return new ApiResponseMessage(ApiResponseMessage.OK, reference);
        }
        return new ApiResponseMessage(ApiResponseMessage.UNAUTHORIZED, "The user can't manage this action");
    }

    public static ApiResponseMessage updateEposDataModelEntity(EPOSDataModelEntity obj, User user, EntityNames entityNames, Class clazz) {

        /** CHECK PERMISSIONS **/
        Boolean isAccessibleByUser = false;

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
            AbstractAPI dbapi = AbstractAPI.retrieveAPI(entityNames.name());

            if (obj.getInstanceId() == null) {
                return new ApiResponseMessage(ApiResponseMessage.ERROR, "InstanceId required");
            }
            if(obj.getInstanceChangedId() == null || obj.getInstanceChangedId().isEmpty())
                obj.setInstanceChangedId(null);

            obj.setStatus(obj.getStatus()==null? StatusType.DRAFT : obj.getStatus());
            obj.setEditorId(user.getAuthIdentifier());
            obj.setFileProvenance("instance created with the backoffice");

            EPOSDataModelEntity eposDataModelEntity = (EPOSDataModelEntity) dbapi.retrieve(obj.getInstanceId());

            if(eposDataModelEntity.getStatus()==null && obj.getStatus()!=null && (obj.getStatus().equals(StatusType.ARCHIVED) || obj.getStatus().equals(StatusType.DISCARDED))) {
                return new ApiResponseMessage(ApiResponseMessage.ERROR, "Unable to update a  PUBLISHED instance");
            }

            if(!eposDataModelEntity.getStatus().equals(obj.getStatus())){
                eposDataModelEntity.setStatus(obj.getStatus());
                obj = eposDataModelEntity;

                if(eposDataModelEntity.getStatus().equals(StatusType.PUBLISHED)){
                    // Get the published instance of the entity and swap into archived status
                    dbapi.retrieveAll().stream().filter(item-> ((EPOSDataModelEntity) item).getStatus().equals(StatusType.PUBLISHED) && ((EPOSDataModelEntity) item).getMetaId().equals(eposDataModelEntity.getMetaId()))
                            .forEach(item -> {
                                ((EPOSDataModelEntity) item).setStatus(StatusType.ARCHIVED);
                                dbapi.create(item, null,null,null);
                            });
                }
            }

            LinkedEntity reference = dbapi.create(obj, null,null,null);

            EntityManagerService.getInstance().getCache().evictAll();

            return new ApiResponseMessage(ApiResponseMessage.OK, reference);
        }
        return new ApiResponseMessage(ApiResponseMessage.UNAUTHORIZED, "The user can't manage this action");
    }

    public static boolean deleteEposDataModelEntity(String instance_id, User user, EntityNames entityNames, Class clazz) {
        AbstractAPI dbapi = AbstractAPI.retrieveAPI(entityNames.name());

        dbapi.delete(instance_id);

        EntityManagerService.getInstance().getCache().evictAll();

        return true;
    }

}
