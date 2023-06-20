package org.epos.backoffice.bean;

import org.epos.eposdatamodel.State;

import java.util.Objects;

public class StateWrapper{
    private State state;
    private Boolean justThisOne;

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Boolean getJustThisOne() {
        return justThisOne;
    }

    public void setJustThisOne(Boolean justThisOne) {
        this.justThisOne = justThisOne;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StateWrapper that = (StateWrapper) o;
        return getState() == that.getState() && getJustThisOne() == that.getJustThisOne();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getState(), getJustThisOne());
    }

    @Override
    public String toString() {
        return "StateWrapper{" +
                "state=" + state +
                ", justThisOne=" + justThisOne +
                '}';
    }
}
