package io.snippy.core;

/**
 * Created by Ryan on 4/8/2017.
 */
public class Group {

    private String name;
    private String joinCode;
    private int ownerID;
    private int groupID;

    public Group(int groupID, String name) {
        this.groupID = groupID;
        this.name = name;
    }

    public Group(int groupID, int ownerID, String name) {
        this.groupID = groupID;
        this.name = name;
        this.ownerID = ownerID;
    }

    public String getName() {
        return name;
    }

    public String getCode(){
        return joinCode;
    }

    public int getGroupID(){
        return groupID;
    }

    public int getGroupOwnerID() {
        return ownerID;
    }

    @Override
    public String toString() {
        return "\tName: " + name + ", Owner's ID: " + ownerID + ", Group's ID: " + groupID;
    }
}
