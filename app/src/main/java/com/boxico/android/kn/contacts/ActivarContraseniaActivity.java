package com.boxico.android.kn.contacts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.boxico.android.kn.contacts.util.ConstantsAdmin;

public class ActivarContraseniaActivity extends Activity {
	

	EditText mContrasenia = null;
	Button btnAceptar = null; 
	Dialog dialog = null;
	ActivarContraseniaActivity me = null;

	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        me = this;
        this.configurarDialog();
        this.registrarWidgets(dialog);
        this.configurarBotonAceptar();
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
        dialog.setContentView(R.layout.alta_contrasenia);
        dialog.setCancelable(true);
        dialog.setTitle(this.getString(R.string.title_alta_contrasenia));
        dialog.setOnCancelListener(new Dialog.OnCancelListener() {
        	public void onCancel(DialogInterface dialog) {
        		finish();
        	}
        });
	}
	
	
	
	private void registrarWidgets(Dialog dialog){
		mContrasenia = (EditText) dialog.findViewById(R.id.contrasenia1);
		btnAceptar = (Button) dialog.findViewById(R.id.botonActivarContrasenia);		
	}
	
    private void configurarBotonAceptar(){
    	btnAceptar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
            	if(validarContrasenia()){
                    ConstantsAdmin.contrasenia.setActiva(true);
                    ConstantsAdmin.actualizarContrasenia(ConstantsAdmin.contrasenia, me);
                    ConstantsAdmin.resetPersonasOrganizadas();
                    setResult(RESULT_OK);
                    finish();
            	}else{
            		Toast t = Toast.makeText(getApplicationContext(), R.string.error_alta_contrasenia, Toast.LENGTH_LONG);
					t.show();
            		
            	}
			}
		});
    	
    }
    
    private boolean validarContrasenia(){
    	return mContrasenia.getText().toString().equals(ConstantsAdmin.contrasenia.getContrasenia());
    }
    
 

	

}
