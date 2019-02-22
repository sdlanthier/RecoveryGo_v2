package ca.recoverygo.recoverygo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import ca.recoverygo.recoverygo.adapters.MeetingRecyclerViewAdapter;
import ca.recoverygo.recoverygo.models.Meeting;
import ca.recoverygo.recoverygo.models.ViewMeetingDialog;
import ca.recoverygo.recoverygo.system.BaseActivity;
import ca.recoverygo.recoverygo.system.IMeetingInputActivity;

public class MeetingInputActivity extends BaseActivity implements
        View.OnClickListener,
        IMeetingInputActivity,
        SwipeRefreshLayout.OnRefreshListener {

    private FirebaseAuth mAuth;

    private View mParentLayout;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<Meeting> mMeetings = new ArrayList<>();
    private MeetingRecyclerViewAdapter mMeetingRecyclerViewAdapter;
    private DocumentSnapshot mLastQueriedDocument;
    TextView mNologinMsg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_input);

        FloatingActionButton mFab = findViewById(R.id.fab);
        mParentLayout = findViewById(android.R.id.content);
        mRecyclerView = findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        mAuth = FirebaseAuth.getInstance();

        mFab.setOnClickListener(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseApp.initializeApp(this);
        FirebaseUser currentUser;
        currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            initRecyclerView();
            getMeetings();
        } else {
            FloatingActionButton mFab = findViewById(R.id.fab);
            mNologinMsg = findViewById(R.id.nologinmsg);

            mFab.setVisibility(View.GONE);
            mNologinMsg.setVisibility(View.VISIBLE);
        }
    }

    private void initRecyclerView() {
        if (mMeetingRecyclerViewAdapter == null) {
            mMeetingRecyclerViewAdapter = new MeetingRecyclerViewAdapter(this, mMeetings);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mMeetingRecyclerViewAdapter);
    }

    private void getMeetings() {
        showProgressDialog();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference meetingsCollectionRef = db.collection("locations");
        Query meetingsQuery;
        if (mLastQueriedDocument != null) {
            meetingsQuery = meetingsCollectionRef
                    .whereEqualTo("user", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                    .orderBy("groupname", Query.Direction.ASCENDING)
                    .startAfter(mLastQueriedDocument);
        } else {
            meetingsQuery = meetingsCollectionRef
                    .whereEqualTo("user", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                    .orderBy("groupname", Query.Direction.ASCENDING);
        }

        meetingsQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        Meeting meeting = document.toObject(Meeting.class);
                        mMeetings.add(meeting);
                    }

                    if (task.getResult().size() != 0) {
                        mLastQueriedDocument = task.getResult().getDocuments().get(task.getResult().size() - 1);
                    }

                    mMeetingRecyclerViewAdapter.notifyDataSetChanged();
                    hideProgressDialog();
                } else {
                    makeSnackBarMessage("Query Failed. Check Logs.");
                }
            }
        });
    }

    @Override
    public void updateMeeting(final Meeting meeting) {
        showProgressDialog();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference meetingRef = db
                .collection("locations")
                .document(meeting.getLocation_id());

        meetingRef.update(
                "groupname", meeting.getGroupname(),
                "site", meeting.getSite(),
                "org", meeting.getOrg(),
                "note", meeting.getNote()

        ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    makeSnackBarMessage("Location Updated");
                    mMeetingRecyclerViewAdapter.updateMeeting(meeting);
                    hideProgressDialog();
                } else {
                    makeSnackBarMessage("Database Unavailable.");
                }
            }
        });
    }

    @Override
    public void deleteMeeting(final Meeting meeting) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference meetingRef = db
                .collection("locations")
                .document(meeting.getLocation_id());

        meetingRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    makeSnackBarMessage("Deleted meeting");
                    mMeetingRecyclerViewAdapter.removeMeeting(meeting);
                } else {
                    makeSnackBarMessage("Failed. Check log.");
                }
            }
        });
    }

    @Override
    public void createNewMeeting(String groupname, String site, String org, String note, String user, GeoPoint location, String location_id, String address, String city) {
        showProgressDialog();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference newMeetingRef = db
                .collection("locations")
                .document();

        Meeting meeting = new Meeting(groupname, site, org, note, user, location, address,city);

        meeting.setGroupname(groupname);
        meeting.setSite(site);
        meeting.setOrg(org);
        meeting.setNote(note);
        meeting.setUser(user);
        meeting.setLocation(location);

        meeting.setLocation_id(newMeetingRef.getId());
        meeting.setAddress(address);
        meeting.setCity(city);


        newMeetingRef.set(meeting).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    hideProgressDialog();
                    makeSnackBarMessage("Created new meeting");
                    getMeetings();
                } else {
                    makeSnackBarMessage("Failed. Check log.");
                }
            }
        });
    }

    @Override
    public void onMeetingSelected(Meeting meeting) {
        ViewMeetingDialog dialog = ViewMeetingDialog.newInstance(meeting);
        dialog.show(getSupportFragmentManager(), getString(R.string.dialog_view_meeting));
    }

    private void makeSnackBarMessage(String message) {
        Snackbar.make(mParentLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.fab: {
                //create a new note
                Intent intent = new Intent(MeetingInputActivity.this, MeetingSetupActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.fab2: {
                signOut();
                Intent intent = new Intent(MeetingInputActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.optionSignOut:
                signOut();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRefresh() {
        getMeetings();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MeetingInputActivity.this, MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

}