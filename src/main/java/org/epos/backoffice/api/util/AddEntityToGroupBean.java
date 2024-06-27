package org.epos.backoffice.api.util;

import java.util.Objects;

public class AddEntityToGroupBean {

    private String metaid;
    private String groupid;

    public AddEntityToGroupBean(){}

    public String getMetaid() {
        return metaid;
    }

    public void setMetaid(String userid) {
        this.metaid = metaid;
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
        AddEntityToGroupBean that = (AddEntityToGroupBean) o;
        return Objects.equals(metaid, that.metaid) && Objects.equals(groupid, that.groupid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(metaid, groupid);
    }

    @Override
    public String toString() {
        return "AddEntityToGroupBean{" +
                "metaid='" + metaid + '\'' +
                ", groupid='" + groupid + '\'' +
                '}';
    }
}
