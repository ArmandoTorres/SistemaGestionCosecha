package com.cac.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.atorres.AndroidUtils;
import com.atorres.BarcodeEncoder;
import com.atorres.Contents;
import com.cac.entities.Frentes;
import com.cac.entities.Transaccion;
import com.cac.pojos.ListadoTransacciones;
import com.delacrmi.persistences.Entity;
import com.delacrmi.persistences.EntityManager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Armando Torres on 13/01/2016.
 *
 * Clase encargada de dar formato a los envios para poder imprimir el texto correspondiente
 * via impresora bluetooth.
 *
 */
public class FormatReportData {

    private Context context;
    private EntityManager entityManager;

    public FormatReportData(Context context, EntityManager entityManager) {
        if ( context == null || entityManager == null )
            throw new NullPointerException("All constructor parameter are required.");

        this.context = context;
        this.entityManager = entityManager;
    }

    public List<ListadoTransacciones> formatAllTransactionOnDataBase( ) {
        try {
            List<ListadoTransacciones> resultado = new ArrayList<>();
            for (Entity a : entityManager.find(Transaccion.class, "*", null, null, Transaccion.NO_RANGO+" desc limit 10")) {
                resultado.add(formatTransaction((Transaccion) a, true));
            }
            return resultado;
        } catch ( Exception ex ) {
            Log.e("Error:","Ocurrio un error a la hora de generar la informacion a mostrar.",ex);
            AndroidUtils.showAlertMsg(context,"Error","Ocurrio un error a la hora de generar la información.");
            return new ArrayList<>();
        }
    }

    public ListadoTransacciones formatTransaction(Transaccion transaccion, boolean includeBMP) {
        try {
            String titulo, subTitle, barcode, reporte = "";
            String separator = ",";
            ListadoTransacciones listadoTransacciones = new ListadoTransacciones();

            titulo = "Ingenio Barahona Zafra 2015-2016";
            subTitle = ((Frentes) entityManager.findOnce(Frentes.class, Frentes.TIPO_CANIA,
                    Frentes.ID_FRENTE + " = " + transaccion.getColumnValueList().getAsString(Transaccion.FRENTE_CORTE),
                    null)).getColumnValueList().getAsString(Frentes.TIPO_CANIA);
            subTitle += " Envio NO. " + transaccion.getColumnValueList().getAsString(Transaccion.NO_RANGO);
            subTitle.replace('ñ', 'n');
            subTitle.replace('Ñ', 'N');

            //Detalle
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            reporte += "Fecha: " + isNullValue(format.format(new Date(transaccion.getColumnValueList().getAsLong(Transaccion.FECHA_CORTE))));
            reporte += " - Frente Corte: " + isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.FRENTE_CORTE));
            reporte += " - Frente Alce: " + isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.FRENTE_ALCE));
            reporte += " - Orden Quema: " + isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.ORDEN_QUEMA));
            reporte += " - Finca: " + isNullValue(String.format("%03d", Integer.parseInt(transaccion.getColumnValueList().getAsString(Transaccion.ID_FINCA))));
            reporte += " - Canial: " + isNullValue(String.format("%04d", Integer.parseInt(transaccion.getColumnValueList().getAsString(Transaccion.ID_CANIAL))));
            reporte += " - Lote: " + isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.ID_LOTE));
            reporte += " - Clave Corte: " + isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.CLAVE_CORTE));
            reporte += " - Carreta: " + isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.CODIGO_CARRETA));
            reporte += " - Vagon: " + isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.CODIGO_VAGON));
            reporte += " - Cosechadora: " + isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.CODIGO_COSECHADORA));
            reporte += " - Operador Cosechadora: " + isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.OPERADOR_COSECHADORA));
            reporte += " - Tractor: " + isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.CODIGO_TRACTOR));
            reporte += " - Operador Tractor: " + isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.OPERADOR_TRACTOR));
            reporte += " - Apuntador:" + isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.CODIGO_APUNTADOR));
            reporte += " - Cabezal: " + isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.CODIGO_CABEZAL));
            reporte += " - Piloto: " + isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.CONDUCTOR_CABEZAL));
            reporte += " - Tipo de Tiro: " + isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.FORMA_TIRO));

            //Lineas del detalle
            List<String> detallePorLinea = new ArrayList<>();
            detallePorLinea.add("Fecha: " + isNullValue(format.format(new Date(transaccion.getColumnValueList().getAsLong(Transaccion.FECHA_CORTE))))
                    + " - Frente Corte: " + isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.FRENTE_CORTE)));
            detallePorLinea.add("Frente Alce: " + isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.FRENTE_ALCE))
                    + "- Orden Quema: " + isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.ORDEN_QUEMA)));
            detallePorLinea.add("Finca: " + isNullValue(String.format("%03d", Integer.parseInt(transaccion.getColumnValueList().getAsString(Transaccion.ID_FINCA))))
                    + " - Canial: " + isNullValue(String.format("%04d", Integer.parseInt(transaccion.getColumnValueList().getAsString(Transaccion.ID_CANIAL)))));
            detallePorLinea.add("Lote: " + isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.ID_LOTE))
                    + " - Clave Corte: " + isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.CLAVE_CORTE)));
            detallePorLinea.add("Carreta: " + isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.CODIGO_CARRETA))
                    + " - Vagon: " + isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.CODIGO_VAGON)));
            detallePorLinea.add("Cosechadora: " + isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.CODIGO_COSECHADORA)));
            detallePorLinea.add("Operador Cosechadora: " + isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.OPERADOR_COSECHADORA)));
            detallePorLinea.add("Tractor: " + isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.CODIGO_TRACTOR)));
            detallePorLinea.add("Operador Tractor: " + isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.OPERADOR_TRACTOR)));
            detallePorLinea.add("Apuntador:" + isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.CODIGO_APUNTADOR))
                    + " - Cabezal: " + isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.CODIGO_CABEZAL)));
            detallePorLinea.add("Piloto: " + isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.CONDUCTOR_CABEZAL))
                    + " - Tipo de Tiro: " + isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.FORMA_TIRO)));

            //Barcode
            barcode = isNullValue(format.format(new Date(transaccion.getColumnValueList().getAsLong(Transaccion.FECHA_CORTE)))) + separator;
            barcode += isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.FRENTE_CORTE)) + separator;
            barcode += isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.FRENTE_ALCE)) + separator;
            barcode += isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.ORDEN_QUEMA)) + separator;
            barcode += isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.ID_FINCA)) + separator;
            barcode += isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.ID_CANIAL)) + separator;
            barcode += isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.ID_LOTE)) + separator;
            barcode += isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.CLAVE_CORTE)) + separator;
            barcode += isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.CODIGO_CARRETA)) + separator;
            barcode += isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.CODIGO_VAGON)) + separator;
            barcode += isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.CODIGO_COSECHADORA)) + separator;
            barcode += isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.OPERADOR_COSECHADORA)) + separator;
            barcode += isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.CODIGO_TRACTOR)) + separator;
            barcode += isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.OPERADOR_TRACTOR)) + separator;
            barcode += isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.CODIGO_APUNTADOR)) + separator;
            barcode += isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.CODIGO_CABEZAL)) + separator;
            barcode += isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.CONDUCTOR_CABEZAL)) + separator;
            barcode += isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.FORMA_TIRO)) + separator;
            barcode += isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.ID_EMPRESA)) + separator;
            barcode += isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.ID_PERIODO)) + separator;
            barcode += isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.NO_RANGO)) + separator;
            barcode += isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.DISPOSITIVO)) + separator;
            barcode += isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.APLICACION)) + separator;
            barcode += isNullValue(transaccion.getColumnValueList().getAsString(Transaccion.ESTADO));

            listadoTransacciones.setTitulo(titulo);
            listadoTransacciones.setSubTitulo(subTitle);
            listadoTransacciones.setDetalle(reporte);
            listadoTransacciones.setBarcode(barcode);
            listadoTransacciones.setDetalles(detallePorLinea);
            listadoTransacciones.setFecha(format.format(new Date(transaccion.getColumnValueList().getAsLong(Transaccion.FECHA_CORTE))));
            listadoTransacciones.setEstado(transaccion.getColumnValueList().getAsString(Transaccion.ESTADO));

            if (includeBMP) {
                // Bitmap with barcode
                BarcodeEncoder qrCodeEncoder = new BarcodeEncoder(barcode, null,
                        Contents.Type.TEXT, BarcodeFormat.PDF_417.toString(), 500);

                Bitmap bitmap = null;
                try {
                    bitmap = qrCodeEncoder.encodeAsBitmap();
                    listadoTransacciones.setBmp(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }

                listadoTransacciones.setBmp(bitmap);
            }

            return listadoTransacciones;
        } catch (Exception ex) {
            Log.e("Error", "Ocurrio un error a la hora de crear el reporte de impresion.", ex);
            AndroidUtils.showAlertMsg(context,"Error","Ocurrio un error al crear el reporte");
            return new ListadoTransacciones();
        }
    }

    private String isNullValue( String value ) {
        if ( value == null || value.isEmpty() || value.equals("") || value.equals("null") )
            return "";
        return value;
    }
}