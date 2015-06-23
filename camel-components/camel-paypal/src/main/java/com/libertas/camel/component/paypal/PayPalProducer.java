package com.libertas.camel.component.paypal;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import lombok.extern.slf4j.Slf4j;

import urn.ebay.api.PayPalAPI.CreateRecurringPaymentsProfileReq;
import urn.ebay.api.PayPalAPI.CreateRecurringPaymentsProfileRequestType;
import urn.ebay.api.PayPalAPI.CreateRecurringPaymentsProfileResponseType;
import urn.ebay.api.PayPalAPI.PayPalAPIInterfaceServiceService;
import urn.ebay.apis.CoreComponentTypes.BasicAmountType;
import urn.ebay.apis.eBLBaseComponents.BillingPeriodDetailsType;
import urn.ebay.apis.eBLBaseComponents.BillingPeriodType;
import urn.ebay.apis.eBLBaseComponents.CreateRecurringPaymentsProfileRequestDetailsType;
import urn.ebay.apis.eBLBaseComponents.CreateRecurringPaymentsProfileResponseDetailsType;
import urn.ebay.apis.eBLBaseComponents.CreditCardDetailsType;
import urn.ebay.apis.eBLBaseComponents.CreditCardTypeType;
import urn.ebay.apis.eBLBaseComponents.CurrencyCodeType;
import urn.ebay.apis.eBLBaseComponents.RecurringPaymentsProfileDetailsType;
import urn.ebay.apis.eBLBaseComponents.ScheduleDetailsType;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.CreditCard;
import com.paypal.api.payments.CreditCardToken;
import com.paypal.api.payments.FundingInstrument;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.Transaction;
import com.paypal.core.rest.APIContext;
import com.paypal.core.rest.OAuthTokenCredential;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

/**
 * The PayPal producer.
 */
@Slf4j
public class PayPalProducer extends DefaultProducer {
	private Object HttpDelete;

    public PayPalProducer(PayPalEndpoint endpoint) {
        super(endpoint);
    }
    @Override
    public PayPalEndpoint getEndpoint() {
        return (PayPalEndpoint) super.getEndpoint();
    }

    public void process(Exchange exchange) throws Exception {
        log.info(""+exchange.getIn().getBody());
        String operation=exchange.getIn().getHeader(Constants.CamelPaypalOperation,String.class);
        if(StringUtils.isEmpty(operation)){
        	throw new IllegalArgumentException("Missing PayPal operation in header");
        }
        Operation op=Operation.valueOf(operation.toUpperCase());

		String mode=getEndpoint().getPayPalConfiguration().getMode();
		String userName=getEndpoint().getPayPalConfiguration().getUserName();
		String password=getEndpoint().getPayPalConfiguration().getPassword();
		String signature=getEndpoint().getPayPalConfiguration().getSignature();
		String clientId=getEndpoint().getPayPalConfiguration().getClientId();
		String clientSecret=getEndpoint().getPayPalConfiguration().getClientSecret();
		String billingStartDate = exchange.getIn().getHeader(Constants.CamelPaypalBillingStartDate,String.class);
		String creditCardNumber = exchange.getIn().getHeader(Constants.CamelPaypalCCNumber,String.class);
		String savedCreditCardId = exchange.getIn().getHeader(Constants.CamelPaypalSavedCC,String.class);
		String cVV2 = exchange.getIn().getHeader(Constants.CamelPaypalCCCVV2,String.class);
		Integer expMonth = exchange.getIn().getHeader(Constants.CamelPaypalCCExpMonth,Integer.class);
		Integer expYear = exchange.getIn().getHeader(Constants.CamelPaypalCCExpYear,Integer.class);
		String creditCardType = exchange.getIn().getHeader(Constants.CamelPaypalCCType,String.class);
		String payerFirstname = exchange.getIn().getHeader(Constants.CamelPaypalPayerFirstname,String.class);
		String payerLastname = exchange.getIn().getHeader(Constants.CamelPaypalPayerLastname,String.class);
		String description = exchange.getIn().getHeader(Constants.CamelPaypalDescription,String.class);
		String amount = exchange.getIn().getHeader(Constants.CamelPaypalAmount,String.class);
		String item = exchange.getIn().getHeader(Constants.CamelPaypalItem,String.class);

		CreditCardDetailsType creditCard = new CreditCardDetailsType();
		creditCard.setCreditCardNumber(creditCardNumber);
		creditCard.setCVV2(cVV2);
		creditCard.setExpMonth(expMonth);
		creditCard.setExpYear(expYear);
		log.info("Performing payment with expiry month and year:{}-{}",expMonth,expYear);
		if (op == Operation.RECURRING_PAYMENT_CREDITCARD)
			creditCard.setCreditCardType(CreditCardTypeType.fromValue(creditCardType));

        switch (op) {
        case RECURRING_PAYMENT_CREDITCARD:
        	CreateRecurringPaymentsCreditCard(exchange,creditCard,billingStartDate,amount,mode,userName,password,signature,item);
			break;
        case SIMPLE_PAYMENT_CREDITCARD:
        	createSimplePaymentsCreditCard(exchange,clientId, clientSecret, mode,
        			creditCardNumber,creditCardType,expMonth,expYear,cVV2,payerFirstname,payerLastname,amount,"USD",description);
			break;
        case SAVE_CREDITCARD_PAYMENT:
        	saveCreditCardPayments(exchange,clientId, clientSecret, mode,
        			creditCardNumber,creditCardType,expMonth,expYear,payerFirstname,payerLastname);
			break;
        case SIMPLE_PAYMENT_WITH_SAVED_CREDITCARD:
        	createSimplePaymentsWithSavedCreditCard(exchange,clientId, clientSecret, mode,
        			savedCreditCardId,amount,description);
			break;
        case DELETE_CREDITCARD:
        	deleteCreditCard(exchange,clientId, clientSecret, mode,	savedCreditCardId);
			break;

//			deleteCreditCard(Exchange exchange,String clientId,String clientSecret,String mode,String creditCardId)
		default:
			throw new UnsupportedOperationException(operation);
		}
    }

    private void CreateRecurringPaymentsCreditCard(Exchange exchange,CreditCardDetailsType creditCard,String billingStartDate,String amount,String mode,String userName,String password,String signature,String item)  throws Exception{
		RecurringPaymentsProfileDetailsType profileDetails = new RecurringPaymentsProfileDetailsType(billingStartDate);
		BasicAmountType paymentAmount = new BasicAmountType(CurrencyCodeType.USD, amount);
		BillingPeriodType period = BillingPeriodType.fromValue("Month");
		int frequency = 1;
		BillingPeriodDetailsType paymentPeriod = new BillingPeriodDetailsType(period, frequency, paymentAmount);
		ScheduleDetailsType scheduleDetails = new ScheduleDetailsType();
		scheduleDetails.setDescription(item);
		scheduleDetails.setPaymentPeriod(paymentPeriod);
		CreateRecurringPaymentsProfileRequestDetailsType createRPProfileRequestDetails = new CreateRecurringPaymentsProfileRequestDetailsType(profileDetails, scheduleDetails);
		createRPProfileRequestDetails.setCreditCard(creditCard);

		CreateRecurringPaymentsProfileRequestType createRPProfileRequest = new CreateRecurringPaymentsProfileRequestType();
		createRPProfileRequest.setCreateRecurringPaymentsProfileRequestDetails(createRPProfileRequestDetails);

		CreateRecurringPaymentsProfileReq createRPPProfileReq = new CreateRecurringPaymentsProfileReq();
		createRPPProfileReq.setCreateRecurringPaymentsProfileRequest(createRPProfileRequest);

		Map<String, String> sdkConfig = new HashMap<String, String>();
		sdkConfig.put("mode", mode);
		sdkConfig.put("acct1.UserName", userName);
		sdkConfig.put("acct1.Password", password);
		sdkConfig.put("acct1.Signature",signature);
		PayPalAPIInterfaceServiceService service = new PayPalAPIInterfaceServiceService(sdkConfig);
		CreateRecurringPaymentsProfileResponseType createRPProfileResponse = service.createRecurringPaymentsProfile(createRPPProfileReq);
		exchange.getIn().setHeader(Constants.CamelPaypalResponseCode, createRPProfileResponse.getAck().getValue());
        XStream xstream = new XStream(new JettisonMappedXmlDriver());
        xstream.setMode(XStream.NO_REFERENCES);
        xstream.alias("CreateRecurringPaymentsProfileResponseDetailsType", CreateRecurringPaymentsProfileResponseDetailsType.class);
		exchange.getIn().setHeader(Constants.CamelPaypalResponseMessage, xstream.toXML(createRPProfileResponse.getCreateRecurringPaymentsProfileResponseDetails()));
         log.info("Subscription message {}",createRPProfileResponse.getCreateRecurringPaymentsProfileResponseDetails());
		log.info("Recurring Payment Ack received= {}",createRPProfileResponse.getAck().getValue());
    }

    private void createSimplePaymentsCreditCard(Exchange exchange,String clientId,String clientSecret,String mode,
    		String creditCardNumber,String creditCardType,Integer expMonth,Integer expYear,String cVV2,
    		String payerFirstname,String payerLastname,String amount,String currencyCodeType,String description) throws Exception{
    	Map<String, String> sdkConfig = new HashMap<String, String>();
		sdkConfig.put("mode", mode);
		String accessToken = new OAuthTokenCredential(clientId, clientSecret, sdkConfig).getAccessToken();
		System.out.println(accessToken);

		HttpClient hc = new DefaultHttpClient();
        String messageRequest;
        HttpPost post = new HttpPost("https://api.sandbox.paypal.com/v1/payments/payment");
        if("live".equals(mode))
        post = new HttpPost("https://api.paypal.com/v1/payments/payment");
        	messageRequest = "{\"intent\": \"sale\",\"payer\": {\"payment_method\": \"credit_card\",\"funding_instruments\": [{\"credit_card\": {\"number\": \""+creditCardNumber+"\",\"type\": \""+creditCardType+"\",\"expire_month\": "+expMonth+",\"expire_year\": "+expYear+",\"cvv2\": "+cVV2+",\"first_name\": \""+payerFirstname+"\",\"last_name\": \""+payerLastname+"\" }}]},\"transactions\": [{\"amount\": {\"total\": \""+amount+"\",\"currency\": \""+currencyCodeType+"\"},\"description\": \""+description+"\"}]}";
        	log.info("Sending paypal purchase request {}",messageRequest);
        	post.setEntity(new StringEntity(messageRequest, "UTF8"));
        	post.setHeader("Content-type", "application/json");
        	post.setHeader("Authorization", accessToken);
            HttpResponse resp = hc.execute(post);
            BufferedReader rd = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
            String messageResponse = rd.readLine();
            int statusCode = resp.getStatusLine().getStatusCode();
            exchange.getIn().setHeader(Constants.CamelPaypalResponseCode, statusCode);
            exchange.getIn().setHeader(Constants.CamelPaypalResponseMessage, messageResponse);
            log.info("Purchase response code {}",statusCode);
            log.info("Purchase response message {}",messageResponse);
    }

    private void saveCreditCardPayments(Exchange exchange,String clientId,String clientSecret,String mode,
    		String creditCardNumber,String creditCardType,Integer expMonth,Integer expYear,
    		String payerFirstname,String payerLastname) throws Exception{

    	Map<String, String> sdkConfig = new HashMap<String, String>();
		sdkConfig.put("mode", mode);
		String accessToken = new OAuthTokenCredential(clientId, clientSecret, sdkConfig).getAccessToken();
		System.out.println(accessToken);
		APIContext apiContext = new APIContext(accessToken);
		apiContext.setConfigurationMap(sdkConfig);
		CreditCard creditCard = new CreditCard();
		creditCard.setType(creditCardType);
		creditCard.setNumber(creditCardNumber);
		creditCard.setExpireMonth(expMonth);
		creditCard.setExpireYear(expYear);
		creditCard.setFirstName(payerFirstname);
		creditCard.setLastName(payerLastname);
		CreditCard createdCreditCard = creditCard.create(apiContext);
		exchange.getIn().setHeader(Constants.CamelPaypalSavedCC,createdCreditCard);
    }

    private void createSimplePaymentsWithSavedCreditCard(Exchange exchange,String clientId,String clientSecret,String mode,
    		String savedCreditCardId,String payedAmount,String description) throws Exception{

    	Map<String, String> sdkConfig = new HashMap<String, String>();
		sdkConfig.put("mode", mode);
		String accessToken = new OAuthTokenCredential(clientId, clientSecret, sdkConfig).getAccessToken();

		APIContext apiContext = new APIContext(accessToken);
		apiContext.setConfigurationMap(sdkConfig);

		CreditCardToken creditCardToken = new CreditCardToken();
		creditCardToken.setCreditCardId(savedCreditCardId);

		FundingInstrument fundingInstrument = new FundingInstrument();
		fundingInstrument.setCreditCardToken(creditCardToken);

		List<FundingInstrument> fundingInstrumentList = new ArrayList<FundingInstrument>();
		fundingInstrumentList.add(fundingInstrument);

		Payer payer = new Payer();
		payer.setFundingInstruments(fundingInstrumentList);
		payer.setPaymentMethod("credit_card");

		Amount amount = new Amount();
		amount.setCurrency("USD");
		amount.setTotal(payedAmount);

		Transaction transaction = new Transaction();
		transaction.setDescription(description);
		transaction.setAmount(amount);

		List<Transaction> transactions = new ArrayList<Transaction>();
		transactions.add(transaction);

		Payment payment = new Payment();
		payment.setIntent("sale");
		payment.setPayer(payer);
		payment.setTransactions(transactions);

		Payment createdPayment = payment.create(apiContext);
		exchange.getIn().setHeader(Constants.CamelPaypalCCPayment,createdPayment);
    }

    private void deleteCreditCard(Exchange exchange,String clientId,String clientSecret,String mode,String creditCardId) throws Exception{
    	Map<String, String> sdkConfig = new HashMap<String, String>();
		sdkConfig.put("mode", mode);
		String accessToken = new OAuthTokenCredential(clientId, clientSecret, sdkConfig).getAccessToken();
		System.out.println(accessToken);

		HttpClient hc = new DefaultHttpClient();
        HttpDelete httpdDelete = new HttpDelete("https://api.sandbox.paypal.com/v1/vault/credit-card/"+creditCardId);
        if("live".equals(mode))
        	httpdDelete = new HttpDelete("https://api.paypal.com/v1/vault/credit-card/"+creditCardId);
        	httpdDelete.setHeader("Content-type", "application/json");
        	httpdDelete.setHeader("Authorization", accessToken);
            HttpResponse resp = hc.execute(httpdDelete);
            int statusCode = resp.getStatusLine().getStatusCode();
            exchange.getIn().setHeader(Constants.CamelPaypalDeleteCCResponseCode, statusCode);
            log.info("Delete CreditCard response code {}",statusCode);
            if (statusCode != 204){
            	log.info("Delete CreditCard {} failed",creditCardId);
            }
    }
}