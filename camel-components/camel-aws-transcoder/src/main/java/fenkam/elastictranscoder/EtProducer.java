package fenkam.elastictranscoder;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;

import com.amazonaws.services.elastictranscoder.model.CreateJobOutput;
import com.amazonaws.services.elastictranscoder.model.CreateJobRequest;
import com.amazonaws.services.elastictranscoder.model.CreateJobResult;
import com.amazonaws.services.elastictranscoder.model.CreatePipelineRequest;
import com.amazonaws.services.elastictranscoder.model.CreatePipelineResult;
import com.amazonaws.services.elastictranscoder.model.JobInput;
import com.amazonaws.services.elastictranscoder.model.Notifications;
import com.amazonaws.services.elastictranscoder.model.Pipeline;
import com.amazonaws.services.elastictranscoder.model.PipelineOutputConfig;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.SubscribeResult;


/**
 * The Et producer.
 */
public class EtProducer extends DefaultProducer {
    public EtProducer(EtEndpoint endpoint) {
        super(endpoint);
    }


    @Override
    public EtEndpoint getEndpoint() {
        return (EtEndpoint) super.getEndpoint();
    }
    
    public void process(Exchange exchange) throws Exception {
        System.out.println("\n\n\n\n\n\n===========================\n\n\n\n\n"+exchange.getIn().getBody());    
        System.out.println("\n\n\n\n\n\n===========================\n\n\n\n\n"+exchange.getIn().getHeaders());
        EtConfiguration config=getEndpoint().getConfiguration();
        transcode(config,exchange);
    }

    private String getValue(String name, Exchange exchange){
    	return exchange.getIn().getHeader(name, String.class);
    }
	public String transcode(EtConfiguration config,Exchange exchange) throws InterruptedException, ExecutionException {
		System.out.println("Starting transcoding process");
		checkForAndCreateBucket(config.getVideoOutputBucket()!=null?config.getVideoOutputBucket():getValue(Constants.CamelAwsEtVideoOutputBucket, exchange));
		checkForAndCreateBucket(config.getThumbnailOutputBucket()!=null?config.getThumbnailOutputBucket():getValue(Constants.CamelAwsEtThumbnailOutputBucket, exchange));
		System.out.println("Buckets created");
		Pipeline pipeline=checkForAndCreatePipeline(config,exchange);
		System.out.println("Pipeline created:"+pipeline.getId());

		CreateJobRequest request=new CreateJobRequest();
		request.setPipelineId(pipeline.getId());
		
		JobInput input= new JobInput();
		input.setKey(config.getInputKey()!=null?config.getInputKey():getValue(Constants.CamelAwsEtInputKey, exchange));
		request.setInput(input);
		
		CreateJobOutput output= new CreateJobOutput();
		output.setKey(config.getOutputKey()!=null?config.getOutputKey():getValue(Constants.CamelAwsEtOutputKey, exchange));
		output.setPresetId(config.getPresetId()!=null?config.getPresetId():getValue(Constants.CamelAwsEtPresetId, exchange));
		output.setThumbnailPattern(config.getThumbnailPattern()!=null?config.getThumbnailPattern():getValue(Constants.CamelAwsEtThumbnailPattern, exchange));
		request.setOutput(output);
		request.setOutputKeyPrefix(config.getOutputKeyPrefix()!=null?config.getOutputKeyPrefix():getValue(Constants.CamelAwsEtOutputKeyPrefix, exchange));
		System.out.println("Submitting job to transcoder");

		
		Future<CreateJobResult>future=getEndpoint().getTranscoderClient().createJobAsync(request);
		CreateJobResult result=future.get();
		if(result!=null && result.getJob()!=null){
			System.out.print("JOb submitted with Id:"+result.getJob().getId());
			return result.getJob().getId();
		}
		throw new UnknownError();
	}


	

	private Pipeline checkForAndCreatePipeline(EtConfiguration config, Exchange exchange) throws InterruptedException, ExecutionException {
		List<Pipeline> pipelines=getEndpoint().getTranscoderClient().listPipelines().getPipelines();
		String pipelineName=config.getPipelineName()!=null?config.getPipelineName():getValue(Constants.CamelAwsEtPipelineName, exchange);
		for(Pipeline pipeline:pipelines){
			if(pipeline.getName().equals(pipelineName)){
				return pipeline;
			}
		}
		CreatePipelineRequest request= new CreatePipelineRequest();
		request.setName(pipelineName);
		
		String notificationEndpoint=config.getNotificationEndpoint()!=null?config.getNotificationEndpoint():getValue(Constants.CamelAwsEtNotificationEndpoint, exchange);
		String notificationProtocol=config.getNotificationProtocol()!=null?config.getNotificationProtocol():getValue(Constants.CamelAwsEtNotificationProtocol, exchange);
		String completed=config.getSnsCompletedSubject()!=null?config.getSnsCompletedSubject():getValue(Constants.CamelAwsEtSnsCompletedSubject, exchange);
		String warning=config.getSnsWarningSubject()!=null?config.getSnsWarningSubject():getValue(Constants.CamelAwsEtSnsWarningSubject, exchange);
		String progressing=config.getSnsProgressingSubject()!=null?config.getSnsProgressingSubject():getValue(Constants.CamelAwsEtSnsProgressingSubject, exchange);
		String error=config.getSnsErrorSubject()!=null?config.getSnsErrorSubject():getValue(Constants.CamelAwsEtSnsErrorSubject, exchange);
		
		Notifications notifications= new Notifications();
		notifications.setCompleted(createTopicAndSubscribe(completed,notificationProtocol,notificationEndpoint));
		notifications.setWarning(createTopicAndSubscribe(warning,notificationProtocol,notificationEndpoint));
		notifications.setError(createTopicAndSubscribe(error,notificationProtocol,notificationEndpoint));
		notifications.setProgressing(createTopicAndSubscribe(progressing,notificationProtocol,notificationEndpoint));
		request.setNotifications(notifications);
		request.setRole(config.getRoleARN()!=null?config.getRoleARN():getValue(Constants.CamelAwsEtRoleARN, exchange));
		
		request.setInputBucket(config.getInputBucket());

		PipelineOutputConfig contentConfig = new PipelineOutputConfig();
		contentConfig.setBucket(config.getVideoOutputBucket()!=null?config.getVideoOutputBucket():getValue(Constants.CamelAwsEtVideoOutputBucket, exchange));
		request.setContentConfig(contentConfig);
		
		PipelineOutputConfig thumbnailConfig = new PipelineOutputConfig();
		thumbnailConfig.setBucket(config.getThumbnailOutputBucket()!=null?config.getThumbnailOutputBucket():getValue(Constants.CamelAwsEtThumbnailOutputBucket, exchange));
		
		request.setThumbnailConfig(thumbnailConfig);
		
		Future<CreatePipelineResult> future=getEndpoint().getTranscoderClient().createPipelineAsync(request);
		CreatePipelineResult result=future.get();
		if(result.getPipeline()!=null){
			return result.getPipeline();
		}
		return null;
	}

	public void checkForAndCreateBucket(String bucketName) {
			String lowerBucketName=bucketName.toLowerCase();
			if (!getEndpoint().getS3Client().doesBucketExist(lowerBucketName)) {
				getEndpoint().getS3Client().createBucket(lowerBucketName);
			}
	}

	public  CreateTopicResult createTopic (String topicName) {
		CreateTopicRequest request = new CreateTopicRequest(topicName);
		CreateTopicResult result = getEndpoint().getSnsClient().createTopic(request);
		return result;
	}

	public  String createTopicAndSubscribe (String topicName, String protocol, String endpoint) {
		CreateTopicRequest request = new CreateTopicRequest(topicName);
		CreateTopicResult result = getEndpoint().getSnsClient().createTopic(request);
		subscribe(result.getTopicArn(), protocol, endpoint);
		return result.getTopicArn();
	}

	/**
	 * Subscribe a given commenter to future comments posted to the given entry.
	 *
	 * @param entry the entry to subscribe the commenter to
	 * @param commenter the commenter to be subscribed
	 * @return the result returned by AWS
	 */
	public  SubscribeResult subscribe (String arn, String protocol, String endpoint) {
		SubscribeRequest request = new SubscribeRequest(arn,protocol,endpoint);
		SubscribeResult result = getEndpoint().getSnsClient().subscribe(request);
		return result;
	}

}
