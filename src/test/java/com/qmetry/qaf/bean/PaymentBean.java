package com.qmetry.qaf.bean;

import com.qmetry.qaf.automation.data.BaseDataBean;
import com.qmetry.qaf.automation.util.Randomizer;

public class PaymentBean extends BaseDataBean{
	@Randomizer
	private String longNum;
	
	@Randomizer
	private String expiries;
	
	@Randomizer
	private String ccv;
	
	@Randomizer
	private String userID;

	public String getLongNumber() {
		return longNum;
	}

	public void setLongNumber(String longNumber) {
		this.longNum = longNumber;
	}

	public String getExpiries() {
		return expiries;
	}

	public void setExpiries(String expiry) {
		this.expiries = expiry;
	}

	public String getCcv() {
		return ccv;
	}

	public void setCcv(String ccv) {
		this.ccv = ccv;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}
	
}
