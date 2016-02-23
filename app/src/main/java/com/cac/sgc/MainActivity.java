package com.cac.sgc;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cac.R;
import com.cac.entities.*;
import com.cac.services.SyncServerService;
import com.cac.tools.BackupBD;
import com.cac.tools.MainComponentEdit;
import com.cac.tools.PrinterManager;
import com.cac.tools.ServerStarter;
import com.cac.viewer.*;
import com.delacrmi.connection.ConnectSQLite;
import com.delacrmi.persistences.Entity;
import com.delacrmi.persistences.EntityManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FragmentManager frm;
    private Fragment actualFragment;
    private String ACTUALFRAGMENT = "MainFragment";

    private static String USER = "";

    //App Menu
    private TextView tv_userName;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private ImageButton btn_fab_right;
    private ImageButton btn_fab_left;
    private RelativeLayout btnContainer;
    private List<View> listOfViews;

    //Fragments
    private MainFragment mainFragment;
    private SyncFragment syncFragment;
    private SettingFragment settingFragment;
    private Formulario1 formulario1;
    private Formulario2 formulario2;
    private Formulario3 formulario3;
    private Formulario4 formulario4;
    private Listado listado;

    //Events References
    private View.OnClickListener onClickListener;

    private EntityManager entityManager;

    private ServerStarter serverStarter;

    //Preferences
    private SharedPreferences sharedPreferences;

    public static int VISIBLE_ACTION = GridLayout.LayoutParams.WRAP_CONTENT;

    private AlertDialog dialog = null;
    private View dialogLayout;
    private EditText userName;
    private EditText password;

    private MenuItem statusConnection;

    //<editor-fold desc="Override Methods">

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        events();

        if(savedInstanceState != null)
            ACTUALFRAGMENT = savedInstanceState.getString("started");

        initComponent();

        //Working with the services class
        startService(new Intent(this, SyncServerService.class));

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SyncServerService.SGC);
        serverStarter = new ServerStarter(this);

        try {
            registerReceiver(serverStarter, intentFilter);
        }catch (NullPointerException npe){}

        //Setting up data base.
        initializeDataBase();

    }

    @Override
    protected void onDestroy() {
        if ( serverStarter != null )
            unregisterReceiver(serverStarter);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        configMenu(data.getExtras().getString("user"), true);
        //returningWithResult = true;
    }

    private void initializeDataBase() {
        getEntityManager().addTable(Fincas.class)
                .addTable(Caniales.class)
                .addTable(Empresas.class)
                .addTable(Frentes.class)
                .addTable(Vehiculos.class)
                .addTable(Lotes.class)
                .addTable(EmpleadoCabezal.class)
                .addTable(EmpleadoCosechadora.class)
                .addTable(EmpleadoDigitador.class)
                .addTable(EmpleadoTractor.class)
                .addTable(Rangos.class)
                .addTable(Transaccion.class)
                .addTable(Periodos.class)
                .addTable(Users.class)
                .addTable(FormaTiro.class)
                .addTable(ClaveCorte.class)
                .init();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("started", ((MainComponentEdit) actualFragment).getTAG());
        outState.putString("user", USER);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        statusConnection = menu.findItem(R.id.connect);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if(!drawerLayout.isDrawerOpen(Gravity.LEFT)) drawerLayout.openDrawer(Gravity.LEFT);
        else drawerLayout.closeDrawers();
    }

    //</editor-fold>

    private void configMenu(String user, boolean show){

        try {
            JSONObject json = new JSONObject(user);
            Menu menu;

            if (show) Snackbar.make(btn_fab_left, getResources().getString(R.string.login_success), Snackbar.LENGTH_SHORT).show();

            String userLow;

            userLow = json.getString(Users.ROLE).toLowerCase();

            if(json.has(Users.USER)) tv_userName.setText(json.getString(Users.USER));
            else tv_userName.setText(json.getString(Users.EMAIL));

            startTransactionByFragmentTag(MainFragment.getInstance().getTAG());

            if (userLow.equals("user")){
                menu = navigationView.getMenu();
                menu.findItem(R.id.client_sync).setEnabled(false);
                menu.findItem(R.id.server_sync).setEnabled(false);
                menu.findItem(R.id.setting).setEnabled(false);
                menu.findItem(R.id.nav_bk_database).setEnabled(false);
                menu.findItem(R.id.nav_up_database).setEnabled(false);
            }else if(userLow.equals("admin")){
                menu = navigationView.getMenu();
                menu.findItem(R.id.client_sync).setEnabled(true);
                menu.findItem(R.id.server_sync).setEnabled(true);
                menu.findItem(R.id.setting).setEnabled(true);
                menu.findItem(R.id.nav_bk_database).setEnabled(true);
                menu.findItem(R.id.nav_up_database).setEnabled(true);
            }else{
                USER = "";
                finish();
            }

            drawerLayout.invalidate();
            USER = user;

        } catch (JSONException e) {
            initOtherActivity();
        }

    }

    private void initOtherActivity (){
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivityForResult(intent, 0);
    }

    private void initComponent(){
        //Bar Menu
        toolbar = (Toolbar)findViewById(R.id.appbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setSubtitle(R.string.app_description_name);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle =
                new ActionBarDrawerToggle(this,drawerLayout,toolbar,
                        R.string.openDrawer, R.string.closeDrawer){

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                        super.onDrawerClosed(drawerView);
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                        super.onDrawerOpened(drawerView);
                    }
                };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView = (NavigationView)findViewById(R.id.nav_view);
        setDrawerMenu();

        btn_fab_right = (ImageButton)findViewById(R.id.btn_fab_right);
        btn_fab_right.setOnClickListener(onClickListener);

        btn_fab_left = (ImageButton)findViewById(R.id.btn_fab_left);
        btn_fab_left.setOnClickListener(onClickListener);

        btnContainer = (RelativeLayout) findViewById(R.id.main_body_action_layout);

        tv_userName = (TextView)findViewById(R.id.nav_userName);

        listOfViews = new ArrayList<>();
        listOfViews.add(btn_fab_left);
        listOfViews.add(btn_fab_right);
        listOfViews.add(btnContainer);

        dialogLayout = getLayoutInflater().inflate(R.layout.login_dialog, null);
        userName = (EditText)dialogLayout.findViewById(R.id.dialig_username);
        password = (EditText)dialogLayout.findViewById(R.id.dialog_password);

        //init fist main fragment
        frm = getFragmentManager();
        startTransactionByFragmentTag(ACTUALFRAGMENT);

        if(USER.equals("")) initOtherActivity();
        else configMenu(USER,false);

    }

    private void events(){
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainComponentEdit)actualFragment).onClickFloating(v);
            }
        };
    }

    public EntityManager getEntityManager() {
        if ( entityManager == null ) {
            entityManager = new EntityManager(this,
                    getResources().getString(R.string.db_name),
                    null,
                    Integer.parseInt(getResources().getString(R.string.db_version))){

                @Override
                public void onCreateDataBase(ConnectSQLite conn, SQLiteDatabase db) {
                    List<List<Entity>> entities = new ArrayList<List<Entity>>();
                    entities.add(new Users().getDefaultInsert());
                    conn.setEntitiesBackup(entities);

                }

                @Override
                public void onDataBaseCreated(ConnectSQLite conn, SQLiteDatabase db) {
                    for(List<Entity> entities : (List<List<Entity>>)conn.getEntitiesBackup())
                        for (Entity entity : entities)
                            db.insert(entity.getName(),null,entity.getContentValues());
                }

                @Override
                public void onDatabaseUpdate(ConnectSQLite conn, SQLiteDatabase db) {
                    List<List<Entity>> entities = new ArrayList<List<Entity>>();
                    List<Entity> value = new ArrayList<Entity>();

                    entities.add(new Users().getDefaultInsert());

                    //Backingup database tables.
                    for (String str : getTablesNames()){
                        Cursor cursor = db.rawQuery("select * from "+str,null);
                        setListFromCursor(cursor,value,getClassByName(str));
                    }

                    if(value != null)
                        entities.add(value);

                    conn.setEntitiesBackup(entities);
                }

                @Override
                public void onUpdatedDataBase(ConnectSQLite conn, SQLiteDatabase db){
                    for(List<Entity> entities : (List<List<Entity>>)conn.getEntitiesBackup())
                        for (Entity entity : entities)
                            db.insert(entity.getName(),null,entity.getContentValues());
                }

            };
        }
        return entityManager;
    }

    /**
     *@args: Fragment fragment
     * This method is the responsible to change the dynamics fragments
     */
    private void startTransaction(Fragment fragment){
        FragmentTransaction frt = frm.beginTransaction();
        frt.replace(R.id.main_body_layout, fragment, ACTUALFRAGMENT);
        frt.commit();

        actualFragment = fragment;
        ((MainComponentEdit) fragment).MainViewConfig(listOfViews);
        getSupportActionBar().setSubtitle(((MainComponentEdit) fragment).getSubTitle());
        try{hideKeyboard();}catch (Exception ex){}
    }

    /**
     * Metodo utilizado para mostrar un fragmento segun su tag.
     *
     * @param: String que representa el fragmento.
     * */
    public void startTransactionByFragmentTag(String tag){
        switch (tag){
            case MainFragment.TAG:
                startTransaction(getMainFragment());
                break;
            case SyncFragment.TAG:
                startTransaction(getSyncFragment());
                break;
            case SettingFragment.TAG:
                startTransaction(getSettingFragment());
                break;
            case Formulario1.TAG:
                startTransaction((Fragment) getFormulario1Fragment());
                break;
            case Formulario2.TAG:
                startTransaction(getFormulario2Fragment());
                break;
            case Formulario3.TAG:
                startTransaction(getFormulario3Fragment());
                break;
            case Formulario4.TAG:
                startTransaction(getFormulario4Fragment());
                break;
            case Listado.TAG:
                startTransaction(getListadoFragment());
                break;
        }
    }

    //<editor-fold desc="Get's">
    public Fragment getListadoFragment() {
        if ( listado == null )
            listado = new Listado().init(this,entityManager);
        return listado;
    }

    public MainFragment getMainFragment(){
        if(mainFragment == null)
            mainFragment = MainFragment.init(this);
        return mainFragment;
    }

    public SyncFragment getSyncFragment(){
        if(syncFragment == null)
            syncFragment = SyncFragment.init(this, getEntityManager(), sharedPreferences.getString("etp_uri1", sharedPreferences.getString("etp_uri2", "")));
        return syncFragment;
    }

    public SettingFragment getSettingFragment(){
        if(settingFragment == null)
            settingFragment = SettingFragment.getInstance(this);
        return settingFragment;
    }

    public Formulario1 getFormulario1Fragment(){
        if ( formulario1 == null )
            formulario1 = new Formulario1().init(this,getEntityManager());
        return formulario1;
    }

    public Formulario2 getFormulario2Fragment(){
        if ( formulario2 == null )
            formulario2 = new Formulario2().init(this, getEntityManager());
        return formulario2;
    }

    public Formulario3 getFormulario3Fragment(){
        if ( formulario3 == null )
            formulario3 = new Formulario3().init(this, getEntityManager());
        return formulario3;
    }

    public Formulario4 getFormulario4Fragment(){
        if ( formulario4 == null )
            formulario4 = new Formulario4().init(this, getEntityManager());
        return formulario4;
    }
    //</editor-fold>

    /**
     * @args
     * Setting the Drawer Menu Options
     */
    public void setDrawerMenu(){
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.nav_home:
                                startTransaction(getMainFragment());
                                break;
                            case R.id.client_sync:
                                startTransaction(getSyncFragment());
                                break;
                            case R.id.setting:
                                startTransaction(getSettingFragment());
                                break;
                            case R.id.work_digitacion:
                                startTransaction(getFormulario1Fragment());
                                break;
                            case R.id.menu_envio_report:
                                startTransaction(getListadoFragment());
                                break;
                            case R.id.nav_change_user:
                                userName.setText("");
                                password.setText("");
                                userName.requestFocus();
                                if (dialog == null){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                                            .setView(dialogLayout)
                                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    String user = LoginActivity.findUser(userName.getText().toString(), password.getText().toString(),
                                                            getEntityManager()).getJSON().toString();
                                                    if (!user.equals("{}")) configMenu(user, true);
                                                    else
                                                        Snackbar.make(btn_fab_left, getResources().getString(R.string.login_reject), Snackbar.LENGTH_SHORT).show();

                                                    dialog.dismiss();
                                                }
                                            })
                                            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                }
                                            });
                                    dialog = builder.create();
                                }

                                dialog.show();

                                break;
                            case R.id.nav_sign_out:
                                USER = "";
                                initOtherActivity();
                                break;
                            case R.id.nav_select_bluetooth:
                                PrinterManager manager = new PrinterManager();
                                manager.setContext(MainActivity.this);
                                manager.chooseBluetoothDevice(false);
                                break;
                            case R.id.nav_bk_database:
                                BackupBD bk = new BackupBD(MainActivity.this,getEntityManager());
                                bk.execute(0);
                                break;
                            case R.id.nav_up_database:
                                BackupBD up = new BackupBD(MainActivity.this,getEntityManager());
                                up.execute(1);
                                break;
                        }
                        drawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    public void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public MenuItem getStatusConnection(){
        return statusConnection;
    }

    /*public void backupDataBaseOnFileDirectory() {
        try {
            for (Class entityClass : getEntityManager().getTables()) {
                Entity instance = getEntityManager().initInstance(entityClass);
                getEntityManager().exportEntityToXML(instance.getName(), getEntityManager().find(entityClass, "*", null, null));
            }
            Toast.makeText(this,"El backup fue realizado correctamente.",Toast.LENGTH_SHORT).show();
        } catch ( Exception ex ) {
            Toast.makeText(this,"Error al realizar el backup.",Toast.LENGTH_LONG).show();
            Log.e("Error","Al realizar el backup.",ex);
        }
    }*/
}