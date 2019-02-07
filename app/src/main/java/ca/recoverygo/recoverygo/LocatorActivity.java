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
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myposition,10));
        Log.d(TAG, "onMapReady() called with: googleMap = [" + myposition + "]");

        LatLng athens1 =            new LatLng(44.6267731,-75.9579179);

        LatLng brockville1 =        new LatLng(44.592836, -75.6805454);
        LatLng brockville2 =        new LatLng(44.600352,-75.6945656);
        LatLng brockville3 =        new LatLng(44.5910584,-75.6877514);

        LatLng carletonPlace1 =     new LatLng(45.1352247,-76.1487062);
        LatLng carletonPlace2 =     new LatLng(45.1428875,-76.1463623);
        LatLng carletonPlace3 =     new LatLng(45.1405046,-76.1466281);

        LatLng lanark1 =            new LatLng(45.0227758,-76.3814993);

        LatLng lyn1 =               new LatLng(44.5783907,-75.7874316);

        LatLng merrickville1 =      new LatLng(44.9163425,-75.8357144);
        LatLng merrickville2 =      new LatLng(44.6779682,-76.3984569);

        LatLng ottawa1 =            new LatLng(45.3638589,-75.7711273);
        LatLng ottawa2 =            new LatLng(45.4340614,-75.6638847);
        LatLng ottawa3 =            new LatLng(45.901282,-77.2752796);
        LatLng ottawa4 =            new LatLng(45.056152,-77.8548495);
        LatLng ottawa5 =            new LatLng(445.3887932,-75.7580722);
        LatLng ottawa6 =            new LatLng(45.3638589,-75.7711273);
        LatLng ottawa7 =            new LatLng(445.4135609,-75.7085326);
        LatLng ottawa8 =            new LatLng(45.3954835,-75.687361);
        LatLng ottawa9 =            new LatLng(45.4700058,-75.6916422);
        LatLng ottawa10 =           new LatLng(45.4370732,-76.3682254);

        LatLng perth1 =             new LatLng(44.9009235, -76.2502683);
        LatLng perth2 =             new LatLng(44.903174,-76.2544246);
        LatLng perth3 =             new LatLng(44.8994238,-76.246947);

        LatLng sharbotlake1 =       new LatLng(44.7713312,-76.6927184);

        LatLng smithsFalls1 =       new LatLng(44.9052747, -76.031819);
        LatLng smithsFalls2 =       new LatLng(44.9017021,-76.0192961);
        LatLng smithsFalls3 =       new LatLng(44.8993029,-76.0271187);

        LatLng westport1 =          new LatLng(45.0227758,-76.3814993);

        mMap.addMarker(new MarkerOptions().position(brockville1).title      ("St. Denis Catholic Church, 3 George St, Athens, ON"));

        mMap.addMarker(new MarkerOptions().position(brockville1).title      ("St. Lawrence Angligan Church, 80 Pine St, Brockville, ON"));
        mMap.addMarker(new MarkerOptions().position(brockville2).title      ("Brockville Wesleyan Church, 33 Central Ave W, Brockville, ON"));
        mMap.addMarker(new MarkerOptions().position(brockville3).title      ("Wall Street United Church, 10 Wall Street, Brockville, ON"));

        mMap.addMarker(new MarkerOptions().position(carletonPlace1).title   ("St. Mary's Catholic Church, 28 Hawthorne Ave, Carleton Place, ON"));
        mMap.addMarker(new MarkerOptions().position(carletonPlace2).title   ("St. James Anglican Church, 225 Edmond St, Carleton Place, ON"));
        mMap.addMarker(new MarkerOptions().position(carletonPlace3).title   ("Zion-Memorial United Church, 37 Franklin St, Carleton Place, ON"));

        mMap.addMarker(new MarkerOptions().position(merrickville1).title    ("106 Church St, Merrickville, ON"));
        mMap.addMarker(new MarkerOptions().position(merrickville2).title    ("Westport United Church, 27 Spring St, Merrickville, ON"));

        mMap.addMarker(new MarkerOptions().position(lanark1).title          ("Community Health Centre, 207 Robertson Dr, Lanark, ON"));

        mMap.addMarker(new MarkerOptions().position(lyn1).title             ("12 Perth St, Lyn, ON"));

        mMap.addMarker(new MarkerOptions().position(ottawa1).title          ("971 Woodroffe Avenue, Ottawa, ON"));
        mMap.addMarker(new MarkerOptions().position(ottawa2).title          ("317 Cody Avenue, Ottawa, ON"));
        mMap.addMarker(new MarkerOptions().position(ottawa3).title          ("1173 Victoria Street, Petawawa, ON"));
        mMap.addMarker(new MarkerOptions().position(ottawa4).title          ("1 Hastings Street South, Bancroft, ON"));
        mMap.addMarker(new MarkerOptions().position(ottawa5).title          ("207 Woodroffe Avenue, Ottawa, ON"));
        mMap.addMarker(new MarkerOptions().position(ottawa6).title          ("470 Roosevelt Ave, Ottawa, ON"));
        mMap.addMarker(new MarkerOptions().position(ottawa7).title          ("211 Bronson Ave, Ottawa, ON"));
        mMap.addMarker(new MarkerOptions().position(ottawa8).title          ("15 Aylmer Ave, Ottawa, ON"));
        mMap.addMarker(new MarkerOptions().position(ottawa9).title          ("5 Rue Saint-Arthur, Gatineau, QC"));
        mMap.addMarker(new MarkerOptions().position(ottawa10).title         ("2279 Alicia St, Arnprior, ON"));

        mMap.addMarker(new MarkerOptions().position(perth1).title           ("Perth Resturaunt, 23 Gore St E, Perth, ON"));
        mMap.addMarker(new MarkerOptions().position(perth2).title           ("First Baptist Church, D'Arcy St, Perth, ON"));
        mMap.addMarker(new MarkerOptions().position(perth3).title           ("St. James Angligan Church, 12 Harvey St, Perth, ON"));

        mMap.addMarker(new MarkerOptions().position(sharbotlake1).title     ("United Church Hall, Sharbot Lake, ON"));

        mMap.addMarker(new MarkerOptions().position(smithsFalls1).title     ("Cornelia Court, 91 Cornelia St, Smith Falls, ON"));
        mMap.addMarker(new MarkerOptions().position(smithsFalls2).title     ("Trinity United Church of Canada, 41 Market St N, Smiths Falls, ON"));
        mMap.addMarker(new MarkerOptions().position(smithsFalls3).title     ("2 George St South, Smiths Falls, ON"));



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
