//MessageController.java - handles sending/receiving client messages

package com.project.controller;

import com.project.model.PostImages;
import com.project.model.Posts;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.springframework.http.HttpMethod;
import org.usergrid.android.client.Client;
import org.usergrid.android.client.entities.User;
import org.usergrid.android.client.response.ApiResponse;


public class MessageController {

	//API url
	private String USERGRID_API_URL = "http://apigee-test.usergrid.com";

	//Application name
	public static final String USERGRID_APP = "twid";


	//user info
	private String email;
	private String username;
	private String password;
	private String imageURL;
	private Client client = null;


	//create posts and postImages to store message board posts
	private Posts posts = new Posts();
	private PostImages postImages = new PostImages();

	//flag to indicate client is gettings posts
	private boolean gettingPostsFlag = false;


	//create client with api url
	public MessageController(){

		client = new Client(USERGRID_APP).withApiUrl(USERGRID_API_URL);

	}


	//function to call client login
	public ApiResponse login(String usernameArg, String passwordArg) {

		//store password
		this.password = passwordArg;


		ApiResponse response=null;

		//attempt to authorize user
		try {
			response = client.authorizeAppUser(usernameArg, password);
		} catch (Exception e) {
			response = null;
		}

		//if response shows success, store account info
		if ((response != null) && !"invalid_grant".equals(response.getError())) {

			User user = response.getUser();
			email = user.getEmail();
			username = user.getUsername();
			imageURL = user.getPicture();

		}

		//return login response
		return response;
	}


	//Grab posts using client
	public void getPostsFromClient(){

		//client call to get message board feed
		ApiResponse resp = client.apiRequest(HttpMethod.GET,null , null, USERGRID_APP, "users",username,"feed");


		//if response has posts add them to the Posts and PostImages objects
		if(resp.getFirstEntity()!=null){

			//clear all posts
			posts.clearAll();

			//add all new posts
			for(int i=resp.getEntityCount()-1; i>=0;i--){

				//grab properties from post
				Map<String, JsonNode> properties = resp.getEntities().get(i).getProperties();


				//get name string
				String poster ="Empty Username";

				if(properties.get("actor").get("displayName")!=null){
					poster = properties.get("actor").get("displayName").getTextValue();	
				}

				//get post 
				String post ="Empty Post";

				if(properties.get("content")!=null){
					post = properties.get("content").getTextValue();
				}

				//get image url
				String urlPic = properties.get("actor").get("image").get("url").getTextValue();

				//add post to posts object
				posts.addPost(poster, post, urlPic);

			}
		}

	}

	
	//client call to add user to follow
	public ApiResponse addFollow(String followName){

		//client call to add user to follow
		ApiResponse resp = client.apiRequest(HttpMethod.POST,null , "{}", USERGRID_APP, "users",username,"following","user",followName);
		
		//return client response
		return resp;
	}

	
	//client call to post new message
	public ApiResponse post(String postMess){

		//post properties
		Map<String, Object> data = new HashMap<String,Object>();
		Map<String, Object> actor = new HashMap<String,Object>();
		Map<String, Object> image = new HashMap<String,Object>();
		
		//add image url, height, and width of image
		image.put("url", imageURL);
		image.put("height", 80);
		image.put("width", 80);
		
		//add username, image, and email
		actor.put("displayName", username);
		actor.put("image", image);
		actor.put("email", email);

		//add actor, set action to post, and add message
		data.put("actor", actor);
		data.put("verb", "post");
		data.put("content",postMess);

		//client call to post message
		ApiResponse resp = client.apiRequest(HttpMethod.POST,null , data, USERGRID_APP, "users",username,"activities");

		//return client response
		return resp;
	}

	
	//client attempt to add account
	public ApiResponse addAccount(String username, String password, String email){
		
		
		//attempt to create account
		Map<String, Object> data = new HashMap<String,Object>();

		//create form
		data.put("username", username);
		data.put("email", email);
		data.put("password", password);

		//client call to add account
		ApiResponse resp = client.apiRequest(HttpMethod.POST, null, data, USERGRID_APP, "users");

		//return client response
		return resp;
	}

	
	//return api url
	public String getAPIURL(){return USERGRID_API_URL;}

	//set api url
	public void setAPIURL(String newURL){this.USERGRID_API_URL = newURL;}

	//return posts object
	public Posts getPosts(){return posts;}

	//return postImage object
	public PostImages getPostImages(){return postImages;}
	
	//return reading posts flag
	public boolean getFlagReadingPosts(){return gettingPostsFlag;}

	//set reading posts flag
	public void setFlagReadingPosts(boolean arg){gettingPostsFlag=arg;}

}
