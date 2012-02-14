//PostImages.java - class to store user images and reuse images with the same URL.

package com.project.model;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import android.graphics.drawable.Drawable;

public class PostImages {
	
	//images keeps a list of images grabbed so far.
	//imageUrls stores the corresponding url for the images stored.
	ArrayList<Drawable> images = new ArrayList<Drawable>();
	ArrayList<String> imageUrls = new ArrayList<String>();

	
	public PostImages(){

	}
	
	
	//Add image to list of images and add url
	public void addImage(Drawable image, String url){
		images.add(image);
		imageUrls.add(url);
	}
	
	
	//Remove all images and urls
	public void clearImages(){
		images.clear();
		imageUrls.clear();
	}
	
	
	//Return image for given url. Checks if image is already stored and 
	//if not calls function to load image from url.
	public Drawable getImage(String url){
		
		//look for image in list
		int index = imageUrls.indexOf(url);
		if(index!=-1){

			return images.get(index);
			
		}
		
		//if not found grab from url and store in list
		else{
		Drawable image = LoadImageFromWebOperations(url);
		addImage(image,url);
		return image;
		}
	}
	
	
	//Function to grab an image from a given url.
	private Drawable LoadImageFromWebOperations(String url)
	{
		try{
			InputStream is = (InputStream) new URL(url).getContent();
			Drawable d = Drawable.createFromStream(is, "src name");
			return d;
		}catch (Exception e) {
			System.out.println("Exc="+e);
			return null;
		}
	}
}
