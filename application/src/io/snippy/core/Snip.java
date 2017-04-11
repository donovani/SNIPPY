package io.snippy.core;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Ryan on 4/6/2017.
 */

public class Snip {

    private int snipID = -1;
    private int ownerID;
    private String title;
    private ArrayList<String> tags = new ArrayList<String>();
    private String language;
    private String codeSnippet;

    public Snip(String title, String codeSnippet, String language, ArrayList<String> t) {
        this.title = title;
        this.codeSnippet = codeSnippet;
        this.language = language;
        this.tags= new ArrayList<String>(t);
    }

    public Snip(int id, int userID, String ttle, ArrayList<String> t, String lang, String code) {
        this.snipID = id;
        this.ownerID = userID;
        this.title = ttle;
        this.tags = new ArrayList<String>(t);
        this.language = lang;
        this.codeSnippet = code;
    }

    //Mutators and accessors
    public int getID() {
        return snipID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public boolean addTag(String tag){
        if (tags.contains(tag)){
            return false;
        }
        tags.add(tag);
        return true;
    }
    public void removeTag(String tag){
        for (String t: tags){
            if (t.equals(tag)){
                tags.remove(t);
            }
        }
    }



    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCodeSnippet() {
        return codeSnippet;
    }

    public void setCodeSnippet(String codeSnippet) {
        this.codeSnippet = codeSnippet;
    }

    @Override
    public String toString() {
        return "Snip{" +
                "snipID=" + snipID +
                ", ownerID=" + ownerID +
                ", title='" + title + '\'' +
                ", tags=" +tags +
                ", language='" + language + '\'' +
                ", codeSnippet='" + codeSnippet + '\'' +
                '}';
    }
}
