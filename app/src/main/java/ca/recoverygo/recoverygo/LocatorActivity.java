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
import android.os.Build;
import android.os.Bundle;
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
import com.google.android.gms.maps.model.LatLngBounds;
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

    private static final String TAG = "rg_LocatorActivity";
    
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locator);
        Log.d(TAG, "onCreate: calling checkPermissions()");
        /* ************************************************************************************** */
        checkPermissions();
        getLocation();
        checkUser();
        /* ************************************************************************************** */
        ImageView logo = findViewById(R.id.logo);

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LocatorActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        Log.d(TAG, "onCreate: loading map fragment");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void checkPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkPermissions: Manifest Permissions Not Granted");

            AlertDialog alertDialog = new AlertDialog.Builder(LocatorActivity.this).create();
            alertDialog.setTitle("Location");
            alertDialog.setMessage("Location Service on this device must be turned on.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(LocatorActivity.this,MainActivity.class);
                            startActivity(intent);
                        }
                    });
            alertDialog.show();

        }else{
            Log.d(TAG, "checkPermissions: Manifest Permissions Granted");
        }
    }
    public void getLocation(){
        Log.d(TAG, "getLocation: Getting Location Provider");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);

        if ( provider != null) {
            Log.d(TAG, "getLocation: Provider = "+provider);
            Log.d(TAG, "getLocation: Getting Location");
            Location location = locationManager.getLastKnownLocation(provider);

            if (location != null) {
                Log.d(TAG, "getLocation: Location = "+location);
            }
        }else{
            AlertDialog alertDialog = new AlertDialog.Builder(LocatorActivity.this).create();
            alertDialog.setTitle("Location");
            alertDialog.setMessage("You must allow Location Permission for this app.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(LocatorActivity.this,MainActivity.class);
                            startActivity(intent);
                        }
                    });
            alertDialog.show();
        }
    }
    public void checkUser(){
        FirebaseApp.initializeApp(this);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser;
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "checkUser: User: "+currentUser);
            return;
        }else{
            AlertDialog alertDialog = new AlertDialog.Builder(LocatorActivity.this).create();
            alertDialog.setTitle("User");
            alertDialog.setMessage("Please Login or Create new account.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(LocatorActivity.this,MainActivity.class);
                            startActivity(intent);
                        }
                    });
            alertDialog.show();
        }
        Log.d(TAG, "checkUser: user is null");
    }

    public void onMapReady(final GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: Started");
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION )
                        == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onMapReady: Build.VERSION.SDK_INT >= 23 - Granted");
            Log.d(TAG, "onMapReady: calling centreMap()");
            centerMap(googleMap);
            Log.d(TAG, "onMapReady: calling setMarkers()");
            setMarkers(googleMap);
            Log.d(TAG, "onMapReady: end");
        }else{
            Log.d(TAG, "onMapReady: Build.VERSION.SDK_INT >= 23 - Denied");
        }

    }
    public void centerMap(GoogleMap googleMap) {
        Log.d(TAG, "centerMap: CALLED");
            Location location = locationManager.getLastKnownLocation(provider);
            locationManager.requestLocationUpdates(provider, 5000, 1, this);
            Double myLat = location.getLatitude();
            Double myLng = location.getLongitude();

        LatLngBounds.Builder latlngBuilder = new LatLngBounds.Builder();

        LatLng myposition  = new LatLng(myLat, myLng);
        LatLng myposition1 = new LatLng(myLat+0.01, myLng+0.01);
        LatLng myposition2 = new LatLng(myLat-0.01, myLng-0.01);
        latlngBuilder.include(myposition1);
        latlngBuilder.include(myposition2);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        myMarker = googleMap
                .addMarker(new MarkerOptions()
                .position(myposition)
                .title("Stop hitting yourself! :). Seriously though, stop beating yourself up.")
                .icon(BitmapDescriptorFactory
                .fromResource(R.drawable.mkr_mylocation)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(myposition));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latlngBuilder.build(), 100));

    }
    public void setMarkers(final GoogleMap googleMap){
        Log.d(TAG, "setMarkers: started");
        mMarker.clear();
        showProgressDialog();
        db.collection("locations").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
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
                        Double mkrLat = marker.getLatitude();
                        Double mkrLng = marker.getLongitude();
                        LatLng mkrposition = new LatLng(mkrLat, mkrLng);
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
                }
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
        Log.d(TAG, "onResume: LocationUpdates 10s 1km");
        locationManager.requestLocationUpdates(provider, 60000, 1, this);
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
        Log.d(TAG, "onLocationChanged: "+location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Toast.makeText(this, "Status Changed " + provider,
                Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onStatusChanged: "+provider +" - status:"+status);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onProviderEnabled: "+provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onProviderDisabled: "+provider);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        Log.d(TAG, "onMarkerClick: Marker "+marker+" click");
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
