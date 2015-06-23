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

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Pattern;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;

import lombok.extern.slf4j.Slf4j;

/**
 * A Producer which sends messages to the Amazon Web Service Simple Storage Service <a
 * href="http://aws.amazon.com/s3/">AWS S3</a>
 */
@Slf4j
public class StackProducer extends DefaultProducer {


	//private static Map<String,Map<String,Object>>headers= new HashMap<String,Map<String,Object>>();
	public StackProducer(final Endpoint endpoint) {
        super(endpoint);
    }

        @SuppressWarnings("unchecked")
    public void process(final Exchange exchange) throws Exception {
        	StackConfiguration config= ((StackEndpoint) super.getEndpoint()).getConfiguration();
        	Stack stack=(Stack)exchange.getProperty(config.getHeaderPrefix()+"Stack");
        	String op=config.getOperation().toLowerCase();
        	if(stack==null){
        		stack= new Stack();
        	}
        	if(op.equals("pop")){
        		exchange.getIn().setBody(stack.pop());
        	}else
        	if(op.equals("peek")){
        		exchange.getIn().setBody(stack.peek());
        	}else
        	if(op.equals("push")){
        		stack.push(exchange.getIn().getBody());
	        }else{
	        	throw new UnsupportedOperationException(op);
	        }
        	exchange.setProperty(config.getHeaderPrefix()+"Stack", stack);
    }

}