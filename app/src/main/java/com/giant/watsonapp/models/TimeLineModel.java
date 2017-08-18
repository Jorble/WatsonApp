package com.giant.watsonapp.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by HP-HP on 05-12-2015.
 */
public class TimeLineModel implements Parcelable {

    private String id;
    private String title;
    private String message;
    private String img;
    private String url;
    private OrderStatus status;
    private boolean isPlaying;

    public TimeLineModel() {
    }

    public TimeLineModel(String id,String message, String title,String img,String url,boolean isPlaying, OrderStatus status) {
        this.message = message;
        this.title = title;
        this.status = status;
        this.img = img;
        this.id = id;
        this.isPlaying=isPlaying;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String date) {
        this.title = date;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.message);
        dest.writeString(this.title);
        dest.writeString(this.img);
        dest.writeString(this.url);
        dest.writeByte((byte) (isPlaying ? 1 : 0));
        dest.writeInt(this.status == null ? -1 : this.status.ordinal());
    }

    protected TimeLineModel(Parcel in) {
        this.id = in.readString();
        this.message = in.readString();
        this.title = in.readString();
        this.img = in.readString();
        this.url = in.readString();
        this.isPlaying = in.readByte() != 0;     //myBoolean == true if byte != 0
        int tmpMStatus = in.readInt();
        this.status = tmpMStatus == -1 ? null : OrderStatus.values()[tmpMStatus];
    }

    public static final Creator<TimeLineModel> CREATOR = new Creator<TimeLineModel>() {
        @Override
        public TimeLineModel createFromParcel(Parcel source) {
            return new TimeLineModel(source);
        }

        @Override
        public TimeLineModel[] newArray(int size) {
            return new TimeLineModel[size];
        }
    };
}
