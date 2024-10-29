package com.richzjc.shortvideo.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.richzjc.shortvideo.R;


/**
 * Created by micker on 16/6/16.
 */
public class RootTabItem extends RelativeLayout {
    TextView tab_textView;
    ImageView tab_img;

    public RootTabItem(Context context) {
        super(context);
        init();
    }

    public RootTabItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RootTabItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        tab_textView = (TextView) findViewById(R.id.tab_text);
        tab_img = (ImageView) findViewById(R.id.tab_img);
    }


    public void configData(String name, int drawableId) {
        init();
        tab_textView.setText(name);
        tab_img.setImageResource(drawableId);
    }


}
