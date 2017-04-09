package io.snippy.core;

/**
 * Created by Ryan on 4/8/2017.
 */
public class Group {

    private String name;
    private String joinCode;
    private int groupID;
    private int owner;

    public Group(int groupID, String name){
        this.groupID = groupID;
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public String getCode(){
        return joinCode;
    }

    public int getGroupID(){
        return groupID;
    }

    public int getOwner(){
        return owner;
    }

}
