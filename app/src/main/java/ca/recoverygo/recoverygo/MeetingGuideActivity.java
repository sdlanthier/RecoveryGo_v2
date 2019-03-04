package ca.recoverygo.recoverygo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import ca.recoverygo.recoverygo.ui.MeetingGuide01Fragment;
import ca.recoverygo.recoverygo.ui.MeetingGuide02Fragment;
import ca.recoverygo.recoverygo.ui.MeetingGuide03Fragment;
import ca.recoverygo.recoverygo.ui.MeetingGuide04Fragment;
import ca.recoverygo.recoverygo.ui.MeetingGuide05Fragment;
import ca.recoverygo.recoverygo.ui.MeetingGuide06Fragment;
import ca.recoverygo.recoverygo.ui.MeetingGuide07Fragment;
import ca.recoverygo.recoverygo.ui.MeetingGuide08Fragment;
import ca.recoverygo.recoverygo.ui.MeetingGuide09Fragment;
import ca.recoverygo.recoverygo.ui.MeetingGuide10Fragment;
import ca.recoverygo.recoverygo.ui.MeetingGuide11Fragment;

public class MeetingGuideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_guide);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
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
                case 1:
                    return new MeetingGuide09Fragment();
                case 2:
                    return new MeetingGuide01Fragment();
                case 3:
                    return new MeetingGuide02Fragment();
                case 4:
                    return new MeetingGuide03Fragment();
                case 5:
                    return new MeetingGuide04Fragment();
                case 6:
                    return new MeetingGuide05Fragment();
                case 7:
                    return new MeetingGuide06Fragment();
                case 8:
                    return new MeetingGuide07Fragment();
                case 9:
                    return new MeetingGuide08Fragment();
                case 10:
                    return new MeetingGuide10Fragment();
                case 11:
                    return new MeetingGuide11Fragment();

                default:
                    break;
            }
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 12;
        }
    }

    public void setVolumeOn() {
        AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

        if((this.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                < Configuration.SCREENLAYOUT_SIZE_LARGE) {
            if (am != null) {
                am.setStreamVolume(AudioManager.STREAM_RING, 9, AudioManager.FLAG_SHOW_UI);
            }
            if (am != null) {
                am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 9, AudioManager.FLAG_SHOW_UI);
            }
        }
        if (am != null) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 9, AudioManager.FLAG_SHOW_UI);
        }
        if (am != null) {
            am.setStreamVolume(AudioManager.STREAM_ALARM, 9, AudioManager.FLAG_SHOW_UI);
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

        if((this.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                < Configuration.SCREENLAYOUT_SIZE_LARGE) {
            if (am != null) {
                am.setStreamVolume(AudioManager.STREAM_RING, 0, AudioManager.FLAG_SHOW_UI);
            }
            if (am != null) {
                am.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, AudioManager.FLAG_SHOW_UI);
            }
        }
        if (am != null) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, AudioManager.FLAG_SHOW_UI);
        }
        if (am != null) {
            am.setStreamVolume(AudioManager.STREAM_ALARM, 0, AudioManager.FLAG_SHOW_UI);
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
