package com.cac.entities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.delacrmi.persistences.Entity;
import com.delacrmi.persistences.EntityColumn;
import com.delacrmi.persistences.EntityFilter;

/**
 * Created by Legal on 20/11/2015.
 */
public class EmpleadoCosechadora extends Entity {

    public static String ID_EMPRESA  = "id_empresa";
    public static String ID_EMPLEADO = "id_empleado";
    public static String NOMBRE      = "nombre";
    public static String TABLE_NAME  = "inf_view_empleado_cosechadora";

    @Override
    public Entity entityConfig() {
        setName(TABLE_NAME);
        setNickName("Operador Cosechadora");
        addColumn(ID_EMPRESA, EntityColumn.ColumnType.INTEGER);
        addColumn(ID_EMPLEADO, EntityColumn.ColumnType.INTEGER);
        addColumn(NOMBRE, EntityColumn.ColumnType.TEXT);
        setSynchronizable(true);
        return this;
    }

    @Override
    public void configureEntityFilter(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String empresa = sharedPreferences.getString("EMPRESA","30");
        setEntityFilter(new EntityFilter(new String[]{ID_EMPRESA},new String[]{empresa}));
    }
}