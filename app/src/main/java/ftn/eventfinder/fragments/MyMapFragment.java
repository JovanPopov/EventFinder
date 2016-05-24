package ftn.eventfinder.fragments;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import ftn.eventfinder.R;
import ftn.eventfinder.RetrofitInt.EventsInterface;
import ftn.eventfinder.dialogs.LocationDialog;
import ftn.eventfinder.model.Event;
import ftn.eventfinder.model.EventsResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MyMapFragment extends Fragment implements LocationListener, OnMapReadyCallback {

	public static int REQUEST_TAG_FILTER = 0;
	public static String TAG_FILTER = "TAG_FILTER";
	private ArrayList<String> tagFilter;

	private GoogleMap map;
	private SupportMapFragment mMapFragment;
	private LocationManager locationManager;
	private String provider;
	private android.support.v7.app.AlertDialog dialog;

	private Marker home;

	List<Event> events = new ArrayList<Event>();
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

		createMapFragmentAndInflate();

		boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean wifi = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		if (!gps && !wifi) {
			//new LocationDialog(getActivity()).prepareDialog().show();
			showLocatonDialog();
		} else {
			// Toast.makeText(getActivity(), "noService",
			// Toast.LENGTH_SHORT).show();
			try {
				locationManager.requestLocationUpdates(provider, 0, 0, this);
			} catch (SecurityException e) {
				//dialogGPS(this.getContext()); // lets the user know there is a problem with the gps
			}
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
		locationManager = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);

		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, true);

		mMapFragment = SupportMapFragment.newInstance();
		FragmentTransaction transaction = getChildFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.map_container, mMapFragment).commit();

		mMapFragment.getMapAsync(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup vg, Bundle data) {

		View view = inflater.inflate(R.layout.map_layout, vg, false);

		return view;

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		try {
			locationManager.removeUpdates(this);
		} catch (SecurityException e) {
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			locationManager.removeUpdates(this);
		} catch (SecurityException e) {
		}
	}

	private void showLocatonDialog() {
		if (dialog == null) {
			dialog = new LocationDialog(getActivity()).prepareDialog();
		} else {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}

		dialog.show();
	}

	private void addMarker(Location location) {
		// Toast.makeText(getActivity(), "addMarker",
		// Toast.LENGTH_SHORT).show();
		LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());

		if (home != null) {
			home.remove();
		}


		home = map.addMarker(new MarkerOptions()
				.title("Hey there.")
				.snippet("You are here at the moment :)")
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_RED))
				.position(loc));

	/*	CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(loc).zoom(12).build();

		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/
	}

	@Override
	public void onLocationChanged(Location location) {
		//Toast.makeText(getActivity(), "onLocationChange()", Toast.LENGTH_SHORT).show();

		//addMarker(location);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		Location location = null;
		try {
			location = locationManager.getLastKnownLocation(provider);
		} catch (SecurityException e) {
		}
		//Toast.makeText(getActivity(), "onMapReady()", Toast.LENGTH_SHORT).show();
		LatLng centar = new LatLng(45.254294, 19.842446);
		map = googleMap;
		try {
			map.setMyLocationEnabled(true);
		}catch(SecurityException e){

		}



		map.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {


				marker.showInfoWindow();
				//Intent intent = new Intent(getActivity(), EventDetailView.class);
				//startActivity(intent);
				//FragmentTransition.to(MyFragment.newInstance(), getActivity(), true);
				return true;
			}
		});


		map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(Marker arg0) {
				//Intent intent = new Intent(getActivity(), EventDetailView.class);
				//startActivity(intent);

			}
		});



		if (location != null) {
			GetDataFromServer(location);
			LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(loc).zoom(14).build();

			map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

		}
	}


	public void GetDataFromServer(Location location) {
		//final String BASE_URL = "http://localhost:3000/events?lat=" + location.getLatitude() + "&lng=" + location.getLongitude() + "&distance=2500&sort=venue&access_token=263841713956622|l2uKn8wbzuk3OpKM7BzwB7y1VvY/";
        final String BASE_URL = "http://188.2.87.248:3000/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


		EventsInterface service = retrofit.create(EventsInterface.class);
		Call<EventsResponse> call = service.getEvents();
		call.enqueue(new Callback<EventsResponse>() {
			@Override
			public void onResponse(Call<EventsResponse> call, Response<EventsResponse> response) {
				int statusCode = response.code();
				EventsResponse eventsResponse = response.body();
				 events = eventsResponse.getEvents();
				//Log.i("odgovor",events.get(0).getEventName());

				Toast.makeText(getActivity(), "number of events:" + events.size(), Toast.LENGTH_SHORT).show();
				PopulateWithMarkers();

			}

			@Override
			public void onFailure(Call<EventsResponse> call, Throwable t) {
				// Log error here since request failed
				Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.i("odgovor",t.getMessage());

			}
		});



	}

	public void PopulateWithMarkers(){
		for (Event e : events) {
			LatLng lokacija = new LatLng(e.getVenueLocation().getLatitude(), e.getVenueLocation().getLongitude());
			map.addMarker(new MarkerOptions()
					.title(e.getEventName())
					.snippet(e.getVenueName())
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_RED))
					.position(lokacija));


		}
	}
}