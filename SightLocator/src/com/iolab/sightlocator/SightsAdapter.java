package com.iolab.sightlocator;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SightsAdapter extends BaseAdapter {

	private Context mContext;
	private int layoutResourceId;
	private List<SightMarkerItem> mData;

	public SightsAdapter(Context context, int resource,
			List<SightMarkerItem> objects) {
		mContext = context;
		this.layoutResourceId = resource;
		mData = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		/*
		 * The convertView argument is essentially a "ScrapView" as described is
		 * Lucas post
		 * http://lucasr.org/2012/04/05/performance-tips-for-androids-listview/
		 * It will have a non-null value when ListView is asking you recycle the
		 * row layout. So, when convertView is not null, you should simply
		 * update its contents instead of inflating a new row layout.
		 */
		if (convertView == null) {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			convertView = inflater.inflate(layoutResourceId, parent, false);

			viewHolder = new ViewHolder();
			viewHolder.title = (TextView) convertView
					.findViewById(R.id.text_view_name_in_list);
			viewHolder.address = (TextView) convertView
					.findViewById(R.id.text_view_address_in_list);
			viewHolder.snippet = (TextView) convertView
					.findViewById(R.id.text_view_description_snippet_in_list);
			viewHolder.image = (ImageView) convertView
					.findViewById(R.id.image_view_in_list);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.title.setText(mData.get(position).getTitle());
		viewHolder.address.setText(mData.get(position).getAddress());
		viewHolder.snippet.setText(mData.get(position).getSnippet());
		String imageUri = mData.get(position).getImageURI();
		loadBitmap(
				imageUri,
				viewHolder.image,
				convertView.getResources().getDimensionPixelSize(
						R.dimen.list_view_image_width),
				convertView.getResources().getDimensionPixelSize(
						R.dimen.list_view_image_width));

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

	private void loadBitmap(String imageUri, ImageView imageView, int reqWidth,
			int reqHeight) {
		if (isPreviousTaskCancelled(imageUri, imageView)) {
			if (imageUri != null) {
				DecodeImageAsyncTask asyncTask = new DecodeImageAsyncTask(
						imageView, imageUri, reqWidth, reqHeight);
				imageView.setImageDrawable(new AsyncDrawable(mContext
						.getResources(), BitmapFactory.decodeResource(
						mContext.getResources(), R.drawable.bubble_shadow),
						asyncTask));
				asyncTask.execute();
			} else {
				imageView.setImageResource(R.drawable.bubble_shadow);
			}
		}
	}

	private boolean isPreviousTaskCancelled(String imagePath,
			ImageView imageView) {

		if (imageView != null) {
			Drawable currentDrawable = imageView.getDrawable();
			if (currentDrawable != null
					&& currentDrawable instanceof AsyncDrawable) {
				DecodeImageAsyncTask currentAsyncTask = ((AsyncDrawable) currentDrawable)
						.getAsyncTask();
				if (currentAsyncTask == null) {
					return true;
				}
				if (currentAsyncTask.getPath() != imagePath) {
					currentAsyncTask.cancel(true);
				} else {
					return false;
				}
			}
		}
		return true;
	}

}
