package com.iolab.sightlocator;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.iolab.sightlocator.Appl.ViewUpdateListener;

import com.iolab.sightlocator.Tags;

public class SightsTextFragment extends Fragment implements OnMapClickListener,
		OnMapLongClickListener, OnMarkerClickListener, ViewUpdateListener {

	private final int ICON_SIZE = 200;
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
		return inflater.inflate(R.layout.text_fragment, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (savedInstanceState != null) {
			String uri = savedInstanceState.getString("uri");

			if (null != uri) {
				ImageView imageView = (ImageView) getActivity().findViewById(
						R.id.imageView);
				imageView.setImageURI(Uri.parse(uri));
				if (!uri.contains(Tags.ONE_PIXEL_JPEG)) {
					Bitmap resizedBitmap = resizeBitmap(imageView, ICON_SIZE);
					if (resizedBitmap != null) {
						imageView.setImageBitmap(resizedBitmap);
					}
				}

				imageView.setTag(R.string.imageview_tag_uri, uri);
			}
		}
		registerImageViewClickListener();
		registerTexViewClickListener();
	}

	@Override
	public void onResume() {
		super.onResume();
		Appl.subscribeForMarkerClickUpdates(this);
		Appl.subscribeForMapClickUpdates(this);
		Appl.subscribeForMapLongClickUpdates(this);
		Appl.subscribeForViewUpdates(this);
	}

	protected Bitmap resizeBitmap(ImageView imageView, int newWidth) {
		// get the bitmap from Image View
		Drawable drawable = imageView.getDrawable();
		if (drawable == null) {
			Resources res = getResources();
			changeImageFragmentToOnePixel(res.getDrawable(R.drawable.one_pixel)
					.toString());
			return null;
		}
		Bitmap bitmapFromImageView = ((BitmapDrawable) drawable).getBitmap();
		// get Width and Height
		int originalWidth = bitmapFromImageView.getWidth();
		int originalHeight = bitmapFromImageView.getHeight();
		// find the proportion (aspect ratio)
		float scale = (float) newWidth / originalWidth;
		// find new Height keeping aspect ratio
		int newHeight = (int) Math.round(originalHeight * scale);
		// get new resized Bitmap
		Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmapFromImageView,
				newWidth, newHeight, true);
		return resizedBitmap;
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
		ImageView imageView = getImageView();

		imageView.setImageURI(Uri.parse(uri));

		if (!uri.contains(Tags.ONE_PIXEL_JPEG)) {
			Bitmap resizedBitmap = resizeBitmap(imageView, ICON_SIZE);
			if (resizedBitmap != null) {
				imageView.setImageBitmap(resizedBitmap);
			}
		}

		imageView.setTag(R.string.imageview_tag_uri, uri);
	}

	private void changeImageFragmentToOnePixel(String uri) {
		ImageView imageView = getImageView();

		Resources res = getResources();
		Drawable drawable = res.getDrawable(R.drawable.one_pixel);
		imageView.setImageDrawable(drawable);
		imageView.setTag(R.string.imageview_tag_uri, uri);
	}

	protected ImageView getImageView() {
		Fragment fragment = getFragmentManager().findFragmentById(
				R.id.text_fragment);
		ImageView imageView = (ImageView) fragment.getView().findViewById(
				R.id.imageView);

		return imageView;
	}

	public boolean onMarkerClick(final Marker marker) {
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
		changeImageFragmentToOnePixel(res.getDrawable(R.drawable.one_pixel)
				.toString());

	}

	@Override
	public void onMapClick(LatLng arg0) {
		String loremIpsum = getString(R.string.lorem_ipsum);
		// changes the text fragment to default (lorem ipsum text)
		changeTextFragment(loremIpsum);
		// changes the image fragment to default (one pixel image)
		Resources res = getResources();
		changeImageFragmentToOnePixel(res.getDrawable(R.drawable.one_pixel)
				.toString());
	}

	private void registerImageViewClickListener() {
		ImageView imageView = getImageView();
		imageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("MSG", "Image View click");
			}
		});
	}

	private void registerTexViewClickListener() {
		final TextView textView = getTextView();
		final ImageView imageView = getImageView();
		
		
		textView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String image_tag = null;
				if (imageView.getTag(R.string.imageview_tag_uri) != null) {
					image_tag = imageView.getTag(R.string.imageview_tag_uri).toString();
				}
				
				String text = textView.getText().toString();
				Intent intent =  new Intent(getActivity(), DisplayFullScreenTextActivity.class);
				intent.putExtra(Tags.EXTRA_TEXT, text);
				intent.putExtra("image", image_tag);
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
			args.putString("uri", uri);
		}
		args.putLong(Tags.ON_MARKER_CLICK_COUNTER, markerClickCounter);
	}

	@Override
	public void onPause() {
		super.onPause();
		Appl.unsubscribeFromMarkerClickUpdates(this);
		Appl.unsubscribeFromMapClickUpdates(this);
		Appl.unsubscribeFromMapLongClickUpdates(this);
		Appl.unsubscribeFromViewUpdates(this);
	}

	@Override
	public void onUpdateView(Bundle bundle) {
		if (bundle.getString(Tags.SIGHT_DESCRIPTION) != null) {
			changeTextFragment(bundle.getString(Tags.SIGHT_DESCRIPTION));
		}

		if (bundle.getString(Tags.PATH_TO_IMAGE) != null) {
			Log.d("MSG",
					"Tags.PATH_TO_IMAGE > "
							+ bundle.getString(Tags.PATH_TO_IMAGE));
			changeImageFragmentUsingImageUri(bundle
					.getString(Tags.PATH_TO_IMAGE));
		}
	}

}
