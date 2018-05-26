package kh.com.ilost.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import kh.com.ilost.activities.SendMessageActivity;
import kh.com.ilost.adapters.ChatListAdapter;
import kh.com.ilost.models.User;


public class FragmentMessage extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener, ChatListAdapter.ChatClickListener {


    private SwipeRefreshLayout swipeRefreshLayout;

    private ChatListAdapter chatAdapter;
    private List<User> listUser = new ArrayList<>();


    public FragmentMessage() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getActivity() != null) {
            getActivity().setTitle("Messages");
        }
        return inflater.inflate(R.layout.fragment_message, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout = view.findViewById(R.id.message_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        RecyclerView recyclerView = view.findViewById(R.id.message_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);

        loadListUser();
        chatAdapter = new ChatListAdapter(getContext(), listUser);
        chatAdapter.setChatClickListener(this);
        recyclerView.setAdapter(chatAdapter);
    }

    private void loadListUser() {
        // load list of history chatting
        swipeRefreshLayout.setRefreshing(true);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listUser.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    User user = child.getValue(User.class);
                    listUser.add(user);
                    Log.d("app", child.toString());
                }
                chatAdapter.setChatList(listUser);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("app", databaseError.getDetails());
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }


    @Override
    public void onRefresh() {
        loadListUser();
    }


    @Override
    public void onChatClick(View view, int position) {
        User user = listUser.get(position);
        startActivity(new Intent(getContext(), SendMessageActivity.class)
                .putExtra("user", user));
    }

} // end
