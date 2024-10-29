package com.tbruyelle.rxpermissions3;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.richzjc.dialoglib.base.BaseDialogFragment;
import com.richzjc.dialoglib.util.ScreenUtil;
import java.util.HashMap;
import java.util.List;


public class PermissionSettiingDialog extends BaseDialogFragment {

    private static HashMap<String, String> permissionMap = new HashMap<>();

    static {
        permissionMap.put(Manifest.permission.READ_EXTERNAL_STORAGE, "存储权限");
        permissionMap.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, "存储权限");
        permissionMap.put(Manifest.permission.READ_CONTACTS, "联系人权限");
        permissionMap.put(Manifest.permission.WRITE_CONTACTS, "联系人权限");
        permissionMap.put(Manifest.permission.READ_PHONE_STATE, "手机状态权限");
        permissionMap.put(Manifest.permission.CALL_PHONE, "打电话权限");
        permissionMap.put(Manifest.permission.READ_SMS, "短信权限");
        permissionMap.put(Manifest.permission.SEND_SMS, "短信权限");
        permissionMap.put(Manifest.permission.READ_SMS, "短信权限");
        permissionMap.put(Manifest.permission.CAMERA, "相机权限");
        permissionMap.put(Manifest.permission.RECORD_AUDIO, "录音权限");
        permissionMap.put(Manifest.permission.READ_CALENDAR, "日历权限");
        permissionMap.put(Manifest.permission.WRITE_CALENDAR, "日历权限");
    }


    private View cancel;
    private TextView confirm;
    private TextView title;


    @Override
    public int doGetContentViewId() {
        return R.layout.permission_dialog_permission_desc;
    }

    @Nullable
    @Override
    public Dialog getDialog() {
        Dialog dialog = super.getDialog();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    public int getDialogWidth() {
        return ScreenUtil.getScreenWidth(getContext()) - ScreenUtil.dip2px(100f, getContext());
    }

    @Override
    public int getDialogHeight() {
        return super.getDialogHeight();
    }

    @Override
    public int getStyle() {
        return R.style.BaseDefaultDialog;
    }

    @Override
    public int getGravity() {
        return Gravity.CENTER;
    }

    @Override
    public void doInitSubViews(View view) {
        super.doInitSubViews(view);
        title = view.findViewById(R.id.title1);
        cancel = view.findViewById(R.id.cancel);
        confirm = view.findViewById(R.id.confirm);

        confirm.setText("去设置");


        String desc = "拒绝且不在访问,后续的权限申请需手动前往设置页面打开";
        List<String> permissions = getArguments().getStringArrayList("permissions");
        if (permissions != null && permissions.size() > 0) {
            StringBuilder builder = new StringBuilder();
            for (String permission : permissions) {
                builder.append(permissionMap.get(permission));
            }

            desc = "授权应用访问你的\"" + builder + "\"";
        }

        title.setText(desc);
    }

    @Override
    public void doInitData() {
        cancel.setOnClickListener(v -> {
            dismiss();
        });

        confirm.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
            dismiss();
        });
    }

}
