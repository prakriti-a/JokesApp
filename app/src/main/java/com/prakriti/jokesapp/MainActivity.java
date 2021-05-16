package com.prakriti.jokesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.arasthel.asyncjob.AsyncJob;
import com.prakriti.jokesapp.controller.CardsDataAdapter;
import com.prakriti.jokesapp.controller.JokeLikeListener;
import com.prakriti.jokesapp.model.Joke;
import com.prakriti.jokesapp.model.JokeManager;
import com.wenchao.cardstack.CardStack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CardStack.CardEventListener, JokeLikeListener {

    CardStack cardStack; // added external library
    CardsDataAdapter cardsDataAdapter;
    private List<Joke> allJokes = new ArrayList<>();
    private JokeManager jokeManager;

    // vars for Shake Detection -> to shuffle jokes
    // better tested on real device
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private ShakeDetector shakeDetector; // online external class added to package
        // just needed to detect the shake -> listener
    // set sensor listener -> override onResume() & onPause()

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    //    Log.i("JOKES", loadJSONFromAsset());

        jokeManager = new JokeManager(this);

        cardStack = findViewById(R.id.container);
        cardStack.setContentResource(R.layout.joke_card);
        cardStack.setStackMargin(20);

        cardsDataAdapter = new CardsDataAdapter(this,0);

        // initialising shake detection vars
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        shakeDetector = new ShakeDetector();
        shakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake(int count) {
                handleShakeEvent();
            }
        });


        // perform heavy ops on background thread then update Main thread -> using external AsyncJob library
        // better UX
        new AsyncJob.AsyncJobBuilder<Boolean>()
                .doInBackground(new AsyncJob.AsyncAction<Boolean>() {
                    @Override
                    public Boolean doAsync() { // DO NOT put codes updating UI in here
                        // Do background tasks here
                        // the foll lines are background tasks, not UI based tasks
                        // getting json object from loadJSONFromAsset()
                        try {
                            // JSONObject / JSONArray constructor converts the returned String value into JSON Object
                            JSONArray fullJokeArray = new JSONArray(loadJSONFromAsset());
                            //    Log.i("JOKES", fullJokeArray + "");
                            addJokesToArrayList(fullJokeArray, allJokes);
                            // repeat for all arrays (within JSON Object) in case of multiple array using their keys
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                })
                .doWhenFinished(new AsyncJob.AsyncResultAction<Boolean>() {
                    @Override
                    public void onResult(Boolean result) { // called to update UI with result after background task is done
                        // this loop updates UI by adding jokes to adapter (controller), i.e recycler view (UI)
                        for(Joke joke:allJokes) {
                            //    Log.i("JOKES_LIST", joke.getJokeText());
                            cardsDataAdapter.add(joke.getJokeText());
                        }
                            // updates UI
                        cardStack.setAdapter(cardsDataAdapter);
                    }
                }).create().start();

        cardStack.setListener(this); // CardEventListener
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = this.getAssets().open("jokes.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void addJokesToArrayList(JSONArray jsonArray, List<Joke> arrayList) {
        try {
            if(jsonArray != null) {
                for (int i=0; i<jsonArray.length(); i++) {
                    JSONObject eachJoke = jsonArray.getJSONObject(i);
                    if(eachJoke.getString("mature")=="false") {
                        String fullJokeText = eachJoke.getString("part1") + "\n" + eachJoke.getString("part2");
                        arrayList.add(new Joke(fullJokeText, false));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean swipeEnd(int section, float distance) {
        // when swiping is done
        return (distance>300)? true : false;
    }
    @Override
    public boolean swipeStart(int section, float distance) {
        return true;
    }
    @Override
    public boolean swipeContinue(int section, float distanceX, float distanceY) {
        return true;
    }
    @Override
    public void discarded(int mIndex, int direction) { // when card is discarded
        }
    @Override
    public void topCardTapped() { // tapping card
        }


    @Override
    public void jokeIsLiked(Joke joke) { // joke is liked or disliked
        // saved to SharedPreferences
        if(joke.isJokeLiked()) {
            jokeManager.saveJoke(joke);
        }
        else {
            jokeManager.removeJoke(joke);
        }
    }

    // Menu options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // single item in menu, create intent
        startActivity(new Intent(this, FavJokesActivity.class));
        return super.onOptionsItemSelected(item);
    }


    private void handleShakeEvent() { // shuffle for random joke & update UI
        // once device has been shook
        // getting a random joke is heavy so we do it on the background thread

        new AsyncJob.AsyncJobBuilder<Boolean>()
                .doInBackground(new AsyncJob.AsyncAction<Boolean>() {
                    @Override
                    public Boolean doAsync() { // DO NOT put codes updating UI in here
                        // Do background tasks here
                        Collections.shuffle(allJokes);
                        return true;
                    }
                })
                .doWhenFinished(new AsyncJob.AsyncResultAction<Boolean>() {
                    @Override
                    public void onResult(Boolean result) { // called to update UI with result after background task is done
                        cardsDataAdapter.clear();
                        cardsDataAdapter = new CardsDataAdapter(MainActivity.this, 0); // anon inner class

                        for(Joke joke : allJokes) {
                            cardsDataAdapter.add(joke.getJokeText());
                        }
                        cardStack.setAdapter(cardsDataAdapter);
                    }
                }).create().start();
        // also set listener for the sensor
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register listener
        sensorManager.registerListener(shakeDetector, accelerometer, SensorManager.SENSOR_DELAY_UI);
            // pass listener obj, sensor obj, sampling period (UI is updated based on ? -> sensor delay)
    }

    @Override
    protected void onPause() {
        super.onPause();
        // unregister listener
        sensorManager.unregisterListener(shakeDetector);
            // only pass listener
    }
}