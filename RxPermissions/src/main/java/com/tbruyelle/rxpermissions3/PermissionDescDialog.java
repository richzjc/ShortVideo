package com.tbruyelle.rxpermissions3;

import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.richzjc.dialoglib.base.BaseDialogFragment;
import com.richzjc.dialoglib.util.ScreenUtil;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;


public class PermissionDescDialog extends BaseDialogFragment implements ObservableOnSubscribe<Boolean> {

    private View cancel;
    private View confirm;
    private TextView title;

    public int flag = FLAG_DEFALUT;
    private final static int FLAG_DEFALUT = 0;
    public final static int FLAG_CANCEL_DISMISS = 1;
    public final static int FLAG_CANCEL_CONFIRM = 2;

    public ObservableEmitter<Boolean> emmiter = null;

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
        String desc = "";
        if (getArguments() != null) {
            desc = getArguments().getString("desc", "点击确定开始申请相应权限");
        }
        title.setText(desc);
    }

    @Override
    public void doInitData() {
        cancel.setOnClickListener(v -> {
            dismiss();
            flag = FLAG_CANCEL_DISMISS;
            if (emmiter != null) {
                emmiter.onNext(false);
                emmiter.onComplete();
            }
        });

        confirm.setOnClickListener(v -> {
            dismiss();
            flag = FLAG_CANCEL_CONFIRM;
            if(emmiter != null) {
                emmiter.onNext(true);
                emmiter.onComplete();
            }
        });
    }

    @Override
    public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) {
        emmiter = emitter;
        if(flag == FLAG_CANCEL_DISMISS){
            if (emmiter != null) {
                emmiter.onNext(false);
                emmiter.onComplete();
            }
        }else if(flag == FLAG_CANCEL_CONFIRM){
            if(emmiter != null) {
                emmiter.onNext(true);
                emmiter.onComplete();
            }
        }
    }
}
