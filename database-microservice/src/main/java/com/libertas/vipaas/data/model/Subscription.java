package com.libertas.vipaas.data.model;

public class Subscription implements ExtensibleBean {
	private String id;
	private String extension;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getExtension() {
		return extension;
	}
	public void setExtension(String extension) {
		this.extension = extension;
	}
	

}
