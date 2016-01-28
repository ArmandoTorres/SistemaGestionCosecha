package com.delacrmi.persistences;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.cac.tools.AppParameters;
import com.delacrmi.connection.ConnectSQLite;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by miguel on 09/10/15.
 */
public class EntityManager  {

    private ConnectSQLite conn = null;
    private List<Class> tables;
    private List<String> tablesNames;
    private HashMap<String,Class> name_class = new HashMap<String,Class>();
    private HashMap<String,String> entitiesNickName = new HashMap<String,String>();

    private Context context;
    private String dbName;
    private int dbVersion;
    private SQLiteDatabase.CursorFactory factory = null;

    public List<String> getTablesNames() {
        return tablesNames;
    }

    public List<Class> getTables() {
        return tables;
    }

    public void setDbVersion(int dbVersion) {
        this.dbVersion = dbVersion;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setFactory(SQLiteDatabase.CursorFactory factory) {
        this.factory = factory;
    }

    public EntityManager(){}
    public EntityManager(Context context, String dbName,
                         SQLiteDatabase.CursorFactory factory, int dbVersion){
        this.context = context;
        this.dbName = dbName;
        this.factory = factory;
        this.dbVersion = dbVersion;
    }

    public EntityManager init(){
        ConnectSQLite.tablesCreater = createList();
        ConnectSQLite.tablesNames = tablesNames;
        conn = new ConnectSQLite(context,dbName,factory,dbVersion){

            @Override
            public void beforeToCreate(SQLiteDatabase db){
                onCreateDataBase(this, db);
            }
            @Override
            public void afterToCreate(SQLiteDatabase db) {
                onDataBaseCreated(this, db);
            }

            @Override
            public void beforeToUpdate(SQLiteDatabase db) {
                onDatabaseUpdate(this, db);
            }

            @Override
            public void afterToUpdate(SQLiteDatabase db) {
                onUpdatedDataBase(this, db);
            }
        };
        read();

        return this;
    }

    public void onCreateDataBase(ConnectSQLite conn, SQLiteDatabase db){}
    public void onDataBaseCreated(ConnectSQLite conn, SQLiteDatabase db){}

    public void onDatabaseUpdate(ConnectSQLite conn, SQLiteDatabase db){}
    public void onUpdatedDataBase(ConnectSQLite conn, SQLiteDatabase db){}

    protected SQLiteDatabase write(){
        if(conn == null)
            init();

        return conn.getWritableDatabase();
    }

    public SQLiteDatabase read(){
        if(conn == null)
            init();

        return conn.getReadableDatabase();
    }

    public EntityManager addTable(Class entity){
        if(tables == null)
            tables = new ArrayList<Class>();
        tables.add(entity);
        return this;
    }

    //Setting the tablesCreater Data Bases
    public void setTables(ArrayList<Class> tables){
        this.tables = tables;
    }

    public Class getClassByName(String name){
        return name_class.get(name);
    }

    public HashMap<String, String> getEntitiesNickName() {
        return entitiesNickName;
    }

    public void setEntitiesNickName(HashMap<String, String> entitiesNickName) {
        this.entitiesNickName = entitiesNickName;
    }

    public String getEntityNicName(String entity){
        return entitiesNickName.get(entity);
    }

    //<editor-fold desc="Saving the Entities class">
    public Entity save(Class entity,ContentValues args){
        Entity ent = initInstance(entity);
        ent.entityConfig().addColumns(args);
        return save(ent);
    }

    public synchronized Entity save(Entity entity){
        if(entity != null){
            long insert = write().insert(entity.getName(), null, entity.getContentValues());
            write().close();
            //Log.e("Save", "" + insert);
            if(insert > 0) {
                //entity.getColumnValueList().put(entity.getPrimaryKey(),insert);
                List<EntityColumn> pk = entity.getPrimariesKeys();
                if(pk.size() == 1)
                    pk.get(0).setValue(insert);
                return entity;
            }else
                return null;
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold desc="Updating the Entities class">
    public  Entity update(Class entity,ContentValues columnsValue,String where, String[] whereValues,boolean save){

        Entity ent= findOnce(entity, "*", where, whereValues);
        ent.setValues(columnsValue);
        return update(ent, where, whereValues, save);
    }

    public synchronized Entity update(Entity entity,String where,String[] whereValues,boolean save){
        if(entity != null){
            long insert = write().update(entity.getName(), entity.getColumnValueList(), where, whereValues);
            write().close();
            if(insert > 0)
                return entity;
            else if(save)
                return save(entity);
        }
        return null;
    }
    //</editor-fold>

    //<editor-fold desc="Finding the Entities class">
    public synchronized Entity findOnce(Class entity,String[] columns,String where,
                                        String[] whereValues, String groupBy, String having, String orderBy){
        Entity ent= initInstance(entity);
        Cursor cursor = read().query(ent.getName(), columns, where, whereValues, groupBy,
                having, orderBy, "1");

        if(cursor != null && cursor.moveToFirst())
            addEntityValues(cursor,ent);

        read().close();
        return ent;
    }

    public synchronized Entity findOnce(Class entity,String columns,String conditions,String[] args){
        Entity ent= initInstance(entity);

        String sql = "select "+columns+" from "+ent.getName();
        if(conditions != null)
            sql += " where "+conditions;

        Cursor cursor = read().rawQuery(sql, args);

        if(cursor != null && cursor.moveToFirst()) addEntityValues(cursor, ent);

        read().close();
        return ent;
    }

    public synchronized List<Entity> find(Class entity, boolean distinct, String[] columns, String where,
                                          String[] whereValues, String groupBy, String having, String orderBy,
                                          String limit){
        Cursor cursor = read().query(distinct, initInstance(entity).getName(),
                columns, where, whereValues, groupBy, having, orderBy, limit);


        if(cursor != null && cursor.moveToFirst()){
            List<Entity> list = new ArrayList<Entity>();
            do {
                Entity ent= initInstance(entity);
                addEntityValues(cursor,ent);
                list.add(ent);
            }while(cursor.moveToNext());

            read().close();

            return  list;
        }
        read().close();
        return new ArrayList<Entity>();
    }

    public synchronized List<Entity> find(Class entity,String columns,String conditions,String[] args, String orderBy){
        Entity ent= initInstance(entity);

        String sql = "select "+columns+" from "+ent.getName();
        if(conditions != null)
            sql += " where "+conditions;

        if (orderBy != null)
            sql += " order by "+orderBy;

        Cursor cursor = read().rawQuery(sql, args);
        List<Entity> list = new ArrayList<Entity>();
        setListFromCursor(cursor,list,entity);

        read().close();
        return list;
    }

    public synchronized List<Entity> find(Class entity,String columns,String conditions,String[] args){
        Entity ent= initInstance(entity);

        String sql = "select "+columns+" from "+ent.getName();
        if(conditions != null)
            sql += " where "+conditions;

        Cursor cursor = read().rawQuery(sql, args);
        List<Entity> list = new ArrayList<Entity>();
        setListFromCursor(cursor,list,entity);

        read().close();
        return list;
    }
    //</editor-fold>

    //<editor-fold desc="deleting the Entities class">
    public synchronized boolean delete(Class entity,String where,String[] whereValues){
        Entity ent = initInstance(entity);

        int deleted = write().delete(ent.getName(),where,whereValues);
        write().close();

        if(deleted > 0)
            return true;
        else
            return false;
    }

    public synchronized boolean delete(Entity entity){

        int deleted = write().delete(entity.getName(), entity.getPrimaryKey() + " = ?",
                new String[]{entity.getColumnValueList().getAsString(entity.getPrimaryKey())});
        write().close();

        if(deleted > 0)
            return true;
        else
            return false;
    }
    //</editor-fold>

    public void importEntityFromXml(String entityName, List<EntityColumn> entityColumns)  throws Exception {

        File ubicacion = new File(AppParameters.BACK_UP_PATH);
        List<String> listadoArchivoEnCarpeta = Arrays.asList(ubicacion.list());

        File archivo = new File(ubicacion,entityName+".xml");

        if (archivo.exists()) {

            Log.e("Grabamos","----------------------------------------");
            Log.e("Grabamos","Archivo. "+archivo.getName());
            Log.e("Grabamos","----------------------------------------");

            FileInputStream fileInputStream = new FileInputStream(archivo);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();

            Document document = builder.parse(fileInputStream);
            Element root = document.getDocumentElement();
            NodeList registros = root.getChildNodes();

            //Delete all transaction.
            delete(getClassByName(entityName),null,null);

            for (int i = 0 ; i < registros.getLength(); i++ ){
                Node registro = registros.item(i);
                if (registro.getNodeType() == Node.ELEMENT_NODE){
                    ContentValues column = new ContentValues();

                    for ( EntityColumn columnName : entityColumns ) {

                        String key   = columnName.getName();
                        String value = getTagValue(columnName.getName(), (Element) registro);

                        if (value != null && !value.equals("") && !value.equals(" ")) {
                            column.put(key,value);
                        }

                    }
                    //Save XML transaction.
                    Entity entity = save(getClassByName(entityName),column);

                    Log.e("Grabamos","Grabamos la Informacion. "+((Long)entity.getPrimariesKeys().get(0).getValue()));

                }
            }

        } else {
            Log.e("Archivo","El archivo no existe en el repositorio.");
        }
    }

    private String getTagValue(String tagName, org.w3c.dom.Element element){
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

                sb.append("<" + entityName + ">");
                for (Entity entity : entities){
                    sb.append("<" + entity.getClass().getSimpleName() + ">");
                    for(EntityColumn column : entity.getEntityColumnList()){
                        if (!entity.getValuesByTypeAsString(column).equals(""))
                            sb.append("<" + column.getName() + ">" + isNullValue(entity.getValuesByTypeAsString(column)) +
                                        "</" + column.getName() + ">");
                    }
                    sb.append("</" + entity.getClass().getSimpleName() + ">");
                }
                sb.append("</" + entityName + ">");

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
            Log.e("XmlTips", "Error al escribir fichero XML."+ex.getMessage());
        }
    }

    private String isNullValue( String value ) {
        if ( value == null || value.isEmpty() || value.equals("") || value.equals("null") )
            return "";
        return value;
    }

    //adding the columns value by entity
    private void addEntityValues(Cursor cursor,Entity entity){
        if(cursor != null){
            for (int index = 0; index < cursor.getColumnNames().length; index++){
                String columnName = cursor.getColumnName(index);
                int col = cursor.getColumnIndex(columnName);
                entity.setValue(columnName,cursor.getString(col));
                entity.setColumnFromSelect(columnName, cursor.getString(col));
            }
        }
    }

    public void setListFromCursor(Cursor cursor, List<Entity> entities,Class entity){
        Entity ent;
        if(cursor != null && cursor.moveToFirst()){
            do {
                ent= initInstance(entity);
                addEntityValues(cursor,ent);
                entities.add(ent);
            }while(cursor.moveToNext());
        }
    }

    private List<String> createList(){
        Iterator tablesIterator = tables.iterator();
        List<String> value= new ArrayList<String>();
        tablesNames = new ArrayList<String>();
        Entity entity;
        while (tablesIterator.hasNext()){

            Class entityClass = (Class)tablesIterator.next();
            entity = initInstance(entityClass);
            entity.setEntityManager(this);

            tablesNames.add(entity.getName());
            entitiesNickName.put(entity.getName(),entity.getNickName());
            name_class.put(entity.getName(),entityClass);
            value.add(createString(entity));

        }

        return value;
    }

    public Entity initInstance(Class entity){
        try {
            return  ((Entity)entity.newInstance()).entityConfig();
        } catch (InstantiationException e1) {
        } catch (IllegalAccessException e1) {}

        return null;
    }

    //making the create String
    private String createString(Entity entity){
        int count = 1;
        Map.Entry me;
        String create = "create table "+entity.getName()+"(";

        //getting the iterator columns to get the key values
        Iterator iteratorColumns = entity.iterator();
        while (iteratorColumns.hasNext()){
            me = (Map.Entry)iteratorColumns.next();

            if(!entity.getPrimaryKey().equals(me.getKey().toString())) {
                if (me.getValue().toString().equals("date"))
                    create += me.getKey() + " " + "numeric";
                else
                    create += me.getKey() + " " + me.getValue();
            }else
                create += entity.getPrimaryKey()+" integer primary key autoincrement";

            if(count < entity.getColumnsCount()){
                create += ",";
                count++;
            }
        }
        create += ")";

        return create;
    }

    public String getDBFileLocation(){
        return read().getPath();
    }
}
