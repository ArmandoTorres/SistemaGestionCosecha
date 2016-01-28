package com.atorres;

import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by Legal on 30/10/2015.
 */
public class AndroidUtils {

    public static void showAlertMsg(Context context, String title,String msg) {
        android.app.AlertDialog.Builder dialogBuilder;
        dialogBuilder = new android.app.AlertDialog.Builder(context);

        dialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage(msg);
        dialogBuilder.show();
    }
}
