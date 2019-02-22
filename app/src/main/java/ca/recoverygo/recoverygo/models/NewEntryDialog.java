package ca.recoverygo.recoverygo.models;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ca.recoverygo.recoverygo.R;
import ca.recoverygo.recoverygo.system.IDirectoryInputActivity;

public class NewEntryDialog extends DialogFragment implements View.OnClickListener {

    private EditText mName, mStreet, mCity, mProv, mPcode, mPhone, mWeb, mBedsttl, mBedsrepair, mBedspublic, mWaittime, mGender, mNextavail;
    private IDirectoryInputActivity mIDirectoryInputActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int theme = android.R.style.Theme_Holo_Light_Dialog;
        setStyle(DialogFragment.STYLE_NORMAL, theme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_new_entry, container, false);

        mName       = view.findViewById(R.id.name);
        mStreet     = view.findViewById(R.id.street);
        mCity       = view.findViewById(R.id.city);
        mProv       = view.findViewById(R.id.prov);
        mPcode      = view.findViewById(R.id.pcode);
        mPhone      = view.findViewById(R.id.phone);
        mWeb        = view.findViewById(R.id.web);
        mBedsttl    = view.findViewById(R.id.bedsttl);
        mBedsrepair = view.findViewById(R.id.bedsrepair);
        mBedspublic = view.findViewById(R.id.bedspublic);
        mWaittime   = view.findViewById(R.id.waittime);
        mGender     = view.findViewById(R.id.gender);
        mNextavail  = view.findViewById(R.id.nextavail);

        TextView mCreate = view.findViewById(R.id.create);
        TextView mCancel = view.findViewById(R.id.cancel);

        mCancel.setOnClickListener(this);
        mCreate.setOnClickListener(this);

        getDialog().setTitle("New Entry");

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.create: {

                String name         = mName.getText().toString();
                String street       = mStreet.getText().toString();
                String city         = mCity.getText().toString();
                String prov         = mProv.getText().toString();
                String pcode        = mPcode.getText().toString();
                String phone        = mPhone.getText().toString();
                String web          = mWeb.getText().toString();
                String bedsttl      = mBedsttl.getText().toString();
                String bedsrepair   = mBedsrepair.getText().toString();
                String bedspublic   = mBedspublic.getText().toString();
                String waittime     = mWaittime.getText().toString();
                String gender       = mGender.getText().toString();
                String nextavail    = mNextavail.getText().toString();

                if (!name.equals("")) {
                    mIDirectoryInputActivity.createNewEntry(name, street, city, prov, pcode, phone, web, bedsttl, bedsrepair, bedspublic, waittime, gender, nextavail);
                    getDialog().dismiss();
                } else {
                    Toast.makeText(getActivity(), "Enter Name of Treatment Centre", Toast.LENGTH_SHORT).show();
                }
                break;
            }

            case R.id.cancel: {
                getDialog().dismiss();
                break;
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mIDirectoryInputActivity = (IDirectoryInputActivity) getActivity();
    }
}