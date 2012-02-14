//NewMessage.java - Handles view for creating a new post.

package com.project.messagee;

import org.usergrid.android.client.response.ApiResponse;

import com.project.messagee.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.view.View;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;


public class NewMessage extends Activity implements View.OnClickListener{

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//set view to show new message screen
		setContentView(R.layout.new_message);

		//grab handles for post and close buttons
		ImageButton postButton = (ImageButton) findViewById(R.id.postbutton);
		ImageButton closeButton = (ImageButton) findViewById(R.id.closebutton);

		//create button listeners
		closeButton.setOnClickListener(this);
		postButton.setOnClickListener(this);

	}


	//called when button is clicked
	public void onClick(View arg0){

		//switch to check which button was clicked
		switch(arg0.getId()){

		//close was clicked - return to messageboard
		case R.id.closebutton:

			Intent i = new Intent();
			i.setClassName("com.project.messagee",
					"com.project.messagee.MessageBoard");
			startActivity(i);
			break;

		//post clicked - get post string and start thread to send post through client.
		case R.id.postbutton:

			//grab post string
			EditText postTextBox = (EditText) findViewById(R.id.postText);
			String message = postTextBox.getText().toString().trim();

			//show progress dialog
			showPostProgress();

			//start thread to send post through client
			new PostTask(this.getApplication(),message).execute();

			break;

		}

	}

	ProgressDialog loginProgressDialog = null;

	//create dialog to show progress wheel
	public void showPostProgress() {
		loginProgressDialog = ProgressDialog.show(this, "",
				"Posting. Please wait...", true);
	}
	
	
	//hide post progress dialog
	public void hidePostProgressDialog() {
		if (loginProgressDialog != null) {
			loginProgressDialog.dismiss();
		}
		loginProgressDialog = null;
	}
	
	
	//show error dialog
	public void showPostError() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Unable to post. Please try again.")
				.setCancelable(false)
				.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}


	//Thread for sending post through client
	private class PostTask extends AsyncTask<Void, Void, ApiResponse> {

		Application app;
		String message;

		public PostTask(Application app, String message) {
			this.app = app;
			this.message = message;
		}

		
		//main thread function for communicating through client
		protected ApiResponse doInBackground(Void... v) {

			//attempt to post message
			return ((Messagee) app).messController.post(message);

		}

		
		//once communication is done check for errors and return to 
		//message board if none are found
		protected void onPostExecute(ApiResponse response) {

			//remove progress dialog
			hidePostProgressDialog();
			
			//show error if reply is empty or contains error
			if ((response == null) || "invalid_grant".equals(response.getError())) {
				showPostError();
			}
			
			//sucessfully posted return to message board
			else{
				Intent i = new Intent();
				i.setClassName("com.project.messagee",
						"com.project.messagee.MessageBoard");
				startActivity(i);

			}
		}

	}

}