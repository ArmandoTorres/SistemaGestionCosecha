package com.cac.tools;

import android.support.design.widget.FloatingActionButton;
import android.view.View;

import java.io.Serializable;
import java.util.List;

/**
 * Created by miguel on 02/11/15.
 */
public interface MainComponentEdit extends Serializable{
    void onClickFloating(View view);
    void MainViewConfig(List<View> views);
    String getTAG();
    int getSubTitle();
}
