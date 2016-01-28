package com.cac.viewer;

import android.app.DatePickerDialog;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import com.cac.customcomponents.AbstractFragment;
import com.cac.tools.FormatReportData;
import com.cac.tools.MainComponentEdit;
import com.delacrmi.persistences.EntityManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.cac.sgc.MainActivity;
import com.cac.R;
import com.cac.pojos.ListadoTransacciones;
import com.cac.customcomponents.MyDialogDateListenerFactory;
import com.cac.customcomponents.TransaccionAdapter;

/**
 * Created by Legal on 04/10/2015.
 */
public class Listado extends AbstractFragment implements MainComponentEdit {

    public static final String TAG = "Listado";

    private EditText editFiltroPorFecha;
    private ImageButton generarReporte;
    private ListView listadoTransacciones;

    public Listado init(MainActivity context, EntityManager entityManager) {
        return (Listado) super.init(context,R.layout.listado,entityManager,TAG);
    }

    @Override
    public void initializeComponents() {
        editFiltroPorFecha      = (EditText) view.findViewById(R.id.editFiltroPorFecha);
        generarReporte          = (ImageButton) view.findViewById(R.id.generarReporte);
        listadoTransacciones    = (ListView) view.findViewById(R.id.listViewTransacciones);
    }

    @Override
    public void initializeMethods() {
        FormatReportData formatReportData = new FormatReportData(getContext(),getContext().getEntityManager());

        listadoTransacciones.setAdapter(new TransaccionAdapter(getContext(), formatReportData.formatAllTransactionOnDataBase()));

        editFiltroPorFecha.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    DatePickerDialog datePicker = new DatePickerDialog(getActivity(), R.style.AppTheme, new MyDialogDateListenerFactory(editFiltroPorFecha),
                            Calendar.getInstance().get(Calendar.YEAR),
                            Calendar.getInstance().get(Calendar.MONTH),
                            Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                    datePicker.show();
                }
            }
        });

        generarReporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filtrarPorFecha(editFiltroPorFecha.getText().toString());
            }
        });
    }

    private void filtrarPorFecha(String s) {
        FormatReportData formatReportData = new FormatReportData(getContext(),getContext().getEntityManager());

        List<ListadoTransacciones> listado = formatReportData.formatAllTransactionOnDataBase();
        if ( s != null && s.length() > 0 ) {
            List<ListadoTransacciones> informationList = new ArrayList<>();
            for (ListadoTransacciones obj : listado) {
                if (obj.getFecha().toLowerCase().contains(s))
                    informationList.add(obj);
            }
            listadoTransacciones.setAdapter(new TransaccionAdapter(getContext(), informationList));
        } else{
            listadoTransacciones.setAdapter(new TransaccionAdapter(getContext(),listado));
        }
    }

    //<editor-fold desc="Override Methods">
    @Override
    public void onClickFloating(View view) {
        switch (view.getId()) {
            case R.id.btn_fab_right:
                Snackbar.make(this.view, "Guardar.", Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.btn_fab_left:
                Snackbar.make(this.view, "Ir al formulario anterior.", Snackbar.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void MainViewConfig(List<View> views) {
        for ( View view : views){
            switch (view.getId()){
                case R.id.btn_fab_right:
                    view.setVisibility(View.INVISIBLE);
                    break;
                case R.id.btn_fab_left:
                    view.setVisibility(View.INVISIBLE);
                    break;
                case R.id.main_body_action_layout:
                    view.getLayoutParams().height = 0;
                    break;
            }
        }
    }

    @Override
    public String getTAG() {return TAG;}

    @Override
    public int getSubTitle() {return R.string.listado_sub_title;}
    //</editor-fold>

}