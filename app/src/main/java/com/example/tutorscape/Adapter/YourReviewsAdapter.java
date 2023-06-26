package com.example.tutorscape.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tutorscape.EditReviewActivity;
import com.example.tutorscape.Fragments.ReviewFragment;
import com.example.tutorscape.Model.Review;
import com.example.tutorscape.Model.TuitionCentre;
import com.example.tutorscape.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.util.List;

public class YourReviewsAdapter extends RecyclerView.Adapter<YourReviewsAdapter.ViewHolder> {
    private Context mContext;
    private List<Review> reviewList;
    private String userId;

    public YourReviewsAdapter(Context mContext, List<Review> reviewList, String userId) {
        this.mContext = mContext;
        this.reviewList = reviewList;
        this.userId = userId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.your_reviews_items, parent, false);
        return new YourReviewsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull YourReviewsAdapter.ViewHolder holder, int position) {
        Review review = reviewList.get(position);

        DatabaseReference nameRef = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Users/" + userId + "/name");

        nameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String user_name = snapshot.getValue(String.class);
                holder.user_name.setText(user_name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //implement the rest of the set text methods here
        holder.review_date.setText(review.getReview_date());
        holder.subjects_enrolled.setText(review.getSubjects_enrolled());

        float rating_float = Float.parseFloat(review.getRating_num());
        holder.rating_bar.setRating(rating_float);

        holder.review_text.setText(review.getReview_text());

        String edit_txt = "Edited";

        Log.d("YourReviewsAdapter", String.valueOf(Boolean.valueOf(review.getEdited())));

        if(review.getEdited()){
            holder.edited_flag.setText(edit_txt);
        }
        else{
            holder.edited_flag.setVisibility(View.GONE);
        }

        DatabaseReference ref = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("TuitionCentre");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    TuitionCentre tuitionCentre = dataSnapshot.getValue(TuitionCentre.class);
                    if(tuitionCentre.getId().equals(review.getTCID())){
                        holder.tc_name.setText(capitalizeAfterSpace(tuitionCentre.getName()));
                        holder.tc_address.setText(capitalizeAfterSpace(tuitionCentre.getAddress()));
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        holder.optionsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context wrapper = new ContextThemeWrapper(mContext, R.style.custom_PopupMenu);
                PopupMenu popupMenu = new PopupMenu(wrapper, holder.optionsAnchor);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    popupMenu.setForceShowIcon(true);
                }
                popupMenu.getMenuInflater().inflate(R.menu.review_options_menu, popupMenu.getMenu());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    popupMenu.getMenu().setGroupDividerEnabled(true); // Optional: Show divider between menu items
                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemID = item.getItemId();
                        if(itemID == R.id.edit_option) {
                            Log.d("YourReviewsAdapter", "Menu listener edit option");
                            Intent intent = new Intent(mContext, EditReviewActivity.class);
                            intent.putExtra("review", review);
                            intent.putExtra("tuitionCentreId", review.getTCID());
                            mContext.startActivity(intent);
                            return true;
                        }
                        else if(itemID == R.id.delete_option) {
                            DatabaseReference ref = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Reviews");
                            ref.child(review.getId()).removeValue();
                            reviewList.remove(review);
                            notifyDataSetChanged();
                            Toast.makeText(mContext, "Review deleted!", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
        /*Context wrapper = new ContextThemeWrapper(this, R.style.custom_PopupMenu);
        PopupMenu popup = new PopupMenu(wrapper, view);*/
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView user_name;
        public TextView review_date;
        public TextView subjects_enrolled;
        public RatingBar rating_bar;
        public TextView review_text;
        public TextView tc_name;
        public TextView tc_address;
        public ImageView optionsIcon;
        public FrameLayout optionsAnchor;
        public TextView edited_flag;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            user_name = itemView.findViewById(R.id.user_name);
            review_date = itemView.findViewById(R.id.review_date);
            subjects_enrolled = itemView.findViewById(R.id.subjects_enrolled);
            rating_bar = itemView.findViewById(R.id.rating_bar);
            review_text = itemView.findViewById(R.id.user_review);
            tc_name = itemView.findViewById(R.id.tc_name);
            tc_name.setPaintFlags(tc_name.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            tc_address = itemView.findViewById(R.id.tc_address);

            optionsIcon = itemView.findViewById(R.id.review_options);
            optionsAnchor = itemView.findViewById(R.id.review_options_anchor);

            edited_flag = itemView.findViewById(R.id.editFlag);
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

}
