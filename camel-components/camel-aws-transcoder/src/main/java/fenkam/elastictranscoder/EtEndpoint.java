package fenkam.elastictranscoder;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.elastictranscoder.AmazonElasticTranscoderAsyncClient;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.sns.AmazonSNSClient;


/**
 * Represents a Et endpoint.
 */
public class EtEndpoint extends DefaultEndpoint {
	private EtConfiguration configuration;

    public EtEndpoint(String uri, EtComponent component) {
        super(uri, component);
    }


    public Producer createProducer() throws Exception {
        return new EtProducer(this);
    }

    
    public EtConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(EtConfiguration configuration) {
		this.configuration = configuration;
	}

	public EtEndpoint(String uri, Component component, EtConfiguration configuration) {
        super(uri, component);
        this.configuration = configuration;
    }

    public Consumer createConsumer(Processor processor) throws Exception {
        throw new UnsupportedOperationException("No consumer for transcoder service");
    }

    public boolean isSingleton() {
        return true;
    }

    public AmazonSNSClient getSnsClient() {
        return configuration.getAmazonSnsClient() != null
                ? configuration.getAmazonSnsClient()
                : createSnsClient();
    }

    private AmazonSNSClient createSnsClient() {
        AWSCredentials credentials = new BasicAWSCredentials(configuration.getAccessKey(), configuration.getSecretKey());
        AmazonSNSClient client = new AmazonSNSClient(credentials);
        configuration.setAmazonSnsClient(client);
        return client;
    }


    public AmazonS3Client getS3Client() {
        return configuration.getAmazonS3Client() != null
                ? configuration.getAmazonS3Client()
                : createS3Client();
    }

    private AmazonS3Client createS3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(configuration.getAccessKey(), configuration.getSecretKey());
        AmazonS3Client client = new AmazonS3Client(credentials);
        configuration.setAmazonS3Client(client);
        return client;
    }


    public AmazonElasticTranscoderAsyncClient getTranscoderClient() {
        return configuration.getAmazonTranscoderClient() != null
                ? configuration.getAmazonTranscoderClient()
                : createAmazonTranscoderClient();
    }

    private AmazonElasticTranscoderAsyncClient createAmazonTranscoderClient() {
        AWSCredentials credentials = new BasicAWSCredentials(configuration.getAccessKey(), configuration.getSecretKey());
        AmazonElasticTranscoderAsyncClient client = new AmazonElasticTranscoderAsyncClient(credentials);
        configuration.setAmazonTranscoderClient(client);
        return client;
    }

}
