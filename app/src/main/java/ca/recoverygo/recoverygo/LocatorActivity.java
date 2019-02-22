package ca.recoverygo.recoverygo;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import ca.recoverygo.recoverygo.adapters.InfoWindowCustom;

public class LocatorActivity extends FragmentActivity implements
        OnMapReadyCallback,
        LocationListener,
        GoogleMap.OnMarkerClickListener {

    private static final String TAG = "rg_Locator";
    private FirebaseFirestore   db        = FirebaseFirestore.getInstance();
    private ArrayList<GeoPoint> mMarker   = new ArrayList<>();
    private ArrayList<String>   mNotes    = new ArrayList<>();
    private ArrayList<String>   mAddress  = new ArrayList<>();
    private ArrayList<String>   mOrg      = new ArrayList<>();
    private ArrayList<String>   mGroup    = new ArrayList<>();
    private ArrayList<String>   mSite     = new ArrayList<>();
    private ProgressDialog      mProgressDialog;
    private String              provider;
    public  Marker              myMarker;
    private LocationManager     locationManager;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 0;//1000 * 60 * 1; // 1 minute

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locator);

        ImageView logo = findViewById(R.id.logo);

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocatorActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Log.d(TAG, "onCreate: Calling Location Manager");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Log.d(TAG, "onCreate: provider = " + provider);

        if ( provider == null) {
            Log.d(TAG, "onCreate: Provider is null, calling loadSettings()");
            loadSettings();
        }
        Log.d(TAG, "onCreate: Getting Last Known Location");
        Location location = locationManager.getLastKnownLocation(provider);
            Log.d(TAG, "onCreate: location = " + location);

            if (location == null) {
                Log.d(TAG, "onCreate: location is null");
            }
        onLocationChanged(location);
        Log.d(TAG, "onCreate: end");
        }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: started");
        super.onStart();
        FirebaseApp.initializeApp(this);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser;
        currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        Log.d(TAG, "onStart: end");
    }
    public void loadSettings(){
        Log.d(TAG, "loadSettings: started");
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
        Log.d(TAG, "loadSettings: end");
    }

    public void updateUI(FirebaseUser user) {
        Log.d(TAG, "updateUI: started");
        if (user != null) {
            Log.d(TAG, "updateUI: user != null");
            return;
        }
            Log.d(TAG, "updateUI: user is null calling alert dialog");
            AlertDialog alertDialog = new AlertDialog.Builder(LocatorActivity.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("You must have a valid account to use the Meeting Locator.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(LocatorActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    });

        Log.d(TAG, "updateUI: user is: "+user);
        
    }

    public void onMapReady(final GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: started");
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION )
                        != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onCreate: Build Version >= 23 - Denied");
            loadSettings();
        }
        centerMap(googleMap);
        setMarkers(googleMap);
    }

    public void centerMap(final GoogleMap googleMap) {
        Log.d(TAG, "centerMap: started");
            Location location = locationManager.getLastKnownLocation(provider);
            Log.d(TAG, "centerMap: locatio = " + location);
            locationManager.requestLocationUpdates(provider, 5000, 1, this);
            Log.d(TAG, "centerMap: Update Interval: 5sec - 1km");
            Double myLat = location.getLatitude();
            Double myLng = location.getLongitude();
            LatLng myposition = new LatLng(myLat, myLng);
            Log.d(TAG, "centerMap: to: " + myposition);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            myMarker = googleMap.addMarker(new MarkerOptions()
                    .position(myposition)
                    .title("Stop hitting yourself! :). Seriously though, stop beating yourself up. You are not alone")
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.mkr_mylocation)));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(myposition));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myposition, 10));
            Log.d(TAG, "centerMap: end");
        Log.d(TAG, "centerMap: build <= 23 - end");
    }
    public void setMarkers(final GoogleMap googleMap){
        Log.d(TAG, "setMarkers: started");
        mMarker.clear();
        showProgressDialog();
        Log.d(TAG, "setMarkers: db connect to Locations");
        db.collection("locations").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: Success");
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {

                        mMarker     .add(document.getGeoPoint   ("location"));
                        mNotes      .add(document.getString     ("note"));
                        mAddress    .add(document.getString     ("address"));
                        mOrg        .add(document.getString     ("org"));
                        mGroup      .add(document.getString     ("groupname"));
                        mSite       .add(document.getString     ("site"));
                    }
                    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                    int i = 0;
                    while (i < mMarker.size()) {

                        String note     = mNotes.get(i);
                        String address  = mAddress.get(i);
                        GeoPoint marker = mMarker.get(i);
                        String org      = mOrg.get(i);
                        String group    = mGroup.get(i);
                        String site     = mSite.get(i);

                        String snippet = org+" - "+site+"\n"+group+"\n"+note;
                        // Log.d("rg_", "onComplete: "+snippet);
                        Double mkrLat = marker.getLatitude();
                        Double mkrLng = marker.getLongitude();
                        LatLng mkrposition = new LatLng(mkrLat, mkrLng);
                        Log.d(TAG, "onComplete: mkrposition = "+mkrposition);
                        switch (org) {
                            case "AL":
                                googleMap.addMarker(new MarkerOptions().position(mkrposition).title(address).snippet(snippet)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.mkr_flag_al)));
                                break;
                            case "NA":
                                googleMap.addMarker(new MarkerOptions().position(mkrposition).title(address).snippet(snippet)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.mkr_flag_na)));
                                break;
                            case "TR":
                                googleMap.addMarker(new MarkerOptions().position(mkrposition).title(address).snippet(snippet)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.beds_48)));
                                break;
                            default:
                                googleMap.addMarker(new MarkerOptions().position(mkrposition).title(address).snippet(snippet)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.mkr_flag_aa)));
                                break;
                        }
                        ++i;
                    }
                    hideProgressDialog();
                    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                } else {
                    hideProgressDialog();
                    Log.d(TAG, "onComplete: Query failed");                }

            }
        });

        googleMap.setInfoWindowAdapter(new InfoWindowCustom(this));
        googleMap.setOnMarkerClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(provider, 90000, 1, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(this, "Location Changed " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText(this, "Status Changed " + provider,
                Toast.LENGTH_SHORT).show();
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
        // Toast.makeText(this, marker.getSnippet() + " has been clicked",Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LocatorActivity.this, MainActivity.class);
        startActivity(intent);
    }
    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(false);
        }
        mProgressDialog.show();
    }
    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
