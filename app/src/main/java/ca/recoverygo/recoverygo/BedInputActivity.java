package ca.recoverygo.recoverygo;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BedInputActivity extends AppCompatActivity {

    private static final String TAG = "BedInputActivity";

    public TextView mFacility;
    public TextView mBeds;
    public TextView mNextAvail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bed_input);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mRef1 = database.getReference("facility");
        // DatabaseReference mRef2 = database.getReference("beds");
        DatabaseReference mRef3 = database.getReference("nextAvail");

        // mRef2.setValue("Renascent - Punanai Center for Men");
        // mRef1.setValue("30");
        // mRef3.setValue("May 01, 2020");

        // Read from the database

        // mBeds = findViewById(R.id.beds);
        mFacility = findViewById(R.id.facility);
        mNextAvail = findViewById(R.id.nextavail);

        mRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String facility = dataSnapshot.getValue(String.class);
                mFacility.setText(facility);
                Log.d(TAG, "Value is: " + facility);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

/*        mRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String beds = dataSnapshot.getValue(String.class);
                mBeds.setText(beds);
                Log.d(TAG, "Value is: " + beds);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });*/


        mRef3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String nextavail = dataSnapshot.getValue(String.class);
                mNextAvail.setText(nextavail);

                Log.d(TAG, "Value is: " + nextavail);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }


}
