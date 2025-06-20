package org.epos.backoffice.api.util;

import java.util.Objects;

public class AddUserToGroupBean {

    private String userid;
    private String groupid;
    private String role;
    private String statusType;

    public AddUserToGroupBean(){}

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatusType() {
        return statusType;
    }

    public void setStatusType(String statusType) {
        this.statusType = statusType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AddUserToGroupBean that = (AddUserToGroupBean) o;
        return Objects.equals(userid, that.userid) && Objects.equals(groupid, that.groupid) && role == that.role && statusType == that.statusType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userid, groupid, role, statusType);
    }

    @Override
    public String toString() {
        return "AddUserToGroupBean{" +
                "userid='" + userid + '\'' +
                ", groupid='" + groupid + '\'' +
                ", role=" + role +
                ", statusType=" + statusType +
                '}';
    }
}
