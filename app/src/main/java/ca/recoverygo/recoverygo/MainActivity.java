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

import ca.recoverygo.recoverygo.ui.MeetingSetupActivity;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private static final String FILE_NAME = "rgsetup.txt";

    private FirebaseAuth mAuth;

    private TextView mStatusTextView;
    // private TextView mDetailTextView;
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
        final TextView mTextView = findViewById(R.id.drydate);

        mStatusTextView = findViewById(R.id.status);
        // mDetailTextView = findViewById(R.id.detail);
        mAuth = FirebaseAuth.getInstance();

        // mToday = findViewById(R.id.today);
        // mToday1 = findViewById(R.id.today1);
        mDays = findViewById(R.id.days);
        mHours = findViewById(R.id.hours);
        mFbSignedIn = findViewById(R.id.fbSignedIn);

        // **************************************************

        FileInputStream fis = null;
        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n"); }

            mTextView.setText(sb.toString());
            String textDate = sb.toString();
            Log.d(TAG, "onCreate: text:"+textDate);

            Date today = Calendar.getInstance().getTime();
            Log.d(TAG, "onCreate: today:"+today);
            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            String formattedDate = df.format(today);
            Log.d(TAG, "onCreate: today:"+formattedDate);
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

            Date date1;
            Date date2;

            try {
                date1 = format.parse(formattedDate);
                date2 = format.parse(textDate);
                DateTime dt1 = new DateTime(date1);
                DateTime dt2 = new DateTime(date2);

                String daysSober = (Days.daysBetween(dt2,dt1).getDays() + " days free!");
                String hoursSober = (Hours.hoursBetween(dt2,dt1).getHours() + " hours");

                mDays.setText(daysSober);
                mHours.setText(hoursSober);

                Log.d(TAG, "onCreate: dt1 = "+dt1);
                Log.d(TAG, "onCreate: dt2 = "+dt2);

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
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        // hideProgressDialog();
        if (user != null) {
            mStatusTextView.setText(getString(R.string.emailpassword_status_fmt));
            // mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));
            mFbSignedIn.setVisibility(View.VISIBLE);
            Log.d(TAG, "updateUI: User is logged in");

        } else {
            mStatusTextView.setText(R.string.signed_out);
            // mDetailTextView.setText(null);
        }
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
        } else if (id == R.id.nav_102) {
            Intent intent = new Intent(MainActivity.this, TestActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_201) {
            Intent intent = new Intent(MainActivity.this, DirectoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_202) {
            Intent intent = new Intent(MainActivity.this, WebActivity.class);
            startActivity(intent);
        }
            else if (id == R.id.nav_501) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
            else if (id == R.id.nav_502) {
            Intent intent = new Intent(MainActivity.this, BedInputActivity.class);
            startActivity(intent);
        }
            else if (id == R.id.nav_503) {
            Intent intent = new Intent(MainActivity.this, LocalSetupActivity.class);
            startActivity(intent);
        }
            else if (id == R.id.nav_504) {
            Intent intent = new Intent(MainActivity.this, FacilitySetupActivity.class);
            startActivity(intent);
        }
            else if (id == R.id.nav_505) {
            Intent intent = new Intent(MainActivity.this, MeetingSetupActivity.class);
            startActivity(intent);
        }
            else if (id == R.id.nav_506) {
            Intent intent = new Intent(MainActivity.this, LocatorActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
