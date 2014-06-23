package com.iolab.sightlocator;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TextFragment extends Fragment {
	@Override
	public void onCreate(Bundle savedInstanceState){
		Log.d("MyLogs","onCreate started");
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		Log.d("MyLogs","onCreateView started");
		return inflater.inflate(R.layout.text_fragment, container, false);
	}
}
