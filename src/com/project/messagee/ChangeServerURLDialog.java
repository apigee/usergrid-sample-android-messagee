//ChangeServerURLDialog - View for changing API URL

package com.project.messagee;


import com.project.messagee.R;


import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class ChangeServerURLDialog extends Activity{

	private Button saveBtn;
	private Button cancelBtn;
	private EditText urlTextField;
	private Application app;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		app = this.getApplication();
		
		
		//set view to show change server dialog
		setContentView(R.layout.change_server_url_dialog);
		
		//get handle to textfield and set text to current url
		urlTextField = (EditText)findViewById(R.id.url_textfield_id);
		urlTextField.setText(((Messagee) this.getApplication()).messController.getAPIURLWithApp());
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		
		
		//grab save button 
		saveBtn = (Button)findViewById(R.id.url_save_button_dialog_id);
		
		//save button listener
		saveBtn.setOnClickListener(new OnClickListener(){
			
			public void onClick(View v) {
				
				
				//change url to textfield url
				String apiURL = urlTextField.getText().toString().trim();
				
				//grab last url segment and save as app name
				int indexLastSlash = apiURL.lastIndexOf("/");
				
				Log.d("twid","api: " + apiURL.substring(0, (indexLastSlash)));
				Log.d("twid","app: " + apiURL.substring((indexLastSlash+1), (apiURL.length())));
				
				//((Messagee) app).messController.setAPIURL(apiURL.substring(0, (indexLastSlash-1)));
				 
				
				//return to login screen
			  	Intent i = new Intent();
			  	i.setClassName("com.project.messagee",
	              "com.project.messagee.Login");
			  	startActivity(i);
			  	
			}
		});
		
		
		//grab cancel button
		cancelBtn = (Button)findViewById(R.id.url_cancel_button_dialog_id);
		
		//cancel button listener
		cancelBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				
				//return to login page
			  	Intent i = new Intent();
			  	i.setClassName("com.project.messagee",
	              "com.project.messagee.Login");
			  	startActivity(i);
			}
		});


	}
	
		
}