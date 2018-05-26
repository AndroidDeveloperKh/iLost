package kh.com.ilost.models;

import java.io.Serializable;
import java.sql.Timestamp;

public class Post implements Serializable {

    private String uid;
    private String title;
    private String type;
    private long timestamp;
    private String date;
    private String uuid;
    private String timeStart;
    private String timeEnd;
    private String category;
    private String location;
    private String imgUrl;

    public Post() {
        // constructor
    }

    public Post(String uid, String title, String type, long timestamp, String date, String uuid,
                String timeStart, String timeEnd, String category, String location) {

        this.uid = uid;
        this.title = title;
        this.type = type;
        this.timestamp = timestamp;
        this.date = date;
        this.uuid = uuid;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.category = category;
        this.location = location;
    }

    public Post(String pid, String title, String type, long timestamp, String date, String uid,
                String timeStart, String timeEnd, String category, String location, String imgUrl) {

        this.uid = pid;
        this.title = title;
        this.type = type;
        this.uid = uid;
        this.date = date;
        this.timestamp = timestamp;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.category = category;
        this.location = location;
        this.imgUrl = imgUrl;
    }

    public String getUId() {
        return uid;
    }

    public void setUId(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUUId() {
        return uuid;
    }

    public void setUUId(String uuid) {
        this.uuid = uuid;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
