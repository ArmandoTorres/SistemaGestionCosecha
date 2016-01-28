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
public class Periodos extends Entity {
    //"CREATE TABLE PG_PERIODO(ID_EMPRESA INTEGER, ID_PERIODO INTEGER,FECHA_INI REAL,FECHA_FIN REAL,DESCRIPCION TEXT)"

    public static String ID_EMPRESA = "id_empresa";
    public static String ID_PERIODO = "id_periodo";
    public static String FECHA_INI  = "fecha_inicio";
    public static String FECHA_FIN  = "fecha_fin";
    public static String DESCRIPCION = "descripcion";
    public static String TABLE_NAME  = "pg_periodo";

    @Override
    public Periodos entityConfig() {
        setName(TABLE_NAME);
        setNickName("Periodos");
        addColumn(ID_PERIODO, EntityColumn.ColumnType.INTEGER);
        addColumn(ID_EMPRESA, EntityColumn.ColumnType.INTEGER);
        addColumn(FECHA_INI, EntityColumn.ColumnType.DATE);
        addColumn(FECHA_FIN, EntityColumn.ColumnType.DATE);
        addColumn(DESCRIPCION,EntityColumn.ColumnType.TEXT);
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
