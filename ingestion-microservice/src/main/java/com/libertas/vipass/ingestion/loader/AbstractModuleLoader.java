package com.libertas.vipass.ingestion.loader;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.camel.CamelContext;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.libertas.vipass.ingestion.wacthers.Watcher;

@Slf4j
public abstract class AbstractModuleLoader implements  PropertyChangeListener, ApplicationContextAware{

	protected Map<String,List<CamelContext>> camelContexts;
	protected String moduleRootFolder;
	protected ApplicationContext applicationContext;
	protected Watcher watcher;
	protected String activeModulesPattern;


	public String getActiveModulesPattern() {
		return activeModulesPattern;
	}
	public void setActiveModulesPattern(String activeModulesPattern) {
		this.activeModulesPattern = activeModulesPattern;
	}
	public Watcher getWatcher() {
		return watcher;
	}
	public void setWatcher(Watcher watcher) {
		this.watcher = watcher;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	public String getModuleRootFolder() {
		return moduleRootFolder;
	}
	public void setModuleRootFolder(String rootFolder) {
		moduleRootFolder = sanitizePath(rootFolder);
		moduleRootFolder=moduleRootFolder.endsWith("/")?moduleRootFolder.substring(0, moduleRootFolder.length()-1):moduleRootFolder;
	}

	protected String sanitizePath(String path){
		return path.trim().replace("\\", "/").replace("//", "/");
	}

	protected void loadModule(String workflowId, String folder, Properties custom){
		if(folder.startsWith(".")){
			log.info("Skipping folder {} as it starts with dot.",folder);
			return;
		}
		if(!Pattern.matches(getActiveModulesPattern(), folder)){
			log.info("Skipping module {} as it does not match regex {}",folder,getActiveModulesPattern());
			return;
		}
		camelContexts=new HashMap<String,List<CamelContext>>();
		log.info("\n\n\n------------Loading Module: {}------------------ with additional properties {} ",folder,custom);
		FileSystemXmlApplicationContext context = loadApplicationContext(folder,  custom, getApplicationContext());
		camelContexts.put(workflowId,camelContexts.get(workflowId)==null?new ArrayList<CamelContext>():camelContexts.get(workflowId));
	    camelContexts.get(workflowId).addAll(context.getBeansOfType(CamelContext.class).values());
	}

	public  FileSystemXmlApplicationContext loadApplicationContext(final String folder,  final Properties custom, ApplicationContext parent){
		FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(new String[]{"file:"+folder},false,parent);
		context.refresh();
		return context;
	}
	public abstract void init() throws IOException;
	public abstract void propertyChange(PropertyChangeEvent evt);

}
