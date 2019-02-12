package com.boxico.android.kn.contacts.util;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.boxico.android.kn.contacts.persistencia.DataBaseManager;
import com.boxico.android.kn.contacts.persistencia.dtos.CategoriaDTO;

public class KNCategoryListAdapter extends ArrayAdapter<CategoriaDTO> {

	private ListadoCategoriaActivity activity = null;
 	private boolean paraProteccion;

	public KNCategoryListAdapter(Context context, int resourceId, int textViewResourceId,
								 List<CategoriaDTO> objects, boolean paraProtec) {
		// TODO Auto-generated constructor stub
		super(context, resourceId, textViewResourceId, objects);
		paraProteccion = paraProtec;

        activity = (ListadoCategoriaActivity) context;

	}

	@NonNull
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = super.getView(position, convertView, parent);
		ListView lv = (ListView)parent;
		final CategoriaDTO cat = (CategoriaDTO) lv.getAdapter().getItem(position);
		TextView tv;
		LinearLayout ll;
		ll = (LinearLayout)v;
		tv = (TextView) ll.getChildAt(0);
		final int pos = position;
		Button btnR = ll.findViewById(R.id.removeButton);

		if(!cat.isCategoriaPersonal()){
			btnR.setVisibility(View.GONE);
		}else{
			btnR.setVisibility(View.VISIBLE);
		}

		btnR.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mostrarEliminarCategoria(cat);
			}
		});



		Button btnE = ll.findViewById(R.id.editButton);

		if(!cat.isCategoriaPersonal()){
			btnE.setVisibility(View.GONE);
		}else{
			btnE.setVisibility(View.VISIBLE);
		}

		btnE.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mostrarEditarCategoria(cat);
			}
		});

		CheckBox cb = ll.findViewById(R.id.checkActivada);
	//	final ListadoCategoriaActivity act = (ListadoCategoriaActivity) activity;

		cb.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                activity.activarODesactivarCategoria(pos);
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

	private void mostrarEditarCategoria(CategoriaDTO cat){
		activity.setCategoriaSeleccionada(cat);
		activity.openAltaCategoria();
	}

	private void mostrarEliminarCategoria(CategoriaDTO cat){
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		final CategoriaDTO myCat = cat;
		builder.setMessage(R.string.mensaje_borrar_categoria)
				.setCancelable(false)
				.setPositiveButton(R.string.label_si, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						eliminarCategoriaSeleccionada(myCat);
						activity.refreshList();

					}
				})
				.setNegativeButton(R.string.label_no, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		builder.show();
	}

	private void eliminarCategoriaSeleccionada(CategoriaDTO cat){
		try {
			DataBaseManager mDBManager = DataBaseManager.getInstance(activity);
			ConstantsAdmin.eliminarCategoriaPersonal(cat, mDBManager);
		} catch (Exception e) {
			ConstantsAdmin.mostrarMensaje(activity, activity.getString(R.string.errorEliminacionCategoria));
		}

	}
	
	

}
