package com.idemia.jkt.tec.VerifClient.service;

import com.idemia.jkt.tec.VerifClient.model.VerifConfig;
import com.idemia.jkt.tec.VerifClient.response.ConverterResponse;
import com.idemia.jkt.tec.VerifClient.response.VerificationResponse;

public interface VerifConfigService {
	
	VerifConfig initConfig();
	void saveConfig(VerifConfig verifConfig);
	VerificationResponse runVerif() throws Exception;
	ConverterResponse convertUxp(String docPath) throws Exception;
	ConverterResponse convertExMorphoDoc(String docPath) throws Exception;
	void shutdownVerifServer() throws Exception;

}
