package com.idemia.jkt.tec.VerifClient.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.idemia.jkt.tec.VerifClient.response.ConverterResponse;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.idemia.jkt.tec.VerifClient.model.VerifConfig;
import com.idemia.jkt.tec.VerifClient.model.VerifLiterals;
import com.idemia.jkt.tec.VerifClient.model.customapdu.CustomApdu;
import com.idemia.jkt.tec.VerifClient.response.VerificationResponse;
import com.idemia.jkt.tec.VerifClient.controller.CustomAPDUController;
import com.idemia.jkt.tec.VerifClient.controller.RootLayoutController;

@Service
public class VerifConfigServiceImpl implements VerifConfigService {
	
	static Logger logger = Logger.getLogger(VerifConfigServiceImpl.class.getName());
	
	// constants for default values
	private final String CSV_PATH_DEFAULT = "C:\\";
	private final int READER_DEFAULT = 0;
	private final String USIM_AID_DEFAULT = "A0000000871002FF49FFFF89040B00FF";
	private final String ADM1_DEFAULT = "933F57845F706921";
	private final String CHV1_DEFAULT = "31323334FFFFFFFF";
	private final String S_FILESTRUCT_TR_DEFAULT = "Transparent;TR";
	private final String S_FILESTRUCT_LF_DEFAULT = "Linear;LF";
	private final String S_FILESTRUCT_CY_DEFAULT = "Cyclic;CY";
	private final String S_FILESTRUCT_LK_DEFAULT = "Link;LK";
	private final String S_ACC_ALW_DEFAULT = "Always;ALW";
	private final String S_ACC_CHV1_DEFAULT = "CHV1;GPIN1";
	private final String S_ACC_CHV2_DEFAULT = "CHV2;LPIN1";
	private final String S_ACC_ADM1_DEFAULT = "ADM1";
	private final String S_ACC_ADM2_DEFAULT = "ADM2";
	private final String S_ACC_ADM3_DEFAULT = "ADM3";
	private final String S_ACC_ADM4_DEFAULT = "ADM4";
	private final String S_ACC_ADM5_DEFAULT = "ADM5";
	private final String S_ACC_ADM6_DEFAULT = "ADM6";
	private final String S_ACC_ADM7_DEFAULT = "ADM7";
	private final String S_ACC_ADM8_DEFAULT = "ADM8";
	private final String S_ACC_NEV_DEFAULT = "Never;NEV";
	private final String S_ACC_AND_DEFAULT = "AND";
	private final String S_ACC_OR_DEFAULT = "OR";
	
	private File configFile;
	
	private final String SERVER_URL = "http://127.0.0.1:5000/";
	private final String statusUri = SERVER_URL + "getServerStatus";
	private String exceptionMessage;
	
	@Autowired
	private RootLayoutController root;
	
	@Autowired
	private CustomAPDUController customApduController;
	
	@Override
	public VerifConfig initConfig() {
		configFile = new File("config.xml");
		if (configFile.exists()) {
			// read config.xml
			try {
				JAXBContext jaxbContext = JAXBContext.newInstance(VerifConfig.class);
				
				Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				VerifConfig userConfig = (VerifConfig) jaxbUnmarshaller.unmarshal(configFile);
				String initialStatus = "Configuration loaded.";
				if (userConfig.getPathToCsv().equals(""))
					initialStatus += " (no CSV selected)";
				else
					initialStatus += ( " Last CSV used: " + userConfig.getPathToCsv());
				root.getAppStatusBar().setText(initialStatus);
				
				return userConfig;
				
			} catch (JAXBException e) {
				e.printStackTrace();
				return null;
			}
		} 
		else {
			// initialize default values and write to xml
			VerifConfig defaultConfig = new VerifConfig();
			defaultConfig.setReaderNumber(READER_DEFAULT);
			defaultConfig.setChv1Disabled(true);
			defaultConfig.setHexSfi(true);
			defaultConfig.setHexRecordNumber(false);
			defaultConfig.setUseVariablesTxt(false);
			defaultConfig.setUsimIn3gMode(false);
			defaultConfig.setUsimAid(USIM_AID_DEFAULT);
			defaultConfig.setUseAdm2(false);
			defaultConfig.setUseAdm3(false);
			defaultConfig.setUseAdm4(false);
			defaultConfig.setCodeAdm1(ADM1_DEFAULT);
			defaultConfig.setCodeAdm2(ADM1_DEFAULT);
			defaultConfig.setCodeAdm3(ADM1_DEFAULT);
			defaultConfig.setCodeAdm4(ADM1_DEFAULT);
			defaultConfig.setCodeChv1(CHV1_DEFAULT);
			defaultConfig.setCodeChv2(CHV1_DEFAULT);
			
			VerifLiterals defaultLiterals = new VerifLiterals();
			defaultLiterals.setsFileStructTR(S_FILESTRUCT_TR_DEFAULT);
			defaultLiterals.setsFileStructLF(S_FILESTRUCT_LF_DEFAULT);
			defaultLiterals.setsFileStructCY(S_FILESTRUCT_CY_DEFAULT);
			defaultLiterals.setsFileStructLK(S_FILESTRUCT_LK_DEFAULT);
			defaultLiterals.setsAccALW(S_ACC_ALW_DEFAULT);
			defaultLiterals.setsAccCHV1(S_ACC_CHV1_DEFAULT);
			defaultLiterals.setsAccCHV2(S_ACC_CHV2_DEFAULT);
			defaultLiterals.setsAccADM1(S_ACC_ADM1_DEFAULT);
			defaultLiterals.setsAccADM2(S_ACC_ADM2_DEFAULT);
			defaultLiterals.setsAccADM3(S_ACC_ADM3_DEFAULT);
			defaultLiterals.setsAccADM4(S_ACC_ADM4_DEFAULT);
			defaultLiterals.setsAccADM5(S_ACC_ADM5_DEFAULT);
			defaultLiterals.setsAccADM6(S_ACC_ADM6_DEFAULT);
			defaultLiterals.setsAccADM7(S_ACC_ADM7_DEFAULT);
			defaultLiterals.setsAccADM8(S_ACC_ADM8_DEFAULT);
			defaultLiterals.setsAccNEV(S_ACC_NEV_DEFAULT);
			defaultLiterals.setsAccAND(S_ACC_AND_DEFAULT);
			defaultLiterals.setsAccOR(S_ACC_OR_DEFAULT);
			
			CustomApdu defaultCustomApdu = customApduController.getDefaultApduParams();
			
			defaultConfig.setCustomApdu(defaultCustomApdu);
			defaultConfig.setVerifLiterals(defaultLiterals);
			defaultConfig.setPathToCsv(CSV_PATH_DEFAULT);
			defaultConfig.setPathToVariablesTxt(""); // set blank for default
			
			try {
				JAXBContext jaxbContext = JAXBContext.newInstance(VerifConfig.class);
				Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
				
				jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); // pretty print xml
				jaxbMarshaller.marshal(defaultConfig, configFile);
				root.getAppStatusBar().setText("config.xml not found; create one with default values (no CSV selected).");
				
				return defaultConfig;
				
			} catch (JAXBException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	@Override
	public void saveConfig(VerifConfig verifConfig) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(VerifConfig.class);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			jaxbMarshaller.marshal(verifConfig, configFile);
			root.getAppStatusBar().setText("Configuration saved.");
			
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	@Override
	public VerificationResponse runVerif() throws Exception {
		String uri = SERVER_URL + "run";

		if (serverIsRunning(statusUri)) {
			String httpGetResult = doGet(uri);
			Gson gson = new Gson();
			return gson.fromJson(httpGetResult, VerificationResponse.class);
		} else
			return new VerificationResponse(false, exceptionMessage);
	}
	
	@Override
	public void shutdownVerifServer() throws Exception {
		String uri = SERVER_URL + "killVerifServer";
		if (serverIsRunning(statusUri))
			doGet(uri);
	}

	@Override
	public ConverterResponse convertUxp(String docPath) throws Exception {
		String uri = SERVER_URL + "convertUxp";

		if (serverIsRunning(statusUri)) {
			String httpPostResult = postToConvert(uri, docPath);
			Gson gson = new Gson();
			return gson.fromJson(httpPostResult, ConverterResponse.class);
		} else
			return new ConverterResponse(false, "", exceptionMessage);
	}

	@Override
	public ConverterResponse convertExMorphoDoc(String docPath) throws Exception {
		String uri = SERVER_URL + "convertExMorphoDoc";

		if (serverIsRunning(statusUri)) {
			String httpPostResult = postToConvert(uri, docPath);
			Gson gson = new Gson();
			return gson.fromJson(httpPostResult, ConverterResponse.class);
		} else
			return new ConverterResponse(false, "", exceptionMessage);
	}

	private String doGet(String uri) throws Exception {
		// use Apache HttpClient to connect to server
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(uri);
		
		HttpResponse response = client.execute(request);
		if (response.getStatusLine().getStatusCode() != 200)
			throw new Exception(response.getStatusLine().getReasonPhrase());
		
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		StringBuffer result = new StringBuffer();
		String line;
		while ((line = rd.readLine()) != null)
			result.append(line);
		
		return result.toString();
	}

	private String postToConvert(String uri, String docPath) throws Exception {
		String keyStr = "";
		if (uri.equals(SERVER_URL + "convertUxp"))
			keyStr = "uxp";
		if (uri.equals(SERVER_URL + "convertExMorphoDoc"))
			keyStr = "xml";

		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(uri);

		List<NameValuePair> uriParams = new ArrayList<NameValuePair>();
		uriParams.add(new BasicNameValuePair(keyStr, docPath));
		post.setEntity(new UrlEncodedFormEntity(uriParams));

		HttpResponse response = client.execute(post);
		if (response.getStatusLine().getStatusCode() != 200)
			throw new Exception(response.getStatusLine().getReasonPhrase());

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		StringBuffer result = new StringBuffer();
		String line;
		while ((line = rd.readLine()) != null)
			result.append(line);

		return result.toString();
	}
	
	private boolean serverIsRunning(String url) {
		try {
			URL serverUrl = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) serverUrl.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			return connection.getResponseCode() == 200;
		} catch (Exception e) {
			exceptionMessage = e.getMessage();
			return false;
		}
	}

}
