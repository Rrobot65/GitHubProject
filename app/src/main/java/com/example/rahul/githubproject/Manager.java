package com.example.rahul.githubproject;



import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by rahul on 5/15/2018.
 */

public class Manager
{
    private static Manager manager = null;
    private OkHttpClient client;
    private String url;
    private String sort;
    private String order;
    private int page;
    private ArrayList<Repository> repositoryArrayList;

    private Manager()
    {
        sort = "";
        order = "desc";
        page = 1;
        client = new OkHttpClient();
        repositoryArrayList = new ArrayList<>();
    }

    public static Manager getInstance()
    {
        if(manager == null)
        {
            manager = new Manager();
        }

        return manager;
    }

    private void requestAPI(String url, final JSONElementCallback callback)
    {
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(response.body().string());
                callback.onResponse(call, element);
            }
        });


    }

    public void getRepositories(String repositoryName, final String language, final RepositoryCallback callback)
    {
        createURL(repositoryName, language);

        requestAPI(url, new JSONElementCallback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {

            }

            @Override
            public void onResponse(Call call, JsonElement element)
            {
                JsonObject wholeJSONObject = element.getAsJsonObject();
                JsonArray items = wholeJSONObject.getAsJsonArray("items");

                for(int i=0; i<items.size(); i++)
                {
                    Repository repository = new Repository(items.get(i).getAsJsonObject().get("full_name").getAsString(),
                            parseLanguageAndDescription(items.get(i).getAsJsonObject().get("description").toString()),
                            parseLanguageAndDescription(items.get(i).getAsJsonObject().get("language").toString()),
                            items.get(i).getAsJsonObject().get("stargazers_count").getAsString(),
                            parseTime(items.get(i).getAsJsonObject().get("pushed_at").getAsString()));

                    repositoryArrayList.add(repository);
                }

                callback.onResponse(repositoryArrayList);
            }
        });
    }


    private void createURL(String repositoryName, String language)
    {
        for(int i = 0; i < repositoryName.length(); i++)
        {
            if(repositoryName.charAt(i) == ' ')
                repositoryName = repositoryName.substring(0, i-1) + '+' + repositoryName.substring(i+1, repositoryName.length()-1);
        }

        if(language.equals("Any Language"))
            language = "";
        else
            language = "+language:" + language;

        url = "https://api.github.com/search/repositories?page=" + page + "&q=" + repositoryName + language + "&sort=" + sort + "&order=" + order;
    }


    private String parseTime(String lastUpdated)
    {
        String newTime = "Updated On " + lastUpdated.substring(5, 7) + "/" + lastUpdated.substring(8,10)
                + "/" + lastUpdated.substring(0,4);

        return newTime;
    }

    //Deletes the quotation marks that are in the String which was retrieved from the Json file
    private String parseLanguageAndDescription(String oldString)
    {
        String newString = oldString.substring(1, oldString.length()-1);

        return newString;
    }

    public ArrayList<Repository> getRepositoryArrayList()
    {
        return repositoryArrayList;
    }

    public void setRepositoryArrayList(ArrayList<Repository> repositoryArrayList)
    {
        this.repositoryArrayList = repositoryArrayList;
    }
}

interface JSONElementCallback
{
    void onFailure(Call call, IOException e);

    void onResponse(Call call, JsonElement element);
}

interface RepositoryCallback
{
    void onFailure(IOException e);

    void onResponse(ArrayList<Repository> arrayListRepository);
}
