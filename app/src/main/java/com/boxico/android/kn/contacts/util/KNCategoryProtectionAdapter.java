package com.boxico.android.kn.contacts.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.boxico.android.kn.contacts.ListadoCategoriaActivity;
import com.boxico.android.kn.contacts.ProteccionCategoriaActivity;
import com.boxico.android.kn.contacts.R;
import com.boxico.android.kn.contacts.persistencia.DataBaseManager;
import com.boxico.android.kn.contacts.persistencia.dtos.CategoriaDTO;

import java.util.List;

public class KNCategoryProtectionAdapter extends ArrayAdapter<CategoriaDTO> {

	private ProteccionCategoriaActivity activity = null;
	private boolean paraProteccion;


	public KNCategoryProtectionAdapter(Context context, int resourceId, int textViewResourceId,
                                       List<CategoriaDTO> objects, boolean paraProtec) {
		// TODO Auto-generated constructor stub
		super(context, resourceId, textViewResourceId, objects);
        activity = (ProteccionCategoriaActivity) context;
	}

	@NonNull
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = super.getView(position, convertView, parent);
		ListView lv = (ListView)parent;
		final CategoriaDTO cat = (CategoriaDTO) lv.getAdapter().getItem(position);
		LinearLayout ll;
		ll = (LinearLayout)v;
		Button btnR = ll.findViewById(R.id.removeButton);
		Button btnE = ll.findViewById(R.id.editButton);
		btnR.setVisibility(View.GONE);
		btnE.setVisibility(View.GONE);
		final TextView txt = ll.findViewById(R.id.text1);


		final CheckBox cb = ll.findViewById(R.id.checkActivada);
		//	final ListadoCategoriaActivity act = (ListadoCategoriaActivity) activity;

		cb.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//activity.activarODesactivarCategoria(pos);
				activity.activarODesactivarCategoria(cat);
				if(cb.isChecked()){
					txt.setTextColor(Color.BLACK);
				}else{
					txt.setTextColor(Color.GRAY);
				}
			}
		});

		if(ConstantsAdmin.estaProtegidaCategoria(cat.getNombreReal())){
			cb.setChecked(true);
			txt.setTextColor(Color.BLACK);
		}else{
			cb.setChecked(false);
			txt.setTextColor(Color.GRAY);
		}

		return ll;
	}


	
	

}
