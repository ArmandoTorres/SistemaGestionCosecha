package com.cac.viewer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.cac.R;
import com.cac.customcomponents.AbstractFragment;
import com.cac.customcomponents.OnKeyListenerRefactory;
import com.delacrmi.persistences.Entity;
import com.delacrmi.persistences.EntityManager;
import com.cac.sgc.MainActivity;
import com.cac.entities.Caniales;
import com.cac.entities.Fincas;
import com.cac.entities.Frentes;
import com.cac.entities.Lotes;
import com.cac.customcomponents.MyOnFocusListenerFactory;
import java.util.List;

/**
 * Created by Legal on 04/10/2015.
 */
public class Formulario1 extends AbstractFragment {

    public static final String TAG = "Formulario1";
    private EditText fteCorte, fteAlce, finca, canial, lote, txtDescFteCorte, txtDescFteAlce, txtDescFinca, txtDescCanial, txtDescLote;

    public Formulario1 init(MainActivity context, EntityManager entityManager) {
        return (Formulario1) super.init(context,R.layout.formulario1,entityManager,TAG);
    }

    //<editor-fold desc="Get's and Set's">
    public String getFteCorte() {
        if ( fteCorte == null )
            return "";

        return fteCorte.getText().toString();
    }

    public String getFteAlce() {
        if ( fteAlce == null )
            return "";

        return fteAlce.getText().toString();
    }

    public String getFinca() {
        if ( finca == null )
            return "";

        return finca.getText().toString();
    }

    public String getCanial() {
        if ( canial == null )
            return "";

        return canial.getText().toString();
    }

    public String getLote() {
        if ( lote == null )
            return "";

        return lote.getText().toString();
    }

    //</editor-fold>

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void initializeComponents() {
        fteCorte = (EditText) view.findViewById(R.id.editTextFrenteCorte);
        txtDescFteCorte = (EditText) view.findViewById(R.id.txtDescFteCorte);
        fteAlce = (EditText) view.findViewById(R.id.editTextFteAlce);
        txtDescFteAlce = (EditText) view.findViewById(R.id.txtDescFteAlce);
        finca = (EditText) view.findViewById(R.id.editTextFinca);
        txtDescFinca = (EditText) view.findViewById(R.id.txtDescFinca);
        canial = (EditText) view.findViewById(R.id.editTextCanial);
        txtDescCanial = (EditText) view.findViewById(R.id.txtDescCanial);
        lote = (EditText) view.findViewById(R.id.editTextlote);
        txtDescLote = (EditText) view.findViewById(R.id.txtDescLote);
        layout = (RelativeLayout) view.findViewById(R.id.formulario1);
    }

    @Override
    public void initializeMethods() {
        fteCorte.setOnFocusChangeListener(new MyOnFocusListenerFactory(txtDescFteCorte,
                getContext().getEntityManager(), Frentes.class, Frentes.DESCRIPCION, Frentes.ID_FRENTE, true));
        fteCorte.setOnKeyListener(new OnKeyListenerRefactory(getInformation(
                Frentes.class, Frentes.ID_FRENTE + " key, " + Frentes.DESCRIPCION + " value", null, null), txtDescFteCorte));

        fteAlce.setOnFocusChangeListener(new MyOnFocusListenerFactory(txtDescFteAlce,
                getContext().getEntityManager(), Frentes.class, Frentes.DESCRIPCION, Frentes.ID_FRENTE,true));
        fteAlce.setOnKeyListener(new OnKeyListenerRefactory(getInformation(
                Frentes.class, Frentes.ID_FRENTE + " key, " + Frentes.DESCRIPCION + " value", null, null), txtDescFteAlce));

        finca.setOnFocusChangeListener(new MyOnFocusListenerFactory(txtDescFinca,
                getContext().getEntityManager(), Fincas.class, Fincas.DESCRIPCION, Fincas.ID_FINCA, true));
        finca.setOnKeyListener(new OnKeyListenerRefactory(getInformation(
                Fincas.class, Fincas.ID_FINCA + " key, " + Fincas.DESCRIPCION + " value", null, null), txtDescFinca));

        canial.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText field = (EditText) v;
                EntityManager entityManager = getContext().getEntityManager();
                if (!hasFocus) {
                    if (field.getText().toString().trim().equals("") || field.getText().toString().trim().length() == 0 || field.getText().toString().trim().isEmpty()) {
                        field.setError("El campo " + field.getHint() + " es Requerido.");
                    }
                    if (getContext().getEntityManager() != null) {
                        Entity result = entityManager.findOnce(Caniales.class, "*",
                                Caniales.ID_FINCA + " = ? and " + Caniales.ID_CANIAL + " = ? ",
                                new String[]{finca.getText().toString(), canial.getText().toString()});
                        if (result != null && result.getColumnValueList().size() > 0) {
                            txtDescCanial.setText(result.getColumnValueList().getAsString(Caniales.DESCRIPCION));
                        } else {
                            txtDescCanial.setText("");
                            field.setError("El " + field.getHint() + " no se encuentra registrado en la base de datos.");
                        }
                    }
                }
            }
        });
        canial.setOnKeyListener(new OnKeyListenerRefactory(txtDescCanial) {
            @Override
            public void beforeOnkeyValidate() {
                setMapValues(getInformation(
                        Caniales.class, Caniales.ID_CANIAL + " key, " + Caniales.DESCRIPCION + " value",
                        Caniales.ID_FINCA + " = ? ", new String[]{finca.getText().toString()}));
            }
        });

        lote.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText field = (EditText) v;
                EntityManager entityManager = getContext().getEntityManager();
                if (!hasFocus) {
                    if (field.getText().toString().trim().equals("") || field.getText().toString().trim().length() == 0 || field.getText().toString().trim().isEmpty()) {
                        field.setError("El campo " + field.getHint() + " es Requerido.");
                    }
                    if (getContext().getEntityManager() != null) {
                        Entity result = entityManager.findOnce(Lotes.class, "*",
                                Lotes.ID_FINCA + " = ? and " + Lotes.ID_CANIAL + " = ? and " + Lotes.ID_LOTE + " = ?",
                                new String[]{finca.getText().toString(), canial.getText().toString(), lote.getText().toString()});
                        if (result != null && result.getColumnValueList().size() > 0) {
                            txtDescLote.setText(result.getColumnValueList().getAsString(Lotes.DESCRIPCION));
                        } else {
                            txtDescLote.setText("");
                            field.setError("El " + field.getHint() + " no se encuentra registrado en la base de datos.");
                        }
                    }
                }
            }
        });
        lote.setOnKeyListener(new OnKeyListenerRefactory(txtDescLote) {
            @Override
            public void beforeOnkeyValidate() {
                setMapValues(getInformation(
                        Lotes.class, Lotes.ID_LOTE + " key, " + Lotes.DESCRIPCION + " value",
                        Lotes.ID_FINCA + " = ? and " + Lotes.ID_CANIAL + " = ? ",
                        new String[]{finca.getText().toString(), canial.getText().toString()}));
            }
        });

    }

    @Override
    public void onClickFloating(View view) {
        switch (view.getId()){
            case R.id.btn_fab_right:
                if ( validateForm() )
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
                    view.setVisibility(View.INVISIBLE);
                    break;
                case R.id.main_body_action_layout:
                    view.getLayoutParams().height = MainActivity.VISIBLE_ACTION;
                    break;
            }
        }
    }

    @Override
    public String getTAG() { return Formulario1.TAG; }

    @Override
    public int getSubTitle() { return R.string.formulario1_sub_title;}
}