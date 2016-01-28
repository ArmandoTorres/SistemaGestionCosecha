package com.cac.entities;

import android.content.Context;

import com.delacrmi.persistences.Entity;
import com.delacrmi.persistences.EntityColumn;
import com.delacrmi.persistences.EntityManager;

import java.util.List;

/**
 * Created by Legal on 15/10/2015.
 */
public class Transaccion extends Entity {

    public static String TABLE_NAME     = "ba_mtransaccion";
    public static String ID_EMPRESA     = "id_empresa";
    public static String ID_PERIODO     = "id_periodo";
    public static String NO_RANGO       = "no_rango";
    public static String FRENTE_CORTE   = "frente_corte";
    public static String FRENTE_ALCE    = "frente_alce";
    public static String ORDEN_QUEMA    = "orden_quema";
    public static String ID_FINCA       = "id_finca";
    public static String ID_CANIAL      = "id_canial";
    public static String ID_LOTE        = "id_lote";
    public static String FECHA_CORTE    = "fecha_corte";
    public static String CLAVE_CORTE    = "clave_corte";
    public static String CODIGO_CABEZAL = "codigo_cabezal";
    public static String CONDUCTOR_CABEZAL    = "conductor_cabezal";
    public static String CODIGO_CARRETA       = "codigo_carreta";
    public static String CODIGO_COSECHADORA   = "codigo_cosechadora";
    public static String OPERADOR_COSECHADORA = "operador_cosechadora";
    public static String CODIGO_TRACTOR       = "codigo_tractor";
    public static String OPERADOR_TRACTOR     = "operador_tractor";
    public static String CODIGO_APUNTADOR     = "codigo_apuntador";
    public static String CODIGO_VAGON         = "codigo_vagon";
    public static String ESTADO               = "estado";
    public static String DISPOSITIVO          = "dispositivo";
    public static String APLICACION           = "aplicacion";
    public static String FORMA_TIRO           = "forma_tiro";
    public static String FECHA_ENVIO          = "fecha_envio";

    public Transaccion(){}
    public Transaccion(EntityManager manager){
        setEntityManager(manager);
    }

    @Override
    public Transaccion entityConfig() {
        setName(Transaccion.TABLE_NAME);
        addColumn(Transaccion.ID_EMPRESA, EntityColumn.ColumnType.INTEGER);
        addColumn(Transaccion.ID_PERIODO, EntityColumn.ColumnType.TEXT);
        addColumn(Transaccion.NO_RANGO, EntityColumn.ColumnType.INTEGER);
        addColumn(Transaccion.FRENTE_CORTE, EntityColumn.ColumnType.INTEGER);
        addColumn(Transaccion.FRENTE_ALCE, EntityColumn.ColumnType.INTEGER);
        addColumn(Transaccion.ORDEN_QUEMA, EntityColumn.ColumnType.INTEGER);
        addColumn(Transaccion.ID_FINCA, EntityColumn.ColumnType.INTEGER);
        addColumn(Transaccion.ID_CANIAL, EntityColumn.ColumnType.INTEGER);
        addColumn(Transaccion.ID_LOTE, EntityColumn.ColumnType.INTEGER);
        addColumn(Transaccion.FECHA_CORTE,EntityColumn.ColumnType.DATE);
        addColumn(Transaccion.CLAVE_CORTE, EntityColumn.ColumnType.TEXT);
        addColumn(Transaccion.CODIGO_CABEZAL,EntityColumn.ColumnType.TEXT);
        addColumn(Transaccion.CONDUCTOR_CABEZAL, EntityColumn.ColumnType.INTEGER);
        addColumn(Transaccion.CODIGO_CARRETA,EntityColumn.ColumnType.TEXT);
        addColumn(Transaccion.CODIGO_COSECHADORA,EntityColumn.ColumnType.TEXT);
        addColumn(Transaccion.OPERADOR_COSECHADORA, EntityColumn.ColumnType.INTEGER);
        addColumn(Transaccion.CODIGO_TRACTOR,EntityColumn.ColumnType.TEXT);
        addColumn(Transaccion.OPERADOR_TRACTOR, EntityColumn.ColumnType.INTEGER);
        addColumn(Transaccion.CODIGO_APUNTADOR, EntityColumn.ColumnType.INTEGER);
        addColumn(Transaccion.CODIGO_VAGON,EntityColumn.ColumnType.TEXT);
        addColumn(Transaccion.ESTADO,EntityColumn.ColumnType.TEXT); // ACTIVA O TRASLADADA
        addColumn(Transaccion.DISPOSITIVO, EntityColumn.ColumnType.TEXT);
        addColumn(Transaccion.APLICACION, EntityColumn.ColumnType.TEXT);
        addColumn(Transaccion.FORMA_TIRO, EntityColumn.ColumnType.TEXT);
        addColumn(Transaccion.FECHA_ENVIO,EntityColumn.ColumnType.DATE);
        setSynchronizable(false);
        return this;
    }

    @Override
    public List<Entity> getDefaultInsert(){
        return getEntityManager().find(getClass(), "*" , null, null);
    }

    public enum TransaccionEstado {
        ACTIVA, TRASLADADA
    }
}