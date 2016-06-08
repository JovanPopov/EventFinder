package ftn.eventfinder.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
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
import ftn.eventfinder.RetrofitInt.EventsInterface;
import ftn.eventfinder.dialogs.LocationDialog;
import ftn.eventfinder.entities.EventStats_db;
import ftn.eventfinder.entities.Event_db;
import ftn.eventfinder.entities.VenueLocation_db;
import ftn.eventfinder.model.Event;
import ftn.eventfinder.model.EventsResponse;
import ftn.eventfinder.sync.SyncService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


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
	LatLng GlobalLocVar= null;
	private ArrayList<Marker> markersInLocation = new ArrayList<Marker>();
	private int markersPosition=0;
	boolean zoom=false;
	Marker previous=null;
	private FloatingActionButton fabn;
	private FloatingActionButton fabp;
	private Marker currentMarker;

	public static MyMapFragment newInstance() {

		MyMapFragment mpf = new MyMapFragment();

		//mpf.markers = new HashMap<Marker, ReviewObject>();

		return mpf;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		createMapFragmentAndInflate();

		if (savedInstanceState != null) {
			tagFilter = savedInstanceState.getStringArrayList(TAG_FILTER);
		} else {
			tagFilter = new ArrayList<String>();
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

		fabn=getNext();
		if(fabn!=null) {
			fabn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					//Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
					if (!markersInLocation.isEmpty()) {
						if (markersPosition == markersInLocation.size() - 1) {
							markersPosition = 0;
						} else {
							markersPosition++;
						}
						markersInLocation.get(markersPosition).showInfoWindow();
					}
				}
			});
		}
		fabp=getPrevious();
		if(fabn!=null) {
			fabp.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					//Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
					if (!markersInLocation.isEmpty()) {
						if (markersPosition == 0) {
							markersPosition = markersInLocation.size() - 1;
						} else {
							markersPosition--;
						}
						markersInLocation.get(markersPosition).showInfoWindow();
					}
				}
			});

		}

	}


	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putStringArrayList(TAG_FILTER, tagFilter);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// Toast.makeText(getActivity(), "onResume()",
		// Toast.LENGTH_SHORT).show();

//		getActivity().getActionBar().setTitle(R.string.home);
		setHasOptionsMenu(true);

		//Toast.makeText(getActivity(), "onResume()", Toast.LENGTH_SHORT).show();
		long minTime = 60000;
		float minDistance = 15;

		createMapFragmentAndInflate();




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
		//fabn.hide();
		//fabp.hide();
	}


	public BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String message = intent.getStringExtra("foo");
			LatLng currentLocation = new LatLng( intent.getDoubleExtra("lat",0), intent.getDoubleExtra("lng",0));
			refreshMGoogleMap(currentLocation);
		}


	};


	public void refreshMGoogleMap(LatLng loc){
		if(map!=null) {


			if (!zoom) {

				//map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 10));

				CameraPosition cameraPosition = new CameraPosition.Builder()
						.target(loc).zoom(14).build();

				map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

				zoom = true;
			}


			/*Circle circle = map.addCircle(new CircleOptions()
					.center(currentLocation)
					.radius(2500)
					.strokeColor(Color.LTGRAY)
					.fillColor(Color.TRANSPARENT));*/

			RefreshMarkers();
		}

	}
	public void RefreshMarkers(){
		List<Event_db> eve=new Select().from(Event_db.class).execute();

		for (Event_db e : eve) {
			LatLng lokacija = new LatLng(e.getVenueLocation().getLatitude(), e.getVenueLocation().getLongitude());

			if(!markers.containsValue(e)) {

				Marker marker = map.addMarker(new MarkerOptions()
						.title(e.getEventName())
						.snippet(e.getVenueName())
						.icon(BitmapDescriptorFactory
								.defaultMarker(BitmapDescriptorFactory.HUE_RED))
						.position(lokacija));

				markers.put(marker, e);
				markersInv.put(e, marker);
			}
		}
	}


	public void RefreshMap(LatLng currentLocation){
		if(map!=null) {

			List<Event_db> eve=new Select().from(Event_db.class).execute();
			int iii= new Select().from(Event_db.class).count();
			LatLng lll=null;
			if(currentLocation!=null) {
				lll=currentLocation;

				mapZoom(lll);
				zoom = true;
				Circle circle = map.addCircle(new CircleOptions()
						.center(lll)
						.radius(2500)
						.strokeColor(Color.LTGRAY)
						.fillColor(Color.TRANSPARENT));
			}else {
				if(iii>0) {
					lll = new LatLng(eve.get(0).getVenueLocation().getLatitude(), eve.get(0).getVenueLocation().getLongitude());
					mapZoom(lll);
				}
			}




			for (Event_db e : eve) {
				LatLng lokacija = new LatLng(e.getVenueLocation().getLatitude(), e.getVenueLocation().getLongitude());

				if(!markers.containsValue(e)) {

					Marker marker = map.addMarker(new MarkerOptions()
							.title(e.getEventName())
							.snippet(e.getVenueName())
							.icon(BitmapDescriptorFactory
									.defaultMarker(BitmapDescriptorFactory.HUE_RED))
							.position(lokacija));

					markers.put(marker, e);
					markersInv.put(e, marker);
				}
			}
		}
	}

	public void mapZoom(LatLng loc){
		if (!zoom) {

			//map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 10));

			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(loc).zoom(13).build();

			map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


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


		// Get LocationManager object from System Service LOCATION_SERVICE



		//Toast.makeText(getActivity(), "Best Provider " + bestProvider, Toast.LENGTH_SHORT).show();

		//locationManager.requestLocationUpdates(bestProvider, minTime, minDistance, this);




		mMapFragment = SupportMapFragment.newInstance();
		FragmentTransaction transaction = getChildFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.map_container, mMapFragment).commit();

		mMapFragment.getMapAsync(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup vg, Bundle data) {

		View view = inflater.inflate(R.layout.map_layout, vg, false);

		Bundle args = getArguments();
		if(getArguments()!=null) {
			GlobalLocVar = new LatLng(args.getDouble("lat"), args.getDouble("lng"));
		}

		return view;

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		LocalBroadcastManager.getInstance(this.getActivity()).unregisterReceiver(mReceiver);
		fabn = getNext();
		fabp = getPrevious();
		if(fabn!=null)
			fabn.hide();
		if(fabp!=null)
			fabp.hide();


	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}






	@Override
	public void onMapReady(GoogleMap googleMap) {
		map = googleMap;
		Location location = null;



		try {
			map.setMyLocationEnabled(true);


		}catch(SecurityException e){

		}


		map.setInfoWindowAdapter(new MapInfoAdapter());
		map.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				if(markers.containsKey(marker)) {
				currentMarker=marker;

						CameraPosition cameraPosition = new CameraPosition.Builder()
								.target(currentMarker.getPosition()).zoom(map.getCameraPosition().zoom).build();

						map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

						//map.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));

					map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 600, new GoogleMap.CancelableCallback() {

						@Override
						public void onFinish() {

							int numberOfEvents=0;
							List<Event_db> queryResults=new Select().from(Event_db.class).execute();
							Event_db e=markers.get(currentMarker);
							markersInLocation.clear();
							for (Event_db event : queryResults) {
								if(event.getVenueId().equals(e.getVenueId())) {
									numberOfEvents++;
									markersInLocation.add(markersInv.get(event));
								}

							}

							if(numberOfEvents>1) {
								//Toast.makeText(getActivity(), "Multiple events on this location, tap the marker to cycle through them", Toast.LENGTH_LONG).show();
								markersPosition=0;
								fabn = getNext();
								fabp = getPrevious();
								if(fabn!=null)
									fabn.show();
								if(fabp!=null)
									fabp.show();
							}else{

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


					fabn = getNext();
					fabp = getPrevious();
					if(fabn!=null)
						fabn.hide();
					if(fabp!=null)
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
				fabn = getNext();
				fabp = getPrevious();
				if(fabn!=null)
					fabn.hide();
				if(fabp!=null)
					fabp.hide();
			}
		});


		map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
			@Override
			public void onMapLongClick(LatLng latLng) {
				fabn = getNext();
				fabp = getPrevious();
				if(fabn!=null)
					fabn.hide();
				if(fabp!=null)
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
				fabn = getNext();
				fabp = getPrevious();
				if(fabn!=null)
					fabn.hide();
				if(fabp!=null)
					fabp.hide();
			}
		});


		map.clear();
		markers.clear();

		zoom=false;

		if(GlobalLocVar!=null){
			refreshMGoogleMap(GlobalLocVar);
		}
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

			TextView title = (TextView) view.findViewById(R.id.marker_name);
			title.setText(event.getEventName());

			TextView date = (TextView) view.findViewById(R.id.marker_date);

			SimpleDateFormat incomingFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
			Date date1=null;
			try {
			date1 = incomingFormat.parse(event.getEventStarttime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			SimpleDateFormat outgoingFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy", java.util.Locale.getDefault());

			date.setText(outgoingFormat.format(date1));
			Date currentDate=new Date();
			if(date1.before(currentDate)) {
				title.setTextColor(Color.RED);
			}
			TextView place = (TextView) view.findViewById(R.id.marker_place);
			place.setText(event.getVenueName());

			ImageView image = (ImageView) view.findViewById(R.id.marker_picture);

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


	public FloatingActionButton getNext() {
		return (FloatingActionButton) getActivity().findViewById(R.id.fab_next);
	}

	public FloatingActionButton getPrevious() {
		return (FloatingActionButton) getActivity().findViewById(R.id.fab_previous);
	}


}