package ca.recoverygo.recoverygo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import ca.recoverygo.recoverygo.models.Meeting;
import ca.recoverygo.recoverygo.system.BaseActivity;

public class MeetingSetupActivity extends BaseActivity {

    private static final String TAG = "rg_MeetingSetup";

    public LocationManager locationManager;
    public String provider;
    public String city;

    EditText mGroup, mSite, mNote, mOrg;
    TextView mUser, mAddressLine, mGeo;
    RadioGroup mOrgRadioGroup;
    RadioButton mOrgRadioBtn;
    RadioButton mRadioButton1;
    RadioButton mRadioButton2;
    RadioButton mRadioButton3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_setup);

        mGroup = findViewById(R.id.group);
        mSite = findViewById(R.id.site);
        mNote = findViewById(R.id.note);
        mOrg = findViewById(R.id.org);
        mUser = findViewById(R.id.user);
        mGeo = findViewById(R.id.geo);
        mOrgRadioGroup = findViewById(R.id.org1);
        mRadioButton1 = findViewById(R.id.orgaa);
        mRadioButton2 = findViewById(R.id.orgna);
        mRadioButton3 = findViewById(R.id.orgal);


        AlertDialog alertDialog = new AlertDialog.Builder(MeetingSetupActivity.this).create();
        alertDialog.setTitle("Note");
        alertDialog.setMessage("This service uses Last Known Location. Please ensure the address shown is the address you are creating the meeting for.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        provider = locationManager.getBestProvider(criteria, true);

        Log.d(TAG, "onCreate: provider: " + provider);

        if (provider != null) {
            Log.d(TAG, "onCreate: provider != null");
        } else {
            Log.d(TAG, "onCreate: provider == null");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);

            if (location != null) {
                double myLat = location.getLatitude();
                double myLng = location.getLongitude();

                LatLng myposition = new LatLng(myLat, myLng);
                mGeo.setText(String.valueOf(myposition));

                Geocoder gcd = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = gcd.getFromLocation(myLat, myLng, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (Objects.requireNonNull(addresses).size() > 0) {
                    String addressLine = addresses.get(0).getAddressLine(0);

                    city = addresses.get(0).getLocality();
                    mAddressLine = findViewById(R.id.addressLine);
                    mAddressLine.setText(String.valueOf(addressLine));
                }
            }else {
                Log.d(TAG, "onCreate: Location is null");
            }

    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseApp.initializeApp(this);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser;
        currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    public void updateUI(FirebaseUser user) {
        if (user == null) {
            AlertDialog alertDialog = new AlertDialog.Builder(MeetingSetupActivity.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("You must have a valid account to use the Meeting Locator.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(MeetingSetupActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    });
        }
    }

    public void checkPermission(){
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            AlertDialog alertDialog = new AlertDialog.Builder(MeetingSetupActivity.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("You must have Location enabled on your device.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(MeetingSetupActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });
            alertDialog.show();
        }
    }
    public void checkUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String uid = user.getUid();
            mUser.setText(uid);
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(MeetingSetupActivity.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("You must have a valid user account to create a new meeting marker.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(MeetingSetupActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    });
            alertDialog.show();
        }
    }
    public void openAlert(View view) {
        if (validateForm()) {
            return;
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MeetingSetupActivity.this);
        alertDialogBuilder.setTitle("You are about to create a new Map Marker");
        alertDialogBuilder.setMessage("Only you can make changes to this marker. Proceed?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
            public void onClick(DialogInterface dialog, int id) {
                save();
            }
        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        alertDialogBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                MeetingSetupActivity.this.finish();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public void save() {

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
            if (ActivityCompat.checkSelfPermission(getBaseContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getBaseContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        Location location   = locationManager.getLastKnownLocation(provider);
        Double myLat        = location.getLatitude();
        Double myLng        = location.getLongitude();
        String groupname    = mGroup.getText().toString();
        String site         = mSite.getText().toString();
        String address      = mAddressLine.getText().toString();
        String note         = mNote.getText().toString();

        int selectedId      = mOrgRadioGroup.getCheckedRadioButtonId();
        mOrgRadioBtn        = findViewById(selectedId);
        String org1         = mOrgRadioBtn.getText().toString();

        GeoPoint loc        = new GeoPoint(myLat, myLng);
        // **************************************************
        showSaveDialog();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        DocumentReference newMeetingRef = db
                .collection("locations")
                .document();

        Meeting meeting = new Meeting(groupname, site, org1, note, userId, loc, city, address);

        meeting.setGroupname(groupname);
        meeting.setSite(site);
        meeting.setOrg(org1);
        meeting.setNote(note);
        meeting.setUser(userId);
        meeting.setLocation(loc);
        meeting.setAddress(address);
        meeting.setCity(city);

        meeting.setLocation_id(newMeetingRef.getId());

        newMeetingRef.set(meeting).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    hideSaveDialog();
                    Intent intent = new Intent(MeetingSetupActivity.this, LocatorActivity.class);
                    startActivity(intent);
                } else {
                    hideSaveDialog();
                }
            }
        });

    }
    public boolean validateForm() {
        boolean valid = true;

        String groupname = mGroup.getText().toString();
        if (TextUtils.isEmpty(groupname)) {
            mGroup.setError("Required.");
            valid = false;
        } else {
            mGroup.setError(null);
        }

        String site = mSite.getText().toString();
        if (TextUtils.isEmpty(site)) {
            mSite.setError("Required.");
            valid = false;
        } else {
            mSite.setError(null);
        }

        String note = mNote.getText().toString();
        if (TextUtils.isEmpty(note)) {
            mNote.setError("Required.");
            valid = false;
        } else {
            mNote.setError(null);
        }

        if(!mRadioButton1.isChecked() && !mRadioButton2.isChecked() && !mRadioButton3.isChecked()) {
            mRadioButton3.setError("Required.");
            valid = false;
        }
        return !valid;
    }
}