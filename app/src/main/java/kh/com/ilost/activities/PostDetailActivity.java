package kh.com.ilost.activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import kh.com.ilost.R;
import kh.com.ilost.adapters.CommentAdapter;
import kh.com.ilost.models.Comment;
import kh.com.ilost.models.Post;
import kh.com.ilost.models.User;

public class PostDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference databaseReference;
    private EditText edtComment;
    private ImageView imgSendComment, imgPost, imgUserProfile;
    private TextView txtUsername, txtTimestamp, txtPostTitle;

    private Post post;
    private CommentAdapter commentAdapter;
    private List<Comment> comments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView recyclerView = findViewById(R.id.post_detail_rcl_comment);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        edtComment = findViewById(R.id.post_detail_edt_comment);
        imgUserProfile = findViewById(R.id.post_detail_img_profile);
        imgSendComment = findViewById(R.id.post_detail_img_send_comment);
        imgPost = findViewById(R.id.post_detail_img_post_image);
        txtUsername = findViewById(R.id.post_detail_txt_user_name);
        txtTimestamp = findViewById(R.id.post_detail_txt_post_timestamp);
        txtPostTitle = findViewById(R.id.post_detail_txt_post_title);

        Collections.reverse(comments);
        commentAdapter = new CommentAdapter(getApplicationContext(), comments);
        recyclerView.setAdapter(commentAdapter);

        recyclerView.setLayoutManager(layoutManager);
        imgSendComment.setOnClickListener(this);
        imgPost.setOnClickListener(this);

        post = (Post) getIntent().getSerializableExtra("post");
        databaseReference = FirebaseDatabase.getInstance().getReference();

        readCommentByPostId(post.getUId());
        getUserById(post.getUUId());
    }

    private void setPostDetail(User user) {
        txtUsername.setText(user.getName());
        txtTimestamp.setText(getDate(post.getTimestamp()));
        txtPostTitle.setText(post.getTitle());
        if (user.getPhotoUrl() != null) {
            Glide.with(getApplicationContext()).load(user.getPhotoUrl()).into(imgUserProfile);
        }
        if (post.getImgUrl() != null) {
            Glide.with(getApplicationContext()).load(post.getImgUrl()).into(imgPost);
        } else {
            imgPost.setVisibility(View.GONE);
        }
    }

    private void getUserById(String uuid) {
        databaseReference.child("users/" + uuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    setPostDetail(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("app", databaseError.getDetails());
            }
        });
    }

    private String getDate(long time) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        return DateFormat.format("dd-MMM-yyyy", calendar).toString();
    }

    private void readCommentByPostId(String postId) {
        databaseReference.child("posts/" + postId + "/comments").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                Comment comment = dataSnapshot.getValue(Comment.class);
                comments.add(comment);
                commentAdapter.notifyItemInserted(comments.size());
                Log.d("app", "onChildAdded: " + comments.size());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.post_detail_img_send_comment) {
            addNewComment(post.getUId());
        }
    }

    private void addNewComment(String postId) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            Comment comment = new Comment();
            comment.setCommentId(databaseReference.push().getKey());
            comment.setComment(edtComment.getText().toString());
            comment.setUserId(firebaseUser.getUid());
            comment.setPostId(post.getUId());
            comment.setTimestamp(System.currentTimeMillis());
            databaseReference.child("posts/" + postId + "/comments").child(comment.getCommentId())
                    .setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    imgSendComment.setEnabled(false);
                    imgSendComment.setAlpha(0.4f);
                    edtComment.setText("");
                    edtComment.clearFocus();
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
