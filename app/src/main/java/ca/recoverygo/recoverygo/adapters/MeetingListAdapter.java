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
    private List<String> mGroupname;
    private List<String> mNote;
    private List<String> mSite;

    public MeetingListAdapter(List<String> address,
                              List<String> groupname,
                              List<String> note,
                              List<String> site) {

        mAddress        = address;
        mGroupname      = groupname;
        mNote           = note;
        mSite           = site;

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView mAddress;
        TextView mGroupname;
        TextView mNote;
        TextView mSite;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);

            mGroupname  = itemView.findViewById(R.id.groupname);
            mSite       = itemView.findViewById(R.id.site);
            mAddress    = itemView.findViewById(R.id.address);
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
        holder.mGroupname.setText(mGroupname.get(position));
        holder.mNote.setText(mNote.get(position));
        holder.mSite.setText(mSite.get(position));

    }

    @Override
    public int getItemCount() {
        return mAddress.size();
    }
}