package ca.recoverygo.recoverygo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;
/*import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;*/

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
/*import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;*/

public class LocatorActivity extends FragmentActivity implements
        OnMapReadyCallback,
        LocationListener,
        GoogleMap.OnMarkerClickListener {

    private static final String TAG = "rg_LocatorActivity";
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    /*private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth;
    private ArrayList<GeoPoint> mMarker  = new ArrayList<>();
    private ArrayList<String>   mNotes   = new ArrayList<>();
    private ArrayList<String>   mAddress = new ArrayList<>();
    private ArrayList<String>   mSite    = new ArrayList<>();
    private ArrayList<String>   mGroup   = new ArrayList<>();*/
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    private LocationManager locationManager;
    private String provider;
    Marker myMarker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locator);
        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) { onLocationChanged(location); }
        else {
            Log.d(TAG, "onCreate: location is NULL");
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        else {
            Log.d(TAG, "onMapReady: ACCESS_FINE_LOCATION GRANTED");
        }

        final Location location = locationManager.getLastKnownLocation(provider);

        Double myLat = location.getLatitude();
        Double myLng = location.getLongitude();
        LatLng myposition = new LatLng(myLat, myLng);

        myMarker = googleMap.addMarker(new MarkerOptions().position(myposition).title("ME").icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(myposition));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myposition,10));

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
/*        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser;
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Log.d(TAG, "onMapReady: User is logged in");
            Log.d(TAG, "onMapReady: loading location markers");
            db.collection("locations").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    for(DocumentSnapshot snapshot : documentSnapshots){

                        mMarker.add(snapshot.getGeoPoint("location"));
                        mNotes.add(snapshot.getString   ("note"));
                        mAddress.add(snapshot.getString ("address"));
                        mSite.add(snapshot.getString    ("site"));
                        mGroup.add(snapshot.getString   ("groupname"));

                        for (int i = 0; i < mMarker.size(); i++) {

                            String note     = mNotes    .get(i);
                            String address  = mAddress  .get(i);
                            String site     = mSite     .get(i);
                            String group    = mGroup    .get(i);
                            GeoPoint marker = mMarker   .get(i);

                            Double mkrLat = marker.getLatitude();
                            Double mkrLng = marker.getLongitude();

                            LatLng mkrposition = new LatLng(mkrLat, mkrLng);
                            googleMap.addMarker(new MarkerOptions()
                                    .position (mkrposition)
                                    .title(address)
                                    .snippet(note)
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                            //Log.d(TAG, "onEvent: "+marker+","+address+","+site+","+group+","+note);
                        }

                    }
                    final int rcdCount = mMarker.size();
                    Log.d(TAG, "onEvent: record count = "+rcdCount);
                }
            });

        } else {
            Log.d(TAG, "onMapReady: User is logged out");
            //Intent intent = new Intent(LocatorActivity.this, MainActivity.class);
            //startActivity(intent);
        }*/

        LatLng perth1 =             new LatLng(44.9009235, -76.2502683);
        LatLng perth2 =             new LatLng(44.903174,-76.2544246);
        LatLng perth3 =             new LatLng(44.8994238,-76.246947);

        LatLng sharbotlake1 =       new LatLng(44.7713312,-76.6927184);

        LatLng smithsFalls1 =       new LatLng(44.9052747, -76.031819);
        LatLng smithsFalls2 =       new LatLng(44.9017021,-76.0192961);
        LatLng smithsFalls3 =       new LatLng(44.8993029,-76.0271187);


        googleMap.addMarker(new MarkerOptions().position(perth1).title           ("Perth Resturaunt, 23 Gore St E, Perth, ON"));
        googleMap.addMarker(new MarkerOptions().position(perth2).title           ("First Baptist Church, D'Arcy St, Perth, ON"));
        googleMap.addMarker(new MarkerOptions().position(perth3).title           ("St. James Angligan Church, 12 Harvey St, Perth, ON"));
        googleMap.addMarker(new MarkerOptions().position(sharbotlake1).title     ("United Church Hall, Sharbot Lake, ON"));
        googleMap.addMarker(new MarkerOptions().position(smithsFalls1).title     ("Cornelia Court, 91 Cornelia St, Smith Falls, ON"));
        googleMap.addMarker(new MarkerOptions().position(smithsFalls2).title     ("Trinity United Church of Canada, 41 Market St N, Smiths Falls, ON"));
        googleMap.addMarker(new MarkerOptions().position(smithsFalls3).title     ("2 George St South, Smiths Falls, ON"));
        googleMap.setOnMarkerClickListener(this);

    }
    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED 
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Log.d(TAG, "onResume: locationManager.requestLocationUpdates(400,1)");
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
        Log.d(TAG, "onPause: removeUpdates");
    }

    @Override
    public void onLocationChanged(Location location) {
        locationManager.removeUpdates(this);
        Log.d(TAG, "onLocationChanged: removeUpdates");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        Toast.makeText(this, marker.getSnippet() + " has been clicked", Toast.LENGTH_LONG).show();
    return false;
    }
}
