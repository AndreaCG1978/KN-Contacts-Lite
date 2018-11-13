package com.boxico.android.kn.contacts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.boxico.android.kn.contacts.persistencia.DataBaseManager;
import com.boxico.android.kn.contacts.persistencia.dtos.PersonaDTO;
import com.boxico.android.kn.contacts.persistencia.dtos.TipoValorDTO;
import com.boxico.android.kn.contacts.util.ConstantsAdmin;

public class AltaTipoValorActivity extends Activity {
	

	private EditText mValor = null;
	private EditText mNuevoTipo = null;
	private TextView labelNuevoTipo = null;
	private Spinner spinnerTipos = null;
	private Button btnEditar = null;
	private Button btnEliminar = null;
	private Dialog dialog = null;
	
	private ArrayList<String> tipos = null;
	private String tipoElemento = null;
	private PersonaDTO personaSeleccionada = null;
	private TipoValorDTO mTipoValor = null;
	private TipoValorDTO mTipoValorAnterior = null;
//	boolean actualizarPersona = false;
//	boolean actualizarTipoValor = false;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTipoValor = ConstantsAdmin.tipoValorSeleccionado;
        mTipoValorAnterior = ConstantsAdmin.tipoValorAnteriorSeleccionado;
        tipos = new ArrayList<>();
        tipos.addAll(ConstantsAdmin.tiposValores);
        personaSeleccionada = ConstantsAdmin.personaSeleccionada;
        
        this.configurarTipoElemento();

        this.configurarDialog();
        this.registrarWidgets(dialog);
        this.configurarBotonAgregarTipoValor();
        this.configurarSpinner();
        if(mTipoValorAnterior == null){
        	btnEliminar.setVisibility(View.GONE);
//        	dialog.setTitle(R.string.title_nuevo_);
        }else{
        	this.configurarBotonEliminarTipoValor();
  //      	dialog.setTitle(this.getString(R.string.hint_categoria) + ": " + mCategoriaSeleccionada.getNombreReal());
        }     
        dialog.show();
        
        
    }
	
	private void configurarSpinner(){
		tipos.add(this.getString(R.string.hint_nuevo_tipo));
		ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tipos);
	    spinnerTipos.setAdapter(adapter);
	    int pos = 0;
	    String tip;
	    boolean encontrado = false;
	    Iterator<String> it = tipos.iterator();
	    if(mTipoValor != null && mTipoValor.getTipo() != null){
		    while (!encontrado && it.hasNext()){
		    	tip = it.next();
		    	encontrado = mTipoValor.getTipo().toUpperCase().equals(tip.toUpperCase());
		    	pos++;
		    }
	    }
		if(encontrado){
			spinnerTipos.setSelection(pos - 1);	
		}
	    
	    OnItemSelectedListener spinnerListener = new seleccionSpinnerListener();
	    spinnerTipos.setOnItemSelectedListener(spinnerListener);
	}
	
    
    class seleccionSpinnerListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View v, int pos, long row) {
        	String tipo = (String) parent.getSelectedItem();
        	if(!tipo.equals(getString(R.string.hint_nuevo_tipo))){
        		mTipoValor.setTipo(tipo);
        		labelNuevoTipo.setVisibility(View.GONE);
        		mNuevoTipo.setVisibility(View.GONE);
        		mNuevoTipo.setText("");
        	}else{
        		labelNuevoTipo.setVisibility(View.VISIBLE);
        		mNuevoTipo.setVisibility(View.VISIBLE);
        		mTipoValor.setId(-1);
        	}
        	
        }

        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
    	
	
	
	private void configurarTipoElemento(){
		tipoElemento = this.getIntent().getStringExtra(ConstantsAdmin.TIPO_ELEMENTO);
	}
	private void configurarDialog(){
		dialog = new Dialog(this);
        dialog.setContentView(R.layout.alta_tipo_valor);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new Dialog.OnCancelListener() {
        	public void onCancel(DialogInterface dialog) {
        		finish();
        	}
        });
	}
	
	
	
	private void registrarWidgets(Dialog dialog){
		
		InputFilter[] filterArray = new InputFilter[1];
		
		
		TextView labelValor = dialog.findViewById(R.id.label_valor);
		labelNuevoTipo = dialog.findViewById(R.id.label_nuevo_tipo);
		
		mValor = dialog.findViewById(R.id.entryValor);
		mNuevoTipo = dialog.findViewById(R.id.entryNuevoTipo);
		spinnerTipos = dialog.findViewById(R.id.spinnerTipoValor);
		btnEditar = dialog.findViewById(R.id.buttonRegistrarCategoria);
		btnEliminar = dialog.findViewById(R.id.buttonEliminarCategoria);

		switch (tipoElemento) {
			case ConstantsAdmin.TIPO_TELEFONO:
				labelValor.setText(this.getString(R.string.label_telefono));
				dialog.setTitle(this.getString(R.string.label_telefono));
				mValor.setInputType(InputType.TYPE_CLASS_PHONE);
				filterArray[0] = new InputFilter.LengthFilter(25);

				break;
			case ConstantsAdmin.TIPO_EMAIL:
				labelValor.setText(this.getString(R.string.label_email));
				dialog.setTitle(this.getString(R.string.label_email));
				mValor.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
				filterArray[0] = new InputFilter.LengthFilter(40);
				break;
			default:
				labelValor.setText(this.getString(R.string.label_direccion));
				dialog.setTitle(this.getString(R.string.label_direccion));
				mValor.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
				filterArray[0] = new InputFilter.LengthFilter(80);
				mValor.setLines(3);
				mValor.setSingleLine(false);
				break;
		}
		mValor.setFilters(filterArray);
		if(mTipoValor != null){
			mValor.setText(mTipoValor.getValor());
		}
		
		labelNuevoTipo.setVisibility(View.GONE);
		mNuevoTipo.setVisibility(View.GONE);
		
	}
	
    protected void onResume() {
        super.onResume();
        if(ConstantsAdmin.mainActivity == null){
        	this.finish();
        	ConstantsAdmin.cerrarMainActivity = true;

        }
    }

	
	
    private void configurarBotonAgregarTipoValor(){
    	btnEditar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
            	if(validarEntradaDeDatos()){
                    registrarTipoValor();
                    setResult(RESULT_OK);
                    finish();
            	}else{
            		Toast t = Toast.makeText(getApplicationContext(), R.string.error_alta_tipo_valor_incompleta, Toast.LENGTH_LONG);
            		t.show();
            		
            	}
			}
		});
    	
    }
    
    private void configurarBotonEliminarTipoValor(){
    	btnEliminar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
					eliminarTipoValor();
					finish();
            		
            	}
		});
    	
    }
    
	private void eliminarTipoValor(){
		try {
			String tipo = mNuevoTipo.getText().toString();
			if(! tipo.equals("")){
				mTipoValor.setTipo(tipo.toUpperCase());
			}
            switch (tipoElemento) {
                case ConstantsAdmin.TIPO_TELEFONO:
                    eliminarTelefono();
                    break;
                case ConstantsAdmin.TIPO_EMAIL:
                    eliminarMail();
                    break;
                default:
                    eliminarDireccion();
                    break;
            }
		} catch (Exception e) {
//			ConstantsAdmin.mostrarErrorAplicacion(this, getString(R.string.errorEliminacionCategoria));
		}

	}
    
	private void eliminarTelefono(){
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		if(esTipoFijoTelefono(mTipoValor.getTipo())){
			this.setearTelefonoPersona(null, mTipoValor.getTipo());
			if(personaSeleccionada.getId() != -1){

				ConstantsAdmin.crearPersona(personaSeleccionada, false, mDBManager);
			}else{
				ConstantsAdmin.personaSeleccionada = personaSeleccionada;
			}
			
		}else{
			if(personaSeleccionada.getId() != -1){
				ConstantsAdmin.eliminarTelefono(mTipoValor, mDBManager);
			}else{
				this.eliminarTipoValor(ConstantsAdmin.telefonosARegistrar, mTipoValor);
			}
			
		}
				
	}
	
	private void eliminarMail(){
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		if(esTipoFijoMail(mTipoValor.getTipo())){
			this.setearMailPersona(null, mTipoValor.getTipo());
			if(personaSeleccionada.getId() != -1){
				ConstantsAdmin.crearPersona(personaSeleccionada, false, mDBManager);
			}else{
				ConstantsAdmin.personaSeleccionada = personaSeleccionada;
			}
			
		}else{
			if(personaSeleccionada.getId() != -1){
				ConstantsAdmin.eliminarEmail(mTipoValor, mDBManager);
			}else{
				this.eliminarTipoValor(ConstantsAdmin.mailsARegistrar, mTipoValor);
			}
			
		}
				
	}
   
	
	private void eliminarDireccion(){
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		if(esTipoFijoDireccion(mTipoValor.getTipo())){
			this.setearDireccionPersona(null, mTipoValor.getTipo());
			if(personaSeleccionada.getId() != -1){
				ConstantsAdmin.crearPersona(personaSeleccionada, false, mDBManager);
			}else{
				ConstantsAdmin.personaSeleccionada = personaSeleccionada;
			}
			
		}else{
			if(personaSeleccionada.getId() != -1){
				ConstantsAdmin.eliminarDireccion(mTipoValor, mDBManager);
			}else{
				this.eliminarTipoValor(ConstantsAdmin.direccionesARegistrar, mTipoValor);
			}
			
		}
				
	}
   	
	
	private void registrarTipoValor(){
        switch (tipoElemento) {
            case ConstantsAdmin.TIPO_TELEFONO:
                this.registrarTelefono();
                break;
            case ConstantsAdmin.TIPO_EMAIL:
                this.registrarMail();
                break;
            default:
                this.registrarDireccion();
                break;
        }

	}
	
	private void registrarMail(){
		if(mTipoValorAnterior == null){
			this.crearMail();
		}else{
			this.actualizarMail();
		}
	}
	
	private void registrarDireccion(){
		if(mTipoValorAnterior == null){
			this.crearDireccion();
		}else{
			this.actualizarDireccion();
		}
	}

	
	private void registrarTelefono(){
		if(mTipoValorAnterior == null){
			this.crearTelefono();
		}else{
			this.actualizarTelefono();
		}
	}
	
	private void crearTelefono(){
		String valor = mValor.getText().toString();
		String tipo = mNuevoTipo.getText().toString();
		mTipoValor.setValor(valor);
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		if(! tipo.equals("")){
			mTipoValor.setTipo(tipo.toUpperCase());
		}
		
		if(esTipoFijoTelefono(mTipoValor.getTipo())){
			this.setearTelefonoPersona(valor, mTipoValor.getTipo());
			if(personaSeleccionada.getId() != -1){
				ConstantsAdmin.crearPersona(personaSeleccionada, false, mDBManager);
			}else{
				ConstantsAdmin.personaSeleccionada = personaSeleccionada;
			}
			
		}else{
			if(personaSeleccionada.getId() != -1){
				ConstantsAdmin.crearTelefono(mTipoValor, mDBManager);
			}else{
				ConstantsAdmin.telefonosARegistrar.add(mTipoValor);
				
			}
		}

		
	}
	
	private void crearMail(){
		String valor = mValor.getText().toString();
		String tipo = mNuevoTipo.getText().toString();
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		mTipoValor.setValor(valor);
		if(!tipo.equals("")){
			mTipoValor.setTipo(tipo.toUpperCase());
		}
		if(esTipoFijoMail(mTipoValor.getTipo())){
			this.setearMailPersona(valor, mTipoValor.getTipo());
			if(personaSeleccionada.getId() != -1){
				ConstantsAdmin.crearPersona(personaSeleccionada, false, mDBManager);
			}else{
				ConstantsAdmin.personaSeleccionada = personaSeleccionada;
			}
		}else{
			
			if(personaSeleccionada.getId() != -1){
				ConstantsAdmin.crearEmail(mTipoValor, mDBManager);
			}else{
				ConstantsAdmin.mailsARegistrar.add(mTipoValor);
				
			}
			
		}
		
		
	}
	
	private void crearDireccion(){
		String valor = mValor.getText().toString();
		String tipo = mNuevoTipo.getText().toString();
		mTipoValor.setValor(valor);
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		if(!tipo.equals("")){
			mTipoValor.setTipo(tipo.toUpperCase());
		}
		
		if(esTipoFijoDireccion(mTipoValor.getTipo())){
			this.setearDireccionPersona(valor, mTipoValor.getTipo());
			if(personaSeleccionada.getId() != -1){
				ConstantsAdmin.crearPersona(personaSeleccionada, false, mDBManager);
			}else{
				ConstantsAdmin.personaSeleccionada = personaSeleccionada;
			}

		}else{
			if(personaSeleccionada.getId() != -1){
				ConstantsAdmin.crearDireccion(mTipoValor, mDBManager);
			}else{
				ConstantsAdmin.direccionesARegistrar.add(mTipoValor);
			}
			
		}
		
		
	}
	
	
	private void setearTelefonoPersona(String valor, String tipo){
		if(tipo.equals(this.getString(R.string.hint_particular))){
			personaSeleccionada.setTelParticular(valor);
		}else if(tipo.equals(this.getString(R.string.hint_telMovil))){
			personaSeleccionada.setCelular(valor);
		}else{
			personaSeleccionada.setTelLaboral(valor);
		}		
	}
	
	private void setearMailPersona(String valor, String tipo){
		if(tipo.equals(this.getString(R.string.hint_particular))){
			personaSeleccionada.setEmailParticular(valor);
		}else if(tipo.equals(this.getString(R.string.hint_laboral))){
			personaSeleccionada.setEmailLaboral(valor);
		}else{
			personaSeleccionada.setEmailOtro(valor);
		}		
	}
	
	private void setearDireccionPersona(String valor, String tipo){
		if(tipo.equals(this.getString(R.string.hint_particular))){
			personaSeleccionada.setDireccionParticular(valor);
		}else {
			personaSeleccionada.setDireccionLaboral(valor);
		}		
	}
	
	

	
	private void eliminarTipoValor(List<TipoValorDTO> listado, TipoValorDTO tv){
		Iterator<TipoValorDTO> it = listado.iterator();
		boolean encontrado = false;
		TipoValorDTO tvTemp;
		while(it.hasNext()&& !encontrado){
			tvTemp = it.next();
			encontrado = tvTemp.getId() == tv.getId();
			if(encontrado){
				it.remove();
			}
			
		}
	}
	

	private void actualizarTelefono(){
		String valor = mValor.getText().toString();
		String tipo = mNuevoTipo.getText().toString();
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		mTipoValor.setValor(valor);
		if(tipo.equals("")){
			mTipoValor.setTipo(tipo.toUpperCase());
		}
		
		if(esTipoFijoTelefono(mTipoValorAnterior.getTipo()) && esTipoFijoTelefono(mTipoValor.getTipo())){
			this.setearTelefonoPersona(null, mTipoValorAnterior.getTipo());
			this.setearTelefonoPersona(valor, mTipoValor.getTipo());
			
			if(personaSeleccionada.getId()!= -1){
				ConstantsAdmin.crearPersona(personaSeleccionada, false, mDBManager);
				
			}else{
				ConstantsAdmin.personaSeleccionada = personaSeleccionada;
			}
			


		}else if(esTipoFijoTelefono(mTipoValorAnterior.getTipo())&& !esTipoFijoTelefono(mTipoValor.getTipo())){
			this.setearTelefonoPersona(null, mTipoValorAnterior.getTipo());
			if(personaSeleccionada.getId()!= -1){
				ConstantsAdmin.crearPersona(personaSeleccionada, false, mDBManager);
				ConstantsAdmin.crearTelefono(mTipoValor, mDBManager);
			}else{
				ConstantsAdmin.personaSeleccionada = personaSeleccionada;
				ConstantsAdmin.telefonosARegistrar.add(mTipoValor);
			}
			
			
			
			
		}else if(!esTipoFijoTelefono(mTipoValorAnterior.getTipo())&& esTipoFijoTelefono(mTipoValor.getTipo())){
			this.setearTelefonoPersona(valor, mTipoValor.getTipo());
			if(personaSeleccionada.getId()!= -1){
				ConstantsAdmin.crearPersona(personaSeleccionada, false, mDBManager);
				ConstantsAdmin.eliminarTelefono(mTipoValor, mDBManager);
			}else{
				ConstantsAdmin.personaSeleccionada = personaSeleccionada;
				this.eliminarTipoValor(ConstantsAdmin.telefonosARegistrar, mTipoValorAnterior);
			}
			
		}else{
			
			if(personaSeleccionada.getId()!= -1){
				mTipoValor.setId(mTipoValorAnterior.getId());
				ConstantsAdmin.crearTelefono(mTipoValor, mDBManager);
			}else{
				this.eliminarTipoValor(ConstantsAdmin.telefonosARegistrar, mTipoValorAnterior);
				ConstantsAdmin.telefonosARegistrar.add(mTipoValor);
			}
		}

		
	}
	
	private void actualizarMail(){
		String valor = mValor.getText().toString();
		String tipo = mNuevoTipo.getText().toString();
		mTipoValor.setValor(valor);
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		if(! tipo.equals("")){
			mTipoValor.setTipo(tipo.toUpperCase());
		}
		
		if(esTipoFijoMail(mTipoValorAnterior.getTipo()) && esTipoFijoMail(mTipoValor.getTipo())){
			this.setearMailPersona(null, mTipoValorAnterior.getTipo());
			this.setearMailPersona(valor, mTipoValor.getTipo());
			
			if(personaSeleccionada.getId()!= -1){
				ConstantsAdmin.crearPersona(personaSeleccionada, false, mDBManager);
				
			}else{
				ConstantsAdmin.personaSeleccionada = personaSeleccionada;
			}
			


		}else if(esTipoFijoMail(mTipoValorAnterior.getTipo())&& !esTipoFijoMail(mTipoValor.getTipo())){
			this.setearMailPersona(null, mTipoValorAnterior.getTipo());
			if(personaSeleccionada.getId()!= -1){
				ConstantsAdmin.crearPersona(personaSeleccionada, false, mDBManager);
				ConstantsAdmin.crearEmail(mTipoValor, mDBManager);
			}else{
				ConstantsAdmin.personaSeleccionada = personaSeleccionada;
				ConstantsAdmin.mailsARegistrar.add(mTipoValor);
			}
			
			
			
			
		}else if(!esTipoFijoMail(mTipoValorAnterior.getTipo())&& esTipoFijoMail(mTipoValor.getTipo())){
			this.setearMailPersona(valor, mTipoValor.getTipo());
			if(personaSeleccionada.getId()!= -1){
				ConstantsAdmin.crearPersona(personaSeleccionada, false, mDBManager);
				ConstantsAdmin.eliminarEmail(mTipoValor, mDBManager);
			}else{
				ConstantsAdmin.personaSeleccionada = personaSeleccionada;
				this.eliminarTipoValor(ConstantsAdmin.mailsARegistrar, mTipoValorAnterior);
			}
			
		}else{
			
			if(personaSeleccionada.getId()!= -1){
				mTipoValor.setId(mTipoValorAnterior.getId());
				ConstantsAdmin.crearEmail(mTipoValor, mDBManager);
			}else{
				this.eliminarTipoValor(ConstantsAdmin.mailsARegistrar, mTipoValorAnterior);
				ConstantsAdmin.mailsARegistrar.add(mTipoValor);
			}
		}

		
	}
	
	private void actualizarDireccion(){
		String valor = mValor.getText().toString();
		String tipo = mNuevoTipo.getText().toString();
		mTipoValor.setValor(valor);
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		if(tipo.equals("")){
			mTipoValor.setTipo(tipo.toUpperCase());
		}
		
		if(esTipoFijoDireccion(mTipoValorAnterior.getTipo()) && esTipoFijoDireccion(mTipoValor.getTipo())){
			this.setearDireccionPersona(null, mTipoValorAnterior.getTipo());
			this.setearDireccionPersona(valor, mTipoValor.getTipo());
			
			if(personaSeleccionada.getId()!= -1){
				ConstantsAdmin.crearPersona(personaSeleccionada, false, mDBManager);
				
			}else{
				ConstantsAdmin.personaSeleccionada = personaSeleccionada;
			}
			


		}else if(esTipoFijoDireccion(mTipoValorAnterior.getTipo())&& !esTipoFijoDireccion(mTipoValor.getTipo())){
			this.setearDireccionPersona(null, mTipoValorAnterior.getTipo());
			if(personaSeleccionada.getId()!= -1){
				ConstantsAdmin.crearPersona(personaSeleccionada, false, mDBManager);
				ConstantsAdmin.crearDireccion(mTipoValor, mDBManager);
			}else{
				ConstantsAdmin.personaSeleccionada = personaSeleccionada;
				ConstantsAdmin.direccionesARegistrar.add(mTipoValor);
			}
			
			
			
			
		}else if(!esTipoFijoDireccion(mTipoValorAnterior.getTipo())&& esTipoFijoDireccion(mTipoValor.getTipo())){
			this.setearDireccionPersona(valor, mTipoValor.getTipo());
			if(personaSeleccionada.getId()!= -1){
				ConstantsAdmin.crearPersona(personaSeleccionada, false, mDBManager);
				ConstantsAdmin.eliminarDireccion(mTipoValor, mDBManager);
			}else{
				ConstantsAdmin.personaSeleccionada = personaSeleccionada;
				this.eliminarTipoValor(ConstantsAdmin.direccionesARegistrar, mTipoValorAnterior);
			}
			
		}else{
			
			if(personaSeleccionada.getId()!= -1){
				mTipoValor.setId(mTipoValorAnterior.getId());
				ConstantsAdmin.crearDireccion(mTipoValor, mDBManager);
			}else{
				this.eliminarTipoValor(ConstantsAdmin.direccionesARegistrar, mTipoValorAnterior);
				ConstantsAdmin.direccionesARegistrar.add(mTipoValor);
			}
		}

		
	}	
	
		
	private boolean esTipoFijoTelefono(String tipo){
		return tipo.equals(this.getString(R.string.hint_particular)) ||
				tipo.equals(this.getString(R.string.hint_laboral)) ||
				tipo.equals(this.getString(R.string.hint_telMovil));
		
	}
	
	private boolean esTipoFijoMail(String tipo){
		return tipo.equals(this.getString(R.string.hint_particular)) ||
				tipo.equals(this.getString(R.string.hint_laboral)) ||
				tipo.equals(this.getString(R.string.hint_otro));
		
	}
	
	private boolean esTipoFijoDireccion(String tipo){
		return tipo.equals(this.getString(R.string.hint_particular)) ||
				tipo.equals(this.getString(R.string.hint_laboral));
		
	}
    
    
    
	private boolean validarEntradaDeDatos(){
		boolean estaOk;
		String nuevoTipoStr = mNuevoTipo.getText().toString();
		String valorStr = mValor.getText().toString();
		
		if(nuevoTipoStr.equals("") && !(spinnerTipos.getSelectedItemPosition() == spinnerTipos.getCount() - 1)){
			nuevoTipoStr = mTipoValor.getTipo();
		}
		estaOk = !valorStr.equals("") && !nuevoTipoStr.equals("");
		return estaOk;
	}
	

}
