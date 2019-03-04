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
import android.location.LocationListener;
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
import android.widget.Toast;

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

public class MeetingSetupActivity extends BaseActivity implements LocationListener {

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

        //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        showProgressDialog();
        Log.d("rg_MeetingSetup", "onCreate: Checking Location Permissions");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d("rg_MeetingSetup", "onCreate: App Permission Not Granted");
            return;
        }
        Log.d("rg_MeetingSetup", "onCreate: App Permission Granted - Getting Provider");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        provider = locationManager.getBestProvider(criteria, true);

        if (provider == null) {
            Log.d("rg_MeetingSetup", "onCreate: Prover is null - Exit to MainActivity");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        Log.d("rg_MeetingSetup", "onCreate: Provider: "+provider);
        Log.d("rg_MeetingSetup", "onCreate: Getting Location ");

        Location location = locationManager.getLastKnownLocation(provider);

            if (location != null) {
                hideProgressDialog();
                Log.d("rg_MeetingSetup", "onCreate: Location: "+location);

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
                Log.d("rg_MeetingSetup", "onCreate: Location is null");
                AlertDialog alertDialog = new AlertDialog.Builder(MeetingSetupActivity.this).create();
                alertDialog.setTitle("Location");
                alertDialog.setMessage("Unable to Obtain Last Known Location. Ensure that Location Services is turned on for this device..");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(MeetingSetupActivity.this,MainActivity.class);
                                startActivity(intent);
                            }
                        });
                alertDialog.show();

            }
        Log.d("rg_MeetingSetup", "onCreate: END");
            hideProgressDialog();

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
        showSaveProgressDialog();
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
                    hideSaveProgressDialog();
                    Intent intent = new Intent(MeetingSetupActivity.this, LocatorActivity.class);
                    startActivity(intent);
                } else {
                    hideSaveProgressDialog();
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

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(provider, 90000, 1, this);
        Log.d("rg_MeetingSetup", "onResume - requestLocationUpdates from: "+provider);
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(this, "Location Changed", Toast.LENGTH_SHORT).show();
        Log.d("rg_MeetingSetup", "New Location: "+location);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Log.d("rg_MeetingSetup", "onLocationChanged: "+locationManager);

        if (location != null) {

            Log.d("rg_MeetingSetup", "onLocationChanged: Location is not null");
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
            Log.d("rg_MeetingSetup", "onLocationChanged: Location is null");

        }
        hideProgressDialog();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}