package kh.com.ilost.activities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

import kh.com.ilost.R;
import kh.com.ilost.models.User;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {


    private static MessageClickListener messageClickListener;
    private Context context;
    private List<User> users = new ArrayList<>();


    public MessageAdapter(Context context, List<User> users){
        this.context = context;
        this.users = users;
    }


    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = users.get(position);
        String imgUrl = user.getPhotoUrl();
        ImageLoader imageLoader = VolleyRequestQueue.getInstance(context).getImageLoader();
        if (imgUrl != null){
            imageLoader.get(imgUrl, ImageLoader.getImageListener(holder.networkImg,
                    R.drawable.arrow, R.drawable.arrow));
        }
        holder.networkImg.setImageUrl(imgUrl, imageLoader);
        holder.txName.setText(user.getDisplayName());
    }


    @Override
    public int getItemCount() {
        return users.size();
    }


    public User getItem(int position) {
        return users.get(position);
    }


    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }


    public void setMessageClickListener(MessageClickListener messageClickListener2){
        messageClickListener = messageClickListener2;
    }


    public interface MessageClickListener {
        void onMessageClick(View view, int position);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        NetworkImageView networkImg;
        TextView txName, txLastMsg;


        private ViewHolder(View itemView) {
            super(itemView);
            networkImg = itemView.findViewById(R.id.message_img);
            txName = itemView.findViewById(R.id.message_txt_username);
            txLastMsg = itemView.findViewById(R.id.message_txt_message);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if (messageClickListener != null) {
                messageClickListener.onMessageClick(view, getAdapterPosition());
            }
        }
    }


}
