package com.example.rahul.githubproject;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by rahul on 5/15/2018.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>
{
    private ArrayList<Repository> repositoryArrayList;

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView nameTextView, descriptionTextView, languageTextView, updatedDateTextView, starGazersTextView;

        public MyViewHolder(View view)
        {
            super(view);
            nameTextView = (TextView) view.findViewById(R.id.nameTextView);
            descriptionTextView = (TextView) view.findViewById(R.id.descriptionTextView);
            languageTextView = (TextView) view.findViewById(R.id.languageTextView);
            updatedDateTextView = (TextView) view.findViewById(R.id.updatedDateTextView);
            starGazersTextView = (TextView) view.findViewById(R.id.starGazersTextView);
        }
    }

    public RecyclerViewAdapter(ArrayList<Repository> repositoryArrayList)
    {
        this.repositoryArrayList = repositoryArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_row, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {
        Repository repository = repositoryArrayList.get(position);

        holder.nameTextView.setText(repository.getName());
        holder.descriptionTextView.setText(repository.getDescription());
        holder.languageTextView.setText(repository.getLanguage());
        holder.updatedDateTextView.setText(repository.getUpdatedDate());
        holder.starGazersTextView.setText(repository.getStarGazers());

    }

    @Override
    public int getItemCount()
    {
        return repositoryArrayList.size();
    }
}
