package ca.recoverygo.recoverygo.adapters;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import ca.recoverygo.recoverygo.GalleryActivity;
import ca.recoverygo.recoverygo.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";

    private Context mContext;
    private ArrayList<String> mImageNames = new ArrayList<>();
    private ArrayList<String> mImages = new ArrayList<>();
    private ArrayList<String> mTextBodys = new ArrayList<>();



    public RecyclerViewAdapter(Context context, ArrayList<String> imageNames, ArrayList<String> images, ArrayList<String> textBodys) {

        mContext = context;
        mImageNames = imageNames;
        mImages = images;
        mTextBodys = textBodys;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(TAG,"onBindViewHolder: called.");

        Glide.with(mContext)
                .asBitmap()
                .load(mImages.get(position))
        .into(holder.image);

        holder.imageName.setText(mImageNames.get(position));
        holder.textBody.setText(mTextBodys.get(position));


        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG,"onClick clicked on: " + mImageNames.get(position));
                Toast.makeText(mContext, mImageNames.get(position),Toast.LENGTH_LONG).show();

                Intent intent = new Intent(mContext, GalleryActivity.class);
                intent.putExtra("image_url",mImages.get(position));
                intent.putExtra("image_name",mImageNames.get(position));
                intent.putExtra("text_body",mTextBodys.get(position));

                mContext.startActivity(intent);
            }

        });
    }

    @Override
    public int getItemCount() {
        return mImageNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

    CircleImageView image;
    TextView imageName;
    TextView textBody;
    RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            imageName = itemView.findViewById(R.id.image_name);
            textBody = itemView.findViewById(R.id.text_body);
            parentLayout = itemView.findViewById(R.id.parent_layout);

        }
    }
}
