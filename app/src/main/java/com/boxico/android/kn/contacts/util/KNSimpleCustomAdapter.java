package com.boxico.android.kn.contacts.util;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.boxico.android.kn.contacts.AltaPersonaActivity;
import com.boxico.android.kn.contacts.R;
import com.boxico.android.kn.contacts.persistencia.DataBaseManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KNSimpleCustomAdapter extends SimpleAdapter {

	private AltaPersonaActivity localContext = null;
	private List<? extends Map<String, ?>> lista = null;
	private static final String ID_TIPO_VALOR = "ID_TIPO_VALOR";
	private static final String ID_PERSONA = "ID_PERSONA";


	/**
	 * Constructor
	 *
	 * @param context  The context where the View associated with this SimpleAdapter is running
	 * @param data     A List of Maps. Each entry in the List corresponds to one row in the list. The
	 *                 Maps contain the data for each row, and should include all the entries specified in
	 *                 "from"
	 * @param resource Resource identifier of a view layout that defines the views for this list
	 *                 item. The layout file should include at least those named views defined in "to"
	 * @param from     A list of column names that will be added to the Map associated with each
	 *                 item.
	 * @param to       The views that should display column in the "from" parameter. These should all be
	 *                 TextViews. The first N views in this list are given the values of the first N columns
	 */
	public KNSimpleCustomAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		localContext = (AltaPersonaActivity) context;
		lista = data;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final View view = super.getView(position, convertView, parent);
		Button btn = view.findViewById(R.id.removeButton);
		final int pos = position;
		btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
			//	DataBaseManager mDBManager = DataBaseManager.getInstance(localContext);
			//	Object idPersona = (lista.get(pos)).get(ID_PERSONA);
				Object idTipoValor = (lista.get(pos)).get(ID_TIPO_VALOR);
				String idTVString = idTipoValor.toString();
				long idTV = Long.valueOf(idTVString);
				ConstantsAdmin.getTelefonosAEliminar().add(new Long(idTV));
				view.setVisibility(View.GONE);
			//	ConstantsAdmin.eliminarTelefonoConId(idTV, mDBManager);
			}
		});
		return view;
	}
}
