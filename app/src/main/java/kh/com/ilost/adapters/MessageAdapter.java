package kh.com.ilost.adapters;


import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import kh.com.ilost.R;
import kh.com.ilost.models.MyMessage;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private Context context;
    private List<MyMessage> myMessageHistory;
    private OnItemClickListener itemClickListener;
    private OnItemLongClickListener itemLongClickListener;


    public MessageAdapter(Context context, List<MyMessage> myMessageHistory) {
        this.context = context;
        this.myMessageHistory = myMessageHistory;
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }


    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }


    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    public void setItemLongClickListener(OnItemLongClickListener itemLongClickListener) {
        this.itemLongClickListener = itemLongClickListener;
    }


    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
        MyMessage myMessage = myMessageHistory.get(position);
        String uid = FirebaseAuth.getInstance().getUid();
        if (myMessage.getSender().equals(uid)) {
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
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        MyMessage myMessage = myMessageHistory.get(position);
        holder.txMsg.setText(myMessage.getMessage());
        holder.txTimestamp.setText(convertTimestamp(myMessage.getTimestamp()));
    }


    @Override
    public int getItemCount() {
        return myMessageHistory.size();
    }


    public void removeItem(int position) {
        myMessageHistory.remove(position);
        notifyItemRemoved(position);
    }


    public MyMessage getLastItem() {
        return myMessageHistory.get(getItemCount() - 1);
    }


    public void setMessages(List<MyMessage> myMessageHistory) {
        this.myMessageHistory = myMessageHistory;
        notifyDataSetChanged();
    }

    public void hideTimestamp(View view) {
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.txTimestamp.setVisibility(View.GONE);
    }

    public void showTimestamp(View view) {
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.txTimestamp.setVisibility(View.VISIBLE);
    }

    protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {
        TextView txMsg, txTimestamp;

        ViewHolder(View itemView) {
            super(itemView);
            txMsg = itemView.findViewById(R.id.message_txt);
            txTimestamp = itemView.findViewById(R.id.message_timestamp);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(view, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (itemLongClickListener != null) {
                itemLongClickListener.onItemLongClick(view, getAdapterPosition());
            }
            return true;
        }

    }

}
