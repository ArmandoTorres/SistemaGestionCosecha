package com.cac.entities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.delacrmi.persistences.Entity;
import com.delacrmi.persistences.EntityColumn;
import com.delacrmi.persistences.EntityFilter;

/**
 * Created by Administrator on 23/2/2016.
 */
public class ClaveCorte extends Entity {

    public static final String ID          = "clave_corte";
    public static final String ID_EMPRESA  = "id_empresa";
    public static final String DESCRIPCION = "descripcion";
    public static final String ESTADO      = "estado";
    public static final String TABLE_NAME  = "ba_clave_corte";

    @Override
    public Entity entityConfig() {
        setName(TABLE_NAME);
        setNickName("Clave de Corte");
        addColumn(ID, EntityColumn.ColumnType.TEXT);
        addColumn(ID_EMPRESA, EntityColumn.ColumnType.INTEGER);
        addColumn(DESCRIPCION, EntityColumn.ColumnType.TEXT);
        addColumn(ESTADO, EntityColumn.ColumnType.TEXT);
        setSynchronizable(true);
        return this;
    }

    @Override
    public void configureEntityFilter(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String empresa = sharedPreferences.getString("EMPRESA", "30");
        setEntityFilter(new EntityFilter(new String[]{ID_EMPRESA, ESTADO},
                new String[]{empresa,Estado.ACTIVO.toString()}));
    }
}