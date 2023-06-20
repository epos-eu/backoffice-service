package org.epos.backoffice.api.util;

import org.epos.backoffice.bean.OperationTypeEnum;
import org.epos.eposdatamodel.Group;

import java.util.List;
import java.util.Objects;

import static org.epos.backoffice.bean.OperationTypeEnum.*;


public class GroupFilter {

    private OperationTypeEnum operationType;
    private List<Group> userGroups;
    private List<Group> instanceGroup;

    public OperationTypeEnum getOperationType() {
        return operationType;
    }

    public GroupFilter operationType(OperationTypeEnum operationType) {
        this.operationType = operationType;
        return this;
    }

    public List<Group> getUserGroups() {
        return userGroups;
    }

    public GroupFilter userGroup(List<Group> userGroup) {
        this.userGroups = Objects.nonNull(userGroup) ? userGroup : List.of();
        return this;
    }

    public List<Group> getInstanceGroup() {
        return instanceGroup;
    }

    public GroupFilter instanceGroup(List<Group> instanceGroup) {
        this.instanceGroup =  Objects.nonNull(instanceGroup) ? instanceGroup : List.of();
        return this;
    }

    public boolean isOk(){

        if (instanceGroup.isEmpty()){
            return true;
        }

        for (Group group : instanceGroup){
            for (Group userGroup : userGroups){
                if (Objects.equals(group.getId(), userGroup.getId())){
                    switch (userGroup.getRole()) {
                        case EDITOR:
                            if(
                                    Objects.equals(operationType, DATAPRODUCT__CHANGE_STATUS__DRAFT_SUBMITTED) ||
                                    Objects.equals(operationType, MANAGE_DRAFT) ||
                                    Objects.equals(operationType, GET_SINGLE) ||
                                    Objects.equals(operationType, GET_ALL)
                            )
                                return true;
                        case REVIEWER:
                            if(
                                    Objects.equals(operationType, DATAPRODUCT__CHANGE_STATUS__SUBMITTED_PUBLISHED) ||
                                    Objects.equals(operationType, DATAPRODUCT__CHANGE_STATUS__SUBMITTED_DISCARDED) ||
                                    Objects.equals(operationType, GET_SINGLE) ||
                                    Objects.equals(operationType, GET_ALL)
                            )
                                return true;
                        case VIEWER:
                            if(
                                    Objects.equals(operationType, GET_SINGLE) ||
                                    Objects.equals(operationType, GET_ALL)
                            )
                                return true;
                        case ADMIN:
                            return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean notOk(){
        return !isOk();
    }


}
