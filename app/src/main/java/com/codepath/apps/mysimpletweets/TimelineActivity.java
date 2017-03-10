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
import android.view.View;
import android.view.ViewGroup;

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
    private MentionsTimelineFragment mentionsTimelineFragment;
    private TwitterClient client;
    private SmartFragmentStatePagerAdapter adapterViewPager;

    private String myName;
    private String myScreenName;
    private String myPhotoUrl;
    private Integer page;

    // REQUEST_CODE can be any value we like, used to determine the result type later
    private final int REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        //get the view pager
        ViewPager vpPager = (ViewPager) findViewById(R.id.viewpager);
        //set the view pager adapter for the pager
        adapterViewPager = new TweetsPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        //Find the pager sliding tabs
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        //attach the pager tabs to the viewpager
        tabStrip.setViewPager(vpPager);

//        homeTimelineFragment = (HomeTimelineFragment) adapterViewPager.getRegisteredFragment(0);
//        adapterViewPager.getRegisteredFragment(vpPager.getCurrentItem());
        client = TwitterApplication.getRestClient(); //singleton client
        getMyInfo();

        // Attach the page change listener to tab strip and **not** the view pager inside the activity
        tabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
                page = position;
//                Toast.makeText(TimelineActivity.this,
//                        "Selected page position: " + page, Toast.LENGTH_SHORT).show();
            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });

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


    public void onOthersProfile(View view) {
        //launch the profile view
        Intent i = new Intent(this,ProfileActivity.class);
        Integer position = (Integer) view.getTag();
        if (page == null || page == 0 ){
            TweetsArrayAdapter aTweets = homeTimelineFragment.getaTweets();
            Tweet selectedTweet = aTweets.getItem(position);
            String userName = selectedTweet.getUser().getScreenName();
            i.putExtra("selectedTweet", selectedTweet);
            i.putExtra("screen_name", userName);
            startActivity(i);
        } else{
            TweetsArrayAdapter aTweets = mentionsTimelineFragment.getaTweets();
            Tweet selectedTweet = aTweets.getItem(position);
            String userName = selectedTweet.getUser().getScreenName();
            i.putExtra("selectedTweet", selectedTweet);
            i.putExtra("screen_name", userName);
            startActivity(i);
        }


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

    // Extend from SmartFragmentStatePagerAdapter now instead for more dynamic ViewPager items
    public class TweetsPagerAdapter extends SmartFragmentStatePagerAdapter {
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

        // Here we can finally safely save a reference to the created
        // Fragment, no matter where it came from (either getItem() or
        // FragmentManger). Simply save the returned Fragment from
        // super.instantiateItem() into an appropriate reference depending
        // on the ViewPager position.
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
            // save the appropriate reference depending on position
            switch (position) {
                case 0:
                    homeTimelineFragment = (HomeTimelineFragment) createdFragment;
                    break;
                case 1:
                    mentionsTimelineFragment = (MentionsTimelineFragment) createdFragment;
                    break;
            }
            return createdFragment;
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
