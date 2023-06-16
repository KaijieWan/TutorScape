package com.example.tutorscape.Adapter;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tutorscape.DrawerItem;

import java.util.List;
import java.util.Map;

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.ViewHolder> {
    private List<DrawerItem> items;
    private Map<Class<? extends DrawerItem>, Integer> viewTypes;
    private SparseArray<DrawerItem> holderFactories;

    private AdapterView.OnItemSelectedListener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static abstract class ViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{

        private DrawerAdapter drawerAdapter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
