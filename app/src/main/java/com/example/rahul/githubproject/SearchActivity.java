package com.example.rahul.githubproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{
    //Variables
    private Spinner spinner;
    private Manager manager;
    private String languageString;
    private EditText repositoryNameEditText;

    @Override
    public void onResume()
    {
        super.onResume();
        manager.setFirstTime(true);
        manager.setPage(1);
        manager.setRepositoryArrayList(new ArrayList<Repository>());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        manager = Manager.getInstance();
        repositoryNameEditText = findViewById(R.id.repositoryNameEditText);


        //region Setting up Spinner
        spinner = findViewById(R.id.languageSpinner);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.languages, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);
        //endregion





    }

    //This spinner method gets the item selected in the language drop down menu
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        languageString = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {

    }

    //When search button is pressed it gets the first page of repositories and goes to the next activity
    public void searchButton(final View view)
    {
        manager.getRepositories(repositoryNameEditText.getText().toString(), languageString, new RepositoryCallback()
        {
            @Override
            public void onFailure(Exception e)
            {
                view.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(getApplicationContext(), "API rate limit exceeded", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(ArrayList<Repository> repositoryArrayList)
            {
                startActivity(new Intent(SearchActivity.this, RepositoriesActivity.class));
            }
        });


    }




}
