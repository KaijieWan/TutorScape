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
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
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
        holder.tuitionAddr.setText(TC.getAddress());
        holder.tuitionPostal.setText(TC.getPostal());
        holder.tuitionContactNo.setText(TC.getContactNo());
        holder.tuitionWebsite.setText(TC.getWebsite());
        holder.tuitionRatingNum.setText(TC.getRating_num());

        float rating_float = Float.parseFloat(TC.getRating_num());
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        holder.tuitionRatingBar.setRating(Float.parseFloat(decimalFormat.format(rating_float)));

        Picasso.get().load(TC.getImageUrl()).placeholder(R.mipmap.ic_launcher).into(holder.tuitionImage);


        // Bind data to the item view

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the item click event
                // You can access the clicked item's position or data to perform the necessary actions

                // Redirect to another page or view based on the selected item
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTC.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView tuitionImage;
        public TextView tuitionName;
        public TextView tuitionAddr;
        public TextView tuitionPostal;
        public TextView tuitionContactNo;
        public TextView tuitionWebsite;
        public TextView tuitionRatingNum;
        public RatingBar tuitionRatingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tuitionImage = itemView.findViewById(R.id.image_name);
            tuitionName = itemView.findViewById(R.id.tuition_name);
            tuitionAddr = itemView.findViewById(R.id.tuition_address);
            tuitionPostal = itemView.findViewById(R.id.tuition_postal);
            tuitionContactNo = itemView.findViewById(R.id.tuition_contact);
            tuitionWebsite = itemView.findViewById(R.id.tuition_website);
            tuitionRatingNum = itemView.findViewById(R.id.rating_num);
            tuitionRatingBar = itemView.findViewById(R.id.rating_bar);
        }
    }



}
