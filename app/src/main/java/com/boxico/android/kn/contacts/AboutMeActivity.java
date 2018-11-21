package com.boxico.android.kn.contacts;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.boxico.android.kn.contacts.util.ConstantsAdmin;


public class AboutMeActivity extends Activity {
	
	private  TextView mail = null;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       	this.setContentView(R.layout.acerca_de);
        this.setTitle(this.getResources().getString(R.string.app_name) + " - " + this.getResources().getString(R.string.title_acerca_de));
       	this.configurarMailYLinkPagEmpresa();
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
    
    private void configurarMailYLinkPagEmpresa(){
    	mail = this.findViewById(R.id.textAcercaDeMail);
    	mail.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				enviarMailGenerico(mail.getText().toString());
			}
		});
		TextView publicaciones = this.findViewById(R.id.textTodasLasPublicaciones);
    	publicaciones.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				abrirNavegadorConLink();
			}
		});

    	
    }
    
    
    private void abrirNavegadorConLink(){
    	try {
    		Intent intent = new Intent(Intent.ACTION_VIEW);
    		intent.setData(Uri.parse("market://search?q=pub:" + getString(R.string.text_acerca_de_empresa)));
    		startActivity(intent);
			
		} catch (Exception e) {
			ConstantsAdmin.mostrarMensaje(this, getString(R.string.error_exportar_csv));
		}
    	 	
    }

    private void enviarMailGenerico(String text){
    	try {
    		Intent email_intent =  new Intent(android.content.Intent.ACTION_SEND);
        	email_intent.setType("plain/text");
        	email_intent.putExtra(android.content.Intent.EXTRA_EMAIL, new
        	String[]{text});
        	email_intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        	email_intent.putExtra(android.content.Intent.EXTRA_TEXT, " ");
        	this.startActivity(Intent.createChooser(email_intent,""));   
			
		} catch (Exception e) {
			ConstantsAdmin.mostrarMensaje(this, getString(R.string.errorMandarMail));
		}
    	 	
    }


}