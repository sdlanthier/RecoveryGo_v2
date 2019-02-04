package ca.recoverygo.recoverygo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;

import ca.recoverygo.recoverygo.adapters.RecyclerViewAdapter;

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

        mImageUrls.add("https://static.olark.com/imageservice/f9d01505e78bd88f8c5c4e23e538078f.jpeg");
        mNames.add("The Developer");
        mTextBody.add("Body text here...");

        mImageUrls.add("http://static1.squarespace.com/static/54e2cd02e4b0409b06651c08/t/5582b592e4b0cb5018f711a5/1536748845874/?format=1500w");
        mNames.add("2");
        mTextBody.add("Body text here...");

        mImageUrls.add("http://www.sobrietyhouse.ca/wp-content/uploads/2015/12/Sobriety-House-Logo-1-web.png");
        mNames.add("3");
        mTextBody.add("Body text here...");

        mImageUrls.add("https://i.redd.it/obx4zydshg601.jpg");
        mNames.add("4");
        mTextBody.add("Body text here...");

        initRecyclerView();

    }

    private void initRecyclerView(){

        Log.d(TAG,"initRecyclerView: init recyclerview.");
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this,mNames,mImageUrls,mTextBody);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
