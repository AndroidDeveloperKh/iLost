package kh.com.ilost.adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kh.com.ilost.R;
import kh.com.ilost.models.Chat;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private Context context;
    private List<Chat> chatHistory = new ArrayList<>();
    private static OnItemClickListener itemClickListener;
    private static OnItemLongClickListener itemLongClickListener;


    public MessageAdapter(Context context, List<Chat> chatHistory) {
        this.context = context;
        this.chatHistory = chatHistory;
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }


    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }


    public void setItemClickListener(OnItemClickListener itemClickListener2) {
        itemClickListener = itemClickListener2;
    }


    public void setItemLongClickListener(OnItemLongClickListener itemLongClickListener2) {
        itemLongClickListener = itemLongClickListener2;
    }


    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(context).inflate(R.layout.item_message_send_rounded,
                    parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_message_recieve_rounded,
                    parent, false);
        }
        return new MessageAdapter.ViewHolder(view);
    }


    @Override
    public int getItemViewType(int position) {
        Chat chat = chatHistory.get(position);
        String uid = FirebaseAuth.getInstance().getUid();
        if (chat.getSender().equals(uid)) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }


    private String convertTimestamp(double chatTimestamp) {
        DateFormat dateFormat = new SimpleDateFormat("EEE, h:mm a", Locale.getDefault());
        return dateFormat.format(chatTimestamp);
    }


    @Override
    public void onBindViewHolder(MessageAdapter.ViewHolder holder, int position) {
        Chat chat = chatHistory.get(position);
        holder.txMsg.setText(chat.getMessage());
        holder.txTimestamp.setText(convertTimestamp(chat.getTimestamp()));
    }


    @Override
    public int getItemCount() {
        return chatHistory.size();
    }


    public Chat getItem(int position){
        return chatHistory.get(position);
    }


    public Chat getLastItem(){
        return chatHistory.get(getItemCount()-1);
    }


    public void setMessages(List<Chat> chatHistory) {
        this.chatHistory = chatHistory;
        notifyDataSetChanged();
    }


    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {
        TextView txMsg, txTimestamp;


        private ViewHolder(View itemView) {
            super(itemView);
            txMsg = itemView.findViewById(R.id.message_txt);
            txTimestamp = itemView.findViewById(R.id.message_timestamp);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if(itemClickListener != null){
                itemClickListener.onItemClick(view, getAdapterPosition());
            }
        }


        @Override
        public boolean onLongClick(View view) {
            if(itemLongClickListener != null){
                itemLongClickListener.onItemLongClick(view, getAdapterPosition());
            }
            return true;
        }

    }

}
