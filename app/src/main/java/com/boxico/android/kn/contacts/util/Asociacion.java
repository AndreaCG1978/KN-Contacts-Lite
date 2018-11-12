package com.boxico.android.kn.contacts.util;

public class Asociacion {
	
	private Object key = null;
	private Object value = null;
	public Object getKey() {
		return key;
	}
	public void setKey(Object key) {
		this.key = key;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public Asociacion(Object key, Object value) {
		super();
		this.key = key;
		this.value = value;
	}
	
	

}
