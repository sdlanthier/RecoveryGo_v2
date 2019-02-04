package ca.recoverygo.recoverygo.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import ca.recoverygo.recoverygo.system.IDataInputActivity;
import ca.recoverygo.recoverygo.R;
import ca.recoverygo.recoverygo.models.Note;

public class NoteRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "NoteRecyclerViewAdapter";
    private ArrayList<Note> mNotes;
    private IDataInputActivity mIDataInputActivity;
    private Context mContext;
    private int mSelectedNoteIndex;
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    public NoteRecyclerViewAdapter(Context context, ArrayList<Note> notes) {
        mNotes = notes;
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_note_list_item, parent, false);

        holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof ViewHolder){
            ((ViewHolder)holder).title.setText(mNotes.get(position).getTitle());

            SimpleDateFormat spf = new SimpleDateFormat("MMM dd, yyyy");
            String date = spf.format(mNotes.get(position).getTimestamp());
            ((ViewHolder)holder).timestamp.setText(date);
        }
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    public void updateNote(Note note){
       mNotes.get(mSelectedNoteIndex).setTitle(note.getTitle());
       mNotes.get(mSelectedNoteIndex).setContent(note.getContent());
       notifyDataSetChanged();
    }

    public void removeNote(Note note){
        mNotes.remove(note);
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mIDataInputActivity = (IDataInputActivity) mContext;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView title, timestamp;

        private ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            timestamp = itemView.findViewById(R.id.timestamp);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mSelectedNoteIndex = getAdapterPosition();
            mIDataInputActivity.onNoteSelected(mNotes.get(mSelectedNoteIndex));
        }
    }
}
















