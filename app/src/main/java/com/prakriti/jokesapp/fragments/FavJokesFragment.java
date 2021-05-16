package com.prakriti.jokesapp.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;
import com.prakriti.jokesapp.R;
import com.prakriti.jokesapp.controller.FavJokeListAdapter;
import com.prakriti.jokesapp.model.Joke;
import com.prakriti.jokesapp.model.JokeManager;

import java.util.ArrayList;
import java.util.List;

public class FavJokesFragment extends Fragment {
    // using recycler view
    // fragment_fav_jokes.xml references

    RecyclerView recyclerView;
    FavJokeListAdapter favJokeListAdapter;
    JokeManager jokeManager;
    private List<Joke> jokeList = new ArrayList<>();

    // swipe to delete jokes from Fav Jokes database
    private Joke deletedJoke; // restorable


    public FavJokesFragment() {
        // Required empty public constructor
    }

    public static FavJokesFragment newInstance() {
        return new FavJokesFragment();
    }

        // lifecycle method
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // called before onCreate and onCreateView
        jokeManager = new JokeManager(context);
            // to add updated list from SharedPreferences
        jokeList.clear();
        if(jokeManager.retrieveJokes().size() > 0) {
            for (Joke joke: jokeManager.retrieveJokes()) {
                jokeList.add(joke);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fav_jokes, container, false);

        if(view != null) {
                // 'view' refers to the specified layout
            recyclerView = view.findViewById(R.id.myRV);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            favJokeListAdapter = new FavJokeListAdapter(jokeList, getContext());
            recyclerView.setAdapter(favJokeListAdapter);

                // swipe to delete joke from Fav List feature
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback); // initialised below
            itemTouchHelper.attachToRecyclerView(recyclerView);
        }
        return view;
    }

    ItemTouchHelper.SimpleCallback simpleCallback =
            new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT)
    {   // pass drag direction & swipe direction (L or R)
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                              @NonNull RecyclerView.ViewHolder target) {
            // no work here
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            // position of each item in recycler view
            final int position = viewHolder.getAdapterPosition();

            switch (direction) {
                case ItemTouchHelper.LEFT:
                case ItemTouchHelper.RIGHT:

                    deletedJoke = jokeList.get(position);
                    jokeManager.removeJoke(deletedJoke);
                    jokeList.remove(position);      // remove from list of Fav Jokes

                    // update UI of recycler view by informing adapter bcoz it is responsible for populating recycler view
                    favJokeListAdapter.notifyItemRemoved(position); // pos to be updated
                    favJokeListAdapter.notifyDataSetChanged();

                    // undo action
                    Snackbar.make(recyclerView, "Joke removed from Favourites", Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() { // Click fn for UNDO action
                                @Override
                                public void onClick(View v) {
                                    // add joke back in all places it was removed from
                                    jokeList.add(position, deletedJoke);
                                    jokeManager.saveJoke(deletedJoke);
                                    favJokeListAdapter.notifyItemInserted(position);
                                }
                            }).show();
                    break;
            }
        }
    };

}