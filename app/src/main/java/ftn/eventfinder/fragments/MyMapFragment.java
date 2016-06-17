package ftn.eventfinder.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ftn.eventfinder.R;
import ftn.eventfinder.entities.Event_db;
import ftn.eventfinder.sync.SyncService;


public class MyMapFragment extends Fragment implements OnMapReadyCallback {

	public static int REQUEST_TAG_FILTER = 0;
	public static String TAG_FILTER = "TAG_FILTER";
	private ArrayList<String> tagFilter;

	private GoogleMap map;
	private SupportMapFragment mMapFragment;
	private android.support.v7.app.AlertDialog dialog;

	private Marker home;
	private HashMap<Marker, Event_db> markers;
	private HashMap<Event_db, Marker> markersInv;
	LatLng GlobalLocVar;
	private ArrayList<Marker> markersInLocation = new ArrayList<Marker>();
	private int markersPosition=0;
	boolean zoom=false;
	Marker previous=null;
	private FloatingActionButton fabn;
	private FloatingActionButton fabp;
	private Marker currentMarker1;
	private Marker currentMarker;
	String lastEventId;
	private boolean windowOpen=false;
	// za cuvanje pozicije
	private LatLng mapPosition;
	private float mZoom;
	private double mLat;
	private double mLng;
	private boolean firstZoomFromMain=false;
	private boolean cluster;

	public static MyMapFragment newInstance() {

		MyMapFragment mpf = new MyMapFragment();

		//mpf.markers = new HashMap<Marker, ReviewObject>();

		return mpf;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		//createMapFragmentAndInflate();
		Log.i("save", "onCreate()");
		if (savedInstanceState != null) {
			tagFilter = savedInstanceState.getStringArrayList(TAG_FILTER);
			//mMapFragment.setRetainInstance(true);
			mZoom=savedInstanceState.getFloat("zoom");
			lastEventId= savedInstanceState.getString("eventId");
			if (lastEventId!=null) {
				windowOpen=true;
				markersPosition= savedInstanceState.getInt("position");
				cluster= savedInstanceState.getBoolean("cluster");
				Log.i("save", "last Id loaded is " + lastEventId);
			}else{
				mLat= savedInstanceState.getDouble("lat");
						mLng=savedInstanceState.getDouble("lng");
				mapPosition=new LatLng(savedInstanceState.getDouble("lat"),savedInstanceState.getDouble("lng"));
				Log.i("save", "Map position loaded is" + String.valueOf(savedInstanceState.getDouble("lat")) + String.valueOf(savedInstanceState.getDouble("lng")));
				//Log.i("save", "Map position loaded is" + String.valueOf(mapPosition));
				//double lat= 0.0;
				//double lng= 0.0;
				//if(savedInstanceState.getDouble("lat")==lat && savedInstanceState.getDouble("lat")==lng) firstZoomFromMain=true;
			}

		} else {
			tagFilter = new ArrayList<String>();
			Log.i("save", " first time starting savedInstanceState is null");
			firstZoomFromMain=true;
		}

/*		Button button = new Button(this.getActivity());
		button.setText("Next");
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
		getActivity().addContentView(button, params);

		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {


				// TODO Auto-generated method stub

			}
		});*/



	}


	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putStringArrayList(TAG_FILTER, tagFilter);
		Log.i("save", "onSaveInstanceState()");

		Log.i("save", "windows is open? " + windowOpen);


			if(windowOpen) {
				outState.putString("eventId", lastEventId);
				outState.putInt("position",markersPosition);
				outState.putBoolean("cluster", cluster);
				Log.i("save", "Last event saved from marker " +lastEventId);
				if(map!=null) {
					outState.putFloat("zoom", map.getCameraPosition().zoom);
				}else{
					outState.putFloat("zoom", mZoom);
				}

		}else{
			if(map!=null) {
				outState.putString("eventId", null);


					mapPosition = map.getCameraPosition().target;
					outState.putDouble("lat", mapPosition.latitude);
					outState.putDouble("lng", mapPosition.longitude);
					outState.putFloat("zoom", map.getCameraPosition().zoom);
					Log.i("save", "map position saved " + String.valueOf(mapPosition));

			}else{
				outState.putDouble("lat", mLat);
				outState.putDouble("lng", mLng);
				outState.putFloat("zoom",mZoom);
				Log.i("save", "map position saved " + String.valueOf(mLat));
			}
		}

	}

	@Override
	public void onResume() {
		Log.i("save", "onResume");
		// TODO Auto-generated method stub
		super.onResume();
		// Toast.makeText(getActivity(), "onResume()",
		// Toast.LENGTH_SHORT).show();

//		getActivity().getActionBar().setTitle(R.string.home);
		setHasOptionsMenu(true);

		//Toast.makeText(getActivity(), "onResume()", Toast.LENGTH_SHORT).show();
		long minTime = 60000;
		float minDistance = 15;

		//createMapFragmentAndInflate();




		if(markers == null)
		{
			markers = new HashMap<Marker, Event_db>();
			markersInv = new HashMap<Event_db, Marker>();
		}

		//navigation drawer highlighting
		NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
		navigationView.setCheckedItem(R.id.nav_map);



		//localReceiver
		LocalBroadcastManager.getInstance(this.getActivity()).registerReceiver(mReceiver, new IntentFilter("syncResponse"));
		Log.i("save", "cluster: " + String.valueOf(cluster));

	}


	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		createMapFragmentAndInflate();
	}

	public BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {


			GlobalLocVar = new LatLng( intent.getDoubleExtra("lat",0), intent.getDoubleExtra("lng",0));
			reactToServerBroadcast(GlobalLocVar);
			//refreshMGoogleMap(currentLocation);
		}


	};

	public void reactToServerBroadcast(LatLng loc){
		List<Event_db> eve=new Select().from(Event_db.class).orderBy("eventStarttime ASC").execute();
		if(map!=null){
		for (Event_db e : eve) {
			LatLng lokacija = new LatLng(e.getVenueLocation().getLatitude(), e.getVenueLocation().getLongitude());

			if (!markers.containsValue(e)) {

				Marker marker = map.addMarker(new MarkerOptions()
						.icon(BitmapDescriptorFactory
								.defaultMarker(BitmapDescriptorFactory.HUE_RED))
						.position(lokacija));

				markers.put(marker, e);
				markersInv.put(e, marker);


				}
			}
		}

		if(firstZoomFromMain){
			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(loc).zoom(14).build();

			map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
			firstZoomFromMain=false;
		}
	}
	public void refreshMGoogleMap(LatLng loc){
		Log.i("save", "refreshMGoogleMap()");
		if(map!=null) {
			boolean helper=false;
			List<Event_db> eve=new Select().from(Event_db.class).orderBy("eventStarttime ASC").execute();

			for (Event_db e : eve) {
				LatLng lokacija = new LatLng(e.getVenueLocation().getLatitude(), e.getVenueLocation().getLongitude());

				if(!markers.containsValue(e)) {

					Marker marker = map.addMarker(new MarkerOptions()
							.icon(BitmapDescriptorFactory
									.defaultMarker(BitmapDescriptorFactory.HUE_RED))
							.position(lokacija));

					if(e.getEventId().equals(lastEventId)){
						currentMarker=marker;
						helper=true;



					}
		/*			else if(loc!=null) {


						if (!zoom) {

							//map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 10));

							CameraPosition cameraPosition = new CameraPosition.Builder()
									.target(loc).zoom(14).build();

							map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

							zoom = true;
						}
					}*/

					markers.put(marker, e);
					markersInv.put(e, marker);


					}






				}
				if(helper){
					//Log.i("save", "RefreshMap current marker is " + markers.get(currentMarker).getEventId());
					//CameraPosition cameraPosition = new CameraPosition.Builder()
							//.target(currentMarker.getPosition()).zoom(mZoom).build();

					CameraPosition cameraPosition = new CameraPosition.Builder()
							.target(
									new LatLng(currentMarker.getPosition().latitude + (
											getResources()
													.getConfiguration()
													.orientation == Configuration.ORIENTATION_LANDSCAPE ? 0.003 : 0
									), currentMarker.getPosition().longitude))
							.zoom(mZoom)
							.build();

					//map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
					map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 10, new GoogleMap.CancelableCallback() {

						@Override
						public void onFinish() {
							if(cluster){
								fabn.show();
								fabp.show();
							}
						}

						@Override
						public void onCancel() {


						}
					});






					currentMarker.showInfoWindow();
					if(cluster) {

						//TODO query
						//List<Event_db> queryResults=new Select().from(Event_db.class).execute();
						Event_db e=markers.get(currentMarker);
						List<Event_db> eves = e.getVenueLocation().events();
						Log.i("save", "reinitialize cluster");
						//List<Event_db> cluster_db=new Select().from(Event_db.class).where("venueId = ?",e.getVenueLocation().getVenueId()).execute();
						markersInLocation.clear();
						for (Event_db event : eves) {
								markersInLocation.add(markersInv.get(event));


						}

						Log.i("save", String.valueOf(markersInLocation.size()) );
						Log.i("save", String.valueOf(markersPosition) );

					}

			}else if(mapPosition!=null){
					CameraPosition cameraPosition = new CameraPosition.Builder()
							.target(mapPosition).zoom(mZoom).build();

					map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
				}else if(loc!=null && firstZoomFromMain) {

					CameraPosition cameraPosition1 = new CameraPosition.Builder()
							.target(loc).zoom(10).build();
					CameraPosition cameraPosition2 = new CameraPosition.Builder()
							.target(loc).zoom(14).build();

					map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));
					map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition2));

					firstZoomFromMain=false;
				}else if(!markers.isEmpty()){

			//opcija 1

			/*		CameraPosition cameraPosition1=null;
					for (Marker marker : markers.keySet()) {
						//builder.include(marker.getPosition());
						cameraPosition1 = new CameraPosition.Builder().target(marker.getPosition()).zoom(10).build();
						break;
					}


					map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition1));*/

				//opcija 2

					LatLngBounds.Builder builder = new LatLngBounds.Builder();
					for (Marker marker : markers.keySet()) {
						builder.include(marker.getPosition());
					}
					LatLngBounds bounds = builder.build();
					int padding = 50; // offset from edges of the map in pixels
					CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 500, 500, padding);


					map.animateCamera(cu);



				}


			/*Circle circle = map.addCircle(new CircleOptions()
					.center(currentLocation)
					.radius(2500)
					.strokeColor(Color.LTGRAY)
					.fillColor(Color.TRANSPARENT));*/

		}

	}






	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		// dodati meni

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		return super.onOptionsItemSelected(item);

	}

	private void createMapFragmentAndInflate() {


		if(mMapFragment==null) {
			Log.i("save", "createMapFragmentAndInflate()");
			mMapFragment = SupportMapFragment.newInstance();


			FragmentTransaction transaction = getChildFragmentManager()
					.beginTransaction();
			transaction.replace(R.id.map_container, mMapFragment).commit();

			mMapFragment.getMapAsync(this);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup vg, Bundle data) {
		Log.i("save", "OnCreateView()");
		View view = inflater.inflate(R.layout.map_layout, vg, false);
		fabn=(FloatingActionButton) view.findViewById(R.id.fab_next);
		//if(fabn!=null) {
			fabn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (!markersInLocation.isEmpty()) {
						if (markersPosition == markersInLocation.size() - 1) {
							markersPosition = 0;
						} else {
							markersPosition++;
						}
						Marker mar = markersInLocation.get(markersPosition);
						mar.showInfoWindow();
						windowOpen=true;
						lastEventId=markers.get(mar).getEventId();
					}
				}
			});
		//}
		fabp=(FloatingActionButton) view.findViewById(R.id.fab_previous);
		//if(fabn!=null) {
			fabp.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (!markersInLocation.isEmpty()) {
						if (markersPosition == 0) {
							markersPosition = markersInLocation.size() - 1;
						} else {
							markersPosition--;
						}
						Marker mar = markersInLocation.get(markersPosition);

						mar.showInfoWindow();
						windowOpen=true;
						lastEventId=markers.get(mar).getEventId();
					}
				}
			});

		//}
		Bundle args = getArguments();
		if(getArguments()!=null) {
			GlobalLocVar = new LatLng(args.getDouble("lat"), args.getDouble("lng"));
		}

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

			layoutParams.setMargins(0, 150, 0, 0);

			fabn.setLayoutParams(layoutParams);
			fabp.setLayoutParams(layoutParams);


		}
		else {
			//fabn.setPadding(0,100,0,0);
		}



		return view;

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		LocalBroadcastManager.getInstance(this.getActivity()).unregisterReceiver(mReceiver);

			fabn.hide();
			fabp.hide();
		if(map!=null) {
			mapPosition = map.getCameraPosition().target;
			mZoom = map.getCameraPosition().zoom;
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}






	@Override
	public void onMapReady(GoogleMap googleMap) {
		map = googleMap;
		Log.i("save", "onMapReady()");


		try {
			map.setMyLocationEnabled(true);


		}catch(SecurityException e){

		}


		map.setInfoWindowAdapter(new MapInfoAdapter());
		map.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {

				if(markers.containsKey(marker)) {
				currentMarker1=marker;


						//CameraPosition cameraPosition = new CameraPosition.Builder()
								//.target(currentMarker1.getPosition()).zoom(map.getCameraPosition().zoom).build();
					CameraPosition cameraPosition = new CameraPosition.Builder()
							.target(
									new LatLng(currentMarker1.getPosition().latitude + (
											getResources()
													.getConfiguration()
													.orientation == Configuration.ORIENTATION_LANDSCAPE ? 0.003 : 0
									), currentMarker1.getPosition().longitude))
							.zoom(map.getCameraPosition().zoom)
							.build();

						//map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

						//map.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

					map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 600, new GoogleMap.CancelableCallback() {

						@Override
						public void onFinish() {
							windowOpen=true;
							int numberOfEvents=0;
							List<Event_db> queryResults=new Select().from(Event_db.class).execute();
							Event_db e=markers.get(currentMarker1);
							lastEventId=e.getEventId();
							Log.i("save", "in marker listener last marker is " + lastEventId);
							Log.i("save", "window is open?" + windowOpen);
							markersInLocation.clear();
							for (Event_db event : queryResults) {
								if(event.getVenueLocation().getVenueId().equals(e.getVenueLocation().getVenueId())) {
									numberOfEvents++;
									markersInLocation.add(markersInv.get(event));
								}

							}

							if(numberOfEvents>1) {
								cluster=true;
								//Toast.makeText(getActivity(), "Multiple events on this location, tap the marker to cycle through them", Toast.LENGTH_LONG).show();
								markersPosition=0;

									fabn.show();
									fabp.show();
							}else{
								cluster=false;
							}

						}

						@Override
						public void onCancel() {


						}
					});

					marker.showInfoWindow();
					//map.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
					//Intent intent = new Intent(getActivity(), EventDetailView.class);
					//startActivity(intent);
					//FragmentTransition.to(MyFragment.newInstance(), getActivity(), true);


						fabn.hide();
						fabp.hide();
				}
				return true;
			}
		});


		map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(Marker arg0) {
				//Intent intent = new Intent(getActivity(), EventDetailView.class);
				//startActivity(intent);

				Event_db event = markers.get(arg0);
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/events/"+event.getEventId()));
				startActivity(browserIntent);


			}
		});
		map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
			@Override
			public void onMapClick(LatLng latLng) {

					fabn.hide();
					fabp.hide();
				lastEventId="";
				windowOpen=false;
				Log.i("save", "window closed");
			}
		});


		map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
			@Override
			public void onMapLongClick(LatLng latLng) {

					fabn.hide();
					fabp.hide();
				Location location= new Location("");
				location.setLatitude(latLng.latitude);
				location.setLongitude(latLng.longitude);
				Intent si = new Intent(getActivity(), SyncService.class);
				si.putExtra("location", location);
				getActivity().startService(si);


			}
		});

		map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition cameraPosition) {

					fabn.hide();
					fabp.hide();
			}
		});

		map.setOnInfoWindowCloseListener(new GoogleMap.OnInfoWindowCloseListener() {
			@Override
			public void onInfoWindowClose(Marker marker) {

			}
		});

		///map.clear();
		//markers.clear();

		zoom=false;
		//Log.i("GlobalLocVar", String.valueOf(GlobalLocVar));

			refreshMGoogleMap(GlobalLocVar);

		/*RefreshMarkers();
		if (loc != null) {
			//GetDataFromServer(location);
			//LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(loc).zoom(18).build();

			//map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc,14));
		}*/

			//RefreshMap(loc);

	}






	private class MapInfoAdapter implements GoogleMap.InfoWindowAdapter {

		public MapInfoAdapter()
		{
			// TODO Auto-generated constructor stub
		}

		@Override
		public View getInfoContents(Marker arg0)
		{
			View view = getLayoutInflater(null).inflate(R.layout.info_window_layout, null);

			Event_db event = markers.get(arg0);

			TextView title = (TextView) view.findViewById(R.id.event_name);
			title.setText(event.getEventName());

			TextView date = (TextView) view.findViewById(R.id.event_date);

			SimpleDateFormat incomingFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
			Date date1=null;
			try {
			date1 = incomingFormat.parse(event.getEventStarttime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			SimpleDateFormat outgoingFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy", java.util.Locale.getDefault());
			SimpleDateFormat outgoingFormat1 = new SimpleDateFormat("HH:mm", java.util.Locale.getDefault());

			date.setText(outgoingFormat.format(date1) + " at " + outgoingFormat1.format(date1));

			Date currentDate=new Date();
			int value = date1.before(currentDate)?Color.RED:Color.BLACK;
			title.setTextColor(value);


			TextView place = (TextView) view.findViewById(R.id.event_location);
			place.setText(event.getVenueLocation().getVenueName());

			ImageView image = (ImageView) view.findViewById(R.id.event_picture);

			if(event.getEventProfilePicture() != null)
			{


				//Picasso.with(view.getContext()).load(event.getEventProfilePicture()).into(image);
				Picasso.with(view.getContext())
						.load(event.getEventProfilePicture())
						.placeholder(R.drawable.image_placeholder)
						.into(image, new MarkerCallback(arg0));
			}



			return view;
		}

		@Override
		public View getInfoWindow(Marker arg0) {
			return null;
		}

	}

	public class MarkerCallback implements com.squareup.picasso.Callback {
		Marker marker=null;

		MarkerCallback(Marker marker) {
			this.marker=marker;
		}

		@Override
		public void onError() {
			Log.e(getClass().getSimpleName(), "Error loading thumbnail!");
		}

		@Override
		public void onSuccess() {
			if (marker != null && marker.isInfoWindowShown()) {
				marker.hideInfoWindow();
				marker.showInfoWindow();
			}
		}
	}

}