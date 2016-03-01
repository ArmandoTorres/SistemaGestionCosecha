package com.cac.tools;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.cac.sgc.MainActivity;
import com.delacrmi.persistences.Entity;
import com.delacrmi.persistences.EntityColumn;
import com.delacrmi.persistences.EntityManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Legal on 19/01/2016.
 */
public class BackupBD extends AsyncTask<Integer,String,Boolean> {

    private MainActivity context = null;
    private EntityManager entityManager = null;
    private ProgressDialog progressDialog;
    private static final String FINISH = "Proceso Completado";

    public BackupBD(MainActivity context, EntityManager entityManager) {

        if ( context == null || entityManager == null )
            throw new NullPointerException("All parameter are required");

        this.context = context;
        this.entityManager = entityManager;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try {
            progressDialog = new ProgressDialog(context);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Realizando Operación");
            progressDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("Error","Error al iniciar el backup",ex);
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        if ( progressDialog != null ) {
            progressDialog.dismiss();
            progressDialog = null;
        }

    }

    @Override
    protected void onProgressUpdate(String... values) {
        if ( values[0] != FINISH ) {
            progressDialog.setMessage(values[0]);
        } else {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    protected Boolean doInBackground(Integer... params) {
        Looper.prepare();
        if ( params[0] == 0 ) {
            backupFromSqlToXMLFile();
        } else {
            backupFromXMLFileToSql();
        }
        Looper.loop();
        return true;
    }

    private void backupFromXMLFileToSql() {
        try {
            for ( Class entityClass : entityManager.getTables() ) {
                Entity entity = entityManager.initInstance(entityClass);
                publishProgress(new String[]{entity.getName()});
                importEntityFromXml(entity.getName(), entity.getEntityColumnList());
            }
            Toast.makeText(context, "El backup ha sido subido correctamente.", Toast.LENGTH_SHORT).show();
            publishProgress(new String[]{FINISH});
        } catch (Exception e) {
            Toast.makeText(context,"Error al subir el backup.",Toast.LENGTH_LONG).show();
            Log.e("Error", "Al subir el backup.", e);
        }
    }

    private void backupFromSqlToXMLFile() {
        try {
            for (Class entityClass : entityManager.getTables()) {
                Entity instance = entityManager.initInstance(entityClass);
                publishProgress(new String[]{instance.getName()});
                exportEntityToXML(instance.getName(), entityManager.find(entityClass, "*", null, null));
            }
            Toast.makeText(context, "El backup fue realizado correctamente.", Toast.LENGTH_SHORT).show();
            publishProgress(new String[]{FINISH});
        } catch ( Exception ex ) {
            Toast.makeText(context,"Error al realizar el backup.",Toast.LENGTH_LONG).show();
            Log.e("Error", "Al realizar el backup.", ex);
        }
    }

    public void importEntityFromXml(String entityName, List<EntityColumn> entityColumns) {

        File ubicacion = new File(AppParameters.BACK_UP_PATH);

        File archivo = new File(ubicacion,entityName+".xml");

        try {
            if (archivo.exists()) {

                FileInputStream fileInputStream = new FileInputStream(archivo);
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();

                Document document = builder.parse(fileInputStream);
                Element root = document.getDocumentElement();
                NodeList registros = root.getChildNodes();

                //Delete all transaction.
                entityManager.delete(entityManager.getClassByName(entityName), null, null);

                for (int i = 0; i < registros.getLength(); i++) {
                    Node registro = registros.item(i);
                    if (registro.getNodeType() == Node.ELEMENT_NODE) {
                        ContentValues column = new ContentValues();

                        for (EntityColumn columnName : entityColumns) {

                            String key = columnName.getName();
                            String value = getTagValue(columnName.getName(), (Element) registro);

                            if (value != null && !value.equals("") && !value.equals(" ")) {
                                column.put(key, value);
                            }

                        }
                        //Save XML transaction.
                        entityManager.save(entityManager.getClassByName(entityName), column);
                    }
                }

            } else {
                Log.e("Archivo", "El archivo no existe en el repositorio.");
            }
        } catch (Exception ex){
            Log.e("Error","Al abrir el archivo.",ex);
            Toast.makeText(context,"Error al abrir el archivo "+entityName,Toast.LENGTH_SHORT);
        }
    }

    private String getTagValue(String tagName, Element element){
        try {
            NodeList list =  element.getElementsByTagName(tagName).item(0).getChildNodes();
            Node nValue = (Node) list.item(0);
            return nValue.getNodeValue();
        } catch (NullPointerException npe){
            return "";
        }
    }

    public void exportEntityToXML(String entityName, List<Entity> entities){
        try {
            if (entities.size() > 0){
                //Creamos un fichero en la memoria interna (NoSuchFieldError)
                File archivo = new File(AppParameters.BACK_UP_PATH);
                if (!archivo.exists()){
                    archivo.mkdirs();
                }
                File file = new File(archivo,entityName+".xml");

                //overwrite
                if (file.exists()) file.delete();

                StringBuilder sb = new StringBuilder();

                sb.append("<" + replaceCharacter(entityName) + ">");
                for (Entity entity : entities){
                    sb.append("<" + replaceCharacter(entity.getClass().getSimpleName()) + ">");
                    for(EntityColumn column : entity.getEntityColumnList()){
                        if (!entity.getValuesByTypeAsString(column).equals(""))
                            sb.append("<" + replaceCharacter(column.getName()) + ">" + replaceCharacter(isNullValue(entity.getValuesByTypeAsString(column))) +
                                    "</" + replaceCharacter(column.getName()) + ">");
                    }
                    sb.append("</" + replaceCharacter(entity.getClass().getSimpleName()) + ">");
                }
                sb.append("</" + replaceCharacter(entityName) + ">");

                //Escribimos el resultado a un fichero
                OutputStream outputStream = null;
                try {
                    outputStream = new BufferedOutputStream(new FileOutputStream(file));
                    outputStream.write(sb.toString().getBytes());
                    outputStream.flush();
                } catch (Exception ex) {
                    Log.e("Error","Al grabar el archivo.",ex);
                } finally {
                    if ( outputStream != null ){
                        outputStream.close();
                    }
                }

                Log.i("XmlTips", "Fichero XML creado correctamente. "+ (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsoluteFile()));
            }
        }
        catch (Exception ex)
        {
            Log.e("XmlTips", "Error al escribir fichero XML.",ex);
            Toast.makeText(context,"Erro al escribir el archivo XML "+entityName,Toast.LENGTH_SHORT);
        }
    }

    private String isNullValue( String value ) {
        if ( value == null || value.isEmpty() || value.equals("") || value.equals("null") )
            return "";
        return value;
    }

    private String replaceCharacter(String value){
        return value.replace(">","").replace("<","").replace("ñ","n").replace("Ñ","N");
    }
}
