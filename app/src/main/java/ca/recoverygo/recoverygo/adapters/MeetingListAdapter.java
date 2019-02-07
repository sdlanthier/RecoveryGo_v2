package ca.recoverygo.recoverygo.adapters;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;
import ca.recoverygo.recoverygo.R;

public class MeetingListAdapter extends RecyclerView.Adapter<MeetingListAdapter.ViewHolder> {

    private List<String> mAddress;
    private List<String> mDay;
    private List<String> mFormat;
    private List<String> mGroupname;
    private List<String> mIntergroup;
    private List<String> mLocation;
    private List<String> mNote;
    private List<String> mSite;
    private List<String> mTime;

    public MeetingListAdapter(List<String> address,
                              List<String> day,
                              List<String> format,
                              List<String> groupname,
                              List<String> intergroup,
                              List<String> location,
                              List<String> note,
                              List<String> site,
                              List<String> time) {

        mAddress        = address;
        mDay            = day;
        mFormat         = format;
        mGroupname      = groupname;
        mIntergroup     = intergroup;
        mLocation       = location;
        mNote           = note;
        mSite           = site;
        mTime           = time;

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView mAddress;
        TextView mDay;
        TextView mFormat;
        TextView mGroupname;
        TextView mIntergroup;
        TextView mLocation;
        TextView mNote;
        TextView mSite;
        TextView mTime;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);

            mGroupname  = itemView.findViewById(R.id.groupname);
            mIntergroup = itemView.findViewById(R.id.intergroup);
            mFormat     = itemView.findViewById(R.id.format);
            mDay        = itemView.findViewById(R.id.day);
            mTime       = itemView.findViewById(R.id.time);
            mSite       = itemView.findViewById(R.id.site);
            mAddress    = itemView.findViewById(R.id.address);
            mLocation   = itemView.findViewById(R.id.location);
            mNote       = itemView.findViewById(R.id.notes);

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meeting_list_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.mAddress.setText(mAddress.get(position));
        holder.mDay.setText(mDay.get(position));
        holder.mFormat.setText(mFormat.get(position));
        holder.mGroupname.setText(mGroupname.get(position));
        holder.mIntergroup.setText(mIntergroup.get(position));
        holder.mLocation.setText(mLocation.get(position));
        holder.mNote.setText(mNote.get(position));
        holder.mSite.setText(mSite.get(position));
        holder.mTime.setText(mTime.get(position));

    }

    @Override
    public int getItemCount() {
        return mAddress.size();
    }
}