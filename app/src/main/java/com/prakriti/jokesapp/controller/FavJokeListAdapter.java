package com.prakriti.jokesapp.controller;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prakriti.jokesapp.R;
import com.prakriti.jokesapp.model.Joke;
import com.prakriti.jokesapp.view.FavJokeViewHolder;

import java.util.List;

public class FavJokeListAdapter extends RecyclerView.Adapter<FavJokeViewHolder> {

    private List<Joke> jokeList;
    private Context context;

    public FavJokeListAdapter(List jokeList, Context context) {
        this.jokeList = jokeList;
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    @NonNull
    @Override
    public FavJokeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // called for creation of viewholder
        // pass the layout created as view holder for Fav Joke List
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_joke_item, parent, false);
            // inflate() returns a view

        return new FavJokeViewHolder(view); // FavJokeViewHolder constructor
    }

    @Override
    public void onBindViewHolder(@NonNull FavJokeViewHolder holder, int position) {
        // executed for each item visible in list
        String jokeText = jokeList.get(position).getJokeText();
            // get the textview which displays the text in viewholder
        holder.getTxtFavJoke().setText(jokeText);

            // set listener for share button
        holder.getImgShareButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create ACTION_SEND Intent
                Intent intent = new Intent(Intent.ACTION_SEND);
                // the content you want to share
                // jokeText is text of each item in list above
                String shareBody = jokeText;
                // type of content
                intent.setType("text/plain");

                // applying information subject & body
                intent.putExtra(Intent.EXTRA_SUBJECT, "Random Jokes!");
                intent.putExtra(Intent.EXTRA_TEXT, shareBody);

                // context variable
                context.startActivity(Intent.createChooser(intent, "Share Via"));
            }
        });
    }

    @Override
    public int getItemCount() {
        return jokeList.size();
    }
}
