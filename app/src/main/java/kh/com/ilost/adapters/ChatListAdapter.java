package kh.com.ilost.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import kh.com.ilost.R;
import kh.com.ilost.helpers.VolleyRequestQueue;
import kh.com.ilost.models.User;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    private static ChatClickListener chatClickListener;
    private Context context;
    private List<User> chatList;

    public ChatListAdapter(Context context, List<User> chats) {
        this.context = context;
        this.chatList = chats;
    }

    @Override
    public ChatListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.view_holder_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = chatList.get(position);
        String imageUrl = user.getPhotoUrl();
        if (imageUrl != null) {
            ImageLoader imageLoader = VolleyRequestQueue.getInstance(context).getImageLoader();
            holder.networkImg.setImageUrl(imageUrl, imageLoader);
        }
        holder.txName.setText(user.getName());
        holder.txLastMsg.setText(user.getEmail());
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public void setChatList(List<User> chats) {
        this.chatList = chats;
        notifyDataSetChanged();
    }

    public void setChatClickListener(ChatClickListener messageClickListener2) {
        chatClickListener = messageClickListener2;
    }

    public interface ChatClickListener {
        void onChatClick(View view, int position);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        NetworkImageView networkImg;
        TextView txName, txLastMsg;

        private ViewHolder(View itemView) {
            super(itemView);
            networkImg = itemView.findViewById(R.id.vh_user_img);
            txName = itemView.findViewById(R.id.vh_user_txt_friend_name);
            txLastMsg = itemView.findViewById(R.id.vh_user_txt_last_message);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (chatClickListener != null) {
                chatClickListener.onChatClick(view, getAdapterPosition());
            }
        }
    }

}
