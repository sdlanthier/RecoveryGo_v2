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
import ca.recoverygo.recoverygo.models.Entry;
import ca.recoverygo.recoverygo.system.IDirectoryInputActivity;

public class EntryRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Entry>        mNames;
    private IDirectoryInputActivity mIDirectoryInputActivity;
    private Context                 mContext;
    private int                     mSelectedEntryIndex;

    public EntryRecyclerViewAdapter(Context context, ArrayList<Entry> names) {
        mNames = names;
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_entry_list_item, parent, false);
        holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ViewHolder) {
            ((ViewHolder) holder).name.setText(mNames.get(position).getName());
            ((ViewHolder) holder).city.setText(mNames.get(position).getCity());
            ((ViewHolder) holder).gender.setText(mNames.get(position).getGender());
            ((ViewHolder) holder).nextavail.setText(mNames.get(position).getNextavail());
        }
    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    public void updateEntry(Entry entry) {
        mNames.get(mSelectedEntryIndex).setName(entry.getName());
        mNames.get(mSelectedEntryIndex).setCity(entry.getCity());
        mNames.get(mSelectedEntryIndex).setGender(entry.getGender());
        mNames.get(mSelectedEntryIndex).setNextavail(entry.getNextavail());

        notifyDataSetChanged();
    }

    public void removeEntry(Entry entry) {
        mNames.remove(entry);
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mIDirectoryInputActivity = (IDirectoryInputActivity) mContext;

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name, city, gender, nextavail;

        private ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            city = itemView.findViewById(R.id.city);
            gender = itemView.findViewById(R.id.gender);
            nextavail = itemView.findViewById(R.id.nextavail);
            // timestamp = itemView.findViewById(R.id.timestamp);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mSelectedEntryIndex = getAdapterPosition();
            mIDirectoryInputActivity.onEntrySelected(mNames.get(mSelectedEntryIndex));
        }
    }
}