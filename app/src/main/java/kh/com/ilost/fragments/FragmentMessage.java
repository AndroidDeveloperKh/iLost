package kh.com.ilost.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import kh.com.ilost.R;
import kh.com.ilost.activities.MessageAdapter;
import kh.com.ilost.models.User;


public class FragmentMessage extends Fragment implements MessageAdapter.MessageClickListener {


    RecyclerView recyclerView;

    MessageAdapter messageAdapter;
    List<User> users = new ArrayList<>();


    public FragmentMessage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_message, container, false);

        recyclerView = root.findViewById(R.id.message_recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);

        messageAdapter = new MessageAdapter(getContext(), users);
        messageAdapter.setMessageClickListener(this);
        recyclerView.setAdapter(messageAdapter);

        return root;
    }


    @Override
    public void onMessageClick(View view, int position) {
        User user = users.get(position);
        Log.d("app", user.getEmail());
    }

}
