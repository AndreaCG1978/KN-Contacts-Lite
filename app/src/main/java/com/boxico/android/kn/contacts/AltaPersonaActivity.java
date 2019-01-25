package com.boxico.android.kn.contacts;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.boxico.android.kn.contacts.persistencia.DataBaseManager;
import com.boxico.android.kn.contacts.persistencia.dtos.CategoriaDTO;
import com.boxico.android.kn.contacts.persistencia.dtos.PersonaDTO;
import com.boxico.android.kn.contacts.persistencia.dtos.TipoValorDTO;
import com.boxico.android.kn.contacts.util.ConstantsAdmin;
import com.boxico.android.kn.contacts.util.KNSimpleCustomAdapter;

public class AltaPersonaActivity extends Activity  {
	
	private ArrayAdapter<CategoriaDTO> mSpinnerAdapt = null;
	private CategoriaDTO mCategoriaSeleccionada = null;
	private PersonaDTO mPersonaSeleccionada = null;
	
	//private ArrayList<Cursor> allMyCursors = null;
	
	private TextView mDateDisplay;
	private Button mPickDate;
	private CheckBox mCheckFechaNac;
	private int mYear;
	private int mMonth;
	private int mDay;
	private boolean mMostrarDatosPersonalesBoolean = false;
	private boolean mMostrarTelefonosBoolean = false;
	private boolean mMostrarEmailsBoolean = false;
	private boolean mMostrarDireccionesBoolean = false;
	private boolean mMostrarDatosPorCategoriaBoolean = false;
    // --Commented out by Inspection (12/11/2018 12:49):Drawable shapeLight = null;
	// --Commented out by Inspection (12/11/2018 12:44):Drawable shapeDark = null;
	// --Commented out by Inspection (12/11/2018 12:32):private TextView mDescripcionLabel = null;
	private TextView mNombContact = null;
	private Spinner mSpinner = null;

	private EditText mEntryApellido = null;
	private EditText mEntryNombre = null;
	private EditText mEntryDatoExtra = null;
	private EditText mEntryDescripcion = null;

	
	
	
	
	private ListView telefonosList = null;
	private ListView mailsList = null;
	private ListView direccionesList = null;
	
	private Button botonAddTel = null;
	private ImageButton botonAddMail = null;
	private ImageButton botonAddDir = null;
	
	private List<TipoValorDTO> telefonos = new ArrayList<>();
	private List<TipoValorDTO> mails = new ArrayList<>();
	private List<TipoValorDTO> direcciones = new ArrayList<>();
	
	private ArrayList<String> tiposTelefono = new ArrayList<>();
	private ArrayList<String> tiposEmail = new ArrayList<>();
	private ArrayList<String> tiposDireccion = new ArrayList<>();
	
	private static final String VALOR = "VALOR";
	private static final String TIPO = "TIPO";
	private static final String ID_TIPO_VALOR = "ID_TIPO_VALOR";
	private static final String ID_PERSONA = "ID_PERSONA";

	
	private TextView sinDatos = null;
	
	private boolean cambioCategoriaFlag = false;
	private boolean vieneDesdeDetalle = false;


	public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem item = menu.add(0, ConstantsAdmin.ACTIVITY_EJECUTAR_LISTADO_CATEGORIAS,0, R.string.menu_listar_categoria);
        item.setIcon(R.drawable.categoria_menu_icon);
        return true;

    }
/*
    @Override
    public void startManagingCursor(Cursor c) {
    	allMyCursors.add(c);
        super.startManagingCursor(c);
    }

*/
    
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case ConstantsAdmin.ACTIVITY_EJECUTAR_LISTADO_CATEGORIAS:
	    	this.openListadoCategoria();
	    	return true;
	    }
        return super.onMenuItemSelected(featureId, item);
    }
    
    private void openListadoCategoria(){
        Intent i = new Intent(this, ListadoCategoriaActivity.class);
        this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_LISTADO_CATEGORIAS);
    }
    

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	super.onActivityResult(requestCode, resultCode, intent);
    //	this.resetAllMyCursors();
		if(mPersonaSeleccionada.getId() != -1){
			this.cargarPersonaDto(String.valueOf(mPersonaSeleccionada.getId()));				
		}
    	switch (requestCode) {
		case ConstantsAdmin.ACTIVITY_EJECUTAR_LISTADO_CATEGORIAS:
			mSpinner.setSelection(0);
			this.configurarSpinner();
			break;
		case ConstantsAdmin.ACTIVITY_EJECUTAR_EDICION_TELEFONO:
			this.cargarTelefonos();
	        telefonosList.setAdapter(obtenerAdapterTelefono(telefonos));
			break;
		case ConstantsAdmin.ACTIVITY_EJECUTAR_ALTA_TELEFONO:
			this.cargarTelefonos();
	        telefonosList.setAdapter(obtenerAdapterTelefono(telefonos));
			break;	
		case ConstantsAdmin.ACTIVITY_EJECUTAR_EDICION_EMAIL:
			this.cargarEmails();
	        mailsList.setAdapter(obtenerAdapter(mails)); 			
			break;
		case ConstantsAdmin.ACTIVITY_EJECUTAR_ALTA_EMAIL:
			this.cargarEmails();
	        mailsList.setAdapter(obtenerAdapter(mails)); 		
			break;	
		case ConstantsAdmin.ACTIVITY_EJECUTAR_EDICION_DIRECCION:
			this.cargarDirecciones();
	        direccionesList.setAdapter(obtenerAdapter(direcciones)); 			
			break;
		case ConstantsAdmin.ACTIVITY_EJECUTAR_ALTA_DIRECCION:
			this.cargarDirecciones();
			direccionesList.setAdapter(obtenerAdapter(direcciones)); 		
			break;			
		default:
			this.configurarSpinner();
			break;
		}    	
    }
    
	
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
        	ConstantsAdmin.telefonosARegistrar = new ArrayList<>();
        	ConstantsAdmin.mailsARegistrar = new ArrayList<>();
        	ConstantsAdmin.direccionesARegistrar = new ArrayList<>();
        //	allMyCursors = new ArrayList<>();
            this.setContentView(R.layout.alta_persona);
            this.registrarWidgets();
            this.guardarPersonaSeleccionada(this.getIntent());
            this.configurarListaTelefonos();
            this.configurarListaDirecciones();
            this.configurarListaEmails();
            this.configurarDatePicker();
            this.configurarSpinner();
            mMostrarDatosPersonalesBoolean = true;
            this.configurarMasOMenosDatosPersonales();
            this.configurarMasOMenosTelefonos();
            this.configurarMasOMenosEmails();
            this.configurarMasOMenosDirecciones();
            this.configurarMasOMenosDatosPorCategoria();
            this.configurarBotonGuardar();
            this.configurarBotonGuardarYSalir();
            this.configurarBotonesAgregarTipoValor();
            if(mPersonaSeleccionada.getId() == 0 || mPersonaSeleccionada.getId() == -1){
                this.setTitle(this.getResources().getString(R.string.app_name) + " - " + this.getResources().getString(R.string.menu_agregar_persona));
            }else{
                this.setTitle(this.getResources().getString(R.string.app_name) + " - " + this.getResources().getString(R.string.menu_editar_persona));
            }

            
		} catch (Exception e) {
		// TODO: handle exception
			ConstantsAdmin.mostrarMensaje(this, getString(R.string.errorInicioAplicacion));
		}

	}
	
	private void  configurarBotonesAgregarTipoValor(){
		botonAddTel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	openAltaTelefono();
            }
        });
		botonAddMail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	openAltaMail();
            }
        });			
		botonAddDir.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	openAltaDireccion();
            }
        });		
	}
	
    private void configurarListaTelefonos(){
        SimpleAdapter adapter = obtenerAdapterTelefono(telefonos);
        telefonosList.setAdapter(adapter);
        telefonosList.setDividerHeight(0);
       	telefonosList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
				openEdicionTelefono(arg2);
				
			}
		});	
    }

    
    private void openEdicionMail(int pos){
    	ConstantsAdmin.tipoValorSeleccionado = mails.get(pos);
    	ConstantsAdmin.tipoValorAnteriorSeleccionado = ConstantsAdmin.tipoValorSeleccionado.getCopy();
    	ConstantsAdmin.tiposValores = tiposEmail;
    	ConstantsAdmin.personaSeleccionada = mPersonaSeleccionada;
        Intent i = new Intent(this, AltaTipoValorActivity.class);
        i.putExtra(ConstantsAdmin.TIPO_ELEMENTO, ConstantsAdmin.TIPO_EMAIL);
        this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_EDICION_EMAIL);
    	
    }
    
    private void openEdicionDireccion(int pos){
    	ConstantsAdmin.tipoValorSeleccionado = direcciones.get(pos);
    	ConstantsAdmin.tipoValorAnteriorSeleccionado = ConstantsAdmin.tipoValorSeleccionado.getCopy();
    	ConstantsAdmin.tiposValores = tiposDireccion;
    	ConstantsAdmin.personaSeleccionada = mPersonaSeleccionada;
        Intent i = new Intent(this, AltaTipoValorActivity.class);
        i.putExtra(ConstantsAdmin.TIPO_ELEMENTO, ConstantsAdmin.TIPO_DIRECCION);
        this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_EDICION_DIRECCION);
    	
    }    
    
    private void openAltaMail(){
    	ConstantsAdmin.tipoValorSeleccionado = new TipoValorDTO();
    	ConstantsAdmin.tipoValorSeleccionado.setIdPersona(String.valueOf(mPersonaSeleccionada.getId()));
    	ConstantsAdmin.tipoValorAnteriorSeleccionado = null;
    	ConstantsAdmin.tiposValores = tiposEmail;
    	ConstantsAdmin.personaSeleccionada = mPersonaSeleccionada;
        Intent i = new Intent(this, AltaTipoValorActivity.class);
        i.putExtra(ConstantsAdmin.TIPO_ELEMENTO, ConstantsAdmin.TIPO_EMAIL);
        this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_ALTA_EMAIL);
    	
    }    
    
    private void openAltaDireccion(){
    	ConstantsAdmin.tipoValorSeleccionado = new TipoValorDTO();
    	ConstantsAdmin.tipoValorSeleccionado.setIdPersona(String.valueOf(mPersonaSeleccionada.getId()));
    	ConstantsAdmin.tipoValorAnteriorSeleccionado = null;
    	ConstantsAdmin.tiposValores = tiposDireccion;
    	ConstantsAdmin.personaSeleccionada = mPersonaSeleccionada;
        Intent i = new Intent(this, AltaTipoValorActivity.class);
        i.putExtra(ConstantsAdmin.TIPO_ELEMENTO, ConstantsAdmin.TIPO_DIRECCION);
        this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_ALTA_DIRECCION);
    	
    } 
    
    private void openEdicionTelefono(int pos){
    	ConstantsAdmin.tipoValorSeleccionado = telefonos.get(pos);
    	ConstantsAdmin.tipoValorAnteriorSeleccionado = ConstantsAdmin.tipoValorSeleccionado.getCopy();
    	ConstantsAdmin.tiposValores = tiposTelefono;
    	ConstantsAdmin.personaSeleccionada = mPersonaSeleccionada;
        Intent i = new Intent(this, AltaTipoValorActivity.class);
        i.putExtra(ConstantsAdmin.TIPO_ELEMENTO, ConstantsAdmin.TIPO_TELEFONO);
        this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_EDICION_TELEFONO);
    	
    }
    
    private void openAltaTelefono(){
    	ConstantsAdmin.tipoValorSeleccionado = new TipoValorDTO();
    	ConstantsAdmin.tipoValorSeleccionado.setIdPersona(String.valueOf(mPersonaSeleccionada.getId()));
    	ConstantsAdmin.tipoValorAnteriorSeleccionado = null;
    	ConstantsAdmin.tiposValores = tiposTelefono;
    	ConstantsAdmin.personaSeleccionada = mPersonaSeleccionada;
        Intent i = new Intent(this, AltaTipoValorActivity.class);
        i.putExtra(ConstantsAdmin.TIPO_ELEMENTO, ConstantsAdmin.TIPO_TELEFONO);
        this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_ALTA_TELEFONO);
    	
    }    
    
    private void configurarListaEmails(){
        SimpleAdapter adapter = obtenerAdapter(mails);
        mailsList.setAdapter(adapter);   
        mailsList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				openEdicionMail(arg2);
				
			}
		});	
    }

    
    private void configurarListaDirecciones(){
        SimpleAdapter adapter = obtenerAdapter(direcciones);
        direccionesList.setAdapter(adapter);   
        direccionesList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				openEdicionDireccion(arg2);
				
			}
		});	
    }

    
    private SimpleAdapter obtenerAdapter(List<TipoValorDTO> lista){
    	ArrayList<HashMap<String,Object>> listdata= new ArrayList<>();
    	HashMap<String, Object> hm;

    	TipoValorDTO tv;
    	if(lista != null){
			for (TipoValorDTO aLista : lista) {
				tv = aLista;
				hm = new HashMap<>();
				hm.put(TIPO, tv.getTipo());
				hm.put(VALOR, tv.getValor());
				listdata.add(hm);

			}
    	}
        
        String[] from = {TIPO, VALOR};
        int[] to={R.id.rowTipo,R.id.rowValor};
        SimpleAdapter sa = new SimpleAdapter(this, listdata, R.layout.row_item_alta, from, to);

		return sa;
    }

	private SimpleAdapter obtenerAdapterTelefono(List<TipoValorDTO> lista){
		ArrayList<HashMap<String,Object>> listdata= new ArrayList<>();
		HashMap<String, Object> hm;

		TipoValorDTO tv;
		if(lista != null){
			for (TipoValorDTO aLista : lista) {
				tv = aLista;
				hm = new HashMap<>();
				hm.put(TIPO, tv.getTipo());
				hm.put(VALOR, tv.getValor());
				hm.put(ID_TIPO_VALOR, tv.getId());
				hm.put(ID_PERSONA, tv.getIdPersona());
				listdata.add(hm);

			}
		}

		String[] from = {TIPO, VALOR};
		int[] to={R.id.rowTipo,R.id.rowValor};
		KNSimpleCustomAdapter sa = new KNSimpleCustomAdapter(this, listdata, R.layout.row_item_alta_numerico, from, to);

		return sa;
	}


	private void registrarWidgets(){
		mDateDisplay = this.findViewById(R.id.labelDateDisplay);
		mPickDate = this.findViewById(R.id.buttonPickDate);
		mCheckFechaNac = this.findViewById(R.id.checkFechaNacimiento);
     //   Resources res = getResources();
	//	mDescripcionLabel = this.findViewById(R.id.label_descripcion);

		mSpinner = this.findViewById(R.id.spinnerCategorias_alta_persona);
		mEntryApellido = this.findViewById(R.id.entryApellido);
		mEntryNombre = this.findViewById(R.id.entryNombre);
		mEntryDatoExtra = this.findViewById(R.id.entryDatoExtra);
		mEntryDescripcion = this.findViewById(R.id.entryDescripcion);
		mNombContact = this.findViewById(R.id.nombre_contacto);
		
		
		telefonosList = this.findViewById(R.id.listaTelefonosAlta);
		mailsList = this.findViewById(R.id.listaMailsAlta);
		direccionesList = this.findViewById(R.id.listaDireccionesAlta);

		sinDatos = this.findViewById(R.id.label_sinDatos);
		botonAddTel = this.findViewById(R.id.addTelefono);
		botonAddMail = this.findViewById(R.id.addMail);
		botonAddDir = this.findViewById(R.id.addDir);

	}
	
	private void cargarPersonaDto(String idPerString){
		int idPer = Integer.valueOf(idPerString);
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		mPersonaSeleccionada = ConstantsAdmin.obtenerPersonaId(this, idPer, mDBManager);
	}
	
	private void cargarEntriesConPersonaDto(){
		mEntryApellido.setText(mPersonaSeleccionada.getApellido());
		mEntryNombre.setText(mPersonaSeleccionada.getNombres());
		mEntryDatoExtra.setText(mPersonaSeleccionada.getDatoExtra());
		mEntryDescripcion.setText(mPersonaSeleccionada.getDescripcion());

		this.cargarTelefonos();
		this.cargarEmails();
		this.cargarDirecciones();
		this.cargarEntryFechaNacimiento();
	}
	
	private void cargarTiposTelefono(){
		tiposTelefono.add(this.getString(R.string.hint_particular).toUpperCase());
		tiposTelefono.add(this.getString(R.string.hint_telMovil).toUpperCase());
		tiposTelefono.add(this.getString(R.string.hint_laboral).toUpperCase());
	}
	
	private void cargarTiposEmail(){
		tiposEmail.add(this.getString(R.string.hint_particular).toUpperCase());
		tiposEmail.add(this.getString(R.string.hint_laboral).toUpperCase());
		tiposEmail.add(this.getString(R.string.hint_otro).toUpperCase());
	}
	
	private void cargarTiposDireccion(){
		tiposDireccion.add(this.getString(R.string.hint_particular).toUpperCase());
		tiposDireccion.add(this.getString(R.string.hint_laboral).toUpperCase());
	}
	
	private void cargarTelefonos(){
		// ACA DEBERIA RECUPERAR LOS TELEFONOS EXTRAS
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		TipoValorDTO tv;
		telefonos = new ArrayList<>();
		tiposTelefono = new ArrayList<>();
		this.cargarTiposTelefono();
		String idPer = String.valueOf(mPersonaSeleccionada.getId());

		if(mPersonaSeleccionada.getTelParticular() != null){
			tv = cargarTipoValor(this.getString(R.string.hint_particular).toUpperCase(),mPersonaSeleccionada.getTelParticular(), idPer);
			telefonos.add(tv);

		}
		
		if(mPersonaSeleccionada.getCelular() != null){
			tv = cargarTipoValor(this.getString(R.string.hint_telMovil).toUpperCase(),mPersonaSeleccionada.getCelular(), idPer);
			telefonos.add(tv);

		}
		
		if(mPersonaSeleccionada.getTelLaboral() != null){
			tv = cargarTipoValor(this.getString(R.string.hint_laboral).toUpperCase(),mPersonaSeleccionada.getTelLaboral(), idPer);
			telefonos.add(tv);
		}
		
		// RECUPERO LOS TIPOS MOVILES
		List<TipoValorDTO> masTelefonos;
		if(mPersonaSeleccionada.getId()!= -1){
			masTelefonos = ConstantsAdmin.obtenerTelefonosIdPersona( mPersonaSeleccionada.getId(), mDBManager);
			this.cargarMasElementos(masTelefonos, telefonos, tiposTelefono);
		}else{
			masTelefonos = ConstantsAdmin.telefonosARegistrar;
			if(masTelefonos!= null && masTelefonos.size() > 0){
				this.cargarMasElementos(masTelefonos, telefonos, tiposTelefono);
			}
			
		}
		

	}

	
	private void cargarEmails(){
		// ACA DEBERIA RECUPERAR LOS TELEFONOS EXTRAS

		TipoValorDTO tv;
		mails = new ArrayList<>();
		tiposEmail = new ArrayList<>();
		this.cargarTiposEmail();
		String idPer = String.valueOf(mPersonaSeleccionada.getId());

		if(mPersonaSeleccionada.getEmailParticular() != null){
			tv = cargarTipoValor(this.getString(R.string.hint_particular).toUpperCase(),mPersonaSeleccionada.getEmailParticular(), idPer);
			mails.add(tv);

		}
		
		if(mPersonaSeleccionada.getEmailLaboral() != null){
			tv = cargarTipoValor(this.getString(R.string.hint_laboral).toUpperCase(),mPersonaSeleccionada.getEmailLaboral(), idPer);
			mails.add(tv);

		}
		
		if(mPersonaSeleccionada.getEmailOtro() != null){
			tv = cargarTipoValor(this.getString(R.string.hint_otro).toUpperCase(),mPersonaSeleccionada.getEmailOtro(), idPer);
			mails.add(tv);
		}
		
		// RECUPERO LOS TIPOS MOVILES
		List<TipoValorDTO> masMails;
		if(mPersonaSeleccionada.getId()!= -1){
			DataBaseManager mDBManager = DataBaseManager.getInstance(this);
			masMails = ConstantsAdmin.obtenerEmailsIdPersona(mPersonaSeleccionada.getId(), mDBManager);
			this.cargarMasElementos(masMails, mails, tiposEmail);
		}else{
			masMails = ConstantsAdmin.mailsARegistrar;
			if(masMails!= null && masMails.size() > 0){
				this.cargarMasElementos(masMails, mails, tiposEmail);
			}
			
		}
		

	}
	
	
	private void cargarDirecciones(){
		// ACA DEBERIA RECUPERAR LOS TELEFONOS EXTRAS

		TipoValorDTO tv;
		direcciones = new ArrayList<>();
		tiposDireccion = new ArrayList<>();
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		this.cargarTiposDireccion();
		String idPer = String.valueOf(mPersonaSeleccionada.getId());

		if(mPersonaSeleccionada.getDireccionParticular() != null){
			tv = cargarTipoValor(this.getString(R.string.hint_particular).toUpperCase(),mPersonaSeleccionada.getDireccionParticular(), idPer);
			direcciones.add(tv);

		}
		
		if(mPersonaSeleccionada.getDireccionLaboral() != null){
			tv = cargarTipoValor(this.getString(R.string.hint_laboral).toUpperCase(),mPersonaSeleccionada.getDireccionLaboral(), idPer);
			direcciones.add(tv);

		}
		
		
		// RECUPERO LOS TIPOS MOVILES
		List<TipoValorDTO> masDirecciones;
		if(mPersonaSeleccionada.getId()!= -1){
			masDirecciones = ConstantsAdmin.obtenerDireccionesIdPersona( mPersonaSeleccionada.getId(), mDBManager);
			this.cargarMasElementos(masDirecciones, direcciones, tiposDireccion);
		}else{
			masDirecciones = ConstantsAdmin.direccionesARegistrar;
			if(masDirecciones!= null && masDirecciones.size() > 0){
				this.cargarMasElementos(masDirecciones, direcciones, tiposDireccion);
			}
			
		}
		

	}
	
	
	private void cargarMasElementos(List<TipoValorDTO> listaOrigen, List<TipoValorDTO> listaDestino, List<String> tipos){
		Iterator<TipoValorDTO> it = listaOrigen.iterator();
		TipoValorDTO tv;
		while(it.hasNext()){
			tv = it.next();
			listaDestino.add(tv);
			tipos.add(tv.getTipo());
			
		}
	}


	private TipoValorDTO cargarTipoValor(String tipo, String valor, String idPer){
		TipoValorDTO tv;
		tv = new TipoValorDTO();
		tv.setId(0);
		tv.setIdPersona(idPer);
		tv.setTipo(tipo);
		tv.setValor(valor);
		return tv;
	}
	
	private void cargarEntryFechaNacimiento(){
		
		if(mPersonaSeleccionada.getFechaNacimiento()!= null && !mPersonaSeleccionada.getFechaNacimiento().equals("")){
			mDateDisplay.setVisibility(View.VISIBLE);
			mDateDisplay.setText(formatFechaToView(mPersonaSeleccionada.getFechaNacimiento()));
			mPickDate.setEnabled(true);
			mCheckFechaNac.setChecked(true);
		}else{
			mDateDisplay.setVisibility(View.GONE);
			mPickDate.setEnabled(false);
			mCheckFechaNac.setChecked(false);
			
		}
		
	}
	
    private String formatFechaToView(String text){
    	String result = null;
    	if(text != null){
	    	Locale locale = this.getResources().getConfiguration().locale;
	    	String lang = locale.getLanguage();
	    	String temp;
			int mes;
	    	String[] array = text.split(ConstantsAdmin.SEPARADOR_FECHA);
	    	temp = array[1];
	    	mes = Integer.valueOf(temp);
	    	
	    	if(lang.equals(Locale.ENGLISH.toString())){
	    		result = textToFechaIngles(array[0], mes, array[2]);
	    	}else if(lang.equals(ConstantsAdmin.LANG_ESPANOL)){
	    		result = textToFechaEspanol(array[0], mes, array[2]);
	    	}else{
	    		result = textToFechaGenerica(array[0], mes, array[2]);
	    	}
	    	
	    }
    	return result;
    }
    
    private String textToFechaGenerica(String diaText, int mesInt, String anioText){
    	return diaText + "/" + String.valueOf(mesInt) + "/" + anioText;
    }
    
    
    private String textToFechaEspanol(String diaText, int mesInt, String anioText){
    	return diaText + " de " + obtenerMes(mesInt) + " de " + anioText;	
    }
    
    private String textToFechaIngles(String diaText, int mesInt, String anioText){
    	return obtenerMes(mesInt) + " " + diaText + ", " + anioText;	
    }

    
    private String obtenerMes(int mesInt){
    	String mes = null;
    	switch (mesInt) {
		case 1:
			mes = getString(R.string.smesEne); 
			break;
		case 2:
			mes = getString(R.string.smesFeb); 
			break;
		case 3:
			mes = getString(R.string.smesMar); 
			break;
		case 4:
			mes = getString(R.string.smesAbr); 
			break;
		case 5:
			mes = getString(R.string.smesMay); 
			break;
		case 6:
			mes = getString(R.string.smesJun); 
			break;
		case 7:
			mes = getString(R.string.smesJul); 
			break;
		case 8:
			mes = getString(R.string.smesAgo); 
			break;
		case 9:
			mes = getString(R.string.smesSep); 
			break;
		case 10:
			mes = getString(R.string.smesOct); 
			break;
		case 11:
			mes = getString(R.string.smesNov); 
			break;
		case 12:
			mes = getString(R.string.smesDic); 
			break;

		default:
			break;
		}

    	return mes;
    	
    }

	
	private void guardarPersonaSeleccionada(Intent intent){
		String idPerString;
		this.cargarTiposTelefono();
		this.cargarTiposEmail();
		this.cargarTiposDireccion();
		if(intent.hasExtra(ConstantsAdmin.PERSONA_SELECCIONADA)){
			idPerString = (String)intent.getExtras().get(ConstantsAdmin.PERSONA_SELECCIONADA);
			this.cargarPersonaDto(idPerString);
			this.cargarEntriesConPersonaDto();
			mNombContact.setVisibility(View.VISIBLE);
			String nombC = null;
			if(mPersonaSeleccionada.getApellido() != null){
                nombC = mPersonaSeleccionada.getApellido().toUpperCase();
            }
			if(mPersonaSeleccionada.getNombres() != null){
				nombC = nombC + " " + mPersonaSeleccionada.getNombres();
			}
			mNombContact.setText(nombC);
			vieneDesdeDetalle  = true;
		}else{
			mPersonaSeleccionada = new PersonaDTO();
			cambioCategoriaFlag = true;
			mNombContact.setVisibility(View.GONE);
			this.cargarEntryFechaNacimiento();
			vieneDesdeDetalle = false;
		}
 
	}
	
	private void cargarVariablesDeFechaNacimiento(String fechaTemp){
		String[] temp;
		if(fechaTemp != null && !fechaTemp.equals("")){
			temp = fechaTemp.split(ConstantsAdmin.SEPARADOR_FECHA);
			mDay = Integer.valueOf(temp[0]);
			mMonth = Integer.valueOf(temp[1]);
			mYear = Integer.valueOf(temp[2]);
		}
	}
	
	private void configurarBotonGuardarYSalir(){
		Button boton = this.findViewById(R.id.butttonRegistrarYSalir);
		boton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if(validarEntradaDeDatos()){
                    registrarPersona();
                    setResult(RESULT_OK);
                   	finish();

                    
            	}else{
            		Toast t = Toast.makeText(getApplicationContext(), R.string.error_alta_persona_incompleta, Toast.LENGTH_LONG);
            		t.show();
            		
            	}
            }
        });
	}
	
	
	
	private void configurarBotonGuardar(){
		Button boton = this.findViewById(R.id.butttonRegistrarPersonaFinal);
		boton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if(validarEntradaDeDatos()){
                    registrarPersona();
            		Toast t = Toast.makeText(getApplicationContext(), R.string.label_guardar_parcial, Toast.LENGTH_LONG);
            		t.show();
             
                    
            	}else{
            		Toast t = Toast.makeText(getApplicationContext(), R.string.error_alta_persona_incompleta, Toast.LENGTH_LONG);
            		t.show();
            		
            	}
            }
        });
	}
	
	private boolean validarEntradaDeDatos(){
		boolean estaOk;
		String apellidoText = mEntryApellido.getText().toString();
		String nombreText = mEntryNombre.getText().toString();
		estaOk = !apellidoText.equals("") || !nombreText.equals("");
		return estaOk;
	}
	
	private void registrarPersona(){
		try {
			
			if(mPersonaSeleccionada == null){
				mPersonaSeleccionada = new PersonaDTO();
			}
			mPersonaSeleccionada.setApellido(mEntryApellido.getText().toString());
			mPersonaSeleccionada.setNombres(mEntryNombre.getText().toString());
			mPersonaSeleccionada.setDatoExtra(mEntryDatoExtra.getText().toString());
			mPersonaSeleccionada.setDescripcion(mEntryDescripcion.getText().toString());

			if(mCheckFechaNac.isChecked()){
				String fechaNacimiento = String.valueOf(mDay) + ConstantsAdmin.SEPARADOR_FECHA + String.valueOf(mMonth) + ConstantsAdmin.SEPARADOR_FECHA + String.valueOf(mYear);
				mPersonaSeleccionada.setFechaNacimiento(fechaNacimiento);
			}else{
				mPersonaSeleccionada.setFechaNacimiento(null);
			}
			if(mCategoriaSeleccionada == null){
				this.seleccionarPrimerCategoria();
			}
			if(cambioCategoriaFlag){
				mPersonaSeleccionada.setCategoriaId(mCategoriaSeleccionada.getId());
				mPersonaSeleccionada.setCategoriaNombre(mCategoriaSeleccionada.getNombreReal());
				mPersonaSeleccionada.setCategoriaNombreRelativo(mCategoriaSeleccionada.getNombreRelativo());
				cambioCategoriaFlag = false;
			}
			DataBaseManager mDBManager = DataBaseManager.getInstance(this);
			long idPer = ConstantsAdmin.crearPersona(mPersonaSeleccionada, false, mDBManager);
			this.getIntent().putExtra(ConstantsAdmin.PERSONA_SELECCIONADA, String.valueOf(idPer));

			if(ConstantsAdmin.telefonosARegistrar != null && ConstantsAdmin.telefonosARegistrar.size() > 0){
				this.registrarTelefonos(ConstantsAdmin.telefonosARegistrar, idPer);
				ConstantsAdmin.telefonosARegistrar = null;
			}
			if(ConstantsAdmin.mailsARegistrar != null && ConstantsAdmin.mailsARegistrar.size() > 0){
				this.registrarMails(ConstantsAdmin.mailsARegistrar, idPer);
				ConstantsAdmin.mailsARegistrar = null;
			}
			if(ConstantsAdmin.direccionesARegistrar != null && ConstantsAdmin.direccionesARegistrar.size() > 0){
				this.registrarDirecciones(ConstantsAdmin.direccionesARegistrar, idPer);
				ConstantsAdmin.direccionesARegistrar = null;
			}			
			mCategoriaSeleccionada = null;
			mPersonaSeleccionada.setId(idPer);
			mNombContact.setVisibility(View.VISIBLE);
			if(mPersonaSeleccionada.getApellido() != null){
                mNombContact.setText(mPersonaSeleccionada.getApellido().toUpperCase() + getString(R.string.blank) + mPersonaSeleccionada.getNombres());
            }else{
                mNombContact.setText(mPersonaSeleccionada.getNombres());
            }


			//mNombContact.setText(mPersonaSeleccionada.getApellido().toUpperCase() + " " + mPersonaSeleccionada.getNombres());
			ConstantsAdmin.resetPersonasOrganizadas();
		} catch (Exception e) {
			if(mPersonaSeleccionada.getId() == -1){
				ConstantsAdmin.mostrarMensaje(this, getString(R.string.errorAltaContacto));
			}else{
				ConstantsAdmin.mostrarMensaje(this, getString(R.string.errorActualizacionContacto));
			}
		}

	}
	
	private void registrarTelefonos(List<TipoValorDTO> telefonos, long idPer){
		Iterator<TipoValorDTO> it = telefonos.iterator();
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		TipoValorDTO tv;
		while(it.hasNext()){
			tv = it.next();
			tv.setIdPersona(String.valueOf(idPer));
		}
		ConstantsAdmin.registrarTelefonos(telefonos, mDBManager);
		
	}

	private void registrarMails(List<TipoValorDTO> mails, long idPer){
		Iterator<TipoValorDTO> it = mails.iterator();
		TipoValorDTO tv;
		while(it.hasNext()){
			tv = it.next();
			tv.setIdPersona(String.valueOf(idPer));
		}
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		ConstantsAdmin.registrarMails(mails, mDBManager);
		
	}
		
	private void registrarDirecciones(List<TipoValorDTO> direcciones, long idPer){
		Iterator<TipoValorDTO> it = direcciones.iterator();
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		TipoValorDTO tv;
		while(it.hasNext()){
			tv = it.next();
			tv.setIdPersona(String.valueOf(idPer));
		}
		ConstantsAdmin.registrarDirecciones(mails, mDBManager);
		
	}
		
		
	
	private void seleccionarPrimerCategoria(){
		mCategoriaSeleccionada = this.mSpinnerAdapt.getItem(0);
	}

	private void configurarMasOMenosDatosPersonales(){

		ImageButton icon = this.findViewById(R.id.menu_icon_identification);
		icon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if(!mMostrarDatosPersonalesBoolean){
	            	mMostrarDatosPersonalesBoolean = !mMostrarDatosPersonalesBoolean;
	            	mMostrarDatosPorCategoriaBoolean = false;
	            	mMostrarDireccionesBoolean = false;
	            	mMostrarEmailsBoolean = false;
	            	mMostrarTelefonosBoolean = false;
	                mostrarTodo();
            	}
            }
        });
	}

	
	private void configurarMasOMenosTelefonos(){
		ImageButton icon = this.findViewById(R.id.menu_icon_phone);
		icon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if(!mMostrarTelefonosBoolean){
            		mMostrarTelefonosBoolean = !mMostrarTelefonosBoolean;
                	mMostrarDatosPersonalesBoolean = false;
                	mMostrarDatosPorCategoriaBoolean = false;
                	mMostrarDireccionesBoolean = false;
                	mMostrarEmailsBoolean = false;
                              
                    mostrarTodo();
            	}
                
            }
        });
	}


	
	
	private void configurarMasOMenosEmails(){
		ImageButton icon = this.findViewById(R.id.menu_icon_mail);
		icon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if(!mMostrarEmailsBoolean){
	                mMostrarEmailsBoolean = !mMostrarEmailsBoolean;
	            	mMostrarDatosPersonalesBoolean = false;
	            	mMostrarDatosPorCategoriaBoolean = false;
	            	mMostrarDireccionesBoolean = false;
	            	mMostrarTelefonosBoolean = false;
	                mostrarTodo();
            	}
            }
        });
	}
	
	private void configurarMasOMenosDirecciones(){
		ImageButton icon = this.findViewById(R.id.menu_icon_addres);
		icon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if(!mMostrarDireccionesBoolean){
	                mMostrarDireccionesBoolean = !mMostrarDireccionesBoolean;
	            	mMostrarDatosPersonalesBoolean = false;
	            	mMostrarDatosPorCategoriaBoolean = false;
	            	mMostrarEmailsBoolean = false;
	            	mMostrarTelefonosBoolean = false;
	                mostrarTodo();
            	}
            }
        });
	}
	
	private void mostrarTodo(){

		this.mostrarDatosPersonales();
		this.mostrarTelefonos();
		this.mostrarEmails();
		this.mostrarDirecciones();
		this.mostrarDatosPorCategoria();
	}
	
	private void configurarMasOMenosDatosPorCategoria(){
		ImageButton icon = this.findViewById(R.id.menu_icon_category);
		icon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if(!mMostrarDatosPorCategoriaBoolean){
	                mMostrarDatosPorCategoriaBoolean = !mMostrarDatosPorCategoriaBoolean;
	            	mMostrarDatosPersonalesBoolean = false;
	            	mMostrarDireccionesBoolean = false;
	            	mMostrarEmailsBoolean = false;
	            	mMostrarTelefonosBoolean = false;
	                mostrarTodo();
            	}
            }
        });
	}
	
	private void mostrarTelefonos(){
	//	Drawable dbw = res.getDrawable(R.drawable.phone_am_icon_bw);
//		Drawable d = res.getDrawable(R.drawable.phone_am_icon);
		ImageButton icon = this.findViewById(R.id.menu_icon_phone);
		TextView text = this.findViewById(R.id.label_phones);
		if(!mMostrarTelefonosBoolean){
			if(text != null){
				text.setVisibility(View.GONE);
			}
			icon.setBackgroundResource(R.drawable.phone_am_icon_bw);
		//	icon.setBackgroundDrawable(dbw);
			botonAddTel.setVisibility(View.GONE);
			telefonosList.setVisibility(View.GONE);
		}else{
			if(text != null){
				text.setVisibility(View.VISIBLE);
			}
			telefonosList.setVisibility(View.VISIBLE);
			if(telefonosList.getCount()== 0){
				sinDatos.setVisibility(View.VISIBLE);
			}else{
				sinDatos.setVisibility(View.GONE);
			}
			botonAddTel.setVisibility(View.VISIBLE);
			mailsList.setVisibility(View.GONE);
			direccionesList.setVisibility(View.GONE);
			icon.setBackgroundResource(R.drawable.phone_am_icon);
			//icon.setBackgroundDrawable(d);
		}
	}
	
	
	private void mostrarDatosPersonales(){
	//	Drawable dbw = res.getDrawable(R.drawable.person_am_icon_bw);
	//	Drawable d = res.getDrawable(R.drawable.person_am_icon);
		ImageButton icon = this.findViewById(R.id.menu_icon_identification);
		TextView text = this.findViewById(R.id.label_identificacion);
		if(!mMostrarDatosPersonalesBoolean){
			this.findViewById(R.id.label_apellido).setVisibility(View.GONE);
			this.findViewById(R.id.label_nombre).setVisibility(View.GONE);
			this.findViewById(R.id.labelDateDisplay).setVisibility(View.GONE);
								
			
			if(text != null)
				text.setVisibility(View.GONE);
			mEntryApellido.setVisibility(View.GONE);
			mEntryNombre.setVisibility(View.GONE);
			mCheckFechaNac.setVisibility(View.GONE);
			mDateDisplay.setVisibility(View.GONE);
			mPickDate.setVisibility(View.GONE);

			icon.setBackgroundResource(R.drawable.person_am_icon_bw);
		//	icon.setBackgroundDrawable(dbw);


		}else{
			if(text != null)
				text.setVisibility(View.VISIBLE);
			sinDatos.setVisibility(View.GONE);
			this.findViewById(R.id.label_apellido).setVisibility(View.VISIBLE);
			this.findViewById(R.id.label_nombre).setVisibility(View.VISIBLE);
			this.findViewById(R.id.labelDateDisplay).setVisibility(View.VISIBLE);
			mEntryApellido.setVisibility(View.VISIBLE);
			mEntryNombre.setVisibility(View.VISIBLE);
			mCheckFechaNac.setVisibility(View.VISIBLE);
			mDateDisplay.setVisibility(View.VISIBLE);
			mPickDate.setVisibility(View.VISIBLE);
			mEntryApellido.setPressed(false);
			icon.setBackgroundResource(R.drawable.person_am_icon);
			//icon.setBackgroundDrawable(d);
		}
	}

	
	private void mostrarEmails(){
		//Drawable dbw = res.getDrawable(R.drawable.mail_am_icon_bw);
	//	Drawable d = res.getDrawable(R.drawable.mail_am_icon);
		ImageButton icon = this.findViewById(R.id.menu_icon_mail);
		TextView text = this.findViewById(R.id.label_emails);
		if(!mMostrarEmailsBoolean){
			if(text != null){
				text.setVisibility(View.GONE);
			}
			icon.setBackgroundResource(R.drawable.mail_am_icon_bw);
			mailsList.setVisibility(View.GONE);
			botonAddMail.setVisibility(View.GONE);
		}else{
			if(text != null){
				text.setVisibility(View.VISIBLE);
			}
			mailsList.setVisibility(View.VISIBLE);
			if(mailsList.getCount() == 0){
				sinDatos.setVisibility(View.VISIBLE);
			}else{
				sinDatos.setVisibility(View.GONE);
			}
			botonAddMail.setVisibility(View.VISIBLE);			
			telefonosList.setVisibility(View.GONE);
			direccionesList.setVisibility(View.GONE);
			icon.setBackgroundResource(R.drawable.mail_am_icon);
		}
	}

	private void mostrarDirecciones(){
	//	Drawable dbw = res.getDrawable(R.drawable.home_am_icon_bw);
	//	Drawable d = res.getDrawable(R.drawable.home_am_icon);
		ImageButton icon = this.findViewById(R.id.menu_icon_addres);
		TextView text = this.findViewById(R.id.label_address);
		if(!mMostrarDireccionesBoolean){
			if(text != null){
				text.setVisibility(View.GONE);
			}
			icon.setBackgroundResource(R.drawable.home_am_icon_bw);
			direccionesList.setVisibility(View.GONE);
			botonAddDir.setVisibility(View.GONE);
		}else{
			if(text != null){
				text.setVisibility(View.VISIBLE);
			}
			direccionesList.setVisibility(View.VISIBLE);
			if(direccionesList.getCount()== 0){
				sinDatos.setVisibility(View.VISIBLE);
			}else{
				sinDatos.setVisibility(View.GONE);
			}
			botonAddDir.setVisibility(View.VISIBLE);
			telefonosList.setVisibility(View.GONE);
			mailsList.setVisibility(View.GONE);
			icon.setBackgroundResource(R.drawable.home_am_icon);

		}
	}
	


	private void mostrarDatosPorCategoria(){
		//Drawable dbw = res.getDrawable(R.drawable.categoria_am_icon_bw);
		//Drawable d = res.getDrawable(R.drawable.categoria_am_icon);
		ImageButton icon = this.findViewById(R.id.menu_icon_category);
		TextView text = this.findViewById(R.id.label_category);
		if(!mMostrarDatosPorCategoriaBoolean){
			this.setVisibilityParaViewPorCategorias(View.GONE);
			if(text != null){
				text.setVisibility(View.GONE);
			}
			icon.setBackgroundResource(R.drawable.categoria_am_icon_bw);
		}else{
			sinDatos.setVisibility(View.GONE);
			this.setVisibilityParaViewPorCategorias(View.VISIBLE);
			icon.setBackgroundResource(R.drawable.categoria_am_icon);
			if(text != null){
				text.setVisibility(View.VISIBLE);
			}
		}
		
		this.configurarEntriesPorCategoriaSeleccionada();
	}

	
	private void setVisibilityParaViewPorCategorias(int visibility){
		Spinner spinner = this.findViewById(R.id.spinnerCategorias_alta_persona);
		mEntryDatoExtra.setVisibility(visibility);
		mEntryDescripcion.setVisibility(visibility);
		this.findViewById(R.id.label_datoExtra).setVisibility(visibility);
		this.findViewById(R.id.label_descripcion).setVisibility(visibility);
		if(spinner != null){
			spinner.setVisibility(visibility);	
		}
		
	}
	
	private void configurarDatePicker(){
    /*    mPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID, null);
            }
        });

*/

		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		mPickDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				DatePickerDialog dpd = new DatePickerDialog(AltaPersonaActivity.this,
						new DatePickerDialog.OnDateSetListener() {
							@Override
							public void onDateSet(DatePicker view, int year, int month, int day) {
								c.set(year, month , day);
						//		String date = new SimpleDateFormat("MM/dd/yyyy").format(c.getTime());
								//mDateDisplay.setText(date);
								mYear = c.get(Calendar.YEAR);
								mMonth = c.get(Calendar.MONTH) + 1;
								mDay = c.get(Calendar.DAY_OF_MONTH);
								updateDisplay();
							}
						}, mYear, mMonth - 1, mDay);
				/*dpd.getDatePicker().setMinDate(System.currentTimeMillis());

				Calendar d = Calendar.getInstance();
				d.add(Calendar.MONTH,1);

				dpd.getDatePicker().setMaxDate(d.getTimeInMillis());*/
				dpd.show();


			}

		});





		mCheckFechaNac.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checkedFechaNacimiento(isChecked);
				
			}
		});

        // get the current date
		//c = Calendar.getInstance();
        if(!(mPersonaSeleccionada == null || mPersonaSeleccionada.getFechaNacimiento() == null)){
        	this.cargarVariablesDeFechaNacimiento(mPersonaSeleccionada.getFechaNacimiento());
        }else{
			mYear = c.get(Calendar.YEAR);
			mMonth = c.get(Calendar.MONTH) + 1;
			mDay = c.get(Calendar.DAY_OF_MONTH);
		}

	}
	
	private void checkedFechaNacimiento(boolean isChecked){
		if(isChecked){
			mPickDate.setEnabled(true);
			mPickDate.setTextColor(getResources().getColor(R.color.color_azul));
			mDateDisplay.setVisibility(View.VISIBLE);
		}else{
			mPickDate.setEnabled(false);
			mPickDate.setTextColor(Color.GRAY);
			mDateDisplay.setVisibility(View.INVISIBLE);
			
		}
	}
	
    private void updateDisplay() {
    	CharSequence text = new StringBuilder()
        // Month is 0 based so add 1
        .append(mDay).append(ConstantsAdmin.SEPARADOR_FECHA)
        .append(mMonth).append(ConstantsAdmin.SEPARADOR_FECHA)
        .append(mYear).append(" ");
        mDateDisplay.setText(formatFechaToView(text.toString()));
    }
    
 // the callback received when the user "sets" the date in the dialog
/*    private final DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {

                public void onDateSet(DatePicker view, int year, 
                                      int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear + 1 ;
                    mDay = dayOfMonth;
                    updateDisplay();
                }
    };*/

 /*   protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DATE_DIALOG_ID:
            return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, mMonth - 1, mDay);
        }
        return null;
    }
*/
    private void configurarSpinner(){
      List<CategoriaDTO> categorias;
      List<CategoriaDTO> categoriasPersonales;
      DataBaseManager mDBManager = DataBaseManager.getInstance(this);
      categorias = ConstantsAdmin.obtenerCategoriasActivas(this, null, mDBManager);
      categoriasPersonales = ConstantsAdmin.obtenerCategoriasActivasPersonales(this, mDBManager);
      categorias.addAll(categoriasPersonales);
      this.cargarNombreRelativoCategorias(categorias);
      this.crearSpinnerCategorias(categorias);
    }
    
  	private void cargarNombreRelativoCategorias(List<CategoriaDTO> categorias){
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
  		Collections.sort(categorias);
  	}

  	
    
    private void crearSpinnerCategorias(List<CategoriaDTO> categorias){
	    Spinner spinner = findViewById(R.id.spinnerCategorias_alta_persona);
	    int pos = 0;
	    CategoriaDTO cat;
	    boolean encontrado = false;
	    Iterator<CategoriaDTO> it = categorias.iterator();
	    while(!encontrado && it.hasNext()){
	    	pos++;
	    	cat = it.next();
	    	if(cat.getNombreReal().equals(mPersonaSeleccionada.getCategoriaNombre())){
	    		encontrado = true;
	    		mCategoriaSeleccionada = cat;
	    	}
	    }
	    this.mSpinnerAdapt = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categorias);

	    spinner.setAdapter(this.mSpinnerAdapt); 
	    if(encontrado){
	    	spinner.setSelection(pos - 1);
	    }
	    OnItemSelectedListener spinnerListener = new seleccionSpinnerListener();
	    spinner.setOnItemSelectedListener(spinnerListener);
	}


	class seleccionSpinnerListener implements OnItemSelectedListener {


        public void onItemSelected(AdapterView<?> parent, View v, int pos, long row) {
        	mCategoriaSeleccionada = (CategoriaDTO) parent.getSelectedItem();
        	cambioCategoriaFlag = true;
        	mostrarDatosPorCategoria();
           
        }

        public void onNothingSelected(AdapterView<?> parent) {


        }
    }
    
    private String obtenerEtiquetaDatoExtra(String nombreEtiqueta){
    	String result = null;
		switch (nombreEtiqueta) {
			case ConstantsAdmin.CATEGORIA_AMIGOS:
				result = getString(R.string.hint_lugarOActividad);
				break;
			case ConstantsAdmin.CATEGORIA_CLIENTES:
				result = getString(R.string.hint_lugarOActividad);
				break;
			case ConstantsAdmin.CATEGORIA_COLEGAS:
				result = getString(R.string.hint_lugarOActividad);
				break;
			case ConstantsAdmin.CATEGORIA_COMPANIEROS:
				result = getString(R.string.hint_lugarOActividad);
				break;
			case ConstantsAdmin.CATEGORIA_CONOCIDOS:
				result = getString(R.string.hint_lugarOActividad);
				break;
			case ConstantsAdmin.CATEGORIA_FAMILIARES:
				result = getString(R.string.hint_parentesco);
				break;
			case ConstantsAdmin.CATEGORIA_JEFES:
				result = getString(R.string.hint_empresa);
				break;
			case ConstantsAdmin.CATEGORIA_MEDICO:
				result = getString(R.string.hint_especialidad);
				break;
			case ConstantsAdmin.CATEGORIA_OTROS:
				result = getString(R.string.hint_rol);
				break;
			case ConstantsAdmin.CATEGORIA_PACIENTES:
				result = getString(R.string.hint_obraSocial);
				break;
			case ConstantsAdmin.CATEGORIA_PROFESORES:
				result = getString(R.string.hint_lugarOActividad);
				break;
			case ConstantsAdmin.CATEGORIA_PROVEEDORES:
				result = getString(R.string.hint_empresa);
				break;
			case ConstantsAdmin.CATEGORIA_SOCIOS:
				result = getString(R.string.hint_empresa);
				break;
			case ConstantsAdmin.CATEGORIA_VECINOS:
				result = getString(R.string.hint_zona);
				break;
		}
    	
    	
    	return result;
    }
    
    
    private void configurarEntriesPorCategoriaSeleccionada(){
    	String text;
    	if(mCategoriaSeleccionada != null){
	    	EditText textViewEntry = this.findViewById(R.id.entryDatoExtra);
	    	TextView textViewLabel = this.findViewById(R.id.label_datoExtra);
	    	text = this.obtenerEtiquetaDatoExtra(mCategoriaSeleccionada.getNombreReal());
	    	if(text == null){
	    		text = mCategoriaSeleccionada.getTipoDatoExtra();
	    	}
	    	textViewEntry.setHint(text.toUpperCase());
	    	textViewLabel.setText(text.toUpperCase());
    	}
    	
    }
    
   
    protected void onResume() {
        super.onResume();
        if(ConstantsAdmin.mainActivity == null){
        	
        	if(!vieneDesdeDetalle){
            	Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(this.getPackageName());
            	startActivity(LaunchIntent);
        	}
        	this.finish();
        	ConstantsAdmin.cerrarMainActivity = true;
        }else{
        //	this.resetAllMyCursors();
        	this.mostrarTodo();
        }
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
}
