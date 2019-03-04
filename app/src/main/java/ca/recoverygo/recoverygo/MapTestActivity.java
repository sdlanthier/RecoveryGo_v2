package ca.recoverygo.recoverygo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.StreetViewPanoramaOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import ca.recoverygo.recoverygo.adapters.InfoWindowCustom;
import ca.recoverygo.recoverygo.models.Meeting;
import ca.recoverygo.recoverygo.system.BaseActivity;
import ca.recoverygo.recoverygo.system.IMeetingInputActivity;

public class MapTestActivity extends BaseActivity implements
        IMeetingInputActivity,
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {

    private static final String TAG = "rg_MapTestActivity";

    private static final String COLLECTION_LOCATIONS = "locations";
    private static final int  REQUEST_LOCATION_PERMISSION = 1;
    public static final float INITIAL_ZOOM = 12f;
    private GoogleMap mMap;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ArrayList<GeoPoint> mMarker     = new ArrayList<>();
    private ArrayList<String>   mNotes      = new ArrayList<>();
    private ArrayList<String>   mAddress    = new ArrayList<>();
    private ArrayList<String>   mOrg        = new ArrayList<>();
    private ArrayList<String>   mGroup      = new ArrayList<>();
    private ArrayList<String>   mSite       = new ArrayList<>();

    private String addressLine  = "Unable To Determine";
    private String cityName     = "Unable To Determine";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locator);
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Pan the camera to your home address (in this case, Google HQ).
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            enableMyLocation(mMap);
            return;
        }
        /* ************************************************************************************** */

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = null;
        if (locationManager != null) {
            provider = locationManager.getBestProvider(criteria, false);
        }
        Location location = null;
        if (locationManager != null) {
            location = locationManager.getLastKnownLocation(provider);
        }

        assert location != null;
        Double myLat = location.getLatitude();
        Double myLng = location.getLongitude();

        LatLng home  = new LatLng(myLat, myLng);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, INITIAL_ZOOM));

        /* ************************************************************************************** */

        setMapLongClick(mMap);
        setPoiClick(mMap);
        setMapStyle(mMap);
        enableMyLocation(mMap);
        setInfoWindowClickToPanorama(mMap);
        setMarkers(mMap);
    }

    private void enableMyLocation(GoogleMap map) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "enableMyLocation: true");
            map.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
            Log.d(TAG, "enableMyLocation: request");
        }
    }

    public void setMarkers(final GoogleMap map){
        showMapProgressDialog();
        /* ************************************************************************************** */

        db.collection(COLLECTION_LOCATIONS)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

                    int i = 0;
                    while (i < mMarker.size()) {

                        String note     = mNotes.get(i);
                        String address  = mAddress.get(i);
                        GeoPoint marker = mMarker.get(i);
                        String org      = mOrg.get(i);
                        String group    = mGroup.get(i);
                        String site     = mSite.get(i);
                        String snippet  = org+" - "+site+"\n"+group+"\n"+note;

                        Double mkrLat = marker.getLatitude();
                        Double mkrLng = marker.getLongitude();
                        LatLng mkrposition = new LatLng(mkrLat, mkrLng);

                        switch (org) {
                            case "AL":
                                map.addMarker(new MarkerOptions().position(mkrposition).title(address).snippet(snippet)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.mkr_flag_al)));
                                break;
                            case "NA":
                                map.addMarker(new MarkerOptions().position(mkrposition).title(address).snippet(snippet)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.mkr_flag_na)));
                                break;
                            case "XX":
                                map.addMarker(new MarkerOptions().position(mkrposition).title(address).snippet(snippet)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.mkr_mylocation)));
                                break;
                            case "TR":
                                map.addMarker(new MarkerOptions().position(mkrposition).title(address).snippet(snippet)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.beds_48)));
                                break;
                            default:
                                map.addMarker(new MarkerOptions().position(mkrposition).title(address).snippet(snippet)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.mkr_flag_aa)));
                                break;
                        }
                        ++i;
                    }
                    hideMapProgressDialog();
                    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                } else {
                    hideMapProgressDialog();
                }
            }
        });

        mMap.setInfoWindowAdapter(new InfoWindowCustom(this));
        mMap.setOnMarkerClickListener(this);
    }

    private void setMapLongClick(final GoogleMap map) {

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                String snippet = String.format(Locale.getDefault(),
                        getString(R.string.lat_long_snippet),
                        latLng.latitude,
                        latLng.longitude);

                String lat = String.valueOf(latLng.latitude);
                String lng = String.valueOf(latLng.longitude);

                map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(getString(R.string.dropped_pin))
                        .snippet(snippet)
                        .icon(BitmapDescriptorFactory.defaultMarker
                                (BitmapDescriptorFactory.HUE_YELLOW)));
                createMeeting(lat,lng);
            }
        });
    }

    private void createMeeting(String lat,String lng) {

        double myLat = Double.parseDouble(lat);
        double myLng = Double.parseDouble(lng);

        final GeoPoint loc        = new GeoPoint(myLat, myLng);
        final String groupname    = "group";
        final String site         = "site";
        final String org          = "TR";
        final String note         = "meeting info";

        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(myLat, myLng, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Objects.requireNonNull(addresses).size() > 0) {
            addressLine = addresses.get(0).getAddressLine(0);
            cityName = addresses.get(0).getLocality();
        }

        AlertDialog alertDialog = new AlertDialog.Builder(MapTestActivity.this).create();
        alertDialog.setCancelable(true);
        alertDialog.setTitle("Create Meeting");
        alertDialog.setMessage("Address: " + addressLine + "\n" + "City: " + cityName);

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        Log.d(TAG, "onClick: create new record in: meetings");
                        dialog.dismiss();
                        showSaveProgressDialog();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                        Log.d(TAG, "onClick: user: "+userId);

                        DocumentReference newMeetingRef = db
                                .collection("locations")
                                .document();

                        String locationId = newMeetingRef.getId();
                        Timestamp createDate = Timestamp.now();

                        createNewMeeting(groupname, site, org, note, userId, loc, locationId, cityName, addressLine);
                        hideProgressDialog();

                        Meeting meeting = new Meeting(groupname, site, org, note, userId, loc, cityName, addressLine);

                        meeting.setGroupname    (groupname);
                        meeting.setSite         (site);
                        meeting.setOrg          (org);
                        meeting.setNote         (note);
                        meeting.setUser         (userId);
                        meeting.setLocation     (loc);
                        meeting.setAddress      (addressLine);
                        meeting.setCity         (cityName);
                        meeting.setLocation_id  (newMeetingRef.getId());

                        newMeetingRef.set(meeting).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    hideSaveProgressDialog();
                                } else {
                                    hideSaveProgressDialog();
                                }
                            }
                        });
                        // Intent intent = new Intent(MapTestActivity.this,MainActivity.class);
                        // startActivity(intent);
                    }
                });
        alertDialog.show();

    }

    private void setPoiClick(final GoogleMap map) {
        map.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(PointOfInterest poi) {
                Marker poiMarker = map.addMarker(new MarkerOptions()
                        .position(poi.latLng)
                        .title(poi.name));
                poiMarker.showInfoWindow();
                poiMarker.setTag(getString(R.string.poi));
            }
        });
    }

    private void setMapStyle(GoogleMap map) {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_style));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
    }

    private void setInfoWindowClickToPanorama(final GoogleMap map) {
        map.setOnInfoWindowClickListener(
                new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        // Check the tag
                        if (marker.getTag() == "poi") {
                            Log.d(TAG, "onInfoWindowClick: marker.getTag == poi");
                            // Set the position to the position of the marker
                            StreetViewPanoramaOptions options =
                                    new StreetViewPanoramaOptions().position(
                                            marker.getPosition());

                            SupportStreetViewPanoramaFragment streetViewFragment
                                    = SupportStreetViewPanoramaFragment
                                    .newInstance(options);

                            // Replace the fragment and add it to the backstack
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container,
                                            streetViewFragment)
                                    .addToBackStack(null).commit();
                        }
                        Log.d(TAG, "onInfoWindowClick: marker.getTag != poi");
                        Log.d(TAG, "onInfoWindowClick: pos = "+marker.getPosition());
                        Log.d(TAG, "onInfoWindowClick: tag = "+marker.getTag());
                        Log.d(TAG, "onInfoWindowClick: id = "+marker.getId());
                        Log.d(TAG, "onInfoWindowClick: title = "+marker.getTitle());
                        Log.d(TAG, "onInfoWindowClick: snippet = "+marker.getSnippet());
                        Log.d(TAG, "onInfoWindowClick: zIndex = "+marker.getZIndex());
                    }
                });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Change the map type based on the user's selection.
        switch (item.getItemId()) {
            case R.id.normal_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.hybrid_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.satellite_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.terrain_map:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void createNewMeeting(String group, String site, String org, String note, String user, GeoPoint location, String location_id,
                                 String address, String city) {

    }

    @Override
    public void updateMeeting(Meeting meeting) {

    }

    @Override
    public void onMeetingSelected(Meeting meeting) {

    }

    @Override
    public void deleteMeeting(Meeting meeting) {

    }
}