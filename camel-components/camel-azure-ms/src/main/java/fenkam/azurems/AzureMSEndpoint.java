package fenkam.azurems;

import org.apache.camel.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.Synchronization;

import com.microsoft.windowsazure.services.core.Configuration;
import com.microsoft.windowsazure.services.media.MediaConfiguration;
import com.microsoft.windowsazure.services.media.MediaContract;
import com.microsoft.windowsazure.services.media.MediaService;


/**
 * Represents a AzureMS endpoint.
 */
public class AzureMSEndpoint extends DefaultEndpoint {
	AzureMSConfiguration azureMSConfiguration;


    public Consumer createConsumer(Processor processor) throws Exception {
        throw new UnsupportedOperationException("No consumer for Azure MediaService");
    }

    public AzureMSEndpoint(String uri, AzureMSComponent azureMSComponent,	AzureMSConfiguration azureMSConfiguration) {
    	 super(uri, azureMSComponent);
         this.azureMSConfiguration = azureMSConfiguration;
	}

	public Producer createProducer() throws Exception {
        return new AzureMSProducer(this);
    }


    public boolean isSingleton() {
        return true;
    }

	public AzureMSConfiguration getAzureMSConfiguration() {
		return azureMSConfiguration;
	}

	public void setAzureMSConfiguration(AzureMSConfiguration azureMSConfiguration) {
		this.azureMSConfiguration = azureMSConfiguration;
	}
    
    public MediaContract getMediaService(){
    	if(getAzureMSConfiguration().getMediaService()==null){
    		MediaContract mediaService=null;
            String mediaServiceUri = getAzureMSConfiguration().getMediaServiceURI();
            String oAuthUri = getAzureMSConfiguration().getOAuthURI();
            String clientId =getAzureMSConfiguration().getClientId();  // Use your media service account name.
            String clientSecret = getAzureMSConfiguration().getClientSecret().replace(" ", "+"); // Use your media service access key. 
            String scope = getAzureMSConfiguration().getScope();
            // Specify the configuration values to use with the MediaContract object.
            Configuration configuration = MediaConfiguration
                    .configureWithOAuthAuthentication(mediaServiceUri, oAuthUri, clientId, clientSecret, scope);

            // Create the MediaContract object using the specified configuration.
            mediaService = MediaService.create(configuration);

    		getAzureMSConfiguration().setMediaService(mediaService);
    	}
    	return getAzureMSConfiguration().getMediaService();
    }
}
