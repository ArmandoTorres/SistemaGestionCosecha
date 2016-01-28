package com.cac.tools;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cac.R;
import com.cac.viewer.SyncFragment;

import org.json.JSONArray;

import java.util.Vector;


/**
 * Created by miguel on 28/10/15.
 */
public class ViewSyncHolder extends RecyclerView.ViewHolder {

    public TextView title;
    public ImageButton btn_sync;
    public ProgressBar pgb_sync;
    public TextView tvProgress;
    public String tableName;
    public String whereCondition;
    public JSONArray whereValues;
    private View view;

    private View.OnClickListener onClickListener;

    public ViewSyncHolder(View itemView) {
        super(itemView);

        events();

        view = itemView;

        title = (TextView)itemView.findViewById(R.id.tv_sync_name);
        btn_sync = (ImageButton)itemView.findViewById(R.id.btn_sync);

        pgb_sync = (ProgressBar)itemView.findViewById(R.id.pgb_sync);
        pgb_sync.getLayoutParams().height = 10;

        tvProgress = (TextView) itemView.findViewById(R.id.tv_sync_progress);

        btn_sync.setOnClickListener(onClickListener);

        itemView.setTag(this);

    }

    public void bindTableSync(Vector<Object> tableInfo){
        title.setText((String)tableInfo.get(0));
        tableName = (String)tableInfo.get(1);
        if ( tableInfo.size() == 5 ) {
            whereCondition = (String) tableInfo.get(3);
            whereValues    = (JSONArray) tableInfo.get(4);
        }else{
            whereCondition = null;
            whereValues = null;
        }
        pgb_sync.setProgress(Integer.parseInt((String) tableInfo.get(2)));
        tvProgress.setText(tableInfo.get(2)+"%");
    }

    public View getView(){
        return view;
    }

    public void events(){
        onClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SyncFragment.getInstance().threadSynchronizer(ViewSyncHolder.this.tableName,ViewSyncHolder.this.whereCondition,ViewSyncHolder.this.whereValues);
            }
        };
    }
}
