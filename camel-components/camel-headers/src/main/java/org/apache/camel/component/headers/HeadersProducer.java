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
package org.apache.camel.component.headers;

import java.util.HashMap;
import java.util.Map;
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
public class HeadersProducer extends DefaultProducer {


	//private static Map<String,Map<String,Object>>headers= new HashMap<String,Map<String,Object>>();
	public HeadersProducer(final Endpoint endpoint) {
        super(endpoint);
    }

        @SuppressWarnings("unchecked")
    public void process(final Exchange exchange) throws Exception {
        	String uuid=exchange.getExchangeId();
        	String operation=getEndpoint().getConfiguration().getOperation();
        	log.info("Executing operation {} on exchnage {}", operation,uuid);
        	if(operation.equalsIgnoreCase("restore")){
        		exchange.getIn().getHeaders().putAll((Map<String,Object>)exchange.getIn().getHeader("LibertasBackup"));
        		exchange.getIn().removeHeader("LibertasBackup");
        	}else if(operation.equalsIgnoreCase("backup")){
        		Map<String,Object> map= new HashMap<String,Object>();
        		map.putAll(exchange.getIn().getHeaders());
        		exchange.getIn().removeHeaders("*");
        		exchange.getIn().setHeader("LibertasBackup", map);
        		if(getConfiguration().getKeep()!=null){
        			String keeps[]=getConfiguration().getKeep().split(",");
        			for(String keep:keeps){
        				exchange.getIn().setHeader(keep, map.get(keep));
        			}
        		}
        	}else if(operation.equalsIgnoreCase("copy-to-headers")){
        		Map<String,Object> body=exchange.getIn().getBody(Map.class);
        		log.info("Copying variables from body to headers. Body:{}",body);
        		String pattern=getConfiguration().getPattern();
        		if(pattern==null || pattern.trim().equals("")){
        			throw new IllegalAccessException("Missing regex pattern in endpoint parameters");
        		}
        		for(Object key:body.keySet()){
        			if(Pattern.matches(pattern, key.toString())){
        				exchange.getIn().setHeader(key.toString(), body.get(key));
        			}
        		}
        	}else{
        		throw new UnsupportedOperationException(operation);
        	}
    }

    protected HeadersConfiguration getConfiguration() {
        return getEndpoint().getConfiguration();
    }

    @Override
    public HeadersEndpoint getEndpoint() {
        return (HeadersEndpoint) super.getEndpoint();
    }

}