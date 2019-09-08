package kh.com.ilost.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kh.com.ilost.R;
import kh.com.ilost.models.User;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {


    private static ChatClickListener chatClickListener;
    private Context context;
    private List<User> chatList = new ArrayList<>();


    public ChatAdapter(Context context, List<User> chats) {
        this.context = context;
        this.chatList = chats;
    }


    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.view_holder_user, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = chatList.get(position);
//        String imgUrl = chat.getPhotoUrl();
//        ImageLoader imageLoader = VolleyRequestQueue.getInstance(context).getImageLoader();
//        if (imgUrl != null) {
//            imageLoader.get(imgUrl, ImageLoader.getImageListener(holder.networkImg,
//                    R.drawable.arrow, R.drawable.arrow));
//        }
//        holder.networkImg.setImageUrl(imgUrl, imageLoader);
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


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //        NetworkImageView networkImg;
        TextView txName, txLastMsg;


        private ViewHolder(View itemView) {
            super(itemView);
//            networkImg = itemView.findViewById(R.id.user_img_circle);
            txName = itemView.findViewById(R.id.user_txt_friend_name);
            txLastMsg = itemView.findViewById(R.id.user_txt_last_message);
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
