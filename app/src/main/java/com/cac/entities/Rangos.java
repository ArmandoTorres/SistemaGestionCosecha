package com.cac.entities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import com.delacrmi.persistences.Entity;
import com.delacrmi.persistences.EntityColumn;
import com.delacrmi.persistences.EntityFilter;

/**
 * Created by Legal on 19/10/2015.
 */
public class Rangos extends Entity {

    public static String TABLE_NAME   = "ba_mrango_dispositivo";
    public static String ID_EMPRESA   = "id_empresa";
    public static String ID_PERIODO   = "id_periodo";
    public static String DISPOSITIVO  = "dispositivo";
    public static String APLICACION   = "aplicacion";
    public static String RANGO_DESDE  = "rango_desde";
    public static String RANGO_HASTA  = "rango_hasta";

    @Override
    public Rangos entityConfig() {
        setName(Rangos.TABLE_NAME);
        setNickName("Rango");
        addColumn(Rangos.ID_EMPRESA, EntityColumn.ColumnType.INTEGER);
        addColumn(Rangos.ID_PERIODO, EntityColumn.ColumnType.INTEGER);
        addColumn(Rangos.DISPOSITIVO, EntityColumn.ColumnType.TEXT);
        addColumn(Rangos.APLICACION, EntityColumn.ColumnType.TEXT);
        addColumn(Rangos.RANGO_DESDE, EntityColumn.ColumnType.INTEGER);
        addColumn(Rangos.RANGO_HASTA, EntityColumn.ColumnType.INTEGER);
        setSynchronizable(true);
        return this;
    }

    @Override
    public void configureEntityFilter(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String empresa     = sharedPreferences.getString("EMPRESA", "30");
        String periodo     = sharedPreferences.getString("PERIODO", "20");
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String dispositivo = telephonyManager.getDeviceId();
        String nombreAplicacion = sharedPreferences.getString("NOMBRE_APLICACION", "SGC");
        setEntityFilter(new EntityFilter(new String[]{ID_EMPRESA, DISPOSITIVO, APLICACION, ID_PERIODO}, new String[]{empresa, dispositivo, nombreAplicacion,periodo}));
    }
}