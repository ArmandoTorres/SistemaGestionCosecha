package com.cac.sgc;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.cac.entities.Users;
import com.delacrmi.persistences.Entity;
import com.delacrmi.persistences.EntityManager;

import com.cac.R;

/**
 * Created by delacrmi on 12/10/2015.
 */
public class LoginActivity extends FragmentActivity {

    private EditText userName;
    private EditText password;

    private Button signin;
    private Button cancel;
    private Intent intent;

    private EntityManager entityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.entityManager = new EntityManager(this,
                getResources().getString(R.string.db_name),
                null,
                Integer.parseInt(getResources().getString(R.string.db_version))).addTable(Users.class);

        userName = (EditText)findViewById(R.id.et_username);
        password = (EditText)findViewById(R.id.et_password);

        signin = (Button)findViewById(R.id.btn_sign_in);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent();
                String json;
                json = findUser(userName.getText().toString(),password.getText().toString(),entityManager).getJSON().toString();

                if(!json.equals("{}")){
                    intent.putExtra("user",json);
                    setResult(50, intent);
                    finish();
                } else Snackbar.make(v, getResources().getString(R.string.login_reject)+" "+json, Snackbar.LENGTH_SHORT).show();

            }
        });

        cancel = (Button)findViewById(R.id.btn_sign_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent();
                intent.putExtra("user","out");
                setResult(50, intent);
                finish();

            }
        });

    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item){
        if (item.getItemId() == android.R.id.home){
            //finish();
            Log.e("Back","Not values");
            return false;
        }else
            return super.onMenuItemSelected(featureId, item);
    }

    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e("Button", ""+keyCode);
        return true;
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                Snackbar.make(cancel,"Back",Snackbar.LENGTH_SHORT).show();
                return false;
            case KeyEvent.KEYCODE_HOME:
                Snackbar.make(cancel,"Home",Snackbar.LENGTH_SHORT).show();
                return false;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }*/

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.e("Button", "" + keyCode);
        return true;
        //return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public static Users findUser(String user, String passport, EntityManager entityManager){
        String us = user;
        String auth;

        if(us.contains("@")) auth = Users.EMAIL;
        else auth = Users.USER;

        String pass = Entity.encode(Users.getEncryptionType(),
                Users.getBaseEncryption(), passport);

        return (Users)entityManager.findOnce(Users.class,"*",
            auth + " = ? and " + Users.PASSWORD + " = ?",
            new String[]{us,pass});
    }
}
