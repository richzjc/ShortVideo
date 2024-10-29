package com.tbruyelle.rxpermissions3;

import static com.tbruyelle.rxpermissions3.PermissionDescDialog.FLAG_CANCEL_DISMISS;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.fragment.app.FragmentActivity;

import com.kronos.router.RouterThenCallback;

public class RequestPermissionEntity implements RouterThenCallback, Parcelable {

    public FragmentActivity activity;
    public String permissionDesc;
    public String[] permissions;
    public PermissionDescDialog dialog;

    public RequestPermissionEntity(FragmentActivity activity, PermissionDescDialog dialog, String permissionDesc, String[] permissions) {
        this.activity = activity;
        this.permissionDesc = permissionDesc;
        this.permissions = permissions;
        this.dialog = dialog;
    }

    protected RequestPermissionEntity(Parcel in) {
        permissionDesc = in.readString();
        permissions = in.createStringArray();
    }

    public static final Creator<RequestPermissionEntity> CREATOR = new Creator<RequestPermissionEntity>() {
        @Override
        public RequestPermissionEntity createFromParcel(Parcel in) {
            return new RequestPermissionEntity(in);
        }

        @Override
        public RequestPermissionEntity[] newArray(int size) {
            return new RequestPermissionEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(permissionDesc);
        dest.writeStringArray(permissions);
    }

    @Override
    public void routerThenCallback() {
        SharedPreferences sp =  activity.getSharedPreferences("shared_preference_config", Context.MODE_PRIVATE);
        boolean isNotAgree = sp.getBoolean("userPrivacy", true);
        if(isNotAgree){
            dialog.flag = FLAG_CANCEL_DISMISS;
            if (dialog.emmiter != null) {
                dialog.emmiter.onNext(false);
                dialog.emmiter.onComplete();
            }
        }else{
            dialog.show(activity.getSupportFragmentManager(), "descDialog");
        }
    }
}
