package com.richzjc.shortvideo.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by micker on 16/6/21.
 */
public class MainTabItemEntity implements Parcelable {

    public int resourceId;
    public String title;
    public Class fragmentClass;
    public String key;

    public MainTabItemEntity(int resourceId, String title, Class fragmentClass, String key) {
        this.resourceId = resourceId;
        this.title = title;
        this.fragmentClass = fragmentClass;
        this.key = key;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.resourceId);
        dest.writeString(this.title);
        dest.writeString(this.key);
        dest.writeSerializable(this.fragmentClass);
    }

    protected MainTabItemEntity(Parcel in) {
        this.resourceId = in.readInt();
        this.title = in.readString();
        this.key = in.readString();
        this.fragmentClass = (Class) in.readSerializable();
    }

    public static final Creator<MainTabItemEntity> CREATOR = new Creator<MainTabItemEntity>() {
        @Override
        public MainTabItemEntity createFromParcel(Parcel source) {
            return new MainTabItemEntity(source);
        }

        @Override
        public MainTabItemEntity[] newArray(int size) {
            return new MainTabItemEntity[size];
        }
    };
}
