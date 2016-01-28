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
public class Caniales extends Entity {
    //"CREATE TABLE CP_CANIAL(ID_EMPRESA INTEGER, ID_FINCA INTEGER,ID_CANIAL INTEGER, DESCIPCION TEXT)";

    public static String ID_EMPRESA  = "id_empresa";
    public static String ID_FINCA    = "id_finca";
    public static String ID_CANIAL   = "id_canial";
    public static String DESCRIPCION = "descripcion";
    public static String TABLE_NAME  = "cp_canial";

    @Override
    public Caniales entityConfig() {
        setName(Caniales.TABLE_NAME);
        setNickName("Cañial");
        addColumn(Caniales.ID_EMPRESA, EntityColumn.ColumnType.INTEGER);
        addColumn(Caniales.ID_FINCA, EntityColumn.ColumnType.INTEGER);
        addColumn(Caniales.ID_CANIAL, EntityColumn.ColumnType.INTEGER);
        addColumn(Caniales.DESCRIPCION, EntityColumn.ColumnType.TEXT);
        setSynchronizable(true);
        return this;
    }

    @Override
    public void configureEntityFilter(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String empresa = sharedPreferences.getString("EMPRESA", "30");
        setEntityFilter(new EntityFilter(new String[]{ID_EMPRESA},new String[]{empresa}));
    }

}