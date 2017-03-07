package com.codepath.apps.mysimpletweets;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static com.codepath.apps.mysimpletweets.R.id.ivMyPhoto;
import static com.raizlabs.android.dbflow.config.FlowManager.getContext;

public class ComposeActivity extends AppCompatActivity {

    public EditText etTweetContent;
    private TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        etTweetContent = (EditText) findViewById(R.id.etTweetContent);
        ImageView myPhoto = (ImageView) findViewById(ivMyPhoto);
        TextView myName = (TextView) findViewById(R.id.tvMyName);
        TextView myScreenName = (TextView) findViewById(R.id.tvMyScreenName);
        final TextView tvRemainingWord = (TextView) findViewById(R.id.tvRemainingWord);

        myName.setText( getIntent().getStringExtra("myName"));
        myScreenName.setText( "@"+ getIntent().getStringExtra("myScreenName"));
        String url = getIntent().getStringExtra("myPhotoUrl");
        Picasso.with(getContext()).load(url).into(myPhoto);



        etTweetContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Fires right as the text is being changed (even supplies the range of text)
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // Fires right before text is changing
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Fires right after the text has changed
                int i = 140 - s.length();

                tvRemainingWord.setText(Integer.toString(i));
            }
        });

    }




    public void onPostTwitterAction(View v) {
//        // Prepare data intent
//        Intent data = new Intent();
//        // Pass relevant data back as a result
//        data.putExtra("text", etTweetContent.getText().toString());
//        data.putExtra("code", 200); // ints work too
//        // Activity finished ok, return the data
//        setResult(RESULT_OK, data); // set result code and bundle data for response
//        finish(); // closes the activity, pass data to parent

        client = TwitterApplication.getRestClient(); //singleton client

        client.postTwitter(etTweetContent.getText().toString(), new JsonHttpResponseHandler(){
            //success
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                Log.d("DEBUG", json.toString());

                Intent data = new Intent();

                Tweet newTweet = Tweet.fromJSON(json);

                data.putExtra("newTweet", newTweet);
//                Toast.makeText(getApplicationContext(),"success tweet"+newTweet.getBody(),Toast.LENGTH_LONG).show();
                data.putExtra("code", 200); // ints work too
                // Activity finished ok, return the data
                setResult(RESULT_OK, data); // set result code and bundle data for response
                ComposeActivity.this.finish();
            }

            //Failure
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG",errorResponse.toString());
                ComposeActivity.this.finish();
            }
        });

    }

    public void onCancelAction(View v) {
        this.finish();
    }


}
