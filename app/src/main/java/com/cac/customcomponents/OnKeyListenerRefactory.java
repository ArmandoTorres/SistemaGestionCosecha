package com.cac.customcomponents;

import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Map;

/**
 * Created by miguel on 20/11/15.
 */
public class OnKeyListenerRefactory implements View.OnKeyListener {

    private boolean onKeyBoolean;
    private String message = "Valor Incorrecto";
    private Map<String,String> values;
    private View target;
    private boolean validateField;

    public OnKeyListenerRefactory(View target){
        this.target = target; validateField = true;
    }

    public OnKeyListenerRefactory(Map<String, String> values, View target){
        this.values = values;
        this.target = target;
        this.validateField = true;
    }

    public OnKeyListenerRefactory(Map<String, String> values, Boolean showDescription){
        this.values = values;
        this.validateField = showDescription;
    }

    public OnKeyListenerRefactory(Map<String, String> values, View target, String message){
        this.values = values;
        this.target = target;
        this.message = message;
    }

    public void setValidateField(boolean validateField) {
        this.validateField = validateField;
    }

    public void setMapValues(Map<String,String> values){
        this.values = values;
    }

    public boolean getOnkeyBoolean(){
        return onKeyBoolean;
    }

    public void beforeOnkeyValidate(){}
    public void afterOnKeyValidate(){}

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        onKeyBoolean = false;
        beforeOnkeyValidate();

        if( keyCode == 66 && validateField ){
            EditText view = (EditText)v;

            if(values.containsKey(view.getText().toString())){
                if(target instanceof TextView )
                    ((TextView)target).setText(values.get(view.getText().toString()));
            }else{
                ((TextView)target).setText("");
                view.setError(message);
                onKeyBoolean = true;
            }
        }
        afterOnKeyValidate();
        return onKeyBoolean;
    }

}
