package fenkam.azurems;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;


/**
 * Represents the component that manages {@link AzureMSEndpoint}.
 */
public class AzureMSComponent extends DefaultComponent {

    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        AzureMSConfiguration configuration = new AzureMSConfiguration();
        setProperties(configuration, parameters);

        if (remaining == null || remaining.trim().length() == 0) {
            throw new IllegalArgumentException("Azure MediaService name must be specified.");
        }
        configuration.setMediaServiceURI(remaining);
        if (configuration.getMediaService() == null  && 
        		(configuration.getClientId()==null || configuration.getClientSecret() == null)) {
            throw new IllegalArgumentException("MediaSiervice Instance or ClientID and ClientSecret must be specified");
        }

        return new AzureMSEndpoint(uri, this, configuration);

    }
}
