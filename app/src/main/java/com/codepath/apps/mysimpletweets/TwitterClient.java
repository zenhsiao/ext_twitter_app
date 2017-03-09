package com.codepath.apps.mysimpletweets;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class; // Change this
	public static final String REST_URL = "https://api.twitter.com/1.1/"; // Change this, base API URL
	public static final String REST_CALLBACK_URL = "oauth://cpsimpletweets"; // Change this (here and in manifest)

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, context.getString(R.string.consumer_key), context.getString(R.string.consumer_secret), REST_CALLBACK_URL);
	}

	//HomeTimeline - Gets us the home timeline
//	public void getHomeTimeline(AsyncHttpResponseHandler handler){
//		String apiUrl = getApiUrl("statuses/home_timeline.json");
//		//specify the params
//		RequestParams params = new RequestParams();
//		params.put("count",25);
//		params.put("since_id",1);
//		//Execute the request
//		getClient().get(apiUrl,params,handler);
//	}

	//HomeTimeline - Gets us the home timeline
	public void getHomeTimeline(long max_id, int page, AsyncHttpResponseHandler handler){
		String apiUrl = getApiUrl("statuses/home_timeline.json");
		//specify the params
		RequestParams params = new RequestParams();
		params.put("count",20);
		params.put("since_id",1);
		if (page >0){
			params.put("max_id", max_id);
		}
		//Execute the request
		getClient().get(apiUrl,params,handler);
	}

	//get own infomation
	public void getMyTwitterInfo(AsyncHttpResponseHandler handler){
		String apiUrl = getApiUrl("account/verify_credentials.json");

		//Execute the request
		getClient().get(apiUrl,handler);
	}

	//post twitter
	public void postTwitter(String tweetcontent, AsyncHttpResponseHandler handler){
		String apiUrl = getApiUrl("statuses/update.json");
		//specify the params
		RequestParams params = new RequestParams();
		params.put("status",tweetcontent);

		//Execute the request
		getClient().post(apiUrl,params,handler);
	}

	public void getMentionsTimeline(long maxId, int page, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/mentions_timeline.json");
		//specify the params
		RequestParams params = new RequestParams();
		params.put("count",20);
		if (page >0){
			params.put("max_id", maxId);
		}
		//Execute the request
		getClient().get(apiUrl,params,handler);
	}

	public void getUserTimeline(String screenName, long maxId, int page, JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/user_timeline.json");
		//specify the params
		RequestParams params = new RequestParams();
		params.put("count",20);
		params.put("screen_name",screenName);
		if (page >0){
			params.put("max_id", maxId);
		}
		//Execute the request
		getClient().get(apiUrl,params,handler);
	}

	/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
	 * 	  i.e getApiUrl("statuses/home_timeline.json");
	 * 2. Define the parameters to pass to the request (query or body)
	 *    i.e RequestParams params = new RequestParams("foo", "bar");
	 * 3. Define the request method and make a call to the client
	 *    i.e client.get(apiUrl, params, handler);
	 *    i.e client.post(apiUrl, params, handler);
	 */
}
