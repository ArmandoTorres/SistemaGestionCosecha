package com.cac.customcomponents;

import android.view.View;
import android.widget.EditText;
import android.widget.DatePicker;
import android.app.DatePickerDialog;

/**
 * Created by Legal on 13/10/2015.
 */
public class MyDialogDateListenerFactory implements DatePickerDialog.OnDateSetListener {

    private View field;

    public MyDialogDateListenerFactory(View field){
        this.field = field;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if ( field instanceof EditText )
            ((EditText) field).setText(putZeroBefore(dayOfMonth) + "/" + putZeroBefore(monthOfYear+1) + "/" + year);
    }

    private String putZeroBefore(int number){
        if(number < 10) return  "0"+number;
        else return ""+number;
    }

}
