package com.cac.viewer;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cac.R;
import com.cac.tools.MainComponentEdit;

import java.util.List;

/**
 * Created by miguel on 02/11/15.
 */
public class MainFragment extends Fragment implements MainComponentEdit {

    //instance variables
    private static MainFragment outInstance;
    public static final String TAG = "MainFragment";
    private AppCompatActivity context;
    private View view;

    public static MainFragment init(AppCompatActivity context){
        if(outInstance == null){
            outInstance = new MainFragment();
            outInstance.context = context;
        }
        return outInstance;
    }

    public static MainFragment getInstance(){
        if(outInstance == null)
            throw new NullPointerException("the "+outInstance.getClass().getSimpleName()+" isn't created, " +
                    "call the init method first to try use this instance");
        return outInstance;
    }

    public String getTAG(){
        return "MainFragment";
    }

    //<editor-fold desc="Override Methods">

    @Override
    public int getSubTitle() {
        return R.string.app_description_name;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view == null)
            view = inflater.inflate(R.layout.main_fragment,container,false);

        return view;
    }

    @Override
    public void onClickFloating(View view) {
        Snackbar.make(view,"Prueba del SnackBar",Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void MainViewConfig(List<View> views) {
        for ( View view : views){
            switch (view.getId()){
                case R.id.btn_fab_right:
                    view.setVisibility(View.INVISIBLE);
                    break;
                case R.id.btn_fab_left:
                    view.setVisibility(View.INVISIBLE);
                    break;
                case R.id.main_body_action_layout:
                    view.getLayoutParams().height = 0;
                    break;
            }
        }
    }
    //</editor-fold>
}