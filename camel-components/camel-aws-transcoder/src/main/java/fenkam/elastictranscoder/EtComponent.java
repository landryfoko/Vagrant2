package fenkam.elastictranscoder;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;



/**
 * Represents the component that manages {@link EtEndpoint}.
 */
public class EtComponent extends DefaultComponent {
    

    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        EtConfiguration configuration = new EtConfiguration();
        setProperties(configuration, parameters);

        if (remaining == null || remaining.trim().length() == 0) {
            throw new IllegalArgumentException("InputBucktName name must be specified.");
        }
        configuration.setInputBucket(remaining);
        if ((configuration.getAmazonS3Client() == null || configuration.getAmazonSnsClient()==null || configuration.getAmazonTranscoderClient()==null) && 
        		(configuration.getAccessKey() == null || configuration.getSecretKey() == null)) {
            throw new IllegalArgumentException("AmazonS3Client and AmazonSnsClient and AmazonElasticTranscoderAsyncClient  or accessKey and secretKey must be specified");
        }

        return new EtEndpoint(uri, this, configuration);
    }
}
