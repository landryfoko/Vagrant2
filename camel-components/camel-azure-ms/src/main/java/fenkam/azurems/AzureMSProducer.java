package fenkam.azurems;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

import com.microsoft.windowsazure.services.blob.client.CloudBlobClient;
import com.microsoft.windowsazure.services.blob.client.CloudBlockBlob;
import com.microsoft.windowsazure.services.blob.models.BlockList;
import com.microsoft.windowsazure.services.core.Configuration;
import com.microsoft.windowsazure.services.core.ServiceException;
import com.microsoft.windowsazure.services.core.storage.StorageException;
import com.microsoft.windowsazure.services.media.MediaConfiguration;
import com.microsoft.windowsazure.services.media.MediaContract;
import com.microsoft.windowsazure.services.media.MediaService;
import com.microsoft.windowsazure.services.media.WritableBlobContainerContract;
import com.microsoft.windowsazure.services.media.models.AccessPolicy;
import com.microsoft.windowsazure.services.media.models.AccessPolicyInfo;
import com.microsoft.windowsazure.services.media.models.AccessPolicyPermission;
import com.microsoft.windowsazure.services.media.models.Asset;
import com.microsoft.windowsazure.services.media.models.AssetFile;
import com.microsoft.windowsazure.services.media.models.AssetFileInfo;
import com.microsoft.windowsazure.services.media.models.AssetInfo;
import com.microsoft.windowsazure.services.media.models.Job;
import com.microsoft.windowsazure.services.media.models.Job.Creator;
import com.microsoft.windowsazure.services.media.models.ContentKey;
import com.microsoft.windowsazure.services.media.models.ContentKeyInfo;
import com.microsoft.windowsazure.services.media.models.ErrorDetail;
import com.microsoft.windowsazure.services.media.models.JobInfo;
import com.microsoft.windowsazure.services.media.models.JobState;
import com.microsoft.windowsazure.services.media.models.ListResult;
import com.microsoft.windowsazure.services.media.models.Locator;
import com.microsoft.windowsazure.services.media.models.LocatorInfo;
import com.microsoft.windowsazure.services.media.models.LocatorType;
import com.microsoft.windowsazure.services.media.models.MediaProcessor;
import com.microsoft.windowsazure.services.media.models.MediaProcessorInfo;
import com.microsoft.windowsazure.services.media.models.Task;
import com.microsoft.windowsazure.services.media.models.TaskInfo;
import com.microsoft.windowsazure.services.media.models.TaskOption;


/**
 * The AzureMS producer.
 */
@Slf4j
public class AzureMSProducer extends DefaultProducer {


    public AzureMSProducer(AzureMSEndpoint endpoint) {
        super(endpoint);
    }
    @Override
    public AzureMSEndpoint getEndpoint() {
        return (AzureMSEndpoint) super.getEndpoint();
    }

    public void process(Exchange exchange) throws Exception {
        log.info(""+exchange.getIn().getBody());
        String operation=exchange.getIn().getHeader(Constants.CamelAzureOperation,String.class);
        if(StringUtils.isEmpty(operation)){
        	throw new IllegalArgumentException("Missing operation in header");
        }
        Operation op=Operation.valueOf(operation.toUpperCase());
        MediaContract mediaService=getEndpoint().getMediaService();
        String assetId=exchange.getIn().getHeader(Constants.CamelAzureRequestAssetID,String.class);
		String localDir=exchange.getIn().getHeader(Constants.CamelAzureLocalDownloadDirectory,String.class);
		String presetName=exchange.getIn().getHeader(Constants.CamelAzureEncodePresetName,String.class);
		Integer priority=getEndpoint().getAzureMSConfiguration().getEncodePriority();

		String encodeJobName=exchange.getIn().getHeader(Constants.CamelAzureJobName,String.class);
		String uploadPolicyName=exchange.getIn().getHeader(Constants.CamelAzureUploadPolicyName,String.class);
		String downloadPolicyName=exchange.getIn().getHeader(Constants.CamelAzureDownloadPolicyName,String.class);
		String filepath=exchange.getIn().getHeader(Constants.CamelAzureLocalFileAbsolutePath,String.class);
		String assetName=exchange.getIn().getHeader(Constants.CamelAzureUploadedAssetName,String.class);
		assetName=StringUtils.isEmpty(assetName)?((filepath.indexOf("/")>0)?filepath.substring(filepath.lastIndexOf("/")):filepath.substring(filepath.lastIndexOf("\\"))):assetName;
		Integer maxEncodeDurationMillis=exchange.getIn().getHeader(Constants.CamelAzureMaxEncodeDurationMillis,Integer.class);
		maxEncodeDurationMillis=maxEncodeDurationMillis==null?10*3600*1000:maxEncodeDurationMillis;
		Integer streamAvailabilityDurationInMinutes=exchange.getIn().getHeader(Constants.CamelAzureStreamAvailabilityDurationInMinutes,Integer.class);
		streamAvailabilityDurationInMinutes=streamAvailabilityDurationInMinutes==null?60*24*3650:streamAvailabilityDurationInMinutes;
		String packagerConfigPath=exchange.getIn().getHeader(Constants.CamelAzurePackagerConfigPath,String.class);
		String encryptorConfigPath=exchange.getIn().getHeader(Constants.CamelAzureEncryptorConfigPath,String.class);

		Double duration=getEndpoint().getAzureMSConfiguration().getPolicyDurationInMinute();
        switch (op) {
		case CLEANUP:
				cleanup(mediaService);
			break;
		case DELETE:
				delete(getEndpoint().getMediaService(), assetId);
			break;
		case UPLOAD:
			String uploadedAssetdId=upload(mediaService, filepath, assetName, null, uploadPolicyName, duration);
		      exchange.getIn().setHeader(Constants.CamelAzureResponseAssetID,uploadedAssetdId);
			//main(null);
			break;

		case DOWNLOAD:
					download(mediaService,exchange, localDir, assetId, downloadPolicyName, duration,streamAvailabilityDurationInMinutes);
			break;
		case ENCODE:
		case ENCRYPT:
		case PACKAGE:
		case ENCODE_PACKAGE:
		case PACKAGE_ENCRYPT:
		case ENCODE_PACKAGE_ENCRYPT:
			transform(mediaService, priority,encodeJobName,op,assetId, exchange, presetName,packagerConfigPath,encryptorConfigPath,maxEncodeDurationMillis,streamAvailabilityDurationInMinutes,assetName);
			break;
		default:
			throw new UnsupportedOperationException(operation);
		}
    }

    private MediaProcessorInfo getLatestMediaProcessor(MediaContract mediaService, String encoderName) throws ServiceException{
        ListResult<MediaProcessorInfo> mediaProcessors = mediaService
                .list(MediaProcessor.list()
                .set("$filter", "Name eq '"+encoderName+"'"));

        // Use the latest version of the media processor.
        MediaProcessorInfo mediaProcessor = null;
        for (MediaProcessorInfo info : mediaProcessors)
        {
            if (null == mediaProcessor || info.getVersion().compareTo(mediaProcessor.getVersion()) > 0)
            {
                mediaProcessor = info;
            }
        }

        log.info("Using processor: " + mediaProcessor.getName() + " " + mediaProcessor.getVersion());
        return mediaProcessor;
    }


    private  static String upload(MediaContract mediaService, String filepath,
    		String assetName, String alternateId, String uploadPolicyName, Double policyDurationInMinute) throws ServiceException, FileNotFoundException, NoSuchAlgorithmException
    		{

        WritableBlobContainerContract uploader;

        AccessPolicyInfo uploadAccessPolicy;
        LocatorInfo uploadLocator = null;

        // Create an asset.
        AssetInfo asset = mediaService.create(Asset.create().setName(StringUtils.isEmpty(assetName)?"myAsset":assetName).setAlternateId(StringUtils.isEmpty(alternateId)?"altId":alternateId));
        log.info("Created asset with id: " + asset.getId());

        // Create an access policy that provides Write access for 15 minutes.
        uploadAccessPolicy = mediaService.create(AccessPolicy.create(StringUtils.isEmpty(uploadPolicyName)?"uploadAccessPolicy":uploadPolicyName,
        		policyDurationInMinute==null?30.0:policyDurationInMinute,
                                                                     EnumSet.of(AccessPolicyPermission.WRITE)));
        log.info("Created upload access policy with id: "
                + uploadAccessPolicy.getId());

        // Create a locator using the access policy and asset.
        // This will provide the location information needed to add files to the asset.
        uploadLocator = mediaService.create(Locator.create(uploadAccessPolicy.getId(),
                asset.getId(), LocatorType.SAS));
        log.info("Created upload locator with id: " + uploadLocator.getId());

        // Create the blob writer using the locator.
        uploader = mediaService.createBlobWriter(uploadLocator);


        // The name of the file as it will exist in your Media Services account.
        File fileToUpload=new File(filepath);
        // The local file that will be uploaded to your Media Services account.
        InputStream input = new FileInputStream(new File(filepath));
        log.info("Now uploading file:{}",fileToUpload.getAbsolutePath());
        // Upload the local file to the asset.



    	// Upload the local file to the asset.
        uploader.createBlockBlob(fileToUpload.getName(), null);

        String blockId;
        byte[] buffer = new byte[1024000];
        BlockList blockList = new BlockList();
        int bytesRead;
        ByteArrayInputStream byteArrayInputStream;
        try{
	        while ((bytesRead = input.read(buffer)) > 0)
	        {
		        blockId = UUID.randomUUID().toString();
		        byteArrayInputStream = new ByteArrayInputStream(buffer, 0, bytesRead);
		        uploader.createBlobBlock(fileToUpload.getName(), blockId, byteArrayInputStream);
		        blockList.addUncommittedEntry(blockId);
	        }
	        input.close();
        }catch(IOException e){
        	log.error(e.getMessage(),e);
        }

        uploader.commitBlobBlocks(fileToUpload.getName(), blockList);



      //  uploader.createBlockBlob(fileToUpload.getName(), input);
        log.info("File uploaded. Now creating file infos for asset:{}",asset.getId());

        // Inform Media Services about the uploaded files.
        mediaService.action(AssetFile.createFileInfos(asset.getId()));
        log.info("File uploaded.");


        log.info("Deleting upload locator and access policy.");
        mediaService.delete(Locator.delete(uploadLocator.getId()));
        mediaService.delete(AccessPolicy.delete(uploadAccessPolicy.getId()));
        return asset.getId();
    }


    // Download the output assets of the transformed asset.
    private void download(MediaContract mediaService, Exchange exchange, String localDownloadDirectory, String assetId, String downloadPolicyName, Double policyDurationInMinute, int streamAvailabilityDurationInMinutes) throws ServiceException, URISyntaxException, FileNotFoundException, StorageException, IOException
    {
    	StringBuffer buffer= new StringBuffer();
    	log.info("Starting download for asset:{}",assetId);
    	File directory=new File(localDownloadDirectory);
    	directory.mkdirs();
    //	String assets[]=commaSeparatedAssetList.split(",");
    	//for(String assetId:assets){
        // Create an asset.
        AssetInfo asset = mediaService.get(Asset.get(assetId));
        log.info("Created asset with id: " + asset.getId());

        AccessPolicyInfo downloadAccessPolicy = null;

        downloadAccessPolicy =
                mediaService.create(AccessPolicy.create(StringUtils.isEmpty(downloadPolicyName)?"Download":downloadPolicyName, policyDurationInMinute==null?30.0:policyDurationInMinute,
                		EnumSet.of(AccessPolicyPermission.READ)));
        log.info("Created download access policy with id: "   + downloadAccessPolicy.getId());

        LocatorInfo downloadLocator = null;
        downloadLocator = mediaService.create(Locator.create(downloadAccessPolicy.getId(), assetId, LocatorType.SAS));
        log.info("Created download locator with id: " + downloadLocator.getId());

        log.info("Accessing the output files of the encoded asset.");
        // Iterate through the files associated with the encoded asset.
        for(int i=0; i<50 && mediaService.list(AssetFile.list(asset.getAssetFilesLink())).size()==0; i++){
        	try{
        		log.info("Waiting 10 more seconds for files to be available");
        		Thread.sleep(10000);
        	}catch(Exception e){}
        }
        String url;
		try {
			url = getStreamingOriginLocation(mediaService,asset,streamAvailabilityDurationInMinutes);
			exchange.getIn().setHeader(Constants.CamelAzureResponseAssetID, buffer.toString());
			exchange.getIn().setHeader(Constants.CamelAzureStreamingOriginLocation, url);
			log.info("Origin URL found:{}",url);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}

        for(AssetFileInfo assetFile: mediaService.list(AssetFile.list(asset.getAssetFilesLink())))
        {
            String fileName = assetFile.getName();

            log.info("Downloading file: " + fileName + ". ");
            String locatorPath = downloadLocator.getPath();
            int startOfSas = locatorPath.indexOf("?");

            String blobPath = locatorPath + fileName;
            if (startOfSas >= 0)
            {
                blobPath = locatorPath.substring(0, startOfSas) + "/" + fileName + locatorPath.substring(startOfSas);
            }
            URI baseuri = new URI(blobPath);
            CloudBlobClient blobClient;
            blobClient = new CloudBlobClient(baseuri);

            // Ensure that you have a c:\output folder, or modify the path specified in the following statement.
            String localFileName = directory.getAbsolutePath()+"/" + fileName;

            CloudBlockBlob sasBlob;
            sasBlob = new CloudBlockBlob(baseuri, blobClient);
            File fileTarget = new File(localFileName);
            FileOutputStream fos= new FileOutputStream(fileTarget);
            sasBlob.download(fos);
            fos.flush();
            fos.close();
            buffer.append(","+localFileName);
            log.info("Download complete "+sasBlob.getMetadata());
            log.info("Deleting download locator and access policy.");

        }
        mediaService.delete(Locator.delete(downloadLocator.getId()));
        mediaService.delete(AccessPolicy.delete(downloadAccessPolicy.getId()));
        if(buffer.length()>0){
        	buffer.deleteCharAt(0);
        }
        exchange.getIn().setHeader(Constants.CamelAzureDownloadedFile, buffer.toString());
        //}
    }

    // Remove all assets from your Media Services account.
    // You could instead remove assets by name or ID, etc., but for
    // simplicity this example removes all of them.
    private  void cleanup(MediaContract mediaService) throws ServiceException
    {
        // Retrieve a list of all assets.
        List<AssetInfo> assets = mediaService.list(Asset.list());

        // Iterate through the list, deleting each asset.
        for (AssetInfo asset: assets)
        {
            log.info("Deleting asset named " + asset.getName() + " (" + asset.getId() + ")");
            mediaService.delete(Asset.delete(asset.getId()));
        }
    }

    private  void delete(MediaContract mediaService, String ...assets) throws ServiceException
    {
        // Iterate through the list, deleting each asset.
        for (String asset: assets)
        {
            mediaService.delete(Asset.delete(asset));
        }
    }

    // Helper function to check to on the status of the job.
    private String checkJobStatus(MediaContract mediaService,String jobId, int maxEncodeDurationMillis)
    {
        int maxRetries = maxEncodeDurationMillis/30000; // Number of times to retry. Small jobs often take 2 minutes.
        JobState jobState = null;
        while (maxRetries > 0)
        {
        	try{
	        	Thread.sleep(30000);  // Sleep for 10 seconds, or use another interval.
	            // Determine the job state.
	            jobState = mediaService.get(Job.get(jobId)).getState();
	            log.info("Job state is " + jobState);

	            if (jobState == JobState.Finished ||
	                jobState == JobState.Canceled ||
	                jobState == JobState.Error)
	            {
	                // The job is done.
	                return jobState.toString();
	            }
	            // The job is not done. Sleep and loop if max retries
	            // has not been reached.
	            maxRetries--;
        	}catch(Exception e){
        		log.warn(e.getLocalizedMessage());
        	}
        }
        return "NoResponse";
    }





    private void transform(MediaContract mediaService,Integer priority, String jobName,Operation op, String assetId, Exchange exchange, String presetName,
    		String packagerConfigPath, String encryptorConfigPath, int maxEncodeDurationMillis, int streamAvailabilityDurationInMinutes, String assetName) throws Exception {

    	AssetInfo asset = mediaService.get(Asset.get(assetId));
        String outputAssetName = asset.getName() + " encrypted";
        MediaProcessorInfo mediaEncoder=getLatestMediaProcessor(mediaService, Constants.ENCODER_NAME);
        MediaProcessorInfo mediaPackager=getLatestMediaProcessor(mediaService, Constants.PACKAGER_NAME);
        MediaProcessorInfo mediaEncryptor=getLatestMediaProcessor(mediaService, Constants.ENCRYPTOR_NAME);

        Creator creator=Job.create()
                .setName(assetName)
                .setPriority(priority)
                .addInputMediaAsset(asset.getId());
        int count=0;
        if(op.toString().contains(Operation.ENCODE.toString())){
        	creator.addTaskCreator(Task.create(mediaEncoder.getId(),
                        "<taskBody>" +
                                "<inputAsset>JobInputAsset("+count+")</inputAsset>" +
                                "<outputAsset>JobOutputAsset("+count+")</outputAsset>" +
                                "</taskBody>")
                        .setConfiguration(presetName)
                        .setOptions(TaskOption.None)
                        .setName("H264 encoding")
                );
        }
        if(op.toString().contains(Operation.PACKAGE.toString())){
        	log.info("Loading config file:{}",packagerConfigPath);
            InputStream stream = this.getClass().getResourceAsStream(packagerConfigPath);
            String packagerConf=IOUtils.toString(stream);
        	creator.addTaskCreator(Task.create(mediaPackager.getId(),
                        "<taskBody>" +
                                "<inputAsset>JobOutputAsset("+count+")</inputAsset>" +
                                "<outputAsset>JobOutputAsset("+(++count)+")</outputAsset>" +
                                "</taskBody>")
                        .setConfiguration(packagerConf)
                        .setOptions(TaskOption.None)
                        .setName("Smooth packaging")
                );
        }
        if(op.toString().contains(Operation.ENCRYPT.toString())){
        	String encryptConf = StringUtils.isEmpty(encryptorConfigPath)?null:FileUtils.readFileToString(new File(encryptorConfigPath), "UTF-8");
           creator.addTaskCreator(Task.create(mediaEncryptor.getId(),
                        "<taskBody>" +
                                "<inputAsset>JobOutputAsset("+count+")</inputAsset>" +
                                "<outputAsset assetName='" + outputAssetName + "'>JobOutputAsset("+(++count)+")</outputAsset>" +
                                "</taskBody>")
                        .setConfiguration(encryptConf)
                        .setOptions(TaskOption.None)
                        .setName("Encryption")
                );
        }
        JobInfo job = mediaService.create(creator);

        String jobId = job.getId();
        log.debug("Created job with id: " + jobId);

        // Check to see if the job has completed.
       String status= checkJobStatus(mediaService,jobId,maxEncodeDurationMillis);
       if(!status.equalsIgnoreCase(JobState.Finished.toString())){
    	   log.error("Job with ID {} did not complete properly. Abandoning.",jobId);
    	   return;
       }
        ListResult<AssetInfo> assets = mediaService.list(Asset.list(job.getOutputAssetsLink()));

        StringBuffer buffer = new StringBuffer();
        for(int i=0; i<assets.size(); i++){
        	buffer.append(","+assets.get(i).getId());
        }
        buffer.deleteCharAt(0);
        int size=0;
        for(int i=0; i<50 && size==0; i++){
        	try{
        		size=mediaService.list(AssetFile.list(assets.get(assets.size()-1).getAssetFilesLink())).size();
        		log.info("Waiting 60 more seconds for files to be available");
        		Thread.sleep(60000);
        	}catch(Exception e){
        		log.warn(e.getMessage());
        	}
        }
        String url;
		try {

			url = getStreamingOriginLocation(mediaService,assets.get(assets.size()-1),streamAvailabilityDurationInMinutes);
			 log.info("Final URL:"+url);
			exchange.getIn().setHeader(Constants.CamelAzureResponseAssetID, buffer.toString());
			exchange.getIn().setHeader(Constants.CamelAzureStreamingOriginLocation, url);
			log.info("Origin URL found:{}",url);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}

    }





    private static String packagingToSmoothConfig;






    private AssetFileInfo getManifestFile(ListResult<AssetFileInfo> files) throws Exception {
        for(AssetFileInfo file: files) {
            if (file.getName().endsWith(".ism"))
                return file;
        }
        throw new Exception("Manifest not found");
    }

    private String getStreamingOriginLocation(MediaContract mediaService, AssetInfo asset, Integer streamAvailabilityDurationInMinutes) throws Exception {

        ListResult<AssetFileInfo> files = mediaService.list(AssetFile.list(asset.getAssetFilesLink()));
        AssetFileInfo manifest = getManifestFile(files);

        // Create a 30-day readonly access policy.
        log.info("Creating an access policy that provides read access for {} minutes",streamAvailabilityDurationInMinutes);
        AccessPolicyInfo streamPolicy = mediaService.create(AccessPolicy.create("streamPolicy",
                streamAvailabilityDurationInMinutes, EnumSet.of(AccessPolicyPermission.READ)));

        LocatorInfo originLocator =
                mediaService.create(Locator.create(streamPolicy.getId(), asset.getId(), LocatorType.OnDemandOrigin));

        log.debug("Created OnDemandOrigin locator with id: " + originLocator.getId());

        log.debug("Streaming asset base path on origin: ");
        log.debug(originLocator.getPath());

        String urlForClientStreaming = originLocator.getPath() + manifest.getName() + "/manifest";

        log.debug("URL to manifest for client streaming: ");
        log.debug(urlForClientStreaming);

        ListResult<ContentKeyInfo> contentKeys = mediaService.list(ContentKey.list(asset.getContentKeysLink()));
        for (ContentKeyInfo key: contentKeys) {
            log.debug("ProtectionKeyId" + key.getProtectionKeyId());
        }

        return urlForClientStreaming;
    }

    private AssetInfo playReadyEncrypt(MediaContract mediaService, AssetInfo asset,MediaProcessorInfo mediaEncoder, MediaProcessorInfo mediaPackager, String encodingPreset, int maxEncodeDurationMillis) throws Exception {
        String outputAssetName = asset.getName() + " encrypted";
        JobInfo job = mediaService.create(Job.create()
                .setName("PlayReady Encryption " + asset.getName())
                .addInputMediaAsset(asset.getId())
                .addTaskCreator(Task.create(mediaEncoder.getId(),
                        "<taskBody>" +
                        		"<inputAsset>JobInputAsset(0)</inputAsset>" +
                                "<outputAsset>JobOutputAsset(1)</outputAsset>" +
                                "</taskBody>")
                        .setConfiguration(encodingPreset)
                        .setOptions(TaskOption.None)
                        .setName("Encoding")
                )

                .addTaskCreator(Task.create(mediaPackager.getId(),
                        "<taskBody>" +
                                "<inputAsset>JobOutputAsset(1)</inputAsset>" +
                                "<outputAsset assetName='" + outputAssetName+"'>JobOutputAsset(2)</outputAsset>" +
                                "</taskBody>")
                        .setConfiguration(packagingToSmoothConfig)
                        .setOptions(TaskOption.None)
                        .setName("Packaging")
                )

        );
        String jobId = job.getId();
        log.debug("Created job with id: " + jobId);
      checkJobStatus(mediaService, jobId,maxEncodeDurationMillis);
        ListResult<AssetInfo> assets = mediaService.list(Asset.list(job.getOutputAssetsLink()));
        return assets.get(assets.size()-1);
    }

    private void cleanup(MediaContract mediaService, AssetInfo[] assets) throws ServiceException
    {
        for (AssetInfo asset: assets)
        {
            if (asset == null) continue;
            log.debug("Deleting asset named " + asset.getName() + " (" + asset.getId() + ")");
            mediaService.delete(Asset.delete(asset.getId()));
        }
    }
   public static void main(String[] args) throws FileNotFoundException, NoSuchAlgorithmException, ServiceException {
	   String mediaServiceUri="https://media.windows.net/API/";
	   String oAuthUri="https://wamsprodglobal001acs.accesscontrol.windows.net/v2/OAuth2-13";
	   String scope="urn:WindowsAzureMediaServices";
			   String	   clientId="fenkamlibertas1";
			   String	   clientSecret="WHPM4Bn+ZlsZGY2i/kjBin5S4J87Ntpp0OGPl1bRLRI=";
	   Configuration configuration = MediaConfiguration
               .configureWithOAuthAuthentication(mediaServiceUri, oAuthUri, clientId, clientSecret, scope);

       // Create the MediaContract object using the specified configuration.
      MediaContract mediaService = MediaService.create(configuration);
      upload(mediaService, "D:\\var\\libertas\\hotfolders\\b88fef68-4dfc-4d35-9eef-7a443101cb99\\small_trailer.mp4", "b88fef68-4dfc-4d35-9eef-7a443101cb99\\small_trailer.mp4", null, "b88fef68-4dfc-4d35-9eef-7a443101cb99\\small_trailer.mp4-2014-12-01", null);
}
}
