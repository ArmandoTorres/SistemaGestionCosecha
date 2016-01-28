package com.delacrmi.persistences;

import android.content.ContentValues;
import android.content.Context;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by miguel on 09/10/15.
 */
public abstract class Entity implements Serializable {

    private String entityName = "";
    private String nickName = "";
    private String pk = "";

    private HashMap<String,ContentValues> constraint = new HashMap<String,ContentValues>();
    private ContentValues constraintDetails;
    private ContentValues columnList = new ContentValues();
    private List<EntityColumn> columns = new ArrayList<EntityColumn>();
    private Map<String,Integer> hashcolumns = new HashMap<String,Integer>();
    private ContentValues newColumnsOnSelect = new ContentValues();
    private ContentValues columnValueList = new ContentValues();
    private boolean synchronizable = false;

    private EntityFilter entityFilter;
    private EntityManager manager = null;

    public abstract Entity entityConfig();

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public boolean isSynchronizable() {
        return synchronizable;
    }

    public void setSynchronizable(boolean synchronizable) {
        this.synchronizable = synchronizable;
    }

    public EntityFilter getEntityFilter() {
        return entityFilter;
    }

    public void setEntityFilter(EntityFilter entityFilter) {
        this.entityFilter = entityFilter;
    }

    public void configureEntityFilter(Context context){}

    public void setEntityManager(EntityManager manager){
        this.manager = manager;
    }
    public EntityManager getEntityManager(){
        return manager;
    }

    public List<Entity> getDefaultInsert(){
        return null;
    }

    /*public void findOne(){
        if(manager == null) throw new NullPointerException("The EntityManager can`t be null");

        List<EntityColumn> list = getPrimariesKeys();
        String[] values = null;
        String name = null;

        if(list.size() > 0){
            values = new String[list.size()];
            name = "";
            int ind = 0;

            for(EntityColumn column : list){
                values[ind] = getValuesByTypeAsString(column);

                ind++;
                name += column.getName();
                if((ind+1) != list.size())
                    name += ",";
            }
        }

        addColumns( manager.findOnce(getClass(),"*", name, values).getContentValues());

    }*/

    public String getCreateString(){
        int count = 1;
        String create = "create table "+entityName+"(";

        for (EntityColumn column: columns){
            create += column.getCreateString();
            if(count < columns.size()){
                create  += ",";
                count++;
            }
        }
        create += ")";
        return create;
    }

    public void setPrimaryKey(String columnName){
        if(!columnName.equals("") || !columnName.equals(" ")) {

            columnList.remove(getName() + "_id");
            columns.remove(hashcolumns.get(pk));
            pk = columnName;
            columnList.put(pk, "integer");
            addColumn(new EntityColumn<Long>(columnName, EntityColumn.ColumnType.NUMERIC,true,true));

        }
    }
    @Deprecated
    public  String getPrimaryKey(){
        return pk;
    }

    public List<EntityColumn> getPrimariesKeys(){

        List<EntityColumn> list = new ArrayList<EntityColumn>();
        for(EntityColumn column : columns) if(column.isPrimaryKey()) list.add(column);

        return  list;
    }

    public void setDefault(String columnName,String value,String type){
        haveDetailsColumn(columnName);
        constraintDetails.putNull("default");
        constraintDetails.put("value", value);
        constraintDetails.put("type", type);
    }

    public void setNullable(String columnName,boolean nullable){
        haveDetailsColumn(columnName);
        constraintDetails.put("nullable", nullable);
    }

    public HashMap<String,ContentValues> getColumnsConstraints(){
        return constraint;
    }

    public ContentValues getColumnConstraints(){
        return constraint.get(getName());
    }

    public void addColumn(String name,EntityColumn.ColumnType type) {
        addColumn(createColumn(name, type));
    }

    public void addColumn(EntityColumn column){
        columnList.put(column.getName(),column.getType().toString().toLowerCase());
        columns.add(column);
        hashcolumns.put(column.getName(), columns.size() - 1);
    }

    public void addColumns(ContentValues contentValues){
        columnList = contentValues;

        Map.Entry me;
        Iterator i = contentValues.valueSet().iterator();
        while (i.hasNext()){
            me = (Map.Entry)i.next();
            if(hashcolumns.containsKey(me.getKey().toString())) {
                EntityColumn column = getColumn(me.getKey().toString());
                if(column.getType() == EntityColumn.ColumnType.DATE)
                    column.setValue(new Date(Long.parseLong(me.getValue().toString())));
                else
                    column.setValue(me.getValue());
            }
        }

    }

    public ContentValues getColumns(){
        return columnList;
    }

    public String getColumnsNameAsString(boolean primaryKey){
        int count = 1;
        String columns = "";

        for (EntityColumn column: this.columns){
            if(!column.isPrimaryKey()){
                columns += column.getName();
                if(count < this.columns.size()){
                    columns += ",";
                    count++;
                }
            }else if(primaryKey){
                columns += column.getName();
                if(count < this.columns.size()){
                    columns += ",";
                    count++;
                }
            }else{
                count++;
            }
        }

        return columns;
    }

    public String getPrimaryKeyAsString(){
        String pks = "";
        for(EntityColumn column : getPrimariesKeys())
            pks += column.getName();

        return pks;
    }

    public String getColumnsNameAsWithout(String [] withoutNames){
        int count = 1;
        String columns = "";

        for (EntityColumn column: this.columns){
            if(!arrayContains(column.getName(), withoutNames)){
                columns += column.getName();
                if(count < this.columns.size()-withoutNames.length){
                    columns += ",";
                    count++;
                }
            }else{
                count++;
            }
        }

        return columns;
    }

    private boolean arrayContains(String arg1, String [] arg2){
        for (int a =0; a < arg2.length; a++){
            if (arg1.equals(arg2[a]))
                return true;
        }
        return false;
    }

    public JSONObject getJSON(){

        JSONObject json = new JSONObject();

        for(EntityColumn column : columns)
            try {
                json.put(column.getName(),column.getValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        return json;
    }

    public ContentValues getContentValues(){
        ContentValues cv = new ContentValues();

        for(EntityColumn column : columns)
            if(column.getValue() == null)
                cv.putNull(column.getName());
            else if(column.getType() == EntityColumn.ColumnType.DATE){
                cv.put(column.getName(),((Date)column.getValue()).getTime());
            }else
                addValuesByType(cv,column.getName(),column.getValue(),column.getType().toString().toLowerCase());

        return cv;
    }

    public JSONArray getColumnstoJSONArray(){
        JSONArray array = new JSONArray();

        Set<String> sets = hashcolumns.keySet();
        for(String set: sets)
            array.put(set);

        return array;
    }

    public Iterator iterator(){
        return  getColumns().valueSet().iterator();
    }

    public int getColumnsCount(){
        return getColumns().size();
    }

    public String getName(){
        if(entityName.equals(""))
            return getClass().getSimpleName();

        return entityName;
    }

    public Entity setName(String entityName){
        this.entityName = entityName;
        if(pk.equals("")){
            pk = entityName+"_id";
            columnList.put(pk,"integer");
            addColumn(new EntityColumn<Long>(pk, EntityColumn.ColumnType.NUMERIC, true, true));
        }
        return this;
    }

    private void haveDetailsColumn(String columnName) {
        if(!columnList.containsKey(columnName))
            return;

        if(constraint.containsKey(columnName))
            constraintDetails = constraint.get(columnName);
        else
            constraintDetails = new ContentValues(5);
    }

    public ContentValues getColumnValueList(){
        return columnValueList;
    }

    public void setValues(ContentValues args) {
        Iterator iterator = args.valueSet().iterator();
        while (iterator.hasNext()){
            Map.Entry me= (Map.Entry)iterator.next();
            if(columnList.containsKey(me.getKey().toString())){
                addValuesByType(columnValueList,me.getKey().toString(),
                        args.get(me.getKey().toString()),columnList.getAsString(me.getKey().toString()));
            }
        }
    }

    public boolean setValue(String columnName,String value){
        boolean set = false;

        if(columnList.containsKey(columnName))
            addValuesByType(columnValueList,columnName,value,columnList.getAsString(columnName));

        if(hashcolumns.containsKey(columnName)) {
            addValuesByType(columns.get(hashcolumns.get(columnName)), value);
            set = true;
        }
        return set;
    }

    public boolean setValue(String columnName,Object value){
        boolean set = false;
        if(hashcolumns.containsKey(columnName)) {
            addValuesByType(columns.get(hashcolumns.get(columnName)), value);
            set = true;
        }
        return set;
    }

    public boolean setValueToEncrypt(String columnName, String value){
        boolean set = false;
        if(hashcolumns.containsKey(columnName)) {
            EntityColumn column = columns.get(hashcolumns.get(columnName));

            if(column.getEncryption() != null)
                value = encode(column.getEncryption(), column.getEncodeFlag(), value);
            else return set;

            addValuesByType(columns.get(hashcolumns.get(columnName)), value);
            set = true;
        }
        return set;
    }

    public String getValueAsString(String columnName){
        String value = "";
        if(hashcolumns.containsKey(columnName)){
            EntityColumn column = columns.get(hashcolumns.get(columnName));
        }

        return value;
    }

    public Object getValue(String columnName){
        if(hashcolumns.containsKey(columnName))
            return columns.get(hashcolumns.get(columnName)).getValue();
        return null;
    }

    public static String encode(EntityColumn.EncryptionType encrypt,int flag, String value){
        String encrypted = "";
        if(encrypt == EntityColumn.EncryptionType.BS64) encrypted =  Base64.encodeToString(value.getBytes(),flag);
        return encrypted.replaceAll("\n","");
    }

    public void setColumnFromSelect(String columnName, Object value){
        if(hashcolumns.containsKey(columnName)){
            addValuesByType(columns.get(hashcolumns.get(columnName)), value);
        }
        else
            addValuesByType(newColumnsOnSelect,columnName,value,value.getClass().getSimpleName().toLowerCase());
    }

    public ContentValues getColumnsFromSelect(){
        return newColumnsOnSelect;
    }

    private void addValuesByType(ContentValues content,String name,Object value,String type) {

        if(type.equals("string"))
            type = "text";

        if ( value == null )
            content.putNull(name);
        else if (type.equals("integer")) {
            if(!value.getClass().getSimpleName().equals(type))
                try {
                    content.put(name,Integer.parseInt((String)value));
                }catch (ClassCastException e){
                    content.put(name,(Integer)value);
                }
            else
                content.put(name,(Integer)value);
        }else if (type.equals("text")) {
            content.put(name,value.toString());
        }else if (type.equals("real")) {
            if(!value.getClass().getSimpleName().equals(type))
                try{
                    content.put(name,Double.parseDouble((String) value));
                }catch (ClassCastException e){
                    content.put(name,(Double)value);
                }
            else
                content.put(name,(Double)value);
        }else if (type.equals("date")) {
            if(!value.getClass().getSimpleName().equals(type))
                try{
                    content.put(name, Long.parseLong((String)value));
                }catch (ClassCastException e){
                    content.put(name,(Long)value);
                }
            else
                content.put(name,(Long)value);
        }else if(type.equals("long")){
            if(!value.getClass().getSimpleName().equals(type))
                try{
                    content.put(name,Long.parseLong((String) value));
                }catch (ClassCastException e){
                    content.put(name,(Long)value);
                }
            else
                content.put(name,(Long)value);
        }
    }

    private String getValuesByTypeAsString(EntityColumn content){
        String value = "";

        if (content.getType() == EntityColumn.ColumnType.INTEGER) {
            if(content.getValue() != null) value = ((Integer)content.getValue()).toString();

        }else if (content.getType() == EntityColumn.ColumnType.TEXT) {
            if(content.getValue() != null) value = ((String)content.getValue());

        }else if (content.getType() == EntityColumn.ColumnType.REAL) {
            if(content.getValue() != null) value = ((Double)content.getValue()).toString();

        }else if (content.getType() == EntityColumn.ColumnType.DATE) {
            if(content.getValue() != null) value = ((Long)((Date)content.getValue()).getTime()).toString();

        }else
        if (content.getValue() != null) value = ((Double)content.getValue()).toString();

        return value;
    }

    private void addValuesByType(EntityColumn content,Object value){
        if ( value == null ){

        }else if (content.getType() == EntityColumn.ColumnType.INTEGER) {

            if(!value.getClass().getSimpleName().toLowerCase()
                    .equals(content.getType().toString().toLowerCase()))
                content.setValue(Integer.parseInt((String) value));
            else
                content.setValue((Integer) value);

        }else if (content.getType() == EntityColumn.ColumnType.TEXT) {

            content.setValue(value.toString());

        }else if (content.getType() == EntityColumn.ColumnType.REAL) {

            if(!value.getClass().getSimpleName().toLowerCase()
                    .equals(content.getType().toString().toLowerCase()))
                content.setValue(Double.parseDouble((String) value));
            else
                content.setValue((Double) value);

        }else if (content.getType() == EntityColumn.ColumnType.DATE) {
            Date d = new Date(Long.parseLong(value+""));
            content.setValue(d);
        }else
        if(!value.getClass().getSimpleName().toLowerCase()
                .equals(content.getType().toString().toLowerCase()))
            content.setValue(Double.parseDouble((String) value));
        else
            content.setValue((Integer) value);
    }

    public EntityColumn getColumn(String index) {
        if(hashcolumns.containsKey(index))
            return columns.get(hashcolumns.get(index));
        return null;
    }

    private EntityColumn createColumn(String name, EntityColumn.ColumnType type){

        if(type == EntityColumn.ColumnType.INTEGER)
            return new EntityColumn<Integer>(name,type);
        else if(type == EntityColumn.ColumnType.TEXT)
            return new EntityColumn<String>(name,type);
        else if(type == EntityColumn.ColumnType.NUMERIC)
            return new EntityColumn<Long>(name,type);
        else if(type == EntityColumn.ColumnType.REAL)
            return new EntityColumn<Double>(name,type);
        else
            return new EntityColumn<Date>(name,type);
    }

    public String getColumnValueListAsString() {

        String description = "";

        if (columnValueList.size() > 0) {
            for (String name : columnValueList.keySet()) {
                description += columnValueList.getAsString(name);
            }
        }

        return description;
    }
}