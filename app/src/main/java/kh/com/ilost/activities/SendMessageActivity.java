package kh.com.ilost.activities;


import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import kh.com.ilost.helpers.KeyboardHelper;
import kh.com.ilost.models.MyMessage;
import kh.com.ilost.models.User;


public class SendMessageActivity extends AppCompatActivity implements View.OnClickListener,
        TextWatcher, MessageAdapter.OnItemLongClickListener,
        MessageAdapter.OnItemClickListener {


    private DatabaseReference databaseReference;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private EditText edtMessage;
    private FloatingActionButton fabSend;

    private MessageAdapter messageAdapter;
    private List<MyMessage> myMessageHistory = new ArrayList<>();
    private User user;
    private boolean showTimestamp = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setTitle("Message");
        LinearLayout layout = findViewById(R.id.send_message_layout);
        edtMessage = findViewById(R.id.send_message_edt_message);
        fabSend = findViewById(R.id.send_message_fab_send);
        recyclerView = findViewById(R.id.send_message_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(layoutManager);

        databaseReference = FirebaseDatabase.getInstance().getReference("messages");
        user = (User) getIntent().getSerializableExtra("user");
        setTitle(user.getName());

        loadMessage();
        messageAdapter = new MessageAdapter(getApplicationContext(), myMessageHistory);
        messageAdapter.setItemClickListener(this);
        messageAdapter.setItemLongClickListener(this);
        edtMessage.addTextChangedListener(this);
        fabSend.setOnClickListener(this);
        layout.setOnClickListener(this);
        recyclerView.setOnClickListener(this);
        recyclerView.setAdapter(messageAdapter);
    }


    private void loadMessage() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            databaseReference.child(firebaseUser.getUid()).child(user.getUid())
                    .addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                            // get a new message from data snapshot and add to myMessage history
                            MyMessage myMessage = dataSnapshot.getValue(MyMessage.class);
                            myMessageHistory.add(myMessage);
                            messageAdapter.notifyDataSetChanged();
                            layoutManager.smoothScrollToPosition(recyclerView, null,
                                    messageAdapter.getItemCount() - 1);
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                            //                        MyMessage chat = dataSnapshot.getValue(MyMessage.class);
                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                            MyMessage myMessage = dataSnapshot.getValue(MyMessage.class);
                            myMessageHistory.remove(myMessage);
                            messageAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
    }


    private void sendMessage() {
        // grab text message from edit text and store in db
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String msg = edtMessage.getText().toString().trim();
            if (!msg.isEmpty()) {
                String key = databaseReference.push().getKey();
                MyMessage newMsg = new MyMessage();
                newMsg.setUid(key);
                newMsg.setMessage(msg);
                newMsg.setTimestamp(System.currentTimeMillis());
                newMsg.setReceiver(user.getUid());
                newMsg.setSender(firebaseUser.getUid());
                databaseReference.child(firebaseUser.getUid()).child(user.getUid()).child(key).setValue(newMsg);
                databaseReference.child(user.getUid()).child(firebaseUser.getUid()).child(key).setValue(newMsg);
                edtMessage.setText("");
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please Sign In or Sign Up  to Send Message",
                    Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.send_message_fab_send:
                sendMessage();
                break;
            case R.id.send_message_layout:
                KeyboardHelper.hide(getApplicationContext(), getCurrentFocus());
                Log.d("app", "onClick: ");
                break;
            case R.id.send_message_recycler_view:
                KeyboardHelper.hide(getApplicationContext(), getCurrentFocus());
                Log.d("app", "onClick: recycler ");
                break;
            default:
                break;
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
    public void onItemClick(View view, int position) {
        Log.d("app", "click message: " + position);
        // hide/show timestamp or friend name
        showTimestamp = !showTimestamp;
        if (showTimestamp) {
            messageAdapter.showTimestamp(view);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(recyclerView);
            }
        } else {
            messageAdapter.hideTimestamp(view);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                TransitionManager.beginDelayedTransition(recyclerView);
            }
        }
    }


    @Override
    public void onItemLongClick(View view, final int position) {
        // show popup dialog to edit or delete a selected message
        final String[] options = {"Forward", "Delete"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Action");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (options[i].equals("Delete")) {
                    confirmDelete(position);
                }
            }
        });
        builder.show();
    }


    private void confirmDelete(final int position) {
        // create a dialog to popup confirm deletion
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Attention !!!");
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
        Log.d("app", "deleted message: " + position);
        // remove a message from chat history
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String chatId = myMessageHistory.get(position).getUid();
            databaseReference.child(firebaseUser.getUid()).child(user.getUid())
                    .child(chatId).removeValue();
            databaseReference.child(user.getUid()).child(firebaseUser.getUid())
                    .child(chatId).removeValue();
            messageAdapter.removeItem(position);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
