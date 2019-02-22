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

import java.util.Objects;

import ca.recoverygo.recoverygo.R;
import ca.recoverygo.recoverygo.system.IDirectoryInputActivity;

public class EditEntryDialog extends DialogFragment implements View.OnClickListener {


    private EditText mName, mStreet, mCity, mProv, mPcode, mPhone, mWeb, mBedsttl, mBedsrepair, mBedspublic, mWaittime, mGender, mNextavail;
    TextView mSave, mDelete;

    private IDirectoryInputActivity mIDirectoryInputActivity;
    private Entry mEntry;

   /* public static EditEntryDialog newInstance(Entry entry) {
        EditEntryDialog dialog = new EditEntryDialog();
        Bundle args = new Bundle();
        args.putParcelable("entry", entry);
        dialog.setArguments(args);
        return dialog;
    }*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int theme = android.R.style.Theme_Holo_Light_Dialog;
        setStyle(DialogFragment.STYLE_NORMAL, theme);

        mEntry = Objects.requireNonNull(getArguments()).getParcelable("entry");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_view_entry, container, false);

        mName = view.findViewById(R.id.name);
        mStreet = view.findViewById(R.id.street);
        mCity = view.findViewById(R.id.city);
        mProv = view.findViewById(R.id.prov);
        mPcode = view.findViewById(R.id.pcode);
        mPhone = view.findViewById(R.id.phone);
        mWeb = view.findViewById(R.id.web);
        mBedsttl = view.findViewById(R.id.bedsttl);
        mBedsrepair = view.findViewById(R.id.bedsrepair);
        mBedspublic = view.findViewById(R.id.bedspublic);
        mWaittime = view.findViewById(R.id.waittime);
        mGender = view.findViewById(R.id.gender);
        mNextavail = view.findViewById(R.id.nextavail);
        mSave = view.findViewById(R.id.save);
        mDelete = view.findViewById(R.id.delete);

        mSave.setOnClickListener(this);
        mDelete.setOnClickListener(this);

        getDialog().setTitle("Edit Entry");
        setInitialProperties();

        return view;
    }

    private void setInitialProperties() {
        mName.setText(mEntry.getName());
        mStreet.setText(mEntry.getStreet());
        mCity.setText(mEntry.getCity());
        mProv.setText(mEntry.getProv());
        mPcode.setText(mEntry.getPcode());
        mPhone.setText(mEntry.getPhone());
        mWeb.setText(mEntry.getWeb());
        mBedsttl.setText(mEntry.getBedsttl());
        mBedsrepair.setText(mEntry.getBedsrepair());
        mBedspublic.setText(mEntry.getBedspublic());
        mWaittime.setText(mEntry.getWaittime());
        mGender.setText(mEntry.getGender());
        mNextavail.setText(mEntry.getNextavail());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.save: {

                String name = mName.getText().toString();
                String street = mStreet.getText().toString();
                String city = mCity.getText().toString();
                String prov = mProv.getText().toString();
                String pcode = mPcode.getText().toString();
                String phone = mPhone.getText().toString();
                String web = mWeb.getText().toString();
                String bedsttl = mBedsttl.getText().toString();
                String bedsrepair = mBedsrepair.getText().toString();
                String bedspublic = mBedspublic.getText().toString();
                String waittime = mWaittime.getText().toString();
                String gender = mGender.getText().toString();
                String nextavail = mNextavail.getText().toString();

                if (!name.equals("")) {
                    mEntry.setName(name);

                    mEntry.setStreet(street);
                    mEntry.setCity(city);
                    mEntry.setProv(prov);
                    mEntry.setPcode(pcode);
                    mEntry.setPhone(phone);
                    mEntry.setWeb(web);
                    mEntry.setBedsttl(bedsttl);
                    mEntry.setBedsrepair(bedsrepair);
                    mEntry.setBedspublic(bedspublic);
                    mEntry.setWaittime(waittime);
                    mEntry.setGender(gender);
                    mEntry.setNextavail(nextavail);

                    mIDirectoryInputActivity.updateEntry(mEntry);
                    getDialog().dismiss();
                } else {
                    Toast.makeText(getActivity(), "Enter a Name", Toast.LENGTH_SHORT).show();
                }
                break;
            }

            case R.id.delete: {
                mIDirectoryInputActivity.deleteEntry(mEntry);
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