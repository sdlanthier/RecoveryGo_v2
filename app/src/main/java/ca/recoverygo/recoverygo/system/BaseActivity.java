package ca.recoverygo.recoverygo.system;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;

import ca.recoverygo.recoverygo.R;

public class BaseActivity extends AppCompatActivity {

    public ProgressDialog mProgressDialog;
    public ProgressDialog mMapProgressDialog;
    public ProgressDialog mSaveProgressDialog;

    public void showMapProgressDialog() {
        if (mMapProgressDialog == null) {
            mMapProgressDialog = new ProgressDialog(this);
            mMapProgressDialog.setMessage(getString(R.string.loading_markers));
            mMapProgressDialog.setIndeterminate(false);
        }
        mMapProgressDialog.show();
    }
    public void hideMapProgressDialog() {
        if (mMapProgressDialog != null && mMapProgressDialog.isShowing()) {
            mMapProgressDialog.dismiss();
        }
    }
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

    public void showSaveProgressDialog() {
        if (mSaveProgressDialog == null) {
            mSaveProgressDialog = new ProgressDialog(this);
            mSaveProgressDialog.setMessage(getString(R.string.saving));
            mSaveProgressDialog.setIndeterminate(false);
        }
        mSaveProgressDialog.show();
    }
    public void hideSaveProgressDialog() {
        if (mSaveProgressDialog != null && mSaveProgressDialog.isShowing()) {
            mSaveProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
        hideMapProgressDialog();
        hideSaveProgressDialog();
    }

}
