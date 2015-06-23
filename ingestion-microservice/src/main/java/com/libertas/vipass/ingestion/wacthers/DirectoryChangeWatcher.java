package com.libertas.vipass.ingestion.wacthers;
/*
 * Copyright (c) 2008, 2010, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.nio.file.*;
import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.LinkOption.*;
import java.nio.file.attribute.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.*;


import lombok.extern.slf4j.Slf4j;

/**
 * Example to watch a directory (or tree) for changes to files.
 */

@Slf4j
public class DirectoryChangeWatcher implements Watcher{

    private WatchService watcher;
    private Map<WatchKey,Path> keys;
    private List<PropertyChangeListener> propertyChangeListeners;
    private Path root;

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }

    /**
     * Register the given directory with the WatchService
     */
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        keys.put(key, dir);
    }

    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void registerAll(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
       public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)  throws IOException         {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void reset()throws IOException{
    	watcher = FileSystems.getDefault().newWatchService();
        keys = new HashMap<WatchKey,Path>();
        registerAll(root);
    }

    /**
     * Process all events for keys queued to the watcher
     */
    public void run() {
    	while(true){
    		try {
				reset();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	for (;;) {
	            WatchKey key;
	            try {
	                key = watcher.take();
	            } catch (InterruptedException x) {
	                break;
	            }
	            Path dir = keys.get(key);
	            if (dir == null) {
	                System.err.println("WatchKey not recognized!!");
	                continue;
	            }
	            for (WatchEvent<?> event: key.pollEvents()) {
	                @SuppressWarnings("rawtypes")
					WatchEvent.Kind kind = event.kind();
	                if (kind == OVERFLOW) {
	                    continue;
	                }
	                // Context for directory entry event is the file name of entry
	                WatchEvent<Path> ev = cast(event);
	                Path name = ev.context();
	                Path child = dir.resolve(name);
	                if (kind == ENTRY_CREATE) {
	                    try {
	                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
	                            registerAll(child);
	                        }
	                    } catch (IOException x) {
	                    }
	                }
		        	for(PropertyChangeListener listener:propertyChangeListeners){
		        		log.info("Passing event {} to listener {}",event.kind().toString(),listener);
		        		PropertyChangeEvent evt= new PropertyChangeEvent(this, event.kind().toString(), keys.get(key)+"/"+event.context(), keys.get(key)+"/"+event.context());
		        		listener.propertyChange(evt);
		        	}
	            }
	            boolean valid = key.reset();
	            if (!valid) {
	                keys.remove(key);
	                if (keys.isEmpty()) {
	                    break;
	                }
	            }
	    	}
        }
    }

	public void setPropertyChangeListeners(List<PropertyChangeListener> propertyChangeListeners) {
		this.propertyChangeListeners=propertyChangeListeners;
	}

	public void setModuleRootDitrectory(String moduleRootDirectory) {
		this.root=Paths.get(moduleRootDirectory);
	}
}
