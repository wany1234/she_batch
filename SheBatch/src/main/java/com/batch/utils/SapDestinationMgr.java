package com.batch.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import org.apache.ibatis.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.conn.jco.ext.DestinationDataProvider;

public class SapDestinationMgr {
	private static final Logger logger = LoggerFactory.getLogger(SapDestinationMgr.class);

	static String ABAP_AS = "ABAP_AS_WITHOUT_POOL"; // sap 연결명(연결파일명으로 사용됨)

	public static void start() throws Exception {

		// TODO Auto-generated method stub
		logger.info("#### SAP CONNECTION start ####");

		// 서버 확인
		Properties properties = new Properties();
		String resource = "application.properties";
		Reader reader = Resources.getResourceAsReader(resource);
		properties.load(reader);

		String active = properties.getProperty("spring.profiles.active");

		// 서버 별 sap config 정보 확인
		resource = "sapconfig-" + active + ".properties";
		reader = Resources.getResourceAsReader(resource);
		properties.load(reader);

		// 연결프로퍼티 생성
		Properties connectProperties = new Properties();

		connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST,
				properties.getProperty("sap.destination.ashost")); // SAP호스트정보
		connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR,
				properties.getProperty("sap.destination.sysnr")); // 인스턴스번호
		connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT,
				properties.getProperty("sap.destination.client")); // SAP클라이언트
		connectProperties.setProperty(DestinationDataProvider.JCO_USER, properties.getProperty("sap.destination.user")); // SAP유저명
		connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD,
				properties.getProperty("sap.destination.passwd")); // SAP패스워드
		connectProperties.setProperty(DestinationDataProvider.JCO_LANG, properties.getProperty("sap.destination.lang")); // 언어

		// 프로퍼티를 이용하여 연결파일을 생성.
		// 실행되고 있는 응용시스템 경로에 생성됨.z
		createDestinationDataFile(ABAP_AS, connectProperties);
	}

	// sap 연결파일 생성
	public static void createDestinationDataFile(String destinationName, Properties connectProperties) throws IOException {
		File destCfg = new File(destinationName + ".jcoDestination");
		FileOutputStream fos = null;
		if (!destCfg.exists()) {
			try {
				fos = new FileOutputStream(destCfg, false);
				connectProperties.store(fos, "for tests only !");
			} catch (IOException e) {
				logger.error(e.getMessage());
			} catch (Exception e) {
				logger.error(e.getMessage());
				throw new RuntimeException("Unable to create the destination files", e);
			} finally {
				if (fos != null) {
					fos.close();
				}
			}
		}
	}

}
