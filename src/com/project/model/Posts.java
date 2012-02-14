//Posts.java - stores lists of information for each post.

package com.project.model;

import java.util.ArrayList;



public class Posts {
	
	//postNames, postMessages, and postPicUrls contain post information
	//same indices have information for same post.
	private ArrayList<String> postNames = new ArrayList<String>();
	private ArrayList<String> postMessages = new ArrayList<String>();
	private ArrayList<String> postPicUrls = new ArrayList<String>();
	
	//Store the number of current posts
	private int numPosts=0;
	
	
	public Posts(){}
	
	
	//Add post info to lists
	public void addPost(String postName, String postMessage, String postImageURL){
		postNames.add(postName);
		postMessages.add(postMessage);
		postPicUrls.add(postImageURL);
		
		numPosts++;
	}
	
	
	//Return the total number of posts
	public int getNumPosts(){
		return numPosts;
	}
	
	
	//Return the list of post usernames
	public ArrayList<String> getPostNames(){
		return postNames;
	}
	
	
	//Return the list of post messages
	public ArrayList<String> getMessages(){
		return postMessages;
	}
	
	
	//Return the username for a given post index
	public String getPostNameByIndex(int index){
		return postNames.get(index);
	}
	
	
	//Return the message for a given post index
	public String getPostMessageByIndex(int index){
		return postMessages.get(index);
	}
	
	
	//Return the picture url for a given post
	public String getPicURLByIndex(int index){
		return postPicUrls.get(index);
	}
	
	
	//Clear all post information
	public void clearAll(){
		postNames.clear();
		postMessages.clear();
		postPicUrls.clear();
		numPosts=0;
	}
}
