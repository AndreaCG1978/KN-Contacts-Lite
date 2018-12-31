package com.boxico.android.kn.contacts;

import java.io.File;
import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.boxico.android.kn.contacts.util.ConstantsAdmin;

public class PhotoActivity extends Activity {
	
	// --Commented out by Inspection (12/11/2018 12:36):protected boolean _taken = true;
	private File sdImageMainDirectory;
	private final String fileTemp = "temp.jpg";

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 0;
	private int mPersonaSeleccionadaId = -1;
	
	private boolean capturoConCamara = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {

	    try {

	        super.onCreate(savedInstanceState);   
	        mPersonaSeleccionadaId = Integer.valueOf((String)getIntent().getExtras().get(ConstantsAdmin.PERSONA_SELECCIONADA));

	        setContentView(R.layout.main);
	             /*   File root = new File(Environment
	                        .getExternalStorageDirectory()
	                        + File.separator + ConstantsAdmin.folder + File.separator);*/
	        File root = new File(ConstantsAdmin.obtenerPathImagen());
	        root.mkdirs();
	        sdImageMainDirectory = new File(root, fileTemp);
            startDialog();

	    } catch (Exception e) {
	        finish();

	    }

	}
	
    private void startDialog(){
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(R.string.title_obtener_imagen)
    	       .setCancelable(true)
    	       .setPositiveButton(R.string.title_capturar_archivo, new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	        	   startFromFileActivity();
    	        }
    	       })
    	       .setNegativeButton(R.string.title_capturar_foto, new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	        	   startCameraActivity();
    	           }
    	       });
    	builder.show();
    	
    }
    

	private void startFromFileActivity() {
		capturoConCamara = false;
	    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
	    startActivityForResult(i, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE); 
	}
	
	private void startCameraActivity() {
		capturoConCamara = true;
		Uri outputFileUri = Uri.fromFile(sdImageMainDirectory);

	    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
	    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

	    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
 

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    switch (requestCode) {
	    case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
	    {
	        if(resultCode==Activity.RESULT_OK)
	        {
	            try{
	            	Bitmap b;
	            	if(capturoConCamara){
            		   	b = BitmapFactory.decodeFile(ConstantsAdmin.obtenerPathImagen() + fileTemp);
   	            	
	            	}else{
	                    Uri selectedImage = data.getData();
	                    InputStream imageStream = getContentResolver().openInputStream(selectedImage);
	                    b = BitmapFactory.decodeStream(imageStream);
	            	}
   	     //       	Bitmap newb = Bitmap.createScaledBitmap(b, 300, 350, true);

   	            	ConstantsAdmin.almacenarImagen(this, ConstantsAdmin.folderCSV + File.separator + ConstantsAdmin.imageFolder, String.valueOf(mPersonaSeleccionadaId) + ".jpg", b);
   	            	File file = new File(ConstantsAdmin.obtenerPathImagen() + fileTemp);
   	            	if(file.exists()){
   	            		file.delete();
   	            	}


	            	
	            }
	            catch (Exception e) {
	                finish();
	            }
	        }

	        break;
	    }

	    default:
	        break;
	    }
	    finish();
	}

	 @Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
	/*    if (savedInstanceState.getBoolean(CameraCapture.PHOTO_TAKEN)) {
	        _taken = true;
	    }*/
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	  //  outState.putBoolean(CameraCapture.PHOTO_TAKEN, _taken);
	}
}
