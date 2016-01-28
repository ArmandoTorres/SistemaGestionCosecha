package com.delacrmi.connection;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.delacrmi.persistences.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by miguel on 07/10/15.
 */
public class ConnectSQLite extends SQLiteOpenHelper{
    //Catalog tablesCreater
    public static List<String> tablesCreater;
    public static List<String> tablesNames;
    public Object entitiesBackup;

    {
        if(tablesCreater == null || tablesNames == null) throw new  NullPointerException();
    }

    public ConnectSQLite(Context context, String DBName,
                         CursorFactory factory, int version) {
        super(context, DBName, factory, version);

    }

    private void createTables(SQLiteDatabase db) {
        for (String value: tablesCreater) {
            Log.d("creating", value);
            db.execSQL(value);
        }
    }

    private void dropTables(SQLiteDatabase db){
        for (String value:tablesNames) {
            Log.d("creating", value);
            db.execSQL("drop table if exists "+value);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("creating", "Creando");
        beforeToCreate(db);
        createTables(db);
        afterToCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Log.d("creating", "Actualizando");
        beforeToUpdate(db);
        dropTables(db);
        createTables(db);
        afterToUpdate(db);
    }

    public void setEntitiesBackup(Object entitiesBackup){
        this.entitiesBackup = entitiesBackup;
    }
    public Object getEntitiesBackup(){
        return entitiesBackup;
    }

    public void beforeToUpdate(SQLiteDatabase db){}
    public void afterToUpdate(SQLiteDatabase db){}
    public void beforeToCreate(SQLiteDatabase db){}
    public void afterToCreate(SQLiteDatabase db){}
}
