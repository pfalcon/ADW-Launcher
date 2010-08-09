/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.adw.launcher.catalogue;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import org.adw.launcher.Launcher;
import org.adw.launcher.R;

/**
 * Adapter showing the types of items that can be added to a {@link Workspace}.
 */
public class AppGroupAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    
    private final ArrayList<ListItem> mItems = new ArrayList<ListItem>();
    
    public static final int APP_GROUP_0   = 0;
    public static final int APP_GROUP_1   = 1;
    public static final int APP_GROUP_2   = 2;
    public static final int APP_GROUP_3   = 3;
    public static final int APP_GROUP_4   = 4;
    public static final int APP_GROUP_5   = 5;
    public static final int APP_GROUP_ALL = 6;
    public static final int APP_GROUP_CONFIG = 7;
    public static final int APP_GROUP_ADD = 8;
    /**
     * Specific item in our list.
     */
    public class ListItem {
        public final CharSequence text;
        public final int actionTag;
        
        public ListItem(Resources res, int textResourceId, /*int imageResourceId,*/ int actionTag) {
            text = res.getString(textResourceId);
            this.actionTag = actionTag;
        }
        public ListItem(Resources res, String textResource,/* int imageResourceId,*/ int actionTag) {
            text = textResource;
            this.actionTag = actionTag;
        }

    }

	private void addListItem(Resources res, int i, int appGrp/*, int iconId*/)
	{

		String grpTitle = AppGrpUtils.getGrpTextFromDB(i);
		if (null != grpTitle) {
			mItems.add(new ListItem(res, grpTitle,
						/*iconId, */appGrp));
		}
	}

    public AppGroupAdapter(Launcher launcher) {
        super();

        mInflater = (LayoutInflater) launcher.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        // Create default actions
        Resources res = launcher.getResources();
        
       mItems.add(new ListItem(res, R.string.AppGroupAdd, APP_GROUP_ADD));
	
		mItems.add(new ListItem(res, R.string.AppGroupAll,
					/*R.drawable.app_grp_all,*/ APP_GROUP_ALL));
		addListItem(res,0,APP_GROUP_0/*,R.drawable.app_grp_multi_media*/);
		addListItem(res,1,APP_GROUP_1/*,R.drawable.app_grp_others*/);
		addListItem(res,2,APP_GROUP_2/*,R.drawable.app_grp_tools*/);
		addListItem(res,3,APP_GROUP_3/*,R.drawable.app_grp_business_tools*/);
    }

	public View getView(int position, View convertView, ViewGroup parent) {
		ListItem item = (ListItem) getItem(position);

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.add_list_item, parent,
					false);
		}

		TextView textView = (TextView) convertView;
		textView.setTag(item);
		textView.setText(item.text);

		return convertView;
	}

    public int getCount() {
        return mItems.size();
    }

    public Object getItem(int position) {
        return mItems.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    
}
