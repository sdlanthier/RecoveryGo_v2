package ca.recoverygo.recoverygo.system;

import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import ca.recoverygo.recoverygo.R;

public class BaseActivity extends AppCompatActivity {

    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(false);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public void showSaveDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.saving));
            mProgressDialog.setIndeterminate(false);
        }
        mProgressDialog.show();
    }

    public void hideSaveDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }
    public void queryGeo(double lat,double lng){

        Geocoder gcd2 = new Geocoder(this, Locale.getDefault());
        List<Address> addresses2 = null;
        try {
            addresses2 = gcd2.getFromLocation(lat, lng, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Objects.requireNonNull(addresses2).size() > 0) {

            String locality         = addresses2.get(0).getLocality();
            String sublocality      = addresses2.get(0).getSubLocality();
            String adminarea        = addresses2.get(0).getAdminArea();
            String subadminarea     = addresses2.get(0).getSubAdminArea();
            String addressline      = addresses2.get(0).getAddressLine(0);
            String featurename      = addresses2.get(0).getFeatureName();
            String premesis         = addresses2.get(0).getPremises();
            String throughfare      = addresses2.get(0).getThoroughfare();
            String subthroughfare   = addresses2.get(0).getSubThoroughfare();
            String url              = addresses2.get(0).getUrl();
            String countrycode      = addresses2.get(0).getCountryCode();
            String countryname      = addresses2.get(0).getCountryName();
            String phone            = addresses2.get(0).getPhone();
            String postalcode       = addresses2.get(0).getPostalCode();
            String extras           = String.valueOf(addresses2.get(0).getExtras());
            String locale           = String.valueOf(addresses2.get(0).getLocale());
            String maxaddresslines  = String.valueOf(addresses2.get(0).getMaxAddressLineIndex());
            String latitude         = String.valueOf(addresses2.get(0).getLatitude());
            String longtitude       = String.valueOf(addresses2.get(0).getLongitude());
            Log.d("rg_", "onCreate:\n"+"locality: "+locality+"\n"+"subLocality: "+sublocality+"\n"+"adminArea: "+adminarea+"\n"+"subAdminArea: "+subadminarea+"\n"+"addressLine: "+addressline+"\n"+"feature: "+featurename
                    +"\n"+"premesis: "+premesis+"\n"+"throughfare: "+throughfare+"\n"+"subThroughfare: "+subthroughfare+"\n"+"url: "+url+"\n"+"extras: "+extras
                    +"\n"+"countrycode: "+countrycode+"\n"+"countryname: "+countryname+"\n"+"phone: "+phone+"\n"+"postalcode: "+postalcode+"\n"+"locale: "+locale
                    +"\n"+"maxaddresslines: "+maxaddresslines+"\n"+"latitude: "+latitude+"\n"+"longtitude: "+longtitude);
        }
    }


}
