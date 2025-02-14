package com.barowoori.foodpinbackend.document.infra.domain;

import com.barowoori.foodpinbackend.common.exception.CustomException;
import com.barowoori.foodpinbackend.config.factory.YamlPropertySourceFactory;
import com.barowoori.foodpinbackend.document.command.domain.exception.DocumentErrorCode;
import com.barowoori.foodpinbackend.document.command.domain.service.BusinessNumberValidator;
import com.barowoori.foodpinbackend.document.infra.domain.dto.BusinessInfo;
import com.barowoori.foodpinbackend.document.infra.domain.dto.BusinessResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@PropertySource(value = "classpath:secrets/odcloud-config.yml", factory = YamlPropertySourceFactory.class)
public class OdCloudBusinessNumberValidator implements BusinessNumberValidator {

    private final Logger LOGGER = LoggerFactory.getLogger(OdCloudBusinessNumberValidator.class);

    @Value("${odcloud.decodingKey}")
    private String decodingKey;

    public OdCloudBusinessNumberValidator() {
    }

    @Override
    public Boolean validate(String businessNumber, String representativeName, LocalDate openingDate) {
        WebClient webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .baseUrl("https://api.odcloud.kr/api/nts-businessman/v1")
                .build();
        try {
            BusinessResponse response = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/validate")
                            .queryParam("serviceKey", decodingKey)
                            .build())
                    .bodyValue(convertValidateRequestBody(businessNumber, representativeName, openingDate))
                    .retrieve()
                    .bodyToMono(BusinessResponse.class)
                    .block();
            LOGGER.info(businessNumber+" 사업자 진위 응답 데이터: "+ response.getStatus_code());

            return response.isValid();

        } catch (Exception e) {
            LOGGER.info("사업자등록증 진위여부 확인 API 에러 : " + e.getMessage());
            throw new CustomException(DocumentErrorCode.BUSINESS_REGISTRATION_VALIDATE_FAILED);
        }
    }

    private String convertValidateRequestBody(String businessNumber, String representativeName, LocalDate openingDate){
        try {
            Map<String, Object> body = new HashMap<>();
            List<BusinessInfo> businesses = new ArrayList<>();
            businesses.add(new BusinessInfo(businessNumber, representativeName, openingDate));
            body.put("businesses", businesses);

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(body);
        }catch (Exception e){
            throw new CustomException(DocumentErrorCode.BUSINESS_REGISTRATION_VALIDATE_FAILED, e.getMessage());
        }
    }
}
