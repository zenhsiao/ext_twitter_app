package com.codepath.apps.mysimpletweets;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.codepath.apps.mysimpletweets.fragments.HomeTimelineFragment;
import com.codepath.apps.mysimpletweets.fragments.TweetsListFragment;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private TweetsListFragment fragmentTweetsList;
    private HomeTimelineFragment homeTimelineFragment;
    private TwitterClient client;

    private String myName;
    private String myScreenName;
    private String myPhotoUrl;

    // REQUEST_CODE can be any value we like, used to determine the result type later
    private final int REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client = TwitterApplication.getRestClient(); //singleton client
        getMyInfo();

        // Load the fragment
        homeTimelineFragment = (HomeTimelineFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_timeline);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.timeline, menu);
        return true;
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
            homeTimelineFragment.insertTweet(newTweet,0);
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
