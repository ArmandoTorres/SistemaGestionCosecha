package com.cac.tools;

import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.cac.entities.Fincas;
import com.cac.entities.Frentes;
import com.cac.entities.Users;
import com.cac.sgc.MainActivity;
import com.delacrmi.persistences.Entity;
import com.delacrmi.persistences.EntityManager;

/**
 * Created by Legal on 19/01/2016.
 */
public class BackupBD extends AsyncTask<Integer,Integer,Boolean> {

    private MainActivity context = null;
    private EntityManager entityManager = null;

    public BackupBD( MainActivity context, EntityManager entityManager ) {

        if ( context == null || entityManager == null )
            throw new NullPointerException("All parameter are required");

        this.context = context;
        this.entityManager = entityManager;
    }

    @Override
    protected Boolean doInBackground(Integer... params) {
        Looper.prepare();
        if ( params[0] == 0 ) {
            backupFromSqlToXMLFile();
        } else
            backupFromXMLFileToSql();
        Looper.loop();
        return true;
    }

    private void backupFromXMLFileToSql() {
        try {
            for ( Class entityClass : entityManager.getTables() ) {
                Entity entity = entityManager.initInstance(entityClass);
                entityManager.importEntityFromXml(entity.getName(),entity.getEntityColumnList());
            }
            Toast.makeText(context, "El backup ha sido subido correctamente.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context,"Error al subir el backup.",Toast.LENGTH_LONG).show();
            Log.e("Error", "Al subir el backup.", e);
        }
    }

    private void backupFromSqlToXMLFile() {
        try {
            for (Class entityClass : entityManager.getTables()) {
                Entity instance = entityManager.initInstance(entityClass);
                entityManager.exportEntityToXML(instance.getName(), entityManager.find(entityClass, "*", null, null));
            }
            Toast.makeText(context, "El backup fue realizado correctamente.", Toast.LENGTH_SHORT).show();
        } catch ( Exception ex ) {
            Toast.makeText(context,"Error al realizar el backup.",Toast.LENGTH_LONG).show();
            Log.e("Error", "Al realizar el backup.", ex);
        }
    }

}
