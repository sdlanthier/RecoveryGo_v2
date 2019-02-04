package ca.recoverygo.recoverygo;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class BruceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bruce);
    }

    public void playFart1(View view) {
        MediaPlayer mp = MediaPlayer.create(this, R.raw.sound_clip_iguana_fart);
        mp.start();
    }
    public void playFart2(View view) {
        MediaPlayer mp = MediaPlayer.create(this, R.raw.sound_clip_wet_fart_squish);
        mp.start();
    }
    public void playFart3(View view) {
        MediaPlayer mp = MediaPlayer.create(this, R.raw.sound_clip_fart1);
        mp.start();
    }
    public void playFart4(View view) {
        MediaPlayer mp = MediaPlayer.create(this, R.raw.sound_clip_fart2);
        mp.start();
    }
    public void playFart5(View view) {
        MediaPlayer mp = MediaPlayer.create(this, R.raw.toilet);
        mp.start();
    }
}
