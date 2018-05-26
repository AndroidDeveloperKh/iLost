package kh.com.ilost.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import kh.com.ilost.R;
import kh.com.ilost.models.Comment;
import kh.com.ilost.models.User;

public class CommentViewHolder extends RecyclerView.ViewHolder {

    private TextView txtUserName, txtComment, txtTimestamp;
    private ImageView imgUserProfile;
    private Context context;
    private User user;

    CommentViewHolder(View itemView) {
        super(itemView);
        txtUserName = itemView.findViewById(R.id.vh_comment_txt_user_name);
        txtComment = itemView.findViewById(R.id.vh_comment_txt_comment);
        txtTimestamp = itemView.findViewById(R.id.vh_comment_txt_timestamp);
        imgUserProfile = itemView.findViewById(R.id.vh_comment_img_profile);
    }

    public void set(Context context, Comment comment) {
        this.context = context;
        if (comment != null) {
            getUser(comment);
        }
    }

    private void getUser(final Comment comment) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users/" + comment.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                setComment(comment);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("app", databaseError.getDetails());
            }
        });
    }

    private void setComment(Comment comment) {
        txtUserName.setText(user.getName());
        txtComment.setText(comment.getComment());
        txtTimestamp.setText(convertTimestamp(comment.getTimestamp()));
        if (user.getPhotoUrl() != null) {
            Glide.with(context).load(user.getPhotoUrl()).into(imgUserProfile);
        }
    }

    private String convertTimestamp(double chatTimestamp) {
        java.text.DateFormat dateFormat = new SimpleDateFormat("EEE, h:mm a", Locale.getDefault());
        return dateFormat.format(chatTimestamp);
    }

}
