package com.cac.viewer;

import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cac.customcomponents.AbstractFragment;
import com.cac.customcomponents.OnKeyListenerRefactory;
import com.cac.entities.EmpleadoCosechadora;
import com.cac.entities.EmpleadoTractor;
import com.delacrmi.persistences.EntityManager;
import java.util.List;

import com.cac.sgc.MainActivity;
import com.cac.R;
import com.cac.entities.Empleados;
import com.cac.entities.Vehiculos;
import com.cac.customcomponents.MyOnFocusListenerFactory;

/**
 * Created by Legal on 04/10/2015.
 */
public class Formulario3 extends AbstractFragment {

    public static final String TAG = "Formulario3";
    private AutoCompleteTextView listaCodigoCarreta, listaCodigoCosechadora, listaCodigoTractor,
            conductorCosechadora, conductorTractor;
    private EditText txtDescConductorCosechadora, txtDescConductorTractor;

    //<editor-fold desc="Get's and Set's">
    public String getListaCodigoCarreta() {
        if ( listaCodigoCarreta == null )
            return "";
        return listaCodigoCarreta.getText().toString();
    }

    public String getListaCodigoCosechadora() {
        if ( listaCodigoCosechadora == null )
            return "";
        return listaCodigoCosechadora.getText().toString();
    }

    public String getListaCodigoTractor() {
        if ( listaCodigoTractor == null )
            return "";
        return listaCodigoTractor.getText().toString();
    }

    public String getConductorCosechadora() {
        if ( conductorCosechadora == null )
            return "";
        return conductorCosechadora.getText().toString();
    }

    public String getConductorTractor() {
        if ( conductorTractor == null )
            return "";
        return conductorTractor.getText().toString();
    }
    //</editor-fold>

    public Formulario3 init(MainActivity context, EntityManager entityManager) {
        return (Formulario3) super.init(context,R.layout.formulario3,entityManager,TAG);
    }

    @Override
    public void initializeComponents() {
        //Listas
        listaCodigoCarreta     = (AutoCompleteTextView) view.findViewById(R.id.listaCodigoCarreta);
        listaCodigoCosechadora = (AutoCompleteTextView) view.findViewById(R.id.listaCodigoCosechadora);
        listaCodigoTractor     = (AutoCompleteTextView) view.findViewById(R.id.listaCodigoTractor);
        //EditText
        conductorCosechadora         = (AutoCompleteTextView) view.findViewById(R.id.editTextConductorCosechadora);
        conductorTractor             = (AutoCompleteTextView) view.findViewById(R.id.editTextConductorTractor);
        txtDescConductorCosechadora  = (EditText) view.findViewById(R.id.txtDescConductorCosechadora);
        txtDescConductorTractor      = (EditText) view.findViewById(R.id.txtDescConductorTractor);
        layout                       = (RelativeLayout) view.findViewById(R.id.formulario3);
    }

    @Override
    public void initializeMethods() {
        conductorCosechadora.setOnFocusChangeListener(new MyOnFocusListenerFactory(txtDescConductorCosechadora,
                getContext().getEntityManager(),
                EmpleadoCosechadora.class,
                EmpleadoCosechadora.NOMBRE,
                Empleados.ID_EMPLEADO,true));

        conductorCosechadora.setOnKeyListener(new OnKeyListenerRefactory(
                        getInformation(EmpleadoCosechadora.class,
                                EmpleadoCosechadora.ID_EMPLEADO + " key, " + EmpleadoCosechadora.NOMBRE + " value",
                                null, null), txtDescConductorCosechadora)
        );

        conductorCosechadora.setAdapter(findCodigosEmpleados(EmpleadoCosechadora.class));
        conductorCosechadora.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                conductorCosechadora.getOnFocusChangeListener().onFocusChange(conductorCosechadora, false);
                listaCodigoTractor.requestFocus();
            }
        });

        conductorTractor.setOnFocusChangeListener(new MyOnFocusListenerFactory(txtDescConductorTractor,
                getContext().getEntityManager(), EmpleadoTractor.class,
                EmpleadoTractor.NOMBRE,
                EmpleadoTractor.ID_EMPLEADO, true));

        conductorTractor.setOnKeyListener(new OnKeyListenerRefactory(
                        getInformation(EmpleadoTractor.class,
                                EmpleadoTractor.ID_EMPLEADO + " key, " + EmpleadoTractor.NOMBRE + " value",
                                null, null), txtDescConductorTractor)
        );
        conductorTractor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getContext().hideKeyboard();
                conductorTractor.getOnFocusChangeListener().onFocusChange(conductorTractor, false);
            }
        });
        conductorTractor.setAdapter(findCodigosEmpleados(EmpleadoTractor.class));

        //------------------------------------------------------------------------------------------
        //                        Listado Codigo Carreta Metodos
        //------------------------------------------------------------------------------------------
        listaCodigoCarreta.setAdapter(findCodigosVehiculos(Vehiculos.class, "C"));
        listaCodigoCarreta.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listaCodigoCosechadora.requestFocus();
            }
        });
        listaCodigoCarreta.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                validateVehiculos(v, hasFocus, "C");
                if (hasFocus && ((AutoCompleteTextView) v).getText().length() <= 1)
                    addInitValue((TextView) v, "C030" + ((AutoCompleteTextView) v).getText());
            }
        });
        listaCodigoCarreta.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (((AutoCompleteTextView) v).getText().length() <= 1)
                    addInitValue((TextView) v, "C030" + ((AutoCompleteTextView) v).getText());
                return false;
            }
        });
        //------------------------------------------------------------------------------------------
        //                        Fin Listado Codigo Carreta Metodos
        //------------------------------------------------------------------------------------------

        //------------------------------------------------------------------------------------------
        //                        Listado Codigo Cosechadora Metodos
        //------------------------------------------------------------------------------------------
        listaCodigoCosechadora.setAdapter(findCodigosVehiculos(Vehiculos.class, "D"));
        listaCodigoCosechadora.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                conductorCosechadora.requestFocus();
            }
        });
        listaCodigoCosechadora.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                validateVehiculos(v, hasFocus, "D");
                if (hasFocus && ((AutoCompleteTextView) v).getText().length() <= 1)
                    addInitValue((TextView) v, "D020" + ((AutoCompleteTextView) v).getText());
            }
        });
        listaCodigoCosechadora.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (((AutoCompleteTextView) v).getText().length() <= 1)
                    addInitValue((TextView) v, "D020" + ((AutoCompleteTextView) v).getText());
                return false;
            }
        });
        //------------------------------------------------------------------------------------------
        //                        Fin Listado Codigo Cosechadora Metodos
        //------------------------------------------------------------------------------------------

        //------------------------------------------------------------------------------------------
        //                        Listado Codigo Tractor Metodos
        //------------------------------------------------------------------------------------------
        listaCodigoTractor.setAdapter(findCodigosVehiculos(Vehiculos.class, "A"));
        listaCodigoTractor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                conductorTractor.requestFocus();
            }
        });
        listaCodigoTractor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                validateVehiculos(v, hasFocus, "A");

            }
        });
        listaCodigoTractor.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                String values;
                try{
                    if (((AutoCompleteTextView) v).getText().length() == 1 &&
                            Integer.parseInt(((AutoCompleteTextView) v).getText()+"") != 2){
                        values = ((AutoCompleteTextView) v).getText()+"";
                        ((AutoCompleteTextView) v).setText("");

                        if(Integer.parseInt(values) <= 1) ((AutoCompleteTextView) v).append("A180" + values);
                        else if(Integer.parseInt(values) <= 3) ((AutoCompleteTextView) v).append("A090" + values);

                    }else if(((AutoCompleteTextView) v).getText().length() == 2){
                        values = ((AutoCompleteTextView) v).getText()+"";
                        ((AutoCompleteTextView) v).setText("");

                        if(Integer.parseInt(values) > 20) ((AutoCompleteTextView) v).append("A090" + values);
                        else ((AutoCompleteTextView) v).append("A180" + values);
                    }
                }catch (NumberFormatException e){
                    ((AutoCompleteTextView) v).setText("");
                    return true;
                }
                return false;
            }
        });
        //------------------------------------------------------------------------------------------
        //                        Fin Listado Codigo Tractor Metodos
        //------------------------------------------------------------------------------------------
    }

    //<editor-fold desc="Override Methods">
    @Override
    public void onClickFloating(View view) {
        switch (view.getId()) {
            case R.id.btn_fab_right:
                if ( validateForm() )
                    getContext().startTransactionByFragmentTag(Formulario4.TAG);
                break;
            case R.id.btn_fab_left:
                getContext().startTransactionByFragmentTag(Formulario2.TAG);
                break;
        }
    }

    @Override
    public void MainViewConfig(List<View> views) {
        for ( View view : views){
            switch (view.getId()){
                case R.id.btn_fab_right:
                    view.setVisibility(View.VISIBLE);
                    ((ImageButton) view).setImageResource(R.drawable.siguiente);
                    break;
                case R.id.btn_fab_left:
                    view.setVisibility(View.VISIBLE);
                    ((ImageButton) view).setImageResource(R.drawable.anterior);
                    break;
                case R.id.main_body_action_layout:
                    view.getLayoutParams().height = MainActivity.VISIBLE_ACTION;
                    break;
            }
        }
    }

    @Override
    public String getTAG() {return TAG;}

    @Override
    public int getSubTitle() {return R.string.formulario3_sub_title;}
    //</editor-fold>
}