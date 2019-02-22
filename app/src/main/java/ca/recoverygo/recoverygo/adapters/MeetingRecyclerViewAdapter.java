package ca.recoverygo.recoverygo.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ca.recoverygo.recoverygo.R;
import ca.recoverygo.recoverygo.models.Meeting;
import ca.recoverygo.recoverygo.system.IMeetingInputActivity;

public class MeetingRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // private static final String TAG = "rg_MeetingRVA";

    private ArrayList<Meeting> mMeetings;
    private IMeetingInputActivity mIMeetingInputActivity;
    private Context mContext;
    private int mSelectedMeetingIndex;
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public MeetingRecyclerViewAdapter(Context context, ArrayList<Meeting> meetings) {
        mMeetings = meetings;
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_meeting_list_item, parent, false);
        holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).groupname.setText(mMeetings.get(position).getGroupname());
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView groupname;

        private ViewHolder(View itemView) {
            super(itemView);
            groupname = itemView.findViewById(R.id.group);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mSelectedMeetingIndex = getAdapterPosition();
            mIMeetingInputActivity.onMeetingSelected(mMeetings.get(mSelectedMeetingIndex));
        }
    }

    @Override
    public int getItemCount() {
        return mMeetings.size();
    }

    public void updateMeeting(Meeting meeting) {
        mMeetings.get(mSelectedMeetingIndex).setGroupname(meeting.getGroupname());
        mMeetings.get(mSelectedMeetingIndex).setSite(meeting.getSite());
        mMeetings.get(mSelectedMeetingIndex).setOrg(meeting.getOrg());
        mMeetings.get(mSelectedMeetingIndex).setNote(meeting.getNote());
        notifyDataSetChanged();
    }

    public void removeMeeting(Meeting meeting) {
        mMeetings.remove(meeting);
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mIMeetingInputActivity = (IMeetingInputActivity) mContext;
    }
}
















