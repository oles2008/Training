package com.iolab.sightlocator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.Activity;
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
	private Marker mSelectedMarker = null;
	private SightMarkerItem mSelectedItem = null;
	private ListView mSights = null;
	private ArrayList<SightMarkerItem> mSightListItems = null;
	private TextView mAddress = null;
	private static long mClusterClickCounter = 0;
	private int mCommonParentID = -1;
	private String mLanguage = null;

    OnTextFragmentClickListener mCallback;

    public interface OnTextFragmentClickListener{
        public void onTextFragmentLongClick();
    }
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			mClusterClickCounter = savedInstanceState.getLong(
					Tags.ON_MARKER_CLICK_COUNTER, 0);
			mSightListItems = savedInstanceState.getParcelableArrayList(Tags.SIGHT_ITEM_LIST);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View inflatedView = inflater.inflate(R.layout.text_fragment, container, false);
		mSights = (ListView) inflatedView.findViewById(R.id.listView);
		if (mSightListItems != null && !mSightListItems.isEmpty()) {
			mSights.setAdapter(new SightsAdapter(getActivity(),
					R.layout.sights_list_item, mSightListItems));
			mSights.setVisibility(View.VISIBLE);
		} else {
			mSights.setVisibility(View.GONE);
		}
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnTextFragmentClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTextFragmentClickListener");
        }
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
		if (mSelectedMarker!=null && marker.getPosition().equals(mSelectedMarker.getPosition())
				&& marker.getTitle().equals(mSelectedMarker.getTitle())) {
			return true;
		}
		Intent intent = new Intent(getActivity(), SightsIntentService.class);
		LatLng position = marker.getPosition();
		Bundle bundle = new Bundle();
		bundle.putDouble(Tags.POSITION_LAT, position.latitude);
		bundle.putDouble(Tags.POSITION_LNG, position.longitude);
		bundle.putLong(Tags.ON_MAP_CLICK_COUNTER,++mClusterClickCounter);
		intent.putExtra(SightsIntentService.ACTION,
				new GetTextOnMarkerClickAction(bundle));
		intent.putExtra(Tags.ON_MARKER_CLICK_COUNTER, mClusterClickCounter);
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
		mSelectedItem = null;
		mAddress.setVisibility(View.GONE);

	}

	@Override
	public void onMapClick(LatLng arg0) {
		// changes the image fragment to default (one pixel image)
		Resources res = getResources();
		FragmentManager fragmentManager = getFragmentManager();

		Utils.changeImageFragmentToOnePixel(res.getDrawable(R.drawable.one_pixel)
				.toString(), res, fragmentManager);
		mSelectedItem = null;
		mAddress.setVisibility(View.GONE);
		mSights.setVisibility(View.GONE);
		mSightListItems = null;
		//added on 22/4/15
		Intent intent = new Intent(getActivity(), SightsIntentService.class);
		Bundle bundle = new Bundle();
		bundle.putInt(Tags.COMMON_PARENT_ID,mCommonParentID);
		bundle.putLong(Tags.ON_MARKER_CLICK_COUNTER,++mClusterClickCounter);
		intent.putExtra(SightsIntentService.ACTION,
				new GetTextOnMarkerClickAction(bundle));
		getActivity().startService(intent);
		//selectedItem = item;
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
                mCallback.onTextFragmentLongClick();
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
		args.putLong(Tags.ON_MARKER_CLICK_COUNTER, mClusterClickCounter);
		args.putInt(Tags.SCROLL_Y, getScrollView().getScrollY());
		args.putParcelableArrayList(Tags.SIGHT_ITEM_LIST, mSightListItems);
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
		Log.d("MyLogs","description is "+bundle.getString(Tags.SIGHT_DESCRIPTION));
		if (bundle.getString(Tags.SIGHT_DESCRIPTION) != null) {
			getScrollView().scrollTo(0, 0);
			changeTextFragment(bundle.getString(Tags.SIGHT_DESCRIPTION));
		}

		if (bundle.getString(Tags.PATH_TO_IMAGE) != null) {
			changeImageFragmentUsingImageUri(bundle
					.getString(Tags.PATH_TO_IMAGE));
		} 
		
		//check if bundle has COMMON_PARENT_ID
		if (bundle.getInt(Tags.COMMON_PARENT_ID,-1) != -1) {
			mCommonParentID = bundle.getInt(Tags.COMMON_PARENT_ID);
		}
		
		if (bundle.getParcelableArrayList(Tags.SIGHT_ITEM_LIST) != null) {
			mSightListItems = new ArrayList<SightMarkerItem>(
					(Collection<? extends SightMarkerItem>) bundle
							.getParcelableArrayList(Tags.SIGHT_ITEM_LIST));
			SightsAdapter adapter = new SightsAdapter(getActivity(),
					R.layout.sights_list_item, mSightListItems);
			mSights.setAdapter(adapter);
			mSights.setVisibility((mSightListItems.size() > 0) ? View.VISIBLE
					: View.GONE);
		}
		
		if (bundle.getString(Tags.SIGHT_NAME) != null) {
			Log.d("MY_log", "messageFromTitle");
			Fragment textFragment = getActivity().getFragmentManager().findFragmentById(R.id.text_fragment);
			TextView object_title = (TextView) textFragment.getView().findViewById(R.id.text_view_object_title);
			object_title.setText(bundle.getString(Tags.SIGHT_NAME));

			}

         if (bundle.getString(Tags.SIGHT_ADDRESS) != null) {
	       
	       Fragment textFragment = getActivity().getFragmentManager().findFragmentById(R.id.text_fragment);
			TextView object_address = (TextView) textFragment.getView().findViewById(R.id.address);
			object_address.setText(bundle.getString(Tags.SIGHT_ADDRESS));
}
			
	}

	@Override
	public boolean onClusterItemClick(SightMarkerItem item) {
		Log.d("MyLogs", "onClusterItemClick");
		if (mSelectedItem != null
				&& item.getPosition().equals(mSelectedItem.getPosition())
				&& ((item.getTitle() == null) || (item.getTitle()
						.equals(mSelectedItem.getTitle())))) {
			return true;
		}
		Intent intent = new Intent(getActivity(), SightsIntentService.class);
		LatLng position = item.getPosition();
		Bundle bundle = new Bundle();
		bundle.putDouble(Tags.POSITION_LAT,position.latitude);
		bundle.putDouble(Tags.POSITION_LNG,position.longitude);
		bundle.putLong(Tags.ON_MAP_CLICK_COUNTER, ++mClusterClickCounter);
		intent.putExtra(SightsIntentService.ACTION,
				new GetTextOnMarkerClickAction(bundle));
		getActivity().startService(intent);
		mSelectedItem = item;


      /*  Fragment textFragmet = getActivity().getFragmentManager()
						.findFragmentById(R.id.text_fragment);
				TextView object_title = (TextView) textFragmet.getView().findViewById(R.id.text_view_object_title);
				object_title.setText(item.getTitle());   
		*/
		mSights.setVisibility(View.GONE);
		mSightListItems = null;
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
		args.putLong(Tags.ON_MARKER_CLICK_COUNTER, ++mClusterClickCounter);
		args.putParcelableArrayList(Tags.SIGHT_ITEM_LIST, new ArrayList<SightMarkerItem>(cluster.getItems()));
		intent.putExtra(SightsIntentService.ACTION,
				new GetTextOnMarkerClickAction(args));
		getActivity().startService(intent);
		
		mAddress.setVisibility(View.GONE);
		mSights.setVisibility(View.GONE);
		return false;
	}

}
