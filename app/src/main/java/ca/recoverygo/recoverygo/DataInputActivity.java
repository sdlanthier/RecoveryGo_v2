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

import ca.recoverygo.recoverygo.adapters.NoteRecyclerViewAdapter;
import ca.recoverygo.recoverygo.models.NewNoteDialog;
import ca.recoverygo.recoverygo.models.ViewNoteDialog;
import ca.recoverygo.recoverygo.system.BaseActivity;
import ca.recoverygo.recoverygo.models.Note;
import ca.recoverygo.recoverygo.system.IDataInputActivity;

public class DataInputActivity extends BaseActivity implements
        View.OnClickListener,
        IDataInputActivity,
        SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "DataInputActivity";

    private View                            mParentLayout;
    private RecyclerView                    mRecyclerView;
    private SwipeRefreshLayout              mSwipeRefreshLayout;
    private FirebaseAuth.AuthStateListener  mAuthListener;
    private ArrayList<Note>                 mNotes = new ArrayList<>();
    private NoteRecyclerViewAdapter         mNoteRecyclerViewAdapter;
    private DocumentSnapshot                mLastQueriedDocument;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_input);

        FloatingActionButton mFab   = findViewById(R.id.fab);
        FloatingActionButton mFab2  = findViewById(R.id.fab2);
        mParentLayout               = findViewById(android.R.id.content);
        mRecyclerView               = findViewById(R.id.recycler_view);
        mSwipeRefreshLayout         = findViewById(R.id.swipe_refresh_layout);

        mFab.                   setOnClickListener(this);
        mFab2.                  setOnClickListener(this);
        mSwipeRefreshLayout.    setOnRefreshListener(this);

        setupFirebaseAuth();
        initRecyclerView();
        getNotes();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DataInputActivity.this, MainActivity.class);
        startActivity(intent);
            super.onBackPressed();
        }

    @Override
    public void deleteNote(final Note note){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference noteRef = db
                .collection("notes")
                .document(note.getNote_id());

        noteRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    makeSnackBarMessage("Deleted note");
                    mNoteRecyclerViewAdapter.removeNote(note);
                }
                else{
                    makeSnackBarMessage("Failed. Check log.");
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        getNotes();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void getNotes(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference notesCollectionRef = db
                .collection("notes");
        Query notesQuery;
        if(mLastQueriedDocument != null){
           notesQuery = notesCollectionRef
                    .whereEqualTo("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .startAfter(mLastQueriedDocument);
        }
        else{
            notesQuery = notesCollectionRef
                    .whereEqualTo("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .orderBy("timestamp", Query.Direction.ASCENDING);
        }

        notesQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){

                    for(QueryDocumentSnapshot document: task.getResult()){
                        Note note = document.toObject(Note.class);
                        mNotes.add(note);
                    }

                    if(task.getResult().size() != 0){
                        mLastQueriedDocument = task.getResult().getDocuments()
                                .get(task.getResult().size() -1);
                    }

                    mNoteRecyclerViewAdapter.notifyDataSetChanged();
                }
                else{
                    Log.d(TAG, "onComplete: QUERY FAILED ***********************************");
                    makeSnackBarMessage("Query Failed. Check Logs.");
                }
            }
        });
    }

    private void initRecyclerView(){
        if(mNoteRecyclerViewAdapter == null){
            mNoteRecyclerViewAdapter = new NoteRecyclerViewAdapter(this, mNotes);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mNoteRecyclerViewAdapter);
    }

    @Override
    public void updateNote(final Note note){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference noteRef = db
                .collection("notes")
                .document(note.getNote_id());

        noteRef.update(
                "title", note.getTitle(),
                "content", note.getContent()
        ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    makeSnackBarMessage("Updated note");
                    mNoteRecyclerViewAdapter.updateNote(note);
                }
                else{
                    makeSnackBarMessage("Failed. Check log.");
                }
            }
        });
    }

    @Override
    public void onNoteSelected(Note note) {
        ViewNoteDialog dialog = ViewNoteDialog.newInstance(note);
        dialog.show(getSupportFragmentManager(), getString(R.string.dialog_view_note));
    }

    @Override
    public void createNewNote(String title, String content) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference newNoteRef = db
                .collection("notes")
                .document();

                Note note = new Note();
                note.setTitle(title);
                note.setContent(content);
                note.setNote_id(newNoteRef.getId());
                note.setUser_id(userId);

                newNoteRef.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    makeSnackBarMessage("Created new note");
                    getNotes();
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
                NewNoteDialog dialog = new NewNoteDialog();
                dialog.show(getSupportFragmentManager(), getString(R.string.dialog_new_note));
                break;
            }
            case R.id.fab2:{
                signOut();
                Intent intent = new Intent(DataInputActivity.this, LoginActivity.class);
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
                    Intent intent = new Intent(DataInputActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
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