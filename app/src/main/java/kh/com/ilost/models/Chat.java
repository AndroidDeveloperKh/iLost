package kh.com.ilost.models;


import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;


@IgnoreExtraProperties
public class Chat {

    private String uid;
    private String message;
    private String sender;
    private String receiver;
    private double timestamp;
    private Boolean read;
    private Boolean active;
    private String mediaUrl;


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(double timestamp) {
        this.timestamp = timestamp;
    }


    @Exclude
    public Map<String, Object> toMap(){
        Map<String, Object> message = new HashMap<>();
        message.put("uid", uid);
        message.put("message", this.message);
        message.put("sender", sender);
        message.put("receiver", receiver);
        message.put("timestamp", timestamp);
        message.put("read", read);
        message.put("active", active);
        message.put("mediaUrl", mediaUrl);
        return message;
    }

}
