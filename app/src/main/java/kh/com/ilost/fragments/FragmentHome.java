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
import android.widget.Button;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import kh.com.ilost.R;
import kh.com.ilost.activities.AddPostActivity;
import kh.com.ilost.adapters.PostAdapter;
import kh.com.ilost.models.Post;


public class FragmentHome extends Fragment implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    private ShimmerFrameLayout shimmerFrameLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DatabaseReference databaseReference;
    private PostAdapter postAdapter;
    private List<Post> posts = new ArrayList<>();


    public FragmentHome() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getActivity() != null) {
            getActivity().setTitle("Home");
        }
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnPost = view.findViewById(R.id.home_btn_post);
        RecyclerView rclProduct = view.findViewById(R.id.home_rcl_product);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        swipeRefreshLayout = view.findViewById(R.id.home_swipe_refresh_layout);
        shimmerFrameLayout = view.findViewById(R.id.home_shimmer_layout);
        postAdapter = new PostAdapter(getContext(), posts);

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        rclProduct.setLayoutManager(layoutManager);
        rclProduct.setAdapter(postAdapter);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        swipeRefreshLayout.setOnRefreshListener(this);
        btnPost.setOnClickListener(this);

        loadPostsFromDb();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.home_btn_post) {
            startActivity(new Intent(getActivity(), AddPostActivity.class));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        shimmerFrameLayout.startShimmerAnimation();
    }

    @Override
    public void onPause() {
        super.onPause();
        shimmerFrameLayout.stopShimmerAnimation();
    }

    private void loadPostsFromDb() {
        swipeRefreshLayout.setRefreshing(true);
        databaseReference.child("posts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                posts.clear();
                for (DataSnapshot dataSnapshotData : dataSnapshot.getChildren()) {
                    Post post = dataSnapshotData.getValue(Post.class);
                    posts.add(post);
                }
                postAdapter.setPosts(posts);
                swipeRefreshLayout.setRefreshing(false);
                shimmerFrameLayout.stopShimmerAnimation();
                shimmerFrameLayout.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("app", databaseError.getMessage());
                swipeRefreshLayout.setRefreshing(false);
                shimmerFrameLayout.stopShimmerAnimation();
                shimmerFrameLayout.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onRefresh() {
        loadPostsFromDb();
    }
}
