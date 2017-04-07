package io.snippy.core;

import java.util.Arrays;

/**
 * Created by Ryan on 4/6/2017.
 */

public class Snip {

    private int snipID;
    private int ownerID;
    private String title;
    private String[] tags;
    private String language;
    // private String documentation;
    private String codeSnippet;
    //private Group[] groups; Omitted until groups are implemented

    public Snip(String title, String codeSnippet, String language) {
        this.title = title;
        this.codeSnippet = codeSnippet;
        this.language = language;
    }

    public Snip(int id, int userID,  String ttle, String[] tgs, String lang, String code) {
        this.snipID = id;
        this.ownerID = userID;
        this.title = ttle;
        this.tags = tgs;
        this.language = lang;
        this.codeSnippet = code;
    }

    //Mutators and accessors
    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
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
                "name='" + title + '\'' +
                ", tags=" + Arrays.toString(tags) +
                ", language='" + language + '\'' +
                ", codeSnippet='" + codeSnippet + '\'' +
                '}';
    }
}
