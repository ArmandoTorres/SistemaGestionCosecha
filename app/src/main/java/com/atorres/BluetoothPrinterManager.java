package com.atorres;

import android.bluetooth.BluetoothAdapter;
import it.custom.printer.api.android.*;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Legal on 28/10/2015.
 */
public class BluetoothPrinterManager {

    //ID del dispositivo elegido.
    private BluetoothDeviceSelected bluetoothDeviceSelected = null;

    //Lista de dispositivos bluetooth en disponibles.
    private static BluetoothDevice[] bluetoothDeviceList = null;

    //Context donde se ejecuta la app.
    private Context context;

    //Adaptador usuado para conocer el estatus del bluetooth
    private BluetoothAdapter bluetoothAdapter;

    //Printer
    private static CustomPrinter printer = null;

    private String lock="lockAccess";

    static Handler handler = new Handler();

    private int STATUS_TIME = 10000;

    public BluetoothPrinterManager(Context context) {
        if ( context == null )
            throw new NullPointerException("All constructor parameter's are required.");

        //Api version
        String apiVersion = CustomAndroidAPI.getAPIVersion();

        Log.i("Api version","BluetoothTestApp: "+apiVersion);

        //Verificamos el status del printer.
        //handler.postDelayed(GetStatusRunnable, STATUS_TIME);

        this.context = context;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        init();
    }

    /**
     * Metodo utilizado para retornar un ArrayAdapter para formar la lista de dispositivos bluetooth
     * que actualmente estan apareados al dispositivo.
     * @return ArrayAdapter<String>
     * */
    public ArrayAdapter<String> getBluetoothDevices() {
        if ( bluetoothDeviceList == null )
            init();

        if ( bluetoothDeviceList != null ) {
            //Llenamos la lista de dispositivos en la pantalla.
            ArrayList<String> listadoDispositivos = new ArrayList<>();
            for (int i = 0; i < bluetoothDeviceList.length; i++) {
                listadoDispositivos.add(bluetoothDeviceList[i].getName());
            }
            return new ArrayAdapter<>(context, android.R.layout.simple_list_item_single_choice, listadoDispositivos);
        }
        return null;
    }

    /**
     * Metodo que permiti iniciar los objetos necesarios para interactuar con el dispositivo.
     * */
    private void init() {
        //Cargamos la lista de dispositivos bluetooth disponibles.
        try {
            if ( isBluetoothEnabled() ) {
                bluetoothDeviceList = CustomAndroidAPI.EnumBluetoothDevices();
                if ( bluetoothDeviceList == null || bluetoothDeviceList.length == 0 ) {
                    AndroidUtils.showAlertMsg(context, "Notificacion", "No se encontro ningun dispositivo bluetooth.");
                }
            }
        } catch (CustomException e) {
            AndroidUtils.showAlertMsg(context, "Notificacion", "No se encontro ningun dispositivo bluetooth.");
            Log.e("Error","Error al buscar el dispositivo.",e);
        }
    }

    public boolean isBluetoothEnabled () {
        if ( bluetoothAdapter == null || !bluetoothAdapter.isEnabled() ) {
            AndroidUtils.showAlertMsg(context,"Notificación","Debe encender el bluetooth del dispositivo.");
            return false;
        }
        return true;
    }

    public boolean isDeviceSelected() {
        return bluetoothDeviceSelected != null;
    }

    public void setBluetoothDeviceSelected(int position) {
        if ( bluetoothDeviceList != null && position >= 0 ){
            BluetoothDeviceSelected btDeviceSelected = new BluetoothDeviceSelected();
            btDeviceSelected.setDeviceId(position);
            btDeviceSelected.setDeviceName(bluetoothDeviceList[position].getName());
            btDeviceSelected.setBluetoothDevice(bluetoothDeviceList[position]);
            this.bluetoothDeviceSelected = btDeviceSelected;
        }
    }

    private boolean isOpenDevice(){

        //Se verifica primero si el usuario seleccion algun printer.
        if ( this.bluetoothDeviceSelected == null){
            AndroidUtils.showAlertMsg(this.context,"Notificación.","No existe ningun dispositivo seleccionado.");
            return false;
        }

        // Si el dispositivo aun no esta abierto.
        if ( printer == null ) {
            try{
                printer = new CustomAndroidAPI().getPrinterDriverBT(this.bluetoothDeviceSelected.getBluetoothDevice());
            }catch ( CustomException e ) {
                AndroidUtils.showAlertMsg(this.context,"Error","Ocurrio un error a la hora de abrir el dispositivo.");
                Log.e("Error","Error Al abrir el dispositivo",e);
                return false;
            } catch (Exception ex) {
                Log.e("Error","Ocurrio un error al abrir el dispositivo.",ex);
                return false;
            }
        }

        return true;
    }

    public void print( List<PrinterObjectFormat> objectList) {

        if ( objectList != null ) {
            //Recorremos el List para conocer que se va imprimir.
            for ( PrinterObjectFormat a : objectList) {
                switch (a.getObjectType()){
                    case TEXT:
                        printText(a);
                        break;
                    case PICTURE:
                        printPicture(a.getBmp());
                        break;
                }
            }
        }

    }

    private void printPicture(final Bitmap bmp) {

        if ( bmp != null && isOpenDevice()) {

            Thread thread1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (printer != null) {
                            printer.printImage(bmp, CustomPrinter.IMAGE_ALIGN_TO_CENTER, CustomPrinter.IMAGE_SCALE_TO_FIT, 0);
                        }
                    } catch (Exception ex) {
                        AndroidUtils.showAlertMsg(context, "Error", "Al imprimir la imagen.");
                        Log.e("Error", "Ocurrio un error al imprimir la imagen.", ex);
                    }
                }
            });
            thread1.start();
        }
    }

    private void printText(PrinterObjectFormat printerObjectFormat) {

        if ( printerObjectFormat.getText() != null && !printerObjectFormat.getText().isEmpty() && !printerObjectFormat.getText().equals("")) {
            PrinterFont textFormat = new PrinterFont();

            if (isOpenDevice() == false)
                return;

            try {

                switch (printerObjectFormat.getFontSize()){
                    case ONE:
                        textFormat.setCharHeight(PrinterFont.FONT_SIZE_X1);
                        textFormat.setCharWidth(PrinterFont.FONT_SIZE_X1);
                        break;
                    case TWO:
                        textFormat.setCharHeight(PrinterFont.FONT_SIZE_X2);
                        textFormat.setCharWidth(PrinterFont.FONT_SIZE_X2);
                        break;
                    case THREE:
                        textFormat.setCharHeight(PrinterFont.FONT_SIZE_X3);
                        textFormat.setCharWidth(PrinterFont.FONT_SIZE_X3);
                        break;
                    case FOUR:
                        textFormat.setCharHeight(PrinterFont.FONT_SIZE_X4);
                        textFormat.setCharWidth(PrinterFont.FONT_SIZE_X4);
                        break;
                    case FIVE:
                        textFormat.setCharHeight(PrinterFont.FONT_SIZE_X5);
                        textFormat.setCharWidth(PrinterFont.FONT_SIZE_X5);
                        break;
                    case SIX:
                        textFormat.setCharHeight(PrinterFont.FONT_SIZE_X6);
                        textFormat.setCharWidth(PrinterFont.FONT_SIZE_X6);
                        break;
                    case SEVEN:
                        textFormat.setCharHeight(PrinterFont.FONT_SIZE_X7);
                        textFormat.setCharWidth(PrinterFont.FONT_SIZE_X7);
                        break;
                    case EIGHT:
                        textFormat.setCharHeight(PrinterFont.FONT_SIZE_X8);
                        textFormat.setCharWidth(PrinterFont.FONT_SIZE_X8);
                        break;
                }

                if (printerObjectFormat.getFontType() != null) {
                    textFormat.setEmphasized(printerObjectFormat.getFontType() == PRINTER_FONT.BOLD);
                }
                textFormat.setItalic(false);
                textFormat.setUnderline(printerObjectFormat.isUnderline());
                textFormat.setJustification(PrinterFont.FONT_JUSTIFICATION_CENTER);
                textFormat.setInternationalCharSet(PrinterFont.FONT_CS_DEFAULT);

            } catch (CustomException e) {
                AndroidUtils.showAlertMsg(context, "Error", "Ocurrio un error al pasar la fuente.");
            }

            synchronized (lock) {
                try {
                    //Titulo del reporte.
                    printer.printTextLF(printerObjectFormat.getText(), textFormat);
                } catch (CustomException ce) {
                    AndroidUtils.showAlertMsg(context, "Error", "Ocurrio un error al imprimir el texto.");
                    Log.e("Error", "Error al imprimir. " + printerObjectFormat.getText(), ce);
                } catch (Exception ex) {
                    AndroidUtils.showAlertMsg(context, "Error", "Ocurrio un error al imprimir el texto.");
                    Log.e("Error", "Error al imprimir. " + printerObjectFormat.getText(), ex);
                }
            }
        }
    }

    private Runnable GetStatusRunnable = new Runnable() {
        @Override
        public void run() {
            if ( printer != null ) {
                synchronized (lock){
                    try{
                        PrinterStatus printerStatus = printer.getPrinterFullStatus();
                        Log.i("PrinterStatus","Out of paper: "+printerStatus.stsNOPAPER);
                        Log.i("PrinterStatus","Paper Rolling: "+printerStatus.stsPAPERROLLING);
                        Log.i("PrinterStatus","LF key pressed: "+printerStatus.stsLFPRESSED);
                        Log.i("PrinterStatus","Printer name: "+printer.getPrinterName());
                        Log.i("PrinterStatus","Printer Info: "+printer.getPrinterInfo());
                    }catch (Exception ex){

                    }
                }
            }
            //Ejecutamos de nuevo el evento despues del tiempo
            handler.postDelayed(GetStatusRunnable,STATUS_TIME);
        }
    };

    public String getBluetoothMacAdreess() {
        if ( bluetoothDeviceSelected != null){
            return bluetoothDeviceSelected.getBluetoothDevice().getAddress();
        }
        return null;
    }

    private class BluetoothDeviceSelected {

        private int deviceId;
        private String deviceName;
        private BluetoothDevice bluetoothDevice;

        public int getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(int deviceId) {
            this.deviceId = deviceId;
        }

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public BluetoothDevice getBluetoothDevice() {
            return bluetoothDevice;
        }

        public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
            this.bluetoothDevice = bluetoothDevice;
        }
    }

    public enum PRINTER_FONT{
        BOLD, NORMAL
    }

    public enum PRINTER_OBJECT{
        TEXT,PICTURE
    }

    public enum PRINTER_FONT_SIZE {
        ONE,TWO,THREE, FOUR, FIVE, SIX, SEVEN, EIGHT
    }
}
