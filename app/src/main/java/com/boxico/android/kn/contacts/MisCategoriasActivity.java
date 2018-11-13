package com.boxico.android.kn.contacts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.boxico.android.kn.contacts.persistencia.DataBaseManager;
import com.boxico.android.kn.contacts.persistencia.dtos.CategoriaDTO;
import com.boxico.android.kn.contacts.util.ConstantsAdmin;
import com.boxico.android.kn.contacts.util.KNArrayAdapter;

public class MisCategoriasActivity extends ListActivity {
	
	private ArrayList<Cursor> allMyCursors = null;
	private int cantActivas = 0;
	private int cantCategorias = 0;
	private String titulo = null;
	private TextView labelCategorias = null;
	
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allMyCursors = new ArrayList<>();
        this.setContentView(R.layout.admin_categorias);
        this.configurarBotonCategorias();
        this.configurarList();
    }
    
    protected void onResume() {
        super.onResume();
        if(ConstantsAdmin.mainActivity == null){
        	this.finish();
        	ConstantsAdmin.cerrarMainActivity = true;
        }
    }
    
    private void resetAllMyCursors(){
    	Cursor cur;
		for (Cursor allMyCursor : allMyCursors) {
			cur = allMyCursor;
			cur.close();
			this.stopManagingCursor(cur);
		}
    	allMyCursors = new ArrayList<>();
    }
    
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        this.activarODesactivarCategoria(l, position, v);
    }
    
    
    private void activarODesactivarCategoria(ListView list, int position, View v){
		LinearLayout ll = (LinearLayout)v;
		TextView tv = (TextView) ll.getChildAt(0);
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
     	CategoriaDTO catSelected = (CategoriaDTO) list.getItemAtPosition(position);
    	if(catSelected.getActiva()==1){
    		catSelected.setActiva(0);
    		cantActivas--;
    		tv.setTextColor(getResources().getColor(R.color.color_azul));	    		
    	}else{
    		catSelected.setActiva(1);
    		cantActivas++;
    		tv.setTextColor(getResources().getColor(R.color.color_gris_oscuro));	    		
    	}
    	ConstantsAdmin.inicializarBD( mDBManager);
    	mDBManager.actualizarCategoriaPersonal(catSelected);
    	ConstantsAdmin.finalizarBD(mDBManager);
        labelCategorias.setText(titulo + " (" + cantActivas + "/" + cantCategorias + ")");

    }
  /*  
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = null;
    	super.onCreateOptionsMenu(menu);
        item = menu.add(0, ConstantsAdmin.ACTIVITY_EJECUTAR_ALTA_CATEGORIA,0, R.string.menu_agregar_categoria);
        item.setIcon(R.drawable.categoria_add_menu_icon);
        return true;

    }*/
   
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case ConstantsAdmin.ACTIVITY_EJECUTAR_ALTA_CATEGORIA:
        	this.openAltaCategoria();
        	return true;
 	    }
        return super.onMenuItemSelected(featureId, item);
    }
    
    private void openAltaCategoria() {
        Intent i = new Intent(this, AltaCategoriaActivity.class);
        this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_ALTA_CATEGORIA);
    }
    
    private void refreshList(){
    	ListView listView = this.getListView();
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
    	CategoriaDTO cat;
		ConstantsAdmin.inicializarBD(mDBManager);
        Cursor categoriasCursor = mDBManager.fetchAllCategoriasPersonalesPorNombre(null);
        if(categoriasCursor!= null){
	        startManagingCursor(categoriasCursor);
	        List categorias = ConstantsAdmin.categoriasPersonalesCursorToDtos(categoriasCursor);
	       // stopManagingCursor(categoriasCursor);
	        Collections.sort(categorias);
	        
	        setListAdapter(new KNArrayAdapter(this, R.layout.categoria_row, R.id.text1, categorias, false));
	        int pos = 0;
	        cantActivas = 0;
	        cantCategorias = categorias.size();
	        while(pos < cantCategorias){
	        	cat = (CategoriaDTO) listView.getItemAtPosition(pos);
	        	if(cat.getActiva() == 1){
	        		cantActivas++;
	        	}
	        	pos++;
	        }
	        labelCategorias.setText(titulo + " (" + cantActivas + "/" + cantCategorias + ")");

        }
        ConstantsAdmin.finalizarBD(mDBManager);
    	
    }
    
	private void configurarBotonCategorias(){
		Button boton;
		boton = this.findViewById(R.id.botonMisCategorias);
		boton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	verCategorias();
            }
        });
		boton = this.findViewById(R.id.botonAgregarCategoria);
		boton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	openAltaCategoria();
            }
        });
		
		labelCategorias = this.findViewById(R.id.categoriaTextView);
		titulo = labelCategorias.getText().toString();


	}
	
	private void verCategorias(){
		this.finish();
	}

    
    
	private void configurarList(){
		ListView listView = this.getListView();
        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setFastScrollEnabled(true);

        this.registerForContextMenu(getListView());
        this.refreshList();
	}
	
    @Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
	    menu.add(0, ConstantsAdmin.ACTIVITY_EJECUTAR_EDITAR_CATEGORIA, 0, R.string.menu_editar_o_eliminar);
		
        // TODO: fill in rest of method
	}
    
	
    @Override
	public boolean onContextItemSelected(MenuItem item) {
    	CategoriaDTO cat;
    	switch(item.getItemId()) {
        case ConstantsAdmin.ACTIVITY_EJECUTAR_EDITAR_CATEGORIA:
            Intent i = new Intent(this, AltaCategoriaActivity.class);
            AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
            //catSelect = info.id;   
            int pos = (Long.valueOf(info.id)).intValue();
            cat = (CategoriaDTO)this.getListAdapter().getItem(pos);
            i.putExtra(ConstantsAdmin.CATEGORIA_SELECCIONADA, String.valueOf(cat.getId()));
            this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_ALTA_CATEGORIA);

            return true;
        }
        return super.onContextItemSelected(item);
		
        // TODO: fill in rest of method
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	super.onActivityResult(requestCode, resultCode, intent);
    	this.resetAllMyCursors();
    	this.refreshList();
    }
	
    @Override
    public void startManagingCursor(Cursor c) {
    	allMyCursors.add(c);
        super.startManagingCursor(c);
    }

}
