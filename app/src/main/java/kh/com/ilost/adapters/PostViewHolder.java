package kh.com.ilost.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Locale;

import kh.com.ilost.R;
import kh.com.ilost.activities.PostDetailActivity;
import kh.com.ilost.models.Post;
import kh.com.ilost.models.User;

public class PostViewHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener {

    private TextView txtUsername, txtTimestamp, txtPostTitle;
    private ImageView imgUserProfile, imgPostImg;
    private Context context;

    private Post post;
    private User user;

    PostViewHolder(View itemView) {
        super(itemView);

        txtUsername = itemView.findViewById(R.id.vh_post_txt_user_name);
        txtTimestamp = itemView.findViewById(R.id.vh_post_txt_timestamp);
        txtPostTitle = itemView.findViewById(R.id.vh_post_txt_post_title);
        imgUserProfile = itemView.findViewById(R.id.vh_post_img_user_profile);
        imgPostImg = itemView.findViewById(R.id.vh_post_img_post_image);
        Button btnComment = itemView.findViewById(R.id.vh_post_btn_comment);
        Button btnSave = itemView.findViewById(R.id.vh_post_btn_save);
        CardView cardDetail = itemView.findViewById(R.id.vh_post_card);

        btnComment.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        cardDetail.setOnClickListener(this);

    }

    public void set(Context context, Post post) {
        this.context = context;
        this.post = post;
        if (post != null) {
            getUser(post.getUUId());
        }
    }

    private String getDate(long timestamp) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(timestamp);
        return DateFormat.format("dd-MMM-yyyy", calendar).toString();
    }

    private void getUser(String uuid){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users/" + uuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                setPostDetail();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("app", databaseError.getDetails());
            }
        });
    }

    private void setPostDetail(){
        txtUsername.setText(user.getName());
        txtPostTitle.setText(post.getTitle());
        txtTimestamp.setText(getDate(post.getTimestamp()));
        Glide.with(context).load(user.getPhotoUrl()).into(imgUserProfile);
        Glide.with(context).load(post.getImgUrl()).into(imgPostImg);
        if (post.getImgUrl() != null) {
            imgPostImg.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.vh_post_btn_comment) {
            // create a pop up dialog for comment
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("sdfjlk");
            builder.show();
        } else if (id == R.id.vh_post_card) {
            context.startActivity(new Intent(context, PostDetailActivity.class).
                    putExtra("post", post));
        }
    }
}
