package com.cac.entities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.delacrmi.persistences.Entity;
import com.delacrmi.persistences.EntityColumn;
import com.delacrmi.persistences.EntityFilter;

/**
 * Created by miguel on 10/10/15.
 */
public class Fincas extends Entity {
    //CP_FINCA(ID_EMPRESA INTEGER, ID_FINCA INTEGER, DESCRIPCION TEXT, UBICACION TEXT)";

    public static String ID_FINCA    = "id_finca";
    public static String DESCRIPCION = "descripcion";
    public static String TABLE_NAME  = "cp_finca";
    public static String UBICACION   = "ubicacion";
    public static String ID_EMPRESA  = "id_empresa";

    @Override
    public Fincas entityConfig() {
        setName(TABLE_NAME);
        setNickName("Finca");
        addColumn(ID_FINCA, EntityColumn.ColumnType.INTEGER);
        addColumn(DESCRIPCION, EntityColumn.ColumnType.TEXT);
        addColumn(UBICACION,EntityColumn.ColumnType.TEXT);
        addColumn(ID_EMPRESA,EntityColumn.ColumnType.INTEGER);
        setSynchronizable(true);
        return this;
    }

    @Override
    public void configureEntityFilter(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String empresa = sharedPreferences.getString("EMPRESA", "30");
        setEntityFilter(new EntityFilter(new String[]{ID_EMPRESA}, new String[]{empresa}));
    }

}