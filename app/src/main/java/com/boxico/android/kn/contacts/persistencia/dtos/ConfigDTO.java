package com.boxico.android.kn.contacts.persistencia.dtos;

public class ConfigDTO {
	long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	boolean ordenadoPorCategoria = false;
	boolean estanDetallados = false;
	boolean listaExpandida = false;
	boolean muestraPreferidos = false;
	public boolean isOrdenadoPorCategoria() {
		return ordenadoPorCategoria;
	}
	public void setOrdenadoPorCategoria(boolean ordenadoPorCategoria) {
		this.ordenadoPorCategoria = ordenadoPorCategoria;
	}
	public boolean isEstanDetallados() {
		return estanDetallados;
	}
	public void setEstanDetallados(boolean estanDetallados) {
		this.estanDetallados = estanDetallados;
	}
	public boolean isListaExpandida() {
		return listaExpandida;
	}
	public void setListaExpandida(boolean listaExpandida) {
		this.listaExpandida = listaExpandida;
	}
	public boolean isMuestraPreferidos() {
		return muestraPreferidos;
	}
	public void setMuestraPreferidos(boolean muestraPreferidos) {
		this.muestraPreferidos = muestraPreferidos;
	}
	
	

}
