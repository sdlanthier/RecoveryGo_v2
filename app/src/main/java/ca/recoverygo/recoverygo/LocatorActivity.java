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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
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

    private static final String TAG = "rg_LocatorActivity";
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth;
    private ArrayList<GeoPoint> mMarker  = new ArrayList<>();
    private ArrayList<String>   mNotes   = new ArrayList<>();
    private ArrayList<String>   mAddress = new ArrayList<>();
    private ArrayList<String>   mOrg    = new ArrayList<>();
    private ArrayList<String>   mGroup   = new ArrayList<>();
    private ArrayList<String>   mLocation_id   = new ArrayList<>();

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    private LocationManager locationManager;
    private String provider;

    Marker myMarker;
    ImageView logo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locator);

        logo = findViewById(R.id.logo);
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

        myMarker = googleMap
                .addMarker(new MarkerOptions()
                        .position(myposition)
                        .title("Stop hitting yourself! :). Seriously though, stop beating yourself up. You are not alone")
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.mkr_mylocation)));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(myposition));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myposition,10));
        showProgressDialog();
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser;
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Log.d(TAG, "onMapReady: User is logged in");
            Log.d(TAG, "onMapReady: loading location markers");
            mMarker.clear();
            Log.d(TAG, "onMapReady: mMarker cleared");

            db.collection("locations")
                    /*.whereEqualTo("org", "NA")*/
                    /*.whereGreaterThanOrEqualTo("org", "NA")*/
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            Log.d(TAG, "onComplete: whereEqualTo(\"org\", \"NA\")");

                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());

                                    mMarker.add(document.getGeoPoint    ("location"));
                                    mNotes.add(document.getString       ("note"));
                                    mAddress.add(document.getString     ("address"));
                                    mOrg.add(document.getString         ("org"));
                                    mGroup.add(document.getString       ("groupname"));
                                    mLocation_id.add(document.getString ("location_id"));

                                }
                                final int rcdCount = mMarker.size();
                                Log.d(TAG, "onEvent: record count = "+rcdCount);
                                //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                                int i = 0;
                                     while(i < mMarker.size()) {

                                         String note = mNotes.get(i);
                                         String address = mAddress.get(i);
                                         GeoPoint marker = mMarker.get(i);
                                         String org = mOrg.get(i);
                                         String group = mGroup.get(i);
                                         String loc_id = mLocation_id.get(i);

                                         Double mkrLat = marker.getLatitude();
                                         Double mkrLng = marker.getLongitude();
                                         LatLng mkrposition = new LatLng(mkrLat, mkrLng);

                                        if(org.equals("AA")) {
                                            googleMap.addMarker(new MarkerOptions()
                                                    .position(mkrposition)
                                                    .title(address)
                                                    .snippet(note)
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.mkr_flag_aa)));
                                        }
                                        else {
                                            googleMap.addMarker(new MarkerOptions()
                                                    .position (mkrposition)
                                                    .title(address)
                                                    .snippet(note)
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.mkr_flag_na)));
                                        }

                                        Log.d(TAG, "result: "+marker+","+address+","+note);
                                        ++i;
                                    }
                                hideProgressDialog();
                                //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }

                        }
                    });

        } else {
            hideProgressDialog();
            Log.d(TAG, "onMapReady: User is logged out");
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
            alertDialog.show();
            //Intent intent = new Intent(LocatorActivity.this, MainActivity.class);
            //startActivity(intent);
        }
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
        Log.d(TAG, "onResume: locationManager.requestLocationUpdates(400,1)");
        locationManager.requestLocationUpdates(provider, 5000, 1, this);
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
        //Toast.makeText(this, marker.getSnippet() + " has been clicked", Toast.LENGTH_LONG).show();
    return false;
    }

    public ProgressDialog mProgressDialog;

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
