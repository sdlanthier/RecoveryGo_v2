package ca.recoverygo.recoverygo.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import ca.recoverygo.recoverygo.R;
import ca.recoverygo.recoverygo.adapters.MeetingListAdapter;

public class MeetingListActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG="MeetingListActivity";

    private List<String> addressList        = new ArrayList<>();
    private List<String> daysList           = new ArrayList<>();
    private List<String> formatsList        = new ArrayList<>();
    private List<String> groupsList         = new ArrayList<>();
    private List<String> intergroupsList    = new ArrayList<>();
    private List<String> locationsList      = new ArrayList<>();
    private List<String> notesList          = new ArrayList<>();
    private List<String> sitesList          = new ArrayList<>();
    private List<String> timesList          = new ArrayList<>();
    ListView listView;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_list);
        Log.d(TAG, "onCreate: Started");
        mRecyclerView = findViewById(R.id.recyclev1);
        mLayoutManager = new LinearLayoutManager((this));
        mRecyclerView.setLayoutManager(mLayoutManager);

     // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        //final ListView listView = findViewById(R.id.listView);
        db.collection("meetings").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                addressList.clear();
                daysList.clear();
                formatsList.clear();
                groupsList.clear();
                intergroupsList.clear();
                locationsList.clear();
                notesList.clear();
                sitesList.clear();
                timesList.clear();

                for(DocumentSnapshot snapshot : documentSnapshots){

                    addressList.add(snapshot.getString("address"));
                    daysList.add(snapshot.getString("day"));
                    formatsList.add(snapshot.getString("format"));
                    groupsList.add(snapshot.getString("groupname"));
                    intergroupsList.add(snapshot.getString("intergroup"));
                    locationsList.add(snapshot.getString("intergroup"));
                    notesList.add(snapshot.getString("note"));
                    sitesList.add(snapshot.getString("site"));
                    timesList.add(snapshot.getString("time"));
                }

                // ArrayAdapter adapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_selectable_list_item,addressList);
                // adapter.notifyDataSetChanged();
                // listView.setAdapter(adapter);
                mAdapter = new MeetingListAdapter(addressList,daysList,formatsList,groupsList,intergroupsList,locationsList,notesList,sitesList,timesList);
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.setHasFixedSize(true);
            }
        });
    }

}
