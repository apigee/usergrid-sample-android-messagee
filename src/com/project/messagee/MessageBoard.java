//MessageBoard.java - handles message board view and creates views for posts.

package com.project.messagee;


import com.project.messagee.Messagee;
import com.project.messagee.R;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.Application;

public class MessageBoard extends Activity implements View.OnClickListener{

	//llPosts contains all posts, each stored in its own layout
	private LinearLayout llPosts;

	//scale layout dimensions based on screen density
	private float scale;

	//flag to indicate when message board view is active
	private boolean isActive = true;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		//set view to show message board
		setContentView(R.layout.message_board);

		//get scale factor from device display
		scale = this.getResources().getDisplayMetrics().density;

		//grab handle to post container
		llPosts = (LinearLayout) findViewById(R.id.linear_layout_posts);

		//create buttons and listeners
		ImageButton newMessButton = (ImageButton) findViewById(R.id.new_message_button_id);
		newMessButton.setOnClickListener(this);

		ImageButton addUserToFollow = (ImageButton) findViewById(R.id.add_follow_button_id);
		addUserToFollow.setOnClickListener(this);

		ImageButton logoutButton = (ImageButton) findViewById(R.id.logout_button_id);
		logoutButton.setOnClickListener(this);


		//periodically poll for new posts in a separate thread
		new PollForPostsTask(this.getApplication(),this).execute();

	}

	@Override
	protected void onResume(){
		super.onResume();

		//activity resumed set flag to true
		isActive = true;
	}


	@Override
	protected void onPause(){
		super.onPause();

		//activity leaving set flag to false
		isActive = false;
	}


	//function to draw all posts
	private void createPosts() {

		//get total number of posts from Posts.java
		int numPosts = ((Messagee) this.getApplication()).messController.getPosts().getNumPosts();

		//clear all posts from llPosts
		llPosts.removeAllViews();

		//create individual post layouts and add to llPosts
		for(int i=numPosts-1; i>=0; i--){

			/*cell layout:

			  		|picture| |arrow| |Username|
			  		|		| |image| |Post ............|
			  		|       | |     | |Post ............|
			        |		| |     | |Post ............|


			 */

			//create layout for post cell
			LinearLayout llCell = new LinearLayout(this);
			llCell.setOrientation(LinearLayout.HORIZONTAL);

			//create layout to hold username and post message
			LinearLayout llUserAndPost = new LinearLayout(this);
			llUserAndPost.setOrientation(LinearLayout.VERTICAL);

			//Create image holder for user image
			ImageView postImage = new ImageView(this);
			postImage.setMaxWidth(dpToPix(50));
			postImage.setBackgroundColor(getResources().getColor(R.color.black));
			postImage.setPadding(dpToPix(1), dpToPix(1), dpToPix(1), dpToPix(1));
			postImage.setMaxHeight(dpToPix(50));
			postImage.setScaleType(ImageView.ScaleType.FIT_XY);
			postImage.setAdjustViewBounds(true);
			String imURL = ((Messagee) this.getApplication()).messController.getPosts().getPicURLByIndex(i);
			postImage.setImageDrawable(((Messagee) this.getApplication()).messController.getPostImages().getImage(imURL));

			//draw arrow
			ImageView arrowImage = new ImageView(this);
			arrowImage.setMaxWidth(dpToPix(30));
			arrowImage.setMaxHeight(dpToPix(30));
			arrowImage.setScaleType(ImageView.ScaleType.FIT_XY);
			arrowImage.setAdjustViewBounds(true);
			arrowImage.setImageDrawable(getResources().getDrawable(R.drawable.arrow));

			//text holder for username
			String username = ((Messagee) this.getApplication()).messController.getPosts().getPostNameByIndex(i);
			TextView usernameText = new TextView(this);
			usernameText.setPadding(0, 0, 0, dpToPix(4));
			usernameText.setTextColor(getResources().getColor(R.color.username_blue));
			usernameText.setText(username);
			usernameText.setTypeface(null,1);
			usernameText.setTextSize(17);

			//text holder for message
			String postMessage = ((Messagee) this.getApplication()).messController.getPosts().getPostMessageByIndex(i);
			TextView postText = new TextView(this);
			postText.setTextColor(getResources().getColor(R.color.post_message_gray));
			postText.setText(postMessage);
			postText.setTextSize(17);

			//add username and post text to a linear layout
			llUserAndPost.addView(usernameText);
			llUserAndPost.addView(postText);

			//set layout properties and add rounded rectangle border
			llUserAndPost.setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_corners_white));
			llUserAndPost.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
			llUserAndPost.setPadding(dpToPix(14), dpToPix(10), dpToPix(14), dpToPix(10));

			//add images and text layout to create the post layout
			llCell.addView(postImage);
			llCell.addView(arrowImage);
			llCell.addView(llUserAndPost);
			llCell.setPadding(dpToPix(10f), dpToPix(18f), dpToPix(10f), 0);

			//add post layout to layout containing all posts
			llPosts.addView(llCell);

		}

	}

	//function to convert from density independent dimension to pixels
	private int dpToPix(float dps){
		return (int)(dps*scale+.5f);
	}

	public void onClick(View arg0){

		//check which button was clicked
		switch(arg0.getId()){

		
		//new message - start new message activity
		case R.id.new_message_button_id:
			//finish();
			Intent i = new Intent();
			i.setClassName("com.project.messagee",
					"com.project.messagee.NewMessage");
			startActivity(i);
			break;

			
		//add follow - start follow dialog
		case R.id.add_follow_button_id:

			Intent intent = new Intent(this,AddUserToFollowDialog.class);
			startActivityForResult(intent, 0);

			break;

			
		//logout - logout and start login activity
		case R.id.logout_button_id:
			
			Intent i3 = new Intent();
			i3.setClassName("com.project.messagee",
					"com.project.messagee.Login");
			startActivity(i3);

			break;
		}

	}

	//thread to periodically grab posts
	private class PollForPostsTask extends AsyncTask<Void, Void, Void> {

		Application app;
		MessageBoard messBoard;

		public PollForPostsTask (Application app, MessageBoard messBoard) {

			this.app = app;
			this.messBoard = messBoard;
		}

		
		//main thread function for grabbing posts
		protected Void doInBackground(Void... params) {

			//only get posts when message board is active
			while(messBoard.isActive){
				
				//draw posts
				publishProgress();
				
				//get posts from client
				((Messagee) app).messController.setFlagReadingPosts(true);
				((Messagee) app).messController.getPostsFromClient();
				((Messagee) app).messController.setFlagReadingPosts(false);

				//wait one second
				android.os.SystemClock.sleep(1000);
			}
			return null;

		}

		//called by publishProgress() to update UI 
		protected void onProgressUpdate(Void... values){

			//create and diplay posts
			createPosts();

		};
	}


}



