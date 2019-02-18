package ca.recoverygo.recoverygo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import ca.recoverygo.recoverygo.adapters.MainAdapter;

public class DirectoryActivity extends AppCompatActivity {

    // private static final String TAG = "DirectoryActivity";

    // csvImport variables
    InputStream inputStream;
    String[] data;

    ArrayList<String> mName;
    ArrayList<String> mStreet;
    ArrayList<String> mCity;
    ArrayList<String> mProv;
    ArrayList<String> mPcode;
    ArrayList<String> mPhone1;
    ArrayList<String> mPhone2;
    ArrayList<String> mType;
    ArrayList<String> mAccess;
    ArrayList<String> mCapacity;
    ArrayList<String> mEmail;
    ArrayList<String> mWebsite;
    ArrayList<String> mLogo;

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory);

        mRecyclerView   = findViewById(R.id.recyclev1);
        mLayoutManager  = new LinearLayoutManager((this));
        mRecyclerView.setLayoutManager(mLayoutManager);

        // *****************************************************************************************
        inputStream = getResources().openRawResource(R.raw.directory);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        mName       = new ArrayList<>();
        mStreet     = new ArrayList<>();
        mCity       = new ArrayList<>();
        mProv       = new ArrayList<>();
        mPcode      = new ArrayList<>();
        mPhone1     = new ArrayList<>();
        mPhone2     = new ArrayList<>();
        mType       = new ArrayList<>();
        mAccess     = new ArrayList<>();
        mCapacity   = new ArrayList<>();
        mEmail      = new ArrayList<>();
        mWebsite    = new ArrayList<>();
        mLogo       = new ArrayList<>();

        try {
            String csvLine;
            while ((csvLine = reader.readLine()) != null) { data = csvLine.split(",");
                try {
                    mName.      add(data[1]);
                    mStreet.    add(data[2]);
                    mCity.      add(data[3]);
                    mProv.      add(data[4]);
                    mPcode.     add(data[5]);
                    mPhone1.    add(data[6]);
                    mPhone2.    add(data[7]);
                    mEmail.     add(data[8]);
                    mWebsite.   add(data[9]);
                    mType.      add(data[10]);
                    mAccess.    add(data[11]);
                    mCapacity.  add(data[12]);
                    mLogo.      add(data[13]);

                } catch (Exception e) {
                    Log.e("DATA RECORD IMPORT FAIL", e.toString());
                }
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Error reading states.csv file: "+ ex);
        }
        // *****************************************************************************************

        mAdapter = new MainAdapter(mName,mStreet,mCity,mProv,mPcode,mPhone1,mPhone2,mType,mAccess,mCapacity,mEmail,mWebsite,mLogo);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
    }

}
