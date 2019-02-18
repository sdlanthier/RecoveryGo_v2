package ca.recoverygo.recoverygo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import ca.recoverygo.recoverygo.adapters.MeetingListAdapter;

public class MeetingListActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth;

    private static final String TAG="RGO_MeetingListActivity";

    private List<String> addressList        = new ArrayList<>();
    private List<String> groupsList         = new ArrayList<>();
    private List<String> notesList          = new ArrayList<>();
    private List<String> sitesList          = new ArrayList<>();
    private List<String> orgsList           = new ArrayList<>();

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_list);

        Log.d(TAG, "onCreate: Started");

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser;
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "onCreate: User is logged in");
            mRecyclerView = findViewById(R.id.recyclev1);
            mLayoutManager = new LinearLayoutManager((this));
            mRecyclerView.setLayoutManager(mLayoutManager);

            db.collection("locations").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                addressList.clear();
                groupsList.clear();
                notesList.clear();
                sitesList.clear();
                orgsList.clear();

                for(DocumentSnapshot snapshot : documentSnapshots){

                    addressList.add(snapshot.       getString("address"));
                    groupsList.add(snapshot.        getString("groupname"));
                    notesList.add(snapshot.         getString("note"));
                    sitesList.add(snapshot.         getString("site"));
                    orgsList.add(snapshot.          getString("org"));
                }
                Log.d(TAG, "onCreate: Record count:"+addressList.size());

                mAdapter = new MeetingListAdapter(addressList,groupsList,notesList,sitesList,orgsList);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setHasFixedSize(true);
            }
        });

        } else {
            Log.d(TAG, "onCreate: User is logged out");
            Intent intent = new Intent(MeetingListActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }
}
