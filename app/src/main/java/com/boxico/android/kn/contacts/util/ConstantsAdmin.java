package com.boxico.android.kn.contacts.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.CursorLoader;
import android.widget.Toast;

import com.boxico.android.kn.contacts.*;
import com.boxico.android.kn.contacts.persistencia.DataBaseManager;
import com.boxico.android.kn.contacts.persistencia.dtos.CategoriaDTO;
import com.boxico.android.kn.contacts.persistencia.dtos.ConfigDTO;
import com.boxico.android.kn.contacts.persistencia.dtos.ContraseniaDTO;
import com.boxico.android.kn.contacts.persistencia.dtos.PersonaDTO;
import com.boxico.android.kn.contacts.persistencia.dtos.TipoValorDTO;

import android.support.v4.content.ContextCompat;

public class ConstantsAdmin {
	
	// CONSTANTES DE LA BASE DE DATOS
	

	private static Map<String, List<PersonaDTO>> organizadosAlfabeticamente = null;
	private static Map<String, List<PersonaDTO>> organizadosPorCategoria = null;


	public static CursorLoader cursorPersonas = null;
	public static ListadoPersonaActivity mainActivity = null;




	public static Map<String, List<PersonaDTO>> obtenerOrganizadosAlfabeticamente(Activity context, DataBaseManager mDBManager){
		if(organizadosAlfabeticamente == null){
			cargarPersonasAlfabeticamenteYPorCategoria(context, mDBManager);
		}
		return organizadosAlfabeticamente;
	}

	
	public static Map<String, List<PersonaDTO>> obtenerOrganizadosPorCategoria(Activity context, DataBaseManager mDBManager){
		if(organizadosPorCategoria == null){
			cargarPersonasAlfabeticamenteYPorCategoria(context, mDBManager);
		}
		return organizadosPorCategoria;
	}
	
	public static boolean isResetPersonasOrganizadas(){
		return organizadosAlfabeticamente == null;
	}
	
	public static void resetPersonasOrganizadas(){
		organizadosAlfabeticamente = null;
		organizadosPorCategoria = null;
	}
	
	private static void cargarPersonasAlfabeticamenteYPorCategoria(Activity context, DataBaseManager mDBManager) {
		// TODO Auto-generated method stub
		List<PersonaDTO> personas = obtenerPersonas(context, mDBManager);
		Iterator<PersonaDTO> it = personas.iterator();
		PersonaDTO per;
		organizadosAlfabeticamente = new HashMap<>();
		organizadosPorCategoria = new HashMap<>();
		List<PersonaDTO> listTemp;
		String primeraLetra;
		String nombreCategoria;
        while(it.hasNext()){
        	per = it.next();
        
        	// ORGANIZO ALFABETICAMENTE
        	if(!per.getApellido().equals("")){
            	primeraLetra = per.getApellido().substring(0, 1).toUpperCase();        		
        	}else{
        		primeraLetra = "";
        	}

        	listTemp = new ArrayList<>();
        	if (organizadosAlfabeticamente.containsKey(primeraLetra)) {
        		listTemp = organizadosAlfabeticamente.get(primeraLetra);
        	}
    		listTemp.add(per);
    		organizadosAlfabeticamente.put(primeraLetra, listTemp);        	

    		// ORGANIZO POR CATEGORIA
    		nombreCategoria = per.getCategoriaNombreRelativo();
        	listTemp = new ArrayList<>();
        	if (organizadosPorCategoria.containsKey(nombreCategoria)) {
        		listTemp = organizadosPorCategoria.get(nombreCategoria);
        	}
    		listTemp.add(per);
    		organizadosPorCategoria.put(nombreCategoria, listTemp);        	
    		
        
        }

		
		
		
		
		
		
	}

		
	//public static DataBaseManager getmDBManager() {
	//	return mDBManager;
	//}
/*
	public static void setmDBManager(DataBaseManager mDBManager) {
		ConstantsAdmin.mDBManager = mDBManager;
	}*/
	
    public static void inicializarBD(DataBaseManager mDBManager){
    	/*if(mDBManager == null){
    		mDBManager = new DataBaseManager(act);
    	}*/
    	mDBManager.open();
    }
    
// --Commented out by Inspection START (12/11/2018 12:34):
//    public static void abrirCursor(Cursor cur){
//    	if(cur.isClosed()){
//    	//	cur.
//    	}
//    }
// --Commented out by Inspection STOP (12/11/2018 12:34)

    public static void finalizarBD(DataBaseManager mDBManager){
    	if(mDBManager != null){
    		mDBManager.close();
    	}
    }

    public static void upgradeBD(DataBaseManager mDBManager){
    	mDBManager.upgradeDB();
    }

    public static void createBD(DataBaseManager mDBManager){
    	mDBManager.createBD();
    }
    
    
    public static void actualizarTablaCategorias(Activity context, DataBaseManager mDBManager){
    	boolean actualizo = mDBManager.actualizarTablaCategoria();
    	List<CategoriaDTO> categorias;
    	Iterator<CategoriaDTO> it;
    	CategoriaDTO cat;
    	if(actualizo){
    	      Cursor cursor = mDBManager.fetchAllCategoriasPorNombre(null);
    	  //    context.startManagingCursor(cursor);
    	      categorias = ConstantsAdmin.categoriasCursorToDtos(cursor);
    	      cursor.close();
    	//      context.stopManagingCursor(cursor);
    	      it = categorias.iterator();
    	      while(it.hasNext()){
	    	      cat = it.next();
	    	      cat.setTipoDatoExtra(obtenerTipoDatoExtraPorCategoria(cat.getNombreReal(), context));
	    	      mDBManager.actualizarCategoria(cat);
    	      }
    	}
    }

    public static void actualizarTablaContrasenia(DataBaseManager mDBManager){
    	mDBManager.actualizarTablaContrasenia();
    }
    
    
    public static void cargarCategorias(Activity context, DataBaseManager mDBManager){
    	long catSize;
    	catSize = mDBManager.tablaCategoriaSize();
    	if(catSize == 0){
    		cargarCategoriasPrivado(context, mDBManager);
    	}
    }
    
    public static void registrarTelefonos(List<TipoValorDTO> telefonos, DataBaseManager mDBManager){
    	registrarTipoValor(telefonos, TABLA_TELEFONOS, mDBManager);
    }
    
    public static void registrarMails(List<TipoValorDTO> mails, DataBaseManager mDBManager){
    	registrarTipoValor(mails, TABLA_EMAILS, mDBManager);
    }
    
    public static void registrarDirecciones(List<TipoValorDTO> direcciones, DataBaseManager mDBManager){
    	registrarTipoValor(direcciones, TABLA_DIRECCIONES, mDBManager);
    }
    
    private static void registrarTipoValor(List<TipoValorDTO> valores, String tablaNombre, DataBaseManager mDBManager){
    	Iterator<TipoValorDTO> it = valores.iterator();
    	TipoValorDTO tv;
    	inicializarBD(mDBManager);
    	while(it.hasNext()){
    		tv = it.next();
    		mDBManager.createTipoValor(tv, tablaNombre);
    	}
    	finalizarBD(mDBManager);
    }
    
    public static long tablaPreferidosSize(DataBaseManager mDBManager){
    	long valor;
    	inicializarBD(mDBManager);
    	valor = mDBManager.tablaPreferidosSize();
    	finalizarBD(mDBManager);
    	return valor;
    }
    
    private static String obtenerTipoDatoExtraPorCategoria(String categoria, Activity context){
    	String result = null;

        switch (categoria) {
            case ConstantsAdmin.CATEGORIA_AMIGOS:
                result = context.getString(R.string.hint_lugarOActividad);
                break;
            case ConstantsAdmin.CATEGORIA_CLIENTES:
                result = context.getString(R.string.hint_lugarOActividad);
                break;
            case ConstantsAdmin.CATEGORIA_COLEGAS:
                result = context.getString(R.string.hint_lugarOActividad);
                break;
            case ConstantsAdmin.CATEGORIA_COMPANIEROS:
                result = context.getString(R.string.hint_lugarOActividad);
                break;
            case ConstantsAdmin.CATEGORIA_CONOCIDOS:
                result = context.getString(R.string.hint_lugarOActividad);
                break;
            case ConstantsAdmin.CATEGORIA_FAMILIARES:
                result = context.getString(R.string.hint_parentesco);
                break;
            case ConstantsAdmin.CATEGORIA_JEFES:
                result = context.getString(R.string.hint_empresa);
                break;
            case ConstantsAdmin.CATEGORIA_MEDICO:
                result = context.getString(R.string.hint_especialidad);
                break;
            case ConstantsAdmin.CATEGORIA_OTROS:
                result = context.getString(R.string.hint_rol);
                break;
            case ConstantsAdmin.CATEGORIA_PACIENTES:
                result = context.getString(R.string.hint_obraSocial);
                break;
            case ConstantsAdmin.CATEGORIA_PROFESORES:
                result = context.getString(R.string.hint_lugarOActividad);
                break;
            case ConstantsAdmin.CATEGORIA_PROVEEDORES:
                result = context.getString(R.string.hint_empresa);
                break;
            case ConstantsAdmin.CATEGORIA_SOCIOS:
                result = context.getString(R.string.hint_empresa);
                break;
            case ConstantsAdmin.CATEGORIA_VECINOS:
                result = context.getString(R.string.hint_zona);
                break;
        }
     	
    	return result;
    }
    
	private static void cargarCategoriasPrivado(Activity context, DataBaseManager mDBManager){
		CategoriaDTO cat;
		String tipoDE;
		
		tipoDE = obtenerTipoDatoExtraPorCategoria(ConstantsAdmin.CATEGORIA_AMIGOS, context);
		cat = new CategoriaDTO(0 , ConstantsAdmin.CATEGORIA_AMIGOS, ConstantsAdmin.CATEGORIA_AMIGOS, 1, tipoDE);
		mDBManager.crearCategoria(cat, false);

		tipoDE = obtenerTipoDatoExtraPorCategoria(ConstantsAdmin.CATEGORIA_CLIENTES, context);
		cat = new CategoriaDTO(0, ConstantsAdmin.CATEGORIA_CLIENTES,ConstantsAdmin.CATEGORIA_CLIENTES, 1, tipoDE);
		mDBManager.crearCategoria(cat, false);

		tipoDE = obtenerTipoDatoExtraPorCategoria(ConstantsAdmin.CATEGORIA_COMPANIEROS, context);
		cat = new CategoriaDTO(0, ConstantsAdmin.CATEGORIA_COMPANIEROS, ConstantsAdmin.CATEGORIA_COMPANIEROS, 1, tipoDE);
		mDBManager.crearCategoria(cat, false);
		
//		cat = new CategoriaDTO(0, ConstantsAdmin.CATEGORIA_CONOCIDOS, ConstantsAdmin.CATEGORIA_CONOCIDOS, 0);
//		mDBManager.crearCategoria(cat);
		tipoDE = obtenerTipoDatoExtraPorCategoria(ConstantsAdmin.CATEGORIA_FAMILIARES, context);		
		cat = new CategoriaDTO(0, ConstantsAdmin.CATEGORIA_FAMILIARES, ConstantsAdmin.CATEGORIA_FAMILIARES, 1, tipoDE);
		mDBManager.crearCategoria(cat, false);

		tipoDE = obtenerTipoDatoExtraPorCategoria(ConstantsAdmin.CATEGORIA_JEFES, context);
		cat = new CategoriaDTO(0, ConstantsAdmin.CATEGORIA_JEFES,ConstantsAdmin.CATEGORIA_JEFES,  1, tipoDE);
		mDBManager.crearCategoria(cat, false);

		tipoDE = obtenerTipoDatoExtraPorCategoria(ConstantsAdmin.CATEGORIA_MEDICO, context);
		cat = new CategoriaDTO(0, ConstantsAdmin.CATEGORIA_MEDICO, ConstantsAdmin.CATEGORIA_MEDICO, 1, tipoDE);
		mDBManager.crearCategoria(cat, false);
	
		tipoDE = obtenerTipoDatoExtraPorCategoria(ConstantsAdmin.CATEGORIA_OTROS, context);
		cat = new CategoriaDTO(0, ConstantsAdmin.CATEGORIA_OTROS, ConstantsAdmin.CATEGORIA_OTROS, 1, tipoDE);
		mDBManager.crearCategoria(cat, false);

		tipoDE = obtenerTipoDatoExtraPorCategoria(ConstantsAdmin.CATEGORIA_PACIENTES, context);
		cat = new CategoriaDTO(0, ConstantsAdmin.CATEGORIA_PACIENTES,ConstantsAdmin.CATEGORIA_PACIENTES, 1, tipoDE);
		mDBManager.crearCategoria(cat, false);

		tipoDE = obtenerTipoDatoExtraPorCategoria(ConstantsAdmin.CATEGORIA_PROFESORES, context);
		cat = new CategoriaDTO(0, ConstantsAdmin.CATEGORIA_PROFESORES, ConstantsAdmin.CATEGORIA_PROFESORES, 1, tipoDE);
		mDBManager.crearCategoria(cat, false);

		tipoDE = obtenerTipoDatoExtraPorCategoria(ConstantsAdmin.CATEGORIA_PROVEEDORES, context);
		cat = new CategoriaDTO(0, ConstantsAdmin.CATEGORIA_PROVEEDORES, ConstantsAdmin.CATEGORIA_PROVEEDORES, 1, tipoDE);
		mDBManager.crearCategoria(cat, false);

		tipoDE = obtenerTipoDatoExtraPorCategoria(ConstantsAdmin.CATEGORIA_SOCIOS, context);
		cat = new CategoriaDTO(0, ConstantsAdmin.CATEGORIA_SOCIOS, ConstantsAdmin.CATEGORIA_SOCIOS, 1, tipoDE);
		mDBManager.crearCategoria(cat, false);
	
		tipoDE = obtenerTipoDatoExtraPorCategoria(ConstantsAdmin.CATEGORIA_VECINOS, context);		
		cat = new CategoriaDTO(0, ConstantsAdmin.CATEGORIA_VECINOS, ConstantsAdmin.CATEGORIA_VECINOS, 1, tipoDE);
		mDBManager.crearCategoria(cat, false);

	
	}



	public static final String DATABASE_NAME = "FreshAir_ContactsApp";
	public static final int DATABASE_VERSION = 1;
	public static final String TAG = "DataBaseManager";
	public static final String PERSONA_SELECCIONADA = "personaSeleccionada";
	

	// TABLA: Persona
	public static final int MAX_CONTACTS = 8;
	
	public static final String TABLA_PERSONA = "persona";
	public static final String KEY_APELLIDO = "apellido";
    public static final String KEY_NOMBRES = "nombre";
    public static final String KEY_FECHA_NACIMIENTO = "fechaNacimiento";
	public static final String KEY_TEL_PARTICULAR = "telParticular";
	public static final String KEY_TEL_CELULAR = "telCelular";
	public static final String KEY_TEL_LABORAL = "telLaboral";
	public static final String KEY_EMAIL_PARTICULAR = "emailParticular";
	public static final String KEY_EMAIL_LABORAL = "emailLaboral";
	public static final String KEY_EMAIL_OTRO = "emailOtro";
	public static final String KEY_CATEGORIA = "categoria";
	public static final String KEY_DESCRIPCION = "descripcion";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_DIRECCION_PARTICULAR = "direccionParticular";
    public static final String KEY_DIRECCION_LABORAL = "direccionLaboral";
    public static final String KEY_DATO_EXTRA = "empresa";
    public static final String KEY_NOMBRE_CATEGORIA_RELATIVO = "nombreCategoriaRelativo";

    public static final String CATEGORIA_PACIENTES = "Paciente";
    public static final String CATEGORIA_PROVEEDORES = "Proveedor";
    public static final String CATEGORIA_CLIENTES = "Cliente";
    public static final String CATEGORIA_OTROS = "Otro";
    public static final String CATEGORIA_FAMILIARES = "Familiar";
    public static final String CATEGORIA_SOCIOS= "Socio";
    public static final String CATEGORIA_JEFES = "Jefe";
    public static final String CATEGORIA_AMIGOS = "Amigo";
    public static final String CATEGORIA_COMPANIEROS = "Companero";
    public static final String CATEGORIA_CONOCIDOS = "Conocido";
    public static final String CATEGORIA_PROFESORES = "Profesor";
    public static final String CATEGORIA_VECINOS = "Vecino";
    public static final String CATEGORIA_COLEGAS = "Colega";
    public static final String CATEGORIA_MEDICO = "Doctor";
    private static final String CATEGORIA_TODOS = "TODAS";
    
    // TABLA: Categoria
    
    public static final String TABLA_CATEGORIA = "categorias";
    public static final String CATEGORIA_SELECCIONADA = "categoriaSeleccionada";
    public static final String TABLA_CATEGORIA_PERSONALES = "categoriasPersonales";
    public static final String KEY_NOMBRE_CATEGORIA = "nombreCategoria";
    public static final String KEY_CATEGORIA_ACTIVA = "categoriaActiva";
    public static final String KEY_CATEGORIA_TIPO_DATO_EXTRA = "tipoDatoExtra";
    
    public static final String TABLA_PREFERIDOS = "preferidos";
    
    /*
    
    public static final String smesEne="Enero";
    public static final String smesFeb="Febrero";
    public static final String smesMar="Marzo";
    public static final String smesAbr="Abril";
    public static final String smesMay="Mayo";
    public static final String smesJun="Junio";
    public static final String smesJul="Julio";
    public static final String smesAgo="Agosto";
    public static final String smesSep="Septiembre";
    public static final String smesOct="Octubre";
    public static final String smesNov="Noviembre";
    public static final String smesDic="Diciembre";

    */
    
    // CODIGOS DE REQUERIMIENTOS DE INTENTS
    
 //   public static final int ACTIVITY_EJECUTAR_LISTADO_PERSONAS=1;
    public static final int ACTIVITY_EJECUTAR_DETALLE_PERSONA=3;
    public static final int ACTIVITY_EJECUTAR_ALTA_PERSONA=2;
    public static final int ACTIVITY_EJECUTAR_LISTADO_CATEGORIAS=4;
    public static final int ACTIVITY_EJECUTAR_IMPORTAR_CONTACTOS=5;
    public static final int ACTIVITY_EJECUTAR_ELIMINAR_CONTACTO=6;
    public static final int ACTIVITY_LLAMAR_CONTACTO = 7;
    public static final int ACTIVITY_EJECUTAR_ABOUT_ME = 8;
    public static final int ACTIVITY_EJECUTAR_MIS_CATEGORIAS = 9;
    public static final int ACTIVITY_EJECUTAR_ALTA_CATEGORIA=10;
    public static final int ACTIVITY_EJECUTAR_EDITAR_CATEGORIA=11;
  //  public static final int ACTIVITY_EJECUTAR_ELIMINAR_CATEGORIA=12;
    public static final int ACTIVITY_EJECUTAR_EDICION_TELEFONO=13;
    public static final int ACTIVITY_EJECUTAR_ALTA_TELEFONO=14;    
    public static final int ACTIVITY_EJECUTAR_EDICION_EMAIL=15;
    public static final int ACTIVITY_EJECUTAR_ALTA_EMAIL=16; 
    public static final int ACTIVITY_EJECUTAR_EDICION_DIRECCION=17;
    public static final int ACTIVITY_EJECUTAR_ALTA_DIRECCION=18; 
  /*
    public static final String TIPO_DATO_EXTRA_ACTIVIDAD = "ACTIVIDAD";
    public static final String TIPO_DATO_EXTRA_PARENTESCO = "PARENTESCO";
    public static final String TIPO_DATO_EXTRA_EMPRESA = "EMPRESA";
    public static final String TIPO_DATO_EXTRA_OBRA_SOCIAL = "OBRA SOCIAL";
    public static final String TIPO_DATO_EXTRA_OBRA_ZONA = "ZONA";
    public static final String TIPO_DATO_EXTRA_OBRA_ESPECIALIDAD = "ESPECIALIDAD";
    public static final String TIPO_DATO_EXTRA_ROL = "ROL";
  */
    public static final String SEPARADOR_FECHA = "-";
    public static final String LANG_ESPANOL = "es";
	public static final String TABLA_TELEFONOS = "telefonos";
	public static final String TABLA_EMAILS = "emails";
	public static final String TABLA_DIRECCIONES = "direcciones";
	public static final String TABLA_CONFIGURACION = "configuraciones";
	public static final String KEY_VALOR = "valor";
	public static final String KEY_TIPO = "tipo";
	public static final String KEY_ID_PERSONA = "idPersona";
	public static final String KEY_ORDENADO_POR_CATEGORIA = "ordenadoPorCateg";
	public static final String KEY_ESTAN_DETALLADOS = "estanDetallados";
	public static final String KEY_LISTA_EXPANDIDA = "listaExpandida";
	public static final String KEY_MUESTRA_PREFERIDOS = "muestraPreferidos";
	
	public static final String TIPO_ELEMENTO = "tipoElemento";
	public static final String TIPO_TELEFONO = "tipoTelefono";
	public static final String TIPO_EMAIL = "tipoEmail";
	public static final String TIPO_DIRECCION = "tipoDireccion";
	
	
    
    public static List<CategoriaDTO> categoriasCursorToDtos(Cursor cursor){
    	ArrayList<CategoriaDTO> result = new ArrayList<>();
    	CategoriaDTO cat;
    	
    	String catName;
    	String tipoDatoExtra;
    	long catId;
    	long catActiva;
    	
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
      	  catName = cursor.getString(cursor.getColumnIndex(ConstantsAdmin.KEY_NOMBRE_CATEGORIA));
      	  catId = cursor.getLong(cursor.getColumnIndex(ConstantsAdmin.KEY_ROWID));
      	  catActiva = cursor.getLong(cursor.getColumnIndex(ConstantsAdmin.KEY_CATEGORIA_ACTIVA));
      	  tipoDatoExtra = cursor.getString(cursor.getColumnIndex(ConstantsAdmin.KEY_CATEGORIA_TIPO_DATO_EXTRA));
      	  cat = new CategoriaDTO(catId, catName, catName, catActiva, tipoDatoExtra);
      	  cat.setCategoriaPersonal(false);
      	  result.add(cat);
      	  cursor.moveToNext();
        }
        
        return result;

    }   
    
    private static List<CategoriaDTO> categoriasProtegidasCursorToDtos(Cursor cursor){
    	ArrayList<CategoriaDTO> result = new ArrayList<>();
    	CategoriaDTO cat;
    	
    	String catName;
    	long catId;
    	
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
      	  catName = cursor.getString(cursor.getColumnIndex(ConstantsAdmin.KEY_NOMBRE_CATEGORIA));
      	  catId = cursor.getLong(cursor.getColumnIndex(ConstantsAdmin.KEY_ROWID));
       	  cat = new CategoriaDTO(catId, catName, catName, 0, "");
      	  result.add(cat);
      	  cursor.moveToNext();
        }
        
        return result;

    }   
    
    public static List<CategoriaDTO> categoriasPersonalesCursorToDtos(Cursor cursor){
    	ArrayList<CategoriaDTO> result = new ArrayList<>();
    	CategoriaDTO cat;
    	
    	String catName;
    	String tipoDatoExtra;
    	long catId;
    	long catActiva;
    	
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
      	  catName = cursor.getString(cursor.getColumnIndex(ConstantsAdmin.KEY_NOMBRE_CATEGORIA));
      	  catId = cursor.getLong(cursor.getColumnIndex(ConstantsAdmin.KEY_ROWID));
      	  catActiva = cursor.getLong(cursor.getColumnIndex(ConstantsAdmin.KEY_CATEGORIA_ACTIVA));
      	  tipoDatoExtra = cursor.getString(cursor.getColumnIndex(ConstantsAdmin.KEY_CATEGORIA_TIPO_DATO_EXTRA));
      	  cat = new CategoriaDTO(catId, catName, catName, catActiva, tipoDatoExtra);
      	  cat.setCategoriaPersonal(true);
      	  result.add(cat);
      	  cursor.moveToNext();
        }
        
        return result;

    }   
    
    public static PersonaDTO obtenerPersonaId(Activity context, long idPer, DataBaseManager mDBManager){
    	PersonaDTO per = new PersonaDTO();
        per.setId(idPer);
    	Cursor perCursor;
		inicializarBD(mDBManager);
    	perCursor = mDBManager.fetchPersonaPorId(idPer);
    //	context.startManagingCursor(perCursor);
    	per = cursorToPersonaDto(perCursor);
    //	perCursor.close();
    //	context.stopManagingCursor(perCursor);
    	finalizarBD(mDBManager);
    	return per;
    }
    
    public static CategoriaDTO obtenerCategoriaPersonalId(Activity context, long idCat, DataBaseManager mDBManager){
    	CategoriaDTO cat = new CategoriaDTO();
    	cat.setId(idCat);
    	Cursor catCursor;
		inicializarBD(mDBManager);
    	catCursor = mDBManager.fetchCategoriaPersonalPorId(idCat);
    //	context.startManagingCursor(catCursor);
    	cat = cursorToCategoriaDto(catCursor);
    //	catCursor.close();
    //	context.stopManagingCursor(catCursor);
    	finalizarBD(mDBManager);
    	return cat;
    }
    
    
    private static List<TipoValorDTO> cursorToTipoValorDtos(Cursor cursor){
    	ArrayList<TipoValorDTO> result = new ArrayList<>();
    	TipoValorDTO tv;

        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
        	tv = cursorToTipoValorDto(cursor);
      	  	result.add(tv);
      	  	cursor.moveToNext();
        }
        
        return result;

    }   
        

    
    public static List<TipoValorDTO> obtenerTelefonosIdPersona(Activity context, long id, DataBaseManager mDBManager){
    	return obtenerTipoValorDtosIdPersona(context, id, ConstantsAdmin.TABLA_TELEFONOS, mDBManager);
    }
    
    public static List<TipoValorDTO> obtenerEmailsIdPersona(Activity context, long id, DataBaseManager mDBManager){
    	return obtenerTipoValorDtosIdPersona(context, id, ConstantsAdmin.TABLA_EMAILS, mDBManager);
    }

    public static List<TipoValorDTO> obtenerDireccionesIdPersona(Activity context, long id, DataBaseManager mDBManager){
    	return obtenerTipoValorDtosIdPersona(context, id, ConstantsAdmin.TABLA_DIRECCIONES, mDBManager);
    }
    
    private static List<TipoValorDTO> obtenerTipoValorDtosIdPersona(Activity context, long id, String tablaName, DataBaseManager mDBManager){
    	Cursor cur;
    	List<TipoValorDTO> result;
		inicializarBD(mDBManager);
    	cur = mDBManager.fetchTipoValorPorIdPersona(id, tablaName);
  //  	context.startManagingCursor(cur);
    	result = cursorToTipoValorDtos(cur);
  //  	cur.close();
  //  	context.stopManagingCursor(cur);
    	finalizarBD(mDBManager);
    	return result;
    }

    
    private static CategoriaDTO cursorToCategoriaDto(Cursor catCursor){
    	String temp;
    	CategoriaDTO cat = new CategoriaDTO();
    	if(catCursor != null){
	  //  	context.stopManagingCursor(catCursor);
	     //   context.startManagingCursor(catCursor);
	        temp = catCursor.getString(catCursor.getColumnIndex(ConstantsAdmin.KEY_ROWID));
	        cat.setId(Long.valueOf(temp));
	        temp = catCursor.getString(catCursor.getColumnIndex(ConstantsAdmin.KEY_NOMBRE_CATEGORIA));
	        cat.setNombreReal(temp);
	        temp = catCursor.getString(catCursor.getColumnIndex(ConstantsAdmin.KEY_CATEGORIA_ACTIVA));
	       	cat.setActiva(Long.valueOf(temp));
	        temp = catCursor.getString(catCursor.getColumnIndex(ConstantsAdmin.KEY_CATEGORIA_TIPO_DATO_EXTRA));
	        cat.setTipoDatoExtra(temp);
    	}
    	return cat;
    }

    
    private static TipoValorDTO cursorToTipoValorDto(Cursor cur){
    	String temp;
    	TipoValorDTO tv = new TipoValorDTO();
    	if(cur != null){
	        temp = cur.getString(cur.getColumnIndex(ConstantsAdmin.KEY_ROWID));
	        tv.setId(Long.valueOf(temp));
	        temp = cur.getString(cur.getColumnIndex(ConstantsAdmin.KEY_TIPO));
	        tv.setTipo(temp);
	        temp = cur.getString(cur.getColumnIndex(ConstantsAdmin.KEY_VALOR));
	       	tv.setValor(temp);
	        temp = cur.getString(cur.getColumnIndex(ConstantsAdmin.KEY_ID_PERSONA));
	        tv.setIdPersona(temp);
    	}
    	return tv;
    }    

    private static Long cursorToPreferido(Cursor cur){
    	String temp;
    	Long id = null;
    	if(cur != null){
	        temp = cur.getString(cur.getColumnIndex(ConstantsAdmin.KEY_ROWID));
	        id = Long.valueOf(temp);
    	}
    	return id;
    }     
    
    private static PersonaDTO cursorToPersonaDto(Cursor perCursor){
    	String temp;
    	PersonaDTO per = new PersonaDTO();
    	if(perCursor != null){
	 //   	context.stopManagingCursor(perCursor);
	//        context.startManagingCursor(perCursor);
	        temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_ROWID));
	        per.setId(Long.valueOf(temp));
	        temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_APELLIDO));
	        per.setApellido(temp);
	        temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_NOMBRES));
	        per.setNombres(temp);
	        temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_TEL_PARTICULAR));
	        per.setTelParticular(temp);
	        temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_TEL_CELULAR));
	        per.setCelular(temp);
	        temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_TEL_LABORAL));
	        per.setTelLaboral(temp);
	        temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_EMAIL_PARTICULAR));
	        per.setEmailParticular(temp);
	        temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_EMAIL_LABORAL));
	        per.setEmailLaboral(temp);
	        temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_EMAIL_OTRO));
	        per.setEmailOtro(temp);
	        temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_DIRECCION_PARTICULAR));
	        per.setDireccionParticular(temp);
	        temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_DIRECCION_LABORAL));
	        per.setDireccionLaboral(temp);
	        temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_FECHA_NACIMIENTO));
	        per.setFechaNacimiento(temp);
	        temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_CATEGORIA)); 
	        per.setCategoriaId(Integer.valueOf(temp));
	        temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_NOMBRE_CATEGORIA)); 
	        per.setCategoriaNombre(temp);
	        temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_NOMBRE_CATEGORIA_RELATIVO)); 
	        per.setCategoriaNombreRelativo(temp);
	        temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_DATO_EXTRA));
	        per.setDatoExtra(temp);
	        temp = perCursor.getString(perCursor.getColumnIndex(ConstantsAdmin.KEY_DESCRIPCION));
	        per.setDescripcion(temp);
    	}
    	return per;
    }
    
    
    public static PersonaDTO obtenerPersonaConNombreYApellido(String name, String apellido, Activity context, DataBaseManager mDBManager){
    	Cursor cursor;
    	PersonaDTO per = null;
    	cursor = mDBManager.fetchPersonaPorNombreYApellido(name, apellido);
    	if(cursor != null){
    	//	context.startManagingCursor(cursor);
    		if(cursor.getCount() > 0){
        		per = cursorToPersonaDto(cursor);
    		}
    	//	cursor.close();
    	//	context.stopManagingCursor(cursor);
    	}
    	if(per == null){
        	cursor = mDBManager.fetchPersonaPorNombreYApellido(apellido, name);
        	if(cursor != null){
        	//	context.startManagingCursor(cursor);
        		if(cursor.getCount() > 0){
            		per = cursorToPersonaDto(cursor);
        		}
        	//	cursor.close();
        	//	context.stopManagingCursor(cursor);
        	}
    		
    	}
    	return per;
    }

    public static String obtenerNombreCategoria(String nCat, Context context){
    	String nombreCat = null;
		switch (nCat) {
			case ConstantsAdmin.CATEGORIA_AMIGOS:
				nombreCat = context.getString(R.string.cat_Amigo);
				break;
			case ConstantsAdmin.CATEGORIA_CLIENTES:
				nombreCat = context.getString(R.string.cat_Cliente);
				break;
			case ConstantsAdmin.CATEGORIA_COLEGAS:
				nombreCat = context.getString(R.string.cat_Colega);
				break;
			case ConstantsAdmin.CATEGORIA_COMPANIEROS:
				nombreCat = context.getString(R.string.cat_Companiero);
				break;
			case ConstantsAdmin.CATEGORIA_CONOCIDOS:
				nombreCat = context.getString(R.string.cat_Conocido);
				break;
			case ConstantsAdmin.CATEGORIA_FAMILIARES:
				nombreCat = context.getString(R.string.cat_Familiar);
				break;
			case ConstantsAdmin.CATEGORIA_JEFES:
				nombreCat = context.getString(R.string.cat_Jefe);
				break;
			case ConstantsAdmin.CATEGORIA_MEDICO:
				nombreCat = context.getString(R.string.cat_Medico);
				break;
			case ConstantsAdmin.CATEGORIA_OTROS:
				nombreCat = context.getString(R.string.cat_Otro);
				break;
			case ConstantsAdmin.CATEGORIA_PACIENTES:
				nombreCat = context.getString(R.string.cat_Paciente);
				break;
			case ConstantsAdmin.CATEGORIA_PROFESORES:
				nombreCat = context.getString(R.string.cat_Profesor);
				break;
			case ConstantsAdmin.CATEGORIA_PROVEEDORES:
				nombreCat = context.getString(R.string.cat_Proveedor);
				break;
			case ConstantsAdmin.CATEGORIA_SOCIOS:
				nombreCat = context.getString(R.string.cat_Socio);
				break;
			case ConstantsAdmin.CATEGORIA_VECINOS:
				nombreCat = context.getString(R.string.cat_Vecino);
				break;
			case ConstantsAdmin.CATEGORIA_TODOS:
				nombreCat = context.getString(R.string.cat_TODAS);
				break;
		}
    	return nombreCat;
    }

    public static void mostrarMensaje(Context context, String message){
    	Toast t = Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_LONG);
		t.show();    	
    }
    
    public static TipoValorDTO tipoValorSeleccionado = null;
    public static TipoValorDTO tipoValorAnteriorSeleccionado = null;
    public static ArrayList<String> tiposValores = null;
	public static PersonaDTO personaSeleccionada = null;
	
	public static ArrayList<TipoValorDTO> telefonosARegistrar = null;
	public static ArrayList<TipoValorDTO> mailsARegistrar = null;
	public static ArrayList<TipoValorDTO> direccionesARegistrar = null;

    public static Asociacion comprobarSDCard(Activity context){
    	Asociacion map;
        String auxSDCardStatus = Environment.getExternalStorageState();
        boolean sePuede = false;
        String msg = null;

        switch (auxSDCardStatus) {
            case Environment.MEDIA_MOUNTED:
                sePuede = true;
                break;
            case Environment.MEDIA_MOUNTED_READ_ONLY:
                msg = context.getString(R.string.mensaje_error_tarjeta_solo_lectura);
                sePuede = false;
                break;
            case Environment.MEDIA_NOFS:
                msg = context.getString(R.string.mensaje_error_tarjeta_no_formato);
                sePuede = false;
                break;
            case Environment.MEDIA_REMOVED:
                msg = context.getString(R.string.mensaje_error_tarjeta_no_montada);
                sePuede = false;
                break;
            case Environment.MEDIA_SHARED:
                msg = context.getString(R.string.mensaje_error_tarjeta_shared);
                sePuede = false;
                break;
            case Environment.MEDIA_UNMOUNTABLE:
                msg = context.getString(R.string.mensaje_error_tarjeta_unmountable);
                sePuede = false;
                break;
            case Environment.MEDIA_UNMOUNTED:
                msg = context.getString(R.string.mensaje_error_tarjeta_unmounted);
                sePuede = false;
                break;
        }
        map = new Asociacion(sePuede, msg);
        
        return map;
    }
    
    public static String mensaje = null;
    public static final String folderCSV = "KN-Contacts";
    private static final String fileCSV = "kncontacts.csv";

	public static void importarCSV(Activity context, DataBaseManager mDBManager){
        String body;
        File file;
        mensaje = context.getString(R.string.error_importar_csv);
        try {

            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (!ActivityCompat.shouldShowRequestPermissionRationale(context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(context,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            1);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }

        	file = obtenerFileCSV(context);
        	if(file != null){
                if(file.getName().equals(fileCSV)){
                	body = obtenerContenidoArchivo(file, context);
                	procesarStringDatos(body, mDBManager);
                	mensaje = context.getString(R.string.mensaje_exito_importar_csv);
                }
            }
		} catch (Exception e) {
			mensaje = context.getString(R.string.error_importar_csv);

		}
    }
    
    private static void procesarStringDatos(String body, DataBaseManager mDBManager){

    	String[] lineas = body.split(ENTER);
    	int i = 0;
    	String linea;
    	String[] campos;
    	ArrayList<String> lista;
    	HashMap<String, ArrayList<String>> map = new HashMap<>();
    	while(i < lineas.length){
    		linea = lineas[i];
    		campos = linea.split(PUNTO_COMA);
    		lista = map.get(campos[0]);
    		if(lista == null){
    			lista = new ArrayList<>();
    			map.put(campos[0], lista);
    		}
    		lista.add(linea);
    		i++;
    	}
    	List<PersonaDTO> personas = procesarPersonas(map.get(HEAD_PERSONA));
    	List<CategoriaDTO> categorias = procesarCategorias(map.get(HEAD_CATEGORIA));
    	List<CategoriaDTO> categoriasPersonales = procesarCategoriasPersonales(map.get(HEAD_CATEGORIA_PERSONAL));
    	List<Long> preferidos = procesarPreferidos(map.get(HEAD_PREFERIDO));
    	List<CategoriaDTO> categoriasProtegidas = procesarCategoriasProtegidas(map.get(HEAD_CATEGORIA_PROTEGIDA));
    	ContraseniaDTO pass = procesarContrasenia(map.get(HEAD_CONTRASENIA));
    	almacenarDatos(personas, categorias, categoriasPersonales, preferidos, categoriasProtegidas, pass, mDBManager );
    	
    }
    
    private static void almacenarDatos(List<PersonaDTO> personas, List<CategoriaDTO> categorias, List<CategoriaDTO> categoriasPersonales, List<Long> preferidos, List<CategoriaDTO> catProtegidas, ContraseniaDTO pass, DataBaseManager mDBManager){
    	PersonaDTO per;
    	CategoriaDTO cat;
    	Long pref;
    	TipoValorDTO tv;
    	
    	Iterator<PersonaDTO> itPers;
    	Iterator<CategoriaDTO> itCat;
    	Iterator<Long> itPref;
    	Iterator<TipoValorDTO> itTv;
    	
    	itPers = personas.iterator();
    	itCat = categorias.iterator();
    	itPref = preferidos.iterator();
    	itTv = telefonosARegistrar.iterator();
    	
    	inicializarBD(mDBManager);
    	mDBManager.deleteAll();
    	while(itPers.hasNext()){
    		per = itPers.next();
    		mDBManager.createPersona(per, true);
    	}
    	while(itCat.hasNext()){
    		cat = itCat.next();
    		mDBManager.crearCategoria(cat, true);
    	}
    	itCat = categoriasPersonales.iterator();
    	while(itCat.hasNext()){
    		cat = itCat.next();
    		mDBManager.crearCategoriaPersonal(cat, null, true);
    	}   
    	
    	itCat = catProtegidas.iterator();
    	categoriasProtegidas = new ArrayList<>();
    	while(itCat.hasNext()){
    		cat = itCat.next();
    		mDBManager.crearCategoriaProtegida(cat);
    		categoriasProtegidas.add(cat);
    		
    	}  
    	while(itPref.hasNext()){
    		pref = itPref.next();
    		mDBManager.crearPreferido(pref);
    	}   
    	while(itTv.hasNext()){
    		tv = itTv.next();
    		mDBManager.createTelefono(tv);
    	} 
    	itTv = mailsARegistrar.iterator();
    	while(itTv.hasNext()){
    		tv = itTv.next();
    		mDBManager.createEmail(tv);
    	}     	
    	itTv = direccionesARegistrar.iterator();
    	while(itTv.hasNext()){
    		tv = itTv.next();
    		mDBManager.createDireccion(tv);
    	}     	
    	
    	contrasenia = pass;
    	if(pass.getId() != -1){
    		pass.setId(-1);
    		long id = mDBManager.crearContrasenia(pass);
    		contrasenia.setId(id);
    	}
    	finalizarBD(mDBManager);
    	telefonosARegistrar = null;
    	mailsARegistrar = null;
    	direccionesARegistrar = null;
    	
    }
    
    private static List<PersonaDTO> procesarPersonas(ArrayList<String> lista){
    	String linea;
    	List<PersonaDTO> resultado = new ArrayList<>();
    	String[] campos;
    	String temp;
    	Iterator<String> it = lista.iterator();
    	PersonaDTO per;
    	int i;
    	String[] par;
    	TipoValorDTO tv;
    	telefonosARegistrar = new ArrayList<>();
    	mailsARegistrar = new ArrayList<>();
    	direccionesARegistrar = new ArrayList<>();
    	while(it.hasNext()){
    		linea = it.next();
    		campos = linea.split(PUNTO_COMA);
    		per = new PersonaDTO();
    		per.setId(Long.valueOf(campos[1]));
    		per.setApellido(campos[2]);
    		if(!campos[3].equals(CAMPO_NULO)){
    			per.setNombres(campos[3]);
    		}
    		if(!campos[4].equals(CAMPO_NULO)){
    			per.setFechaNacimiento(campos[4]);
    		}
    		
    		//PROCESO LOS TELEFONOS
    		temp = campos[6];
    		i = 6;
    		while(!temp.equals(FIN)){
    			par = campos[i].split(SEPARACION_ATRIBUTO);
				switch (par[0]) {
					case PARTICULAR:
						per.setTelParticular(par[1]);
						break;
					case LABORAL:
						per.setTelLaboral(par[1]);
						break;
					case MOVIL:
						per.setCelular(par[1]);
						break;
					default:
						tv = new TipoValorDTO();
						tv.setIdPersona(String.valueOf(per.getId()));
						tv.setTipo(par[0]);
						tv.setValor(par[1]);
						telefonosARegistrar.add(tv);
						break;
				}
    			
    			i++;
    			temp = campos[i];
    		}
    		
    		//PROCESO LOS MAILS
    		
    		i++;
    		i++;
    		temp = campos[i];
    		while(!temp.equals(FIN)){
    			par = campos[i].split(SEPARACION_ATRIBUTO);
                switch (par[0]) {
                    case PARTICULAR:
                        per.setEmailParticular(par[1]);
                        break;
                    case LABORAL:
                        per.setEmailLaboral(par[1]);
                        break;
                    case OTRO:
                        per.setEmailOtro(par[1]);
                        break;
                    default:
                        tv = new TipoValorDTO();
                        tv.setIdPersona(String.valueOf(per.getId()));
                        tv.setTipo(par[0]);
                        tv.setValor(par[1]);
                        mailsARegistrar.add(tv);
                        break;
                }
    			
    			i++;
    			temp = campos[i];
    		}
    		
    		//PROCESO LAS DIRECCIONES
    		
    		i++;
    		i++;
    		temp = campos[i];
    		while(!temp.equals(FIN)){
    			par = campos[i].split(SEPARACION_ATRIBUTO);
                switch (par[0]) {
                    case PARTICULAR:
                        per.setDireccionParticular(par[1]);
                        break;
                    case LABORAL:
                        per.setDireccionLaboral(par[1]);
                        break;
                    default:
                        tv = new TipoValorDTO();
                        tv.setIdPersona(String.valueOf(per.getId()));
                        tv.setTipo(par[0]);
                        tv.setValor(par[1]);
                        direccionesARegistrar.add(tv);
                        break;
                }
    			
    			i++;
    			temp = campos[i];
    		}
    		//PROCESO LOS DATOS POR CATEGORIAS
    		i++;
    		per.setCategoriaId(Long.valueOf(campos[i]));
    		i++;
    		per.setCategoriaNombre(campos[i]);
    		i++;
    		per.setCategoriaNombreRelativo(campos[i]);
    		i++;
    		temp = campos[i];
    		if(!temp.equals(CAMPO_NULO)){
    			per.setDatoExtra(temp);
    		}
    		i++;
    		temp = campos[i];
    		if(!temp.equals(CAMPO_NULO)){
    			per.setDescripcion(temp);
    		}
    		resultado.add(per);
    		    		
    	}
    	return resultado;
    	
    }

    private static List<CategoriaDTO> procesarCategorias(ArrayList<String> lista){
    	String linea;
    	List<CategoriaDTO> resultado = new ArrayList<>();
    	String[] campos;
    	if(lista != null){
        	Iterator<String> it = lista.iterator();
        	CategoriaDTO cat;
        	while(it.hasNext()){
        		linea = it.next();
        		campos = linea.split(PUNTO_COMA);
        		cat = new CategoriaDTO();
        		cat.setId(Long.valueOf(campos[1]));
        		cat.setNombreReal(campos[2]);
        		cat.setActiva(Long.valueOf(campos[3]));
        		cat.setTipoDatoExtra(campos[4]);
        		resultado.add(cat);
        	}   		
    	}

    	return resultado;
    }

    private static List<CategoriaDTO> procesarCategoriasProtegidas(ArrayList<String> lista){
    	String linea;
    	List<CategoriaDTO> resultado = new ArrayList<>();
    	String[] campos;
    	if(lista != null){
        	Iterator<String> it = lista.iterator();
        	CategoriaDTO cat;
        	while(it.hasNext()){
        		linea = it.next();
        		campos = linea.split(PUNTO_COMA);
        		cat = new CategoriaDTO();
        		cat.setId(Long.valueOf(campos[1]));
        		cat.setNombreReal(campos[2]);
        		resultado.add(cat);
        	}   		
    	}

    	return resultado;
    }
    
    private static List<CategoriaDTO> procesarCategoriasPersonales(ArrayList<String> lista){
    	List<CategoriaDTO> result = procesarCategorias(lista);
    	Iterator<CategoriaDTO> it = result.iterator();
    	CategoriaDTO cat;
    	while(it.hasNext()){
    		cat = it.next();
    		cat.setCategoriaPersonal(true);
    	}
    	return result;
    }
    


    private static List<Long> procesarPreferidos(ArrayList<String> lista){
    	String linea;
    	List<Long> resultado = new ArrayList<>();
    	String[] campos;
    	if(lista != null){
        	Iterator<String> it = lista.iterator();
        	Long id;
        	while(it.hasNext()){
        		linea = it.next();
        		campos = linea.split(PUNTO_COMA);
        		id = Long.valueOf(campos[1]);
        		resultado.add(id);
        	}    		
    	}

    	return resultado;
    }
    
    private static ContraseniaDTO procesarContrasenia(ArrayList<String> lista){
    	String linea;
    	ContraseniaDTO pass = new ContraseniaDTO();
    	String[] campos;
    	if(lista != null){
        	Iterator<String> it = lista.iterator();
        	Long id;
        	if(it.hasNext()){
        		linea = it.next();
        		campos = linea.split(PUNTO_COMA);
        		id = Long.valueOf(campos[1]);
        		pass.setId(id);
        		pass.setContrasenia(campos[2]);

         	}    		
    	}

    	return pass;
    }
    
    private static String obtenerContenidoArchivo(File file, Activity context)throws IOException{
        // ACA DEBERIA CARGAR EL CONTENIDO DEL ARCHIVO PASADO COMO PARAMETRO, HACER LOS CONTROLES DE LECTURA
    	String line;
    	Asociacion canStore = comprobarSDCard(context);
    	boolean boolValue = (Boolean)canStore.getKey();
    	String msg = (String) canStore.getValue();
    	StringBuilder result = new StringBuilder();
    	if(boolValue){
    		BufferedReader input =  new BufferedReader(new FileReader(file));
    		line = input.readLine();
    		while(line != null){
    			if(!line.equals("")){
    				result.append(line).append(ENTER);
    			}
    			
    			line = input.readLine();
    		}
    		
    	}else{
			  mensaje = msg;
    	}
    	
        return result.toString();

    }    
    
    public static void exportarCSV(Activity context, DataBaseManager mDBManager){
    	Asociacion canStore;
    	Boolean boolValue;
    	String msg;
    	String body;
        try
        {	
        	canStore = comprobarSDCard(context);
     		boolValue = (Boolean)canStore.getKey();
     		msg = (String) canStore.getValue();
     		if(boolValue){
     			body = obtenerCSVdeContactos(context, mDBManager);
     			almacenarArchivo(folderCSV, fileCSV , body);
     			mensaje = context.getString(R.string.mensaje_exito_exportar_csv);
     		}else{
    			mensaje = msg;
     		}
     		
	  	  }catch (Exception e) {
	  		  mensaje = context.getString(R.string.error_exportar_csv);
	  	  }    	
    }

    public static void exportarCSVEstetico(Activity context, String separador, List<CategoriaDTO> categoriasProtegidas, DataBaseManager mDBManager){
    	Asociacion canStore;
    	Boolean boolValue;
    	String msg;
    	String body;
        try
        {

			if (ContextCompat.checkSelfPermission(context,
					android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
					== PackageManager.PERMISSION_GRANTED) {

				// Should we show an explanation?
				if (!ActivityCompat.shouldShowRequestPermissionRationale(context,
						Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

					// Show an expanation to the user *asynchronously* -- don't block
					// this thread waiting for the user's response! After the user
					// sees the explanation, try again to request the permission.

					// No explanation needed, we can request the permission.

					ActivityCompat.requestPermissions(context,
							new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
							1);

					// MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
					// app-defined int constant. The callback method gets the
					// result of the request.
				}
			}

			canStore = comprobarSDCard(context);
     		boolValue = (Boolean)canStore.getKey();
     		msg = (String) canStore.getValue();
     		if(boolValue){
     			body = obtenerCSVdeContactosEstetico(context, separador, categoriasProtegidas, mDBManager);
				String fileEsteticoCSV = "kncontactsExcel.csv";
				almacenarArchivo(folderCSV, fileEsteticoCSV, body);
     			mensaje = context.getString(R.string.mensaje_exito_exportar_csv);
     		}else{
    			mensaje = msg;
     		}
     		
	  	  }catch (Exception e) {
	  		  mensaje = context.getString(R.string.error_exportar_csv);
	  	  }    	
    }
    
    
    private static void almacenarArchivo(String nombreDirectorio, String nombreArchivo, String body) throws IOException {
    	String path = obtenerPath(nombreDirectorio);

	    File dir = new File(path);
	    dir.mkdir();
      
	    File file = new File(dir.getPath(), nombreArchivo);
	    if(!file.exists()){
	    	file.createNewFile();
	    }
	    PrintWriter writer = new PrintWriter(file);
	    writer.append(body);
	    writer.flush();
	    writer.close();
    }
  
    private static String obtenerPath(String nombreDirectorio){
    	String path = Environment.getExternalStorageDirectory().toString();
    	return path + File.separator + nombreDirectorio;
    }

    
    private static String obtenerCSVdeContactosEstetico(Activity context, String separador, List<CategoriaDTO> categoriasProtegidas, DataBaseManager mDBManager){
    	StringBuilder result = new StringBuilder();
    	PersonaDTO per;
    	
    	// RECUPERO PERSONAS
    	List<PersonaDTO> personas = obtenerPersonas(context, categoriasProtegidas, mDBManager);
		for (PersonaDTO persona : personas) {
			per = persona;
			//	result = result + obtenerStringPersona(per, context);
			result.append(obtenerStringEsteticoPersona(per, context, separador, mDBManager));

		}
    	return result.toString();
    }
    
    private static String obtenerCSVdeContactos(Activity context, DataBaseManager mDBManager){
    	StringBuilder result = new StringBuilder();
    	PersonaDTO per;
    	CategoriaDTO cat;
    	
    	
    	// RECUPERO PERSONAS
    	List<PersonaDTO> personas = obtenerPersonas(context, mDBManager);
        for (PersonaDTO persona : personas) {
            per = persona;
            result.append(obtenerStringPersona(per, context, mDBManager));
        }
    	
    	
    	// RECUPERO CATEGORIAS FIJAS
    	List<CategoriaDTO> categorias = obtenerCategorias(context, mDBManager);
    	Iterator<CategoriaDTO> itCat = categorias.iterator();
    	while(itCat.hasNext()){
    		cat = itCat.next();
    		result.append(obtenerStringCategoria(cat, HEAD_CATEGORIA));
    	}
    	
    	
    	// RECUPERO CATEGORIAS PERSONALES
    	categorias = obtenerCategoriasPersonales(context, mDBManager);
    	itCat = categorias.iterator();
    	while(itCat.hasNext()){
    		cat = itCat.next();
    		result.append(obtenerStringCategoria(cat, HEAD_CATEGORIA_PERSONAL));
    	}
    	
    	
    	// RECUPERO PREFERIDOS
    	Long idPref;
    	List<Long> preferidos = obtenerPreferidos(context, mDBManager);
        for (Long preferido : preferidos) {
            idPref = preferido;
            result.append(HEAD_PREFERIDO).append(PUNTO_COMA).append(idPref).append(ENTER);
        }
    	
    	
    	// RECUPERO CATEGORIAS PROTEGIDAS
    //	categorias = obtenerCategoriasProtegidas(context);
    	categorias = categoriasProtegidas;
    	itCat = categorias.iterator();
    	while(itCat.hasNext()){
    		cat = itCat.next();
    		result.append(obtenerStringCategoriaProtegida(cat, HEAD_CATEGORIA_PROTEGIDA));
    	}
    	result.append(obtenerStringContrasenia());
    	
    	return result.toString();
    	
    }
    
    private static String obtenerStringCategoria(CategoriaDTO cat, String header){
    	String result;
    	result = header + PUNTO_COMA + cat.getId() + PUNTO_COMA + cat.getNombreReal() + PUNTO_COMA + cat.getActiva() + PUNTO_COMA + cat.getTipoDatoExtra() + ENTER;
    	return result;
    }
    
    private static String obtenerStringContrasenia(){
    	String result;
    	result = HEAD_CONTRASENIA + PUNTO_COMA + contrasenia.getId() + PUNTO_COMA + contrasenia.getContrasenia() + ENTER;
    	return result;
    }
    
    
    private static String obtenerStringCategoriaProtegida(CategoriaDTO cat, String header){
    	String result;
    	result = header + PUNTO_COMA + cat.getId() + PUNTO_COMA + cat.getNombreReal() + ENTER;
    	return result;
    }
    
    
    
    private static String obtenerStringPersona(PersonaDTO per, Activity context, DataBaseManager mDBManager){
    	StringBuilder result = new StringBuilder();
    	List<TipoValorDTO> masTVs;
    	TipoValorDTO tv;
    	Iterator<TipoValorDTO> it;
    	//result = result + SEPARACION_PERSONA + ENTER;
 
    	// DATOS PERSONALES
    	
    	result.append(HEAD_PERSONA).append(PUNTO_COMA).append(per.getId()).append(PUNTO_COMA).append(per.getApellido()).append(PUNTO_COMA);
    	if(per.getNombres() != null && !per.getNombres().equals("")){
    		result.append(per.getNombres()).append(PUNTO_COMA);
    	}else{
    		result.append(CAMPO_NULO).append(PUNTO_COMA);
    	}
    	if(per.getFechaNacimiento() != null){
    		result.append(per.getFechaNacimiento()).append(PUNTO_COMA);
    	}else{
    		result.append(CAMPO_NULO).append(PUNTO_COMA);
    	}
    	
    	// TELEFONOS

		String INICIO = "#I#";
		result.append(INICIO).append(PUNTO_COMA);
    	if(per.getTelParticular() != null){
    		result.append(PARTICULAR).append(SEPARACION_ATRIBUTO).append(per.getTelParticular()).append(PUNTO_COMA);
    	}
    	if(per.getCelular() != null){
    		result.append(MOVIL).append(SEPARACION_ATRIBUTO).append(per.getCelular()).append(PUNTO_COMA);
    	}
    	if(per.getTelLaboral() != null){
    		result.append(LABORAL).append(SEPARACION_ATRIBUTO).append(per.getTelLaboral()).append(PUNTO_COMA);
    	}
    	
    
    	masTVs = obtenerTelefonosIdPersona(context, per.getId(), mDBManager);
    	it = masTVs.iterator();
    	while(it.hasNext()){
    		tv = it.next();
    		result.append(tv.getTipo()).append(SEPARACION_ATRIBUTO).append(tv.getValor()).append(PUNTO_COMA);
    	}

    	result.append(FIN).append(PUNTO_COMA);
    	// MAILS
    	
    	result.append(INICIO).append(PUNTO_COMA);
    	if(per.getEmailParticular() != null){
    		result.append(PARTICULAR).append(SEPARACION_ATRIBUTO).append(per.getEmailParticular()).append(PUNTO_COMA);
    	}
    	if(per.getEmailLaboral() != null){
    		result.append(LABORAL).append(SEPARACION_ATRIBUTO).append(per.getEmailLaboral()).append(PUNTO_COMA);
    	}
    	if(per.getEmailOtro() != null){
    		result.append(OTRO).append(SEPARACION_ATRIBUTO).append(per.getEmailOtro()).append(PUNTO_COMA);
    	}
    	
    	masTVs = obtenerEmailsIdPersona(context, per.getId(), mDBManager);
    	it = masTVs.iterator();
    	while(it.hasNext()){
    		tv = it.next();
    		result.append(tv.getTipo()).append(SEPARACION_ATRIBUTO).append(tv.getValor()).append(PUNTO_COMA);
    	}
    	
    	result.append(FIN).append(PUNTO_COMA);
    	
    	// DIRECCIONES
    	
    	result.append(INICIO).append(PUNTO_COMA);
    	if(per.getDireccionParticular() != null){
    		result.append(PARTICULAR).append(SEPARACION_ATRIBUTO).append(per.getDireccionParticular()).append(PUNTO_COMA);
    	}
    	if(per.getDireccionLaboral() != null){
    		result.append(LABORAL).append(SEPARACION_ATRIBUTO).append(per.getDireccionLaboral()).append(PUNTO_COMA);
    	}
    	
    	masTVs = obtenerDireccionesIdPersona(context, per.getId(), mDBManager);
    	it = masTVs.iterator();
    	while(it.hasNext()){
    		tv = it.next();
    		result.append(tv.getTipo()).append(SEPARACION_ATRIBUTO).append(tv.getValor()).append(PUNTO_COMA);
    	}
    	
    	result.append(FIN).append(PUNTO_COMA);
    	
    	// DATOS POR CATEGORIA

    	result.append(per.getCategoriaId()).append(PUNTO_COMA);
    	result.append(per.getCategoriaNombre()).append(PUNTO_COMA);
    	result.append(per.getCategoriaNombreRelativo()).append(PUNTO_COMA);
    	if(per.getDatoExtra()!= null && !per.getDatoExtra().equals("")){
    		result.append(per.getDatoExtra()).append(PUNTO_COMA);
    	}else{
    		result.append(CAMPO_NULO).append(PUNTO_COMA);
    	}
    	if(per.getDescripcion() != null && !per.getDescripcion().equals("")){
    		result.append(per.getDescripcion());
    	}else{
    		result.append(CAMPO_NULO);
    	}

    	result.append(ENTER);
    	
    	
    	return result.toString();
    	
    }

    private static String obtenerStringEsteticoPersona(PersonaDTO per, Activity context, String separador, DataBaseManager mDBManager){
    	StringBuilder result = new StringBuilder();
    	List<TipoValorDTO> masTVs;
    	TipoValorDTO tv;
    	Iterator<TipoValorDTO> it;
    	//result = result + SEPARACION_PERSONA + ENTER;
 
    	// DATOS PERSONALES
    	
    	result.append(per.getApellido()).append(separador);
    	if(per.getNombres() != null && !per.getNombres().equals("")){
    		result.append(per.getNombres()).append(separador);
    	}else{
    		result.append(separador);
    	}
    	if(per.getFechaNacimiento() != null){
    		result.append(context.getString(R.string.label_fechaNacimiento)).append(SEPARACION_ATRIBUTO).append(per.getFechaNacimiento()).append(separador);
    	}else{
    		result.append(context.getString(R.string.label_fechaNacimiento)).append(SEPARACION_ATRIBUTO).append(separador);
    	}
    	
    	// DATOS POR CATEGORIA
    	String datosCategoria = per.getCategoriaNombreRelativo();
    	
    	if(per.getDatoExtra()!= null && !per.getDatoExtra().equals("")){
    		datosCategoria = datosCategoria + "-" + per.getDatoExtra();	
    	}
    	if(per.getDescripcion() != null && !per.getDescripcion().equals("")){
    		datosCategoria = datosCategoria + "-" + per.getDescripcion();
    	}
    	result.append(datosCategoria).append(separador);
    	
    	// TELEFONOS

		String PIPE = " | ";
		if(per.getTelParticular() != null){
    		result.append(context.getString(R.string.label_telefono)).append("-").append(context.getString(R.string.hint_particular)).append(SEPARACION_ATRIBUTO).append(per.getTelParticular()).append(PIPE);
    	}
    	if(per.getCelular() != null){
    		result.append(context.getString(R.string.label_telefono)).append("-").append(context.getString(R.string.hint_telMovil)).append(SEPARACION_ATRIBUTO).append(per.getCelular()).append(PIPE);
    	}
    	if(per.getTelLaboral() != null){
    		result.append(context.getString(R.string.label_telefono)).append("-").append(context.getString(R.string.hint_laboral)).append(SEPARACION_ATRIBUTO).append(per.getTelLaboral()).append(PIPE);
    	}
    	
    
    	masTVs = obtenerTelefonosIdPersona(context, per.getId(), mDBManager);
    	it = masTVs.iterator();
    	while(it.hasNext()){
    		tv = it.next();
    		result.append(context.getString(R.string.label_telefono)).append("-").append(tv.getTipo()).append(SEPARACION_ATRIBUTO).append(tv.getValor()).append(PIPE);
    	}
    	
    	result.append(separador);

    	// MAILS

    	if(per.getEmailParticular() != null){
    		result.append(context.getString(R.string.label_email)).append("-").append(context.getString(R.string.hint_particular)).append(SEPARACION_ATRIBUTO).append(per.getEmailParticular()).append(PIPE);
    	}
    	if(per.getEmailLaboral() != null){
    		result.append(context.getString(R.string.label_email)).append("-").append(context.getString(R.string.hint_laboral)).append(SEPARACION_ATRIBUTO).append(per.getEmailLaboral()).append(PIPE);
    	}
    	if(per.getEmailOtro() != null){
    		result.append(context.getString(R.string.label_email)).append("-").append(context.getString(R.string.hint_otro)).append(SEPARACION_ATRIBUTO).append(per.getEmailOtro()).append(PIPE);
    	}
    	
    	masTVs = obtenerEmailsIdPersona(context, per.getId(), mDBManager);
    	it = masTVs.iterator();
    	while(it.hasNext()){
    		tv = it.next();
    		result.append(context.getString(R.string.label_email)).append("-").append(tv.getTipo()).append(SEPARACION_ATRIBUTO).append(tv.getValor()).append(PIPE);
    	}
    	
    	result.append(separador);
    	
    	// DIRECCIONES
    	
    	if(per.getDireccionParticular() != null){
    		result.append(context.getString(R.string.label_direccion)).append("-").append(context.getString(R.string.hint_particular)).append(SEPARACION_ATRIBUTO).append(per.getDireccionParticular()).append(PIPE);
    	}
    	if(per.getDireccionLaboral() != null){
    		result.append(context.getString(R.string.label_direccion)).append("-").append(context.getString(R.string.hint_laboral)).append(SEPARACION_ATRIBUTO).append(per.getDireccionLaboral()).append(PIPE);
    	}
    	
    	masTVs = obtenerDireccionesIdPersona(context, per.getId(), mDBManager);
    	it = masTVs.iterator();
    	while(it.hasNext()){
    		tv = it.next();
    		result.append(context.getString(R.string.label_direccion)).append("-").append(tv.getTipo()).append(SEPARACION_ATRIBUTO).append(tv.getValor()).append(PIPE);
    	}
    	
    	result.append(separador);

    	result.append(ENTER);
    	
    	
    	return result.toString();
    	
    }


    private static String obtenerStringEsteticoPersonaParaEnviar(PersonaDTO per, Activity context, String separador, DataBaseManager mDBManager){
    	StringBuilder result = new StringBuilder();
    	List<TipoValorDTO> masTVs;
    	TipoValorDTO tv;
    	Iterator<TipoValorDTO> it;
    	//result = result + SEPARACION_PERSONA + ENTER;
 
    	// DATOS PERSONALES
    	
    	result.append("- ").append(per.getApellido()).append(" ");
    	if(per.getNombres() != null && !per.getNombres().equals("")){
    		result.append(per.getNombres()).append(separador);
    	}
		result.append(separador);
    	// TELEFONOS
    	
    	result = new StringBuilder(separador + result + context.getResources().getString(R.string.label_telefonos) + separador);
    	
    	result.append(separador);
    	
    	if(per.getTelParticular() != null){
    		result.append("* ").append(context.getString(R.string.label_telefono)).append("-").append(context.getString(R.string.hint_particular)).append(SEPARACION_ATRIBUTO).append(per.getTelParticular()).append(separador);
    	}
    	if(per.getCelular() != null){
    		result.append("* ").append(context.getString(R.string.label_telefono)).append("-").append(context.getString(R.string.hint_telMovil)).append(SEPARACION_ATRIBUTO).append(per.getCelular()).append(separador);
    	}
    	if(per.getTelLaboral() != null){
    		result.append("* ").append(context.getString(R.string.label_telefono)).append("-").append(context.getString(R.string.hint_laboral)).append(SEPARACION_ATRIBUTO).append(per.getTelLaboral()).append(separador);
    	}
    	
    
    	masTVs = obtenerTelefonosIdPersona(context, per.getId(), mDBManager);
    	it = masTVs.iterator();
    	while(it.hasNext()){
    		tv = it.next();
    		result.append("* ").append(context.getString(R.string.label_telefono)).append("-").append(tv.getTipo()).append(SEPARACION_ATRIBUTO).append(tv.getValor()).append(separador);
    	}

    	result.append(separador);
    	// MAILS
    	result = new StringBuilder(separador + result + context.getResources().getString(R.string.label_mail) + separador);

    	result.append(separador);
    	if(per.getEmailParticular() != null){
    		result.append("* ").append(context.getString(R.string.label_email)).append("-").append(context.getString(R.string.hint_particular)).append(SEPARACION_ATRIBUTO).append(per.getEmailParticular()).append(separador);
    	}
    	if(per.getEmailLaboral() != null){
    		result.append("* ").append(context.getString(R.string.label_email)).append("-").append(context.getString(R.string.hint_laboral)).append(SEPARACION_ATRIBUTO).append(per.getEmailLaboral()).append(separador);
    	}
    	if(per.getEmailOtro() != null){
    		result.append("* ").append(context.getString(R.string.label_email)).append("-").append(context.getString(R.string.hint_otro)).append(SEPARACION_ATRIBUTO).append(per.getEmailOtro()).append(separador);
    	}
    	
    	masTVs = obtenerEmailsIdPersona(context, per.getId(),mDBManager);
    	it = masTVs.iterator();
    	while(it.hasNext()){
    		tv = it.next();
    		result.append("* ").append(context.getString(R.string.label_email)).append("-").append(tv.getTipo()).append(SEPARACION_ATRIBUTO).append(tv.getValor()).append(separador);
    	}
    	
    	result.append(separador);
    	// DIRECCIONES

    	result = new StringBuilder(separador + result + context.getResources().getString(R.string.label_direcciones) + separador);

    	result.append(separador);
    	if(per.getDireccionParticular() != null){
    		result.append("* ").append(context.getString(R.string.label_direccion)).append("-").append(context.getString(R.string.hint_particular)).append(SEPARACION_ATRIBUTO).append(per.getDireccionParticular()).append(separador);
    	}
    	if(per.getDireccionLaboral() != null){
    		result.append("* ").append(context.getString(R.string.label_direccion)).append("-").append(context.getString(R.string.hint_laboral)).append(SEPARACION_ATRIBUTO).append(per.getDireccionLaboral()).append(separador);
    	}
    	
    	masTVs = obtenerDireccionesIdPersona(context, per.getId(), mDBManager);
    	it = masTVs.iterator();
    	while(it.hasNext()){
    		tv = it.next();
    		result.append("* ").append(context.getString(R.string.label_direccion)).append("-").append(tv.getTipo()).append(SEPARACION_ATRIBUTO).append(tv.getValor()).append(separador);
    	}
    	
    	result.append(separador);

    	result.append(ENTER);
    	
    	
    	return result.toString();
    	
    }


    
    
    private static List<PersonaDTO> obtenerPersonas(Activity context, List<CategoriaDTO> categoriasProtegidas, DataBaseManager mDBManager){
    	PersonaDTO per;
    	Cursor cursor = null;
	//	CursorLoader cursorLoader = null;
    	List<PersonaDTO> result = new ArrayList<>();
    	inicializarBD(mDBManager);
   // 	Cursor cur = mDBManager.fetchAllPersonas(categoriasProtegidas);
   // 	context.startManagingCursor(cur);


		CursorLoader cursorLoader = ConstantsAdmin.cursorPersonas;
		if(cursorLoader == null){
			cursorLoader = mDBManager.cursorLoaderPersonas(categoriasProtegidas, context);

		}
		cursor = cursorLoader.loadInBackground();

     //   CursorLoader cursorLoader = mDBManager.cursorLoaderPersonas(categoriasProtegidas, context);
    //    cursorLoader.startLoading();
     //   Cursor cur = cursorLoader.loadInBackground();
		cursor.moveToFirst();
    	while(!cursor.isAfterLast()){
           per = cursorToPersonaDto(cursor);
           result.add(per);
			cursor.moveToNext();
    	}
    //	cur.close();
   // 	context.stopManagingCursor(cur);
    	finalizarBD(mDBManager);
    	return result;
    }
    
    private static List<PersonaDTO> obtenerPersonas(Activity context, DataBaseManager mDBManager){
    	PersonaDTO per;
		CursorLoader cursorLoader = null;
		Cursor cur = null;
    	List<PersonaDTO> result = new ArrayList<>();
    	inicializarBD(mDBManager);
		cursorLoader = ConstantsAdmin.cursorPersonas;
		if(cursorLoader == null){
			cursorLoader = mDBManager.cursorLoaderPersonas(categoriasProtegidas, context);

		}
		cur = cursorLoader.loadInBackground();
    //	cursorLoader = mDBManager.cursorLoaderPersonas(categoriasProtegidas, context);
	//	Cursor cur = cursorLoader.loadInBackground();
		cur.moveToFirst();
		while(!cur.isAfterLast()){
			per = cursorToPersonaDto(cur);
			result.add(per);
			cur.moveToNext();
		}
		cur.close();

    /*	Cursor cur = mDBManager.fetchAllPersonas(categoriasProtegidas);
    	context.startManagingCursor(cur);
    	cur.moveToFirst();
    	while(!cur.isAfterLast()){
           per = cursorToPersonaDto(cur);
           result.add(per);
           cur.moveToNext();
    	}
    	cur.close();
    	context.stopManagingCursor(cur);*/
    	finalizarBD(mDBManager);
    	return result;
    }
    
    private static List<CategoriaDTO> obtenerCategorias(Activity context, DataBaseManager mDBManager){
    	CategoriaDTO cat;
    	List<CategoriaDTO> result = new ArrayList<>();
    	inicializarBD(mDBManager);
    	Cursor cur = mDBManager.fetchAllCategoriasPorNombre(null);
    	context.startManagingCursor(cur);
    	cur.moveToFirst();
    	while(!cur.isAfterLast()){
           cat = cursorToCategoriaDto(cur);
           result.add(cat);
           cur.moveToNext();
    	}
    	cur.close();
    	context.stopManagingCursor(cur);
    	finalizarBD(mDBManager);
    	return result;
    }    

    private static List<CategoriaDTO> obtenerCategoriasPersonales(Activity context, DataBaseManager mDBManager){
    	CategoriaDTO cat;
    	List<CategoriaDTO> result = new ArrayList<>();
    	inicializarBD(mDBManager);
    	Cursor cur = mDBManager.fetchAllCategoriasPersonalesPorNombre(null);
    	context.startManagingCursor(cur);
    	cur.moveToFirst();
    	while(!cur.isAfterLast()){
           cat = cursorToCategoriaDto(cur);
           result.add(cat);
           cur.moveToNext();
    	}
    	cur.close();
    	context.stopManagingCursor(cur);
    	finalizarBD(mDBManager);
    	return result;
    }     
    

    
    private static List<Long> obtenerPreferidos(Activity context, DataBaseManager mDBManager){
    	Long id;
    	List<Long> result = new ArrayList<>();
    	inicializarBD(mDBManager);
    	Cursor cur = mDBManager.fetchAllPreferidos();
    	context.startManagingCursor(cur);
    	cur.moveToFirst();
    	while(!cur.isAfterLast()){
           id = cursorToPreferido(cur);
           result.add(id);
           cur.moveToNext();
    	}
    	cur.close();
    	context.stopManagingCursor(cur);
    	finalizarBD(mDBManager);
    	return result;
    }     
        
    
    private static final String ENTER = "\n";
    public static final String PUNTO_COMA = ";";
    public static final String COMA = ",";
	private static final String FIN = "#F#";
    private static final String CAMPO_NULO = "###";
    private static final String PARTICULAR = "PARTICULAR";
    private static final String LABORAL = "LABORAL";
    private static final String OTRO = "OTRO";
    private static final String MOVIL = "MOVIL";
    private static final String SEPARACION_ATRIBUTO = ":";
    private static final String HEAD_PERSONA = "$$PE";
    private static final String HEAD_CATEGORIA = "$$C";
    private static final String HEAD_CATEGORIA_PERSONAL = "$$CP";
    private static final String HEAD_PREFERIDO = "$$PR";
    public static final int ACTIVITY_EJECUTAR_EXPORTAR_CONTACTOS = 19;
    public static final int ACTIVITY_EJECUTAR_IMPORTAR_CONTACTOS_CSV = 20;
	public static final int ACTIVITY_EJECUTAR_EXPORTAR_CONTACTOS_ESTETICO = 21;
    
    public static void mostrarMensajeDialog(Activity context, String message){
    	AlertDialog.Builder builder = new AlertDialog.Builder(context);
    	builder.setMessage(message)
    	       .setCancelable(true)
    	       .setPositiveButton(R.string.label_ok, new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	        	    dialog.toString();       				
    	           }
    	       });
    	builder.show(); 
    }
    
    private static File obtenerFileCSV(Activity context){
        // LEVANTAR TODOS LOS FILES QUE ESTAN EN LA CARPETA KN-CSVfiles
    	  File backup = null;
    	  boolean boolValue;
    	  Asociacion canStore;
    	  canStore = comprobarSDCard(context);
   		  boolValue = (Boolean)canStore.getKey();
   		  String msg = (String)canStore.getValue();
   		  if(boolValue){
   		 	  String path = obtenerPath(folderCSV);
   		      backup = new File(path + File.separator + fileCSV);
   		  }else{
  			  mensaje = msg;
  		  }
   		  return backup;
    }    
        
    public static void eliminarCategoriaPersonal(CategoriaDTO cat, DataBaseManager mDBManager){
		inicializarBD(mDBManager);
		mDBManager.eliminarCategoriaPersonal(cat.getId());
		finalizarBD(mDBManager);
    }
    
    
    public static void crearCategoriaPersonal(CategoriaDTO cat, String oldNameCat, boolean forImport, DataBaseManager mDBManager){
		inicializarBD(mDBManager);
		mDBManager.crearCategoriaPersonal(cat, oldNameCat, forImport);
		finalizarBD(mDBManager);
    }
    
    public static long crearPersona(PersonaDTO personaSeleccionada, boolean forImport, DataBaseManager mDBManager){
    	long id;
    	inicializarBD(mDBManager);
    	id = mDBManager.createPersona(personaSeleccionada, forImport);
    	finalizarBD(mDBManager);
    	return id;
    }
    
    public static void eliminarTelefono(TipoValorDTO tv, DataBaseManager mDBManager){
        inicializarBD(mDBManager);
    	mDBManager.eliminarTelefono(tv.getId());
    	finalizarBD(mDBManager);
    }
    
    public static void eliminarEmail(TipoValorDTO tv, DataBaseManager mDBManager){
        inicializarBD(mDBManager);
    	mDBManager.eliminarEmail(tv.getId());
    	finalizarBD(mDBManager);
    }

    public static void eliminarDireccion(TipoValorDTO tv, DataBaseManager mDBManager){
        inicializarBD(mDBManager);
    	mDBManager.eliminarDireccion(tv.getId());
    	finalizarBD(mDBManager);
    }
    
    public static void crearTelefono(TipoValorDTO mTipoValor, DataBaseManager mDBManager){
		inicializarBD(mDBManager);
		mDBManager.createTipoValor(mTipoValor, ConstantsAdmin.TABLA_TELEFONOS);
		finalizarBD(mDBManager);
    }
    
    public static void crearEmail(TipoValorDTO mTipoValor, DataBaseManager mDBManager){
		inicializarBD(mDBManager);
		mDBManager.createTipoValor(mTipoValor, ConstantsAdmin.TABLA_EMAILS);
		finalizarBD(mDBManager);
    }
    
    public static void crearDireccion(TipoValorDTO mTipoValor, DataBaseManager mDBManager){
		inicializarBD(mDBManager);
		mDBManager.createTipoValor(mTipoValor, ConstantsAdmin.TABLA_DIRECCIONES);
		finalizarBD(mDBManager);
    }

    public static List<CategoriaDTO> obtenerCategoriasActivas(Activity context, String nombre, DataBaseManager mDBManager){
    	Cursor cursor;
    	List<CategoriaDTO> categorias = new ArrayList<>();
	    inicializarBD(mDBManager);
	    cursor = mDBManager.fetchCategoriasActivasPorNombre(nombre);
	    if(cursor != null){
	        context.startManagingCursor(cursor);
	        categorias = categoriasCursorToDtos(cursor);
	        cursor.close();
	        context.stopManagingCursor(cursor);
	    }
	    finalizarBD(mDBManager);
	    return categorias;
    }
    
    public static List<CategoriaDTO> obtenerCategoriasActivasPersonales(Activity context, DataBaseManager mDBManager){
    	Cursor cursor;
    	List<CategoriaDTO> categorias = new ArrayList<>();
	    inicializarBD(mDBManager);
	    cursor = mDBManager.fetchCategoriasPersonalesActivasPorNombre(null);
	    if(cursor != null){
	        context.startManagingCursor(cursor);
	        categorias = ConstantsAdmin.categoriasCursorToDtos(cursor);
	        cursor.close();
	        context.stopManagingCursor(cursor);
	    }

	    finalizarBD(mDBManager);
	    return categorias;
    }
    
    public static List<CategoriaDTO> obtenerCategorias(Activity context, String nombre, DataBaseManager mDBManager){
    	Cursor cursor;
    	List<CategoriaDTO> categorias = new ArrayList<>();
	    inicializarBD(mDBManager);
	    cursor = mDBManager.fetchAllCategoriasPorNombre(nombre);
	    if(cursor != null){
	        context.startManagingCursor(cursor);
	        categorias = categoriasCursorToDtos(cursor);
	        cursor.close();
	        context.stopManagingCursor(cursor);
	    }
	    finalizarBD(mDBManager);
	    return categorias;
    }
    
    public static void actualizarCategoria(CategoriaDTO catSelected, DataBaseManager mDBManager){
 		inicializarBD(mDBManager);
		mDBManager.actualizarCategoria(catSelected);
		finalizarBD(mDBManager);
    }
    
    public static void actualizarConfig(DataBaseManager mDBManager){
 		inicializarBD(mDBManager);
		mDBManager.actualizarConfig(config);
		finalizarBD(mDBManager);
    }
        
    
    public static void eliminarPreferido(long idPer, DataBaseManager mDBManager){
		inicializarBD(mDBManager);
		mDBManager.eliminarPreferido(idPer);
		finalizarBD(mDBManager);
    }
    
    public static void crearPreferido(long idPer, DataBaseManager mDBManager){
		inicializarBD(mDBManager);
		mDBManager.crearPreferido(idPer);
		finalizarBD(mDBManager);
    }
    
    public static void eliminarPersona(long perId, DataBaseManager mDBManager){
		inicializarBD(mDBManager);
		mDBManager.eliminarPersona(perId);
		finalizarBD(mDBManager);
    }
    
    
    // CAMBIOS PARA AGREGAR CONTRASENIA EN LAS CATEGORIAS
    
    // TABLA: Contrasenia
    
    
    public static final String TABLA_CONTRASENIA = "contrasenia";
    public static final String KEY_CONTRASENIA = "contrasenia";
    public static final String KEY_CONTRASENIA_ACTIVA = "contraseniaActiva";
    public static final String KEY_MAIL1 = "mail1";
    public static final String KEY_MAIL2 = "mail2";
    
    
// TABLA: Categoria protegida
    
    
    public static final String TABLA_CATEGORIA_PROTEGIDA = "categoriaProtegida";
    
    
    public static void crearCategoriaProtegida(CategoriaDTO cat, DataBaseManager mDBManager){
		inicializarBD(mDBManager);
		mDBManager.crearCategoriaProtegida(cat);
		finalizarBD(mDBManager);
    }
    
    
    public static long crearContrasenia(ContraseniaDTO contrasenia, DataBaseManager mDBManager){
    	long id;
		inicializarBD(mDBManager);
		id = mDBManager.crearContrasenia(contrasenia);
		finalizarBD(mDBManager);
		return id;
    }
    
    public static void almacenarContraseniaEnArchivo(Activity context){
    	Asociacion canStore;
    	Boolean boolValue;
    	String msg = null;
    	String body;
	
    	canStore = comprobarSDCard(context);
    	body = contrasenia.getContrasenia();
 		boolValue = (Boolean)canStore.getKey();
 		if(boolValue){
 			try {
				String filePassword = "kncontacts.txt";
				almacenarArchivo(folderCSV, filePassword, body);
			} catch (Exception ignored) {
				
			}
 			msg = context.getString(R.string.mensaje_exito_almacenar_password);
 		}
 		if(msg != null){
 	 		mostrarMensajeDialog(context, msg);
 		}

   	
    }
    
    public static void eliminarCategoriaProtegida(CategoriaDTO cat, DataBaseManager mDBManager){
		inicializarBD(mDBManager);
		mDBManager.eliminarCategoriaProtegida(cat.getNombreReal());
		finalizarBD(mDBManager);
    }
    
    
    private static List<CategoriaDTO> obtenerCategoriasProtegidas(Activity context, DataBaseManager mDBManager){
    	CursorLoader cursorLoader = null;
    	List<CategoriaDTO> categorias = new ArrayList<>();
		cursorLoader = mDBManager.cursorLoaderCategoriasProtegidas(null, context);
		//		cursor = mDBManager.fetchCategoriasActivasPorNombre(null);
		if(cursorLoader != null){
			//	startManagingCursor(cursor);
			categorias = ConstantsAdmin.categoriasProtegidasCursorToDtos(cursorLoader.loadInBackground());
		}


	/*
	    cursor = mDBManager.fetchAllCategoriasProtegidasPorNombre(null);
	    if(cursor != null){
	        context.startManagingCursor(cursor);
	        categorias = ConstantsAdmin.categoriasProtegidasCursorToDtos(cursor);
	        cursor.close();
	        context.stopManagingCursor(cursor);
	    }


	    */
	    return categorias;
    }
    
    private static ContraseniaDTO obtenerContrasenia(Activity context, DataBaseManager mDBManager){
    	CursorLoader cursorLoader;
    	ContraseniaDTO contrasenia = null;

	//	ConstantsAdmin.inicializarBD( mDBManager);
		cursorLoader = mDBManager.cursorLoaderContrasenia(context);
		if(cursorLoader != null){
			//	startManagingCursor(cursor);
			contrasenia = ConstantsAdmin.contraseniaCursorToDtos(cursorLoader.loadInBackground());
		}
	//	ConstantsAdmin.finalizarBD(mDBManager);




		/*    cursor = mDBManager.fetchContrasenia();
	    if(cursor != null){
	        context.startManagingCursor(cursor);
	        contrasenia = ConstantsAdmin.contraseniaCursorToDtos(cursor);
	        cursor.close();
	        context.stopManagingCursor(cursor);
	    }
*/
	    return contrasenia;
    }
    
    
    public static ConfigDTO obtenerConfiguracion(Activity context, DataBaseManager mDBManager){
    	Cursor cursor;
    	ConfigDTO config = null;
    	inicializarBD(mDBManager);
	    cursor = mDBManager.fetchConfig();
	    if(cursor != null){
	    	cursor.moveToFirst();
	    	if(!cursor.isAfterLast()){
		        context.startManagingCursor(cursor);
		        config = ConstantsAdmin.configCursorToDtos(cursor);
		        cursor.close();
		        context.stopManagingCursor(cursor);
	    	}else{
		    	config = new ConfigDTO();
		    	config.setEstanDetallados(false);
		    	config.setListaExpandida(false);
		    	config.setMuestraPreferidos(false);
		    	config.setOrdenadoPorCategoria(true);
		    }

	    }
	    finalizarBD(mDBManager);
	    return config;
    }
    
	public static boolean estaProtegidaCategoria(String nombreCategoria){
		boolean result = false;
		CategoriaDTO cat;
		if(categoriasProtegidas != null){
			Iterator<CategoriaDTO> it = categoriasProtegidas.iterator();
			while(it.hasNext() && !result){
				cat = it.next();
				result = cat.getNombreReal().equals(nombreCategoria);
				
			}
		}
		return result;
		
	}
    
    
    private static ContraseniaDTO contraseniaCursorToDtos(Cursor cursor){
    	ContraseniaDTO contrasenia = null; 
    	
    	String pass;
    	long passId;
    	int activa;
    	String mail;
    	
        cursor.moveToFirst();
        if(!cursor.isAfterLast()){
        	pass = cursor.getString(cursor.getColumnIndex(ConstantsAdmin.KEY_CONTRASENIA));
        	mail = cursor.getString(cursor.getColumnIndex(ConstantsAdmin.KEY_MAIL1));
        	passId = cursor.getLong(cursor.getColumnIndex(ConstantsAdmin.KEY_ROWID));
        	activa = cursor.getInt(cursor.getColumnIndex(ConstantsAdmin.KEY_CONTRASENIA_ACTIVA));
        	contrasenia = new ContraseniaDTO();
        	if(activa == 1){
        		contrasenia.setActiva(true);	
        	}else{
        		contrasenia.setActiva(false);
        	}
        	
        	contrasenia.setContrasenia(pass);
        	contrasenia.setId(passId);
        	contrasenia.setMail(mail);
        }
        
        return contrasenia;

    }  
    
    private static ConfigDTO configCursorToDtos(Cursor cursor){
    	ConfigDTO config = null; 
    	int id;
    	int estanDetallados;
    	int organizadosPorCateg;
    	int estanExpandidos;
    	int muestraPreferidos;
        	
        cursor.moveToFirst();
        if(!cursor.isAfterLast()){
        	estanDetallados = cursor.getInt(cursor.getColumnIndex(ConstantsAdmin.KEY_ESTAN_DETALLADOS));
        	organizadosPorCateg = cursor.getInt(cursor.getColumnIndex(ConstantsAdmin.KEY_ORDENADO_POR_CATEGORIA));
        	estanExpandidos = cursor.getInt(cursor.getColumnIndex(ConstantsAdmin.KEY_LISTA_EXPANDIDA));
        	muestraPreferidos = cursor.getInt(cursor.getColumnIndex(ConstantsAdmin.KEY_MUESTRA_PREFERIDOS));
        	id = cursor.getInt(cursor.getColumnIndex(ConstantsAdmin.KEY_ROWID));
        	config = new ConfigDTO();
        	if(estanDetallados == 1){
        		config.setEstanDetallados(true);	
        	}
        	if(organizadosPorCateg == 1){
        		config.setOrdenadoPorCategoria(true);	
        	}
        	if(estanExpandidos == 1){
        		config.setListaExpandida(true);	
        	}
        	if(muestraPreferidos == 1){
        		config.setMuestraPreferidos(true);	
        	}
        	config.setId(id);
        }
        
        return config;

    }  

    
    
    public static List<CategoriaDTO> categoriasProtegidas = null;
    
    public static ContraseniaDTO contrasenia = null;
    public static ConfigDTO config = null;
   
    public static void cargarCategoriasProtegidas(Activity context, DataBaseManager mDBManager){
    	categoriasProtegidas = obtenerCategoriasProtegidas(context, mDBManager);
    }
    
    public static void cargarContrasenia(Activity context, DataBaseManager mDBManager){
    	contrasenia = obtenerContrasenia(context, mDBManager);
    	if(contrasenia == null){
    		contrasenia = new ContraseniaDTO();
    		
    	}
    	
    }
    
    public static void actualizarContrasenia(ContraseniaDTO pass, DataBaseManager mDBManager){
    	inicializarBD(mDBManager);
    	mDBManager.crearContrasenia(pass);
    	finalizarBD(mDBManager);
    }
    
    public static final int ACTIVITY_EJECUTAR_PROTECCION_CATEGORIA = 22;
    public static final int ACTIVITY_EJECUTAR_ACTIVAR_CONTRASENIA = 23;
    // --Commented out by Inspection (12/11/2018 12:44):public static final int ACTIVITY_EJECUTAR_ACTIVAR_CONTRASENIA_DESDE_SPINNER = 24;
    
    private static final String HEAD_CATEGORIA_PROTEGIDA = "$$PP";
    
    private static final String HEAD_CONTRASENIA = "$$PS";

	public static final String imageFolder = "Pictures";
	public static boolean cerrarMainActivity = false;
    
    public static final int ACTIVITY_EJECUTAR_SACAR_PHOTO = 25;

    
    public static String obtenerPathImagen(){
    	String result;
    	result = obtenerPath(folderCSV) + File.separator + imageFolder + File.separator;
    	return result;
    }
    
    public static void almacenarImagen(Activity context, String nombreDirectorio, String nombreArchivo, Bitmap bm) throws IOException {
  	  	  String path = obtenerPath(nombreDirectorio);
	      OutputStream fOut;
	      File dir = new File(path);
	      dir.mkdirs();
	      
	      File file = new File(dir.getPath(), nombreArchivo);
	      if(!file.exists()){
	    	  file.createNewFile();
	      }
	      fOut = new FileOutputStream(file);
	      bm.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
	 //     bm.compress(Bitmap.CompressFormat.JPEG, 90, fOut);
	      fOut.flush();
	      fOut.close();

	      MediaStore.Images.Media.insertImage(context.getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
	  }


	public static void enviarMailContraseniaCategoriasProtegidas(Activity activity) {
		// TODO Auto-generated method stub
		   
		String emailaddress = contrasenia.getMail();
		String body = activity.getResources().getString(R.string.app_name) + ":" + contrasenia.getContrasenia();
		String subject = activity.getResources().getString(R.string.app_name);

		enviarMailGenerico(activity, emailaddress, body, subject);
	}


	public static void enviarMailGenerico(Activity activity, String emailaddress, String body, String subject) {
		// TODO Auto-generated method stub
		   
		String[] address = {emailaddress};
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND); //This is the email intent
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, address); // adds the address to the intent
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);//the subject
		emailIntent.setType("plain/text");
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, body); //adds the body of the mail
		 
		activity.startActivity(emailIntent);	
	}


	public static String recuperarInfoContacto(Activity activity, long id, DataBaseManager mDBManager) {
		// TODO Auto-generated method stub
		
		String result;
		PersonaDTO per = obtenerPersonaId(activity, id, mDBManager);
		result = obtenerStringEsteticoPersonaParaEnviar(per, activity, ENTER, mDBManager);
		return result;
	}

	public static final int ACTIVITY_EJECUTAR_MENU_PERSONA = 26;

}
