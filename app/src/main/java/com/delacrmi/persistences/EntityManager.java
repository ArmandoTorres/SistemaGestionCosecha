package com.delacrmi.persistences;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cac.entities.Transaccion;
import com.delacrmi.connection.ConnectSQLite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
        Cursor cursor = read().query(ent.getName(),columns,where,whereValues,groupBy,
                having,orderBy,"1");

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

        int deleted = write().delete(entity.getName(),entity.getPrimaryKey()+" = ?",
                new String[]{entity.getColumnValueList().getAsString(entity.getPrimaryKey())});
        write().close();

        if(deleted > 0)
            return true;
        else
            return false;
    }
    //</editor-fold>

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
}
