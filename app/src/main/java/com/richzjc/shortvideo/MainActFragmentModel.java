package com.richzjc.shortvideo;

import com.richzjc.shortvideo.fragment.EditPicFragment;
import com.richzjc.shortvideo.fragment.PicVideoFragment;
import com.richzjc.shortvideo.fragment.VideoCreateFragment;
import com.richzjc.shortvideo.model.MainTabItemEntity;

import java.util.ArrayList;

public class MainActFragmentModel {
    private static ArrayList<MainTabItemEntity> tabItemEntities;
    private static MainActFragmentModel instance;

    private MainActFragmentModel() {

    }

    public static MainActFragmentModel getInstance() {
        if (instance == null)
            instance = new MainActFragmentModel();
        return instance;
    }

    public ArrayList<MainTabItemEntity> getTabItems() {
        if (tabItemEntities == null)
            tabItemEntities = new ArrayList<>();
        return tabItemEntities;
    }

    static {
        if (tabItemEntities == null)
            tabItemEntities = new ArrayList<>();

//        tabItemEntities.add(new MainTabItemEntity(R.drawable.tab_drawable_news, "图片编辑", EditPicFragment.class, "editPic"));
        tabItemEntities.add(new MainTabItemEntity(R.drawable.tab_drawable_news, "视频制作", VideoCreateFragment.class, "videoCreate"));
        tabItemEntities.add(new MainTabItemEntity(R.drawable.tab_drawable_news, "图片视频", PicVideoFragment.class, "videoCreate"));
    }
}
