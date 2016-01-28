package com.cac.customcomponents;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;

import com.cac.entities.Empresas;
import com.cac.sgc.MainActivity;
import com.delacrmi.persistences.Entity;
import com.delacrmi.persistences.EntityManager;

import java.util.List;

/**
 * Created by Atorres on 16/11/2015.
 */
public class MyCustomPreferencesEmpresas extends ListPreference {

    private EntityManager entityManager;

    public MyCustomPreferencesEmpresas(Context context, AttributeSet attrs) {
        super(context, attrs);

        if ( context instanceof MainActivity ) {
            entityManager = ((MainActivity) context).getEntityManager();
            setEntries(getEntries());
            setEntryValues(getEntryValues());
            setValueIndex(initializeIndex());
        }
    }

    private int initializeIndex() {
        return 0;
    }

    public MyCustomPreferencesEmpresas(Context context) {
        this(context,null);
    }

    @Override
    public CharSequence[] getEntries() {
        int index = 0;
        List<Entity> empresas = entityManager.find(Empresas.class,Empresas.DESCRIPCION,Empresas.ESTADO+" = ?",new String[]{Empresas.EstadoEmpresas.ACTIVA.toString()});
        if ( empresas.size() > 0 ) {
            CharSequence[] charSequences = new CharSequence[empresas.size()];
            for (Entity entity : empresas) {
                charSequences[index++] = entity.getColumnValueList().getAsString(Empresas.DESCRIPCION);
            }
            return charSequences;
        }
        return new CharSequence[]{"No hay datos"};
    }

    @Override
    public CharSequence[] getEntryValues() {
        int index = 0;
        List<Entity> empresas = entityManager.find(Empresas.class,Empresas.ID_EMPRESA,Empresas.ESTADO+" = ?",new String[]{Empresas.EstadoEmpresas.ACTIVA.toString()});
        if ( empresas.size() > 0 ) {
            CharSequence[] charSequences = new CharSequence[empresas.size()];
            for ( Entity entity : empresas ) {
                charSequences[index++] = entity.getColumnValueList().getAsString(Empresas.ID_EMPRESA);
            }
            return charSequences;
        }
        return new CharSequence[]{"0"};
    }
}