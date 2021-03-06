package com.samm.restapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.samm.biz.FestivalBiz;
import com.samm.mapper.AreaMapper;
import com.samm.mapper.FestivalDetailMapper;
import com.samm.mapper.FestivalMapper;

@Controller
public class TourFestivalAPI {

	@Autowired
	AreaMapper dao;

	@Autowired
	FestivalMapper fdao;

	@Autowired
	FestivalDetailMapper fDdao;
	@Autowired
	FestivalBiz fbiz;

	// tag값의 정보를 가져오는 함수
	public static String getTagValue(String tag, Element eElement) {

		String result = "";

		if (eElement.getElementsByTagName(tag).item(0) == null) { // --->해당 호출된 XML데이터가 없을때
			result = null;
		} else {
			NodeList nlList = eElement.getElementsByTagName(tag).item(0).getChildNodes();
			if (nlList.item(0) == null) { // --> 호출된 XML 태그가 존재하지만 태그안 text가 없을때..
				result = null;
			} else { // --> 호출된 XML 태그안 text 꺼냄.
				result = nlList.item(0).getTextContent();
			}
		}
		return result;
	}

	public boolean insertFestivalApi() throws IOException {
		StringBuilder urlBuilder = new StringBuilder(
				"https://api.visitkorea.or.kr/openapi/service/rest/KorService/searchFestival"); /* 행사정보 URL */
		urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8")
				+ "=tfve0pUOnMXCq5%2F%2Fm0wkmz%2BoE2%2BWsgFwlecdUgsklVSER1UoydTgZG1ZaUOK%2FtsetQnRNi1TuOiEjM%2BHncD9qw%3D%3D"); // servicekey
		urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "="
				+ URLEncoder.encode("10000", "UTF-8")); /* 한 페이지 결과수 */
		urlBuilder.append(
				"&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /* 현재 페이지 번호 */
		urlBuilder.append("&" + URLEncoder.encode("MobileOS", "UTF-8") + "="
				+ URLEncoder.encode("ETC", "UTF-8")); /* IOS (아이폰), AND (안드로이드), WIN (원도우폰), ETC */
		urlBuilder.append("&" + URLEncoder.encode("MobileApp", "UTF-8") + "="
				+ URLEncoder.encode("AppTest", "UTF-8")); /* 서비스명=어플명 */
		urlBuilder.append("&" + URLEncoder.encode("arrange", "UTF-8") + "=" + URLEncoder.encode("A", "UTF-8"));

		urlBuilder.append("&" + URLEncoder.encode("listYN", "UTF-8") + "="
				+ URLEncoder.encode("Y", "UTF-8")); /* listYN 목록구분(Y=목록,N=개수) */
		urlBuilder.append("&" + URLEncoder.encode("eventStartDate", "UTF-8") + "="
				+ URLEncoder.encode("20220101", "UTF-8")); /* listYN 목록구분(Y=목록,N=개수) */

		URL url = new URL(urlBuilder.toString());
		String strURL = urlBuilder.toString();
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-type", "application/json");
		System.out.println("Response code: " + conn.getResponseCode());
		BufferedReader rd;
		if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		} else {
			rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
		}

		DocumentBuilderFactory dbFactoty = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactoty.newDocumentBuilder();
			Document doc = dBuilder.parse(strURL);

			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("item");

			int cnt = 0;
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				Element eElement = (Element) nNode;
				HashMap<String, String> hashmap = new HashMap<String, String>();

				hashmap.put("contentid", getTagValue("contentid", eElement));
				hashmap.put("title", getTagValue("title", eElement));
				hashmap.put("areacode", getTagValue("areacode", eElement));
				hashmap.put("eventstartdate", getTagValue("eventstartdate", eElement));
				hashmap.put("eventenddate", getTagValue("eventenddate", eElement));
				hashmap.put("tel", getTagValue("tel", eElement));
				hashmap.put("addr1", getTagValue("addr1", eElement));
				hashmap.put("mapx", getTagValue("mapx", eElement));
				hashmap.put("mapy", getTagValue("mapy", eElement));
				hashmap.put("firstimage", getTagValue("firstimage", eElement));
				hashmap.put("firstimage2", getTagValue("firstimage2", eElement));
				hashmap.put("createdtime", getTagValue("createdtime", eElement));
				cnt++;

				try {
					fdao.apiinsert(hashmap);

				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("cnt ::" + cnt);
				System.out.println(hashmap);

			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		rd.close();
		conn.disconnect();
		return true;
	}

	public boolean DetailInfoInsert() throws Exception {
		List<String> festivalID = null;
		try {
			festivalID = fbiz.getContentId();
		} catch (Exception e1) {
			e1.printStackTrace();
			throw new Exception("!!!!!!!!!SQL ERROR!!");
		}
		int cnt = 0;
		String apikey = "";

		for (String contentId : festivalID) {
			cnt++;
			if (cnt % 4 == 0) {
				apikey = "tfve0pUOnMXCq5%2F%2Fm0wkmz%2BoE2%2BWsgFwlecdUgsklVSER1UoydTgZG1ZaUOK%2FtsetQnRNi1TuOiEjM%2BHncD9qw%3D%3D";
			} else if (cnt % 4 == 1) {
				apikey = "ef66qL2r05NW7EdBh1eB98C%2BxrT35HALxay6h9dV9PqWOtHM%2BD6CtWlwqMnS4ZifsmsKSWHvBFBV9j4slyeRCw%3D%3D";
			} else if (cnt % 4 == 2) {
				apikey = "daFe2av2mANluZHrEgRotgYJfcr%2BNJpDzHNx53PoqBnCL97Xxd7yCqRvF507%2FxLykBOPWv8XYq%2BD2ZPelZgQtA%3D%3D";
			} else if (cnt % 4 == 3) {
				apikey = "teAxoJQn7ObRsL4pq%2FSxALPZ22zZR479%2ButF7fNk1BzwEZ9WPxoQk8%2FTtP2BJ1v9R7aXXT8Bz8wEt7gS9uJ81Q%3D%3D";
			}

			System.out.println("apikey:: " + apikey);
			System.out.println("count::" + cnt);
			StringBuilder urlBuilder = new StringBuilder(
					"https://api.visitkorea.or.kr/openapi/service/rest/KorService/detailInfo"); /* 반복정보 URL */
			urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + apikey); // servicekey
			urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "="
					+ URLEncoder.encode("10", "UTF-8")); /* 한 페이지 결과수 */
			urlBuilder.append(
					"&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /* 현재 페이지 번호 */
			urlBuilder.append("&" + URLEncoder.encode("MobileOS", "UTF-8") + "="
					+ URLEncoder.encode("ETC", "UTF-8")); /* IOS (아이폰), AND (안드로이드), WIN (원도우폰), ETC */
			urlBuilder.append("&" + URLEncoder.encode("MobileApp", "UTF-8") + "="
					+ URLEncoder.encode("AppTest", "UTF-8")); /* 서비스명=어플명 */
			/*
			 * 220803 날짜로 삭제됨. urlBuilder.append("&" + URLEncoder.encode("listYN", "UTF-8")
			 * + "=" + URLEncoder.encode("Y", "UTF-8")); listYN 목록구분(Y=목록,N=개수)
			 */
			urlBuilder.append("&" + URLEncoder.encode("contentId", "UTF-8") + "="
					+ URLEncoder.encode(contentId, "UTF-8")); /* contentID */
			urlBuilder.append("&" + URLEncoder.encode("contentTypeId", "UTF-8") + "="
					+ URLEncoder.encode("15", "UTF-8")); /* 축제 콘텐츠타입 = 15 */

			URL url = new URL(urlBuilder.toString());
			// String strURL = urlBuilder.toString();
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-type", "application/json");
			System.out.println("Response code: " + conn.getResponseCode());
			BufferedReader rd;
			if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
				rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			} else {
				rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			}

			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}

			/*
			 * DocumentBuilderFactory dbFactoty = DocumentBuilderFactory.newInstance();
			 * DocumentBuilder dBuilder;
			 */
			Document doc;
			try {
				if (sb.toString().indexOf("&#x1d") >= 0) {
					sb.toString().replace("&#x1d", "");
					String xmllist[] = sb.toString().split("&#x1d;");
					StringBuilder sb2 = new StringBuilder();
					for (String string : xmllist) {
						sb2.append(string);
					}
					doc = convertStringToXMLDocument(sb2.toString());
				} else {
					doc = convertStringToXMLDocument(sb.toString());
				}
				doc.getDocumentElement().normalize();
				NodeList nList = doc.getElementsByTagName("item");
				NodeList resultMsg = doc.getElementsByTagName("header");
				Node nNode3 = resultMsg.item(0);
				Element eElement3 = (Element) nNode3;
				System.out.println(resultMsg);
				Node nNode = nList.item(0);
				System.out.println("nNode::" + nNode);
				Node nNode2 = nList.item(1);
				System.out.println("nNode2::" + nNode2);
				System.out.println("ressult::" + getTagValue("resultMsg", eElement3));
				if (!getTagValue("resultMsg", eElement3).equals("OK")) {
					System.out.println(sb.toString());
					throw new Exception("!!!!!!!!!API ERROR!!");
				}
				Element eElement = (Element) nNode;
				Element eElement2 = (Element) nNode2;
				HashMap<String, String> hashmap = new HashMap<String, String>();
				if (nNode == null) {
					hashmap.put("contentid", contentId);
				} else if (nNode2 == null) {
					hashmap.put("contentid", getTagValue("contentid", eElement));
					hashmap.put("infoname", getTagValue("infoname", eElement));
					hashmap.put("infotext", getTagValue("infotext", eElement));
				} else {
					hashmap.put("contentid", getTagValue("contentid", eElement));
					hashmap.put("infoname", getTagValue("infoname", eElement));
					hashmap.put("infotext", getTagValue("infotext", eElement));
					hashmap.put("infoname2", getTagValue("infoname", eElement2));
					hashmap.put("infotext2", getTagValue("infotext", eElement2));
				}

				try {
					fDdao.infoInsert(hashmap);
				} catch (Exception e) {
					e.printStackTrace();
					throw new Exception("!!!!!!!!!SQL ERROR!!");
				}

				System.out.println("festival::" + hashmap);

			} catch (IOException e) {
				e.printStackTrace();
				throw new Exception("!!!!!!!!!ERROR!!");
			}
			rd.close();
			conn.disconnect();
		}
		return true;
	}

	public boolean DetailCommonInsert() throws Exception {

		List<String> festivalID = null;
		try {
			festivalID = fbiz.getContentId();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			int cnt = 0;
			String apikey = "";
			for (String contentId : festivalID) {
				System.out.println("contentId::" + contentId);
				cnt++;
				if (cnt % 4 == 0) {
					apikey = "tfve0pUOnMXCq5%2F%2Fm0wkmz%2BoE2%2BWsgFwlecdUgsklVSER1UoydTgZG1ZaUOK%2FtsetQnRNi1TuOiEjM%2BHncD9qw%3D%3D";
				} else if (cnt % 4 == 1) {
					apikey = "ef66qL2r05NW7EdBh1eB98C%2BxrT35HALxay6h9dV9PqWOtHM%2BD6CtWlwqMnS4ZifsmsKSWHvBFBV9j4slyeRCw%3D%3D";
				} else if (cnt % 4 == 2) {
					apikey = "daFe2av2mANluZHrEgRotgYJfcr%2BNJpDzHNx53PoqBnCL97Xxd7yCqRvF507%2FxLykBOPWv8XYq%2BD2ZPelZgQtA%3D%3D";
				} else if (cnt % 4 == 3) {
					apikey = "teAxoJQn7ObRsL4pq%2FSxALPZ22zZR479%2ButF7fNk1BzwEZ9WPxoQk8%2FTtP2BJ1v9R7aXXT8Bz8wEt7gS9uJ81Q%3D%3D";
				}

				System.out.println("apikey:: " + apikey);
				System.out.println("count::" + cnt);

				StringBuilder urlBuilder = new StringBuilder(
						"https://api.visitkorea.or.kr/openapi/service/rest/KorService/detailCommon"); /* 공통정보 URL */
				urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + apikey); // servicekey
				urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "="
						+ URLEncoder.encode("10", "UTF-8")); /* 한 페이지 결과수 */
				urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "="
						+ URLEncoder.encode("1", "UTF-8")); /* 현재 페이지 번호 */
				urlBuilder.append("&" + URLEncoder.encode("MobileOS", "UTF-8") + "="
						+ URLEncoder.encode("ETC", "UTF-8")); /* IOS (아이폰), AND (안드로이드), WIN (원도우폰), ETC */
				urlBuilder.append("&" + URLEncoder.encode("MobileApp", "UTF-8") + "="
						+ URLEncoder.encode("AppTest", "UTF-8")); /* 서비스명=어플명 */
				urlBuilder.append("&" + URLEncoder.encode("defaultYN", "UTF-8") + "="
						+ URLEncoder.encode("Y", "UTF-8")); /* 기본정보 조회여부 */
				urlBuilder.append("&" + URLEncoder.encode("firstImageYN", "UTF-8") + "="
						+ URLEncoder.encode("Y", "UTF-8")); /* 원본, 썸네일 대표이미지 조회여부 */
				urlBuilder.append("&" + URLEncoder.encode("areacodeYN", "UTF-8") + "="
						+ URLEncoder.encode("Y", "UTF-8")); /* 지역코드, 시군구코드 조회여부 */
				urlBuilder.append("&" + URLEncoder.encode("catcodeYN", "UTF-8") + "="
						+ URLEncoder.encode("Y", "UTF-8")); /* 대,중,소분류코드 조회여부 */
				urlBuilder.append("&" + URLEncoder.encode("addrinfoYN", "UTF-8") + "="
						+ URLEncoder.encode("Y", "UTF-8")); /* 주소, 상세주소 조회여부 */
				urlBuilder.append("&" + URLEncoder.encode("mapinfoYN", "UTF-8") + "="
						+ URLEncoder.encode("Y", "UTF-8")); /* 좌표 X,Y 조회여부 */
				urlBuilder.append("&" + URLEncoder.encode("overviewYN", "UTF-8") + "="
						+ URLEncoder.encode("Y", "UTF-8")); /* 콘텐츠 개요 조회여부 */

				urlBuilder.append("&" + URLEncoder.encode("contentId", "UTF-8") + "="
						+ URLEncoder.encode(contentId, "UTF-8")); /* contentID */
				urlBuilder.append("&" + URLEncoder.encode("contentTypeId", "UTF-8") + "="
						+ URLEncoder.encode("15", "UTF-8")); /* 축제 콘텐츠타입 = 15 */

				URL url = new URL(urlBuilder.toString());
				String strURL = urlBuilder.toString();
				System.out.println("strURL::" + strURL);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Content-type", "application/json");
				System.out.println("Response code: " + conn.getResponseCode());
				BufferedReader rd;
				if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
					rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				} else {
					rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
				}
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = rd.readLine()) != null) {
					sb.append(line);
				}
				System.out.println("전체XML::" + sb.toString());
				/*
				 * DocumentBuilderFactory dbFactoty = DocumentBuilderFactory.newInstance();
				 * DocumentBuilder dBuilder;
				 */
				Document doc;
				try {
					if (sb.toString().indexOf("&#x1d") >= 0) {
						sb.toString().replace("&#x1d", "");
						String xmllist[] = sb.toString().split("&#x1d;");
						StringBuilder sb2 = new StringBuilder();
						for (String string : xmllist) {
							sb2.append(string);
						}
						doc = convertStringToXMLDocument(sb2.toString());
					} else {
						doc = convertStringToXMLDocument(sb.toString());
					}
					doc.getDocumentElement().normalize();
					NodeList nList = doc.getElementsByTagName("item");
					NodeList resultMsg = doc.getElementsByTagName("header");
					Node nNode3 = resultMsg.item(0);
					Element eElement3 = (Element) nNode3;
					System.out.println("ressult::" + getTagValue("resultMsg", eElement3));
					if (!getTagValue("resultMsg", eElement3).equals("OK")) {
						System.out.println("전체XML::" + sb.toString());
						throw new Exception("!!!!!!!!!API ERROR!!");
					}

					for (int temp = 0; temp < nList.getLength(); temp++) {
						Node nNode = nList.item(temp);
						Element eElement = (Element) nNode;
						HashMap<String, String> hashmap = new HashMap<String, String>();

						hashmap.put("contentid", getTagValue("contentid", eElement));
						hashmap.put("createdtime", getTagValue("createdtime", eElement));
						hashmap.put("addr1", getTagValue("addr1", eElement));
						hashmap.put("areacode", getTagValue("areacode", eElement));
						hashmap.put("firstimage", getTagValue("firstimage", eElement));
						hashmap.put("firstimage2", getTagValue("firstimage2", eElement));
						hashmap.put("homepage", getTagValue("homepage", eElement));
						hashmap.put("mapx", getTagValue("mapx", eElement));
						hashmap.put("mapy", getTagValue("mapy", eElement));
						hashmap.put("modifiedtime", getTagValue("modifiedtime", eElement));
						hashmap.put("overview", getTagValue("overview", eElement));
						hashmap.put("tel", getTagValue("tel", eElement));
						hashmap.put("telname", getTagValue("telname", eElement));
						hashmap.put("title", getTagValue("title", eElement));
						hashmap.put("zipcode", getTagValue("zipcode", eElement));
						hashmap.put("sigungucode", getTagValue("sigungucode", eElement));

						System.out.println("festival::  " + hashmap);

						try {
							fDdao.commonInsert(hashmap);

						} catch (Exception e) {
							e.printStackTrace();
							throw new Exception("!!!!!!!!!SQL ERROR!!!!!!!!!");
						}
					}

				} catch (ParserConfigurationException e) {
					e.printStackTrace();
					throw new Exception("!!!!!!!!!ERROR!!!!!!!!!");
				} catch (SAXException e) {
					e.printStackTrace();
					throw new Exception("!!!!!!!!!ERROR!!!!!!!!!");
				}

				rd.close();
				conn.disconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("!!!!!!!!!ERROR!!!!!!!!!");
		}

		return true;

	}

	public boolean DetailIntroInsert() throws Exception {

		List<String> festivalID = null;
		try {
			festivalID = fbiz.getContentId();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
			int cnt = 0;
			String apikey = "tfve0pUOnMXCq5%2F%2Fm0wkmz%2BoE2%2BWsgFwlecdUgsklVSER1UoydTgZG1ZaUOK%2FtsetQnRNi1TuOiEjM%2BHncD9qw%3D%3D";
			for (String contentId : festivalID) {
				cnt++;
				if (cnt % 4 == 0) {
					apikey = "tfve0pUOnMXCq5%2F%2Fm0wkmz%2BoE2%2BWsgFwlecdUgsklVSER1UoydTgZG1ZaUOK%2FtsetQnRNi1TuOiEjM%2BHncD9qw%3D%3D";
				} else if (cnt % 4 == 1) {
					apikey = "ef66qL2r05NW7EdBh1eB98C%2BxrT35HALxay6h9dV9PqWOtHM%2BD6CtWlwqMnS4ZifsmsKSWHvBFBV9j4slyeRCw%3D%3D";
				} else if (cnt % 4 == 2) {
					apikey = "daFe2av2mANluZHrEgRotgYJfcr%2BNJpDzHNx53PoqBnCL97Xxd7yCqRvF507%2FxLykBOPWv8XYq%2BD2ZPelZgQtA%3D%3D";
				} else if (cnt % 4 == 3) {
					apikey = "teAxoJQn7ObRsL4pq%2FSxALPZ22zZR479%2ButF7fNk1BzwEZ9WPxoQk8%2FTtP2BJ1v9R7aXXT8Bz8wEt7gS9uJ81Q%3D%3D";
				}
				System.out.println("apikey:: " + apikey);
				System.out.println("count::" + cnt);
				StringBuilder urlBuilder = new StringBuilder(
						"https://api.visitkorea.or.kr/openapi/service/rest/KorService/detailIntro"); /* 소개정보 URL */
				urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + apikey); // servicekey
				urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "="
						+ URLEncoder.encode("10", "UTF-8")); /* 한 페이지 결과수 */
				urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "="
						+ URLEncoder.encode("1", "UTF-8")); /* 현재 페이지 번호 */
				urlBuilder.append("&" + URLEncoder.encode("MobileOS", "UTF-8") + "="
						+ URLEncoder.encode("ETC", "UTF-8")); /* IOS (아이폰), AND (안드로이드), WIN (원도우폰), ETC */
				urlBuilder.append("&" + URLEncoder.encode("MobileApp", "UTF-8") + "="
						+ URLEncoder.encode("AppTest", "UTF-8")); /* 서비스명=어플명 */
				/*
				 * 220803 기준 해당 파라미터 삭제됨. urlBuilder.append("&" + URLEncoder.encode("listYN",
				 * "UTF-8") + "=" + URLEncoder.encode("Y", "UTF-8")); listYN 목록구분(Y=목록,N=개수)
				 */
				urlBuilder.append("&" + URLEncoder.encode("contentId", "UTF-8") + "="
						+ URLEncoder.encode(contentId, "UTF-8")); /* contentID */
				urlBuilder.append("&" + URLEncoder.encode("contentTypeId", "UTF-8") + "="
						+ URLEncoder.encode("15", "UTF-8")); /* 축제 콘텐츠타입 = 15 */

				URL url = new URL(urlBuilder.toString());
				// String strURL = urlBuilder.toString();
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Content-type", "application/json");
				System.out.println("Response code: " + conn.getResponseCode());
				BufferedReader rd;
				if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
					rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				} else {
					rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
				}

				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = rd.readLine()) != null) {
					sb.append(line);
				}

				/*
				 * DocumentBuilderFactory dbFactoty = DocumentBuilderFactory.newInstance();
				 * DocumentBuilder dBuilder;
				 */
				Document doc;
				try {
					if (sb.toString().indexOf("&#x1d") >= 0) {
						sb.toString().replace("&#x1d", "");
						String xmllist[] = sb.toString().split("&#x1d;");
						StringBuilder sb2 = new StringBuilder();
						for (String string : xmllist) {
							sb2.append(string);
						}
						doc = convertStringToXMLDocument(sb2.toString());
					} else {
						doc = convertStringToXMLDocument(sb.toString());
					}
					NodeList nList = doc.getElementsByTagName("item");
					NodeList resultMsg = doc.getElementsByTagName("header");
					Node nNode3 = resultMsg.item(0);
					Element eElement3 = (Element) nNode3;
					System.out.println("ressult::" + getTagValue("resultMsg", eElement3));
					if (!getTagValue("resultMsg", eElement3).equals("OK")) {
						System.out.println("전체XML::" + sb.toString());
						throw new Exception("!!!!!!!!!API ERROR!!");
					}

					for (int temp = 0; temp < nList.getLength(); temp++) {
						Node nNode = nList.item(temp);
						Element eElement = (Element) nNode;

						HashMap<String, String> hashmap = new HashMap<String, String>();
						hashmap.put("contentid", getTagValue("contentid", eElement));
						hashmap.put("agelimit", getTagValue("agelimit", eElement));
						hashmap.put("eventplace", getTagValue("eventplace", eElement));
						hashmap.put("eventstartdate", getTagValue("eventstartdate", eElement));
						hashmap.put("eventenddate", getTagValue("eventenddate", eElement));
						hashmap.put("festivaltype", getTagValue("festivaltype", eElement));
						hashmap.put("bookingplace", getTagValue("bookingplace", eElement));
						hashmap.put("placeinfo", getTagValue("placeinfo", eElement));
						hashmap.put("playtime", getTagValue("playtime", eElement));
						hashmap.put("program", getTagValue("program", eElement));
						hashmap.put("progresstype", getTagValue("progresstype", eElement));
						hashmap.put("spendtimefestival", getTagValue("spendtimefestival", eElement));
						hashmap.put("sponsor1", getTagValue("sponsor1", eElement));
						hashmap.put("sponsor1tel", getTagValue("sponsor1tel", eElement));
						hashmap.put("sponsor2", getTagValue("sponsor2", eElement));
						hashmap.put("sponsor1tel2", getTagValue("sponsor1tel2", eElement));
						hashmap.put("subevent", getTagValue("subevent", eElement));
						hashmap.put("usetimefestival", getTagValue("usetimefestival", eElement));
						System.out.println("festival ::" + hashmap);

						try {
							fDdao.introInsert(hashmap);
						} catch (Exception e) {
							e.printStackTrace();
							throw new Exception("!!!!!!!!!SQL ERROR!!!!!!!!!");
						}

					}
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
					throw new Exception("!!!!!!!!!ERROR!!!!!!!!!");
				} catch (SAXException e) {
					e.printStackTrace();
					throw new Exception("!!!!!!!!!ERROR!!!!!!!!!");
				}

				rd.close();
				conn.disconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("!!!!!!!!!API전체에러!!!!!!!!!");
		}
		return true;
	}

	private static Document convertStringToXMLDocument(String xmlString) {
		// Parser that produces DOM object trees from XML content
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		// API to obtain DOM Document instance
		DocumentBuilder builder = null;
		try {
			// Create DocumentBuilder with default configuration
			builder = factory.newDocumentBuilder();

			// Parse the content to Document object
			Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
