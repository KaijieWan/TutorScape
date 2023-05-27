package com.example.tutorscape.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UpdatesAdapter {
    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageUpdate;
        public TextView textName;
        public TextView textUpdate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
