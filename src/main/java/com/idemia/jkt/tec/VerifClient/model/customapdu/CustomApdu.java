package com.idemia.jkt.tec.VerifClient.model.customapdu;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CustomApdu {
	
	private Verify2g verify2g;
	private Verify3g verify3g;
	
	public CustomApdu() {}

	public CustomApdu(Verify2g verify2g, Verify3g verify3g) {
		this.verify2g = verify2g;
		this.verify3g = verify3g;
	}

	public Verify2g getVerify2g() {
		return verify2g;
	}
	
	@XmlElement
	public void setVerify2g(Verify2g verify2g) {
		this.verify2g = verify2g;
	}

	public Verify3g getVerify3g() {
		return verify3g;
	}
	
	@XmlElement
	public void setVerify3g(Verify3g verify3g) {
		this.verify3g = verify3g;
	}

}
