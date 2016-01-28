package com.cac.tools;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.os.Looper;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.atorres.AndroidUtils;
import com.cac.R;
import com.cac.pojos.ListadoTransacciones;

import java.util.ArrayList;

import datamaxoneil.printer.DocumentEZ;
import datamaxoneil.printer.ParametersEZ;
import datamaxoneil.connection.ConnectionBase;
import datamaxoneil.connection.Connection_Bluetooth;
import it.custom.printer.api.android.CustomAndroidAPI;
import it.custom.printer.api.android.CustomException;

/**
 * Created by ArmandoTorres on 13/01/2016.
 */
public class PrinterManager {

    private Context context;
    private ListadoTransacciones listadoTransacciones;
    private int numberOfCopy;

    public PrinterManager( Context context, ListadoTransacciones listadoTransacciones, int numberOfCopy) {
        if ( context == null || listadoTransacciones == null )
            throw new NullPointerException("All constructor parameter's are required.");

        this.context = context;
        this.listadoTransacciones = listadoTransacciones;
        this.numberOfCopy = numberOfCopy;
    }

    public PrinterManager() {}

    public void setContext(Context context) {
        this.context = context;
    }

    public ListadoTransacciones getListadoTransacciones() {
        return listadoTransacciones;
    }

    public void setListadoTransacciones(ListadoTransacciones listadoTransacciones) {
        this.listadoTransacciones = listadoTransacciones;
    }

    public int getNumberOfCopy() {
        return numberOfCopy;
    }

    public void setNumberOfCopy(int numberOfCopy) {
        this.numberOfCopy = numberOfCopy;
    }

    public void printText ( ) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                callPrinter();
            }
        },"PrintManagerThread");

        thread.start();

    }

    public void chooseBluetoothDevice( final boolean chooseAndPrintText ) {

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //Preguntamos si el bluetooth esta activo
        if ( bluetoothAdapter == null || !bluetoothAdapter.isEnabled() ) {
            AndroidUtils.showAlertMsg(context,"Notificación","Debe encender el bluetooth del dispositivo.");
            return;
        }

        try {
            BluetoothDevice [] bluetoothDeviceList = CustomAndroidAPI.EnumBluetoothDevices();

            if ( bluetoothDeviceList != null ) {

                ArrayList<String> listadoDispositivos = new ArrayList<>();
                for (int i = 0; i < bluetoothDeviceList.length; i++) {
                    listadoDispositivos.add(bluetoothDeviceList[i].getName());
                }

                ArrayAdapter adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_single_choice, listadoDispositivos);

                // 1-) Creamos el dialogo.
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom_alert_dialog);
                dialog.setTitle("Elegir dispositivo");

                // 2-) Enlazamos los botones y lista.
                Button customAlertDialogButton   = (Button) dialog.findViewById(R.id.customAlertDialogButton);
                ListView customAlertDialogList   = (ListView) dialog.findViewById(R.id.customAlertDialogList);

                //3-) Al presionar el boton ocultamos el dialog.
                customAlertDialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                customAlertDialogList.setAdapter(adapter);
                customAlertDialogList.setItemChecked(0, true);
                customAlertDialogList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                customAlertDialogList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try {

                            BluetoothDevice[] bluetoothDeviceList = CustomAndroidAPI.EnumBluetoothDevices();
                            AppParameters.BLUETOOTH_PRINTER_MACADDRESS = bluetoothDeviceList[position].getAddress();
                            if (chooseAndPrintText)
                                printText();

                        } catch ( Exception ex ) { }
                    }
                });
                dialog.show();
            }
        } catch (CustomException e) {
            Log.e("Error: ", "Al buscar la lista de dispositivos.", e);
            AndroidUtils.showAlertMsg(context, "Notificación", "No se encontro ningun dispositivo bluetooth.");
            return;
        } catch (Exception ex) {
            Log.e("Error: ", "Al mostrar la lista de dispositivo.", ex);
            AndroidUtils.showAlertMsg(context, "Error", "Al mostrar la lista de dispositivos.");
            return;
        }

    }

    public void callPrinter() {

        //Bluetooth connection
        ConnectionBase conn = null;
        int row = 50;
        int col = 1;
        Looper.prepare();
        try {



            if (AppParameters.BLUETOOTH_PRINTER_MACADDRESS == null){

                chooseBluetoothDevice(true);

            } else {

                conn = Connection_Bluetooth.createClient(AppParameters.BLUETOOTH_PRINTER_MACADDRESS);
                conn.open();

                if (conn.getIsOpen()) {

                    DocumentEZ document = new DocumentEZ("MF204");

                    document.writeText(listadoTransacciones.getTitulo(), row, col);
                    row += 25;
                    document.writeText("--------------------------------------------", row, col);
                    row += 25;
                    document.writeText(listadoTransacciones.getSubTitulo(), row, col);
                    row += 25;
                    document.writeText("--------------------------------------------", row, col);
                    row += 25;
                    for (String str : listadoTransacciones.getDetalles()) {
                        document.writeText(str, row, col);
                        row += 25;
                    }
                    row += 25;
                    document.writeText("    ", row, col);
                    ParametersEZ parameter = new ParametersEZ();
                    parameter.setHorizontalMultiplier(3);
                    parameter.setVerticalMultiplier(3);

                    //PDF417
                    document.writeBarCodePDF417(listadoTransacciones.getBarcode(), row, col, 2, 1, parameter);

                    row += 150;
                    document.writeText("    ", row, col);
                    row += 150;
                    document.writeText("    ", row, col);

                    for (int i = 0; i < numberOfCopy; i++) {

                        conn.write(document.getDocumentData());
                        Thread.sleep(2000);

                    }

                } else {
                    Log.e("Conection Status", "La coneccion no pudo ser abierta!!!");
                    AndroidUtils.showAlertMsg(context, "Notificacion", "La coneccion con el dispositivo bluetooth no pudo ser abierta!");
                }
            }



        } catch (Exception e) {
            e.printStackTrace();
            AndroidUtils.showAlertMsg(context, "Notificacion", "Ocurrio un error al abrir la coneccion con el dispositivo bluetooth.");
        } finally {
            if ( conn != null ){
                try{
                    conn.close();
                } catch (Exception e ){
                    e.printStackTrace();
                    AndroidUtils.showAlertMsg(context, "Notificacion", "Ocurrio un error al cerrar la coneccion con el dispositivo bluetooth.");
                }
            }
            Looper.loop();
        }

    }
}