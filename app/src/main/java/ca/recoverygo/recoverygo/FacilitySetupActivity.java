package ca.recoverygo.recoverygo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FacilitySetupActivity extends AppCompatActivity {

 // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private static final String TAG                 = "LocalSetupActivity";
    private static final String FILE_NAME           = "facilitylist.txt";
    private static final String CONTAINER_NAME      = "Notebook";
    private static final String KEY_NAME            = "name";
    private static final String KEY_CAMPUS          = "campus";
    private static final String KEY_CAMPUSGENDER    = "campusgender";
    private static final String KEY_STREET          = "street";
    private static final String KEY_CITY            = "city";
    private static final String KEY_PROV            = "prov";
    private static final String KEY_PCODE           = "pcode";
    private static final String KEY_COUNTRY         = "country";
    private static final String KEY_PHONE1          = "phone1";
    private static final String KEY_PHONE2          = "phone2";
    private static final String KEY_EMAIL           = "email";
    private static final String KEY_WEBSITE         = "website";
    private static final String KEY_LOGOURL         = "logourl";
    private static final String KEY_BEDSTTL         = "bedsttl";
    private static final String KEY_BEDREPAIR       = "bedrepair";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    EditText mFieldName;            ArrayList<String> mName;
    EditText mFieldCampus;          ArrayList<String> mCampus;
    EditText mFieldCampusGender;    ArrayList<String> mCampusGender;
    EditText mFieldStreet;          ArrayList<String> mStreet;
    EditText mFieldCity;            ArrayList<String> mCity;
    EditText mFieldProv;            ArrayList<String> mProv;
    EditText mFieldPcode;           ArrayList<String> mPcode;
    EditText mFieldCountry;         ArrayList<String> mCountry;
    EditText mFieldPhone1;          ArrayList<String> mPhone1;
    EditText mFieldPhone2;          ArrayList<String> mPhone2;
    EditText mFieldEmail;           ArrayList<String> mEmail;
    EditText mFieldWebSite;         ArrayList<String> mWebsite;
    EditText mFieldLogoUrl;         ArrayList<String> mLogoUrl;
    EditText mFieldBedsTtl;         ArrayList<String> mBedsTtl;
    EditText mFieldBedsRepair;      ArrayList<String> mBedsRepair;

    String[] data;
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_setup);

     // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        mFieldName          = findViewById(R.id.field_name);
        mFieldStreet        = findViewById(R.id.field_street);
        mFieldCity          = findViewById(R.id.field_city);
        mFieldProv          = findViewById(R.id.field_prov);
        mFieldPcode         = findViewById(R.id.field_pcode);
        mFieldCountry       = findViewById(R.id.field_country);
        mFieldCampus        = findViewById(R.id.field_campus);
        mFieldPhone1        = findViewById(R.id.field_phone1);
        mFieldPhone2        = findViewById(R.id.field_phone2);
        mFieldEmail         = findViewById(R.id.field_email);
        mFieldWebSite       = findViewById(R.id.field_website);
        mFieldLogoUrl       = findViewById(R.id.field_logo_url);
        mFieldCampusGender  = findViewById(R.id.field_campus_type);
        mFieldBedsTtl       = findViewById(R.id.field_beds_ttl);
        mFieldBedsRepair    = findViewById(R.id.field_beds_repair);
     // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    }

    public void save(View v) {
        // **************************************************
        String name         = mFieldName.getText().toString();
        String street       = mFieldStreet.getText().toString();
        String city         = mFieldCity.getText().toString();
        String prov         = mFieldProv.getText().toString();
        String pcode        = mFieldPcode.getText().toString();
        String country      = mFieldCountry.getText().toString();
        String campus       = mFieldCampus.getText().toString();
        String phone1       = mFieldPhone1.getText().toString();
        String phone2       = mFieldPhone2.getText().toString();
        String email        = mFieldEmail.getText().toString();
        String website      = mFieldWebSite.getText().toString();
        String logourl      = mFieldLogoUrl.getText().toString();
        String campusgender = mFieldCampusGender.getText().toString();
        String bedsttl      = mFieldBedsTtl.getText().toString();
        String bedsrepair   = mFieldBedsRepair.getText().toString();

        // **************************************************

        FileOutputStream fos = null;

        // **************************************************
        mFieldName.getText().clear();
        mFieldStreet.getText().clear();
        mFieldCity.getText().clear();
        mFieldProv.getText().clear();
        mFieldPcode.getText().clear();
        mFieldCountry.getText().clear();
        mFieldCampus.getText().clear();
        mFieldPhone1.getText().clear();
        mFieldPhone2.getText().clear();
        mFieldEmail.getText().clear();
        mFieldWebSite.getText().clear();
        mFieldLogoUrl.getText().clear();
        mFieldCampusGender.getText().clear();
        mFieldBedsTtl.getText().clear();
        mFieldBedsRepair.getText().clear();

        // **************************************************
        String facility = name + "," + street + "," + city + "," + prov + "," + pcode + "," + country + "," + campus + "," + phone1 + "," + phone2 + "," + email + "," + website + "," + logourl + "," + campusgender + "," + bedsttl + "," + bedsrepair + "\n";
        String facilityid = name + " - " + campus;


        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(facility.getBytes());

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
            Toast.makeText(this, "Saved to: " + getFilesDir() + "/" + FILE_NAME, Toast.LENGTH_LONG).show();
        }

        // **************************************************
        Map<String,Object> facilityrcd = new HashMap<>();

        facilityrcd.put(KEY_NAME,        name+" - "+campus);
        facilityrcd.put(KEY_CAMPUS,         campus);
        facilityrcd.put(KEY_CAMPUSGENDER,   campusgender);
        facilityrcd.put(KEY_STREET,         street);
        facilityrcd.put(KEY_CITY,           city);
        facilityrcd.put(KEY_PROV,           prov);
        facilityrcd.put(KEY_PCODE,          pcode);
        facilityrcd.put(KEY_COUNTRY,        country);
        facilityrcd.put(KEY_PHONE1,         phone1);
        facilityrcd.put(KEY_PHONE2,         phone2);
        facilityrcd.put(KEY_EMAIL,          email);
        facilityrcd.put(KEY_WEBSITE,        website);
        facilityrcd.put(KEY_LOGOURL,        logourl);
        facilityrcd.put(KEY_BEDSTTL,        bedsttl);
        facilityrcd.put(KEY_BEDREPAIR,      bedsrepair);

        db.collection(CONTAINER_NAME).document(facilityid).set(facilityrcd).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(FacilitySetupActivity.this, "Record Saved", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FacilitySetupActivity.this, "Error!", Toast.LENGTH_LONG).show();
                Log.d(TAG, "onFailure: "+e.toString());
            }
        });
    }

    public void load(View v) {
        FileInputStream fis;

        mName           = new ArrayList<>();
        mStreet         = new ArrayList<>();
        mCity           = new ArrayList<>();
        mProv           = new ArrayList<>();
        mPcode          = new ArrayList<>();
        mCountry        = new ArrayList<>();
        mCampus         = new ArrayList<>();
        mPhone1         = new ArrayList<>();
        mPhone2         = new ArrayList<>();
        mEmail          = new ArrayList<>();
        mWebsite        = new ArrayList<>();
        mLogoUrl        = new ArrayList<>();
        mCampusGender   = new ArrayList<>();
        mBedsTtl        = new ArrayList<>();
        mBedsRepair     = new ArrayList<>();

        try {
            fis = openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(isr);

            String record;
            while ((record = reader.readLine()) != null) {
                data = record.split(",");
                try {
                    mName.          add(data[1]);
                    mStreet.        add(data[2]);
                    mCity.          add(data[3]);
                    mProv.          add(data[4]);
                    mPcode.         add(data[5]);
                    mCountry.       add(data[6]);
                    mCampus.        add(data[7]);
                    mPhone1.        add(data[8]);
                    mPhone2.        add(data[9]);
                    mEmail.         add(data[10]);
                    mWebsite.       add(data[11]);
                    mLogoUrl.       add(data[12]);
                    mCampusGender  .add(data[13]);
                    mBedsTtl.       add(data[14]);
                    mBedsRepair.    add(data[15]);
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                mFieldName          .setText(data[0]);
                mFieldStreet        .setText(data[1]);
                mFieldCity          .setText(data[2]);
                mFieldProv          .setText(data[3]);
                mFieldPcode         .setText(data[4]);
                mFieldCountry       .setText(data[5]);
                mFieldCampus        .setText(data[6]);
                mFieldPhone1        .setText(data[7]);
                mFieldPhone2        .setText(data[8]);
                mFieldEmail         .setText(data[9]);
                mFieldWebSite       .setText(data[10]);
                mFieldLogoUrl       .setText(data[11]);
                mFieldCampusGender  .setText(data[12]);
                mFieldBedsTtl       .setText(data[13]);
                mFieldBedsRepair    .setText(data[14]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
