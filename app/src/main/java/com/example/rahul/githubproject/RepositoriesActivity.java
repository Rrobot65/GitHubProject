package com.example.rahul.githubproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class RepositoriesActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{
    private RecyclerView recyclerView;
    private Manager manager;
    private Spinner sortSpinner;
    RecyclerViewAdapter adapter;
    boolean isSpinnerInitial;

    @Override
    public void onResume()
    {
        super.onResume();
        isSpinnerInitial = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repositories);

        manager = Manager.getInstance();
        isSpinnerInitial = false;
        recyclerView = findViewById(R.id.recyclerView);
        sortSpinner = findViewById(R.id.sortSpinner);

        //Setting up the sort spinner
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.sorts, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(spinnerAdapter);
        sortSpinner.setOnItemSelectedListener(this);



        //These couple lines set up the recycler view using the RecyclerViewAdapter class
        adapter = new RecyclerViewAdapter(manager.getRepositoryArrayList());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
        manager.setAdapter(adapter);


        //Setting up the on touch listener for each item in the recycler view
        recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getApplicationContext(), recyclerView, new RecyclerViewTouchListener.ClickListener()
        {
            @Override
            public void onClick(View view, int position)
            {
                Repository repository = manager.getRepositoryArrayList().get(position);

                //Each repository in the recycler view goes to its github website
                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(repository.getUrl()));
                startActivity(webIntent);
            }

            @Override
            public void onLongClick(View view, int position)
            {

            }
        }));


        adapter.notifyDataSetChanged();
    }

    /**This function checks which item you've selected in the sort drop down menu. And changes the
     * and changes the recycler view.
    */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        //It checks this boolean so that the drop down menu doesn't get selected when the activity starts
        if(isSpinnerInitial)
        {
            String sortString = parent.getItemAtPosition(position).toString();
            manager.updateSortParameter(sortString);
            recyclerView.post(new Runnable()
            {
                @Override
                public void run()
                {
                    adapter.notifyDataSetChanged();
                    adapter.notifyDataSetChanged();
                }
            });
        }
        else
        {
            isSpinnerInitial = true;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {
    }

    //Gets the next page and adds all the new repositories to the array list.
    public void moreButton(final View view)
    {
        manager.getNextPage(new RepositoryCallback()
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
            public void onResponse(ArrayList<Repository> arrayListRepository)
            {
            }
        });
        adapter.notifyDataSetChanged();
    }
}
