package ca.recoverygo.recoverygo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

public class MeetingSetupActivity extends AppCompatActivity {

 // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private static final String TAG                 = "RGO_MeetingSetupActivity";

    private static final String CONTAINER_NAME      = "locations";

    private static final String KEY_GROUPNAME       = "groupname";
    private static final String KEY_SITE            = "site";
    private static final String KEY_ADDRESS         = "address";
    private static final String KEY_NOTE            = "note";
    private static final String KEY_MARKER          = "location";
    private static final String KEY_USER            = "user";

    public FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LocationManager locationManager;
    public String provider;

    EditText mGroup;
    EditText mSite;
    EditText mAddress;
    EditText mNote;
    EditText mMarker;
    TextView mUser;
    TextView mGeo;

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_setup);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        if (user != null) {
                // Name, email address, and profile photo Url
                String name = user.getDisplayName();
                String email = user.getEmail();
                Uri photoUrl = user.getPhotoUrl();

                // Check if user's email is verified
                boolean emailVerified = user.isEmailVerified();

                // The user's ID, unique to the Firebase project. Do NOT use this value to
                // authenticate with your backend server, if you have one. Use
                // FirebaseUser.getIdToken() instead.
                String uid = user.getUid();
            mUser         = findViewById(R.id.user);
            mUser.setText(uid);

        }
        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        Double myLat = location.getLatitude();
        Double myLng = location.getLongitude();

        LatLng myposition = new LatLng(myLat, myLng);
     // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        mGroup        = findViewById(R.id.group);
        mSite         = findViewById(R.id.site);
        mAddress      = findViewById(R.id.address);
        mNote         = findViewById(R.id.note);
        mMarker       = findViewById(R.id.marker);
        mUser         = findViewById(R.id.user);
        mGeo          = findViewById(R.id.geo);

        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        mMarker.setText(String.valueOf(myposition));
        mGeo.setText(String.valueOf(myposition));

        AlertDialog alertDialog = new AlertDialog.Builder(MeetingSetupActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("You are about to save to a live Database of meeting locations. Please use this function responsibly.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void save(View v) {

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        Double myLat = location.getLatitude();
        Double myLng = location.getLongitude();
        // **************************************************
        String group   = mGroup.getText().toString();
        String site    = mSite.getText().toString();
        String address = mAddress.getText().toString();
        String note    = mNote.getText().toString();
        String uid    = mUser.getText().toString();

        Object marker = new GeoPoint(myLat,myLng);
        
        // **************************************************
        mGroup.getText().clear();
        mSite.getText().clear();
        mAddress.getText().clear();
        mNote.getText().clear();
        mMarker.getText().clear();
        mGroup.setVisibility(View.INVISIBLE);
        mSite.setVisibility(View.INVISIBLE);
        mAddress.setVisibility(View.INVISIBLE);
        mNote.setVisibility(View.INVISIBLE);

        // **************************************************
        Map<String,Object> meetingrcd = new HashMap<>();

        meetingrcd.put(KEY_GROUPNAME, group);
        meetingrcd.put(KEY_SITE,      site);
        meetingrcd.put(KEY_ADDRESS,   address);
        meetingrcd.put(KEY_NOTE,      note);
        meetingrcd.put(KEY_MARKER,    marker);
        meetingrcd.put(KEY_USER,      uid);

        db.collection(CONTAINER_NAME).document().set(meetingrcd).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MeetingSetupActivity.this, "Record Saved", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MeetingSetupActivity.this, "Error!", Toast.LENGTH_LONG).show();
            }
        });
    }

}
