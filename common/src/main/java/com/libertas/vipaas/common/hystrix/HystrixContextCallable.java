package com.libertas.vipaas.common.hystrix;

import java.util.Map;
import java.util.concurrent.Callable;

import org.json.simple.JSONObject;
import org.slf4j.MDC;

import com.libertas.vipaas.common.servlet.CredentialsThreadLocal;

public class HystrixContextCallable<K> implements Callable {

private final Callable<K> actual;
private final Map parentMDC;
private final JSONObject parentCredentials;

public HystrixContextCallable(Callable<K> actual) {
    this.actual = actual;
    this.parentMDC = MDC.getCopyOfContextMap();
    this.parentCredentials=CredentialsThreadLocal.getCredentials();
}

@Override
public K call() throws Exception {
    Map childMDC = MDC.getCopyOfContextMap();
    try {
    	if(parentMDC!=null){
    		MDC.setContextMap(parentMDC);
    	}
    //	System.out.println("Transferring tenant Id:"+parentTenantId);
        CredentialsThreadLocal.setCredentials(parentCredentials);
        
      //  System.out.println("Tenant Id now set to:"+CredentialsThreadLocal.getId());
        return actual.call();
    } finally {
    	if(childMDC!=null){
    		MDC.setContextMap(childMDC);
    	}
    }
}

}