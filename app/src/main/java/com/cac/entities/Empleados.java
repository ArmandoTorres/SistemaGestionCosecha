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
public class Empleados extends Entity{
    //"CREATE TABLE RH_EMPLEADO(ID_EMPRESA INTEGER,ID_EMPLEADO INTEGER, ID_PUESTO INTEGER, NOMBRE TEXT, ESTADO TEXT)";

    public static String ID_EMPLEADO = "id_empleado";
    public static String ID_EMPRESA = "id_empresa";
    public static String ID_PUESTO  = "id_puesto";
    public static String NOMBRE1 = "nombre1";
    public static String NOMBRE2 = "nombre2";
    public static String APELLIDO1 = "apellido1";
    public static String APELLIDO2 = "apellido2";
    public static String ESTADO_EMPLEADO = "estado_empleado";
    public static String TABLE_NAME = "rh_empleado";

    @Override
    public Empleados entityConfig() {
        setName(Empleados.TABLE_NAME);
        setNickName("Empleado");
        addColumn(Empleados.ID_EMPLEADO, EntityColumn.ColumnType.INTEGER);
        addColumn(Empleados.ID_EMPRESA, EntityColumn.ColumnType.INTEGER);
        addColumn(Empleados.ID_PUESTO, EntityColumn.ColumnType.TEXT);
        addColumn(Empleados.NOMBRE1, EntityColumn.ColumnType.TEXT);
        addColumn(Empleados.NOMBRE2, EntityColumn.ColumnType.TEXT);
        addColumn(Empleados.APELLIDO1, EntityColumn.ColumnType.TEXT);
        addColumn(Empleados.APELLIDO2, EntityColumn.ColumnType.TEXT);
        addColumn(Empleados.ESTADO_EMPLEADO,EntityColumn.ColumnType.TEXT);
        setSynchronizable(true);
        return this;
    }

    @Override
    public void configureEntityFilter(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String empresa = sharedPreferences.getString("EMPRESA", "30");
        String estadoEmpleado = "A";
        setEntityFilter(new EntityFilter(new String[]{ID_EMPRESA, ESTADO_EMPLEADO}, new String[]{empresa,estadoEmpleado}));
    }
}