package com.boxico.android.kn.contacts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.boxico.android.kn.contacts.persistencia.DataBaseManager;
import com.boxico.android.kn.contacts.persistencia.dtos.CategoriaDTO;
import com.boxico.android.kn.contacts.util.ConstantsAdmin;
import com.boxico.android.kn.contacts.util.KNArrayAdapter;

public class ProteccionCategoriaActivity extends ListActivity {
	
	private Button botonRegistrarContrasenia = null;
	private Button botonActivarContrasenia = null;
	private Button botonDesactivarContrasenia = null;
	private EditText contrasenia1 = null;
	private EditText contrasenia2 = null;
	private EditText mailPassword = null;
	private TextView labelCategorias = null;
	private ProteccionCategoriaActivity me = null;
	private ImageView imagen = null;
	//	private ArrayList<Cursor> allMyCursors = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	//	allMyCursors = new ArrayList<>();
		me = this;
		this.setContentView(R.layout.proteccion_categorias);
		this.inicializarList();
		this.inicializarCategoriasProtegidas();
		this.registrarWidgets();
		this.configurarBotonRegistrarContrasenia();
		this.configurarBotonActivarContrasenia();
		this.configurarBotonDesactivarContrasenia();
		this.habilitarCampos();
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
	}
	/*
    @Override
    public void startManagingCursor(Cursor c) {
    	allMyCursors.add(c);
        super.startManagingCursor(c);
    }
	*/
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	super.onActivityResult(requestCode, resultCode, intent);
    	
    	switch (requestCode) {
		case ConstantsAdmin.ACTIVITY_EJECUTAR_ACTIVAR_CONTRASENIA:
			this.habilitarCampos();
			break;
		default:
			//this.configurarSpinner();
			break;
		}  	
    	//this.resetAllMyCursors();
    	
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
	private void habilitarCampos(){
		if(ConstantsAdmin.contrasenia.isActiva()){// Nunca se registro la contrasenia
			contrasenia1.setEnabled(true);
			contrasenia2.setEnabled(true);
			botonRegistrarContrasenia.setEnabled(true);
			mailPassword.setEnabled(true);
			botonRegistrarContrasenia.setTextColor(getResources().getColor(R.color.color_azul));

			botonActivarContrasenia.setEnabled(false);
			botonActivarContrasenia.setTextColor(getResources().getColor(R.color.color_negro));

			botonDesactivarContrasenia.setEnabled(true);
			botonDesactivarContrasenia.setTextColor(getResources().getColor(R.color.color_azul));
			
			imagen.setBackground(getResources().getDrawable(R.drawable.candado_abierto));
		}else{
			contrasenia1.setEnabled(false);
			contrasenia2.setEnabled(false);
			botonRegistrarContrasenia.setEnabled(false);			
			mailPassword.setEnabled(false);
			botonRegistrarContrasenia.setTextColor(getResources().getColor(R.color.color_negro));

			botonActivarContrasenia.setEnabled(true);
			botonActivarContrasenia.setTextColor(getResources().getColor(R.color.color_azul));
			
			botonDesactivarContrasenia.setEnabled(false);
			botonDesactivarContrasenia.setTextColor(getResources().getColor(R.color.color_negro));
			imagen.setBackground(getResources().getDrawable(R.drawable.candado_cerrado));
		//	imagen.setBackgroundDrawable(dCandadoCerrado);
		}

		if(ConstantsAdmin.contrasenia.getId() == -1){
			this.getListView().setVisibility(View.GONE);
			labelCategorias.setVisibility(View.GONE);
		}else if(ConstantsAdmin.contrasenia.isActiva()){
			this.getListView().setVisibility(View.VISIBLE);
			labelCategorias.setVisibility(View.VISIBLE);
			this.mostrarCantidadCategoriasProtegidas();
			this.getListView().setEnabled(true);
			
		}else{
			this.getListView().setVisibility(View.GONE);
			labelCategorias.setVisibility(View.GONE);

		}
		
	}
	
	private void mostrarCantidadCategoriasProtegidas(){
		labelCategorias.setText( this.getString(R.string.label_cantidad_categorias_protegidas) + " (" + ConstantsAdmin.categoriasProtegidas.size() + "/" + this.getListAdapter().getCount() + ")");
	}
	
	private void registrarWidgets(){
		contrasenia1 = this.findViewById(R.id.contrasenia1);
		contrasenia2 = this.findViewById(R.id.contrasenia2);
		mailPassword = this.findViewById(R.id.mailPassword);
		if(ConstantsAdmin.contrasenia.getContrasenia()!= null && !ConstantsAdmin.contrasenia.getContrasenia().equals("")){
			contrasenia1.setText(ConstantsAdmin.contrasenia.getContrasenia());
			contrasenia2.setText(ConstantsAdmin.contrasenia.getContrasenia());
			mailPassword.setText(ConstantsAdmin.contrasenia.getMail());
		}
		botonRegistrarContrasenia = this.findViewById(R.id.botonRegistrarContrasenia);
		botonActivarContrasenia = this.findViewById(R.id.botonActivarContrasenia);
		botonDesactivarContrasenia = this.findViewById(R.id.botonDesactivarContrasenia);
		imagen = this.findViewById(R.id.imagenProtegerCategorias);
	//	Drawable dCandadoAbierto = getResources().getDrawable(R.drawable.candado_abierto);
	//	Drawable dCandadoCerrado = getResources().getDrawable(R.drawable.candado_cerrado);
	}
	
    private void configurarBotonRegistrarContrasenia(){
    	botonRegistrarContrasenia.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				registrarContrasenia();
			}
		});
    	
    }
    
    private void configurarBotonActivarContrasenia(){
    	botonActivarContrasenia.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openVerActivarContrasenia();
			}
		});
    	
    }
    
    private void configurarBotonDesactivarContrasenia(){
    	botonDesactivarContrasenia.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(ConstantsAdmin.contrasenia.getId() != -1){
					DataBaseManager mDBManager = DataBaseManager.getInstance(me);
					ConstantsAdmin.contrasenia.setActiva(false);
					ConstantsAdmin.actualizarContrasenia(ConstantsAdmin.contrasenia, mDBManager);
					ConstantsAdmin.resetPersonasOrganizadas();
					habilitarCampos();
					contrasenia1.setText(ConstantsAdmin.contrasenia.getContrasenia());
					contrasenia2.setText(ConstantsAdmin.contrasenia.getContrasenia());
					ConstantsAdmin.mostrarMensaje(me,  me.getString(R.string.mensaje_activacion_proteccion));
				}else{
					ConstantsAdmin.mostrarMensaje(me, me.getString(R.string.mensaje_debe_registrar_contrasenia));
				}
			}
		});
    	
    }
    
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        this.activarODesactivarCategoria(l, position, v);   
        
    }
    
    private void activarODesactivarCategoria(ListView list, int position, View v){
    	CategoriaDTO catSelected = (CategoriaDTO) list.getItemAtPosition(position);
		LinearLayout ll = (LinearLayout)v;
		TextView tv = (TextView) ll.getChildAt(0);
    	if(this.estaRegistradaCategoria(catSelected)){
    		this.eliminarCategoriaProtegida(catSelected);
    		tv.setTextColor(getResources().getColor(R.color.color_azul));
    	}else{
    		this.agregarCategoriaProtegida(catSelected);
    		tv.setTextColor(getResources().getColor(R.color.color_gris_oscuro));
    	}
/*    	LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
     	layoutInflater.inflate(R.layout.categoria_row, ll);*/
    	this.mostrarCantidadCategoriasProtegidas();
    }
    
    private boolean estaRegistradaCategoria(CategoriaDTO catSelected){
    	boolean result = false;
    	CategoriaDTO cat;
    	Iterator<CategoriaDTO> it = ConstantsAdmin.categoriasProtegidas.iterator();
    	while(!result && it.hasNext()){
    		cat = it.next();
    		result = cat.getNombreReal().equals(catSelected.getNombreReal());
    	}
    	return result;
    	
    }
    
    private void eliminarCategoriaProtegida(CategoriaDTO catSelected){
    	Iterator<CategoriaDTO> it = ConstantsAdmin.categoriasProtegidas.iterator();
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
    	CategoriaDTO cat;
    	boolean encontrada = false;
    	ConstantsAdmin.eliminarCategoriaProtegida(catSelected, mDBManager);
    	while(it.hasNext() && !encontrada){
    		cat = it.next();
    		if(cat.getNombreReal().equals(catSelected.getNombreReal())){
    			it.remove();
    			encontrada = true;
    		}
    	}
    }
    
    private void agregarCategoriaProtegida(CategoriaDTO catSelected){
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
    	ConstantsAdmin.categoriasProtegidas.add(catSelected);
    	ConstantsAdmin.crearCategoriaProtegida(catSelected, mDBManager);
    }

    
    
    private void openVerActivarContrasenia() {
        Intent i = new Intent(this, ActivarContraseniaActivity.class);
        this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_ACTIVAR_CONTRASENIA);
    }
    
    private void cargarDatosContrasenia(String pass, String mail){
    	long id;
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
    	ConstantsAdmin.contrasenia.setActiva(true);
    	ConstantsAdmin.contrasenia.setContrasenia(pass);
    	ConstantsAdmin.contrasenia.setMail(mail);
    	id = ConstantsAdmin.crearContrasenia(ConstantsAdmin.contrasenia, mDBManager);
    	ConstantsAdmin.contrasenia.setId(id);
		ConstantsAdmin.resetPersonasOrganizadas();

    	this.habilitarCampos();
    }
    
    private void registrarContrasenia(){
    	String c1 = contrasenia1.getText().toString();
    	String c2 = contrasenia2.getText().toString();
    	String c3 = mailPassword.getText().toString();
    	String mensaje;
    	if(c1.trim().equals("")){
    		mensaje = this.getString(R.string.mensaje_complete_contrasenia);
    	}else if(c2.trim().equals("")){
    		mensaje = this.getString(R.string.mensaje_complete_repeticion_contrasenia);
    	}else if(!c1.equals(c2)){
    		mensaje = this.getString(R.string.mensaje_contrasenia_no_coinciden);
    	}else if(c3.trim().equals("")){
    		mensaje =  this.getString(R.string.mensaje_debe_ingresar_mail);
    	}else{
    		mensaje =  this.getString(R.string.mensaje_contrasenia_registrada_ok);
    		cargarDatosContrasenia(c1, c3);
    		try {
    		//	ConstantsAdmin.almacenarContraseniaEnArchivo(this);
    			ConstantsAdmin.enviarMailContraseniaCategoriasProtegidas(this);
			} catch (Exception e) {
				ConstantsAdmin.mostrarMensajeDialog(this, e.getMessage());
			}
    		
    		
    	}
		ConstantsAdmin.mostrarMensaje(this, mensaje);
	}
	
	private void inicializarList(){
        this.getListView().setItemsCanFocus(false);
        this.getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        this.getListView().setFastScrollEnabled(true);
        labelCategorias = this.findViewById(R.id.tituloCategorias);
	}

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
	
	
	private void inicializarCategoriasProtegidas(){
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		List<CategoriaDTO> categoriasActivas = ConstantsAdmin.obtenerCategoriasActivas(this, null, mDBManager);
		List<CategoriaDTO> categoriasPersonalesActivas = ConstantsAdmin.obtenerCategoriasActivasPersonales(this, mDBManager);
		categoriasActivas.addAll(categoriasPersonalesActivas);
		this.cambiarNombreCategorias(categoriasActivas);
		Collections.sort(categoriasActivas);
		
//        setListAdapter(new KNArrayAdapter(this,R.layout.categoria_row, categoriasActivas));
        setListAdapter(new KNArrayAdapter(this, R.layout.categoria_row, R.id.text1, categoriasActivas, true));


	}


}
