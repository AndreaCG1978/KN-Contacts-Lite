package com.boxico.android.kn.contacts;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.boxico.android.kn.contacts.persistencia.dtos.CategoriaDTO;
import com.boxico.android.kn.contacts.util.ConstantsAdmin;

public class AltaCategoriaActivity extends Activity {
	
	private CategoriaDTO mCategoriaSeleccionada = null;
	EditText mNombreCategoria = null;
	EditText mNombreCaracteristica = null;
	Button btnEditar = null; 
	Button btnEliminar = null; 
	Dialog dialog = null;
	
    
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //       this.setContentView(R.layout.alta_categoria);
        this.configurarDialog();
        this.registrarWidgets(dialog);
        this.guardarCategoriaSeleccionada(this.getIntent());
        this.configurarBotonAgregarCategoria();
        if(mCategoriaSeleccionada.getId()== 0){
        	btnEliminar.setVisibility(View.GONE);
        	dialog.setTitle(R.string.title_nueva_categoria);
        }else{
        	this.configurarBotonEliminarCategoria();
        	dialog.setTitle(this.getString(R.string.hint_categoria) + ": " + mCategoriaSeleccionada.getNombreReal());
        }
        
        dialog.show();
        
        
    }

	
    protected void onResume() {
        super.onResume();
        if(ConstantsAdmin.mainActivity == null){
        	Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(this.getPackageName());
        	startActivity(LaunchIntent);
        	this.finish();
        	ConstantsAdmin.cerrarMainActivity = true;

        }
    }
	private void configurarDialog(){
		dialog = new Dialog(this);
        dialog.setContentView(R.layout.alta_categoria);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new Dialog.OnCancelListener() {
        	public void onCancel(DialogInterface dialog) {
        		finish();
        	}
        });
	}
	
	
	
	private void registrarWidgets(Dialog dialog){
		mNombreCategoria = (EditText) dialog.findViewById(R.id.entryNombreCategoria);
		mNombreCaracteristica = (EditText) dialog.findViewById(R.id.entryTipoDatoExtra);		
		btnEditar = (Button) dialog.findViewById(R.id.buttonRegistrarCategoria); 
		btnEliminar = (Button) dialog.findViewById(R.id.buttonEliminarCategoria); 
	}
	
    private void configurarBotonAgregarCategoria(){
    	btnEditar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
            	if(validarEntradaDeDatos()){
                    registrarCategoria();
                    setResult(RESULT_OK);
                    finish();
            	}else{
            		Toast t = Toast.makeText(getApplicationContext(), R.string.error_alta_categoria_incompleta, 4);
            		t.show();
         //   		mEntryApellido.clearFocus();
            		
            	}
			}
		});
    	
    }
    
    private void configurarBotonEliminarCategoria(){
    	btnEliminar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
					eliminarCategoriaDialog();
            		
            	}
		});
    	
    }
    
	private void eliminarCategoriaSeleccionada(){
		try {
			ConstantsAdmin.eliminarCategoriaPersonal(mCategoriaSeleccionada, this);
		} catch (Exception e) {
			ConstantsAdmin.mostrarMensaje(this, getString(R.string.errorEliminacionCategoria));
		}

	}
    
	private void eliminarCategoriaDialog(){
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(R.string.mensaje_borrar_categoria)
    	       .setCancelable(false)
    	       .setPositiveButton(R.string.label_si, new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	        	   eliminarCategoriaSeleccionada();
    	        	   finish();
    	           }
    	       })
    	       .setNegativeButton(R.string.label_no, new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	                dialog.cancel();
    	           }
    	       });
    	builder.show();
	}
    
	private void cargarCategoriaDto(String idCatString){
		int idCat = new Integer(idCatString);
		mCategoriaSeleccionada = ConstantsAdmin.obtenerCategoriaPersonalId(this, idCat);
	}
	
	private void cargarEntriesConCategoriaDto(){
		mNombreCategoria.setText(mCategoriaSeleccionada.getNombreReal());
		mNombreCaracteristica.setText(mCategoriaSeleccionada.getTipoDatoExtra());

	}
    
    
	private void guardarCategoriaSeleccionada(Intent intent){
		String idCatString = null;
		if(intent.hasExtra(ConstantsAdmin.CATEGORIA_SELECCIONADA)){
			idCatString = (String)intent.getExtras().get(ConstantsAdmin.CATEGORIA_SELECCIONADA);
			this.cargarCategoriaDto(idCatString);
			this.cargarEntriesConCategoriaDto();
		}else{
			mCategoriaSeleccionada = new CategoriaDTO();
		}
 
	}
    
    
	private void registrarCategoria(){
		String oldNameCat = "-";
		boolean reset = false;
		try {

			if(mCategoriaSeleccionada == null){
				mCategoriaSeleccionada = new CategoriaDTO();
			}else{
				oldNameCat = mCategoriaSeleccionada.getNombreReal();
				reset = true;
			}
			mCategoriaSeleccionada.setNombreReal(mNombreCategoria.getText().toString());
			mCategoriaSeleccionada.setTipoDatoExtra(mNombreCaracteristica.getText().toString());
			mCategoriaSeleccionada.setActiva(1);
			mCategoriaSeleccionada.setCategoriaPersonal(true);
			ConstantsAdmin.crearCategoriaPersonal(mCategoriaSeleccionada, oldNameCat, false, this);
			if(reset){
				ConstantsAdmin.resetPersonasOrganizadas();
			}
//					mPersonaSeleccionada.setId(idPer);

		} catch (Exception e) {
			if(mCategoriaSeleccionada.getId() == 0){
				ConstantsAdmin.mostrarMensaje(this, getString(R.string.errorAltaCategoria));
			}else{
				ConstantsAdmin.mostrarMensaje(this, getString(R.string.errorActualizacionCategoria));
			}
		}

	}
    
    
	private boolean validarEntradaDeDatos(){
		boolean estaOk = false;
		
		String nomCatStr = mNombreCategoria.getText().toString();
		String nomDatoRelStr = mNombreCaracteristica.getText().toString();
		estaOk = !nomCatStr.equals("") && !nomDatoRelStr.equals("");
		return estaOk;
	}
}
