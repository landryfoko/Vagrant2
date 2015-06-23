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
package org.apache.camel.component.stack;


import org.apache.camel.CamelContext;
import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;

/**
 * Defines the <a href="http://camel.apache.org/aws.html">AWS S3 Endpoint</a>.  
 *
 */
public class StackEndpoint extends DefaultEndpoint {


    private StackConfiguration configuration;

    @Deprecated
    public StackEndpoint(String uri, CamelContext context, StackConfiguration configuration) {
        super(uri, context);
        this.configuration = configuration;
    }
    public StackEndpoint(String uri, Component comp, StackConfiguration configuration) {
        super(uri, comp);
        this.configuration = configuration;
    }

    public Consumer createConsumer(Processor processor) throws Exception {
               throw new UnsupportedOperationException();
    }

    public Producer createProducer() throws Exception {
        return new StackProducer(this);
    }

    public boolean isSingleton() {
        return true;
    }
    public StackConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(StackConfiguration configuration) {
        this.configuration = configuration;
    }
    
}