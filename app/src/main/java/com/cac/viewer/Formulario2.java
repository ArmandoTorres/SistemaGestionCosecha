package com.cac.viewer;

import android.app.DatePickerDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cac.customcomponents.AbstractFragment;
import com.cac.customcomponents.OnKeyListenerRefactory;
import com.cac.entities.EmpleadoCabezal;
import com.delacrmi.persistences.Entity;
import com.delacrmi.persistences.EntityManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.cac.sgc.MainActivity;
import com.cac.R;
import com.cac.entities.Vehiculos;
import com.cac.customcomponents.MyDialogDateListenerFactory;
import com.cac.customcomponents.MyOnFocusListenerFactory;

/**
 * Created by Legal on 04/10/2015.
 */
public class Formulario2 extends AbstractFragment {


    public static final String TAG = "Formulario2";
    private EditText fechaCorte, ordenQuema, txtDescConductorCabezal;
    private Spinner listaClaveCorte, listaFormaTiro;
    private AutoCompleteTextView autoCompleteListaCabezales, autoCompleteConductorCabezal;

    public Formulario2 init(MainActivity context, EntityManager entityManager) {
        return (Formulario2) super.init(context,R.layout.formulario2,entityManager,TAG);
    }

    //<editor-fold desc="Get's and Set's">
    public Date getFechaCorte() {
        try {
            SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
            Date date = formater.parse(fechaCorte.getText().toString());
            //return Long.toString(date.getTime());
            return date;
        } catch (ParseException e) {
            Log.e("Error", "Al convertir la fecha de corte", e);
            Toast.makeText(getContext(),"Error al convertir la fecha de corte.", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public String getOrdenQuema() {
        if ( ordenQuema == null )
            return "";
        return ordenQuema.getText().toString();
    }

    public String getTxtDescConductorCabezal() {
        if ( txtDescConductorCabezal == null )
            return "";
        return txtDescConductorCabezal.getText().toString();
    }

    public String getListaClaveCorte() {
        if ( listaClaveCorte == null )
            return "";
        return listaClaveCorte.getSelectedItem().toString();
    }

    public String getAutoCompleteListaCabezales() {
        if ( autoCompleteListaCabezales == null )
            return "";
        return autoCompleteListaCabezales.getText().toString();
    }

    public String getAutoCompleteConductorCabezal() {
        if ( autoCompleteConductorCabezal == null )
            return "";
        return autoCompleteConductorCabezal.getText().toString();
    }

    public String getListaFormaTiro() {
        if ( listaFormaTiro == null )
            return "";
        return listaFormaTiro.getSelectedItem().toString();
    }

    //</editor-fold>

    @Override
    public void initializeComponents() {
        ordenQuema = (EditText) view.findViewById(R.id.editTextOrdenQuema);
        fechaCorte = (EditText) view.findViewById(R.id.editTextFechaCorte);
        autoCompleteConductorCabezal = (AutoCompleteTextView) view.findViewById(R.id.conductorCabezal);
        txtDescConductorCabezal = (EditText) view.findViewById(R.id.txtDescConductorCabezal);
        layout = (RelativeLayout) view.findViewById(R.id.formulario2);
        autoCompleteListaCabezales = (AutoCompleteTextView) view.findViewById(R.id.codigoCabezal);
        listaClaveCorte = (Spinner) view.findViewById(R.id.listaClaveCorte);
        listaFormaTiro  = (Spinner) view.findViewById(R.id.listaFormaDeTiro);
    }

    @Override
    public void initializeMethods() {
        fechaCorte.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    DatePickerDialog datePicker = new DatePickerDialog(getActivity(),R.style.AppTheme,new MyDialogDateListenerFactory(fechaCorte),
                            Calendar.getInstance().get(Calendar.YEAR),
                            Calendar.getInstance().get(Calendar.MONTH),
                            Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                    datePicker.show();
                } else {
                    validateField(fechaCorte);
                }
            }
        });
        ordenQuema.setOnFocusChangeListener(new MyOnFocusListenerFactory(null));
        //fechaCorte.setText(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "/" + Calendar.getInstance().get(Calendar.MONTH) + "/" + Calendar.getInstance().get(Calendar.YEAR));
        fechaCorte.setText(new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime()));

        autoCompleteConductorCabezal.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (listaFormaTiro != null &&
                        (listaFormaTiro.getSelectedItem().toString().toLowerCase().equals("cabezal")
                                ||
                                listaFormaTiro.getSelectedItem().toString().toLowerCase().equals("mixto")
                        )) {
                    EditText field = (EditText) v;
                    if (!hasFocus) {
                        if (field.getText().toString().trim().equals("") || field.getText().toString().trim().length() == 0 || field.getText().toString().trim().isEmpty()) {
                            field.setError("El campo " + field.getHint() + " es Requerido.");
                        }
                        try {
                            if (getContext().getEntityManager() != null) {
                                Entity result = getContext().getEntityManager().findOnce(EmpleadoCabezal.class,
                                        EmpleadoCabezal.NOMBRE, EmpleadoCabezal.ID_EMPLEADO + " = ?",
                                        new String[]{field.getText().toString()});
                                if (result != null && result.getColumnValueList().size() > 0) {
                                    txtDescConductorCabezal.setText(result.getColumnValueList()
                                            .getAsString(EmpleadoCabezal.NOMBRE));
                                } else {
                                    txtDescConductorCabezal.setText("");
                                    field.setError("El " + field.getHint() + " no se encuentra registrado en la base de datos.");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("Error", "Error al buscar el resultado: " + e.getMessage());
                        }
                    }
                }
            }
        });

        autoCompleteConductorCabezal.setOnKeyListener(new OnKeyListenerRefactory(getInformation(
                EmpleadoCabezal.class,
                EmpleadoCabezal.ID_EMPLEADO + " key, " + EmpleadoCabezal.NOMBRE + " value",
                null, null), txtDescConductorCabezal) {
            @Override
            public void beforeOnkeyValidate() {
                if (listaFormaTiro != null &&
                        (listaFormaTiro.getSelectedItem().toString().toLowerCase().equals("cabezal")
                                ||
                                listaFormaTiro.getSelectedItem().toString().toLowerCase().equals("mixto")
                        )) {
                    setValidateField(true);
                } else {
                    setValidateField(false);
                }
            }
        });

        autoCompleteConductorCabezal.setAdapter(findCodigosEmpleados(EmpleadoCabezal.class));
        autoCompleteConductorCabezal.setThreshold(2);
        autoCompleteConductorCabezal.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listaFormaTiro != null &&
                        (listaFormaTiro.getSelectedItem().toString().toLowerCase().equals("cabezal")
                                ||
                                listaFormaTiro.getSelectedItem().toString().toLowerCase().equals("mixto")
                        )) {
                    autoCompleteConductorCabezal.getOnFocusChangeListener().onFocusChange(autoCompleteConductorCabezal, false);
                }
                getContext().hideKeyboard();
            }
        });
        autoCompleteListaCabezales.setAdapter(findCodigosVehiculos(Vehiculos.class, "B"));
        autoCompleteListaCabezales.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                autoCompleteConductorCabezal.requestFocus();
            }
        });
        autoCompleteListaCabezales.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (listaFormaTiro != null &&
                        (listaFormaTiro.getSelectedItem().toString().toLowerCase().equals("cabezal")
                                ||
                                listaFormaTiro.getSelectedItem().toString().toLowerCase().equals("mixto")
                        )) {
                    validateVehiculos(v, hasFocus, "B");
                    if (hasFocus && ((AutoCompleteTextView) v).getText().length() <= 1)
                        addInitValue((TextView) v, "B010" + ((AutoCompleteTextView) v).getText());
                }
            }
        });

        autoCompleteListaCabezales.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (((AutoCompleteTextView) v).getText().length() <= 1)
                    addInitValue((TextView) v, "B010" + ((AutoCompleteTextView) v).getText());
                return false;
            }
        });

        //Llenamos la lista en pantalla con las claves de corte
        String [] clavesCorte = this.getResources().getStringArray(R.array.clave_corte);
        List<String> listaClavesCorte = Arrays.asList(clavesCorte);
        if (listaClavesCorte != null && !listaClavesCorte.isEmpty() ){
            ArrayAdapter<String> adapterClaveCorte = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_selectable_list_item,listaClavesCorte);
            listaClaveCorte.setAdapter(adapterClaveCorte);
        }

        //Llenamos la lista en pantalla con las Forma de Tiro
        String [] formaDeTiro = this.getResources().getStringArray(R.array.forma_tiro);
        List<String> listaDeTiro = Arrays.asList(formaDeTiro);
        if ( listaDeTiro != null && !listaDeTiro.isEmpty()){
            ArrayAdapter<String> adapterFormaDeTiro = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_selectable_list_item,listaDeTiro);
            listaFormaTiro.setAdapter(adapterFormaDeTiro);
        }

        listaFormaTiro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    switch (parent.getItemAtPosition(position).toString().toLowerCase()) {
                        case "cabezal":
                        case "mixto":
                            autoCompleteListaCabezales.setEnabled(true);
                            autoCompleteListaCabezales.invalidate();
                            autoCompleteConductorCabezal.setEnabled(true);
                            autoCompleteConductorCabezal.invalidate();
                            txtDescConductorCabezal.setEnabled(true);
                            txtDescConductorCabezal.invalidate();
                            break;
                        case "directo":
                            autoCompleteListaCabezales.setText("");
                            autoCompleteListaCabezales.setEnabled(false);
                            autoCompleteListaCabezales.invalidate();
                            autoCompleteConductorCabezal.setText("");
                            autoCompleteConductorCabezal.setEnabled(false);
                            autoCompleteConductorCabezal.invalidate();
                            txtDescConductorCabezal.setText("");
                            txtDescConductorCabezal.setEnabled(false);
                            txtDescConductorCabezal.invalidate();
                            break;
                    }
                }catch (Exception ex) {}
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing
            }
        });
    }

    //<editor-fold desc="Override Methods">
    @Override
    public void onClickFloating(View view) {
        switch (view.getId()) {
            case R.id.btn_fab_right:
                if ( validateForm() )
                    getContext().startTransactionByFragmentTag(Formulario3.TAG);
                break;
            case R.id.btn_fab_left:
                getContext().startTransactionByFragmentTag(Formulario1.TAG);
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
    public int getSubTitle() {return R.string.formulario2_sub_title;}
    //</editor-fold>
}