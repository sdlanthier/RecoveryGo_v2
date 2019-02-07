package ca.recoverygo.recoverygo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import ca.recoverygo.recoverygo.ui.Meeting1Fragment;
import ca.recoverygo.recoverygo.ui.Meeting2Fragment;
import ca.recoverygo.recoverygo.ui.Meeting3Fragment;
import ca.recoverygo.recoverygo.ui.Meeting4Fragment;
import ca.recoverygo.recoverygo.ui.Meeting5Fragment;
import ca.recoverygo.recoverygo.ui.Meeting6Fragment;
import ca.recoverygo.recoverygo.ui.Meeting7Fragment;
import ca.recoverygo.recoverygo.ui.Meeting8Fragment;
import ca.recoverygo.recoverygo.ui.Meeting9Fragment;
import ca.recoverygo.recoverygo.ui.MeetingA1Fragment;
import ca.recoverygo.recoverygo.ui.MeetingA2Fragment;

public class MeetingGuideActivity extends AppCompatActivity {

    private static final String TAG="MeetingGuideActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_guide);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: Started");
        getMenuInflater().inflate(R.menu.menu_meeting_guide, menu);
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

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_meeting_guide, container, false);
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 1: return  new Meeting9Fragment();
                case 2: return  new Meeting1Fragment();
                case 3: return  new Meeting2Fragment();
                case 4: return  new Meeting3Fragment();
                case 5: return  new Meeting4Fragment();
                case 6: return  new Meeting5Fragment();
                case 7: return  new Meeting6Fragment();
                case 8: return  new Meeting7Fragment();
                case 9: return  new Meeting8Fragment();
                case 10: return new MeetingA2Fragment();
                case 11: return new MeetingA1Fragment();

                default: break; }
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 12;
        }
    }

    public void setVolumeOn() {

        AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        if (am != null) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 9, AudioManager.FLAG_SHOW_UI);
        }
        if (am != null) {
            am.setStreamVolume(AudioManager.STREAM_RING, 9, AudioManager.FLAG_SHOW_UI);
        }
        if (am != null) {
            am.setStreamVolume(AudioManager.STREAM_ALARM, 9, AudioManager.FLAG_SHOW_UI);
        }
        if (am != null) {
            am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 9, AudioManager.FLAG_SHOW_UI);
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
            }
        }, 10);
    }
    public void setVolumeOff(View view) {

        AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        if (am != null) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_SHOW_UI);
        }
        if (am != null) {
            am.setStreamVolume(AudioManager.STREAM_RING, 0, AudioManager.FLAG_SHOW_UI);
        }
        if (am != null) {
            am.setStreamVolume(AudioManager.STREAM_ALARM, 0, AudioManager.FLAG_SHOW_UI);
        }
        if (am != null) {
            am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, AudioManager.FLAG_SHOW_UI);
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setVolumeOn();
            }
        }, 3600000);

        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams") View popupView = inflater.inflate(R.layout.popup_window_mute, null);

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }
}
