package com.libertas.vipass.ingestion.loader;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.StandardWatchEventKinds;

import org.apache.camel.CamelContext;

import lombok.extern.slf4j.Slf4j;



@Slf4j
public class StaticModuleLoader extends AbstractModuleLoader  {


	public void init() {
		new Thread(){
			public void run() {
				log.info("Module loader started with root location:{}",getModuleRootFolder());
				File folder= new File(getModuleRootFolder());
				while(!folder.exists()){
					log.info("Folder {} does not exist. Waiting.",folder);
					try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {
					}
				}
				File [] moduleFolders=folder.listFiles();
				for(File f:moduleFolders){
					try{
						String sanitizedFolder=sanitizePath(f.getAbsolutePath());
						loadModule(sanitizedFolder,sanitizedFolder,null);
					}catch(Throwable t){
						log.error("Skiping module. Could not load "+f.getAbsolutePath(), t);
					}
				}
				new Thread(watcher).start();
			};
		}.start();
	}

	public  void propertyChange(PropertyChangeEvent evt) {
		String folder=sanitizePath(evt.getNewValue().toString());
		if(folder.startsWith(".")){
			log.info("Skipping folder {} as it starts with dot.",folder);
			return;
		}
		log.info("Change event receiuved for  {}. Reloading it.",folder);
		if(!folder.startsWith(getModuleRootFolder())){
			log.info("Skipping event {}. It does not start with module folder:{}",folder, getModuleRootFolder());
			return;
		}
		String moduleFolder=folder.substring(getModuleRootFolder().length()+1);
		moduleFolder=moduleFolder.indexOf("/")>0?moduleFolder.substring(0, moduleFolder.indexOf("/")):moduleFolder;

		log.info("Module folder relative path found:{}",moduleFolder);
		moduleFolder=getModuleRootFolder()+"/"+moduleFolder.substring(0,Math.max(moduleFolder.indexOf("/"),moduleFolder.length()));
		log.info("Undeploying module {} from {}",moduleFolder,camelContexts);
		if(camelContexts.get(moduleFolder)!=null){
			try {
				for(CamelContext ctx:camelContexts.get(moduleFolder)){
					ctx.stop();
				}
				log.info("Module {} undeployed",moduleFolder);
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}
			camelContexts.remove(moduleFolder);
		}
		if(evt.getPropertyName().equalsIgnoreCase(StandardWatchEventKinds.ENTRY_DELETE.toString())){
			log.info("Stopping processing of the event");
			return;
		}
		try{
			String sanitizedFolder=sanitizePath(moduleFolder);
			loadModule(sanitizedFolder,sanitizedFolder,null);
		}catch(Throwable t){
			log.error(t.getMessage(),t);
		}
	}
}
