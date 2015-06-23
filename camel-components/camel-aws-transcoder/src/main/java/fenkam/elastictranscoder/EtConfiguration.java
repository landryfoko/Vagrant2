package fenkam.elastictranscoder;

import com.amazonaws.services.elastictranscoder.AmazonElasticTranscoderAsyncClient;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.sns.AmazonSNSClient;

public class EtConfiguration {

	private String inputBucket;
	private String pipelineName;
	private String snsErrorSubject;
	private String snsWarningSubject;
	private String snsProgressingSubject;
	private String snsCompletedSubject;
	private String accessKey;
	private String secretKey;	
	private String roleARN;
	private String presetId;
	private String notificationEndpoint;
	private String notificationProtocol;
	private String thumbnailOutputBucket;
	private String videoOutputBucket;
	private boolean reducedRedundancy;
	private String outputKeyPrefix;
	private String inputKey;
	private String outputKey;
	private String thumbnailPattern;
	
	private AmazonSNSClient amazonSnsClient;
	private AmazonS3Client amazonS3Client;
	private AmazonElasticTranscoderAsyncClient amazonTranscoderClient;
	
	
	
	public String getOutputKeyPrefix() {
		return outputKeyPrefix;
	}
	public void setOutputKeyPrefix(String outputKeyPrefix) {
		this.outputKeyPrefix = outputKeyPrefix;
	}
	public String getInputKey() {
		return inputKey;
	}
	public void setInputKey(String inputKey) {
		this.inputKey = inputKey;
	}
	public String getOutputKey() {
		return outputKey;
	}
	public void setOutputKey(String outputKey) {
		this.outputKey = outputKey;
	}
	public String getThumbnailPattern() {
		return thumbnailPattern;
	}
	public void setThumbnailPattern(String thumbnailPattern) {
		this.thumbnailPattern = thumbnailPattern;
	}
	public AmazonElasticTranscoderAsyncClient getAmazonTranscoderClient() {
		return amazonTranscoderClient;
	}
	public void setAmazonTranscoderClient(AmazonElasticTranscoderAsyncClient amazonTranscoderClient) {
		this.amazonTranscoderClient = amazonTranscoderClient;
	}
	public boolean isReducedRedundancy() {
		return reducedRedundancy;
	}
	public void setReducedRedundancy(boolean reducedRedundancy) {
		this.reducedRedundancy = reducedRedundancy;
	}
	public AmazonSNSClient getAmazonSnsClient() {
		return amazonSnsClient;
	}
	public void setAmazonSnsClient(AmazonSNSClient amazonSnsClient) {
		this.amazonSnsClient = amazonSnsClient;
	}
	
	public AmazonS3Client getAmazonS3Client() {
		return amazonS3Client;
	}
	public void setAmazonS3Client(AmazonS3Client amazonS3Client) {
		this.amazonS3Client = amazonS3Client;
	}
	public String getAccessKey() {
		return accessKey;
	}
	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}
	public String getSecretKey() {
		return secretKey;
	}
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	public String getSnsErrorSubject() {
		return snsErrorSubject;
	}
	public void setSnsErrorSubject(String snsErrorSubject) {
		this.snsErrorSubject = snsErrorSubject;
	}
	public String getSnsWarningSubject() {
		return snsWarningSubject;
	}
	public void setSnsWarningSubject(String snsWarningSubject) {
		this.snsWarningSubject = snsWarningSubject;
	}
	public String getSnsProgressingSubject() {
		return snsProgressingSubject;
	}
	public void setSnsProgressingSubject(String snsProgressingSubject) {
		this.snsProgressingSubject = snsProgressingSubject;
	}
	public String getSnsCompletedSubject() {
		return snsCompletedSubject;
	}
	public void setSnsCompletedSubject(String snsCompletedSubject) {
		this.snsCompletedSubject = snsCompletedSubject;
	}
	
	public String getThumbnailOutputBucket() {
		return thumbnailOutputBucket;
	}
	public void setThumbnailOutputBucket(String thumbnailOutputBucket) {
		this.thumbnailOutputBucket = thumbnailOutputBucket;
	}
	public String getVideoOutputBucket() {
		return videoOutputBucket;
	}
	public void setVideoOutputBucket(String videoOutputBucket) {
		this.videoOutputBucket = videoOutputBucket;
	}
	
	public String getNotificationEndpoint() {
		return notificationEndpoint;
	}
	public void setNotificationEndpoint(String notificationEndpoint) {
		this.notificationEndpoint = notificationEndpoint;
	}
	public String getNotificationProtocol() {
		return notificationProtocol;
	}
	public void setNotificationProtcol(String notificationProtocol) {
		this.notificationProtocol = notificationProtocol;
	}
	
	public String getRoleARN() {
		return roleARN;
	}
	public void setRoleARN(String roleARN) {
		this.roleARN = roleARN;
	}
	public String getPresetId() {
		return presetId;
	}
	public void setPresetId(String presetId) {
		this.presetId = presetId;
	}
	public String getInputBucket() {
		return inputBucket;
	}
	public void setInputBucket(String inputBucket) {
		this.inputBucket = inputBucket;
	}
	public String getPipelineName() {
		return pipelineName;
	}
	public void setPipelineName(String pipelineName) {
		this.pipelineName = pipelineName;
	}
	

}
