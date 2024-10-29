package com.tbruyelle.rxpermissions3;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.kronos.router.Router;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Predicate;


public class RxPermissionsNew {
    public static Observable<Boolean> requestPermissions(final FragmentActivity activity, String permissionDesc, String... permissions) {
        ArrayList<String> list = checkReject(activity, permissions);
        if (list != null && list.size() > 0) {
            PermissionSettiingDialog dialog = new PermissionSettiingDialog();
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("permissions", list);
            bundle.putString("desc", permissionDesc);
            dialog.setArguments(bundle);
            dialog.show(activity.getSupportFragmentManager(), "settingDialog");
            return Observable.just(false);
        }
        List<String> realPermissions = checkAllGranted(activity, permissions);
        if (!TextUtils.isEmpty(permissionDesc)) {
            if (realPermissions != null && realPermissions.size() > 0) {
                RxPermissions rxPermissions = new RxPermissions(activity);
                PermissionDescDialog dialog = new PermissionDescDialog();
                Bundle bundle = new Bundle();
                bundle.putString("desc", permissionDesc);
                dialog.setArguments(bundle);
                String[] permissionArr = new String[realPermissions.size()];
                for (int i = 0; i < realPermissions.size(); i++) {
                    permissionArr[i] = realPermissions.get(i);
                }
                Observable<Boolean> observable = Observable.create(dialog)
                        .filter(aBoolean -> aBoolean)
                        .compose(rxPermissions.ensure(permissionArr));

                Bundle bundle1 = new Bundle();
                RequestPermissionEntity entity1 = new RequestPermissionEntity(activity, dialog, permissionDesc, permissions);
                bundle1.putParcelable("then", entity1);
                bundle1.putBoolean("needCancel", true);
                Router.sharedRouter().open("wscn://wallstreetcn.com/show/user/privacy/dialog", bundle1, activity);
                return observable;
            } else {
                return Observable.just(true);
            }
        } else {
            if (realPermissions != null && realPermissions.size() > 0) {
                RxPermissions rxPermissions = new RxPermissions(activity);
                String[] permissionArr = new String[realPermissions.size()];
                for (int i = 0; i < realPermissions.size(); i++) {
                    permissionArr[i] = realPermissions.get(i);
                }
                return rxPermissions.request(permissionArr);
            } else {
                return Observable.just(true);
            }
        }
    }


    private static ArrayList<String> checkReject(Activity context, String... permissions) {
        ArrayList<String> list = new ArrayList<>();
        if (permissions != null) {
            for (String permission : permissions) {
                boolean granted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
                boolean nationale = ActivityCompat.shouldShowRequestPermissionRationale(context, permission);
                if (!granted && !nationale && SpUtil.getCount(context, permission) > 0) {
                    list.add(permission);
                }
            }
        }
        return list;
    }


    public static Observable<Boolean> requestPermissions(@NonNull final Fragment fragment, String permissionDesc, String... permissions) {
        ArrayList<String> list = checkReject(fragment.getActivity(), permissions);
        if (list != null && list.size() > 0) {
            PermissionSettiingDialog dialog = new PermissionSettiingDialog();
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("permissions", list);
            bundle.putString("desc", permissionDesc);
            dialog.setArguments(bundle);
            dialog.show(fragment.getActivity().getSupportFragmentManager(), "settingDialog");
            return Observable.just(false);
        }

        List<String> realPermissions = checkAllGranted(fragment.getActivity(), permissions);
        if (!TextUtils.isEmpty(permissionDesc)) {
            if (realPermissions != null && realPermissions.size() > 0) {
                RxPermissions rxPermissions = new RxPermissions(fragment.getActivity());
                PermissionDescDialog dialog = new PermissionDescDialog();
                Bundle bundle = new Bundle();
                bundle.putString("desc", permissionDesc);
                dialog.setArguments(bundle);
                String[] permissionArr = new String[realPermissions.size()];
                for (int i = 0; i < realPermissions.size(); i++) {
                    permissionArr[i] = realPermissions.get(i);
                }
                Observable<Boolean> observable = Observable.create(dialog)
                        .filter(new Predicate<Boolean>() {
                            @Override
                            public boolean test(Boolean aBoolean) throws Exception {
                                return aBoolean;
                            }
                        })
//                        .filter(aBoolean -> aBoolean)
                        .compose(rxPermissions.ensure(permissionArr));
                dialog.show(fragment.getActivity().getSupportFragmentManager(), "descDialog");
                return observable;
            } else {
                return Observable.just(true);
            }
        } else {
            if (realPermissions != null && realPermissions.size() > 0) {
                RxPermissions rxPermissions = new RxPermissions(fragment.getActivity());
                String[] permissionArr = new String[realPermissions.size()];
                for (int i = 0; i < realPermissions.size(); i++) {
                    permissionArr[i] = realPermissions.get(i);
                }
                return rxPermissions.request(permissionArr);
            } else {
                return Observable.just(true);
            }
        }
    }

    private static List<String> checkAllGranted(FragmentActivity activity, String... permissions) {
        List<String> realPermissions = new ArrayList<>();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return realPermissions;
        } else if (permissions != null) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    realPermissions.add(permission);
                }
            }
            return realPermissions;
        } else {
            return realPermissions;
        }
    }
}
