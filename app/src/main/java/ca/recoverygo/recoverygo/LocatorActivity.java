package ca.recoverygo.recoverygo;

import android.Manifest;
import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocatorActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private static final String TAG = "LocatorActivity";
    private LocationManager locationManager;
    private String provider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locator);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d(TAG, "onCreate() provider: " + provider);
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);

        Log.d(TAG, "onCreate: " + location);
        if (location != null) {
            Log.d(TAG, "Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else { Log.d(TAG, "Location not available"); }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged() called with: location = [" + location + "]");
        int lat = (int) (location.getLatitude());
        int lng = (int) (location.getLongitude());
        Log.d(TAG, "onLocationChanged() called with: location = [" + lat + "]");
        Log.d(TAG, "onLocationChanged() called with: location = [" + lng + "]");

        LatLng myposition = new LatLng(lat, lng);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location location = locationManager.getLastKnownLocation(provider);

        Double myLat = Double.valueOf(location.getLatitude());
        Double myLng = Double.valueOf(location.getLongitude());
        LatLng myposition = new LatLng(myLat, myLng);

        GoogleMap mMap = googleMap;

        mMap.addMarker(new MarkerOptions().position(myposition).title("ME"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myposition));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myposition,18));
        Log.d(TAG, "onMapReady() called with: googleMap = [" + myposition + "]");

        LatLng brockville1 =        new LatLng(44.592836, -75.6805454);
        LatLng brockville2 =        new LatLng(44.600352,-75.6945656);
        LatLng carletonPlace1 =     new LatLng(45.1352247,-76.1487062);
        LatLng carletonPlace2 =     new LatLng(45.1428875,-76.1463623);
        LatLng perth1 =             new LatLng(44.9009235, -76.2502683);
        LatLng smithsFalls1 =       new LatLng(44.9052747, -76.031819);
        LatLng smithsFalls2 =       new LatLng(44.9017021,-76.0192961);
        LatLng merrickville1 =      new LatLng(44.9163425,-75.8357144);
        mMap.addMarker(new MarkerOptions().position(brockville1).title      ("80 Pine St, Brockville, ON"));
        mMap.addMarker(new MarkerOptions().position(brockville2).title      ("Brockville Wesleyan Church, 33 Central Ave W, Brockville, ON"));
        mMap.addMarker(new MarkerOptions().position(carletonPlace1).title   ("St. Mary's Catholic Church, 28 Hawthorne Ave, Carleton Place, ON"));
        mMap.addMarker(new MarkerOptions().position(carletonPlace2).title   ("St. James Anglican Church, 225 Edmond St, Carleton Place, ON"));
        mMap.addMarker(new MarkerOptions().position(perth1).title           ("Perth Resturaunt, 23 Gore St E, Perth, ON"));
        mMap.addMarker(new MarkerOptions().position(smithsFalls1).title     ("91 Cornelia St, Smith Falls, ON"));
        mMap.addMarker(new MarkerOptions().position(smithsFalls2).title     ("Trinity United Church of Canada, 41 Market St N, Smiths Falls, ON"));
        mMap.addMarker(new MarkerOptions().position(merrickville1).title    ("106 Church St, Merrickville, ON"));
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
}
