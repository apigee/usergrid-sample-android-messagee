//Login.java - handles login view

package com.project.messagee;

import org.usergrid.android.client.response.ApiResponse;

import com.project.messagee.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class Login extends Activity implements View.OnClickListener{
	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		//set view to login
		setContentView(R.layout.login);

		//grab button handles and set click listeners
		ImageButton loginButton = (ImageButton) findViewById(R.id.login_button_id);
		loginButton.setOnClickListener(this);

		ImageButton serverButton = (ImageButton) findViewById(R.id.server_button_id);
		serverButton.setOnClickListener(this);

		ImageButton registerButton = (ImageButton) findViewById(R.id.register_button_id);
		registerButton.setOnClickListener(this);

	}

	public void onClick(View arg0){

		//find what button was clicked
		switch(arg0.getId()){


		//login clicked
		case R.id.login_button_id:

			//grab login info
			String email = ((EditText) findViewById(R.id.login_email))
			.getText().toString().trim();
			String password = ((EditText) findViewById(R.id.login_password))
					.getText().toString().trim();

			//show logging in dialog
			showLoginProgress();

			//thread to try to login through client
			new LoginTask(email, password,this.getApplication()).execute();

			break;


			//change server api clicked
		case R.id.server_button_id:

			//oper new url dialog
			Intent i = new Intent();
			i.setClassName("com.project.messagee",
					"com.project.messagee.ChangeServerURLDialog");
			startActivity(i);
			break;


			//register button clicked
		case R.id.register_button_id:

			//open new account page
			Intent i2 = new Intent();
			i2.setClassName("com.project.messagee",
					"com.project.messagee.AddAccount");
			startActivity(i2);	  		
			break;
		}
	}


	
	ProgressDialog loginProgressDialog = null;

	
	//dialog to display login progress
	public void showLoginProgress() {
		loginProgressDialog = ProgressDialog.show(this, "",
				"Signing in. Please wait...", true);
	}
	
	
	//hide logging in dialog
	public void hideLoginProgressDialog() {
		if (loginProgressDialog != null) {
			loginProgressDialog.dismiss();
		}
		loginProgressDialog = null;
	}
	
	
	//show login error dialog
	public void showLoginError() {
		((EditText) findViewById(R.id.login_password)).getText().clear();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Unable to sign in. Please check your name, password, and API URL.")
				.setCancelable(false)
				.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	
	//thread to attempt login
	private class LoginTask extends AsyncTask<Void, Void, ApiResponse> {

		String email;
		String password;
		Application app;

		public LoginTask(String email, String password, Application app) {

			this.email = email;
			this.password = password;
			this.app = app;
		}


		//main thread function attempts login
		protected ApiResponse doInBackground(Void... v) {
			
			//clear all posts in case previous user data is still active
			((Messagee)app).messController.getPosts().clearAll();
			((Messagee)app).messController.getPostImages().clearImages();
			
			return ((Messagee) app).messController.login(email, password);


		}


		//check if login was successful and act on it
		protected void onPostExecute(ApiResponse response) {
			
			
			//show error if response is empty or shows error
			if ((response == null) || "invalid_grant".equals(response.getError())) {
				hideLoginProgressDialog();
				showLoginError();
			}
			
			
			//successful login
			else{
				
				//get posts for user
				((Messagee) app).messController.getPostsFromClient();

				
				//display messageboard
				Intent i = new Intent();
				i.setClassName("com.project.messagee",
						"com.project.messagee.MessageBoard");
				startActivity(i);
				
				
				//hide logging in dialog
				hideLoginProgressDialog();

			}
		}

	}



}