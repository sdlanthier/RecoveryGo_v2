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
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

    EditText mGroup, mSite, mNote, mOrg;
    TextView mUser, mAddressLine, mGeo;

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_setup);

        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        mGroup  = findViewById(R.id.group);
        mSite   = findViewById(R.id.site);
        mNote   = findViewById(R.id.note);
        mOrg    = findViewById(R.id.org);
        mUser   = findViewById(R.id.user);
        mGeo    = findViewById(R.id.geo);
        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String uid = user.getUid();
            mUser.setText(uid);
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(MeetingSetupActivity.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("You must have a valid acount to create a new meeting marker.");
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
        // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
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
                    mAddressLine = findViewById(R.id.addressLine);
                    mAddressLine.setText(String.valueOf(addressLine));
                }
        }

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

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called");
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.d(TAG, "onPostResume: called");
    }

    public void openAlert(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MeetingSetupActivity.this);

        alertDialogBuilder.setTitle(this.getTitle()+ " decision");
        alertDialogBuilder.setMessage("Are you sure?");
        // set positive button: Yes message
        alertDialogBuilder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                provider = locationManager.getBestProvider(criteria, false);
                if (ActivityCompat.checkSelfPermission(getBaseContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(getBaseContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Location location = locationManager.getLastKnownLocation(provider);
                Double myLat = location.getLatitude();
                Double myLng = location.getLongitude();
                // **************************************************
                String groupname   = mGroup.getText().toString();
                String site    = mSite.getText().toString();
                String address = mAddressLine.getText().toString();
                String note    = mNote.getText().toString();
                String org     = mOrg.getText().toString();

                GeoPoint loc = new GeoPoint(myLat,myLng);

                // **************************************************
                mGroup.getText().clear();
                mSite.getText().clear();
                mNote.getText().clear();
                mOrg.getText().clear();

                mGroup      .setVisibility(View.INVISIBLE);
                mSite       .setVisibility(View.INVISIBLE);
                mNote       .setVisibility(View.INVISIBLE);
                mOrg        .setVisibility(View.INVISIBLE);
                showProgressDialog();
                // **************************************************
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

                DocumentReference newMeetingRef = db
                        .collection("locations")
                        .document();

                Meeting meeting = new Meeting(groupname, site, org, note, userId, loc, address);

                meeting.setGroupname(groupname);
                meeting.setSite(site);
                meeting.setOrg(org);
                meeting.setNote(note);
                meeting.setUser(userId);
                meeting.setLocation(loc);
                meeting.setAddress(address);

                meeting.setLocation_id(newMeetingRef.getId());


                newMeetingRef.set(meeting).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MeetingSetupActivity.this, "Created New Meeting", Toast.LENGTH_SHORT).show();
                            hideProgressDialog();
                            Log.d(TAG, "onComplete: Created New meeting");                }
                        else{
                            Log.d(TAG, "onComplete: Create New Meeting Failed");                }
                    }
                });

            }
        });
        // set negative button: No message
        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                // cancel the alert box and put a Toast to the user
                dialog.cancel();
                Toast.makeText(getApplicationContext(), "You chose a negative answer",
                        Toast.LENGTH_LONG).show();
            }
        });
        // set neutral button: Exit the app message
        alertDialogBuilder.setNeutralButton("Exit the app",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                // exit the app and go to the HOME
                MeetingSetupActivity.this.finish();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();
    }
}