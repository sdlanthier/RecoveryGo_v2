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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import ca.recoverygo.recoverygo.adapters.EntryRecyclerViewAdapter;
import ca.recoverygo.recoverygo.models.Entry;
import ca.recoverygo.recoverygo.models.NewEntryDialog;
import ca.recoverygo.recoverygo.models.ViewEntryDialog;
import ca.recoverygo.recoverygo.system.BaseActivity;
import ca.recoverygo.recoverygo.system.IDirectoryInputActivity;

public class DirectoryInputActivity extends BaseActivity implements
        View.OnClickListener,
        IDirectoryInputActivity,
        SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "DirectoryInputActivity";

    private View                            mParentLayout;
    private RecyclerView                    mRecyclerView;
    private SwipeRefreshLayout              mSwipeRefreshLayout;
    private FirebaseAuth.AuthStateListener  mAuthListener;
    private ArrayList<Entry>                mNames = new ArrayList<>();
    private EntryRecyclerViewAdapter        mEntryRecyclerViewAdapter;
    private DocumentSnapshot                mLastQueriedDocument;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory_input);

        FloatingActionButton mFab   = findViewById(R.id.fab);
        mParentLayout               = findViewById(android.R.id.content);
        mRecyclerView               = findViewById(R.id.recycler_view);
        mSwipeRefreshLayout         = findViewById(R.id.swipe_refresh_layout);

        mFab.setOnClickListener(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        setupFirebaseAuth();
        initRecyclerView();
        getNames();
    }

    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: started.");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    Intent intent = new Intent(DirectoryInputActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    private void initRecyclerView(){
        if(mEntryRecyclerViewAdapter == null){
            mEntryRecyclerViewAdapter = new EntryRecyclerViewAdapter(this, mNames);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mEntryRecyclerViewAdapter);

    }

    private void getNames(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference notesCollectionRef = db
                .collection("Notebook");
        Query notesQuery;
        if(mLastQueriedDocument != null){
            notesQuery = notesCollectionRef
                    .orderBy("nextavail", Query.Direction.ASCENDING)
                    .startAfter(mLastQueriedDocument);
        }
        else{
            notesQuery = notesCollectionRef
                    .orderBy("nextavail", Query.Direction.ASCENDING);
        }

        notesQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){

                    for(QueryDocumentSnapshot document: Objects.requireNonNull(task.getResult())){
                        Entry entry = document.toObject(Entry.class);
                        mNames.add(entry);
                    }

                    if(task.getResult().size() != 0){
                        mLastQueriedDocument = task.getResult().getDocuments()
                                .get(task.getResult().size() -1);
                    }

                    mEntryRecyclerViewAdapter.notifyDataSetChanged();

                }
                else{
                    makeSnackBarMessage("Query Failed. Check Logs.");
                }
            }
        });

    }

    @Override
    public void deleteEntry(final Entry entry){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference noteRef = db
                .collection("Notebook")
                .document(entry.getEntry_id());

        noteRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    makeSnackBarMessage("Deleted Entry");
                    mEntryRecyclerViewAdapter.removeEntry(entry);
                }
                else{
                    makeSnackBarMessage("Failed. Check log.");
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        // getNames();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void updateEntry(final Entry entry){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference noteRef = db
                .collection("Notebook")
                .document(entry.getEntry_id());

        noteRef.update(
                "name", entry.getName(),
                "street", entry.getStreet(),
                "city", entry.getCity(),
                "prov", entry.getProv(),
                "pcode", entry.getPcode(),
                "phone", entry.getPhone(),
                "web", entry.getWeb(),
                "bedsttl", entry.getBedsttl(),
                "bedsrepair", entry.getBedsrepair(),
                "bedspublic", entry.getBedspublic(),
                "waittime", entry.getWaittime(),
                "gender", entry.getGender(),
                "nextavail", entry.getNextavail()


        ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    makeSnackBarMessage("Updated Entry");
                    mEntryRecyclerViewAdapter.updateEntry(entry);
                }
                else{
                    makeSnackBarMessage("Failed. Check log.");
                }
            }
        });
    }

    @Override
    public void onEntrySelected(Entry entry) {
        ViewEntryDialog dialog = ViewEntryDialog.newInstance(entry);
        dialog.show(getSupportFragmentManager(), getString(R.string.dialog_view_entry));
    }

    @Override
    public void createNewEntry(String name, String street, String city, String prov, String pcode,
                               String phone, String web, String bedsttl, String bedsrepair, String bedspublic, String waittime, String gender,
                               String nextavail) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference newNoteRef = db
                .collection("Notebook")
                .document();

                Entry entry = new Entry();
        entry.setName(name);
        entry.setStreet(street);
        entry.setCity(city);
        entry.setProv(prov);
        entry.setPcode(pcode);
        entry.setPcode(phone);
        entry.setPcode(web);
        entry.setPcode(bedsttl);
        entry.setPcode(bedsrepair);
        entry.setPcode(bedspublic);
        entry.setPcode(waittime);
        entry.setPcode(gender);
        entry.setPcode(nextavail);

        entry.setEntry_id(newNoteRef.getId());

                newNoteRef.set(entry).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    makeSnackBarMessage("Created new Entry");
                    getNames();
                }
                else{
                    makeSnackBarMessage("Failed. Check log.");
                }
            }
        });
    }

    private void makeSnackBarMessage(String message){
        Snackbar.make(mParentLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.fab:{
                //create a new note
                NewEntryDialog dialog = new NewEntryDialog();
                dialog.show(getSupportFragmentManager(), getString(R.string.dialog_new_note));
                break;
            }
            case R.id.fab2:{
                signOut();
                Intent intent = new Intent(DirectoryInputActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.optionSignOut:
                signOut();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void signOut(){
        Log.d(TAG, "signOut: signing out");
        FirebaseAuth.getInstance().signOut();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DirectoryInputActivity.this, MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }

}