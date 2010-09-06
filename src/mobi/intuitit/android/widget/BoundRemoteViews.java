package mobi.intuitit.android.widget;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;

public class BoundRemoteViews extends SimpleRemoteViews {

	protected class BindingAction extends SimpleRemoteViews.ReflectionAction
	{
		public static final int tag = 99;
		
		private int mCursorIndex;
		private int mDefaultResource;
		
		public BindingAction(int viewId, String methodName, int type, int cursorIndex, int defaultResource) {
			super(viewId, methodName, type);
			mCursorIndex = cursorIndex;
			mDefaultResource = defaultResource;
	    }

		public BindingAction(Parcel in) {
			super(in);
		}
		
		@Override
	    protected int getTag() {
	    	return tag;
	    }
		
		@Override
		protected void readValue(Parcel in) {			
			mCursorIndex = in.readInt();
			mDefaultResource = in.readInt();
		}
		
		@Override
		protected void writeValue(Parcel out, int flags) {
			out.writeInt(mCursorIndex);
			out.writeInt(mDefaultResource);
		}
		
		@Override
		protected Object getValue(Context context) {
			switch(this.type) {
				case STRING:
				case CHAR_SEQUENCE:
					String value;
					try
					{
						value = BoundRemoteViews.this.mCursor.getString(mCursorIndex);
						if (value == null && mDefaultResource > 0)
							value = context.getString(mDefaultResource);						
					}
					catch(Exception ex) {
						if (mDefaultResource > 0)
							value = context.getString(mDefaultResource);
						else
							value = null;
					}
					return value;
				// BOOLEAN ?!?
				case BYTE:
					return (byte)BoundRemoteViews.this.mCursor.getInt(mCursorIndex);
				case SHORT:
					return (short)BoundRemoteViews.this.mCursor.getInt(mCursorIndex);
				case INT:
					return BoundRemoteViews.this.mCursor.getInt(mCursorIndex);
				case LONG:
					return BoundRemoteViews.this.mCursor.getLong(mCursorIndex);
				case FLOAT:
					return BoundRemoteViews.this.mCursor.getFloat(mCursorIndex);
				case DOUBLE:
					return BoundRemoteViews.this.mCursor.getDouble(mCursorIndex);			
				case CHAR:
					return BoundRemoteViews.this.mCursor.getString(mCursorIndex).charAt(0);
				case URI:
					return Uri.parse(BoundRemoteViews.this.mCursor.getString(mCursorIndex));
				case BITMAP:
					Bitmap bmp_value = null;
					try
					{
						byte[] blob = BoundRemoteViews.this.mCursor.getBlob(mCursorIndex);
						if (blob != null) 
							bmp_value = BitmapFactory.decodeByteArray(blob, 0, blob.length);
						else if (mDefaultResource > 0)
							bmp_value = BitmapFactory.decodeResource(context.getResources(), mDefaultResource);
						else
							bmp_value = null;						
					}
					catch(Exception ex)
					{
						if (mDefaultResource > 0)
							bmp_value = BitmapFactory.decodeResource(context.getResources(), mDefaultResource);
						else
							bmp_value = null;
					}
	                return bmp_value;
			}
		   //    static final int BUNDLE = 13;

			
			
			// TODO Auto-generated method stub
			return super.getValue(context);
		}
		
		
		public void apply(View root) {
			super.apply(root);
		}
	}
	
	protected class SetBoundOnClickIntent extends Action {
		private static final int TAG = 100;
		
		private String mExtraName;
		private int mExtraCursorIndex;
		private int mViewId;
		private Intent mIntent;
		
        public SetBoundOnClickIntent(int id, Intent intent, 
        		String extraName, int extraCursorIndex) {
        	mViewId = id;
        	mIntent = intent;
        	mExtraName = extraName;
        	mExtraCursorIndex = extraCursorIndex;
        }
       
        public SetBoundOnClickIntent(Parcel parcel) {
        	mViewId = parcel.readInt();
        	mExtraName = parcel.readString();
        	mExtraCursorIndex = parcel.readInt();
        	mIntent = Intent.CREATOR.createFromParcel(parcel);
        }
        
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(TAG);
            dest.writeInt(mViewId);
            dest.writeString(mExtraName);
            dest.writeInt(mExtraCursorIndex);
            mIntent.writeToParcel(dest, 0);
        }
        
        @Override
        public void apply(View root) {
            final View target = root.findViewById(mViewId);           
            if (target != null && mIntent != null) {
                target.setOnClickListener(new BoundOnClickListener(BoundRemoteViews.this.mCursor.getPosition()));
            }
        }
        
        private class BoundOnClickListener implements OnClickListener {
        	
        	private int mCursorPos;
        	 
        	public BoundOnClickListener(int cursorPos) {
        		mCursorPos = cursorPos;
        	}
        	
        	public void onClick(View v) {
                 // Find target view location in screen coordinates and
                 // fill into PendingIntent before sending.
          	   final int[] location = new int[2];
                 v.getLocationOnScreen(location);
                 Rect srcRect = new Rect();
                 srcRect.left = location[0];
                 srcRect.top = location[1];
                 srcRect.right = srcRect.left + v.getWidth();
                 srcRect.bottom = srcRect.top + v.getHeight();
                 Intent intent = new Intent(mIntent);
                 intent.setSourceBounds(srcRect);        
                 intent.setComponent(BoundRemoteViews.this.mComponentName);
                 mCursor.moveToPosition(mCursorPos);
                 prepareIntent(intent);
                 try {
                 	v.getContext().sendBroadcast(intent);
                 } catch (Exception e) {
                     android.util.Log.e("SetBoundOnClickIntent", "Cannot send intent: ", e);
                 }
             }
        }
        

        protected void prepareIntent(Intent intent) {
        	String value = BoundRemoteViews.this.mCursor.getString(mExtraCursorIndex);
        	intent.putExtra(mExtraName, value);
        }
	}
	
	private Cursor mCursor;
	private ComponentName mComponentName;
	
	public BoundRemoteViews(Parcel parcel) {
		super(parcel);
	}

	public BoundRemoteViews(int layoutId) {
		super(layoutId);
	}
	
	public void setBindingCursor(Cursor cursor) {
		mCursor = cursor;
	}
	
	public void setIntentComponentName(ComponentName componenName) {
		mComponentName = componenName;
	}

	protected Action loadActionFromParcel(int tag, Parcel parcel) {
		if (tag == BoundRemoteViews.BindingAction.tag)
			return new BindingAction(parcel);
		else if (tag == SetBoundOnClickIntent.TAG)
			return new SetBoundOnClickIntent(parcel);
		else
			return super.loadActionFromParcel(tag, parcel);
	}
	
    public void setBoundString(int viewId, String methodName, int cursorIndex, int defaultResource) {
    	addAction(new BindingAction(viewId, methodName, ReflectionAction.STRING, 
    								cursorIndex, defaultResource));
    }
    
    public void setBoundCharSequence(int viewId, String methodName, int cursorIndex, int defaultResource) {
    	addAction(new BindingAction(viewId, methodName, ReflectionAction.CHAR_SEQUENCE, 
    								cursorIndex, defaultResource));
    }    

    public void setBoundByte(int viewId, String methodName, int cursorIndex) {
    	addAction(new BindingAction(viewId, methodName, ReflectionAction.BYTE, 
    								cursorIndex,0));
    }        
    
    public void setBoundShort(int viewId, String methodName, int cursorIndex) {
    	addAction(new BindingAction(viewId, methodName, ReflectionAction.SHORT, 
    								cursorIndex,0));
    }      
    
    public void setBoundInt(int viewId, String methodName, int cursorIndex) {
    	addAction(new BindingAction(viewId, methodName, ReflectionAction.INT, 
    								cursorIndex,0));
    }
    
    public void setBoundLong(int viewId, String methodName, int cursorIndex) {
    	addAction(new BindingAction(viewId, methodName, ReflectionAction.LONG, 
    								cursorIndex,0));
    }
    
    public void setBoundFloat(int viewId, String methodName, int cursorIndex) {
    	addAction(new BindingAction(viewId, methodName, ReflectionAction.FLOAT, 
    								cursorIndex,0));
    }

    public void setBoundDouble(int viewId, String methodName, int cursorIndex) {
    	addAction(new BindingAction(viewId, methodName, ReflectionAction.DOUBLE, 
    								cursorIndex,0));
    }
    
    public void setBoundChar(int viewId, String methodName, int cursorIndex) {
    	addAction(new BindingAction(viewId, methodName, ReflectionAction.CHAR, 
    								cursorIndex,0));
    }    

    public void setBoundUri(int viewId, String methodName, int cursorIndex) {
    	addAction(new BindingAction(viewId, methodName, ReflectionAction.URI, 
    								cursorIndex,0));
    }

    public void setBoundBitmap(int viewId, String methodName, int cursorIndex, int defaultResource) {
    	addAction(new BindingAction(viewId, methodName, ReflectionAction.BITMAP, 
    								cursorIndex, defaultResource));
    }
    
    public void SetBoundOnClickIntent(int viewId, Intent intent,
    		String extraName, int extraCursorIndex) {
        addAction(new SetBoundOnClickIntent(viewId, intent, extraName, extraCursorIndex));
    }
    
    /**
     * Parcelable.Creator that instantiates RemoteViews objects
     */
    public static final Parcelable.Creator<BoundRemoteViews> CREATOR = new Parcelable.Creator<BoundRemoteViews>() {
        public BoundRemoteViews createFromParcel(Parcel parcel) {
            return new BoundRemoteViews(parcel);
        }

        public BoundRemoteViews[] newArray(int size) {
            return new BoundRemoteViews[size];
        }
    };    
}
