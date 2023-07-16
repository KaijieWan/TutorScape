package com.example.tutorscape.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.example.tutorscape.Adapter.MessagesAdapter;
import com.example.tutorscape.Adapter.UpdatesAdapter;
import com.example.tutorscape.Model.Message;
import com.example.tutorscape.Model.Updates;
import com.example.tutorscape.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class NotifFragment extends Fragment {
    private RecyclerView recyclerViewMessages;
    private MessagesAdapter messagesAdapter;
    private List<Message> messagesList;
    private ImageView filterIcon;
    private FrameLayout filterAnchor;
    private FirebaseAuth firebaseAuth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notif, container, false);

        recyclerViewMessages = view.findViewById(R.id.recycler_view_inbox);
        recyclerViewMessages.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerViewMessages.setLayoutManager(linearLayoutManager);
        messagesList = new ArrayList<>();
        messagesAdapter = new MessagesAdapter(getContext(), messagesList);
        recyclerViewMessages.setAdapter(messagesAdapter);

        filterIcon = view.findViewById(R.id.filter_icon);
        filterAnchor = view.findViewById(R.id.filter_anchor);

        readMessages();

        filterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(requireContext(), filterAnchor);
                popupMenu.getMenuInflater().inflate(R.menu.update_filter_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int itemID = item.getItemId();
                        if(itemID == R.id.option_newest_to_oldest) {
                            applyDateSorting(true);
                            return true;
                        }
                        else if(itemID == R.id.option_oldest_to_newest) {
                            applyDateSorting(false);
                            return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        return view;
    }

    private void readMessages() {
        firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance("https://tutorscape-509ea-default-rtdb.asia-southeast1.firebasedatabase.app").getReference()
                .child("Messages/" + userId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("NotifFragment - readMessages", "onDataChange called");
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Message message = snapshot.getValue(Message.class);
                    messagesList.add(message);
                }

                messagesAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("NotifFragment - readMessages", "Database error: " + error.getMessage());
            }
        });
    }

    private void applyDateSorting(boolean ascending) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy HH:mm", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Singapore"));
        Collections.sort(messagesList, new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                Date date1 = null;
                Date date2 = null;
                try {
                    date1 = dateFormat.parse(o1.getDate());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                try {
                    date2 = dateFormat.parse(o2.getDate());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                if (ascending) {
                    return date1.compareTo(date2);
                } else {
                    return date2.compareTo(date1);
                }
            }
        });
        // Notify the adapter that the data has changed
        // Assuming you have an adapter for the RecyclerView
        messagesAdapter.notifyDataSetChanged();
    }
}