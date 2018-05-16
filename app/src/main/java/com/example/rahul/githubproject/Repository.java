package com.example.rahul.githubproject;

/**
 * Created by rahul on 5/15/2018.
 */

public class Repository
{
    private String name;
    private String description;
    private String language;
    private String starGazers;
    private String updatedDate;

    public Repository(String name, String description, String language, String starGazers, String updatedDate)
    {
        this.name = name;
        this.description = description;
        this.language = language;
        this.starGazers = starGazers;
        this.updatedDate = updatedDate;
    }


    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getLanguage()
    {
        return language;
    }

    public void setLanguage(String language)
    {
        this.language = language;
    }

    public String getStarGazers()
    {
        return starGazers;
    }

    public void setStarGazers(String starGazers)
    {
        this.starGazers = starGazers;
    }

    public String getUpdatedDate()
    {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate)
    {
        this.updatedDate = updatedDate;
    }
}
