package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.models.Tweet;
import com.squareup.picasso.Picasso;

import java.util.List;

//Taking the Tweets object and turn them into Views and displayed in the list
public class TweetsArrayAdapter extends ArrayAdapter<Tweet>{

    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, 0,tweets);
    }

    //Override and set up custom template

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //1.Get the tweet
        Tweet tweet = getItem(position);
        //2.Find or inflate the template
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet,parent,false);
        }
        //3.Find the subviews to fill with data in the template
        ImageView ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
        TextView tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
        TextView tvScreenName = (TextView) convertView.findViewById(R.id.tvScreenName);
        TextView tvBody = (TextView) convertView.findViewById(R.id.tvBody);
        TextView tvTime = (TextView) convertView.findViewById(R.id.tvTime);
        //4.Populate data into subviews
        tvUserName.setText(tweet.getUser().getName());
        tvScreenName.setText("@"+tweet.getUser().getScreenName());
        tvBody.setText(tweet.getBody());
        tvTime.setText(tweet.getCreatedAt());
        ivProfileImage.setImageResource(0);//clear out the oldimage for a recycled view
        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(ivProfileImage);
        ivProfileImage.setTag(position);
        //5.Return the view to be inserted into the list
        return convertView;
    }


}
