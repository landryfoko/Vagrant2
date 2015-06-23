package com.libertas.camel.component.braintree;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;

public class BraintreeConfiguration {

	private String environment;
	private String merchantId;
	private String publicKey;
	private String privateKey;
	private BraintreeGateway gateway;
	
	public Environment getEnvironment() {
		return environment.equalsIgnoreCase("development")?Environment.DEVELOPMENT:(environment.equalsIgnoreCase("sandbox")?Environment.SANDBOX:Environment.PRODUCTION);
	}
	public void setEnvironment(String environment) {
		this.environment = environment;
	}
	public String getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
	public String getPublicKey() {
		return publicKey;
	}
	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}
	public String getPrivateKey() {
		return privateKey;
	}
	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}


    public BraintreeGateway getGateway(){
		if(gateway==null){
			gateway = new BraintreeGateway(getEnvironment(),
						getMerchantId(),
						getPublicKey(),getPrivateKey()
					);
		}
			return gateway;
    }
    
}
