package ca.recoverygo.recoverygo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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
import ca.recoverygo.recoverygo.system.BaseActivity;

public class MeetingListActivity extends BaseActivity {

    private List<String> addressList = new ArrayList<>();
    private List<String> groupsList = new ArrayList<>();
    private List<String> notesList = new ArrayList<>();
    private List<String> sitesList = new ArrayList<>();
    private List<String> orgsList = new ArrayList<>();

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_list);

        checkUser();

        mRecyclerView = findViewById(R.id.recyclev1);
        mLayoutManager = new LinearLayoutManager((this));
        mRecyclerView.setLayoutManager(mLayoutManager);
        loadData();
    }

    public void checkUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            AlertDialog alertDialog = new AlertDialog.Builder(MeetingListActivity.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("You must have a valid user account to create a new meeting marker.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(MeetingListActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    });
            alertDialog.show();
        }
    }
    public void loadData(){
        showProgressDialog();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("locations").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
            for (DocumentSnapshot snapshot : documentSnapshots) {

                addressList.add(snapshot.getString("address"));
                groupsList. add(snapshot.getString("groupname"));
                notesList.  add(snapshot.getString("note"));
                sitesList.  add(snapshot.getString("site"));
                orgsList.   add(snapshot.getString("org"));
            }
            mAdapter = new MeetingListAdapter(addressList, groupsList, notesList, sitesList, orgsList);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setHasFixedSize(true);
            hideProgressDialog();
        }
    });
}
}
