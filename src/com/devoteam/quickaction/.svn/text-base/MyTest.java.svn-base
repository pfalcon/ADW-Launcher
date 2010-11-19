package com.devoteam.quickaction;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * Activity that provides a simple sample of how to use a QuickActionWindow
 *
 */
public class MyTest extends Activity {

	private Context mContext = this;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button b1 = (Button) findViewById(R.id.b1);
		b1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				//array to hold the coordinates of the clicked view
				int[] xy = new int[2];
				//fills the array with the computed coordinates
				v.getLocationInWindow(xy);
				//rectangle holding the clicked view area
				Rect rect = new Rect(xy[0], xy[1], xy[0]+v.getWidth(), xy[1]+v.getHeight());
				
				//a new QuickActionWindow object
				final QuickActionWindow qa = new QuickActionWindow(mContext, v, rect);

				//adds an item to the badge and defines the quick action to be triggered
				//when the item is clicked on
				qa.addItem(getResources().getDrawable(android.R.drawable.ic_menu_agenda), "agenda", new OnClickListener() {
					public void onClick(View v) {
						Toast.makeText(mContext, "agenda", Toast.LENGTH_SHORT).show();
						qa.dismiss();
					}
				});

				qa.addItem(getResources().getDrawable(android.R.drawable.ic_menu_add), "add", new OnClickListener() {
					public void onClick(View v) {
						Toast.makeText(mContext, "add", Toast.LENGTH_SHORT).show();
						qa.dismiss();
					}
				});

				qa.addItem(getResources().getDrawable(android.R.drawable.ic_menu_call), "call", new OnClickListener() {
					public void onClick(View v) {
						Toast.makeText(mContext, "call", Toast.LENGTH_SHORT).show();
						qa.dismiss();
					}
				});

				qa.addItem(getResources().getDrawable(android.R.drawable.ic_menu_camera), "camera", new OnClickListener() {
					public void onClick(View v) {
						Toast.makeText(mContext, "camera", Toast.LENGTH_SHORT).show();
						qa.dismiss();
					}
				});

				qa.addItem(getResources().getDrawable(android.R.drawable.ic_menu_compass), "compass", new OnClickListener() {
					public void onClick(View v) {
						Toast.makeText(mContext, "compass", Toast.LENGTH_SHORT).show();
						qa.dismiss();
					}
				});
				qa.addItem(getResources().getDrawable(android.R.drawable.ic_menu_crop), "crop", new OnClickListener() {
					public void onClick(View v) {
						Toast.makeText(mContext, "crop", Toast.LENGTH_SHORT).show();
						qa.dismiss();
					}
				});

				qa.addItem(getResources().getDrawable(android.R.drawable.ic_menu_help), "help", new OnClickListener() {
					public void onClick(View v) {
						Toast.makeText(mContext, "help", Toast.LENGTH_SHORT).show();
						qa.dismiss();
					}
				});
				
				//shows the quick action window on the screen
				qa.show();
			}
		});


		Button b2 = (Button) findViewById(R.id.b2);
		b2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				int[] xy = new int[2];
				v.getLocationInWindow(xy);
				Rect rect = new Rect(xy[0], xy[1], xy[0]+v.getWidth(), xy[1]+v.getHeight());

				final QuickActionWindow qa = new QuickActionWindow(mContext, v, rect);
				
				qa.addItem(getResources().getDrawable(android.R.drawable.ic_menu_agenda), "agenda", new OnClickListener() {
					public void onClick(View v) {
						Toast.makeText(mContext, "agenda", Toast.LENGTH_SHORT).show();
						qa.dismiss();
					}
				});

				qa.addItem(getResources().getDrawable(android.R.drawable.ic_menu_add), "add", new OnClickListener() {
					public void onClick(View v) {
						Toast.makeText(mContext, "add", Toast.LENGTH_SHORT).show();
						qa.dismiss();
					}
				});

				qa.addItem(getResources().getDrawable(android.R.drawable.ic_menu_call), "call", new OnClickListener() {
					public void onClick(View v) {
						Toast.makeText(mContext, "call", Toast.LENGTH_SHORT).show();
						qa.dismiss();
					}
				});
				
				qa.show();
			}
		});

	}
}
