package com.example.rahul.githubproject;



import android.util.Log;

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
    //All variables
    private static Manager manager = null;
    private OkHttpClient client;
    private String url;
    private String sort;
    private String order;
    private int page;
    private ArrayList<Repository> repositoryArrayList;
    private String searchedRepositoryName;
    private String language;
    private boolean firstTime;
    private RecyclerViewAdapter adapter;

    private Manager()
    {
        sort = "";
        order = "desc";
        page = 1;
        firstTime = true;
        client = new OkHttpClient();
        repositoryArrayList = new ArrayList<>();
    }

    //This makes Manager a singleton
    public static Manager getInstance()
    {
        if(manager == null)
        {
            manager = new Manager();
        }

        return manager;
    }

    //This function is where it uses the url and gets a JSON element
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

    //Whenever the app needs to get the repositories this function is called
    public void getRepositories(String searchedRepositoryName, final String language, final RepositoryCallback callback)
    {
        //This if checks if you've just come from the previous activity
        if(firstTime == true)
        {
            sort = "";
            order = "desc";
            createURL(searchedRepositoryName, language);
            firstTime = false;
        }

        //Calls the request API to get the Json element
        requestAPI(url, new JSONElementCallback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, JsonElement element)
            {
                try
                {
                    //Gets the Json element from the requestAPI function and gets the items array from it
                    JsonObject wholeJSONObject = element.getAsJsonObject();
                    JsonArray items = wholeJSONObject.getAsJsonArray("items");

                    //Goes through the items array which has all the repositories
                    for (int i = 0; i < items.size(); i++)
                    {
                        try
                        {
                            //Gets each repository's attributes and stores it in a Repository object which gets added to the array list of repositories
                            Repository repository = new Repository(items.get(i).getAsJsonObject().get("full_name").getAsString(),
                                    parseLanguageAndDescription(items.get(i).getAsJsonObject().get("description").toString()),
                                    parseLanguageAndDescription(items.get(i).getAsJsonObject().get("language").toString()),
                                    items.get(i).getAsJsonObject().get("stargazers_count").toString(),
                                    parseTime(items.get(i).getAsJsonObject().get("pushed_at").toString()));

                            repositoryArrayList.add(repository);
                        }
                        catch (Exception e)
                        {
                        }
                    }
                    callback.onResponse(repositoryArrayList);
                }
                catch (Exception e)
                {
                    callback.onFailure(e);
                }
            }
        });
    }

    //This function creates the default URL (first page, sorting by best match)
    private void createURL(String searchedRepositoryName, String language)
    {
        //This for loop finds any spaces within the searched repository name and replaces them with plus signs for the url
        for (int i = 0; i < searchedRepositoryName.length(); i++)
        {
            if (searchedRepositoryName.charAt(i) == ' ')
                searchedRepositoryName = searchedRepositoryName.substring(0, i) + '+' + searchedRepositoryName.substring(i+1, searchedRepositoryName.length());
        }
        this.searchedRepositoryName = searchedRepositoryName;


        if (language.equals("Any Language"))
            language = "";
        else
            language = "+language:" + language;

        this.language = language;


        url = "https://api.github.com/search/repositories?page=" + page + "&q=" + searchedRepositoryName + language + "&sort=" + sort + "&order=" + order;
    }

    //When selecting a different sort from the drop down menu this function gets called
    public void updateSortParameter(String sortString)
    {
            page = 1;
            repositoryArrayList.clear();

            //This if statment checks what sort you've picked and changes the sort and order strings
            //which is used in the URL
            if (sortString.equals("Best Match"))
            {
                sort = "";
                order = "desc";
            } else if (sortString.equals("Most Stars"))
            {
                sort = "stars";
                order = "desc";
            } else if (sortString.equals("Fewest Stars"))
            {
                sort = "stars";
                order = "asc";
            } else if (sortString.equals("Most Forks"))
            {
                sort = "forks";
                order = "desc";
            } else if (sortString.equals("Fewest Forks"))
            {
                sort = "forks";
                order = "asc";
            }

            //The url gets changed and we request the api again and get the new array list of repositories
            url = "https://api.github.com/search/repositories?page=" + page + "&q=" + searchedRepositoryName + language + "&sort=" + sort + "&order=" + order;

            getRepositories(searchedRepositoryName, language, new RepositoryCallback()
            {
                @Override
                public void onFailure(Exception e)
                {
                }

                @Override
                public void onResponse(ArrayList<Repository> arrayListRepository)
                {

                }
            });
    }

    //This function gets the next page of repositories when the more button is pressed
    public void getNextPage(final RepositoryCallback callback)
    {
        url = "https://api.github.com/search/repositories?page=" + (++page) + "&q=" + searchedRepositoryName + language + "&sort=" + sort + "&order=" + order;

        getRepositories(searchedRepositoryName, language, new RepositoryCallback()
        {
            @Override
            public void onFailure(Exception e)
            {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(ArrayList<Repository> arrayListRepository)
            {
                callback.onResponse(arrayListRepository);
            }
        });
    }

    //Parses the time so it displays in a MM/DD/YYYY manner and deletes any quotation marks
    private String parseTime(String lastUpdated)
    {
        Log.d("Date:", lastUpdated);
        String newTime = "Updated On " + lastUpdated.substring(6, 8) + "/" + lastUpdated.substring(9,11)
                + "/" + lastUpdated.substring(1,5);

        return newTime;
    }

    //Deletes the quotation marks that are in the String which was retrieved from the Json file
    private String parseLanguageAndDescription(String oldString)
    {
        String newString = oldString.substring(1, oldString.length()-1);

        return newString;
    }


    //Getter and Setter for the ArrayList
    public ArrayList<Repository> getRepositoryArrayList()
    {
        return repositoryArrayList;
    }
    public void setRepositoryArrayList(ArrayList<Repository> repositoryArrayList)
    {
        this.repositoryArrayList = repositoryArrayList;
    }

    //Getter and setter for the recycler view adapter
    public RecyclerViewAdapter getAdapter()
    {
        return adapter;
    }
    public void setAdapter(RecyclerViewAdapter adapter)
    {
        this.adapter = adapter;
    }

    //Setter for first time boolean variable
    public void setFirstTime(boolean firstTime)
    {
        this.firstTime = firstTime;
    }

    //Setters for the sort and order variables
    public void setSort(String sort)
    {
        this.sort = sort;
    }
    public void setOrder(String order)
    {
        this.order = order;
    }

    //Setter for the page variable
    public void setPage(int page)
    {
        this.page = page;
    }
}



interface JSONElementCallback
{
    void onFailure(Call call, IOException e);

    void onResponse(Call call, JsonElement element);
}

interface RepositoryCallback
{
    void onFailure(Exception e);

    void onResponse(ArrayList<Repository> arrayListRepository);
}
