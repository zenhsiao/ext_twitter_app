package com.codepath.apps.mysimpletweets;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.mysimpletweets.fragments.HomeTimelineFragment;
import com.codepath.apps.mysimpletweets.fragments.MentionsTimelineFragment;
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
    private SmartFragmentStatePagerAdapter adapterViewPager;

    private String myName;
    private String myScreenName;
    private String myPhotoUrl;

    // REQUEST_CODE can be any value we like, used to determine the result type later
    private final int REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        //get the view pager
        ViewPager vpPager = (ViewPager) findViewById(R.id.viewpager);
        //set the view pager adapter for the pager
        vpPager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager()));
        //Find the pager sliding tabs
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        //attach the pager tabs to the viewpager
        tabStrip.setViewPager(vpPager);

        client = TwitterApplication.getRestClient(); //singleton client
        getMyInfo();




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


    public void onProfileView(MenuItem mi){
        //launch the profile view
        Intent i = new Intent(this,ProfileActivity.class);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Load the fragment
            homeTimelineFragment = (HomeTimelineFragment) adapterViewPager.getRegisteredFragment(0);

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

    // Extend from SmartFragmentStatePagerAdapter now instead for more dynamic ViewPager items
    public static class TweetsPagerAdapter extends SmartFragmentStatePagerAdapter{
        private String tabTitles[] = {"Home","Mentions"};

        //adapter gets the manager insert or remove fragment from activity
        public TweetsPagerAdapter(FragmentManager fm){
            super(fm);
        }

        //the order and creation of fragments within the pager
        @Override
        public Fragment getItem(int position) {
            if(position == 0){
                return new HomeTimelineFragment();
            } else if(position == 1){
                return new MentionsTimelineFragment();
            } else {
                return  null;
            }
        }

        //return the tab title
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        //how many fragments there are to swipe between?
        @Override
        public int getCount() {
            return tabTitles.length;
        }
    }



}
