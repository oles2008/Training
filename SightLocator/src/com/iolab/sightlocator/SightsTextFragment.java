package com.iolab.sightlocator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager.OnClusterClickListener;
import com.google.maps.android.clustering.ClusterManager.OnClusterItemClickListener;
import com.iolab.sightlocator.Appl.ViewUpdateListener;

public class SightsTextFragment extends Fragment implements OnMapClickListener,
		OnMapLongClickListener, OnMarkerClickListener,
		OnClusterClickListener<SightMarkerItem>,
		OnClusterItemClickListener<SightMarkerItem>, ViewUpdateListener {

	private final int ICON_SIZE = 200;
	private Marker selectedMarker = null;
	private SightMarkerItem selectedItem = null;
	private ListView sights = null;
	private TextView mAddress = null;
	private static long mClusterClickCounter = 0;
	public static long markerClickCounter = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			markerClickCounter = savedInstanceState.getLong(
					Tags.ON_MARKER_CLICK_COUNTER, 0);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View inflatedView = inflater.inflate(R.layout.text_fragment, container, false);
		sights = (ListView) inflatedView.findViewById(R.id.listView);
		sights.setVisibility(View.GONE);
		mAddress = (TextView) inflatedView.findViewById(R.id.address);
		mAddress.setVisibility(View.GONE);
		return inflatedView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (savedInstanceState != null) {
			String uri = savedInstanceState.getString(Tags.URI);

			if (null != uri) {
				ImageView imageView = (ImageView) getActivity().findViewById(
						R.id.imageView);
				imageView.setImageURI(Uri.parse(uri));
				if (!uri.contains(Tags.ONE_PIXEL_JPEG)) {
					Bitmap resizedBitmap = Utils.resizeBitmap(imageView, ICON_SIZE);
					if (resizedBitmap != null) {
						imageView.setImageBitmap(resizedBitmap);
					}
				}

				imageView.setTag(R.string.imageview_tag_uri, uri);
			}
			
			final int scrollY = savedInstanceState.getInt(Tags.SCROLL_Y);
			final ScrollView scr = getScrollView();
			scr.post(new Runnable() {
			    @Override
			    public void run() {
			        scr.scrollTo(0, scrollY);
			    } 
			});
		}
		registerImageViewClickListener();
		registerTexViewClickListener();
	}

	@Override
	public void onResume() {
		super.onResume();
		//Appl.subscribeForMarkerClickUpdates(this);
		Appl.subscribeForClusterItemClickUpdates(this);
		Appl.subscribeForClusterClickUpdates(this);
		Appl.subscribeForMapClickUpdates(this);
		Appl.subscribeForMapLongClickUpdates(this);
		Appl.subscribeForViewUpdates(this);
	}

	/**
	 * Change text in text fragment to new one.
	 * 
	 * @param newText
	 *            the new text to be displayed
	 */
	private void changeTextFragment(String newText) {
		TextView textView = getTextView();
		textView.setText(newText);
	}

	private TextView getTextView() {
		Fragment textFragmet = getActivity().getFragmentManager()
				.findFragmentById(R.id.text_fragment);
		TextView textView = (TextView) textFragmet.getView().findViewById(
				R.id.textView);

		return textView;
	}

	/**
	 * On markers click change image fragment from default (one pixel image) to
	 * an icon, using image uri. The uri is stored in ImageView tag and then
	 * used in onSaveInstanceState() and onActivityCreated methods
	 * 
	 * @param uri
	 *            the uri, path to device Download folder
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private void changeImageFragmentUsingImageUri(String uri) {
		FragmentManager fragmentManager = getFragmentManager();
		ImageView imageView = Utils.getImageView(fragmentManager);
		Log.d("MyLogs","image uri: "+(uri));

		imageView.setImageURI(Uri.parse(uri));

//		if (!uri.contains(Tags.ONE_PIXEL_JPEG)) {
			Log.d("MyLogs","not one_pixel ");
			Bitmap resizedBitmap = Utils.resizeBitmap(imageView, ICON_SIZE);
			//Bitmap resizedBitmap = resizeBitmap(imageView, ICON_SIZE);
			imageView.setImageBitmap(resizedBitmap);
//		}

		imageView.setTag(R.string.imageview_tag_uri, uri);
	}

//	private void changeImageFragmentToOnePixel(String uri) {
//		ImageView imageView = getImageView();
//
//		Resources res = getResources();
//		Drawable drawable = res.getDrawable(R.drawable.one_pixel);
//		imageView.setImageDrawable(drawable);
//		imageView.setTag(R.string.imageview_tag_uri, uri);
//	}

//	protected ImageView getImageView() {
//		Fragment fragment = getFragmentManager().findFragmentById(
//				R.id.text_fragment);
//		ImageView imageView = (ImageView) fragment.getView().findViewById(
//				R.id.imageView);
//
//		return imageView;
//	}

	@Deprecated
	public boolean onMarkerClick(final Marker marker) {
		if (selectedMarker!=null && marker.getPosition().equals(selectedMarker.getPosition())
				&& marker.getTitle().equals(selectedMarker.getTitle())) {
			return true;
		}
		Intent intent = new Intent(getActivity(), SightsIntentService.class);
		LatLng position = marker.getPosition();
		intent.putExtra(SightsIntentService.ACTION,
				new GetTextOnMarkerClickAction(position, ++markerClickCounter));
		intent.putExtra(Tags.ON_MARKER_CLICK_COUNTER, markerClickCounter);
		getActivity().startService(intent);

		return true;
	}

	@Override
	public void onMapLongClick(LatLng arg0) {
		String loremIpsum = getString(R.string.lorem_ipsum);
		// changes the text fragment to default (lorem ipsum text)
		changeTextFragment(loremIpsum);
		// changes the image fragment to default (one pixel image)
		Resources res = getResources();
		FragmentManager fragmentManager = getFragmentManager();

		Utils.changeImageFragmentToOnePixel(res.getDrawable(R.drawable.one_pixel)
				.toString(), res, fragmentManager);
		selectedItem = null;
		mAddress.setVisibility(View.GONE);

	}

	@Override
	public void onMapClick(LatLng arg0) {
		String loremIpsum = getString(R.string.lorem_ipsum);
		// changes the text fragment to default (lorem ipsum text)
		changeTextFragment(loremIpsum);
		// changes the image fragment to default (one pixel image)
		Resources res = getResources();
		FragmentManager fragmentManager = getFragmentManager();

		Utils.changeImageFragmentToOnePixel(res.getDrawable(R.drawable.one_pixel)
				.toString(), res, fragmentManager);
		selectedItem = null;
		mAddress.setVisibility(View.GONE);
	}

	private void registerImageViewClickListener() {
		FragmentManager fragmentManager = getFragmentManager();
		ImageView imageView = Utils.getImageView(fragmentManager);
		imageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("MSG", "Image View click");
			}
		});
	}

	private void registerTexViewClickListener() {
		final TextView textView = getTextView();
		FragmentManager fragmentManager = getFragmentManager();
		final ImageView imageView = Utils.getImageView(fragmentManager);
		
		
		textView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String imageTag = null;
				if (imageView.getTag(R.string.imageview_tag_uri) != null) {
					imageTag = imageView.getTag(R.string.imageview_tag_uri).toString();
				}
				
				String text = textView.getText().toString();
				Intent intent =  new Intent(getActivity(), DisplayFullScreenTextActivity.class);
				intent.putExtra(Tags.EXTRA_TEXT, text);
				intent.putExtra(Tags.PATH_TO_IMAGE, imageTag);
				intent.putExtra("layout", R.layout.text_fragment);
				startActivity(intent);
			}
		});
	}

	@Override
	public void onSaveInstanceState(Bundle args) {
		super.onSaveInstanceState(args);
		ImageView imageView = (ImageView) getActivity().findViewById(
				R.id.imageView);
		String uri = (String) imageView.getTag(R.string.imageview_tag_uri);
		if (null != uri) {
			args.putString(Tags.URI, uri);
		}
		args.putLong(Tags.ON_MARKER_CLICK_COUNTER, markerClickCounter);
		args.putInt(Tags.SCROLL_Y, getScrollView().getScrollY());
	}

	@Override
	public void onPause() {
		super.onPause();
		//Appl.unsubscribeFromMarkerClickUpdates(this);
		Appl.unsubscribeFromClusterItemClickUpdates(this);
		Appl.unsubscribeFromClusterClickUpdates(this);
		Appl.unsubscribeFromMapClickUpdates(this);
		Appl.unsubscribeFromMapLongClickUpdates(this);
		Appl.unsubscribeFromViewUpdates(this);
	}
	
	private ScrollView getScrollView() {
		ScrollView scr = null;
		try{
			scr = (ScrollView) getView();
		}catch(ClassCastException e){
			scr = (ScrollView) getView().findViewById(R.id.scrollView);
		}
		return scr;
	}

	@Override
	public void onUpdateView(Bundle bundle) {
		if (bundle.getString(Tags.SIGHT_DESCRIPTION) != null) {
			getScrollView().scrollTo(0, 0);
			changeTextFragment(bundle.getString(Tags.SIGHT_DESCRIPTION));
		}

		if (bundle.getString(Tags.PATH_TO_IMAGE) != null) {
//			Log.d("MSG", "Tags.PATH_TO_IMAGE > " + bundle.getString(Tags.PATH_TO_IMAGE));
			changeImageFragmentUsingImageUri(bundle
					.getString(Tags.PATH_TO_IMAGE));
		}
		
		if (bundle.getParcelableArrayList(Tags.SIGHT_ITEM_LIST) != null) {
			SightsAdapter adapter = new SightsAdapter(
					getActivity(),
					R.layout.sights_list_item,
					new ArrayList<SightMarkerItem>(
							(Collection<? extends SightMarkerItem>) bundle
									.getParcelableArrayList(Tags.SIGHT_ITEM_LIST)));
			sights.setAdapter(adapter);
			sights.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public boolean onClusterItemClick(SightMarkerItem item) {
		if (selectedItem!=null && item.getPosition().equals(selectedItem.getPosition())
				&& item.getTitle().equals(selectedItem.getTitle())) {
			return true;
		}
		Intent intent = new Intent(getActivity(), SightsIntentService.class);
		LatLng position = item.getPosition();
		intent.putExtra(SightsIntentService.ACTION,
				new GetTextOnMarkerClickAction(position, ++markerClickCounter));
		intent.putExtra(Tags.ON_MARKER_CLICK_COUNTER, markerClickCounter);
		getActivity().startService(intent);
		selectedItem = item;


        Fragment textFragmet = getActivity().getFragmentManager()
						.findFragmentById(R.id.text_fragment);
				TextView object_title = (TextView) textFragmet.getView().findViewById(R.id.text_view_object_title);
				object_title.setText(item.getTitle());
		sights.setVisibility(View.GONE);
		mAddress.setVisibility(View.VISIBLE);
		
		return true;
	}

	@Override
	public boolean onClusterClick(Cluster<SightMarkerItem> cluster) {
		Intent intent = new Intent(getActivity(), SightsIntentService.class);
		List<int[]>parentIDs = new ArrayList<int[]>();
		for(SightMarkerItem item: cluster.getItems()){
			parentIDs.add(item.getParentIDs());
		}
		int clusterCommonParentId = ItemGroupAnalyzer.findCommonParent(parentIDs, 0);
		Bundle args = new Bundle();
		args.putInt(Tags.COMMON_PARENT_ID, clusterCommonParentId);
		args.putLong(Tags.ON_MARKER_CLICK_COUNTER, ++markerClickCounter);
		intent.putExtra(SightsIntentService.ACTION,
				new GetTextOnMarkerClickAction(args));
		getActivity().startService(intent);
		
		mAddress.setVisibility(View.GONE);
		return false;
	}

}
