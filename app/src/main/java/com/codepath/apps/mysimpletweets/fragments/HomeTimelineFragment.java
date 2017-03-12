package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class HomeTimelineFragment extends TweetsListFragment{

    private TwitterClient client;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get the client
        client = TwitterApplication.getRestClient(); //singleton client
        populateTimeline(0,0);


    }



    //send an API request to get the timeline json
    //Fill the listview by creating the tweet objects from the json
    public void populateTimeline( long max_id, int page) {
        client.getHomeTimeline(max_id,page, new JsonHttpResponseHandler(){
            //success
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                Log.d("DEBUG", json.toString());
                //Deserialize JSON
                //CREATE MODELS AND ADD THEM INTO ADAPTER
                //Load the model into listview
                addAll(Tweet.fromJSONArray(json));
            }

            //Failure
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG",errorResponse.toString());
            }
        });
    }

}
