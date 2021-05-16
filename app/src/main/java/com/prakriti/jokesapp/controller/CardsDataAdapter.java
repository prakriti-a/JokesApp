package com.prakriti.jokesapp.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.prakriti.jokesapp.R;
import com.prakriti.jokesapp.model.Joke;

public class CardsDataAdapter extends ArrayAdapter<String> {

    private Context context;
    private boolean clicked = true;

    private JokeLikeListener jokeLikeListener; // reference to interface implemented by MainActivity
    private Joke joke;

    // debug errors reg Like button
    private SharedPreferences sharedPreferences;


    public CardsDataAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.context = context;
        jokeLikeListener = (JokeLikeListener) context; // listener for interface
            // cast because we know for sure the listener implements this interface
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public View getView(int position, final View contentView, ViewGroup parent){
        // supply the layout for your card
        // called for each cardview visible on screen
        // reference buttons & listeners here
        TextView v = (contentView.findViewById(R.id.content));
        v.setText(getItem(position)); // returns text of joke

        ImageButton likeButton = contentView.findViewById(R.id.likeButton);
        ImageButton shareButton = contentView.findViewById(R.id.shareButton);

        // if joke is already liked, like button should reflect it
        if(sharedPreferences.contains(getItem(position))) {
            likeButton.setImageResource(R.drawable.like_filled);
            clicked = false;
        }
        else {
            likeButton.setImageResource(R.drawable.like_empty);
            clicked = true;
        }

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clicked) {
                    likeButton.setImageResource(R.drawable.like_filled);
                    clicked = false;

                    // animation for like button
                    YoYo.with(Techniques.Bounce)
                            .duration(500) // in ms
                           // .repeat(5)
                            .playOn(likeButton); // pass the view

                    Toast.makeText(context, "Joke added to Favourites!", Toast.LENGTH_SHORT).show();
                        // create new joke, get text from textview, and set true for isLiked value
                    joke = new Joke(getItem(position), true);
                        // pass joke to MainActivity via listener reference
                    jokeLikeListener.jokeIsLiked(joke);
                }
                else {
                    likeButton.setImageResource(R.drawable.like_empty);
                    clicked = true;

                    YoYo.with(Techniques.Bounce).duration(500).playOn(likeButton);

                    Toast.makeText(context, "Joke removed from Favourites", Toast.LENGTH_SHORT).show();
                    joke = new Joke(getItem(position), false);
                    jokeLikeListener.jokeIsLiked(joke);
                }
            }
        });

            // to share jokes via other apps -> tested better on real device
            // also add listener for share button in fav list fragment
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // create ACTION_SEND Intent
                Intent intent = new Intent(Intent.ACTION_SEND);
                // the content you want to share
                    // 'v' refers to TextView created above
                String shareBody = v.getText().toString();
                // type of content
                intent.setType("text/plain");

                // applying information subject & body
                intent.putExtra(Intent.EXTRA_SUBJECT, "Random Jokes!");
                intent.putExtra(Intent.EXTRA_TEXT, shareBody);

                v.getContext().startActivity(Intent.createChooser(intent, "Share Via"));
            }
        });
        return contentView;
    }
}