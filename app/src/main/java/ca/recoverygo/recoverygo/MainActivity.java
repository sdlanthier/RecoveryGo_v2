package ca.recoverygo.recoverygo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_101) {
            Intent intent = new Intent(MainActivity.this, MeetingGuideActivity.class);
            startActivity(intent);
        /*} else if (id == R.id.nav_102) {
            Intent intent = new Intent(MainActivity.this, NotifyDemoActivity.class);
            startActivity(intent);*/
        } else if (id == R.id.nav_201) {
            Intent intent = new Intent(MainActivity.this, DirectoryActivity.class);
            startActivity(intent);
        /*} else if (id == R.id.nav_104) {
            Intent intent = new Intent(MainActivity.this, TwelveStepsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_201) {
            Intent intent = new Intent(MainActivity.this, TreatmentActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_202) {
            Intent intent = new Intent(MainActivity.this, RecoveryActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_203) {
            Intent intent = new Intent(MainActivity.this, LivingSoberActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_204) {
            Intent intent = new Intent(MainActivity.this, StartActivity.class);
            startActivity(intent);*/
        } else if (id == R.id.nav_301) {
            String link = getString(R.string.res_url_01);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            startActivity(browserIntent);
        } else if (id == R.id.nav_302) {
            String link = getString(R.string.res_url_02);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            startActivity(browserIntent);
        } else if (id == R.id.nav_303) {
            String link = getString(R.string.res_url_03);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            startActivity(browserIntent);
        } else if (id == R.id.nav_304) {
            String link = getString(R.string.res_url_04);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            startActivity(browserIntent);
        } else if (id == R.id.nav_305) {
            String link = getString(R.string.res_url_05);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            startActivity(browserIntent);
        } else if (id == R.id.nav_306) {
            String link = getString(R.string.res_url_06);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            startActivity(browserIntent);
        } else if (id == R.id.nav_307) {
            String link = getString(R.string.res_url_07);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            startActivity(browserIntent);
        } else if (id == R.id.nav_308) {
            String link = getString(R.string.res_url_08);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
            startActivity(browserIntent);
        } else if (id == R.id.nav_501) {
            Intent intent = new Intent(MainActivity.this, GalleryListActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
