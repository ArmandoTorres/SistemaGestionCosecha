package com.cac.pojos;

import android.graphics.Bitmap;
import java.util.List;

/**
 * Listado de transaccion por envio.
 * Created by Legal on 20/10/2015.
 */
public class ListadoTransacciones {

    private String titulo;
    private String subTitulo;
    private String detalle;
    private String barcode;
    private Bitmap bmp;
    private String fecha;
    private String estado;
    private List<String> detalles;

    public ListadoTransacciones() {}

    public ListadoTransacciones(String titulo, String subTitulo, String detalle, String barcode) {
        this.subTitulo = subTitulo;
        this.titulo = titulo;
        this.detalle = detalle;
        this.barcode = barcode;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getSubTitulo() {
        return subTitulo;
    }

    public String getDetalle() {
        return detalle;
    }

    public String getBarcode() {
        return barcode;
    }

    public Bitmap getBmp() {
        return bmp;
    }

    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;
    }

    public String getFecha() {return fecha; }

    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getEstado() {return estado;}

    public void setEstado(String estado) {this.estado = estado;}

    public List<String> getDetalles() {return detalles;}

    public void setDetalles(List<String> detalles) {this.detalles = detalles;}

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setSubTitulo(String subTitulo) {
        this.subTitulo = subTitulo;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
}