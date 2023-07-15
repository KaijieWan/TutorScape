package com.example.tutorscape.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tutorscape.Model.Updates;
import com.example.tutorscape.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UpdatesAdapter extends RecyclerView.Adapter<UpdatesAdapter.ViewHolder>{
    private Context mContext;
    private List<Updates> mUpdates;

    public UpdatesAdapter(Context mContext, List<Updates> mUpdates) {
        this.mContext = mContext;
        this.mUpdates = mUpdates;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.updates_item, parent, false);
        return new UpdatesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Updates update = mUpdates.get(position);
        Picasso.get().load(update.getImageUrl()).into(holder.imageUpdate);

        SpannableString title_span = new SpannableString(update.getUpdateName());
        title_span.setSpan(new StyleSpan(Typeface.BOLD), 0, title_span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.textTitle.setText(title_span);

        holder.textUpdate.setText(update.getUpdateText());

        String date_trimmed = update.getDate().substring(0,8);
        holder.updateDate.setText(date_trimmed);
    }

    @Override
    public int getItemCount() {
        return mUpdates.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageUpdate;
        public TextView textTitle;
        public TextView textUpdate;
        public ImageView updaterFilter;
        public TextView updateDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageUpdate = itemView.findViewById(R.id.update_image);

            textTitle = itemView.findViewById(R.id.update_title);
            textTitle.setPaintFlags(textTitle.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            textUpdate = itemView.findViewById(R.id.update_info);
            updaterFilter = itemView.findViewById(R.id.filter_icon);
            updateDate = itemView.findViewById(R.id.update_date);
        }
    }
}
