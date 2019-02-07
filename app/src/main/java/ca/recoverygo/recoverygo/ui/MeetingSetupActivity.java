package ca.recoverygo.recoverygo.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


import ca.recoverygo.recoverygo.R;

public class MeetingSetupActivity extends AppCompatActivity {

 // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private static final String TAG                 = "MeetingSetupActivity";

    private static final String CONTAINER_NAME      = "meetings";

    private static final String KEY_GROUPNAME       = "groupname";
    private static final String KEY_FORMAT          = "format";
    private static final String KEY_DAY             = "day";
    private static final String KEY_TIME            = "time";
    private static final String KEY_SITE            = "site";
    private static final String KEY_ADDRESS         = "address";
    private static final String KEY_INTERGROUP      = "intergroup";
    private static final String KEY_LOCATION        = "location";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private LocationManager locationManager;
    private String provider;

    EditText mFieldGroup;
    EditText mFieldFormat;
    EditText mFieldDay;
    EditText mFieldTime;
    EditText mFieldSite;
    EditText mFieldAddress;
    EditText mFieldIntergroup;
    EditText mFieldLocation;

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_setup);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onCreate MeetingSetupActivity() provider: " + provider);
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        Double myLat = location.getLatitude();
        Double myLng = location.getLongitude();

        LatLng myposition = new LatLng(myLat, myLng);
     // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


     // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        mFieldGroup        = findViewById(R.id.field_group);
        mFieldFormat       = findViewById(R.id.field_format);
        mFieldDay          = findViewById(R.id.field_weekday);
        mFieldTime         = findViewById(R.id.field_time);
        mFieldSite         = findViewById(R.id.field_site);
        mFieldAddress      = findViewById(R.id.field_address);
        mFieldIntergroup   = findViewById(R.id.field_intergroup);
        mFieldLocation     = findViewById(R.id.field_location);
     // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        mFieldLocation.setText(String.valueOf(myposition));
    }

    public void save(View v) {
        // **************************************************
        String groupname   = mFieldGroup.getText().toString();
        String format      = mFieldFormat.getText().toString();
        String day         = mFieldDay.getText().toString();
        String time        = mFieldTime.getText().toString();
        String site        = mFieldSite.getText().toString();
        String address     = mFieldAddress.getText().toString();
        String intergroup  = mFieldIntergroup.getText().toString();
        String district    = mFieldLocation.getText().toString();
        // **************************************************
        mFieldGroup.getText().clear();
        mFieldFormat.getText().clear();
        mFieldDay.getText().clear();
        mFieldTime.getText().clear();
        mFieldSite.getText().clear();
        mFieldAddress.getText().clear();
        mFieldIntergroup.getText().clear();
        mFieldLocation.getText().clear();
        // **************************************************
        Map<String,Object> meetingrcd = new HashMap<>();

        meetingrcd.put(KEY_GROUPNAME, groupname);
        meetingrcd.put(KEY_FORMAT,   format);
        meetingrcd.put(KEY_DAY,         day);
        meetingrcd.put(KEY_TIME,        time);
        meetingrcd.put(KEY_SITE,        site);
        meetingrcd.put(KEY_ADDRESS,     address);
        meetingrcd.put(KEY_INTERGROUP,  intergroup);
        meetingrcd.put(KEY_LOCATION,    district);

        db.collection(CONTAINER_NAME).document().set(meetingrcd).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MeetingSetupActivity.this, "Record Saved", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MeetingSetupActivity.this, "Error!", Toast.LENGTH_LONG).show();
                Log.d(TAG, "onFailure: "+e.toString());
            }
        });
    }

}
