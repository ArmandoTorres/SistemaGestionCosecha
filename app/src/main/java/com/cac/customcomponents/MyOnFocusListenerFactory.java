package com.cac.customcomponents;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.delacrmi.persistences.Entity;
import com.delacrmi.persistences.EntityManager;

/**
 * Created by Legal on 13/10/2015.
 */
public class MyOnFocusListenerFactory implements View.OnFocusChangeListener {

    private View target;
    private EntityManager entityManager;
    private Class entity;
    private String campoDescripcion, campoFiltro;
    private Boolean showDescription;

    public MyOnFocusListenerFactory ( View target ) {
        this.target = target;
    }

    public MyOnFocusListenerFactory ( View target, EntityManager entityManager, Class entity, String campoDescripcion, String campoFiltro, Boolean showDescription) {
        if ( target == null || entityManager == null || entity == null || campoDescripcion == null || campoFiltro == null || campoFiltro.equals("")) {
            throw new NullPointerException("Todos los parametros del constructor son obligatorios.");
        }
        this.target = target;
        this.entityManager = entityManager;
        this.entity = entity;
        this.campoDescripcion = campoDescripcion;
        this.campoFiltro = campoFiltro;
        this.showDescription = showDescription;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        EditText campo = (EditText) v;

        if (!hasFocus) {
            if ( !validateFields(campo) ) {
                if (this.target != null && target instanceof TextView )
                    ((TextView) target).setText("");
            }
        }
    }

    private Boolean validateFields(EditText field) {
        if (field != null) {
            if ( field.getText().toString().trim().equals("") || field.getText().toString().trim().length() == 0 || field.getText().toString().trim().isEmpty() ) {
                field.setError("El campo "+field.getHint()+" es Requerido.");
                return false;
            }
            try {
                if (entityManager != null) {
                    Entity result = entityManager.findOnce(entity, campoDescripcion , campoFiltro + " = ?", new String[]{field.getText().toString()});
                    if (result != null && result.getColumnValueList().size() > 0) {
                        if (showDescription)
                            ((TextView) target).setText(result.getColumnValueList().getAsString(campoDescripcion));
                    } else {
                        if (showDescription)
                            ((TextView) target).setText("");
                        field.setError("El " + field.getHint() + " no se encuentra registrado en la base de datos.");
                        return false;
                    }
                }
            } catch ( Exception e ) {
                e.printStackTrace();
                Log.e("Error","Error al buscar el resultado: "+e.getMessage());
                return false;
            }
        }
        return true ;
    }

}