//	MessageController.java - handles sending and receiving client messages 
//
//	Source code for the android client at: https://github.com/usergrid/usergrid-android-client
//
//	information on authentication and authorization at: http://usergrid.github.com/docs/build/html/auth.html#applications
//
//	Example of using usergrid to create an application at: http://usergrid.github.com/docs/build/html/running_samples.html
//
//  NOTE: you must have usergrid running on a server with an application created in order to run this sample.

package com.project.controller;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.springframework.http.HttpMethod;
import org.usergrid.android.client.Client;
import org.usergrid.android.client.entities.User;
import org.usergrid.android.client.response.ApiResponse;

import com.project.model.PostImages;
import com.project.model.Posts;

public class MessageController {

	// API url:
	// This is the url of the server where you have usergrid running.
	// You can see all usergrid activity from the usergrid
	// console: http://usergrid.github.com/console/?api_url=insert_your_api_url
	private String USERGRID_API_URL = "http://api.usergrid.com";

	// Application name:
	// This is the name you selected when you set up the usergrid application
	// It is reassigned when a new server url is entered while running the app.
	// It is grabbed as the
	// last segment of the API URL that the user enters.
	private String USERGRID_APP = "MessageeApp";

	// User variables set when you log in as a specific user
	private String email;
	private String username;
	private String imageURL;
	private Client client = null;

	// create posts and postImages to store message board posts
	private final Posts posts = new Posts();
	private final PostImages postImages = new PostImages();

	// flag to indicate client is gettings posts
	private boolean gettingPostsFlag = false;

	// This function creates of communication client with an api url
	// only one client is created and used by all the applications views to
	// communicate with usergrid.
	public MessageController() {

		client = new Client(USERGRID_APP).withApiUrl(USERGRID_API_URL);

	}

	// This function is used to login using a username and password
	// calling client.authorizeAppUser(username,password) authorizes the client
	// object
	// to perform certain user actions. This must be called before the app can
	// get/post messages
	// and add users to follow.
	public ApiResponse login(String usernameArg, String passwordArg) {

		ApiResponse response = null;

		// attempt to authorize user
		try {
			response = client.authorizeAppUser(usernameArg, passwordArg);
		} catch (Exception e) {
			response = null;
		}

		// if response shows success, store account info
		if ((response != null) && !"invalid_grant".equals(response.getError())) {

			User user = response.getUser();
			email = user.getEmail();
			username = user.getUsername();
			imageURL = user.getPicture();

		}

		// return login response
		return response;
	}

	// A number of requests can be made using the apiRequest function. It takes
	// the following arguments:
	//
	// 1) method
	// HttpMethod method
	// 2) params
	// parameters to encode as querystring or body parameters
	// 3) data
	// JSON data to put in body
	// 4-n) segments
	// REST url path segments (i.e. /segment1/segment2/segment3)
	//
	// apiRequest returns a message that contains transaction information such
	// as requested info, errors, or success notifications.
	//
	// getPostsFromClient uses apiRequest to grab the information from the
	// current user's feed.
	// The feed is then parsed to grab data for each post.
	public void getPostsFromClient() {

		// client call to get message board feed
		ApiResponse resp = null;
		try {
			resp = client.apiRequest(HttpMethod.GET, null, null, USERGRID_APP,
					"users", username, "feed");
		} catch (Exception e) {
			resp = null;
		}

		// if response has posts add them to the Posts and PostImages objects
		if (resp != null && resp.getFirstEntity() != null) {

			// clear all posts
			posts.clearAll();

			// add all new posts
			for (int i = resp.getEntityCount() - 1; i >= 0; i--) {

				// grab properties from post
				Map<String, JsonNode> properties = resp.getEntities().get(i)
						.getProperties();

				// get name string and url string
				String poster = "";
				String urlPic = null;

				JsonNode actor = properties.get("actor");

				if (actor != null) {

					JsonNode displayName = actor.get("displayName");
					JsonNode image = actor.get("image");

					if (displayName != null) {
						poster = displayName.getTextValue();
					}

					if (image != null) {
						JsonNode imageUrl = image.get("url");
						if (imageUrl != null) {
							urlPic = imageUrl.getTextValue();
						}
					}

					// support for a slightly different Json format
					else {
						JsonNode picture = actor.get("picture");
						if (picture != null) {
							urlPic = picture.getTextValue();
						}
					}
				}

				// get post
				String post = "";
				JsonNode content = properties.get("content");
				if (content != null) {
					post = content.getTextValue();
				}

				// add post to posts object
				posts.addPost(poster, post, urlPic);

			}
		}

	}

	// client call to add user to follow using apiRequest
	public ApiResponse addFollow(String followName) {

		// client call to add user to follow
		ApiResponse resp = null;
		try {
			resp = client.apiRequest(HttpMethod.POST, null, "{}", USERGRID_APP,
					"users", username, "following", "user", followName);

		} catch (Exception e) {
			resp = null;
		}

		// return client response
		return resp;
	}

	// client call to post new message
	// This function builds a map of data to be sent as an "activity", in this
	// case a post.
	// The post is added to the activities for the current user.
	public ApiResponse post(String postMess) {

		// post properties
		Map<String, Object> data = new HashMap<String, Object>();
		Map<String, Object> actor = new HashMap<String, Object>();
		Map<String, Object> image = new HashMap<String, Object>();

		// add image url, height, and width of image
		image.put("url", imageURL);
		image.put("height", 80);
		image.put("width", 80);

		// add username, image, and email
		actor.put("displayName", username);
		actor.put("image", image);
		actor.put("email", email);

		// add actor, set action to post, and add message
		data.put("actor", actor);
		data.put("verb", "post");
		data.put("content", postMess);

		// client call to post message
		ApiResponse resp = null;
		try {
			resp = client.apiRequest(HttpMethod.POST, null, data, USERGRID_APP,
					"users", username, "activities");
		} catch (Exception e) {
			resp = null;
		}

		// return client response
		return resp;
	}

	// client add account
	// apiRequest is used to send a map containing new account info.
	public ApiResponse addAccount(String username, String password, String email) {

		// attempt to create account
		Map<String, Object> data = new HashMap<String, Object>();

		// create form
		data.put("username", username);
		data.put("email", email);
		data.put("password", password);

		// client call to add account
		ApiResponse resp = null;

		// attempt to add account
		try {
			resp = client.apiRequest(HttpMethod.POST, null, data, USERGRID_APP,
					"users");
		} catch (Exception e) {
			resp = null;
		}

		// return client response
		return resp;

	}

	// return api url
	public String getAPIURL() {
		return USERGRID_API_URL;
	}

	// return app name
	public String getAppName() {
		return USERGRID_APP;
	}

	// return api url with app name
	public String getAPIURLWithApp() {
		return USERGRID_API_URL + "/" + USERGRID_APP;
	}

	// set api url
	public void setAPIURL(String newURL) {
		this.USERGRID_API_URL = newURL;
		client.setApiUrl(USERGRID_API_URL);
	}

	// set app name
	public void setAppName(String appName) {
		this.USERGRID_APP = appName;
		client.setApplicationId(appName);
	}

	// return posts object
	public Posts getPosts() {
		return posts;
	}

	// return postImage object
	public PostImages getPostImages() {
		return postImages;
	}

	// return reading posts flag
	public boolean getFlagReadingPosts() {
		return gettingPostsFlag;
	}

	// set reading posts flag
	public void setFlagReadingPosts(boolean arg) {
		gettingPostsFlag = arg;
	}

}
