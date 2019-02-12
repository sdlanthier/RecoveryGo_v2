package ca.recoverygo.recoverygo.models;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import ca.recoverygo.recoverygo.R;
import ca.recoverygo.recoverygo.system.IDirectoryInputActivity;

public class ViewEntryDialog extends DialogFragment implements View.OnClickListener{

    private static final String TAG = "RGO_ViewEntryDialog";

    // private EditText mName, mStreet, mCity, mProv, mPcode, mPhone, mWeb, mBedsttl, mBedsrepair, mBedspublic, mWaittime, mGender, mNextavail;
    private TextView vName, vStreet, vCity, vProv, vPcode, vPhone, vWeb, vBedsttl, vBedsrepair, vBedspublic, vWaittime, vGender, vNextavail;
    private TextView mSave, mDelete;

    private IDirectoryInputActivity mIDirectoryInputActivity;
    private Entry mEntry;

    public static ViewEntryDialog newInstance(Entry entry) {
        ViewEntryDialog dialog = new ViewEntryDialog();
        Log.d(TAG, "newInstance: "+entry);
        Bundle args = new Bundle();
        args.putParcelable("entry", entry);
        dialog.setArguments(args);
        Log.d(TAG, "newInstance: "+dialog);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Started");
        int style = DialogFragment.STYLE_NORMAL;
        int theme = android.R.style.Theme_Holo_Light_Dialog;
        setStyle(style, theme);

        mEntry = getArguments().getParcelable("entry");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: inflate layout: dialog_view_entry");
        View view = inflater.inflate(R.layout.dialog_view_entry, container, false);

/*        mName       = view.findViewById(R.id.name);
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
        mSave       = view.findViewById(R.id.save);
        mDelete     = view.findViewById(R.id.delete);*/
        //-------------------------------------------------------------
        vName       = view.findViewById(R.id.vname);
        vStreet     = view.findViewById(R.id.vstreet);
        vCity       = view.findViewById(R.id.vcity);
        vProv       = view.findViewById(R.id.vprov);
        vPcode      = view.findViewById(R.id.vpcode);
        vPhone      = view.findViewById(R.id.vphone);
        vNextavail  = view.findViewById(R.id.vnextavail);
        /*vWeb        = view.findViewById(R.id.vweb);
        vBedsttl    = view.findViewById(R.id.vbedsttl);
        vBedsrepair = view.findViewById(R.id.vbedsrepair);
        vBedspublic = view.findViewById(R.id.vbedspublic);
        vWaittime   = view.findViewById(R.id.vwaittime);
        vGender     = view.findViewById(R.id.vgender);
        */

        getDialog().setTitle("Contact Information");
        setInitialProperties();

        return view;
    }

    private void setInitialProperties(){
/*        mName.      setText(mEntry.getName());
        mStreet.    setText(mEntry.getStreet());
        mCity.      setText(mEntry.getCity());
        mProv.      setText(mEntry.getProv());
        mPcode.     setText(mEntry.getPcode());
        mPhone.     setText(mEntry.getPhone());
        mWeb.       setText(mEntry.getWeb());
        mBedsttl.   setText(mEntry.getBedsttl());
        mBedsrepair.setText(mEntry.getBedsrepair());
        mBedspublic.setText(mEntry.getBedspublic());
        mWaittime.  setText(mEntry.getWaittime());
        mGender.    setText(mEntry.getGender());
        mNextavail. setText(mEntry.getNextavail());*/

        vName.      setText(mEntry.getName());
        vStreet.    setText(mEntry.getStreet());
        vCity.      setText(mEntry.getCity());
        vProv.      setText(mEntry.getProv());
        vPcode.     setText(mEntry.getPcode());
        vPhone.     setText(mEntry.getPhone());
        vNextavail. setText(mEntry.getNextavail());
/*        vWeb.       setText(mEntry.getWeb());
        vBedsttl.   setText(mEntry.getBedsttl());
        vBedsrepair.setText(mEntry.getBedsrepair());
        vBedspublic.setText(mEntry.getBedspublic());
        vWaittime.  setText(mEntry.getWaittime());
        vGender.    setText(mEntry.getGender());
        vNextavail. setText(mEntry.getNextavail());*/

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.save:{

                /*String name         = mName.getText().toString();
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

                if(!name.equals("")){ mEntry.setName(name);

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
                    Log.d(TAG, "onClick: sending to: IDirectoryInputActivity"+mEntry);
                    mIDirectoryInputActivity.updateEntry(mEntry);
                    getDialog().dismiss();
                }
                else{
                    Toast.makeText(getActivity(), "Enter a Name", Toast.LENGTH_SHORT).show();
                }*/
                break;
            }

            case R.id.delete:{
                mIDirectoryInputActivity.deleteEntry(mEntry);
                getDialog().dismiss();
                break;
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: sending to: IDirectoryInputActivity"+context);
        mIDirectoryInputActivity = (IDirectoryInputActivity)getActivity();
    }

}