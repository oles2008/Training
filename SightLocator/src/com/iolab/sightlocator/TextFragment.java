package com.iolab.sightlocator;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class TextFragment extends Fragment {
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		return inflater.inflate(R.layout.text_fragment, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if (savedInstanceState != null) {
			String uri = savedInstanceState.getString("uri");
			Log.d("MSG", " > URI 1 > " + uri);
			
			if (null != uri) {
				ImageView imageView = (ImageView) getActivity().findViewById(R.id.imageView);
				imageView.setImageURI(Uri.parse(uri));
				if (!uri.contains("onePixel")) {
					imageView.setImageBitmap(new SightsMapFragment().resizeBitmap100by100(imageView));
				}
				
				imageView.setTag(R.string.imageview_tag_uri, uri);
			}
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle args){
		super.onSaveInstanceState(args);		
		ImageView imageView = (ImageView) getActivity().findViewById(R.id.imageView);
		String uri = (String) imageView.getTag(R.string.imageview_tag_uri);
		if (null != uri) {
			args.putString("uri", uri);
		}
		Log.d("MSG", " > URI 2 > " + uri);
	}
}
