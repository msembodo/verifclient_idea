package com.idemia.jkt.tec.VerifClient.model.customapdu;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Verify2gAdm1 {
	
	private String p1;
	private String p2;
	private String p3;
	
	public Verify2gAdm1() {}

	public Verify2gAdm1(String p1, String p2, String p3) {
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
	}

	public String getP1() {
		return p1;
	}
	
	@XmlAttribute
	public void setP1(String p1) {
		this.p1 = p1;
	}

	public String getP2() {
		return p2;
	}
	
	@XmlAttribute
	public void setP2(String p2) {
		this.p2 = p2;
	}

	public String getP3() {
		return p3;
	}
	
	@XmlAttribute
	public void setP3(String p3) {
		this.p3 = p3;
	}

}
