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
public class Frentes extends Entity {
    //"CREATE TABLE RH_FRENTE(ID_FRENTE INTEGER, DESCRIPCION TEXT)"

    public static String ID_FRENTE   = "id_frente";
    public static String DESCRIPCION = "descripcion";
    public static String TABLE_NAME  = "RH_FRENTE";
    public static String TIPO_CANIA  = "tipo_cania";

    @Override
    public Frentes entityConfig() {
        setName(TABLE_NAME);
        setNickName("Frente");
        addColumn(ID_FRENTE, EntityColumn.ColumnType.INTEGER);
        addColumn(DESCRIPCION,EntityColumn.ColumnType.TEXT);
        addColumn(TIPO_CANIA,EntityColumn.ColumnType.TEXT);
        setSynchronizable(true);
        return this;
    }

    @Override
    public void configureEntityFilter(Context context) {
    }
}
