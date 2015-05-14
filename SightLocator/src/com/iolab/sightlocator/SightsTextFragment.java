package com.iolab.sightlocator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

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
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
		registerLinearLayoutClickListener();
        registerBackButtonListener();
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
		Fragment textFragment = getFragmentManager()
				.findFragmentById(R.id.text_fragment);
		return (TextView) textFragment.getView().findViewById(R.id.textView);
	}

    private LinearLayout getLinearLayout() {
        Fragment textFragment = getActivity().getFragmentManager()
                .findFragmentById(R.id.text_fragment);
        return (LinearLayout) textFragment.getView().findViewById(R.id.linear_layout_child_of_scroll);
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

    private void registerLinearLayoutClickListener() {
        LinearLayout linearLayout = getLinearLayout();
        linearLayout.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                /*** changing text to full screen using Fragment transaction ***/
                Fragment mapFragment = getFragmentManager().findFragmentById(R.id.map_fragment);

                if (mapFragment.isVisible()) {
                    getTextView().setTextSize(24);
                    getFragmentManager().beginTransaction().hide(mapFragment).addToBackStack(null).commit();
                    MainActivity.mapFragmentVisible = false;
                } else {
                    getTextView().setTextSize(14);
                    getFragmentManager().beginTransaction().show(mapFragment).addToBackStack(null).commit();
                    MainActivity.mapFragmentVisible = true;
                }
                return true;
            }
        });
    }

    private void registerBackButtonListener() {
        getFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    public void onBackStackChanged() {
                        Fragment mMapFragment = getFragmentManager().findFragmentById(R.id.map_fragment);
                        if (mMapFragment.isVisible()) {
                            MainActivity.mapFragmentVisible = true;
                        } else {
                            MainActivity.mapFragmentVisible = false;
                        }
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
		ScrollView scr;
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
			changeImageFragmentUsingImageUri(bundle
					.getString(Tags.PATH_TO_IMAGE));
		}
	}

	@Override
	public boolean onClusterItemClick(SightMarkerItem item) {
		if (selectedItem != null
				&& item.getPosition().equals(selectedItem.getPosition())
				&& ((item.getTitle() == null) || (item.getTitle()
						.equals(selectedItem.getTitle())))) {
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
		SightsAdapter adapter = new SightsAdapter(getActivity(), R.layout.sights_list_item, new ArrayList<SightMarkerItem>(cluster.getItems()));
		
//			LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, 100);
//			LinearLayout lin = (LinearLayout) getView().findViewById(R.id.text_fragment);
//			lin.addView(sights, layoutParams);
			
			Log.d("MyLogs", "sights == null: "+(sights==null));
		
		sights.setAdapter(adapter);
		sights.setVisibility(View.VISIBLE);
		mAddress.setVisibility(View.GONE);
		return false;
	}

}