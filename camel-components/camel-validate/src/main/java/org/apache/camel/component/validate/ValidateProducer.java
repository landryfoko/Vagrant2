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

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Pattern;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ValidationException;
import org.apache.camel.impl.DefaultProducer;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import lombok.extern.slf4j.Slf4j;

/**
 * A Producer which sends messages to the Amazon Web Service Simple Storage Service <a
 * href="http://aws.amazon.com/s3/">AWS S3</a>
 */
@Slf4j
public class ValidateProducer extends DefaultProducer {

	public ValidateProducer(final Endpoint endpoint) {
        super(endpoint);
    }

    public void process(final Exchange exchange) throws Exception {
        	ValidateConfiguration config= ((ValidateEndpoint) super.getEndpoint()).getConfiguration();
        	if(!config.isCondition()){
        		throw new ValidationException(exchange, config.getMessage());
        	}
    }

}