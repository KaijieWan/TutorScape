package com.example.tutorscape.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tutorscape.Model.TuitionCentre;
import com.example.tutorscape.R;

import java.util.List;

public class TCAdapter extends RecyclerView.Adapter<TCAdapter.ViewHolder>{

    private Context mContext;
    private  List<TuitionCentre> mTC;
    private boolean isFragment;

    public TCAdapter(Context mContext, List<TuitionCentre> mTC, boolean isFragment) {
        this.mContext = mContext;
        this.mTC = mTC;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.tuition_centre_item, parent, false);
        return new TCAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TuitionCentre TC = mTC.get(position);
        holder.tuitionName.setText(TC.getName());
        //
    }

    @Override
    public int getItemCount() {
        return mTC.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageName;
        public TextView tuitionName;
        public TextView tuitionAddr;
        public TextView tuitionPostal;
        public TextView tuitionContactNo;
        public TextView tuitionWebsite;
        public TextView tuitionRatingNum;
        public RatingBar tuitionRatingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageName = itemView.findViewById(R.id.image_name);
            tuitionName = itemView.findViewById(R.id.tuition_name);
            //Continue assigning the view ids to the attributes
        }
    }

    ;

}
