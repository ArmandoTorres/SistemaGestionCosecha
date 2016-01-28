package com.cac.customcomponents;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.cac.R;
import com.cac.entities.Transaccion;
import com.cac.pojos.ListadoTransacciones;
import com.cac.tools.PrinterManager;

import java.util.List;

/**
 * Created by Legal on 20/10/2015.
 */
public class TransaccionAdapter extends ArrayAdapter<ListadoTransacciones> {

    public TransaccionAdapter(Context context, List<ListadoTransacciones> objects) {
        super(context, R.layout.listado_card_view, objects);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View item = convertView;
        TransaccionViewHolder holder;
        final ListadoTransacciones listadoTransacciones = getItem(position);

        if ( item == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            item = inflater.inflate(R.layout.listado_card_view, null);

            holder = new TransaccionViewHolder();
            holder.title    = (TextView) item.findViewById(R.id.reportTitle);
            holder.btnPrint = (ImageButton) item.findViewById(R.id.listadoCardViewBtnPrint);
            holder.subTitle = (TextView) item.findViewById(R.id.reportSubTitle);
            holder.details  = (TextView) item.findViewById(R.id.reportDetails);
            holder.bmp      = (ImageView)item.findViewById(R.id.reportBarcodeImage);
            item.setTag(holder);
        } else
            holder = (TransaccionViewHolder) item.getTag();

        holder.subTitle.setText(listadoTransacciones.getSubTitulo());
        holder.details.setText(listadoTransacciones.getDetalle());
        holder.btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrinterManager printerManager = new PrinterManager(getContext(),listadoTransacciones,1);
                printerManager.printText();
            }
        });
        Bitmap bmp = listadoTransacciones.getBmp();
        if ( bmp != null )
            holder.bmp.setImageBitmap(bmp);

        if ( listadoTransacciones.getEstado().equals(Transaccion.TransaccionEstado.TRASLADADA.toString()) ){
            holder.title.setTextColor(Color.BLACK);
        } else {
            holder.title.setTextColor(Color.RED);
        }
        holder.title.invalidate();

        return item;
    }

    static class TransaccionViewHolder{
        TextView subTitle;
        TextView details;
        ImageView bmp;
        ImageButton btnPrint;
        TextView title;
    }
}