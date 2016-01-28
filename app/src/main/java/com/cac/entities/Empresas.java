package com.cac.entities;

import android.content.Context;

import com.delacrmi.persistences.Entity;
import com.delacrmi.persistences.EntityColumn;

/**
 * Created by miguel on 10/10/15.
 */
public class Empresas extends Entity {

    public static String ID_EMPRESA = "id_empresa";
    public static String DIRECCION_COMERCIAL = "direccion_comercial";
    public static String DESCRIPCION = "descripcion";
    public static String TABLE_NAME = "pg_empresa";
    public static String ESTADO = "estado";

    private boolean selected = false;

    public Empresas(){

    }

    @Override
    public Empresas entityConfig() {
        setName(Empresas.TABLE_NAME);
        setNickName("Empresa");
        addColumn(ID_EMPRESA, EntityColumn.ColumnType.INTEGER);
        addColumn(DESCRIPCION, EntityColumn.ColumnType.TEXT);
        addColumn(DIRECCION_COMERCIAL,EntityColumn.ColumnType.TEXT);
        addColumn(ESTADO,EntityColumn.ColumnType.TEXT);
        setSynchronizable(true);
        return this;
    }

    public enum EstadoEmpresas {
        ACTIVA, INACTIVA
    }

    @Override
    public void configureEntityFilter(Context context) {

    }
}