package com.boxico.android.kn.contacts.util;

import java.util.List;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.boxico.android.kn.contacts.R;
import com.boxico.android.kn.contacts.persistencia.dtos.CategoriaDTO;

public class KNArrayAdapter extends ArrayAdapter<CategoriaDTO> {

	private final Context activity;
	private final boolean paraProteccion;
	
// --Commented out by Inspection START (12/11/2018 12:34):
//	public KNArrayAdapter(Context context, int textViewResourceId,
//			List<CategoriaDTO> objects) {
//		super(context, textViewResourceId, objects);
//		// TODO Auto-generated constructor stub
//	}
// --Commented out by Inspection STOP (12/11/2018 12:34)

	public KNArrayAdapter(Context context, int resourceId, int textViewResourceId,
			List<CategoriaDTO> objects, boolean paraProtec) {
		// TODO Auto-generated constructor stub
		super(context, resourceId, textViewResourceId, objects);
		paraProteccion = paraProtec;
		activity = context;
	}

	@NonNull
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = super.getView(position, convertView, parent);
		ListView lv = (ListView)parent;
		CategoriaDTO cat = (CategoriaDTO) lv.getAdapter().getItem(position);
		TextView tv;
		LinearLayout ll;
		ll = (LinearLayout)v;
		tv = (TextView) ll.getChildAt(0);

		if(paraProteccion){// SE USA PARA MOSTRAR LAS CATEGORIAS PROTEGIDAS
			if(ConstantsAdmin.estaProtegidaCategoria(cat.getNombreReal())){
				tv.setTextColor(activity.getResources().getColor(R.color.color_celeste));
				tv.setText("<< " + tv.getText() + " >>");
			}else{
	    		tv.setTextColor(activity.getResources().getColor(R.color.color_gris_oscuro));
	    	}
		}else{// SE USA PARA MOSTRAR LAS CATEGORIAS ACTIVAS
			if(cat.getActiva() == 1){
				tv.setTextColor(activity.getResources().getColor(R.color.color_celeste));
				tv.setText("<< " + tv.getText() + " >>");
			}else{
				tv.setTextColor(activity.getResources().getColor(R.color.color_gris_oscuro));
				
			}
		}
		tv.setText("- " + tv.getText().toString().toUpperCase());
		return ll;
	}
	
	

}
