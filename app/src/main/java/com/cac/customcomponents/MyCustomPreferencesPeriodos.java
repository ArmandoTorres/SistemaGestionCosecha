package com.cac.customcomponents;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

import com.cac.entities.Periodos;
import com.cac.sgc.MainActivity;
import com.delacrmi.persistences.Entity;
import com.delacrmi.persistences.EntityManager;

import java.util.List;

/**
 * Created by Atorres on 16/11/2015.
 */
public class MyCustomPreferencesPeriodos extends ListPreference {

    private EntityManager entityManager;

    public MyCustomPreferencesPeriodos(Context context, AttributeSet attrs) {
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

    public MyCustomPreferencesPeriodos(Context context) {
        this(context,null);
    }

    @Override
    public CharSequence[] getEntries() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String defaultEmpresa = sharedPreferences.getString("EMPRESA","30");
        int index = 0;
        List<Entity> periodos = entityManager.find(Periodos.class,Periodos.DESCRIPCION,Periodos.ID_EMPRESA+" = ?", new String[]{defaultEmpresa});
        if ( periodos.size() > 0 ) {
            CharSequence[] charSequences = new CharSequence[periodos.size()];
            for (Entity entity : periodos) {
                charSequences[index++] = entity.getColumnValueList().getAsString(Periodos.DESCRIPCION);
            }
            return charSequences;
        }
        return new CharSequence[]{"No hay datos"};
    }

    @Override
    public CharSequence[] getEntryValues() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String defaultEmpresa = sharedPreferences.getString("EMPRESA","30");
        int index = 0;
        List<Entity> periodos = entityManager.find(Periodos.class,Periodos.ID_PERIODO,Periodos.ID_EMPRESA+" = ?", new String[]{defaultEmpresa});
        if ( periodos.size() > 0 ) {
            CharSequence[] charSequences = new CharSequence[periodos.size()];
            for ( Entity entity : periodos ) {
                charSequences[index++] = entity.getColumnValueList().getAsString(Periodos.ID_PERIODO);
            }
            return charSequences;
        }
        return new CharSequence[]{"0"};
    }
}