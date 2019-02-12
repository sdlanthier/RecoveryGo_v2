package ca.recoverygo.recoverygo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class LocatorActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    private ArrayList<GeoPoint> mMarker  = new ArrayList<>();
    private ArrayList<String>   mNotes   = new ArrayList<>();
    private ArrayList<String>   mAddress = new ArrayList<>();
    private ArrayList<String>   mSite    = new ArrayList<>();
    private ArrayList<String>   mGroup   = new ArrayList<>();

    private static final String TAG = "RGO_LocatorActivity";
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
        int lat = (int) (location.getLatitude());
        int lng = (int) (location.getLongitude());
        LatLng myposition = new LatLng(lat, lng);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        final Location location = locationManager.getLastKnownLocation(provider);

        Double myLat = location.getLatitude();
        Double myLng = location.getLongitude();
        LatLng myposition = new LatLng(myLat, myLng);

        googleMap.addMarker(new MarkerOptions().position(myposition).title("ME"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(myposition));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myposition,10));
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser;
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Log.d(TAG, "updateUI: User is logged in");


            db.collection("locations").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                    mMarker.clear();
                    mNotes.clear();

                    for(DocumentSnapshot snapshot : documentSnapshots){

                        mMarker.clear();
                        mNotes.clear();
                        mAddress.clear();
                        mSite.clear();
                        mGroup.clear();

                        mMarker.add(snapshot.getGeoPoint("location"));
                        mNotes.add(snapshot.getString("note"));
                        mAddress.add(snapshot.getString("address"));
                        mSite.add(snapshot.getString("site"));
                        mGroup.add(snapshot.getString("groupname"));

                        for (int i = 0; i < mMarker.size(); i++) {
                            String note         = mNotes.get(i);
                            String address      = mAddress.get(i);
                            String site         = mSite.get(i);
                            String group        = mGroup.get(i);
                            GeoPoint marker = mMarker.get(i);
                            Log.d(TAG, "onEvent: "+marker);
                            Log.d(TAG, "onEvent: "+address);
                            Double mkrLat = marker.getLatitude();
                            Double mkrLng = marker.getLongitude();
                            LatLng mkrposition = new LatLng(mkrLat, mkrLng);
                            googleMap.addMarker(new MarkerOptions().position (mkrposition).title(address).snippet(note));
                        }
                        //Log.d(TAG, "onEvent: locationsList"+mMarker);
                        //Log.d(TAG, "onEvent: locationsList"+mNotes);

                    }

                }
            });

        } else {
            Log.d(TAG, "updateUI: User is logged out");
            Intent intent = new Intent(LocatorActivity.this, MainActivity.class);
            startActivity(intent);

        }
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++




/**
        LatLng ottawa71	    = new LatLng ( 45.38133,-75.66865);
        LatLng ottawa72	    = new LatLng ( 45.38184,-75.66189);
        LatLng ottawa73	    = new LatLng ( 45.38291,-75.73325);
        LatLng ottawa74 	= new LatLng ( 45.38418,-75.66162);
        LatLng ottawa75	    = new LatLng ( 45.38753,-75.61584);
        LatLng ottawa76 	= new LatLng ( 45.38848,-75.7556);
        LatLng ottawa77 	= new LatLng ( 45.38889,-75.66288);
        LatLng ottawa78	    = new LatLng ( 45.38958,-75.73378);
        LatLng ottawa79 	= new LatLng ( 45.39114,-75.75611);
        LatLng ottawa80	    = new LatLng ( 45.39237,-75.75488);
        LatLng ottawa81	    = new LatLng ( 45.39338,-76.18594);
        LatLng ottawa82 	= new LatLng ( 45.39529,-75.83894);
        LatLng ottawa83 	= new LatLng ( 45.3954,-75.6853);
        LatLng ottawa84	    = new LatLng ( 45.39568,-75.72593);
        LatLng ottawa85 	= new LatLng ( 45.39626,-75.84555);
        LatLng ottawa86	    = new LatLng ( 45.39729,-75.84645);
        LatLng ottawa87 	= new LatLng ( 45.39982,-75.72756);
        LatLng ottawa88	    = new LatLng ( 45.40208,-75.69141);
        LatLng ottawa89	    = new LatLng ( 45.40259,-75.6627);
        LatLng ottawa90	    = new LatLng ( 45.40369,-75.72469);
        LatLng ottawa91	    = new LatLng ( 45.40434,-75.74548);
        LatLng ottawa92	    = new LatLng ( 45.40463,-75.69083);
        LatLng ottawa93	    = new LatLng ( 45.40519,-75.70486);
        LatLng ottawa94	    = new LatLng ( 45.40929,-75.70156);
        LatLng ottawa95	    = new LatLng ( 45.41029,-75.70842);
        LatLng ottawa96	    = new LatLng ( 45.41345,-75.70666);
        LatLng ottawa97	    = new LatLng ( 45.41587,-75.68939);
        LatLng ottawa98 	= new LatLng ( 45.41923,-75.69113);
        LatLng ottawa99	    = new LatLng ( 45.42064,-75.70388);
        LatLng ottawa100	= new LatLng ( 45.42088,-75.69328);
        LatLng ottawa101	= new LatLng ( 45.42223,-75.67733);
        LatLng ottawa102	= new LatLng ( 45.42279,-75.67775);
        LatLng ottawa103	= new LatLng ( 45.42495,-75.65714);
        LatLng ottawa104	= new LatLng ( 45.42526,-75.66745);
        LatLng ottawa105	= new LatLng ( 45.42552,-75.68474);
        LatLng ottawa106	= new LatLng ( 45.4266,-75.68624);
        LatLng ottawa107	= new LatLng ( 45.42778,-75.72375);
        LatLng ottawa108	= new LatLng ( 45.43184,-75.69099);
        LatLng ottawa109	= new LatLng ( 45.43397,-75.66225);
        LatLng ottawa110	= new LatLng ( 45.43467,-76.36164);
        LatLng ottawa111	= new LatLng ( 45.43477,-76.36164);
        LatLng ottawa112	= new LatLng ( 45.43562,-75.7237);
        LatLng ottawa113	= new LatLng ( 45.437,-76.36614);
        LatLng ottawa114	= new LatLng ( 45.43731,-75.71435);
        LatLng ottawa115	= new LatLng ( 45.43732,-75.71434);
        LatLng ottawa116	= new LatLng ( 45.43846,-76.35108);
        LatLng ottawa117	= new LatLng ( 45.43893,-75.65365);
        LatLng ottawa118	= new LatLng ( 45.43951,-75.65405);
        LatLng ottawa119	= new LatLng ( 45.43956,-76.35048);
        LatLng ottawa120	= new LatLng ( 45.44041,-75.60447);
        LatLng ottawa121	= new LatLng ( 45.44761,-75.63181);
        LatLng ottawa122	= new LatLng ( 45.45422,-76.08769);
        LatLng ottawa123	= new LatLng ( 45.4573,-76.7777);
        LatLng ottawa124	= new LatLng ( 45.45735,-75.69938);
        LatLng ottawa125	= new LatLng ( 45.45763,-75.74363);
        LatLng ottawa126	= new LatLng ( 45.46183,-75.76462);
        LatLng ottawa127	= new LatLng ( 45.46187,-75.76242);
        LatLng ottawa128	= new LatLng ( 45.4626,-75.54348);
        LatLng ottawa129	= new LatLng ( 45.46653,-75.47117);
        LatLng ottawa130	= new LatLng ( 45.4695,-75.68888);
        LatLng ottawa131	= new LatLng ( 45.46998,-75.68938);
        LatLng ottawa132	= new LatLng ( 45.47109,-76.68152);
        LatLng ottawa133	= new LatLng ( 45.4715,-76.2138);
        LatLng ottawa134	= new LatLng ( 45.47164,-75.50844);
        LatLng ottawa135	= new LatLng ( 45.47434,-76.69112);
        LatLng ottawa136	= new LatLng ( 45.47672,-75.53509);
        LatLng ottawa137	= new LatLng ( 45.48488,-75.6494);
        LatLng ottawa138	= new LatLng ( 45.48608,-78.2406);
        LatLng ottawa139	= new LatLng ( 45.48735,-77.67892);
        LatLng ottawa140	= new LatLng ( 45.48738,-75.52145);
        LatLng ottawa141	= new LatLng ( 45.48821,-75.7041);
        LatLng ottawa142	= new LatLng ( 45.491,-75.70276);
        LatLng ottawa143	= new LatLng ( 45.49465,-75.58081);
        LatLng ottawa144	= new LatLng ( 45.49886,-75.60812);
        LatLng ottawa145	= new LatLng ( 45.50831,-75.78672);
        LatLng ottawa146	= new LatLng ( 45.54128,-77.09969);
        LatLng ottawa147	= new LatLng ( 45.54673,-75.29379);
        LatLng ottawa148	= new LatLng ( 45.54829,-76.89633);
        LatLng ottawa149	= new LatLng ( 45.55833,-74.88185);
        LatLng ottawa150	= new LatLng ( 45.57443,-77.25975);
        LatLng ottawa151	= new LatLng ( 45.58808,-75.41504);
        LatLng ottawa152	= new LatLng ( 45.596,-75.24547);
        LatLng ottawa153	= new LatLng ( 45.60362,-76.49152);
        LatLng ottawa154	= new LatLng ( 45.60586,-74.61617);
        LatLng ottawa155	= new LatLng ( 45.60591,-74.59124);
        LatLng ottawa156	= new LatLng ( 45.61048,-74.60472);
        LatLng ottawa157	= new LatLng ( 45.61058,-74.6038);
        LatLng ottawa158	= new LatLng ( 45.62471,-76.88136);
        LatLng ottawa159	= new LatLng ( 45.62554,-74.60383);
        LatLng ottawa160	= new LatLng ( 45.62779,-74.58991);
        LatLng ottawa161	= new LatLng ( 45.65467,-74.94534);
        LatLng ottawa162	= new LatLng ( 45.67485,-74.40705);
        LatLng ottawa163	= new LatLng ( 45.67799,-74.41338);
        LatLng ottawa164	= new LatLng ( 45.73443,-76.85929);
        LatLng ottawa165	= new LatLng ( 45.7853,-75.09956);
        LatLng ottawa166	= new LatLng ( 45.80769,-77.15264);
        LatLng ottawa167	= new LatLng ( 45.82428,-77.13099);
        LatLng ottawa168	= new LatLng ( 45.82472,-77.09828);
        LatLng ottawa169	= new LatLng ( 45.82475,-77.11923);
        LatLng ottawa170	= new LatLng ( 45.82508,-77.11522);
        LatLng ottawa171	= new LatLng ( 45.82548,-77.09078);
        LatLng ottawa172	= new LatLng ( 45.82652,-77.1095);
        LatLng ottawa173	= new LatLng ( 45.83207,-77.14112);
        LatLng ottawa174	= new LatLng ( 45.84806,-77.19778);
        LatLng ottawa175	= new LatLng ( 45.88389,-75.05873);
        LatLng ottawa176	= new LatLng ( 45.90128,-77.27527);
        LatLng ottawa177	= new LatLng ( 46.09684,-76.04527);
        LatLng ottawa178	= new LatLng ( 46.13448,-77.55474);
        LatLng ottawa179	= new LatLng ( 46.3691,-75.98265);
        LatLng toronto1	    = new LatLng (43.6678888,-79.398372);
        LatLng toronto2	    = new LatLng (43.714011,-79.7084381);
        LatLng toronto3	    = new LatLng (43.6888706,-79.7633566);
        LatLng toronto4	    = new LatLng (43.6033862,-79.5039018);
        LatLng toronto5	    = new LatLng (44.0021937,-79.4677216);
        LatLng toronto6	    = new LatLng (43.6467355,-79.3856648);
        LatLng toronto7	    = new LatLng (43.6889693,-79.3957377);
        LatLng toronto8	    = new LatLng (43.666532,-79.38097);
        LatLng toronto9	    = new LatLng (43.7425258,-79.2697348);
        LatLng toronto10	 = new LatLng (43.6658993,-79.4116382);
        LatLng toronto11	 = new LatLng (43.8721064,-79.4381818);
        LatLng toronto12	 = new LatLng (43.4737973,-79.6981393);
        LatLng toronto13	 = new LatLng (43.9232918,-79.2713892);
        LatLng toronto14	 = new LatLng (43.680053,-79.3384901);
        LatLng toronto15	 = new LatLng (43.6387865,-79.5487532);
        LatLng toronto16	 = new LatLng (43.5982299,-79.6044515);
        LatLng toronto17	 = new LatLng (44.052113,-79.453559);
        LatLng toronto18	 = new LatLng (43.6539239,-79.3766814);
        LatLng toronto19	 = new LatLng (43.6890637,-79.314454);
        LatLng toronto20	 = new LatLng (43.6793622,-79.3461484);
        LatLng toronto21	 = new LatLng (43.5101946,-79.6498599);
        LatLng toronto22	 = new LatLng (43.684868,-79.5282215);
        LatLng toronto23	 = new LatLng (43.7803978,-79.2045492);
        LatLng toronto24	 = new LatLng (43.8631394,-79.7182505);
        LatLng toronto25	 = new LatLng (43.5788,-79.709826);
        LatLng toronto26	 = new LatLng (43.6904519,-79.763629);
        LatLng toronto27	 = new LatLng (43.6499665,-79.5449078);
        LatLng toronto28	 = new LatLng (43.6629058,-79.3727357);
        LatLng toronto29	 = new LatLng (43.7581809,-79.5128075);
        LatLng toronto30	 = new LatLng (43.85527,-79.02025);
        LatLng toronto31	 = new LatLng (43.7516554,-79.3842254);
        LatLng toronto32	 = new LatLng (43.6401166,-79.4345027);
        LatLng toronto33	 = new LatLng (44.322012,-79.2149509);
        LatLng toronto34	 = new LatLng (43.824552,-79.417142);
        LatLng toronto35	 = new LatLng (43.6566764,-79.4081527);
        LatLng toronto36	 = new LatLng (43.6854215,-79.4860886);
        LatLng toronto37	 = new LatLng (43.7298973,-79.4827881);
        LatLng toronto38	 = new LatLng (43.828172,-79.101024);
        LatLng toronto39	 = new LatLng (43.5791901,-79.758828);
        LatLng toronto40	 = new LatLng (43.7078359,-79.3885127);
        LatLng toronto41	 = new LatLng (43.7015012,-79.3955073);
        LatLng toronto42	 = new LatLng (43.6653745,-79.453121);
        LatLng toronto43	 = new LatLng (43.6693766,-79.4328238);
        LatLng toronto44	 = new LatLng (43.721503,-79.5038354);
        LatLng toronto45	 = new LatLng (43.7800598,-79.3022485);
        LatLng toronto46	 = new LatLng (44.2439062,-79.4682909);
        LatLng toronto47	 = new LatLng (43.8737179,-79.4383234);
        LatLng toronto48	 = new LatLng (44.2122778,-79.4638728);
        LatLng toronto49	 = new LatLng (43.6664195,-79.4039092);
        LatLng toronto50	 = new LatLng (44.0555363,-79.4645402);
        LatLng toronto51	 = new LatLng (43.6396553,-79.5449881);
        LatLng toronto52	 = new LatLng (43.668835,-79.76169);
        LatLng toronto53	 = new LatLng (43.6841427,-79.3723562);
        LatLng toronto54	 = new LatLng (43.7019765,-79.3737168);
        LatLng toronto55	 = new LatLng (43.659004,-79.4290141);
        LatLng toronto56	 = new LatLng (43.9987712,-79.4651134);
        LatLng toronto57	 = new LatLng (43.8795008,-79.2610469);
        LatLng toronto58	 = new LatLng (43.6659441,-79.3480958);
        LatLng toronto59	 = new LatLng (43.7234429,-79.5724263);
        LatLng toronto60	 = new LatLng (43.7061167,-79.3095566);
        LatLng toronto61	 = new LatLng (43.733924,-79.719183);
        LatLng toronto62	 = new LatLng (43.6022764,-79.5046802);
        LatLng toronto63	 = new LatLng (43.7671138,-79.1921444);
        LatLng toronto64	 = new LatLng (43.7625913,-79.2125038);
        LatLng toronto65	 = new LatLng (43.7866031,-79.4247694);
        LatLng toronto66	 = new LatLng (43.6533856,-79.4059423);
        LatLng toronto67	 = new LatLng (43.5529199,-79.587502);
        LatLng toronto68	 = new LatLng (44.0537459,-79.4585384);
        LatLng toronto69	 = new LatLng (43.4650144,-79.6930078);
        LatLng toronto70	 = new LatLng (43.6660231,-79.405681);
        LatLng toronto71	 = new LatLng (43.6710302,-79.3011646);
        LatLng toronto72	 = new LatLng (43.7868768,-79.5997413);
        LatLng toronto73	 = new LatLng (43.7676221,-79.3905603);
        LatLng toronto74	 = new LatLng (43.7187607,-79.3655719);
        LatLng toronto75	 = new LatLng (43.6863291,-79.4058983);

        LatLng 	kingston1	 = new LatLng (44.2177581,-76.5720113);
        LatLng 	kingston2   = new LatLng (44.2273039,-76.4934767);
        LatLng 	kingston3   = new LatLng (44.2808982,-76.5458307);
        LatLng 	kingston4	= new LatLng (44.2193102,-76.5735446);
        LatLng 	kingston5	= new LatLng (44.2332955,-76.4869538);
        LatLng 	kingston6	= new LatLng (44.2308559,-76.4881091);
        LatLng 	kingston7	= new LatLng (44.2312519,-76.49051);
        LatLng 	kingston8	= new LatLng (44.4064276,-76.6605125);
        LatLng 	kingston9	= new LatLng (44.2397427,-76.6145503);
        LatLng 	kingston10  = new LatLng (44.2402412,-76.6124345);
        LatLng 	kingston11 = new LatLng (44.2355627,-76.5009797);
        LatLng 	kingston12 = new LatLng (44.2758577,-76.7210332);
        LatLng 	kingston13 = new LatLng (44.24345,-76.5222605);
        LatLng 	kingston14 = new LatLng (44.2299227,-76.490394);
        LatLng 	kingston15 = new LatLng (44.2158628,-76.5303426);
        LatLng 	kingston16 = new LatLng (44.2241659,-76.494898);
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++



googleMap.addMarker(new MarkerOptions().position (ottawa71).title ("Hope in Recovery, 1525 Bank Street, Ottawa"));
        googleMap.addMarker(new MarkerOptions().position (ottawa72).title ("Daily Reflections, 2400 Alta Vista Drive, Ottawa"));
        googleMap.addMarker(new MarkerOptions().position (ottawa73).title ("Back to Basics, 1303 Leaside Avenue, Ottawa"));
        googleMap.addMarker(new MarkerOptions().position (ottawa74).title ("Willing to Change, 2345 Alta Vista Drive, Ottawa 45.38522"));
        googleMap.addMarker(new MarkerOptions().position (ottawa75).title ("12 Steps to Serenity, 2255 St. Laurent Boulevard, Ottawa"));
        googleMap.addMarker(new MarkerOptions().position (ottawa76).title ("Lunch with Bill, 470 Roosevelt Avenue, Ottawa"));
        googleMap.addMarker(new MarkerOptions().position (ottawa77).title ("Oasis, 2203 Alta Vista Drive, Ottawa"));
        googleMap.addMarker(new MarkerOptions().position (ottawa78).title ("Freedom, 630 Island Park Drive, Ottawa"));
        googleMap.addMarker(new MarkerOptions().position (ottawa79).title ("Sunday Morning Venture Group, 389 Richmond Road, Ottawa"));
        googleMap.addMarker(new MarkerOptions().position (ottawa80).title ("Sunday Night 12 Step, 347 Richmond Road, Ottawa"));
        googleMap.addMarker(new MarkerOptions().position (ottawa81).title ("Kinburn Big Book, 3045 Kinburn Side Road, Ottawa"));
        googleMap.addMarker(new MarkerOptions().position (ottawa82).title ("Joy of Living, 164 rue Principale, Gatineau"));
        googleMap.addMarker(new MarkerOptions().position (ottawa83).title ("Wednesday Noon Glebe, 15 Aylmer Avenue, Ottawa"));
        googleMap.addMarker(new MarkerOptions().position (ottawa84).title ("Step By Step Saturday Morning Big Book, 579 Parkdale Avenue, Ottawa"));
        googleMap.addMarker(new MarkerOptions().position (ottawa85).title ("Aylmer Triple A, St. Andrew's Presbyterian Church"));
        googleMap.addMarker(new MarkerOptions().position (ottawa86).title ("Notre-Père, Centre alimentaire"));
        googleMap.addMarker(new MarkerOptions().position (ottawa87).title ("Fellowship, 29 Parkdale Avenue, Ottawa"));
        googleMap.addMarker(new MarkerOptions().position (ottawa88).title ("Attitude of Gratitude Women's Group, 175 Third Avenue, Ottawa"));
        googleMap.addMarker(new MarkerOptions().position (ottawa89).title ("Alta Vista Open Door, 1758 Alta Vista Drive, Ottawa"));
        googleMap.addMarker(new MarkerOptions().position (ottawa90).title ("Rainbow     B-United, 1064 Wellington Street West, Ottawa"));
        googleMap.addMarker(new MarkerOptions().position (ottawa91).title ("Awakening Group, 149 Cowley Avenue, Ottawa"));
        googleMap.addMarker(new MarkerOptions().position (ottawa92).title ("First AAvenue to Recovery, 181 First Avenue, Ottawa"));
        googleMap.addMarker(new MarkerOptions().position (ottawa93).title ("New Life - New Hope, 241 Bell Street North, Ottawa"));
        googleMap.addMarker(new MarkerOptions().position (ottawa94).title ("McNabb Sunday Morning, 180 Percy Street, Ottawa"));
        googleMap.addMarker(new MarkerOptions().position (ottawa95).title ("The Labyrinth Young People's Group, 760 Somerset Street West, Ottawa"));
        googleMap.addMarker(new MarkerOptions().position (ottawa96).title ("Nueva Esperanza, 211 Bronson Avenue, Ottawa"));
        googleMap.addMarker(new MarkerOptions().position (ottawa97).title ("Tuesday/Friday Beginners, 320 Jack Purcell Lane, Ottawa"));
        googleMap.addMarker(new MarkerOptions().position (ottawa98).title ("Uptown, 120 Lisgar Street, Ottawa"));
        googleMap.addMarker(new MarkerOptions().position (ottawa99).title ("The Podium/Hill Group, 82 Kent Street, Ottawa"));
        googleMap.addMarker(new MarkerOptions().position (ottawa100).title ("Spirit of Hope, 140 Laurier Avenue West, Ottawa"));
        googleMap.addMarker(new MarkerOptions().position (ottawa101).title ("Fraternité Je suis responsable, Centre Communautaire Côte-de-Sable"));
        googleMap.addMarker(new MarkerOptions().position (ottawa102).title ("Secular Sobriety Group, 250 Somerset Street East, Ottawa"));
        googleMap.addMarker(new MarkerOptions().position (ottawa103).title ("Hand in Hand, 33 Quill Street, Ottawa"));
        googleMap.addMarker(new MarkerOptions().position (ottawa104).title ("Renaissance, Centre Communautaire Overbrook "));
        googleMap.addMarker(new MarkerOptions().position (ottawa105).title ("Laurier-Sandy Hill, 151 Laurier Avenue East, Ottawa"));
        googleMap.addMarker(new MarkerOptions().position (ottawa106).title ("Live and Let Live, Gay/Lesbian, 473 Cumberland Street, Ottawa"));
        googleMap.addMarker(new MarkerOptions().position (ottawa107).title ("Sagesse, Église Catholique Portugaise du Saint-Esprit"));
        googleMap.addMarker(new MarkerOptions().position (ottawa108).title ("Together We Can, 233 Murray Street, Ottawa"));
        googleMap.addMarker(new MarkerOptions().position (ottawa109).title ("Vanier Early Birds, 317 Cody Avenue, Ottawa"));
        googleMap.addMarker(new MarkerOptions().position (ottawa110).title ("A Step Ahead Group, 16 Edward Street, Arnprior"));
        googleMap.addMarker(new MarkerOptions().position (ottawa111).title ("Arnprior Big Book Study, 16 Edward Street, Arnprior"));
        googleMap.addMarker(new MarkerOptions().position (ottawa112).title ("Hull Central, École secondaire de l’Île"));
        googleMap.addMarker(new MarkerOptions().position (ottawa113).title ("As Bill Sees It, 279 Alicia Street, Arnprior"));
        googleMap.addMarker(new MarkerOptions().position (ottawa114).title ("Du Portage, Église Notre-Dame de l'ile"));
        googleMap.addMarker(new MarkerOptions().position (ottawa115).title ("Hull Liberty, Paroisse Notre-Dame-De-L'Ile"));
        googleMap.addMarker(new MarkerOptions().position (ottawa116).title ("Steps & Traditions Group, 295 Albert Street, Arnprior"));
        googleMap.addMarker(new MarkerOptions().position (ottawa117).title ("Midi-Vanier, Maison Fraternité"));
        googleMap.addMarker(new MarkerOptions().position (ottawa118).title ("Midi-Vanier, Maison Fraternité"));
        googleMap.addMarker(new MarkerOptions().position (ottawa119).title ("Arnprior Saturday Night, 295 Albert Street, Arnprior 45.44037"));
        googleMap.addMarker(new MarkerOptions().position (ottawa120).title ("12 & 12 Beacon Hill, 55 Appleford Street, Ottawa"));
        googleMap.addMarker(new MarkerOptions().position (ottawa121).title ("Easy Does It Women's Group, 550 Codd's Road, Ottawa"));
        googleMap.addMarker(new MarkerOptions().position (ottawa122).title ("Woodlawn, 3794 Woodkilton Road, Ottawa"));
        googleMap.addMarker(new MarkerOptions().position (ottawa123).title ("First Things Discussion Meeting, 477 Stone Road, Renfrew"));
        googleMap.addMarker(new MarkerOptions().position (ottawa124).title ("Du bonheur, Église St-François-de-Sales"));
        googleMap.addMarker(new MarkerOptions().position (ottawa125).title ("Midi-Hawkesbury, Église Holy Trinity"));
        googleMap.addMarker(new MarkerOptions().position (ottawa126).title ("Midi partage, Église St-Pierre-Chanel"));
        googleMap.addMarker(new MarkerOptions().position (ottawa127).title ("Amour et Fraternité, Église St-Pierre-Chanel"));
        googleMap.addMarker(new MarkerOptions().position (ottawa128).title ("Voyageur, Terrasses Montfort Renaissance"));
        googleMap.addMarker(new MarkerOptions().position (ottawa129).title ("Orléans, Paroisse Sainte-Marie d'Orléans"));
        googleMap.addMarker(new MarkerOptions().position (ottawa130).title ("Midi-Réflexion, Église St-Rosaire"));
        googleMap.addMarker(new MarkerOptions().position (ottawa131).title ("Partage du Gros Livre, Église St-Rosaire"));
        googleMap.addMarker(new MarkerOptions().position (ottawa132).title ("Serenity Group, 291 Plaunt Street South, Renfrew"));
        googleMap.addMarker(new MarkerOptions().position (ottawa133).title ("Harbour Group, 176 Shirreff Street, Ottawa"));
        googleMap.addMarker(new MarkerOptions().position (ottawa134).title ("Sunday Night Big Book Study Group, 1485 Duford Street, Ottawa"));
        googleMap.addMarker(new MarkerOptions().position (ottawa135).title ("Third Tradition Group, 16 Argyle Street North, Renfrew"));
        googleMap.addMarker(new MarkerOptions().position (ottawa136).title ("Loving-Kindness Meditation Meeting, 1111 Orleans Boulevard, Ottawa 45.48256"));
        googleMap.addMarker(new MarkerOptions().position (ottawa137).title ("Main dans la main, Église Anglicaine St-Georges"));
        googleMap.addMarker(new MarkerOptions().position (ottawa138).title ("Whitney East Gate Group, 9 Third Avenue, Whitney"));
        googleMap.addMarker(new MarkerOptions().position (ottawa139).title ("Barry's Bay Group, 32 Inglis Street, Barry's Bay"));
        googleMap.addMarker(new MarkerOptions().position (ottawa140).title ("Stepping Ahead Orleans Hub, 109 Larch Crescent, Ottawa"));
        googleMap.addMarker(new MarkerOptions().position (ottawa141).title ("Eurêka, Église St-Jean XXIII"));
        googleMap.addMarker(new MarkerOptions().position (ottawa142).title ("Le Réveil, Centre résidentiel communautaire"));
        googleMap.addMarker(new MarkerOptions().position (ottawa143).title ("De retour sur terre, Centre communautaire St-Gérard"));
        googleMap.addMarker(new MarkerOptions().position (ottawa144).title ("Amour et Sérénité, Église Ste-Rose-de-Lima"));
        googleMap.addMarker(new MarkerOptions().position (ottawa145).title ("Friday Night Chelsea Big Book Meeting, 537 Route 105, Chelsea 45.52240"));
        googleMap.addMarker(new MarkerOptions().position (ottawa146).title ("Gold Bond, 99 Victoria Street, Eganville"));
        googleMap.addMarker(new MarkerOptions().position (ottawa147).title ("Rockland Hometown Group, 2178 Laurier Street, Rockland"));
        googleMap.addMarker(new MarkerOptions().position (ottawa148).title ("Douglas Group, 1766 Barr Line, Douglas"));
        googleMap.addMarker(new MarkerOptions().position (ottawa149).title ("Saint-Joseph d'Alfred, Paroisse Saint-Victor"));
        googleMap.addMarker(new MarkerOptions().position (ottawa150).title ("Golden Lake Group, 96 Chibekana Inamo, Golden Lake"));
        googleMap.addMarker(new MarkerOptions().position (ottawa151).title ("Retour à la Vie, Our Lady of Victory Church"));
        googleMap.addMarker(new MarkerOptions().position (ottawa152).title ("Avec Amour, Église St-Jean-L'Évangéliste"));
        googleMap.addMarker(new MarkerOptions().position (ottawa153).title ("Shawville Group, St. Paul’s Anglican Church"));
        googleMap.addMarker(new MarkerOptions().position (ottawa154).title ("Sunday Steps, 440 Stanley Street, Hawkesbury"));
        googleMap.addMarker(new MarkerOptions().position (ottawa155).title ("Harmonie, Centre Chrétien Viens et Vois"));
        googleMap.addMarker(new MarkerOptions().position (ottawa156).title ("Hawkesbury Sunray's, 166 John Street, Hawkesbury"));
        googleMap.addMarker(new MarkerOptions().position (ottawa157).title ("De la Trinité, Centre Guindon"));
        googleMap.addMarker(new MarkerOptions().position (ottawa158).title ("Way of Life, 27 Main Street, Cobden"));
        googleMap.addMarker(new MarkerOptions().position (ottawa159).title ("Saint-Ludger, >Église Notre-Dame des Sept Douleurs"));
        googleMap.addMarker(new MarkerOptions().position (ottawa160).title ("Du Canal, Centre de Santé d'Argenteuil"));
        googleMap.addMarker(new MarkerOptions().position (ottawa161).title ("Amour et partage, Chalet en bois rond"));
        googleMap.addMarker(new MarkerOptions().position (ottawa162).title ("Happy Days, Royal Canadian Legion"));
        googleMap.addMarker(new MarkerOptions().position (ottawa163).title ("Joie de vivre, Église catholique Saint-Louis-de-France"));
        googleMap.addMarker(new MarkerOptions().position (ottawa164).title ("Beachburg New Freedom, 9 Hannah Street, Beachburg"));
        googleMap.addMarker(new MarkerOptions().position (ottawa165).title ("Amour et détente, Paroisse St-Casimir-de-Ripon"));
        googleMap.addMarker(new MarkerOptions().position (ottawa166).title ("Centre Group, 39 Shalom Street, Pembroke"));
        googleMap.addMarker(new MarkerOptions().position (ottawa167).title ("West End Group, 295 1st Avenue North, Pembroke"));
        googleMap.addMarker(new MarkerOptions().position (ottawa168).title ("Just For Today Group, 503 Alfred Street, Pembroke"));
        googleMap.addMarker(new MarkerOptions().position (ottawa169).title ("Step Sisters Women's Group, 257 Pembroke Street West, Pembroke"));
        googleMap.addMarker(new MarkerOptions().position (ottawa170).title ("The Hand of AA, 42 Renfrew Street, Pembroke"));
        googleMap.addMarker(new MarkerOptions().position (ottawa171).title ("Search for Serenity, 860 Pembroke Street East, Pembroke"));
        googleMap.addMarker(new MarkerOptions().position (ottawa172).title ("New Hope Group, 202 Pembroke Street East, Pembroke"));
        googleMap.addMarker(new MarkerOptions().position (ottawa173).title ("Windsor New Freedom Group, 1111 Pembroke Street West, Pembroke"));
        googleMap.addMarker(new MarkerOptions().position (ottawa174).title ("Stepping Stones, 34 Barry Street, Pembroke"));
        googleMap.addMarker(new MarkerOptions().position (ottawa175).title ("Groupe Touristique, Église Chénéville"));
        googleMap.addMarker(new MarkerOptions().position (ottawa176).title ("Triangle Group, 1173 Victoria Street, Petawawa"));
        googleMap.addMarker(new MarkerOptions().position (ottawa177).title ("Par La Grâce"));
        googleMap.addMarker(new MarkerOptions().position (ottawa178).title ("River Group, 34465 Highway 17, Point Alexander"));
        googleMap.addMarker(new MarkerOptions().position (ottawa179).title ("Algonquin Group, Community Hall"));
        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        googleMap.addMarker(new MarkerOptions().position (toronto1).title ("O.I.S.E. Building, 252 Bloor St W, Toronto, ON M5R, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto2).title ("Emmanuel United Church, 420 Balmoral Dr, Brampton, ON L6T 2T5, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto3).title ("Grace United Church, 156 Main St N, Brampton, ON L6V 1N9, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto4).title ("Lakeshore Community Centre, 185 Fifth St, Etobicoke, ON M8V 2Z5, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto5).title ("Our Lady Of Grace Church, 15347 Yonge St, Aurora, ON L4G 1N7, Canada	"));
        googleMap.addMarker(new MarkerOptions().position (toronto6).title ("St. Andrew&#039;s Hall, 73 Simcoe St, Toronto, ON M5J 1W9, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto7).title ("Calvin Presbyterian Church, 26 Delisle Ave, Toronto, ON M4V 1S5, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto8).title ("Church St. Community Centre, 519 Church St, Toronto, ON M4Y 2C9, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto9).title ("Jack Goodlad Community Centre, 929 Kennedy Rd, Scarborough, ON M1K 2E9, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto10).title ("Paulist Centre, 830 Bathurst St, Toronto, ON M5R 3G1, Canada	"));
        googleMap.addMarker(new MarkerOptions().position (toronto11).title ("St. Mary&#039;s Anglican Church Hall, 10030 Yonge St, Richmond Hill, ON L4C 1T8, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto12).title ("St. Simon&#039;s Anglican Church, 1450 Litchfield Rd, Oakville, ON L6H 4R9, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto13).title ("Wideman Mennonite Church, 10530 ON-48, Markham, ON L3P 3J3, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto14).title ("Oasis Club, 921 Danforth Ave, Toronto, ON M4J 1L8, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto15).title ("St. Matthews Anglican Church, 3962 Bloor St W, Etobicoke, ON M9B 1M3, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto16).title ("Salvation Army, 3173 Cawthra Rd, Mississauga, ON L5A 2X4, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto17).title ("York Region Police District 1, 240 Prospect St, Newmarket, ON L3Y 3T9, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto18).title ("Metropolitan United Church, 56 Queen St E, Toronto, ON M5C 2Z3, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto19).title ("Church Of The Resurrection, 1100 Woodbine Ave, East York, ON M4C 4C7, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto20).title ("Holy Name Church, 71 Gough Ave, Toronto, ON M4K 3N9, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto21).title ("Sheridan United Church, 2501 Truscott Dr, Mississauga, ON L5J 2B3, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto22).title ("St. Matthias Anglican Church, 1428 Royal York Rd, Etobicoke, ON M9P 3A7, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto23).title ("Centenary Health Centre, 2867 Ellesmere Rd, Scarborough, ON M1E 4B9, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto24).title ("Exchange, 55 Healey Rd, Bolton, ON L7E 5A2, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto25).title ("St. Andrew&#039;s Presbyterian Church, 293 Queen St S, Mississauga, ON L5M 1L9, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto26).title ("Central Public School, 24 Alexander St, Brampton, ON L6V 1J7, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto27).title ("Olivet Church, 279 Burnhamthorpe Rd, Etobicoke, ON M9B 1Z6, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto28).title ("St. Luke&#039;s United Church, 353 Sherbourne St, Toronto, ON M5A 2S3, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto29).title ("University Presbyterian Church, 1830 Finch Ave W, North York, ON M3N, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto30).title ("Ajax Baptist Church, 46 Angus Dr, Ajax, ON L1S 5C3, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto31).title ("Bayview United Church, 2609 Bayview Ave, North York, ON M2L 1B3, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto32).title ("Bonar-Parkdale Presbyterian Church, 250 Dunn Ave, Toronto, ON M6K, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto33).title ("Cedardale Church Of The Nazarene, 471 Pefferlaw Rd, Pefferlaw, ON L0E 1N0, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto34).title ("Christ The King Lutheran Church, 149 Bay Thorn Dr, Thornhill, ON L3T 3V2, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto35).title ("College Street United Church, 452 College St, Toronto, ON M6G 1A1, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto36).title ("Commercial Storefront, 1049 Weston Rd, York, ON M6N 3R9, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto37).title ("Downsview United Church, 2822 Keele St, North York, ON M3M 2G6, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto38).title ("Dunbarton United Church, 1066 Dunbarton Rd, Pickering, ON L1V 1G8, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto39).title ("Eden United Church, 3051 Battleford Rd, Mississauga, ON L5N 5Z9, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto40).title ("First Christian Reformed Church Of Toronto, 67 Taunton Rd, Toronto, ON M4S 2P2, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto41).title ("Glebe Road Churches, 20 Glebe Rd E, Toronto, ON M4S 1N6, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto42).title ("Grupo Espanol, 1560 Dupont St, Toronto, ON M6P 3S6, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto43).title ("Grupo Espanol, 1070 Dovercourt Rd, Toronto, ON M6H 2X8, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto44).title ("Grupo Espanol, 1477 Wilson Ave, North York, ON M3M 1J5, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto45).title ("Holy Spirit R.C. Church, 3526 Sheppard Ave E, Scarborough, ON M1T 3K7, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto46).title ("Keswick United Church, 177 Church St, Georgina, ON L4P, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto47).title ("M.L. Mcconaghy Seniors Centre, 10100 Yonge St, Richmond Hill, ON L4C 1T8, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto48).title ("Maple Hill Baptist Church, 215 Glenwoods Ave, Keswick, ON L4P 2E2, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto49).title ("Miles Nadal JCC Community Centre, 750 Spadina Ave, Toronto, ON M5S, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto50).title ("New Hope Methodist Church, 337 Queen St, Newmarket, ON L3Y 2G5, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto51).title ("Our Lady Of Peace Church, 3914 Bloor St W, Etobicoke, ON M9B 1L7, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto52).title ("Peel Chemical Withdrawal Centre, 135 McLaughlin Rd S, Brampton, ON L6Y 2C8, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto53).title ("Rosedale United Church, 159 Roxborough Dr, Toronto, ON M4W 1X7, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto54).title ("St Cuthbert's Anglican Church, 1399 Bayview Ave, East York, ON M4G 3A6, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto55).title ("St Paul&#039;s Presbyterian Church, 100 Hepbourne St, Toronto, ON M6H 1K5, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto56).title ("St. Andrew&#039;s Presbyterian Church, 32 Mosley St, Aurora, ON L4G 1G9, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto57).title ("St. Andrew&#039;s Presbyterian Church, 143 Main St N, Markham, ON L3P 1Y3, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto58).title ("St. Ann&#039;s Catholic Church, 120 First Ave, Toronto, ON M4M 1X1, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto59).title ("St. Benedict&#039;s Church, 2194 Kipling Ave, Etobicoke, ON M9W 4L2, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto60).title ("St. Columba And All Hallows Church, 2723 St Clair Ave E, East York, ON M4B 1M8, Canada"));
        googleMap.addMarker(new MarkerOptions().position (toronto61).title ("St. Judes Anglican Church, 1000 Central Park Dr, Brampton, ON L6S 3L6, Canada	"));
        googleMap.addMarker(new MarkerOptions().position (toronto62).title ("St. Margaret&#039;s Church, 156 Sixth St, Etobicoke, ON M8V 3A5, Canada	"));
        googleMap.addMarker(new MarkerOptions().position (toronto63).title ("St. Margaret&#039;s In The Pines Church, 4130 Lawrence Ave E, Scarborough, ON M1E, Canada	"));
        googleMap.addMarker(new MarkerOptions().position (toronto64).title ("St. Stephen&#039;s Presbyterian Church, 3817 Lawrence Ave E, Scarborough, ON M1G 1P9, Canada	"));
        googleMap.addMarker(new MarkerOptions().position (toronto65).title ("The Willowdale Christian School, 60 Hilda Ave, North York, ON M2M 1V5, Canada	"));
        googleMap.addMarker(new MarkerOptions().position (toronto66).title ("Toronto Western Hospital, 399 Bathurst St, Toronto, ON M5T 2S8, Canada	"));
        googleMap.addMarker(new MarkerOptions().position (toronto67).title ("Trinity - St. Paul&#039;s Church, 26 Stavebank Rd, Mississauga, ON L5G 2T5, Canada	"));
        googleMap.addMarker(new MarkerOptions().position (toronto68).title ("Trinity United Church, 461 Park Ave, Newmarket, ON L3Y 1V9, Canada	"));
        googleMap.addMarker(new MarkerOptions().position (toronto69).title ("Trinity United Church, 1250 McCraney St E, Oakville, ON L6H 3K3, Canada	"));
        googleMap.addMarker(new MarkerOptions().position (toronto70).title ("Trinity-St. Paul&#039;s United Church, 427 Bloor St W, Toronto, ON M5S 1X6, Canada	"));
        googleMap.addMarker(new MarkerOptions().position (toronto71).title ("Waverley Road Baptist Church, 129 Waverley Rd, Toronto, ON M4L 3T4, Canada	"));
        googleMap.addMarker(new MarkerOptions().position (toronto72).title ("Woodbridge United Church, 8090 Kipling Ave, Vaughan, ON L4L, Canada	"));
        googleMap.addMarker(new MarkerOptions().position (toronto73).title ("Church Of The Incarnation, 15 Clairtrell Rd, North York, ON M2N 5J6, Canada	"));
        googleMap.addMarker(new MarkerOptions().position (toronto74).title ("Bellwood/Waterstone, 175 Brentcliffe Rd, Toronto, ON M4G, Canada	"));
        googleMap.addMarker(new MarkerOptions().position (toronto75).title ("Timothy Eaton Memorial Church, 230 St Clair Ave W, Toronto, ON M4V 1R5, Canada	"));
        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        googleMap.addMarker(new MarkerOptions().position (kingston1).title ("St. Andrew's-by-the-Lake Church, 1 Redden St, Kingston, ON"));
        googleMap.addMarker(new MarkerOptions().position (kingston2).title ("St. James Anglican Church, 10 Union St, Kingston, ON"));
        googleMap.addMarker(new MarkerOptions().position (kingston3).title ("Kingston Standard Church, 1185 Sunnyside Road (Sydenham Rd. at Sunnyside)"));
        googleMap.addMarker(new MarkerOptions().position (kingston4).title ("St. Thomas Anglican Church, 130 Lakeview Ave. (Lakeview at Cranbrook St.)"));
        googleMap.addMarker(new MarkerOptions().position (kingston5).title ("St. Paul's Anglican Church, 137 Queen Street (Queen &amp; Montreal Sts.)"));
        googleMap.addMarker(new MarkerOptions().position (kingston6).title ("Hotel Dieu Hospital, 166 Brock St, Kingston, ON K7L 5G2"));
        googleMap.addMarker(new MarkerOptions().position (kingston7).title ("Detox Centre , 240 Brock St.,&nbsp;"));
        googleMap.addMarker(new MarkerOptions().position (kingston8).title ("Harrowsmith Free Methodist Church, 3876 Harrowsmith Rd, Harrowsmith, ON K0H 1V0	"));
        googleMap.addMarker(new MarkerOptions().position (kingston9).title ("Edith Rankin Memorial United Church, 4080 Bath Rd."));
        googleMap.addMarker(new MarkerOptions().position (kingston10).title ("St. Peter's Anglican Church, 4333 Bath Road"));
        googleMap.addMarker(new MarkerOptions().position (kingston11).title ("Harbour Light Centre, 562 Princess Street"));
        googleMap.addMarker(new MarkerOptions().position (kingston12).title ("Emmanuel United Church, 63 Factory Street (Upstairs)"));
        googleMap.addMarker(new MarkerOptions().position (kingston13).title ("Crossroads United Church, 690 Sir John A. Macdonald Blvd."));
        googleMap.addMarker(new MarkerOptions().position (kingston14).title ("The Spire (Sydenham Street United Church), 82 Sydenham St., Kingston, ON &bull; K7L 3H4"));
        googleMap.addMarker(new MarkerOptions().position (kingston15).title ("Providence Care Mental Health Services, 752 King St W, Kingston, ON K7L 4X3"));
        googleMap.addMarker(new MarkerOptions().position (kingston16).title ("Kingston General Hospital, 76 Stuart St, Kingston, ON K7L 2V7"));
        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        LatLng ottawavalley1 =            new LatLng(45.3638589,-75.7711273);
        LatLng ottawavalley2 =            new LatLng(45.4340614,-75.6638847);
        LatLng ottawavalley3 =            new LatLng(45.901282,-77.2752796);
        LatLng ottawavalley4 =            new LatLng(45.056152,-77.8548495);
        LatLng ottawavalley5 =            new LatLng(445.3887932,-75.7580722);
        LatLng ottawavalley6 =            new LatLng(45.3638589,-75.7711273);
        LatLng ottawavalley7 =            new LatLng(445.4135609,-75.7085326);
        LatLng ottawavalley8 =            new LatLng(45.3954835,-75.687361);
        LatLng ottawavalley9 =            new LatLng(45.4700058,-75.6916422);
        LatLng ottawavalley10 =           new LatLng(45.4370732,-76.3682254);

        LatLng perth1 =             new LatLng(44.9009235, -76.2502683);
        LatLng perth2 =             new LatLng(44.903174,-76.2544246);
        LatLng perth3 =             new LatLng(44.8994238,-76.246947);

        LatLng sharbotlake1 =       new LatLng(44.7713312,-76.6927184);

        LatLng smithsFalls1 =       new LatLng(44.9052747, -76.031819);
        LatLng smithsFalls2 =       new LatLng(44.9017021,-76.0192961);
        LatLng smithsFalls3 =       new LatLng(44.8993029,-76.0271187);

        googleMap.addMarker(new MarkerOptions().position(lanark1).title          ("Community Health Centre, 207 Robertson Dr, Lanark, ON"));
        googleMap.addMarker(new MarkerOptions().position(lyn1).title             ("12 Perth St, Lyn, ON"));
        googleMap.addMarker(new MarkerOptions().position(ottawavalley1).title          ("971 Woodroffe Avenue, Ottawa, ON"));
        googleMap.addMarker(new MarkerOptions().position(ottawavalley2).title          ("317 Cody Avenue, Ottawa, ON"));
        googleMap.addMarker(new MarkerOptions().position(ottawavalley3).title          ("1173 Victoria Street, Petawawa, ON"));
        googleMap.addMarker(new MarkerOptions().position(ottawavalley4).title          ("1 Hastings Street South, Bancroft, ON"));
        googleMap.addMarker(new MarkerOptions().position(ottawavalley5).title          ("207 Woodroffe Avenue, Ottawa, ON"));
        googleMap.addMarker(new MarkerOptions().position(ottawavalley6).title          ("470 Roosevelt Ave, Ottawa, ON"));
        googleMap.addMarker(new MarkerOptions().position(ottawavalley7).title          ("211 Bronson Ave, Ottawa, ON"));
        googleMap.addMarker(new MarkerOptions().position(ottawavalley8).title          ("15 Aylmer Ave, Ottawa, ON"));
        googleMap.addMarker(new MarkerOptions().position(ottawavalley9).title          ("5 Rue Saint-Arthur, Gatineau, QC"));
        googleMap.addMarker(new MarkerOptions().position(ottawavalley10).title         ("2279 Alicia St, Arnprior, ON"));
        googleMap.addMarker(new MarkerOptions().position(perth1).title           ("Perth Resturaunt, 23 Gore St E, Perth, ON"));
        googleMap.addMarker(new MarkerOptions().position(perth2).title           ("First Baptist Church, D'Arcy St, Perth, ON"));
        googleMap.addMarker(new MarkerOptions().position(perth3).title           ("St. James Angligan Church, 12 Harvey St, Perth, ON"));
        googleMap.addMarker(new MarkerOptions().position(sharbotlake1).title     ("United Church Hall, Sharbot Lake, ON"));
        googleMap.addMarker(new MarkerOptions().position(smithsFalls1).title     ("Cornelia Court, 91 Cornelia St, Smith Falls, ON"));
        googleMap.addMarker(new MarkerOptions().position(smithsFalls2).title     ("Trinity United Church of Canada, 41 Market St N, Smiths Falls, ON"));
        googleMap.addMarker(new MarkerOptions().position(smithsFalls3).title     ("2 George St South, Smiths Falls, ON"));*/
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
