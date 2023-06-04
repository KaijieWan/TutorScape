package com.example.tutorscape.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tutorscape.Model.TuitionCentre;
import com.example.tutorscape.R;
import com.example.tutorscape.ResultsActivity;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TCAdapter extends RecyclerView.Adapter<TCAdapter.ViewHolder>{

    private Context mContext;
    private  List<TuitionCentre> mTC;
    private boolean isFragment;
    private AdapterView.OnItemClickListener itemClickListener;

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

        String address_msg = mContext.getString(R.string.address_msg, capitalizeAfterSpace(TC.getAddress()));
        String postal_msg = mContext.getString(R.string.postal_msg, TC.getPostal());
        String contact_msg = mContext.getString(R.string.contact_msg, TC.getContactNo());
        String website_msg = mContext.getString(R.string.website_msg, TC.getWebsite());
        String rating_msg = mContext.getString(R.string.rating_msg, TC.getRating_num());

        SpannableString address_span = new SpannableString(address_msg);
        address_span.setSpan(new StyleSpan(Typeface.BOLD), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString postal_span = new SpannableString(postal_msg);
        postal_span.setSpan(new StyleSpan(Typeface.BOLD), 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString contact_span = new SpannableString(contact_msg);
        contact_span.setSpan(new StyleSpan(Typeface.BOLD), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString website_span = new SpannableString(website_msg);
        website_span.setSpan(new StyleSpan(Typeface.BOLD), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableString rating_span = new SpannableString(rating_msg);
        rating_span.setSpan(new StyleSpan(Typeface.BOLD), 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.tuitionName.setText(capitalizeAfterSpace(TC.getName()));
        holder.tuitionAddr.setText(address_span);
        holder.tuitionPostal.setText(postal_span);
        holder.tuitionContactNo.setText(contact_span);
        holder.tuitionWebsite.setText(website_span);
        holder.tuitionRatingNum.setText(rating_span);

        float rating_float = Float.parseFloat(TC.getRating_num());
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        holder.tuitionRatingBar.setRating(Float.parseFloat(decimalFormat.format(rating_float)));

        Picasso.get().load(TC.getImageUrl()).placeholder(R.mipmap.ic_launcher).into(holder.tuitionImage);


        // Bind data to the item view

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the item click event
                Intent intent = new Intent(mContext, ResultsActivity.class);
                intent.putExtra("tuitionCentreId", TC.getId());
                // Redirect to another page or view based on the selected item
                mContext.startActivity(intent);
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
            tuitionName.setPaintFlags(tuitionName.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            tuitionAddr = itemView.findViewById(R.id.tuition_address);
            tuitionPostal = itemView.findViewById(R.id.tuition_postal);
            tuitionContactNo = itemView.findViewById(R.id.tuition_contact);
            tuitionWebsite = itemView.findViewById(R.id.tuition_website);
            tuitionRatingNum = itemView.findViewById(R.id.rating_num);
            tuitionRatingBar = itemView.findViewById(R.id.rating_bar);
        }
    }

    public String capitalizeAfterSpace(String input) {
        StringBuilder output = new StringBuilder();

        boolean capitalizeNext = true;
        for (char c : input.toCharArray()) {
            if (Character.isWhitespace(c)) {
                capitalizeNext = true;
                output.append(c);
            } else {
                if (capitalizeNext) {
                    c = Character.toUpperCase(c);
                    capitalizeNext = false;
                }
                output.append(c);
            }
        }

        return output.toString();
    }

    public interface OnItemClickListener{
        void onItemClick(String tuitionCentreId);
    }
}
