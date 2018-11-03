package com.boxico.android.kn.contacts.persistencia.dtos;

public class CategoriaDTO implements Comparable{
	
	@Override
	public String toString() {
		String valor = this.nombreRelativo;
		return valor;
	}

	private long id = 0;
	private String nombreReal = null;
	private String nombreRelativo = null;
	private String tipoDatoExtra = null;
	private boolean categoriaPersonal = false;
	private boolean protegida = false;

	public boolean isProtegida() {
		return protegida;
	}

	public void setProtegida(boolean protegida) {
		this.protegida = protegida;
	}

	public boolean isCategoriaPersonal() {
		return categoriaPersonal;
	}

	public void setCategoriaPersonal(boolean categoriaPersonal) {
		this.categoriaPersonal = categoriaPersonal;
	}

	public String getTipoDatoExtra() {
		return tipoDatoExtra;
	}

	public void setTipoDatoExtra(String tipoDatoExtra) {
		this.tipoDatoExtra = tipoDatoExtra;
	}

	private long activa = 0;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	
	public String getNombreReal() {
		return nombreReal;
	}

	public void setNombreReal(String nombreReal) {
		this.nombreReal = nombreReal;
	}

	public String getNombreRelativo() {
		return nombreRelativo;
	}

	public void setNombreRelativo(String nombreRelativo) {
		this.nombreRelativo = nombreRelativo;
	}

	public long getActiva() {
		return activa;
	}
	
	public void setActiva(long activa) {
		this.activa = activa;
	}
	
	public CategoriaDTO(long id, String nombreReal, String nombreRelativo, long activa, String tipoDatoExtra) {
		super();
		this.id = id;
		this.nombreReal = nombreReal;
		this.nombreRelativo = nombreRelativo;
		this.activa = activa;
		this.tipoDatoExtra = tipoDatoExtra;
	}



	@Override
	public int compareTo(Object another) {
		// TODO Auto-generated method stub
		int val = 0;
		CategoriaDTO cat = (CategoriaDTO) another;
		val = this.getNombreRelativo().compareTo(cat.getNombreRelativo());
		return val;
	}

	public CategoriaDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
