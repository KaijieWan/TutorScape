package com.example.tutorscape.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tutorscape.Model.Message;
import com.example.tutorscape.R;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    private Context mContext;
    private List<Message> messageList;

    public MessagesAdapter(Context mContext, List<Message> messageList) {
        this.mContext = mContext;
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.message_items, parent, false);
        return new MessagesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messageList.get(position);

        SpannableString title_span = new SpannableString(message.getTitle());
        title_span.setSpan(new StyleSpan(Typeface.BOLD), 0, title_span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.messageTitle.setText(title_span);

        String date_trimmed = message.getDate().substring(0,8);
        holder.messageDate.setText(date_trimmed);
        holder.messageContent.setVisibility(View.GONE);

        //Only if message has been read will the unread icon disappear
        if(message.isRead()){
            holder.unreadIcon.setVisibility(View.INVISIBLE);
        }

        //Set onClickListener for the layout to lead to the full message activity page
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView messageTitle;
        private ImageView unreadIcon;
        private TextView messageDate;
        private TextView messageContent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            messageTitle = itemView.findViewById(R.id.message_title);
            messageDate = itemView.findViewById(R.id.message_date);
            messageContent = itemView.findViewById(R.id.message_content);
            unreadIcon = itemView.findViewById(R.id.unread_message);
        }
    }
}
