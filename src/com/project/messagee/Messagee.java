//Messagee.java - Global class for creating a single message controller for
//views to use for communications. 

package com.project.messagee;


import com.project.controller.MessageController;

import android.app.Application;

public class Messagee extends Application{

	public MessageController messController = new MessageController();
	
	
}
