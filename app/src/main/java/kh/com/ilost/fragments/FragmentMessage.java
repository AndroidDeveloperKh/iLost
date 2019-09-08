package kh.com.ilost.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import kh.com.ilost.R;
import kh.com.ilost.activities.ChatActivity;
import kh.com.ilost.adapters.ChatAdapter;
import kh.com.ilost.models.User;


public class FragmentMessage extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener, ChatAdapter.ChatClickListener {


    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;

    ChatAdapter chatAdapter;
    List<User> chatList = new ArrayList<>();


    public FragmentMessage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_message, container, false);

        swipeRefreshLayout = root.findViewById(R.id.message_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView = root.findViewById(R.id.message_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);

        loadChatList();
        chatAdapter = new ChatAdapter(getContext(), chatList);
        chatAdapter.setChatClickListener(this);
        recyclerView.setAdapter(chatAdapter);

        return root;
    }


    private void loadChatList() {
        swipeRefreshLayout.setRefreshing(true);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatList.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    User listUser = child.getValue(User.class);
                    chatList.add(listUser);
                    Log.d("app", child.toString());
                }
                chatAdapter.setChatList(chatList);
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        swipeRefreshLayout.setRefreshing(false);
    }


    @Override
    public void onRefresh() {
        loadChatList();
    }


    @Override
    public void onChatClick(View view, int position) {
        User user = chatList.get(position);
        startActivity(new Intent(getContext(), ChatActivity.class)
                .putExtra("uidFriend", user.getUid()));
    }

}
