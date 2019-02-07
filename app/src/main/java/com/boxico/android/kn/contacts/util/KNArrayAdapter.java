package com.boxico.android.kn.contacts.util;

import java.util.List;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.boxico.android.kn.contacts.ListadoCategoriaActivity;
import com.boxico.android.kn.contacts.MisCategoriasActivity;
import com.boxico.android.kn.contacts.R;
import com.boxico.android.kn.contacts.persistencia.dtos.CategoriaDTO;

public class KNArrayAdapter extends ArrayAdapter<CategoriaDTO> {

	private ListadoCategoriaActivity activityA = null;
    private MisCategoriasActivity activityP = null;
	private boolean paraProteccion;
	private boolean desdeMisCategorias = false;
	
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
		try{
            activityA = (ListadoCategoriaActivity) context;
        }catch (ClassCastException exc){
		    activityP = (MisCategoriasActivity) context;
		    desdeMisCategorias = true;
        }

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
		final int pos = position;
		Button btn = ll.findViewById(R.id.removeButton);

		if(!cat.isCategoriaPersonal()){
			btn.setVisibility(View.GONE);
		}else{
			btn.setVisibility(View.VISIBLE);
		}

		CheckBox cb = ll.findViewById(R.id.checkActivada);
	//	final ListadoCategoriaActivity act = (ListadoCategoriaActivity) activity;

		cb.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			    if(!desdeMisCategorias) {
                    activityA.activarODesactivarCategoria(pos);
                }

			}
		});

		// HABILITAR ESTO!!! SAQUE PARA HACER PRUEBAS

		if(paraProteccion){// SE USA PARA MOSTRAR LAS CATEGORIAS PROTEGIDAS
		/*	if(ConstantsAdmin.estaProtegidaCategoria(cat.getNombreReal())){
				tv.setTextColor(activity.getResources().getColor(R.color.color_celeste));
				tv.setText("<< " + tv.getText() + " >>");
			}else{
	    		tv.setTextColor(activity.getResources().getColor(R.color.color_gris_oscuro));
	    	}*/
		}else{// SE USA PARA MOSTRAR LAS CATEGORIAS ACTIVAS
			if(cat.getActiva() == 1){
				cb.setChecked(true);
			}else{
				cb.setChecked(false);
				
			}
		}
		return ll;
	}
	
	

}
