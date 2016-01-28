package com.delacrmi.persistences;

/**
 * Created by miguel on 13/11/15.
 */
public class EntityColumn<valueType> {

    private String name;
    private ColumnType type;
    private boolean isNullable = true;
    private boolean isPrimaryKey = false;
    private boolean autoIncrement = false;
    private boolean serverColumn = true;
    private valueType defaultValue = null;
    private valueType value = null;
    private EncryptionType encryption = null;
    private int flag = 0;

    public EntityColumn(){}

    public EntityColumn(String name){
        this.name = name;
    }

    public EntityColumn(String name,ColumnType columnType){
        this.name = name;
        type = columnType;
    }

    public EntityColumn(String name,ColumnType columnType, valueType defaultValue){
        this.name = name;
        type = columnType;
        this.defaultValue = defaultValue;
    }

    public EntityColumn(String name,ColumnType columnType, boolean isPrimaryKey){
        this.name = name;
        type = columnType;
        this.isPrimaryKey = isPrimaryKey;
    }

    public EntityColumn(String name, boolean isPrimaryKey){
        this.name = name;
        this.isPrimaryKey = isPrimaryKey;
        type = ColumnType.INTEGER;
    }

    public EntityColumn(String name,ColumnType columnType,boolean isPrimaryKey,
                        boolean autoIncrement, valueType defaultValue){
        this.name = name;
        type = columnType;
        this.isPrimaryKey = isPrimaryKey;
        this.autoIncrement = autoIncrement;
        this.defaultValue = defaultValue;
        isNullable = false;
    }

    public EntityColumn(String name,ColumnType columnType,boolean isPrimaryKey,boolean autoIncrement){
        this.name = name;
        type = columnType;
        this.isPrimaryKey = isPrimaryKey;
        this.autoIncrement = autoIncrement;
        isNullable = false;
    }


    public boolean isNullable() {
        return isNullable;
    }
    public EntityColumn setNotNullable() {
        if(isPrimaryKey)
            throw new IllegalStateException("Can't set the nullable option if this column is primary key");
        this.isNullable = false;
        return this;
    }

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }
    public EntityColumn setPrimaryKey() {
        isNullable = false;
        this.isPrimaryKey = true;
        return this;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }
    public EntityColumn setAutoIncrement() {
        this.autoIncrement = true;
        return this;
    }

    public valueType getDefaultValue(){
        return defaultValue;
    }
    public EntityColumn setDefaultValue(valueType defaultValue){
        this.defaultValue = defaultValue;
        return this;
    }

    public ColumnType getType() {
        return type;
    }
    public EntityColumn setType(ColumnType type){
        this.type = type;
        return this;
    }

    public String getName() {
        return name;
    }
    public EntityColumn setName(String name){
        this.name = name;
        return this;
    }

    public boolean isServerColumn() {
        return serverColumn;
    }
    public EntityColumn setOutServerColumn() {
        this.serverColumn = true;
        return this;
    }

    public valueType getValue(){
        return value;
    }
    public void setValue(valueType value){
        this.value = value;
    }

    public String getCreateString(){
        String create = getName()+" ";

        if(type == ColumnType.DATE)
            create += ColumnType.NUMERIC+" ";
        else
            create += type;

        if(isPrimaryKey())
            create += "PRIMARY KEY ";

        if(autoIncrement)
            create += "AUTOINCREMENT ";

        if(!isNullable)
            create += " NOT NULL";

        return create;
    }

    public EncryptionType getEncryption() {
        return encryption;
    }
    public int getEncodeFlag(){
        return flag;
    }

    public EntityColumn setEncryption(EncryptionType encryption, int flag) {
        this.encryption = encryption;
        this.flag = flag;
        return this;
    }

    public enum ColumnType{
        TEXT,INTEGER,REAL,DATE,NUMERIC
    }

    public enum EncryptionType{
        BS64
    }
}
