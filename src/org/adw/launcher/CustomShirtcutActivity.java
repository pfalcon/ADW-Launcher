/**
* @author AnderWeb <anderweb@gmail.com>
*
**/

package org.adw.launcher;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class CustomShirtcutActivity extends Activity implements OnClickListener {
	private static final int PICK_CUSTOM_ICON=1;
	private static final int PICK_CUSTOM_ACTIVITY=2;
	private Button btPickActivity;
	private ImageButton btPickIcon;
	private Button btOk;
	private EditText edLabel;
	private ActivityInfo mInfo;
	private Drawable mIcon;
	private Bitmap mBitmap;
	PackageManager mPackageManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_shirtcuts);
		btPickActivity=(Button) findViewById(R.id.pick_activity);
		btPickActivity.setOnClickListener(this);
		btPickIcon=(ImageButton) findViewById(R.id.pick_icon);
		btPickIcon.setOnClickListener(this);
		btPickIcon.setEnabled(false);
		btOk=(Button) findViewById(R.id.shirtcut_ok);
		btOk.setEnabled(false);
		btOk.setOnClickListener(this);
		edLabel=(EditText) findViewById(R.id.shirtcut_label);
		mPackageManager=getPackageManager();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK){
			switch (requestCode) {
			case PICK_CUSTOM_ACTIVITY:
				mInfo=data.getParcelableExtra("activityInfo");
				btPickActivity.setText(mInfo.packageName);
				mIcon=mInfo.loadIcon(mPackageManager);
				btPickIcon.setImageDrawable(mIcon);
				btPickIcon.setEnabled(true);
				btOk.setEnabled(true);
				edLabel.setText(mInfo.loadLabel(mPackageManager));
				break;
			case PICK_CUSTOM_ICON:
				mBitmap = (Bitmap) data.getParcelableExtra("data");
				if(mBitmap!=null){
					btPickIcon.setImageBitmap(mBitmap);
				}
			default:
				break;
			}
		}
	}
	@Override
	public void onClick(View v) {
		Intent picker=new Intent();
		if(v.equals(btPickActivity)){
			picker.setClass(this, ActivityPickerActivity.class);
			startActivityForResult(picker,PICK_CUSTOM_ACTIVITY);
		}else if(v.equals(btPickIcon)){
			int width;
			int height;
		    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		    intent.setType("image/*");
			width = height=(int) getResources().getDimension(android.R.dimen.app_icon_size);
	        intent.putExtra("crop", "true");
			intent.putExtra("outputX", width);
			intent.putExtra("outputY", height);
			intent.putExtra("aspectX", width);
			intent.putExtra("aspectY", height);
	        intent.putExtra("noFaceDetection", true);
	        intent.putExtra("return-data", true);
			startActivityForResult(intent, PICK_CUSTOM_ICON);
		}else if(v.equals(btOk)){
	        // Build the intent for the chosen activity
	        Intent intent = new Intent();
	        intent.setComponent(new ComponentName(mInfo.applicationInfo.packageName,
	                mInfo.name));
	        Intent mReturnData = new Intent();
	        intent.setAction(Intent.ACTION_MAIN);
	        intent.addCategory(Intent.CATEGORY_LAUNCHER);
	        mReturnData.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

	        // Set the name of the activity
	        mReturnData.putExtra(Intent.EXTRA_SHORTCUT_NAME, edLabel.getText().toString());
			
	        if(mBitmap==null){
		        ShortcutIconResource iconResource = new ShortcutIconResource();
		        iconResource.packageName = mInfo.packageName;
		        try {
					Resources resources = mPackageManager.getResourcesForApplication(iconResource.packageName);
					iconResource.resourceName = resources.getResourceName(mInfo.getIconResource());
					mReturnData.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);
				} catch (NameNotFoundException e) {
				} catch (Resources.NotFoundException e) {
				}
	        }else{
				mReturnData.putExtra(Intent.EXTRA_SHORTCUT_ICON, mBitmap);
	        }
			setResult(RESULT_OK,mReturnData);
			finish();
		}
	}
	

}
