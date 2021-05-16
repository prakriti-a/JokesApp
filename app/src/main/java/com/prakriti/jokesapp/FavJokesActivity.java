package com.prakriti.jokesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.prakriti.jokesapp.fragments.FavJokesFragment;

public class FavJokesActivity extends AppCompatActivity {
// hosts the fragment that will display the fav jokes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav_jokes);

        FavJokesFragment favJokesFragment = FavJokesFragment.newInstance();
        // replace container with fragment, id given to container in activity_fav_jokes.xml
        getSupportFragmentManager().beginTransaction().replace(R.id.fav_jokes_container, favJokesFragment).commit();
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//    }

}