package com.atorres;

import android.os.Looper;
import android.util.Log;

import com.cac.pojos.ListadoTransacciones;

import datamaxoneil.connection.ConnectionBase;
import datamaxoneil.connection.Connection_Bluetooth;
import datamaxoneil.printer.DocumentEZ;
import datamaxoneil.printer.ParametersEZ;

/**
 * Created by Legal on 08/01/2016.
 */
public class OneilPrinterHelper {


    public void openConnection(final String macAddress, final ListadoTransacciones listadoTransacciones, final int numberOfCopy ){


        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                //bluetooth connection
                ConnectionBase conn = null;
                int row = 50;
                int col = 1;
                try {

                    Looper.prepare();
                    conn = Connection_Bluetooth.createClient(macAddress);
                    conn.open();

                    if ( conn.getIsOpen() ) {

                        DocumentEZ document = new DocumentEZ("MF204");

                        document.writeText("Ingenio Barahona Zafra 2015-2016",row,col);
                        row+=25;
                        document.writeText("--------------------------------------------",row,col);
                        row+=25;
                        document.writeText(listadoTransacciones.getSubTitulo(),row,col);
                        row+=25;
                        document.writeText("--------------------------------------------",row,col);
                        row+=25;
                        for ( String str : listadoTransacciones.getDetalles() ){
                            document.writeText(str,row,col);
                            row+=25;
                        }
                        row+=25;
                        document.writeText("    ", row, col);
                        ParametersEZ parameter = new ParametersEZ();
                        parameter.setHorizontalMultiplier(3);
                        parameter.setVerticalMultiplier(3);

                        Log.e("Barcode Format.", listadoTransacciones.getBarcode());
                        //PDF417
                        document.writeBarCodePDF417(listadoTransacciones.getBarcode(), row, col, 2, 1, parameter);

                        row+=150;
                        document.writeText("    ", row, col);
                        row+=150;
                        document.writeText("    ", row, col);

                        for ( int i = 0; i < numberOfCopy; i++ ) {

                            conn.write(document.getDocumentData());
                            Thread.sleep(2000);

                        }

                    } else
                        Log.e("Conection Status","La coneccion no pudo ser abierta!!!");

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if ( conn != null ){
                        try{
                            conn.close();
                        } catch (Exception e ){
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        thread1.start();

    }

}