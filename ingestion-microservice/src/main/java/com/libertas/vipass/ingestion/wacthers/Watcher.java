package com.libertas.vipass.ingestion.wacthers;

import java.beans.PropertyChangeListener;
import java.util.List;

public interface Watcher extends Runnable {
	
	public void setPropertyChangeListeners(List<PropertyChangeListener> propertyChangeListeners);
	public void setModuleRootDitrectory(String moduleRootDirectory);
}
