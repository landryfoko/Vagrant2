/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.restlet;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Method;
import org.restlet.data.Status;

import lombok.extern.slf4j.Slf4j;

/**
 * A simple router that routes requests to target Restlets based on method.
 *
 * @version
 */
@Slf4j
class MethodBasedRouter extends Restlet {

    private String uriPattern;

    private Map<Method, Restlet> routes = new ConcurrentHashMap<Method, Restlet>();
    private AtomicBoolean hasBeenAttachedFlag = new AtomicBoolean(false);

    MethodBasedRouter(String uriPattern) {
        this.uriPattern = uriPattern;
    }

    @Override
    public void handle(Request request, Response response) {
        Method method = request.getMethod();
        log.debug("MethodRouter ({}) received request method: {}", uriPattern, method);

        Restlet target = routes.get(method);
        if (target != null) {
            target.handle(request, response);
        } else {
            log.debug("No route for request method: {}", method);
            response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
        }
    }

    void addRoute(Method method, Restlet target) {
        routes.put(method, target);
    }

    void removeRoute(Method method) {
        routes.remove(method);
    }

    boolean hasRoutes() {
        return !routes.isEmpty();
    }

    /**
     * This method does "test-and-set" on the underlying flag that indicates
     * whether this router restlet has been attached to a server or not.  It
     * is the caller's responsibility to perform the "attach" when this method
     * returns false.
     *
     * @return true only this method is called the first time.
     */
    boolean hasBeenAttached() {
        return hasBeenAttachedFlag.getAndSet(true);
    }

    String getUriPattern() {
        return uriPattern;
    }
}
