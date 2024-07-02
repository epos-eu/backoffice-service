package org.epos.backoffice.api.util;

import java.util.Objects;

public class RemoveUserFromGroupBean {

    private String userid;
    private String groupid;

    public RemoveUserFromGroupBean(){}

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RemoveUserFromGroupBean that = (RemoveUserFromGroupBean) o;
        return Objects.equals(userid, that.userid) && Objects.equals(groupid, that.groupid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userid, groupid);
    }

    @Override
    public String toString() {
        return "RemoveUserFromGroupBean{" +
                "userid='" + userid + '\'' +
                ", groupid='" + groupid + '\'' +
                '}';
    }
}
