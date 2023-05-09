package com.lets.kkiri.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.lets.kkiri.dto.member.KakaoInfoDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

@Service
public class KakaoService {
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String KAKAO_KEY;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String KAKAO_REDIRECT_URL;

    public KakaoInfoDto getKakaoInfo(String code) {
        String access_Token = "";
        String refresh_Token = "";
        Long refreshTokenExpiresIn = 0l;
        Long expiresIn = 0l;
        KakaoInfoDto kakaoUser = KakaoInfoDto.builder().build();
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=" + KAKAO_KEY); // TODO REST_API_KEY 입력
            sb.append("&redirect_uri=" + KAKAO_REDIRECT_URL); // TODO 인가코드 받은 redirect_uri 입력
            sb.append("&code=" + code);
            bw.write(sb.toString());
            bw.flush();

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

            //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonObject element = new Gson().fromJson(result, JsonObject.class);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();
            expiresIn = element.getAsJsonObject().get("expires_in").getAsLong();
            refreshTokenExpiresIn = element.getAsJsonObject().get("refresh_token_expires_in").getAsLong();

            kakaoUser.setAccessToken(access_Token);
            kakaoUser.setRefreshToken(refresh_Token);
            kakaoUser.setRefreshTokenExpiration(refreshTokenExpiresIn);
            kakaoUser.setExpiresIn(expiresIn);

            br.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return kakaoUser;
    }

    public HashMap<String, Object> getUserInfo (String accessToken) {

        //    요청하는 클라이언트마다 가진 정보가 다를 수 있기에 HashMap타입으로 선언
        HashMap<String, Object> userInfo = new HashMap<String, Object>();
        String reqURL = "https://kapi.kakao.com/v2/user/me";
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            //    요청에 필요한 Header에 포함될 내용
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);

            int responseCode = conn.getResponseCode();
            if(responseCode == 400) {
                userInfo.put("STATUS", 400);
                userInfo.put("MESSAGE", "INVALID ACCESS TOKEN");
                return userInfo;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

            JsonObject element = new Gson().fromJson(result, JsonObject.class);

            JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
            JsonObject kakao_account = element.getAsJsonObject().get("kakao_account").getAsJsonObject();

            String nickname = properties.getAsJsonObject().get("nickname").getAsString();
            String email = kakao_account.getAsJsonObject().get("email").getAsString();

            userInfo.put("accessToken", accessToken);
            userInfo.put("nickname", nickname);
            userInfo.put("email", email);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return userInfo;
    }
}