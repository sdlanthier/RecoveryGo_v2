package ca.recoverygo.recoverygo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class GalleryActivity extends AppCompatActivity {

    private static final String TAG = "GalleryActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        Log.d(TAG, "onCreate: Started.");

        getIncomingIntent();
    }
    private void getIncomingIntent(){
        Log.d(TAG, "getIncomingIntent: Checking for incoming intents.");
        if(
                getIntent().hasExtra("image_url") &&
                getIntent().hasExtra("image_name") &&
                getIntent().hasExtra("text_body")) {

                Log.d(TAG, "getIncomingIntent: getIncomingIntent: found incoming extras.");

                String imageUrl = getIntent().getStringExtra("image_url");
                String imageName = getIntent().getStringExtra("image_name");
                String textBody = getIntent().getStringExtra("text_body");

                setImage(imageUrl,imageName,textBody);
        }
    }
    
    private void setImage(String imageUrl, String imageName, String textBody){
        Log.d(TAG, "setImage: setImage: setting ");

        ImageView image = findViewById(R.id.image);
            Glide.with(this)
                    .asBitmap()
                    .load(imageUrl)
                    .into(image);

        TextView name = findViewById(R.id.image_description);
        name.setText(imageName);

        TextView body = findViewById(R.id.text_body);
        body.setText(textBody);


    }
}
