package com.codepath.apps.mysimpletweets;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.fragments.UserTimelineFragment;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static com.codepath.apps.mysimpletweets.R.id.ivMyPhoto;
import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

public class ProfileActivity extends AppCompatActivity {
    TwitterClient client;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        client = TwitterApplication.getRestClient();
        //Get the account info
        client.getMyTwitterInfo(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                user = User.fromJSON(response);
                //My current user account's info
                getSupportActionBar().setTitle("@" + user.getScreenName());
                populateProfileHeader(user);
            }
        });
        //Get screen_name from the activity that launch this
        String screenName = getIntent().getStringExtra("screen_name");
        //Create the user timeline fragment
        UserTimelineFragment fragmentUserTimeline = UserTimelineFragment.newInstance(screenName);
        //Display user fragment within the activity(dynamically)
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flContainer,fragmentUserTimeline);
        ft.commit();//changes the fragments
    }

    private void populateProfileHeader(User user) {

        TextView myScreenName = (TextView) findViewById(R.id.tvMyScreenName);
        TextView tvTagline = (TextView) findViewById(R.id.tvTagline);
        TextView tvFollower = (TextView) findViewById(R.id.tvFollowers);
        TextView tvFollowing = (TextView) findViewById(R.id.tvFollowing);
        ImageView myPhoto = (ImageView) findViewById(ivMyPhoto);

        myScreenName.setText(user.getName());
        tvTagline.setText( user.getTagline());
        tvFollower.setText( user.getFollowersCount()+" followers");
        tvFollowing.setText( user.getFollowingsCount()+" followings");
        Picasso.with(getContext()).load(user.getProfileImageUrl()).into(myPhoto);
    }


}
