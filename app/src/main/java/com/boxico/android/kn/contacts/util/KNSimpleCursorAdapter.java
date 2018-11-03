package com.boxico.android.kn.contacts.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.boxico.android.kn.contacts.ListadoPersonaActivity;
import com.boxico.android.kn.contacts.R;

public class KNSimpleCursorAdapter extends SimpleCursorAdapter {
	
	private ListadoPersonaActivity localContext;

	public KNSimpleCursorAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		localContext = (ListadoPersonaActivity) context;
		// TODO Auto-generated constructor stub
	}

    private void mostrarFoto(ImageView photo, long idPer){
    	try {
    		Asociacion puedeCargar = ConstantsAdmin.comprobarSDCard(localContext);
    		boolean puede = (Boolean) puedeCargar.getKey();
    		if(puede){
            	Bitmap b = BitmapFactory.decodeFile(ConstantsAdmin.obtenerPathImagen() + String.valueOf(idPer)  + ".jpg");
            	if(b != null){
            		photo.setVisibility(View.VISIBLE);
            		photo.setImageBitmap(b);
            	}else{
            		photo.setVisibility(View.GONE);
            	}
    		}else{
    			photo.setVisibility(View.GONE);
    		}

		} catch (Exception e) {
			photo.setVisibility(View.GONE);
		}

    	
    }
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub
		 super.bindView(view, context, cursor);
		 int colorPreferidoNaranja = Color.parseColor("#24221F");
	     TextView textA = (TextView) view.findViewById(R.id.rowApellido);
	     TextView textN = (TextView) view.findViewById(R.id.rowNombres);
	     ImageView photo = (ImageView) view.findViewById(R.id.photo);
	     
	     long id = cursor.getLong(cursor.getColumnIndex(ConstantsAdmin.KEY_ROWID));
	     
	     this.mostrarFoto(photo, id);
	     
/*	     if(localContext.mMostrandoPreferidos){
	    	 textA.setTextColor(colorPreferidoNaranja);
	    	 textN.setTextColor(colorPreferidoNaranja);
	     }*/
	     textA.setText("- " + textA.getText().toString().toUpperCase());
	     
	
	}

}
