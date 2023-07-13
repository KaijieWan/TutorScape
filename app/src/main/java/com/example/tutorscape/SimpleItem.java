package com.example.tutorscape;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.tutorscape.Adapter.DrawerAdapter;
import com.example.tutorscape.Model.Favourite;
import com.example.tutorscape.Model.Notification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SimpleItem extends DrawerItem<SimpleItem.ViewHolder>{
    private int selectedItemIconTint;
    private int selectedItemTextTint;

    private int normalItemIconTint;
    private int normalItemTextTint;
    private Drawable icon;
    private String title;
    private int position;
    private int favCount = 0;
    private FirebaseAuth firebaseAuth;
    private boolean isFavCount;
    public SimpleItem(Drawable icon, String title, int position) {
        this.icon = icon;
        this.title = title;
        this.position = position;
    }

    @Override
    public ViewHolder createViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_option, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void bindViewHolder(ViewHolder holder) {
        holder.title.setText(title);
        holder.icon.setImageDrawable(icon);
        holder.numBanner.setVisibility(View.INVISIBLE);

        if(isFavCount){
            holder.numBanner.setText(String.format(String.valueOf(favCount)));
            holder.numBanner.setVisibility(View.VISIBLE);
        }

        holder.title.setTextColor(isChecked ? selectedItemTextTint : normalItemTextTint);
        holder.icon.setColorFilter(isChecked ? selectedItemIconTint : normalItemIconTint);
    }

    public void setFavCount(int count){
        favCount = count;
    }

    public SimpleItem withSelectedIconTint(int selectedItemIconTint){
        this.selectedItemIconTint = selectedItemIconTint;
        return this;
    }

    public SimpleItem withSelectedTextTint(int selectedItemTextTint){
        this.selectedItemTextTint = selectedItemTextTint;
        return this;
    }

    public SimpleItem withIconTint(int normalItemIconTint){
        this.normalItemIconTint = normalItemIconTint;
        return this;
    }

    public SimpleItem withTextTint(int normalItemTextTint){
        this.normalItemTextTint = normalItemTextTint;
        return this;
    }

    public void setNumBannerVisible(boolean set){
        isFavCount = set;
    }

    static class ViewHolder extends DrawerAdapter.ViewHolder{
        private ImageView icon;
        private TextView title;
        private TextView numBanner;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            title = itemView.findViewById(R.id.title);
            numBanner = itemView.findViewById(R.id.number_banner);
        }
    }
}
