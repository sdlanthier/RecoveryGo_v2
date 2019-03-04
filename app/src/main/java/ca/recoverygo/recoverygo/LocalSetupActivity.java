package ca.recoverygo.recoverygo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LocalSetupActivity extends AppCompatActivity {

    private static final String FILE_NAME = "rgsetup.txt";

    EditText mEditText;
    TextView mToday,mDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_setup);

        // **************************************************
        mEditText   = findViewById(R.id.edit_text);
        mToday      = findViewById(R.id.today);
        mDays       = findViewById(R.id.days);
        // **************************************************

        FileInputStream fis = null;

        try {
            fis = openFileInput(FILE_NAME);

            InputStreamReader isr   = new InputStreamReader(fis);
            BufferedReader br       = new BufferedReader(isr);
            StringBuilder sb        = new StringBuilder();
            String text;

            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }

            mEditText.setText(sb.toString());
            String textDate = sb.toString();

            Date today = Calendar.getInstance().getTime();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
            String formattedDate = df.format(today);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
            mToday.setText(formattedDate);

            Date date1;
            Date date2;

            try {
                date1 = format.parse(formattedDate);
                date2 = format.parse(textDate);
                DateTime dt1 = new DateTime(date1);
                DateTime dt2 = new DateTime(date2);

                String daysSober = (Days.daysBetween(dt2, dt1).getDays() + " days");
                mDays.setText(daysSober);

            } catch (ParseException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {

            //TODO: Welcome scree activity here.

            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void save(View v) {
        String text = mEditText.getText().toString();
        FileOutputStream fos = null;
        mEditText.getText().clear();

        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(text.getBytes());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Intent intent = new Intent(LocalSetupActivity.this, MainActivity.class);
        startActivity(intent);

    }
    public void load(View v) {
        FileInputStream fis = null;
        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }

            mEditText.setText(sb.toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(LocalSetupActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
