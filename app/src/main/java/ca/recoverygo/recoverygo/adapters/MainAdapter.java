package ca.recoverygo.recoverygo.adapters;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import ca.recoverygo.recoverygo.R;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private static final String TAG = "MainAdapter";
    private ArrayList<String> mName;
    private ArrayList<String> mStreet;
    private ArrayList<String> mCity;
    private ArrayList<String> mProv;
    private ArrayList<String> mPcode;
    private ArrayList<String> mPhone1;
    private ArrayList<String> mPhone2;
    private ArrayList<String> mType;
    private ArrayList<String> mAccess;
    private ArrayList<String> mBeds;
    private ArrayList<String> mEmail;
    private ArrayList<String> mWebsite;
    private ArrayList<String> mLogo;

    public MainAdapter(ArrayList<String> name, ArrayList<String> street, ArrayList<String> city, ArrayList<String> prov, ArrayList<String> pcode,
                       ArrayList<String> phone1, ArrayList<String> phone2,
                       ArrayList<String> type, ArrayList<String> access, ArrayList<String> capacity, ArrayList<String> email, ArrayList<String> website, ArrayList<String> logo) {

        mName       = name;
        mStreet     = street;
        mCity       = city;
        mProv       = prov;
        mPcode      = pcode;
        mPhone1     = phone1;
        mPhone2     = phone2;
        mType       = type;
        mAccess     = access;
        mBeds       = capacity;
        mEmail      = email;
        mWebsite    = website;
        mLogo       = logo;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView mName;
        TextView mStreet;
        TextView mCity;
        TextView mProv;
        TextView mPcode;
        TextView mPhone1;
        TextView mPhone2;
        TextView mType;
        TextView mAccess;
        TextView mBeds;
        TextView mEmail;
        TextView mWebsite;
        TextView mLogo;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);

            Log.d(TAG, "ViewHolder: Setting up individual cards");
            mName = itemView.findViewById(R.id.full_name);
            mStreet = itemView.findViewById(R.id.street);
            mCity = itemView.findViewById(R.id.city);
            mProv = itemView.findViewById(R.id.prov);
            mPcode = itemView.findViewById(R.id.pcode);
            mPhone1 = itemView.findViewById(R.id.phone1);
            mPhone2 = itemView.findViewById(R.id.phone2);
            mType = itemView.findViewById(R.id.type);
            mAccess = itemView.findViewById(R.id.access);
            mBeds = itemView.findViewById(R.id.capacity);
            mEmail = itemView.findViewById(R.id.email);
            mWebsite = itemView.findViewById(R.id.website);
            // mLogo = itemView.findViewById(R.id.logo_img);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { int position = getAdapterPosition();
                switch (position){
                    case 0: Snackbar.make(v, "" + position, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                        default:
                            break;
                    }
                Snackbar.make(v, "Click detected on item " + position, Snackbar.LENGTH_LONG).setAction("Action", null).show(); }});
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.mName.setText(mName.get(position));
        holder.mStreet.setText(mStreet.get(position));
        holder.mCity.setText(mCity.get(position));
        holder.mProv.setText(mProv.get(position));
        holder.mPcode.setText(mPcode.get(position));
        holder.mPhone1.setText(mPhone1.get(position));
        holder.mPhone2.setText(mPhone2.get(position));
        holder.mType.setText(mType.get(position));
        holder.mAccess.setText(mAccess.get(position));
        holder.mBeds.setText(mBeds.get(position));
        holder.mEmail.setText(mEmail.get(position));
        holder.mWebsite.setText(mWebsite.get(position));
        // holder.mLogo.setText(mLogo.get(position));
        // holder.mLogo.setImageResource(mLogo[position])
    }

    @Override
    public int getItemCount() {
        return mName.size();
    }
}