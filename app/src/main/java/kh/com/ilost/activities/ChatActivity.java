package kh.com.ilost.activities;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import kh.com.ilost.R;
import kh.com.ilost.adapters.MessageAdapter;
import kh.com.ilost.models.Chat;


public class ChatActivity extends AppCompatActivity implements View.OnClickListener,
        TextWatcher, RecyclerView.RecyclerListener, MessageAdapter.OnItemLongClickListener,
        MessageAdapter.OnItemClickListener {


    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user_chat");
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    MessageAdapter messageAdapter;
    List<Chat> chatHistory = new ArrayList<>();
    String uidFriend;
//    String nameFriend;
    String myUid;

    LinearLayoutManager layoutManager;
    RecyclerView recyclerView;
    EditText edtMessage;
    FloatingActionButton fabSend;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

//        nameFriend = getIntent().getStringExtra("nameFriend");
        uidFriend = getIntent().getStringExtra("uidFriend");
        myUid = firebaseUser.getUid();
        //noinspection ConstantConditions
        getSupportActionBar().setTitle(uidFriend);

        edtMessage = findViewById(R.id.chat_edt_message);
        edtMessage.addTextChangedListener(this);
        fabSend = findViewById(R.id.chat_fab_send);
        fabSend.setOnClickListener(this);

        recyclerView = findViewById(R.id.chat_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setRecyclerListener(this);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(layoutManager);

        loadMessage();
        messageAdapter = new MessageAdapter(getApplicationContext(), chatHistory);
        messageAdapter.setItemClickListener(this);
        messageAdapter.setItemLongClickListener(this);
        recyclerView.setAdapter(messageAdapter);
    }


    private void loadMessage() {
        databaseReference.child(myUid).child(uidFriend)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        // get a new message from data snapshot and add to chat history
                        Chat chat = dataSnapshot.getValue(Chat.class);
                        chatHistory.add(chat);
                        messageAdapter.notifyDataSetChanged();
                        layoutManager.smoothScrollToPosition(recyclerView, null,
                                messageAdapter.getItemCount() - 1);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                        Chat chat = dataSnapshot.getValue(Chat.class);
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        Chat chat = dataSnapshot.getValue(Chat.class);
                        chatHistory.remove(chat);
                        messageAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    private void sendMessage() {
        // grab text message from edit text and store in db
        String msg = edtMessage.getText().toString().trim();
        String key = databaseReference.push().getKey();
        if (!msg.isEmpty()) {
            Chat newMsg = new Chat();
            newMsg.setUid(key);
            newMsg.setMessage(msg);
            newMsg.setTimestamp(System.currentTimeMillis());
            newMsg.setReceiver(uidFriend);
            newMsg.setSender(myUid);
            databaseReference.child(myUid).child(uidFriend).child(key).setValue(newMsg);
            databaseReference.child(uidFriend).child(myUid).child(key).setValue(newMsg);
            edtMessage.setText("");
        }
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.chat_fab_send) {
            sendMessage();
        }
    }


    private void disableSendButton() {
        // disable send button if message is empty
        String msg = edtMessage.getText().toString().trim();
        if (msg.isEmpty()) {
            fabSend.setEnabled(false);
            fabSend.setAlpha(0.4f);
        } else {
            fabSend.setEnabled(true);
            fabSend.setAlpha(1f);
        }

        // if edit text focused scroll to last message
        if (edtMessage.isFocused()) {
            layoutManager.smoothScrollToPosition(recyclerView,
                    null, messageAdapter.getItemCount() - 1);
        }
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        disableSendButton();
    }


    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        disableSendButton();
    }


    @Override
    public void afterTextChanged(Editable editable) {
        disableSendButton();
    }


    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        Log.d("app", "onViewRecycled: " + holder.getAdapterPosition());
    }


    @Override
    public void onItemClick(View view, int position) {
        // hide/show timestamp or friend name
        recyclerView.findViewHolderForAdapterPosition(position).itemView.setAlpha(0.5f);
        Log.d("app", "onItemClick: " + position);
    }


    @Override
    public void onItemLongClick(View view, final int position) {
        // show popup dialog to edit or delete a selected message
        final String[] options = {"Edit", "Delete"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Action");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (options[i].equals("Delete")) {
                    confirmDelete(position);
                } else {
                    editMessage(position);
                }
            }
        }).show();
        Log.d("app", "onItemLongClick: " + position);
    }


    private void editMessage(int position) {
        final Chat chat = messageAdapter.getItem(position);
//        final Dialog dialog = new Dialog(this);
//        dialog.setContentView(R.layout.dialog_edit_message);
//        dialog.setTitle(R.string.edit_message);
//        final EditText editText = dialog.findViewById(R.id.dialog_edt_message);
//        editText.setText(chat.getMessage());
//        Button btnCancel = dialog.findViewById(R.id.dialog_btn_cancel);
//        final Button btnDone = dialog.findViewById(R.id.dialog_btn_done);
//
//        btnDone.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // update a message in database
//                String updatedMsg = editText.getText().toString().trim();
//                if (!updatedMsg.isEmpty()) {
//                    chat.setMessage(updatedMsg);
//                    databaseReference.child(uid).child(uidFriend)
//                            .child(chat.getUid()).setValue(chat);
//                    databaseReference.child(uidFriend).child(uid)
//                            .child(chat.getUid()).setValue(chat);
//                    messageAdapter.notifyDataSetChanged();
//                }
//                dialog.dismiss();
//            }
//        });
//
//        // dismiss dialog
//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//            }
//        });
//
//        // listener when user tying in edit text
//        editText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                disableDoneButton();
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                disableDoneButton();
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                disableDoneButton();
//            }
//
//            // disable button done if message is empty
//            private void disableDoneButton() {
//                if (editText.getText().toString().trim().isEmpty()) {
//                    btnDone.setEnabled(false);
//                    btnDone.setAlpha(0.4f);
//                } else {
//                    btnDone.setEnabled(true);
//                    btnDone.setAlpha(1);
//                }
//            }
//        });
//        dialog.show();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(getLayoutInflater().inflate(R.layout.dialog_edit_message, null));
        builder.setMessage(chat.getMessage());
        builder.show();
    }


    private void confirmDelete(final int position) {
        // create a dialog to popup confirm deletion
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirm_delete);

        // dialog positive button click
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                removeItem(position);
            }
        });

        // dialog negative button click
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        // show dialog
        builder.show();
    }


    private void removeItem(int position) {
        // remove a message from chat history
        String chatId = chatHistory.get(position).getUid();
        databaseReference.child(myUid).child(uidFriend)
                .child(chatId).removeValue();
        databaseReference.child(uidFriend).child(myUid)
                .child(chatId).removeValue();
        Log.d("app", "deleted message: " + chatId);
    }

}
