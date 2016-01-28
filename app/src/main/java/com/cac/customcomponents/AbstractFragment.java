package com.cac.customcomponents;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cac.R;
import com.cac.entities.Empleados;
import com.cac.entities.Vehiculos;
import com.cac.sgc.MainActivity;
import com.cac.tools.MainComponentEdit;
import com.delacrmi.persistences.Entity;
import com.delacrmi.persistences.EntityManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ATORRES on 10/11/2015.
 *
 * Clase creada para reducir la diplicidad del codigo, manejando los metodos mas comunes que
 * debe de manejar cada formulario.
 *
 */
public abstract class AbstractFragment extends Fragment implements MainComponentEdit {

    //Instance variables.
    public RelativeLayout layout;
    public View view;
    private Context context;
    private EntityManager entityManager;
    public int formID;
    public static String TAG = "";

    public AbstractFragment() {
    }

    /**
     * Este metodo es necesario para iniciar el correcto funcionamiento
     * del objeto creado.
     *
     * @param context contexto principal
     * @param formID Formulario a inflar en el layout.
     * @param entityManager Clase que permite el manejo de la BD.
     * */
    public AbstractFragment init (Context context, int formID, EntityManager entityManager, String TAG){
        this.context = context;
        this.formID  = formID;
        this.entityManager = entityManager;
        this.TAG = TAG;
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(formID, container, false);
        initializeComponents();
        initializeMethods();
        return view;
    }

    /**
     * Metodo utilizado para enlazar los componentes.
     * */
    public abstract void initializeComponents();

    /**
     * Metodo que se debe utilizar para agregar los metodos a los componentes.
     * */
    public abstract void initializeMethods();

    /**
     * Metodo utilizado para conseguir la informacion de la DB.
     * @return Key, value con le nombre del campo y el valor.
     * @param entityName Nombre de la entidad a buscar.
     * @param columns String con las columnas a buscar en formato columna1, columna2, ...
     * @param where Condicion de busqueda. Ejemplo Campo1 = ? || Campo1 = :p1
     * @param whereValues Valores de los parametor del where en el mismo orden en que fueron
     *                    ingresados.
     * */
    public Map<String,String> getInformation(Class entityName,String columns,String where, String[] whereValues) {
        Map<String,String> values = new HashMap<String,String>();
        List<Entity> entities = entityManager.find(entityName, columns, where, whereValues);
        for(Entity entity: entities){
            values.put(entity.getColumnsFromSelect().getAsString("key"),
                    entity.getColumnsFromSelect().getAsString("value"));
        }
        return values;
    }

    /**
     * Metodo que permite buscar los vehiculos en el formato B01001 el cual es el formato
     * utilizado en le CAC.
     *
     * @param entidad Entity.class que indica la tabla donde se va a buscar.
     * @param codigoGrupo letra que identifica que codigo de vehiculo se debe de utilizar
     *                    para filtrar los registros.
     * @return Adapter que contiene los codigo del vehiculos a mostrar.
     * */
    public ArrayAdapter<String> findCodigosVehiculos(Class entidad, String codigoGrupo ) {

        List<Entity> entidades =  entityManager.find(entidad, "*", Vehiculos.CODIGO_GRUPO + " = '" + codigoGrupo + "' and " + Vehiculos.STATUS + " = 1", null);
        List<String> listado = new ArrayList<>();
        for ( Entity a : entidades ){
            String descripcion = a.getColumnValueList().getAsString(Vehiculos.CODIGO_GRUPO);
            descripcion += String.format("%02d", Integer.parseInt(a.getColumnValueList().getAsString(Vehiculos.CODIGO_SUBGRUPO)));
            descripcion += String.format("%03d", Integer.parseInt(a.getColumnValueList().getAsString(Vehiculos.CODIGO_VEHICULO)));
            listado.add(descripcion);
        }

        if ( listado != null && !listado.isEmpty() ) {
            Collections.sort(listado);
            return new ArrayAdapter<>(context, android.R.layout.simple_selectable_list_item, listado);
        }else
            return new ArrayAdapter<>(context, android.R.layout.simple_selectable_list_item, new ArrayList<String>());
    }

    /**
     * Metodo que permite buscar los vehiculos en el formato B01001 el cual es el formato
     * utilizado en le CAC.
     *
     * @param entidad Entity.class que indica la tabla donde se va a buscar.
     * @param codigoGrupo letra que identifica que codigo de vehiculo se debe de utilizar
     *                    para filtrar los registros.
     * @return list que contiene los codigo del vehiculos a mostrar.
     * */
    public List<String> findListadoVehiculos( Class entidad, String codigoGrupo ) {

        List<Entity> entidades =  entityManager.find(entidad, "*", Vehiculos.CODIGO_GRUPO + " = '" + codigoGrupo + "' and " + Vehiculos.STATUS + " = 1", null);
        List<String> listado = new ArrayList<>();
        for ( Entity a : entidades ){
            String descripcion = a.getColumnValueList().getAsString(Vehiculos.CODIGO_GRUPO);
            descripcion += String.format("%02d", Integer.parseInt(a.getColumnValueList().getAsString(Vehiculos.CODIGO_SUBGRUPO)));
            descripcion += String.format("%03d", Integer.parseInt(a.getColumnValueList().getAsString(Vehiculos.CODIGO_VEHICULO)));
            listado.add(descripcion);
        }

        if ( listado != null && !listado.isEmpty() ) {
            Collections.sort(listado);
            return listado;
        }else
            return new ArrayList<String>();
    }


    /**
     * Metodo utilizado paara buscar los codigos de empleados para ser mostrados en una lista.
     *
     * @return listado de codigos de empleados.
     * @param entity nombre de la entidad en la cual se va a buscar la data.
     * */
    public ArrayAdapter<String> findCodigosEmpleados(Class entity) {

        List<Entity> entities = entityManager.find(entity, "*", null, null);
        List<String> list = new ArrayList<>();
        for (Entity a : entities ){
            list.add(a.getColumnValueList().getAsString(Empleados.ID_EMPLEADO));
        }
        if ( list != null && !list.isEmpty() ){
            Collections.sort(list);
            return new ArrayAdapter<String>(context,android.R.layout.simple_selectable_list_item,list);
        } else
            return new ArrayAdapter<String>(context,android.R.layout.simple_selectable_list_item,new ArrayList<String>());
    }


    /**
     * Metodo que permite validar todos los edittext que existen en el formulario para idenficar
     * si son validos o no.
     *
     * @return True: en caso de que el formulario este valido. False: En caso contrario.
     * */
    public boolean validateForm() {
        if ( layout != null ){
            for (View a : layout.getFocusables(RelativeLayout.FOCUS_BACKWARD)){
                if ( a instanceof EditText) {
                    EditText editText = (EditText) a;
                    View.OnFocusChangeListener onFocusChangeListener = a.getOnFocusChangeListener();
                    if ( onFocusChangeListener != null )
                        onFocusChangeListener.onFocusChange(editText,false);

                    if ( editText.getError() != null ){
                        Snackbar.make(this.view, getResources().getString(R.string.required_fields) + editText.getHint(), Snackbar.LENGTH_SHORT).show();
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Metodo que permite limpiar todo los componentes editables de la pantalla.
     * */
    public void cleanAllFields() {
        if (layout != null ){
            for ( int index = 0; index < layout.getChildCount();index++){
                View view = layout.getChildAt(index);
                if ( view instanceof TextView ){
                    TextView txt = (TextView) view;
                    txt.setText("");
                } else if ( view instanceof EditText ) {
                    EditText text = (EditText) view;
                    text.setText("");
                }
            }
        }
    }

    @Override
    public MainActivity getContext() {
        return (MainActivity)context;
    }

    public boolean validateField(EditText field){
        if (field != null){
            if ( field.getText().toString().trim().equals("") || field.getText().toString().trim().length() == 0 || field.getText().toString().trim().isEmpty() ) {
                field.setError("Campo Requerido");
                return false;
            }
        }
        return true;
    }

    public void validateVehiculos(View v, Boolean hasFocus, String codigoGrupo){
        EditText field = (EditText) v;
        if (!hasFocus) {
            if (field.getText().toString().trim().equals("") || field.getText().toString().trim().length() == 0 || field.getText().toString().trim().isEmpty()) {
                field.setError("El campo " + field.getHint() + " es Requerido.");
            }
            if (getContext().getEntityManager() != null) {
                if ( !findListadoVehiculos(Vehiculos.class,codigoGrupo).contains(field.getText().toString()) ) {
                    field.setError("El " + field.getHint() + " no se encuentra registrado en la base de datos.");
                }
            }
        }
    }

    public void addInitValue(TextView t, String value){
        t.append(value);
    }
}