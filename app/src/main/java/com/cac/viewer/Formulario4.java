package com.cac.viewer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.atorres.AndroidUtils;
import com.cac.customcomponents.AbstractFragment;
import com.cac.customcomponents.OnKeyListenerRefactory;
import com.cac.entities.EmpleadoDigitador;
import com.cac.entities.Rangos;
import com.cac.entities.Transaccion;
import com.cac.tools.FormatReportData;
import com.cac.tools.PrinterManager;
import com.delacrmi.persistences.Entity;
import com.delacrmi.persistences.EntityManager;

import java.util.List;

import com.cac.sgc.MainActivity;
import com.cac.R;
import com.cac.entities.Vehiculos;
import com.cac.customcomponents.MyOnFocusListenerFactory;

/**
 * Created by Legal on 04/10/2015.
 */
public class Formulario4 extends AbstractFragment {

    public static final String TAG = "Formulario4";
    private TextView txtDescCodigoApuntador;
    private AutoCompleteTextView listaCodigoVagones, codigoApuntador;
    private Transaccion transaccion;

    //Filtros queries
    private String EMPRESA, PERIODO, DISPOSITIVO, APLICACION;

    public Formulario4 init(MainActivity context, EntityManager entityManager) {
        return (Formulario4) super.init(context,R.layout.formulario4,entityManager,TAG);
    }

    @Override
    public void initializeComponents() {
        codigoApuntador         = (AutoCompleteTextView) view.findViewById(R.id.editTextCodigoApuntador);
        listaCodigoVagones      = (AutoCompleteTextView) view.findViewById(R.id.listaCodigoVagones);
        txtDescCodigoApuntador  = (TextView) view.findViewById(R.id.txtDescCodigoApuntador);
        layout = (RelativeLayout) view.findViewById(R.id.formulario4);
    }

    @Override
    public void initializeMethods() {
        codigoApuntador.setOnFocusChangeListener(new MyOnFocusListenerFactory(txtDescCodigoApuntador,
                getContext().getEntityManager(), EmpleadoDigitador.class,
                EmpleadoDigitador.NOMBRE,
                EmpleadoDigitador.ID_EMPLEADO,true));
        codigoApuntador.setOnKeyListener(new OnKeyListenerRefactory(
                getInformation(EmpleadoDigitador.class,
                        EmpleadoDigitador.ID_EMPLEADO + " key, " + EmpleadoDigitador.NOMBRE + " value",
                        null, null),
                txtDescCodigoApuntador
        ));
        codigoApuntador.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                codigoApuntador.getOnFocusChangeListener().onFocusChange(codigoApuntador,false);
                listaCodigoVagones.requestFocus();
            }
        });
        codigoApuntador.setAdapter(findCodigosEmpleados(EmpleadoDigitador.class));

        //Llenando parametros de control
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        EMPRESA =  sharedPreferences.getString("EMPRESA","30");
        PERIODO =  sharedPreferences.getString("PERIODO", "30");
        APLICACION = sharedPreferences.getString("NOMBRE_APLICACION", "SGC");
        TelephonyManager telephonyManager = (TelephonyManager)getContext().getSystemService(Context.TELEPHONY_SERVICE);
        DISPOSITIVO = telephonyManager.getDeviceId();
        listaCodigoVagones.setAdapter(findCodigosVehiculos(Vehiculos.class,"C"));
        listaCodigoVagones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getContext().hideKeyboard();
            }
        });
    }

    @Override
    public void onClickFloating(View view) {
        switch (view.getId()) {
            case R.id.btn_fab_right:
                if ( validateForm() && saveInformationOnDataBase() ) {
                    getContext().startTransactionByFragmentTag(MainFragment.TAG);
                    Toast.makeText(getContext(), "Informacion guardada correctamente", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_fab_left:
                getContext().startTransactionByFragmentTag(Formulario3.TAG);
                break;
        }
    }

    private boolean saveInformationOnDataBase() {
        try {
            if ( getContext() != null ) {

                if ( EMPRESA == null ) {
                    AndroidUtils.showAlertMsg(getContext(), "Notificación", "Debe de seleccionar la empresa antes de ingresar la transacción.");
                    return false;
                } else if ( PERIODO == null ) {
                    AndroidUtils.showAlertMsg(getContext(), "Notificación", "Debe de seleccionar el periodo antes de ingresar la transacción.");
                    return false;
                } else if ( DISPOSITIVO == null ) {
                    AndroidUtils.showAlertMsg(getContext(), "Notificación", "No se ha podido conseguir el IMEI del dispositivo, contactar a soporte tecnico.");
                    return false;
                }
                int envioActual = findMaxEnvio();
                if ( envioActual == 0 ) {
                    AndroidUtils.showAlertMsg(getContext(), "Notificación", "No se encontro el número de envio.");
                    return false;
                }

                transaccion = new Transaccion().entityConfig();
                //Informacion del formulario 1
                transaccion.getColumn(Transaccion.FRENTE_CORTE)
                        .setValue(getContext().getFormulario1Fragment().getFteCorte());
                transaccion.getColumn(Transaccion.FRENTE_ALCE)
                        .setValue(getContext().getFormulario1Fragment().getFteAlce());
                transaccion.getColumn(Transaccion.ID_FINCA)
                        .setValue(getContext().getFormulario1Fragment().getFinca());
                transaccion.getColumn(Transaccion.ID_CANIAL)
                        .setValue(getContext().getFormulario1Fragment().getCanial());
                transaccion.getColumn(Transaccion.ID_LOTE)
                        .setValue(getContext().getFormulario1Fragment().getLote());

                //formulario 2
                transaccion.getColumn(Transaccion.ORDEN_QUEMA)
                        .setValue(getContext().getFormulario2Fragment().getOrdenQuema());
                transaccion.getColumn(Transaccion.FECHA_CORTE)
                        .setValue(getContext().getFormulario2Fragment().getFechaCorte());
                transaccion.getColumn(Transaccion.CLAVE_CORTE)
                        .setValue(getContext().getFormulario2Fragment()
                                .getListaClaveCorte());

                String formaTiro = getContext().getFormulario2Fragment().getListaFormaTiro();
                transaccion.getColumn(Transaccion.FORMA_TIRO)
                        .setValue(formaTiro);
                if ( formaTiro.toLowerCase().equals("mixto") ||
                     formaTiro.toLowerCase().equals("cabezal") ) {
                    transaccion.getColumn(Transaccion.CODIGO_CABEZAL)
                            .setValue(getContext().getFormulario2Fragment()
                                    .getAutoCompleteListaCabezales());
                    transaccion.getColumn(Transaccion.CONDUCTOR_CABEZAL)
                            .setValue(getContext().getFormulario2Fragment()
                                    .getAutoCompleteConductorCabezal());
                }

                //Formulario 3.
                transaccion.getColumn(Transaccion.CODIGO_CARRETA)
                        .setValue(getContext().getFormulario3Fragment()
                                .getListaCodigoCarreta());
                transaccion.getColumn(Transaccion.CODIGO_COSECHADORA)
                        .setValue(getContext().getFormulario3Fragment()
                                .getListaCodigoCosechadora());
                transaccion.getColumn(Transaccion.OPERADOR_COSECHADORA)
                        .setValue(getContext().getFormulario3Fragment()
                                .getConductorCosechadora());
                transaccion.getColumn(Transaccion.CODIGO_TRACTOR)
                        .setValue(getContext().getFormulario3Fragment()
                                .getListaCodigoTractor());
                transaccion.getColumn(Transaccion.OPERADOR_TRACTOR)
                        .setValue(getContext().getFormulario3Fragment()
                                .getConductorTractor());
                //formulario 4.
                transaccion.getColumn(Transaccion.CODIGO_APUNTADOR)
                        .setValue(codigoApuntador.getText().toString());
                transaccion.getColumn(Transaccion.CODIGO_VAGON)
                        .setValue(listaCodigoVagones.getText().toString());

                //Setiamos el numero de envio.
                transaccion.getColumn(Transaccion.NO_RANGO).setValue(envioActual + "");
                //Setiamos el indicador de traslado
                transaccion.getColumn(Transaccion.ESTADO).
                        setValue(Transaccion.TransaccionEstado.ACTIVA.toString());
                transaccion.getColumn(Transaccion.ID_EMPRESA).setValue(EMPRESA);
                transaccion.getColumn(Transaccion.ID_PERIODO).setValue(PERIODO);
                transaccion.getColumn(Transaccion.APLICACION).setValue(APLICACION);
                transaccion.getColumn(Transaccion.DISPOSITIVO).setValue(DISPOSITIVO);

                //Grabamos la transaccion.
                getContext().getEntityManager().save(transaccion);

                transaccion = (Transaccion) getContext().getEntityManager().findOnce(Transaccion.class, "*", Transaccion.NO_RANGO+" = "+envioActual,null);
                try {
                    Transaccion oldTransaccion = (Transaccion) getContext().getEntityManager().findOnce(Transaccion.class, "*", Transaccion.NO_RANGO + " = " + (envioActual - 1), null);


                } catch (Exception ex){

                }

                //Preguntamos por la impresion
                askPrinter();

                return true;
            }
        } catch (Exception e){
            Log.e("Error", "Metodo grabar formularios.", e);
            Toast.makeText(getContext(), "Ocurrio un error al grabar la información.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void askPrinter(){
        new AlertDialog.Builder(getContext())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("¿Pregunta?")
                .setMessage("¿Desea Imprimir el envio?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        askPrinterOkBtn();
                    }
                }).setNegativeButton("No", null).show();
    }

    public void askPrinterOkBtn(){
        FormatReportData formatReportData = new FormatReportData(getContext(),getContext().getEntityManager());
        PrinterManager printerManager = new PrinterManager();
        printerManager.setContext(getContext());
        printerManager.setListadoTransacciones(formatReportData.formatTransaction(transaccion, false));
        printerManager.setNumberOfCopy(2);
        printerManager.printText();
    }

    public int findMaxEnvio (){

        int envioActual = 0;

        //Buscamos los numeros permitidos a generar.
        Rangos rangos =  (Rangos) getContext().getEntityManager().findOnce(Rangos.class, "*",
                Rangos.ID_EMPRESA + " = ? and " + Rangos.ID_PERIODO + " = ? and " +
                Rangos.APLICACION + " = ? and " + Rangos.DISPOSITIVO + " = ?",
                new String[]{EMPRESA, PERIODO, APLICACION, DISPOSITIVO});
        if ( rangos == null ) {
            AndroidUtils.showAlertMsg(getContext(), "Notificación", "Debe actualizar la tabla de rangos para proseguir.");
            return 0;
        }

        int minEnvio = rangos.getColumnValueList().getAsInteger(Rangos.RANGO_DESDE);
        int maxEnvio = rangos.getColumnValueList().getAsInteger(Rangos.RANGO_HASTA);

        Entity transaccionTemp = getContext().getEntityManager().findOnce(
                Transaccion.class, "max(" + Transaccion.NO_RANGO + ")+1 " + Transaccion.NO_RANGO,
                Transaccion.ID_EMPRESA + " = ? and " + Rangos.ID_PERIODO + " = ?",
                new String[]{EMPRESA, PERIODO});
        if ( transaccionTemp != null ){
            try {
                envioActual = transaccionTemp.getColumnValueList().getAsInteger(Transaccion.NO_RANGO);
            } catch (Exception ex) { envioActual = minEnvio; }
        }

        if ( maxEnvio == 0 || minEnvio == 0 ) {
            AndroidUtils.showAlertMsg(getContext(),"Notificación","No se han definido los correlativos de envio, favor actualizar la información.");
            return 0;
        }else if ( envioActual > maxEnvio ){
            AndroidUtils.showAlertMsg(getContext(),"Notificación","El dispositivo ha excedido el número maximo de envios, solo tiene permitido generar hasta "+maxEnvio+" Envios y el envio actual es el "+envioActual);
            return 0;
        } else if ( envioActual == 0 ) {
            envioActual = minEnvio;
        }

        return envioActual;
    }

    @Override
    public void MainViewConfig(List<View> views) {
        for ( View view : views){
            switch (view.getId()){
                case R.id.btn_fab_right:
                    view.setVisibility(View.VISIBLE);
                    ((ImageButton) view).setImageResource(R.drawable.grabar);
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
    public int getSubTitle() {return R.string.formulario4_sub_title;}
    //</editor-fold>
}