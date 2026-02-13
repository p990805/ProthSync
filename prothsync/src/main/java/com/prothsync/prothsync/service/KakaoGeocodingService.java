package com.prothsync.prothsync.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Service
public class KakaoGeocodingService implements GeocodingService {

    private final WebClient kakaoWebClient;

    public KakaoGeocodingService(@Qualifier("kakaoWebClient") WebClient kakaoWebClient) {
        this.kakaoWebClient = kakaoWebClient;
    }

    @Override
    public GeocodingResult geocode(String address) {
        if (address == null || address.isBlank()) {
            log.warn("지오코딩 요청 주소가 비어있습니다.");
            return GeocodingResult.fail();
        }

        try {
            JsonNode response = kakaoWebClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/v2/local/search/address.json")
                    .queryParam("query", address)
                    .queryParam("analyze_type", "similar")
                    .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

            return parseResponse(response, address);

        } catch (WebClientResponseException e) {
            log.error("Kakao Geocoding API 호출 실패 - 상태코드: {}, 주소: {}",
                e.getStatusCode(), address);
            return GeocodingResult.fail();
        } catch (Exception e) {
            log.error("Kakao Geocoding API 호출 중 예외 발생 - 주소: {}", address, e);
            return GeocodingResult.fail();
        }
    }

    private GeocodingResult parseResponse(JsonNode response, String address) {
        if (response == null) {
            log.warn("Kakao Geocoding API 응답이 null입니다. 주소: {}", address);
            return GeocodingResult.fail();
        }

        JsonNode documents = response.get("documents");
        if (documents == null || documents.isEmpty()) {
            log.warn("Kakao Geocoding API 검색 결과가 없습니다. 주소: {}", address);
            return GeocodingResult.fail();
        }

        JsonNode firstResult = documents.get(0);
        // Kakao API에서 x = 경도(longitude), y = 위도(latitude)
        Double longitude = firstResult.get("x").asDouble();
        Double latitude = firstResult.get("y").asDouble();

        log.debug("지오코딩 성공 - 주소: {}, 위도: {}, 경도: {}", address, latitude, longitude);
        return GeocodingResult.success(latitude, longitude);
    }
}
