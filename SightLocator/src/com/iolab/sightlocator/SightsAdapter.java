package com.iolab.sightlocator;

import java.util.List;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SightsAdapter extends BaseAdapter {
	
	private Context context;
	private int layoutResourceId;
	private List<SightMarkerItem> mData;

	public SightsAdapter(Context context, int resource, List<SightMarkerItem> objects) {
		this.context=context;
		this.layoutResourceId=resource;
		mData = objects;
	}
	
	 @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	 
	        /*
	         * The convertView argument is essentially a "ScrapView" as described is Lucas post
	         * http://lucasr.org/2012/04/05/performance-tips-for-androids-listview/
	         * It will have a non-null value when ListView is asking you recycle the row layout.
	         * So, when convertView is not null, you should simply update its contents instead of inflating a new row layout.
	         */
	        if(convertView==null){
	            // inflate the layout
	            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
	            convertView = inflater.inflate(layoutResourceId, parent, false);
	        }
	        
	        TextView title = (TextView) convertView.findViewById(R.id.text_view_name_in_list);
	        TextView address = (TextView) convertView.findViewById(R.id.text_view_address_in_list);
	        TextView snippet = (TextView) convertView.findViewById(R.id.text_view_description_snippet_in_list);
	        
	        title.setText(mData.get(position).getTitle());
	        //address.setText(data.get(position).get);
	        snippet.setText(mData.get(position).getSnippet());
	         
	        // object item based on the position
	        SightMarkerItem item = mData.get(position);
	         
	        // get the TextView and then set the text (item name) and tag (item ID) values
//	        TextView textViewItem = (TextView) convertView.findViewById
//	        textViewItem.setText(item.getTitle());
	        //textViewItem.setTag(objectItem.itemId);
	 
	        return convertView;
	         
	    }

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

}
