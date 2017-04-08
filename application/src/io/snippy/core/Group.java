package io.snippy.core;

/**
 * Created by Ryan on 4/8/2017.
 */
public class Group {

    private String name;
    private int groupID;

    public Group(int groupID, String name){
        this.groupID = groupID;
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public int getGroupID(){
        return groupID;
    }

}
