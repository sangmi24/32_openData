package com.kh.opendata.run;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kh.opendata.model.vo.AirVO;

public class AirPollutionTest {
	
	// 발급받은 인증키 정보를 변수에 담아두기
	// => 어차피 변할 일이 없기 때문에 상수필드로 구성
	public static final String SERVICEKEY = "Yr16XkfGx9Vy2lEjt4bQy%2BSYthnLjMGP%2BMz3T%2FfsiD%2FTpHcQewhjnP97drW0gBlWKg%2FXGl6AX%2BXRPVz7UKx8ew%3D%3D";
	
	// 공공데이터를 테스트하기위한 간단한 테스트 환경 조성 => JUnit (main 메소드를 만들 필요가 없음)
	// junit테스트 실행을 하기 위해서는 꼭 @Test로 명시를 해줘야함 
	@Test
	public void testRun() throws IOException {
		
		// OpenAPI 서버로 요청하고자 하는 url 주소 만들기
		String url = "http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty";
			   url += "?serviceKey=" + SERVICEKEY;
			   url += "&returnType=json";
			   url += "&sidoName=" + URLEncoder.encode("서울", "UTF-8"); // 요청시 전달값에 한글이 있을 경우 encoding 해야함
		
		// System.out.println(url);
		// 서비스키가 제대로 부여되지 않을 경우
		// SERVICE_KEY_IS_NOT_REGISTERED_ERROR 오류 발생
		
		// * HttpURLConnection 객체를 이용해서 OpenAPI 요청 절차
		// 1. 요청하고자 하는 url 주소값을 전달하면서 java.net.URL 객체 생성
		URL requestUrl = new URL(url);
		
		// 2. 생성된 URL 객체를 가지고 HttpURLConnection 객체 생성
		// 강제형변환(다운캐스팅) HttpURLConnection자식타입  / openConnection부모타입
		HttpURLConnection urlConnection = (HttpURLConnection)requestUrl.openConnection();
		
		// 3. 요청에 필요한 Header 설정하기 => GET 방식으로
		urlConnection.setRequestMethod("GET");
			   
		// 4. 해당 OpenAPI 서버로 요청을 보낸 후 스트림을 통해서 응답데이터 읽어들이기
		BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
		// BufferedReader 보조스트림은 절대 혼자 쓰일 수 없음!!
		// 기반스트림인 urlConnection.getInputStream() 은 바이트스트림(좁은 통로)
		// 보조스트림인 BufferedReader 는 문자스트림(넓은 통로)
		// => InputStreamReader 라는 1byte 짜리 통로를 2byte 짜리 통로로 맞춰주는 보조스트림 을 활용하면됨 
		
		// 5. 반복적으로 매번 응답데이터를 읽어들이기
		String line; // 기본적으로 null 이 담겨있음 (퐁당퐁당 안하기 위해서)
		
		String responseText = ""; //변수 . 응답데이터 기록용
		
		while((line = br.readLine()) != null) { //더이상 뽑을게 없나
			
			// System.out.println(br.readLine());
			// System.out.println(line);
			responseText += line;
		}
		
		// System.out.println(responseText);
		
		/*
		 * {
		 * 		"response":
		 * 			{
		 * 				"body":
		 * 					{
		 * 						"totalCount":40,
		 * 						"items":
		 * 							[
		 * 								{
		 * 									"so2Grade":"1",
		 * 									"coFlag":null,
		 * 									"khaiValue":"92",
		 * 									"so2Value":"0.003",
		 * 									"coValue":"0.4",
		 * 									"pm10Flag":null,
		 * 									"o3Grade":"2",
		 * 									"pm10Value":"25",
		 * 									"khaiGrade":"2",
		 * 									"sidoName":"서울",
		 * 									"no2Flag":null,
		 * 									"no2Grade":"1",
		 * 									"o3Flag":null,
		 * 									"so2Flag":null,
		 * 									"dataTime":"2022-05-29 14:00",
		 * 									"coGrade":"1",
		 * 									"no2Value":"0.011",
		 * 									"stationName":"중구",
		 * 									"pm10Grade":"1",
		 * 									"o3Value":"0.080"
		 * 								},
		 * 								{
		 * 									"so2Grade":"1",
		 * 									"coFlag":null,
		 * 									"khaiValue":"67",
		 * 									"so2Value":"0.003",
		 * 									"coValue":"0.4",
		 * 									"pm10Flag":null,
		 *								...
		 *								}
		 *							],
		 *						"pageNo":1,
		 *						"numOfRows":10
		 *					},
		 *				"header":
		 *					{
		 *						"resultMsg":"NORMAL_CODE",
		 *						"resultCode":"00"
		 *					}
		 *			}
		 *	}
		 */
		
		// JsonObject, JsonArray, JsonElement 를 이용해서 파싱할 수 있음  값추출
		// => gson 라이브러리에서 제공
		// 주의!! 기존의 JSONObject, JSONArray 랑은 다른 객체임 (json 라이브러리에서 제공하던것) 값가공
		
		// 각각의 item 정보들이 키-밸류 세트로 담겨있는 상태
		// => AirVO 로 가공 => ArrayList 로 차곡차곡 담기
		JsonObject totalObj = JsonParser.parseString(responseText).getAsJsonObject();
		// 전체 내용물 {} 추출
		
		// 전체 JSON 형식으로부터 response 라는 속성명에 접근해서 value 뽑기
		JsonObject responseObj = totalObj.getAsJsonObject("response");
		// response : {} <- 이 내용물이 추출될것임
		// System.out.println(responseObj);
		
		// response 속성값에 담긴 밸류로부터 body 속성명에 접근해서 value 뽑기
		JsonObject bodyObj = responseObj.getAsJsonObject("body");
		// body : {} <- 이 내용물이 추출될것임
		// System.out.println(bodyObj);
		
		// body 속성값에 담긴 밸류로부터 totalCount 속성명에 접근해서 value 뽑기
		int totalCount = bodyObj.get("totalCount").getAsInt();
		// totalCount : 40 <- 이 내용물이 추출될것임
		
		// body 속성값에 담긴 밸류로부터 items 속성명에 접근해서 value 뽑기
		JsonArray itemArr = bodyObj.getAsJsonArray("items");
		// items : [] <- 이 내용물이 추출될것임
		
		// System.out.println(totalCount);
		// System.out.println(itemArr);
		// [ item, item, item, .. ] <- items
		
		// JSON 타입을 자바의 AirVO, ArrayList 타입으로 가공하기
		ArrayList<AirVO> list = new ArrayList<>();
		
		for(int i = 0; i < itemArr.size(); i++) {
			
			JsonObject item = itemArr.get(i).getAsJsonObject();
			
			// System.out.println(item);
			
			AirVO air = new AirVO();
			air.setStationName(item.get("stationName").getAsString());
			air.setDataTime(item.get("dataTime").getAsString());
			air.setKhaiValue(item.get("khaiValue").getAsString());
			air.setPm10Value(item.get("pm10Value").getAsString());
			air.setSo2Value(item.get("so2Value").getAsString());
			air.setCoValue(item.get("coValue").getAsString());
			air.setNo2Value(item.get("no2Value").getAsString());
			air.setO3Value(item.get("o3Value").getAsString());
			
			list.add(air);
		}
		
		// ArrayList 에 담겨있는 AirVO 객체들 출력
		for(AirVO a : list) {
			
			System.out.println(a);
		}
		
		// 6. 다 사용한 스트림 반납 및 연결 끊기
		br.close();
		urlConnection.disconnect();
	}
}
