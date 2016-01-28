package com.cac.entities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.delacrmi.persistences.Entity;
import com.delacrmi.persistences.EntityColumn;
import com.delacrmi.persistences.EntityFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by delacrmi on 12/15/2015.
 */
public class Users extends Entity {

    public static String USER = "usuario";
    public static String PASSWORD = "contrasena";
    public static String EMAIL = "email";
    public static String ROLE = "role";

    @Override
    public Entity entityConfig (){

        setName("USUARIO_SISTEMAS");
        setNickName("Usuarios");

        addColumn(new EntityColumn<String>(USER, EntityColumn.ColumnType.TEXT));
        addColumn(new EntityColumn<String>(EMAIL, EntityColumn.ColumnType.TEXT));
        addColumn(new EntityColumn<String>(PASSWORD, EntityColumn.ColumnType.TEXT)
                .setEncryption(getEncryptionType(), getBaseEncryption()));
        addColumn(new EntityColumn<String>(ROLE, EntityColumn.ColumnType.TEXT));

        setSynchronizable(true);

        return this;

    }

    @Override
    public void configureEntityFilter(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String empresa  = sharedPreferences.getString("EMPRESA", "30");
        String app = sharedPreferences.getString("APLICACION", "SGC");
        setEntityFilter(new EntityFilter(new String[]{"id_empresa", "app"}, new String[]{empresa, app}));
    }

    @Override
    public List<Entity> getDefaultInsert(){
        List<Entity> list= new ArrayList<Entity>();
        Users user = (Users)new Users().entityConfig();

        user.setValue(Users.USER, "Admin");
        user.setValueToEncrypt(Users.PASSWORD, "1234");
        user.setValue(Users.EMAIL, "bkcac30@gmail.com");
        user.setValue(Users.ROLE, "ADMIN");

        list.add(user);

        return list;
    }

    public static int getBaseEncryption(){
        return Base64.DEFAULT;
    }

    public static EntityColumn.EncryptionType getEncryptionType(){
        return EntityColumn.EncryptionType.BS64;
    }
}
