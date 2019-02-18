package ca.recoverygo.recoverygo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG         = "rg_MainActivity";
    private static final String FILE_NAME   = "rgsetup.txt";
    
    private FirebaseAuth mAuth;

    private TextView mStatusTextView;
            TextView mDays;
            TextView mHours;
            ImageView mFbSignedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        mAuth                       = FirebaseAuth.getInstance();
        mDays                       = findViewById(R.id.days);
        mHours                      = findViewById(R.id.hours);
        mFbSignedIn                 = findViewById(R.id.fbSignedIn);
        TextView mTextView          = findViewById(R.id.drydate);
        mStatusTextView             = findViewById(R.id.status);
        // **************************************************
        FileInputStream fis = null;
        try {
            Log.d(TAG, "onCreate: opening file:"+FILE_NAME);
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine()) != null) { sb.append(text).append("\n");
                Log.d(TAG, "onCreate: file contents:"+text);
            }

            mTextView.setText(sb.toString());
            String savedDate = sb.toString();

            Date today = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            String todayDate = df.format(today);

            Date date1, date2;

            try {
                date1 = df.parse(todayDate); date2 = df.parse(savedDate);
                DateTime dt1 = new DateTime(date1); DateTime dt2 = new DateTime(date2);

                String daysSober = (Days.daysBetween(dt2,dt1).getDays() + " days free!");
                String hoursSober = (Hours.hoursBetween(dt2,dt1).getHours()-1 + " hours");
                mDays.setText(daysSober); mHours.setText(hoursSober);

                int in = Days.daysBetween(dt2,dt1).getDays();

                if (in ==30) {
                    Log.d(TAG, "onCreate: xxx_result = 30");
                    Toast.makeText(this, "Congrats: "+daysSober, Toast.LENGTH_SHORT).show();
                }
                if (in ==60) {
                    Log.d(TAG, "onCreate: xxx_result = 60");
                    Toast.makeText(this, "Congrats: "+daysSober, Toast.LENGTH_SHORT).show();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (fis != null) {
                try {
                    fis.close();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d(TAG, "onCreate: finished");
    }
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: getting user info");
        FirebaseApp.initializeApp(this);
        FirebaseUser currentUser;
        currentUser = mAuth.getCurrentUser();
        Log.d(TAG, "onStart: sending to updateUI: "+currentUser);
        updateUI(currentUser);
    }
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            String uid = user.getUid();

            Log.d(TAG, "updateUI: user = "+uid);
            Log.d(TAG, "updateUI: name = "+name);
            Log.d(TAG, "updateUI: email = "+email);

            mStatusTextView.setText(getString(R.string.emailpassword_status_fmt));
            mFbSignedIn.setVisibility(View.VISIBLE);

        } else {
            Log.d(TAG, "updateUI: user is null");
            mStatusTextView.setText(R.string.signed_out);
            Log.d(TAG, "updateUI: User is logged out");
        }
        Log.d(TAG, "updateUI: complete");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, LocalSetupActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_101) {
            Intent intent = new Intent(MainActivity.this, MeetingGuideActivity.class);
            startActivity(intent);
        }   else if (id == R.id.nav_102) {
            Intent intent = new Intent(MainActivity.this, MeetingInfoActivity.class);
            startActivity(intent);
        }   else if (id == R.id.nav_103) {
            Intent intent = new Intent(MainActivity.this, LocatorActivity.class);
            startActivity(intent);

        }   else if (id == R.id.nav_201) {
            Intent intent = new Intent(MainActivity.this, DataInputActivity.class);
            startActivity(intent);
        }   else if (id == R.id.nav_202) {
            Intent intent = new Intent(MainActivity.this, LocalSetupActivity.class);
            startActivity(intent);
        }   else if (id == R.id.nav_203) {
            Intent intent = new Intent(MainActivity.this, WebActivity.class);
            startActivity(intent);

        }   else if (id == R.id.nav_301) {
            Intent intent = new Intent(MainActivity.this, DirectoryInputActivity.class);
            startActivity(intent);
        }   else if (id == R.id.nav_302) {
            Intent intent = new Intent(MainActivity.this, MeetingSetupActivity.class);
            startActivity(intent);
        }   else if (id == R.id.nav_303) {
            Intent intent = new Intent(MainActivity.this, MeetingInputActivity.class);
            startActivity(intent);

        }   else if (id == R.id.nav_401) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        /*}   else if (id == R.id.nav_402) {
            Intent intent = new Intent(MainActivity.this, FingerPaintMainActivity.class);
            startActivity(intent);*/
        }   else if (id == R.id.nav_403) {
            Intent intent = new Intent(MainActivity.this, GalleryListActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
