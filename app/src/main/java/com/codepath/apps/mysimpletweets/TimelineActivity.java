package com.codepath.apps.mysimpletweets;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private TwitterClient client;
    private ArrayList<Tweet> tweets;
    private TweetsArrayAdapter aTweets;
    private ListView lvTweets;
    private String myName;
    private String myScreenName;
    private String myPhotoUrl;

    // REQUEST_CODE can be any value we like, used to determine the result type later
    private final int REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        //Find the listview
        lvTweets = (ListView) findViewById(R.id.lvTweets);
        //Create the arraylist(data source)
        tweets = new ArrayList<>();
        //Construct adapter to listview
        aTweets = new TweetsArrayAdapter(this,tweets);
        //connect adapter to listview
        lvTweets.setAdapter(aTweets);
        //Get the client
        client = TwitterApplication.getRestClient(); //singleton client
        populateTimeline(0);
        getMyInfo();

        // Attach the listener to the AdapterView onCreate
        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                loadNextDataFromApi(page);
                // or loadNextDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.timeline, menu);
        return true;
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int offset) {
        populateTimeline(offset);
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyDataSetChanged()`
    }

    //send an API request to get the timeline json
    //Fill the listview by creating the tweet objects from the json
    private void populateTimeline( int page) {
        client.getHomeTimeline(Tweet.getMaxId(),page, new JsonHttpResponseHandler(){
            //success
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                Log.d("DEBUG", json.toString());
                //Deserialize JSON
                //CREATE MODELS AND ADD THEM INTO ADAPTER
                //Load the model into listview
                aTweets.addAll(Tweet.fromJSONArray(json));
            }

            //Failure
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG",errorResponse.toString());
            }
        });
    }

    public void onComposeAction(MenuItem mi) {
        // handle click here
//      Toast.makeText(getApplicationContext(),"compose",Toast.LENGTH_LONG).show();
        Intent i = new Intent(TimelineActivity.this,ComposeActivity.class);
        i.putExtra("myName", myName);
        i.putExtra("myScreenName", myScreenName);
        i.putExtra("myPhotoUrl", myPhotoUrl);
        startActivityForResult(i, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {

            Tweet newTweet =(Tweet) data.getSerializableExtra("newTweet");
            aTweets.insert(newTweet,0);
            aTweets.notifyDataSetChanged();
//            // Extract name value from result extras
//            String text = data.getExtras().getString("text");
//
//            client = TwitterApplication.getRestClient(); //singleton client
//
//
//            client.postTwitter(text, new JsonHttpResponseHandler(){
//                //success
//                @Override
//                public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
//                    Log.d("DEBUG", json.toString());
//
//                    Tweet newTweet = new Tweet();
//                    newTweet = Tweet.fromJSON(json);
//                    aTweets.insert(newTweet,0);
//                    aTweets.notifyDataSetChanged();
//
//                Toast.makeText(getApplicationContext(),"success tweet: "+newTweet.getBody(),Toast.LENGTH_LONG).show();
//                }
//
//                //Failure
//                @Override
//                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                    Log.d("DEBUG",errorResponse.toString());
//                }
//            });
        }
    }

    public void getMyInfo(){
        client.getMyTwitterInfo(new JsonHttpResponseHandler(){
            //success
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                Log.d("DEBUG", json.toString());
                try {
                    myName = json.getString("name");
                    myScreenName = json.getString("screen_name");
                    myPhotoUrl = json.getString("profile_image_url");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //Failure
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG",errorResponse.toString());
            }
        });
    }
}
