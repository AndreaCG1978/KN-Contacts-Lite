package com.boxico.android.kn.contacts;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.boxico.android.kn.contacts.persistencia.DataBaseManager;
import com.boxico.android.kn.contacts.persistencia.dtos.PersonaDTO;
import com.boxico.android.kn.contacts.util.ConstantsAdmin;

public class MenuPersonaActivity extends Activity {
	
	Dialog dialog = null;
	private int mPersonaSeleccionadaId = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        mPersonaSeleccionadaId = Integer.valueOf((String)getIntent().getExtras().get(ConstantsAdmin.PERSONA_SELECCIONADA));
        this.configurarDialog();
        this.registrarWidgets(dialog);
        dialog.show();
	}
	
	
	private void configurarDialog(){
		dialog = new Dialog(this);
        dialog.setContentView(R.layout.menu_persona);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new Dialog.OnCancelListener() {
        	public void onCancel(DialogInterface dialog) {
        		finish();
        	}
        });
	}
	
	
	private void registrarWidgets(Dialog dialog){
		PersonaDTO per = null;
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		per = ConstantsAdmin.obtenerPersonaId(this, mPersonaSeleccionadaId, mDBManager);
    	String nombre = per.getApellido() + " ";
    	if(per.getNombres() != null && !per.getNombres().equals("")){
    		nombre = nombre + ", " + per.getNombres();	
    	}
		dialog.setTitle(nombre);
		Button btn = dialog.findViewById(R.id.buttonVerPersona);
		btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	openVerDetallePersona();
            }
        });
		btn = dialog.findViewById(R.id.buttonEditarPersona);
		btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	abrirEditarPersona();
            }
        });
		btn = dialog.findViewById(R.id.buttonBorrarPersona);
		btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	eliminarPersonaDialog();
            }
        });
		btn = dialog.findViewById(R.id.buttonEnviarInfoPersona);
		btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	sendInfoByMail();
            }
        });
	}

	
    private void sendInfoByMail(){
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
    	String infoContacto = ConstantsAdmin.recuperarInfoContacto(this, mPersonaSeleccionadaId, mDBManager);
    	ConstantsAdmin.enviarMailGenerico(this, "", infoContacto, "");
    	dialog.cancel();
    	this.finish();
    }
    

	private void eliminarPersonaDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.mensaje_borrar_persona)
	       .setCancelable(false)
	       .setPositiveButton(R.string.label_si, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   eliminarPersonaSeleccionada();
	        	   ConstantsAdmin.resetPersonasOrganizadas();
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

	private void eliminarPersonaSeleccionada(){
		try {
			DataBaseManager mDBManager = DataBaseManager.getInstance(this);
			ConstantsAdmin.eliminarPersona(this, mPersonaSeleccionadaId, mDBManager);
	
		} catch (Exception e) {
			ConstantsAdmin.mostrarMensaje(this, getString(R.string.errorEliminacionContacto));
		}
	
	}
	
	private void abrirEditarPersona(){
        Intent i = new Intent(this, AltaPersonaActivity.class);
        i.putExtra(ConstantsAdmin.PERSONA_SELECCIONADA, String.valueOf(mPersonaSeleccionadaId));
        this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_ALTA_PERSONA);
    	dialog.cancel();
    	this.finish();
	}
		
    private void openVerDetallePersona(){
        Intent i = new Intent(this, DetallePersonaActivity.class);
        i.putExtra(ConstantsAdmin.PERSONA_SELECCIONADA, String.valueOf(mPersonaSeleccionadaId));
        this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_DETALLE_PERSONA);
    	dialog.cancel();
    	this.finish();
    }


}
