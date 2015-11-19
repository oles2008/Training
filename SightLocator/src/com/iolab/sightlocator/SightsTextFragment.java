package com.iolab.sightlocator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager.OnClusterClickListener;
import com.google.maps.android.clustering.ClusterManager.OnClusterItemClickListener;
import com.iolab.sightlocator.Appl.ViewUpdateListener;

public class SightsTextFragment extends Fragment implements OnMapClickListener,
		OnMapLongClickListener, OnClusterClickListener<SightMarkerItem>,
		OnClusterItemClickListener<SightMarkerItem>, ViewUpdateListener {

	private static final int ICON_SIZE = 200;
	
	private ListView mSights;
	private SightMarkerItem mSelectedItem;
	private ArrayList<SightMarkerItem> mSightListItems;
	private String mImagePath;
	private TextView mAddress;
	private TextView mTitle;
	private TextView mDescription;
	private ImageView mImage;
	private static long mClusterClickCounter = 0;
	private int mCommonParentID = -1;
	private String mLanguage;
	private Queue<DestinationEndPoint> mBackStack = new ArrayDeque<DestinationEndPoint>();

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
			mSelectedItem = savedInstanceState.getParcelable(Tags.SELECTED_ITEM);
			mImagePath = savedInstanceState.getString(Tags.PATH_TO_IMAGE);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View inflatedView = inflater.inflate(R.layout.text_fragment, container,
				false);
		mSights = (ListView) inflatedView.findViewById(R.id.listView);
		initializeListView();
		mAddress = (TextView) inflatedView.findViewById(R.id.address);
		mAddress.setVisibility(View.GONE);
		mTitle = (TextView) inflatedView
				.findViewById(R.id.text_view_object_title);
		mDescription = (TextView) inflatedView.findViewById(R.id.textView);
		mImage = (ImageView) inflatedView.findViewById(
				R.id.imageView);
		changeImageFragmentUsingImageUri(mImagePath);
		return inflatedView;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Inflate the menu; this adds items to the action bar if it is present.
		inflater.inflate(R.menu.sights_text_fragment_menu, menu);
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {

		case R.id.action_languages_dialog:
			int itemId=3;                  //TODO replace hardcoded value of itemId with automatically id of chosen item
//			showLanguagesDialog(itemId);
			return true;
			
		case R.id.action_back:
			navigateBack();
			return true;
		
		default:
			return super.onOptionsItemSelected(item);
		}
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
		mImagePath = uri;
		if (uri == null || uri.isEmpty()) {
			mImage.setImageBitmap(null);
			return;
		}
		mImage.setImageURI(Uri.parse(uri));
		Bitmap resizedBitmap = Utils.resizeBitmap(mImage, ICON_SIZE);
		mImage.setImageBitmap(resizedBitmap);
		//mImage.setTag(R.string.imageview_tag_uri, uri);
	}

	@Override
	public void onMapLongClick(LatLng arg0) {
		onMapClick(arg0);
	}

	@Override
	public void onMapClick(LatLng arg0) {
		cleanAllViews();
		navigateTo(mCommonParentID, false, true);
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
		args.putParcelable(Tags.SELECTED_ITEM, mSelectedItem);
		args.putString(Tags.PATH_TO_IMAGE, mImagePath);
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
		
		if (bundle.getInt(Tags.COMMON_PARENT_ID,-1) != -1) {
			mCommonParentID = bundle.getInt(Tags.COMMON_PARENT_ID);
		}
		
		if (bundle.getParcelableArrayList(Tags.SIGHT_ITEM_LIST) != null) {
			mSightListItems = new ArrayList<SightMarkerItem>(
					(Collection<? extends SightMarkerItem>) bundle
							.getParcelableArrayList(Tags.SIGHT_ITEM_LIST));
			initializeListView();
		}
		
		if (bundle.getInt(Tags.ID, -1) != -1) {
			mSelectedItem = new SightMarkerItem(bundle.getInt(Tags.ID));
		}
		
		if (bundle.getString(Tags.SIGHT_NAME) != null) {
			String title = bundle.getString(Tags.SIGHT_NAME);
			mTitle.setText(title);
			mSelectedItem.setTitle(title);
		}

		if (bundle.getString(Tags.SIGHT_ADDRESS) != null) {
			String address = bundle.getString(Tags.SIGHT_ADDRESS);
			mAddress.setText(address);
			mSelectedItem.setAddress(address);
		}
		
		if (bundle.getParcelable(Tags.SIGHT_POSITION) != null) {
			LatLng position = bundle.getParcelable(Tags.SIGHT_POSITION);
			mSelectedItem.setPosition(position);
		}
		
		if (bundle.getIntArray(Tags.PARENT_IDS) != null) {
			int[] parentIDs = bundle.getIntArray(Tags.PARENT_IDS);
			mSelectedItem.setParentIDs(parentIDs);
		}
		
		if(bundle.getBoolean(Tags.SHOW_ON_MAP)){
			Set<SightMarkerItem> itemsToBeShownOnMap = new HashSet<SightMarkerItem>();
			itemsToBeShownOnMap.add(mSelectedItem);
			if(mSightListItems != null){
				itemsToBeShownOnMap.addAll(mSightListItems);
			}
			Appl.notifyNavigationUpdates(itemsToBeShownOnMap);
		}
			
	}

	@Override
	public boolean onClusterItemClick(SightMarkerItem item) {
		int id = item.getID();
		if ((mSelectedItem == null) || (mSelectedItem.getID() != id)) {
			cleanAllViews();
			navigateTo(id, false, true);
			mSights.setVisibility(View.GONE);
			mSightListItems = null;
			mAddress.setVisibility(View.VISIBLE);
		}
		return true;
	}

	@Override
	public boolean onClusterClick(Cluster<SightMarkerItem> cluster) {
		//TODO avoid sending the same request if the selected marker is the same
		cleanAllViews();
		List<int[]>parentIDs = new ArrayList<int[]>();
		for(SightMarkerItem item: cluster.getItems()){
			parentIDs.add(item.getParentIDs());
		}
		int clusterCommonParentId = ItemGroupAnalyzer.findCommonParent(parentIDs, 0);
		navigateTo(clusterCommonParentId, false, cluster.getItems(), true);
		return false;
	}
	
	private void cleanAllViews(){
		 changeImageFragmentUsingImageUri(null);
		 changeTextFragment(null);
		 mAddress.setText(null);
		 mTitle.setText(null);
		 mDescription.setText(null);
		 mSights.setVisibility(View.GONE);
		 mSightListItems = null; 
	}

	/**
	 * Navigates to the given item.
	 *
	 * @param id the id of the item
	 * @param showOnMap whether the item (or items) should be show and selected on map
	 * @param addToBackStack whether this navigation action should be added to back stack
	 * @return the {@link DestinationEndPoint} representing this navigation action
	 */
	private DestinationEndPoint navigateTo(int id, boolean showOnMap, boolean addToBackStack) {
		return navigateTo(id, showOnMap, null, addToBackStack);
	}

	/**
	 * Navigates to the given item.
	 *
	 * @param id the id of the item
	 * @param showOnMap whether the item (or items) should be show and selected on map
	 * @param items the multiple items to be shown, if any
	 * @param addToBackStack whether this navigation action should be added to back stack
	 * @return the {@link DestinationEndPoint} representing this navigation action
	 */
	private DestinationEndPoint navigateTo(int id, boolean showOnMap,
			Collection<SightMarkerItem> items, boolean addToBackStack) {
		// TODO avoid sending the same request if the selected marker is the
		// same
		cleanAllViews();
		Intent intent = new Intent(getActivity(), SightsIntentService.class);
		Bundle bundle = new Bundle();
		bundle.putInt(Tags.ID, id);
		if ((items != null) && !items.isEmpty()) {
			bundle.putParcelableArrayList(Tags.SIGHT_ITEM_LIST,
					new ArrayList<SightMarkerItem>(items));
		}
		bundle.putLong(Tags.ON_MARKER_CLICK_COUNTER, ++mClusterClickCounter);
		bundle.putBoolean(Tags.SHOW_ON_MAP, showOnMap);
		intent.putExtra(SightsIntentService.ACTION,
				new GetTextOnMarkerClickAction(bundle));
		getActivity().startService(intent);
		DestinationEndPoint destinationEndPoint = new DestinationEndPoint(id, items);
		if(addToBackStack){
			mBackStack.add(destinationEndPoint);
		}
		return destinationEndPoint;
	}
	
	private void initializeListView() {
		if (mSightListItems != null && !mSightListItems.isEmpty()) {
			mSights.setAdapter(new SightsAdapter(getActivity(),
					R.layout.sights_list_item, mSightListItems));
			mSights.setVisibility(View.VISIBLE);
			mSights.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					SightMarkerItem selectedItem = mSightListItems.get(position);
					navigateTo(selectedItem.getID(), true, true);
				}
			});
		} else {
			mSights.setVisibility(View.GONE);
		}
	}
	
	/**
	 * Navigate back.
	 */
	private void navigateBack() {
		if(!mBackStack.isEmpty()){
			DestinationEndPoint backItem = mBackStack.poll();
			navigateTo(backItem.getID(), true, backItem.getClusteredItems(), false);
		}
	}

}
