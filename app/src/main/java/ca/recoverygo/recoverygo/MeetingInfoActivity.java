package ca.recoverygo.recoverygo;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.recoverygo.recoverygo.ui.MeetingInfo1Fragment;
import ca.recoverygo.recoverygo.ui.MeetingInfo2Fragment;
import ca.recoverygo.recoverygo.ui.MeetingInfo3Fragment;
import ca.recoverygo.recoverygo.ui.MeetingInfo4Fragment;
import ca.recoverygo.recoverygo.ui.MeetingInfo5Fragment;
import ca.recoverygo.recoverygo.ui.MeetingInfo6Fragment;

public class MeetingInfoActivity extends AppCompatActivity {

    // private static final String TAG="RGO_MeetingInfoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_info);

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
            return inflater.inflate(R.layout.fragment_meeting_info, container, false);
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 1: return  new MeetingInfo1Fragment();
                case 2: return  new MeetingInfo2Fragment();
                case 3: return  new MeetingInfo3Fragment();
                case 4: return  new MeetingInfo4Fragment();
                case 5: return  new MeetingInfo5Fragment();
                case 6: return  new MeetingInfo6Fragment();
                default: break; }

            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 7;
        }
    }
}
