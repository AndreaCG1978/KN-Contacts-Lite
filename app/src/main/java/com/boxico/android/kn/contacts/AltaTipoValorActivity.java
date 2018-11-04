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

import com.boxico.android.kn.contacts.persistencia.dtos.PersonaDTO;
import com.boxico.android.kn.contacts.persistencia.dtos.TipoValorDTO;
import com.boxico.android.kn.contacts.util.ConstantsAdmin;

public class AltaTipoValorActivity extends Activity {
	

	EditText mValor = null;
	EditText mNuevoTipo = null;
	TextView labelNuevoTipo = null;
	Spinner spinnerTipos = null;
	Button btnEditar = null; 
	Button btnEliminar = null; 
	Dialog dialog = null;
	
	ArrayList<String> tipos = null;
	String tipoElemento = null;
	PersonaDTO personaSeleccionada = null;
	TipoValorDTO mTipoValor = null;
	TipoValorDTO mTipoValorAnterior = null;	
	boolean actualizarPersona = false;
	boolean actualizarTipoValor = false;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTipoValor = ConstantsAdmin.tipoValorSeleccionado;
        mTipoValorAnterior = ConstantsAdmin.tipoValorAnteriorSeleccionado;
        tipos = new ArrayList<String>();
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
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, tipos);
	    spinnerTipos.setAdapter(adapter);
	    int pos = 0;
	    String tip = null;
	    boolean encontrado = false;
	    Iterator<String> it = tipos.iterator();
	    if(mTipoValor != null && mTipoValor.getTipo() != null){
		    while (!encontrado && it.hasNext()){
		    	tip = (String) it.next();
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
	
    
    public class seleccionSpinnerListener implements OnItemSelectedListener {
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
		
		
		TextView labelValor = (TextView) dialog.findViewById(R.id.label_valor);
		labelNuevoTipo = (TextView) dialog.findViewById(R.id.label_nuevo_tipo);
		
		mValor = (EditText) dialog.findViewById(R.id.entryValor);
		mNuevoTipo = (EditText) dialog.findViewById(R.id.entryNuevoTipo);	
		spinnerTipos = (Spinner) dialog.findViewById(R.id.spinnerTipoValor);
		btnEditar = (Button) dialog.findViewById(R.id.buttonRegistrarCategoria); 
		btnEliminar = (Button) dialog.findViewById(R.id.buttonEliminarCategoria); 
		
		if(tipoElemento.equals(ConstantsAdmin.TIPO_TELEFONO)){
			labelValor.setText(this.getString(R.string.label_telefono));
			dialog.setTitle(this.getString(R.string.label_telefono));
			mValor.setInputType(InputType.TYPE_CLASS_PHONE);
			filterArray[0] = new InputFilter.LengthFilter(25);
			
		}else if(tipoElemento.equals(ConstantsAdmin.TIPO_EMAIL)){
			labelValor.setText(this.getString(R.string.label_email));
			dialog.setTitle(this.getString(R.string.label_email));
			mValor.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
			filterArray[0] = new InputFilter.LengthFilter(40);
		}else{
			labelValor.setText(this.getString(R.string.label_direccion));
			dialog.setTitle(this.getString(R.string.label_direccion));
			mValor.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
			filterArray[0] = new InputFilter.LengthFilter(80);
			mValor.setLines(3);
			mValor.setSingleLine(false);
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
			if(!(tipo == null || tipo.equals(""))){
				mTipoValor.setTipo(tipo.toUpperCase());
			}
			if(tipoElemento.equals(ConstantsAdmin.TIPO_TELEFONO)){
				eliminarTelefono();
			}else if(tipoElemento.equals(ConstantsAdmin.TIPO_EMAIL)){
				eliminarMail();
			}else{
				eliminarDireccion();
			}
		} catch (Exception e) {
//			ConstantsAdmin.mostrarErrorAplicacion(this, getString(R.string.errorEliminacionCategoria));
		}

	}
    
	private void eliminarTelefono(){
		
		if(esTipoFijoTelefono(mTipoValor.getTipo())){
			this.setearTelefonoPersona(null, mTipoValor.getTipo());
			if(personaSeleccionada.getId() != -1){
				ConstantsAdmin.crearPersona(personaSeleccionada, false, this);
			}else{
				ConstantsAdmin.personaSeleccionada = personaSeleccionada;
			}
			
		}else{
			if(personaSeleccionada.getId() != -1){
				ConstantsAdmin.eliminarTelefono(mTipoValor, this);
			}else{
				this.eliminarTipoValor(ConstantsAdmin.telefonosARegistrar, mTipoValor);
			}
			
		}
				
	}
	
	private void eliminarMail(){
		
		if(esTipoFijoMail(mTipoValor.getTipo())){
			this.setearMailPersona(null, mTipoValor.getTipo());
			if(personaSeleccionada.getId() != -1){
				ConstantsAdmin.crearPersona(personaSeleccionada, false, this);
			}else{
				ConstantsAdmin.personaSeleccionada = personaSeleccionada;
			}
			
		}else{
			if(personaSeleccionada.getId() != -1){
				ConstantsAdmin.eliminarEmail(mTipoValor, this);
			}else{
				this.eliminarTipoValor(ConstantsAdmin.mailsARegistrar, mTipoValor);
			}
			
		}
				
	}
   
	
	private void eliminarDireccion(){
		
		if(esTipoFijoDireccion(mTipoValor.getTipo())){
			this.setearDireccionPersona(null, mTipoValor.getTipo());
			if(personaSeleccionada.getId() != -1){
				ConstantsAdmin.crearPersona(personaSeleccionada, false, this);
			}else{
				ConstantsAdmin.personaSeleccionada = personaSeleccionada;
			}
			
		}else{
			if(personaSeleccionada.getId() != -1){
				ConstantsAdmin.eliminarDireccion(mTipoValor, this);
			}else{
				this.eliminarTipoValor(ConstantsAdmin.direccionesARegistrar, mTipoValor);
			}
			
		}
				
	}
   	
	
	private void registrarTipoValor(){
		if(tipoElemento.equals(ConstantsAdmin.TIPO_TELEFONO)){
			this.registrarTelefono();
		}else if(tipoElemento.equals(ConstantsAdmin.TIPO_EMAIL)){
			this.registrarMail();
		}else{
			this.registrarDireccion();
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
		if(!(tipo == null || tipo.equals(""))){
			mTipoValor.setTipo(tipo.toUpperCase());
		}
		
		if(esTipoFijoTelefono(mTipoValor.getTipo())){
			this.setearTelefonoPersona(valor, mTipoValor.getTipo());
			if(personaSeleccionada.getId() != -1){
				ConstantsAdmin.crearPersona(personaSeleccionada, false, this);
			}else{
				ConstantsAdmin.personaSeleccionada = personaSeleccionada;
			}
			
		}else{
			if(personaSeleccionada.getId() != -1){
				ConstantsAdmin.crearTelefono(mTipoValor, this);
			}else{
				ConstantsAdmin.telefonosARegistrar.add(mTipoValor);
				
			}
		}

		
	}
	
	private void crearMail(){
		String valor = mValor.getText().toString();
		String tipo = mNuevoTipo.getText().toString();
		mTipoValor.setValor(valor);
		if(!(tipo == null || tipo.equals(""))){
			mTipoValor.setTipo(tipo.toUpperCase());
		}
		if(esTipoFijoMail(mTipoValor.getTipo())){
			this.setearMailPersona(valor, mTipoValor.getTipo());
			if(personaSeleccionada.getId() != -1){
				ConstantsAdmin.crearPersona(personaSeleccionada, false, this);
			}else{
				ConstantsAdmin.personaSeleccionada = personaSeleccionada;
			}
		}else{
			
			if(personaSeleccionada.getId() != -1){
				ConstantsAdmin.crearEmail(mTipoValor, this);
			}else{
				ConstantsAdmin.mailsARegistrar.add(mTipoValor);
				
			}
			
		}
		
		
	}
	
	private void crearDireccion(){
		String valor = mValor.getText().toString();
		String tipo = mNuevoTipo.getText().toString();
		mTipoValor.setValor(valor);
		if(!(tipo == null || tipo.equals(""))){
			mTipoValor.setTipo(tipo.toUpperCase());
		}
		
		if(esTipoFijoDireccion(mTipoValor.getTipo())){
			this.setearDireccionPersona(valor, mTipoValor.getTipo());
			if(personaSeleccionada.getId() != -1){
				ConstantsAdmin.crearPersona(personaSeleccionada, false, this);
			}else{
				ConstantsAdmin.personaSeleccionada = personaSeleccionada;
			}

		}else{
			if(personaSeleccionada.getId() != -1){
				ConstantsAdmin.crearDireccion(mTipoValor, this);
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
		TipoValorDTO tvTemp = null;
		while(it.hasNext()&& !encontrado){
			tvTemp = (TipoValorDTO) it.next();
			encontrado = tvTemp.getId() == tv.getId();
			if(encontrado){
				it.remove();
			}
			
		}
	}
	

	private void actualizarTelefono(){
		String valor = mValor.getText().toString();
		String tipo = mNuevoTipo.getText().toString();
		mTipoValor.setValor(valor);
		if(!(tipo == null || tipo.equals(""))){
			mTipoValor.setTipo(tipo.toUpperCase());
		}
		
		if(esTipoFijoTelefono(mTipoValorAnterior.getTipo()) && esTipoFijoTelefono(mTipoValor.getTipo())){
			this.setearTelefonoPersona(null, mTipoValorAnterior.getTipo());
			this.setearTelefonoPersona(valor, mTipoValor.getTipo());
			
			if(personaSeleccionada.getId()!= -1){
				ConstantsAdmin.crearPersona(personaSeleccionada, false, this);
				
			}else{
				ConstantsAdmin.personaSeleccionada = personaSeleccionada;
			}
			


		}else if(esTipoFijoTelefono(mTipoValorAnterior.getTipo())&& !esTipoFijoTelefono(mTipoValor.getTipo())){
			this.setearTelefonoPersona(null, mTipoValorAnterior.getTipo());
			if(personaSeleccionada.getId()!= -1){
				ConstantsAdmin.crearPersona(personaSeleccionada, false, this);
				ConstantsAdmin.crearTelefono(mTipoValor, this);
			}else{
				ConstantsAdmin.personaSeleccionada = personaSeleccionada;
				ConstantsAdmin.telefonosARegistrar.add(mTipoValor);
			}
			
			
			
			
		}else if(!esTipoFijoTelefono(mTipoValorAnterior.getTipo())&& esTipoFijoTelefono(mTipoValor.getTipo())){
			this.setearTelefonoPersona(valor, mTipoValor.getTipo());
			if(personaSeleccionada.getId()!= -1){
				ConstantsAdmin.crearPersona(personaSeleccionada, false, this);
				ConstantsAdmin.eliminarTelefono(mTipoValor, this);
			}else{
				ConstantsAdmin.personaSeleccionada = personaSeleccionada;
				this.eliminarTipoValor(ConstantsAdmin.telefonosARegistrar, mTipoValorAnterior);
			}
			
		}else{
			
			if(personaSeleccionada.getId()!= -1){
				mTipoValor.setId(mTipoValorAnterior.getId());
				ConstantsAdmin.crearTelefono(mTipoValor, this);
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
		if(!(tipo == null || tipo.equals(""))){
			mTipoValor.setTipo(tipo.toUpperCase());
		}
		
		if(esTipoFijoMail(mTipoValorAnterior.getTipo()) && esTipoFijoMail(mTipoValor.getTipo())){
			this.setearMailPersona(null, mTipoValorAnterior.getTipo());
			this.setearMailPersona(valor, mTipoValor.getTipo());
			
			if(personaSeleccionada.getId()!= -1){
				ConstantsAdmin.crearPersona(personaSeleccionada, false, this);
				
			}else{
				ConstantsAdmin.personaSeleccionada = personaSeleccionada;
			}
			


		}else if(esTipoFijoMail(mTipoValorAnterior.getTipo())&& !esTipoFijoMail(mTipoValor.getTipo())){
			this.setearMailPersona(null, mTipoValorAnterior.getTipo());
			if(personaSeleccionada.getId()!= -1){
				ConstantsAdmin.crearPersona(personaSeleccionada, false, this);
				ConstantsAdmin.crearEmail(mTipoValor, this);
			}else{
				ConstantsAdmin.personaSeleccionada = personaSeleccionada;
				ConstantsAdmin.mailsARegistrar.add(mTipoValor);
			}
			
			
			
			
		}else if(!esTipoFijoMail(mTipoValorAnterior.getTipo())&& esTipoFijoMail(mTipoValor.getTipo())){
			this.setearMailPersona(valor, mTipoValor.getTipo());
			if(personaSeleccionada.getId()!= -1){
				ConstantsAdmin.crearPersona(personaSeleccionada, false, this);
				ConstantsAdmin.eliminarEmail(mTipoValor, this);
			}else{
				ConstantsAdmin.personaSeleccionada = personaSeleccionada;
				this.eliminarTipoValor(ConstantsAdmin.mailsARegistrar, mTipoValorAnterior);
			}
			
		}else{
			
			if(personaSeleccionada.getId()!= -1){
				mTipoValor.setId(mTipoValorAnterior.getId());
				ConstantsAdmin.crearEmail(mTipoValor, this);
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
		if(!(tipo == null || tipo.equals(""))){
			mTipoValor.setTipo(tipo.toUpperCase());
		}
		
		if(esTipoFijoDireccion(mTipoValorAnterior.getTipo()) && esTipoFijoDireccion(mTipoValor.getTipo())){
			this.setearDireccionPersona(null, mTipoValorAnterior.getTipo());
			this.setearDireccionPersona(valor, mTipoValor.getTipo());
			
			if(personaSeleccionada.getId()!= -1){
				ConstantsAdmin.crearPersona(personaSeleccionada, false, this);
				
			}else{
				ConstantsAdmin.personaSeleccionada = personaSeleccionada;
			}
			


		}else if(esTipoFijoDireccion(mTipoValorAnterior.getTipo())&& !esTipoFijoDireccion(mTipoValor.getTipo())){
			this.setearDireccionPersona(null, mTipoValorAnterior.getTipo());
			if(personaSeleccionada.getId()!= -1){
				ConstantsAdmin.crearPersona(personaSeleccionada, false, this);
				ConstantsAdmin.crearDireccion(mTipoValor, this);
			}else{
				ConstantsAdmin.personaSeleccionada = personaSeleccionada;
				ConstantsAdmin.direccionesARegistrar.add(mTipoValor);
			}
			
			
			
			
		}else if(!esTipoFijoDireccion(mTipoValorAnterior.getTipo())&& esTipoFijoDireccion(mTipoValor.getTipo())){
			this.setearDireccionPersona(valor, mTipoValor.getTipo());
			if(personaSeleccionada.getId()!= -1){
				ConstantsAdmin.crearPersona(personaSeleccionada, false, this);
				ConstantsAdmin.eliminarDireccion(mTipoValor, this);
			}else{
				ConstantsAdmin.personaSeleccionada = personaSeleccionada;
				this.eliminarTipoValor(ConstantsAdmin.direccionesARegistrar, mTipoValorAnterior);
			}
			
		}else{
			
			if(personaSeleccionada.getId()!= -1){
				mTipoValor.setId(mTipoValorAnterior.getId());
				ConstantsAdmin.crearDireccion(mTipoValor, this);
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
		boolean estaOk = true;
		String nuevoTipoStr = mNuevoTipo.getText().toString();
		String valorStr = mValor.getText().toString();
		
		if(nuevoTipoStr.equals("") && !(spinnerTipos.getSelectedItemPosition() == spinnerTipos.getCount() - 1)){
			nuevoTipoStr = mTipoValor.getTipo();
		}
		estaOk = estaOk && !valorStr.equals("") && !nuevoTipoStr.equals("");
		return estaOk;
	}
	

}
