package ca.recoverygo.recoverygo;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BedInputActivity extends AppCompatActivity {

    private static final String TAG = "BedInputActivity";

    public TextView mFacility;
    public TextView mNextAvail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bed_input);

        FirebaseDatabase database   = FirebaseDatabase.getInstance();
        DatabaseReference mRef1     = database.getReference("facility");
        DatabaseReference mRef3     = database.getReference("nextAvail");

        mFacility   = findViewById(R.id.facility);
        mNextAvail  = findViewById(R.id.nextavail);

        mRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String facility = dataSnapshot.getValue(String.class);
                mFacility.setText(facility);
                Log.d(TAG, "Value is: " + facility);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        mRef3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String nextavail = dataSnapshot.getValue(String.class);
                mNextAvail.setText(nextavail);

                Log.d(TAG, "Value is: " + nextavail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }


}
