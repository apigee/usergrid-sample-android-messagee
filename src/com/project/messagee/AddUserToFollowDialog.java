//AddUserToFollowDialog - View for adding users to follow

package com.project.messagee;

import org.usergrid.android.client.response.ApiResponse;

import com.project.messagee.R;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class AddUserToFollowDialog extends Activity{

	private Button okBtn;
	private Button cancelBtn;
	private EditText nameTextField;
	private Application app;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		app = this.getApplication();


		//set view to add user to follow dialog
		setContentView(R.layout.new_follow_dialog);

		//grab handle to text field
		nameTextField = (EditText)findViewById(R.id.search_query);

		//blur background
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);


		//grab ok button handle
		okBtn = (Button)findViewById(R.id.ok_button_dialog_id);

		//set ok button click listener
		okBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {

				//set wait dialog
				showAddFollowProgress();

				//try to add follow in separate thread
				new AddFollowTask(app,nameTextField.getText().toString().trim()).execute();

			}
		});



		//grab cancel button handle
		cancelBtn = (Button)findViewById(R.id.cancel_button_dialog_id);

		//set cancel button listener
		cancelBtn.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {

				//return to message board
				Intent i = new Intent();
				i.setClassName("com.project.messagee",
						"com.project.messagee.MessageBoard");
				startActivity(i);
			}
		});


	}
	

	ProgressDialog addFollowDialog = null;

	//show adding user dialog
	public void showAddFollowProgress() {
		addFollowDialog = ProgressDialog.show(this, "",
				"Adding user. Please wait...", true);
	}

	//hide adding user dialog
	public void hideAddFollowProgressDialog() {
		if (addFollowDialog != null) {
			addFollowDialog.dismiss();
		}
		addFollowDialog = null;
	}
	
	//show adding user error dialog
	public void showAddFollowError() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Unable to add user. Please try again.")
				.setCancelable(false)
				.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	//show user not found error dialog
	public void showNoFollowError() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"User not found. Please try again.")
				.setCancelable(false)
				.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	
	//add user to follow thread
	private class AddFollowTask extends AsyncTask<Void, Void, ApiResponse> {

		String username;
		Application app;

		public AddFollowTask(Application app, String followName) {
			super();
			this.app = app;
			this.username = followName;
		}
		
		//main function to try and add user to follow
		@Override
		protected ApiResponse doInBackground(Void... v) {

			//attempt to add user
			return ((Messagee) app).messController.addFollow(username);

		}

		
		//add user request complete, check for errors, if none return to messageboard
		@Override
		protected void onPostExecute(ApiResponse response) {
			
			//hide progress dialog
			hideAddFollowProgressDialog();
			
			//show error if no response or resposne with error
			if ((response == null) || "invalid_grant".equals(response.getError())) {
				showAddFollowError();
			}
			
			//if no user was added show error dialog
			else if(response.getEntityCount()==0){
				showNoFollowError();
			}
			
			//successful add, return to message board
			else{

				Intent i = new Intent();
				i.setClassName("com.project.messagee",
						"com.project.messagee.MessageBoard");
				startActivity(i);

			}
		}

	}

}