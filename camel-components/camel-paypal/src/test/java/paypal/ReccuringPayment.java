package paypal;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.paypal.core.credential.ICredential;
import com.paypal.exception.ClientActionRequiredException;
import com.paypal.exception.HttpErrorException;
import com.paypal.exception.InvalidCredentialException;
import com.paypal.exception.InvalidResponseDataException;
import com.paypal.exception.MissingCredentialException;
import com.paypal.exception.SSLConfigurationException;
import com.paypal.sdk.exceptions.OAuthException;

import urn.ebay.api.PayPalAPI.CreateRecurringPaymentsProfileReq;
import urn.ebay.api.PayPalAPI.CreateRecurringPaymentsProfileRequestType;
import urn.ebay.api.PayPalAPI.CreateRecurringPaymentsProfileResponseType;
import urn.ebay.api.PayPalAPI.PayPalAPIInterfaceServiceService;
import urn.ebay.apis.CoreComponentTypes.BasicAmountType;
import urn.ebay.apis.eBLBaseComponents.BillingPeriodDetailsType;
import urn.ebay.apis.eBLBaseComponents.BillingPeriodType;
import urn.ebay.apis.eBLBaseComponents.CreateRecurringPaymentsProfileRequestDetailsType;
import urn.ebay.apis.eBLBaseComponents.CreditCardDetailsType;
import urn.ebay.apis.eBLBaseComponents.CreditCardTypeType;
import urn.ebay.apis.eBLBaseComponents.CurrencyCodeType;
import urn.ebay.apis.eBLBaseComponents.RecurringPaymentsProfileDetailsType;
import urn.ebay.apis.eBLBaseComponents.ScheduleDetailsType;

//import com.paypal.soap.api.BasicAmountType;
//import com.paypal.soap.api.BillingPeriodDetailsType;
//import com.paypal.soap.api.BillingPeriodType;
//import com.paypal.soap.api.CreateRecurringPaymentsProfileReq;
//import com.paypal.soap.api.CreateRecurringPaymentsProfileRequestDetailsType;
//import com.paypal.soap.api.CreateRecurringPaymentsProfileRequestType;
//import com.paypal.soap.api.CreditCardDetailsType;
//import com.paypal.soap.api.CreditCardTypeType;
//import com.paypal.soap.api.CurrencyCodeType;
//import com.paypal.soap.api.RecurringPaymentsProfileDetailsType;
//import com.paypal.soap.api.ScheduleDetailsType;

public class ReccuringPayment {

	public static void main(String[] args) throws SSLConfigurationException, InvalidCredentialException, HttpErrorException, InvalidResponseDataException, ClientActionRequiredException, MissingCredentialException, OAuthException, IOException, InterruptedException, ParserConfigurationException, SAXException {
		RecurringPaymentsProfileDetailsType profileDetails = new RecurringPaymentsProfileDetailsType("2014-03-19T00:00:00:000Z");

		BasicAmountType paymentAmount = new BasicAmountType(CurrencyCodeType.USD, "2.1");
		BillingPeriodType period = BillingPeriodType.fromValue("Month");
		int frequency = 1;
		BillingPeriodDetailsType paymentPeriod = new BillingPeriodDetailsType(period, frequency, paymentAmount);

		ScheduleDetailsType scheduleDetails = new ScheduleDetailsType();
		scheduleDetails.setDescription("recurring billing");
		scheduleDetails.setPaymentPeriod(paymentPeriod);

		CreditCardDetailsType creditCard = new CreditCardDetailsType();
		creditCard.setCreditCardNumber("4745425765192217");
		creditCard.setCVV2("962");
		creditCard.setExpMonth(03);
		creditCard.setExpYear(2014); 
		creditCard.setCreditCardType(CreditCardTypeType.fromValue("Visa"));

		CreateRecurringPaymentsProfileRequestDetailsType createRPProfileRequestDetails = new CreateRecurringPaymentsProfileRequestDetailsType(profileDetails, scheduleDetails);
		createRPProfileRequestDetails.setCreditCard(creditCard);

		CreateRecurringPaymentsProfileRequestType createRPProfileRequest = new CreateRecurringPaymentsProfileRequestType();
		createRPProfileRequest.setCreateRecurringPaymentsProfileRequestDetails(createRPProfileRequestDetails);

		CreateRecurringPaymentsProfileReq createRPPProfileReq = new CreateRecurringPaymentsProfileReq();
		createRPPProfileReq.setCreateRecurringPaymentsProfileRequest(createRPProfileRequest);

		Map<String, String> sdkConfig = new HashMap<String, String>();
//		sdkConfig.put("mode", "sandbox");
		sdkConfig.put("mode", "sandbox");
		sdkConfig.put("acct1.UserName", "melek.zri-facilitator_api1.gmail.com");
		sdkConfig.put("acct1.Password", "1394224432");
		sdkConfig.put("acct1.Signature","Ai5EQKjO1bWUNWBYzUItnQVlshUGASVjaND7j6Mggx0Ea1a9xBu1TcVF");
		PayPalAPIInterfaceServiceService service = new PayPalAPIInterfaceServiceService(sdkConfig);
		CreateRecurringPaymentsProfileResponseType createRPProfileResponse = service.createRecurringPaymentsProfile(createRPPProfileReq);	
		System.out.println(createRPProfileResponse.getAck().getValue());	
		
	}
	
	

}
