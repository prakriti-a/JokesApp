package com.prakriti.jokesapp.model;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JokeManager {

    private Context context;
    SharedPreferences sharedPreferences;

    public JokeManager(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void saveJoke(Joke joke) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(joke.getJokeText(), joke.isJokeLiked()); // key is String
        editor.apply();
    }

    public void removeJoke(Joke joke) {
        if(sharedPreferences.contains(joke.getJokeText())) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(joke.getJokeText()).commit();
        }
    }

    public List<Joke> retrieveJokes() {
        Map<String, ?> data = sharedPreferences.getAll();
        List<Joke> jokes = new ArrayList<>();
            // data contains entry sets, entrySet() returns Set of Entry objects
        for(Map.Entry<String,?> entry : data.entrySet()) {
            Joke joke = new Joke(entry.getKey(), (Boolean) entry.getValue()); // cast to Boolean class (not type)

                // this value appears in fav list (UI bug?) -> skip adding this value to the List of Joke objects
            if(entry.getKey().matches("variations_seed_native_stored")) {
                continue;
            }
            jokes.add(joke);
        }
        return jokes;
    }

}
