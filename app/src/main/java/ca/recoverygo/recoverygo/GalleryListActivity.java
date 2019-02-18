package ca.recoverygo.recoverygo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;

import ca.recoverygo.recoverygo.adapters.GalleryRecyclerViewAdapter;

public class GalleryListActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

        //vars
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private ArrayList<String> mTextBody = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_list);
        Log.d(TAG,"onCreate: started.");

        initImageBitmaps();
    }

    private void initImageBitmaps(){
        Log.d(TAG,"initImageBitmaps: ");

        mImageUrls.add("https://i.redd.it/obx4zydshg601.jpg");
        mNames.add("1");
        mTextBody.add("Darkness");

        initRecyclerView();

    }

    private void initRecyclerView(){

        Log.d(TAG,"initRecyclerView: init recyclerview.");
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        GalleryRecyclerViewAdapter adapter = new GalleryRecyclerViewAdapter(this,mNames,mImageUrls,mTextBody);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
