//AddAccount.java - handles view for creating new accounts


package com.project.messagee;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


public class AddAccount extends Activity implements View.OnClickListener{

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//set view to add account
		setContentView(R.layout.add_account);

		//grab button handles
		ImageButton createButton = (ImageButton) findViewById(R.id.add_acct_add_btn);
		ImageButton cancelButton = (ImageButton) findViewById(R.id.add_account_close_btn);

		//set click listeners
		createButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);

	}


	//button clicked
	public void onClick(View arg0){
		
		//check which button was clicked
		switch(arg0.getId()){

		//close clicked return to login
		case R.id.add_account_close_btn:

			Intent i = new Intent();
			i.setClassName("com.project.messagee",
					"com.project.messagee.Login");
			startActivity(i);
			break;
			

		//add account clicked - attempt to add account
		case R.id.add_acct_add_btn:

			//grab account data textfields
			EditText usernameTxt = (EditText) findViewById(R.id.add_acct_user_textbox);
			EditText emailTxt = (EditText) findViewById(R.id.add_acct_email_textbox);
			EditText passVerifyTxt = (EditText) findViewById(R.id.add_acct_pass_verify_textbox);
			EditText passTxt = (EditText) findViewById(R.id.add_acct_pass_textbox);
			
			//get strings from textfields
			String username = usernameTxt.getText().toString().trim();
			String email = emailTxt.getText().toString().trim();
			String passVerify = passVerifyTxt.getText().toString().trim();
			String password = passTxt.getText().toString().trim();


			//if length of email < 6, show error
			if(email.length()<6){
				showAddAccountEmailLengthError();
				break;
			}

			//if email does not look like email show error
			Pattern pattern = Pattern.compile(".+@.+\\.[a-z]+");
			Matcher matcher = pattern.matcher(email);
			boolean matchFound = matcher.matches();
			if(!matchFound){
				showAddAccountEmailFormatError();
				break;
			}
			
			//if length password < 5, show error
			if(password.length()<5){
				showAddAccountPasswordLengthError();
				break;
			}

			//if passwords match try to create account
			if(password.toString().equals(passVerify)){

				showAddAcctProgress();

				new AddAcctTask(this.getApplication(),username, password, email).execute();

			}
			
			//passwords don't match show error dialog
			else{
				showAddAccountPasswordError();
			}

			
			break;

		}

	}

	
	ProgressDialog addAccountProgressDialog = null;

	//show add account progress dialog
	public void showAddAcctProgress() {
		addAccountProgressDialog = ProgressDialog.show(this, "",
				"Creating Account. Please wait...", true);
	}

	//remove add account progress dialog
	public void hideAddAcctProgressDialog() {
		if (addAccountProgressDialog != null) {
			addAccountProgressDialog.dismiss();
		}
		addAccountProgressDialog = null;
	}

	//add account error dialog
	private void showAddAccountError() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Unable to add account. Please try again.")
				.setCancelable(false)
				.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	//client error dialog
	private void showAddAccountClientError(String error) {
		String message = "Error: " + error;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
		.setCancelable(false)
		.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	//password mismatch error
	private void showAddAccountPasswordError() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Passwords don't match. Please try again.")
				.setCancelable(false)
				.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	//password length error
	private void showAddAccountPasswordLengthError() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Password must be five characters or more. Please try again.")
				.setCancelable(false)
				.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	//email length error
	private void showAddAccountEmailLengthError() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Email must be six characters or more. Please try again.")
				.setCancelable(false)
				.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	//email length error
	private void showAddAccountEmailFormatError() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Email format error. eg. ui@jquery.com")
				.setCancelable(false)
				.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	//show account created dialog
	private void showAddAccountSuccess() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"New account created.")
				.setCancelable(false)
				.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Intent i = new Intent();
						i.setClassName("com.project.messagee",
								"com.project.messagee.Login");
						startActivity(i);
						dialog.dismiss();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	
	//thread to try to add account
	private class AddAcctTask extends AsyncTask<Void, Void, ApiResponse> {

		Application app;
		String username;
		String password;
		String email;
		
		
		public AddAcctTask(Application app, String username, String password, String email) {
			this.app = app;
			this.username = username;
			this.password = password;
			this.email = email;
		}
		

		//main function, attempts to add account
		protected ApiResponse doInBackground(Void... v) {

			//attempt to post message
			return ((Messagee) app).messController.addAccount(username, password, email);

		}

		
		//client responded, chech for errors, if none show success dialog
		protected void onPostExecute(ApiResponse response) {
			
			//hide progress dialog
			hideAddAcctProgressDialog();
			
			
			//if there is no response show error dialog
			if (response == null) {
				showAddAccountError();
				
				
		    //if there is a client error display it
			}else if(response.getError()!=null){
				showAddAccountClientError(response.getError().toString());
			}
	
			//Display successful creation dialog then return to login screen
			else{

				showAddAccountSuccess();

			}
		}

	}

}