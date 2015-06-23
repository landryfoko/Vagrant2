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
package org.apache.camel.component.validate;


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
public class ValidateEndpoint extends DefaultEndpoint {


    private ValidateConfiguration configuration;

    @Deprecated
    public ValidateEndpoint(String uri, CamelContext context, ValidateConfiguration configuration) {
        super(uri, context);
        this.configuration = configuration;
    }
    public ValidateEndpoint(String uri, Component comp, ValidateConfiguration configuration) {
        super(uri, comp);
        this.configuration = configuration;
    }

    public Consumer createConsumer(Processor processor) throws Exception {
               throw new UnsupportedOperationException();
    }

    public Producer createProducer() throws Exception {
        return new ValidateProducer(this);
    }

    public boolean isSingleton() {
        return true;
    }
    public ValidateConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ValidateConfiguration configuration) {
        this.configuration = configuration;
    }
    
}