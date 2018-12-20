package com.boxico.android.kn.contacts;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.boxico.android.kn.contacts.persistencia.DataBaseManager;
import com.boxico.android.kn.contacts.persistencia.dtos.CategoriaDTO;
import com.boxico.android.kn.contacts.util.ConstantsAdmin;
import com.boxico.android.kn.contacts.util.KNArrayAdapter;
import com.boxico.android.kn.contacts.util.KNListFragment;

public class ListadoCategoriaActivity extends KNListFragment  {
	

//	private ArrayList<Cursor> allMyCursors = null;
	private TextView labelCategorias = null;
	private int cantActivas = 0;
	private int cantCategorias = 0;
	private String titulo = null;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  allMyCursors = new ArrayList<>();
        this.setContentView(R.layout.list_categorias);
        this.registrarWidgets();
        this.configurarList(getListView());
        
    }


	private void registrarWidgets(){
		labelCategorias = this.findViewById(R.id.categoriaTextView);
		titulo = labelCategorias.getText().toString();
	}
/*
    private void resetAllMyCursors(){
    	Cursor cur;
		for (Cursor allMyCursor : allMyCursors) {
			cur = allMyCursor;
			cur.close();
			this.stopManagingCursor(cur);
		}
    	allMyCursors = new ArrayList<>();
    }
  	
	*/
    private void cambiarNombreCategorias(List<CategoriaDTO> categorias){
  		Iterator<CategoriaDTO> it = categorias.iterator();
  		CategoriaDTO catTemp;
  		String nombreRelativo;
  		while(it.hasNext()){
  			catTemp = it.next();
  			nombreRelativo = ConstantsAdmin.obtenerNombreCategoria(catTemp.getNombreReal(), this);
  			if(nombreRelativo == null){
  				nombreRelativo = catTemp.getNombreReal();
  			}
  			catTemp.setNombreRelativo(nombreRelativo);
  		}
  	}
	
    
    private void configurarList(ListView listView){
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setFastScrollEnabled(true);

        List<CategoriaDTO> categorias = ConstantsAdmin.obtenerCategorias(this, null, mDBManager);
		this.cambiarNombreCategorias(categorias);
		Collections.sort(categorias);
        
        setListAdapter(new KNArrayAdapter(this, R.layout.categoria_row, R.id.text1, categorias, false));
        CategoriaDTO cat;
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

        this.configurarBotonMisCategorias();
		
	}
	
	private void configurarBotonMisCategorias(){
		Button misCat;
		misCat = this.findViewById(R.id.botonMisCategorias);
		misCat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	verMisCategorias();
            }
        });
	}
	
	private void verMisCategorias(){
        Intent i = new Intent(this, MisCategoriasActivity.class);
        this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_MIS_CATEGORIAS);
	}
	/*
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        this.activarODesactivarCategoria(l, position, v);
        //this.configurarList(l);
    }
    */
/*    private long cantidadCategoriasActivas(ListView list){
    	long result = 0;
    	int iterar = 0;
    	CategoriaDTO catTemp = null;
    	while(iterar < list.getAdapter().getCount()){
    		catTemp = (CategoriaDTO) list.getItemAtPosition(iterar);
    		result = result + catTemp.getActiva();
    		iterar ++;
    	}
    	return result;
    }
  */  
    private void activarODesactivarCategoria(ListView list, int position, View v){
		LinearLayout ll = (LinearLayout)v;
		TextView tv = (TextView) ll.getChildAt(0);
//    	long cantCatActivas = this.cantidadCategoriasActivas(list);
    	CategoriaDTO catSelected = (CategoriaDTO) list.getItemAtPosition(position);
    	if(!(cantActivas == 1 && catSelected.getActiva() == 1)){
			DataBaseManager mDBManager = DataBaseManager.getInstance(this);
	    	if(catSelected.getActiva()==1){
	    		catSelected.setActiva(0);
	    		cantActivas--;
	    		//list.setItemChecked(position, false);
	    		tv.setTextColor(getResources().getColor(R.color.color_azul));	    		
	    	}else{
	    		catSelected.setActiva(1);
	    		cantActivas++;
	    		//list.setItemChecked(position, true);
	    		tv.setTextColor(getResources().getColor(R.color.color_gris_oscuro));
	    	}
	    	ConstantsAdmin.actualizarCategoria(catSelected, mDBManager);
	        labelCategorias.setText(titulo + " (" + cantActivas + "/" + cantCategorias + ")");
    	
    	}else{
    		
    		ConstantsAdmin.mostrarMensaje(this, getString(R.string.errorDesactivandoCategorias));
    		list.setItemChecked(position, true);
    	}
    }


	protected void onResume() {
        super.onResume();
        if(ConstantsAdmin.mainActivity == null){
        	Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(this.getPackageName());
        	startActivity(LaunchIntent);
        	this.finish();
        	ConstantsAdmin.cerrarMainActivity = true;

        }
        /*else{
        	this.resetAllMyCursors();	
        }
        */
    }

/*
    @Override
    public void startManagingCursor(Cursor c) {
    	allMyCursors.add(c);
        super.startManagingCursor(c);
    }

*/
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	//	super.onListItemClick(l, v, position, id);
		this.activarODesactivarCategoria(this.getListView(), position, view);
	}


}
