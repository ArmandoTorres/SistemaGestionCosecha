package com.atorres;

import android.graphics.Bitmap;

public class PrinterObjectFormat {

    private BluetoothPrinterManager.PRINTER_FONT fontType;
    private BluetoothPrinterManager.PRINTER_OBJECT objectType;
    private BluetoothPrinterManager.PRINTER_FONT_SIZE fontSize;
    private boolean underline;
    private String text;
    private Bitmap bmp;

    public PrinterObjectFormat() {}

    public PrinterObjectFormat( BluetoothPrinterManager.PRINTER_FONT fontType, BluetoothPrinterManager.PRINTER_FONT_SIZE fontSize,  BluetoothPrinterManager.PRINTER_OBJECT objectType, boolean underline, String text ) {
        this.fontType = fontType;
        this.objectType = objectType;
        this.fontSize = fontSize;
        this.underline = underline;
        this.text = text;
    }

    public PrinterObjectFormat( BluetoothPrinterManager.PRINTER_OBJECT objectType, Bitmap bmp ) {
        this.objectType = objectType;
        this.bmp = bmp;
    }

    public BluetoothPrinterManager.PRINTER_FONT getFontType() {
        return fontType;
    }

    public void setFontType(BluetoothPrinterManager.PRINTER_FONT fontType) {
        this.fontType = fontType;
    }

    public BluetoothPrinterManager.PRINTER_OBJECT getObjectType() {
        return objectType;
    }

    public void setObjectType(BluetoothPrinterManager.PRINTER_OBJECT objectType) {
        this.objectType = objectType;
    }

    public String getText() {
        //Verificamos si el texto no contenga caracteres no permitidos.
        if ( text != null ) {
            text = text.replaceAll("ñ", "n");
            text = text.replaceAll("Ñ", "N");
        }
        return text;
    }

    public void setText(String text) {
        text = text;
    }

    public Bitmap getBmp() {
        return bmp;
    }

    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;
    }

    public BluetoothPrinterManager.PRINTER_FONT_SIZE getFontSize() {
        return fontSize;
    }

    public void setFontSize(BluetoothPrinterManager.PRINTER_FONT_SIZE fontSize) {
        this.fontSize = fontSize;
    }

    public boolean isUnderline() {        return underline;
    }

    public void setUnderline(boolean underline) {
        this.underline = underline;
    }
}