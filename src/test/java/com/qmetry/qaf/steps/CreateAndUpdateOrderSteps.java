package com.qmetry.qaf.steps;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import com.google.gson.Gson;
import com.qmetry.qaf.automation.core.ConfigurationManager;
import com.qmetry.qaf.automation.step.QAFTestStep;
import com.qmetry.qaf.automation.util.Reporter;
import com.qmetry.qaf.automation.util.Validator;
import com.qmetry.qaf.automation.ws.Response;
import com.qmetry.qaf.automation.ws.rest.RestWSTestCase;
import com.qmetry.qaf.bean.AddressBean;
import com.qmetry.qaf.bean.PaymentBean;
import com.qmetry.qaf.bean.UserBean;
import com.sun.jersey.api.client.ClientResponse;

public class CreateAndUpdateOrderSteps extends RestWSTestCase{

	static String userID=null;
	static String username=null;
	static String password=null; 
	String baseurl=ConfigurationManager.getBundle().getString("env.baseurl");
		
	@QAFTestStep(description="user adds new shipping address {0} {1} {2} {3} {4}")
	public void addNewAddress(String city,String country,String number,String postcode, String street) {
		AddressBean addressBean=new AddressBean();
		addressBean.setCity(city);
		addressBean.setCountry(country);
		addressBean.setNumber(number);
		addressBean.setPostcode(postcode);
		addressBean.setStreet(street);
		addressBean.setUserid(userID);
		getClient().resource(baseurl+"/addresses").header("Content-Type", "application/json").post(new Gson().toJson(addressBean));
	}
	
	@QAFTestStep(description="address is added successfully")
	public void verifyAddressAdded() {
		JSONObject jsonObject=new JSONObject(getResponse().getMessageBody());
		getWebResource("/addresses").header("Content-Type", "application/json").get(ClientResponse.class);
		Reporter.log("Address id:"+jsonObject.get("id"));
		Validator.verifyThat("Adding address status code",getResponse().getStatus().getStatusCode(), Matchers.equalTo(200));
	}
	
	@QAFTestStep(description="user adds new payment details {0} {1} {2}")
	public void addNewCard(String longNum,String expiry,String cvv) {
		PaymentBean paymentBean=new PaymentBean();
		paymentBean.setLongNumber(longNum);
		paymentBean.setExpiries(expiry);
		paymentBean.setCcv(cvv);
		paymentBean.setUserID(userID);
		getClient().resource(baseurl+"/cards").header("Content-Type", "application/json").post(new Gson().toJson(paymentBean));
		JSONObject jsonObject=new JSONObject(getResponse().getMessageBody());
		getWebResource("/cards").header("Content-Type", "application/json").get(ClientResponse.class);
		Reporter.log("Address id:"+jsonObject.get("id"));
		Validator.verifyThat("Adding card status code",getResponse().getStatus().getStatusCode(), Matchers.equalTo(200));
	}
	
	@QAFTestStep(description="user places the order")
	public void orderPlacement() {
		getWebResource("/orders").header("Content-Type", "application/json").post(ClientResponse.class);
		Validator.verifyThat("Adding card status code",getResponse().getStatus().getStatusCode(), Matchers.equalTo(201));
	}
	
	@QAFTestStep(description="verifies the placed order")
	public void verifyOrderPlacement() {
		JSONObject jsonObject=new JSONObject(getResponse().getMessageBody());
		getWebResource("/orders").header("Content-Type", "application/json").get(ClientResponse.class);
		Reporter.log("Order created id:"+jsonObject.get("id"));
		Validator.verifyThat("Order placement status code",getResponse().getStatus().getStatusCode(), Matchers.equalTo(201));
	}

	@QAFTestStep(description="user creates a new user")
	public void createUser() {
		UserBean userBean=new UserBean();
		userBean.fillRandomData();
		getWebResource("/register").header("Content-Type", "application/json").post(new Gson().toJson(userBean));
		Response response = getResponse();
		JSONObject jsonObject=new JSONObject(response.getMessageBody());
		username=userBean.getUsername();
		password=userBean.getPassword();
		userID=jsonObject.getString("id");
		Validator.verifyThat("Create User:",jsonObject.getString("id").length(), Matchers.greaterThan(10));
		Reporter.log("user id for create user:"+jsonObject.getString("id"));
	}
	
	@QAFTestStep(description="user add Product into cart {0}")
	public void addProductToCart(String item) {
		Response response = getResponse();
		JSONObject jsonObject=new JSONObject(response.getMessageBody());
		jsonObject=new JSONObject();
		jsonObject.put("id", item);
		jsonObject.put("userID", userID);
		getWebResource("/cart").header("Content-Type", "application/json").post(jsonObject.toString());
		Validator.verifyThat("Add to cart status:",getResponse().getStatus().getStatusCode(),
				Matchers.equalTo(201));
	}	
	
	@QAFTestStep(description = "user verify cart")
	public void verifyCart() {
		ClientResponse clientResponse = getClient().resource(baseurl + "/cart")
				.header("Content-Type", "application/json").get(ClientResponse.class);
		Response response = new Response(clientResponse);
		Reporter.log("Response for verify cart:" + response.getMessageBody().toString());
		Validator.verifyThat("Verify cart status:",getResponse().getStatus().getStatusCode(),
				Matchers.equalTo(200));
	}
	
	@QAFTestStep(description="user updates the cart quantity by {0} for {1}")
	public void updateCart(String quantity,String item) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("quantity", quantity);
		map.put("id", item);
		map.put("userID", userID);
		getWebResource("/cart/update")
				.header("Content-Type", "application/json")
				.post(ClientResponse.class, new Gson().toJson(map));
		Validator.verifyThat("Update cart status:",getResponse().getStatus().getStatusCode(),
				Matchers.equalTo(202));
	}
	
	@QAFTestStep(description="user deletes product {0}")
	public void deleteProduct(String item) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", item);
		map.put("userID", userID);
		getWebResource("/cart").header("Content-Type", "application/json").delete(ClientResponse.class);
		Validator.verifyThat("Delete Product status code:",getResponse().getStatus().getStatusCode(), Matchers.equalTo(202));
	}
}
