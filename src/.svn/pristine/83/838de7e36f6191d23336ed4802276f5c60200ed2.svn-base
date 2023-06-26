/*
 * Unitel WebView, Mobile web applications system.
 * Copyright (C) 2021 Unitel, Star Telecom Co.Ltd.
 * mailto: unitellao AT unitel DOT com DOT la
 *
 * This class is to implement the methods that defined in ICommons1 interface.
 */
package com.viettel.base.cms.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viettel.base.cms.dto.ResponseDTO;
import com.viettel.base.cms.service.CommonService;

import java.util.Map;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Service
public class CommonsServiceImpl implements CommonService {

  private final ObjectMapper mapper;

  public CommonsServiceImpl(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public ResponseDTO getApiBase(HttpMethod method, String url, Object request, Map<String, String> headers) throws Exception {
    HttpHeaders httpHeaders = new HttpHeaders();
    if (headers != null) {
      for (Map.Entry<String, String> entry : headers.entrySet()) {
        String key = entry.getKey();
        String value = entry.getValue();
        httpHeaders.set(key, value);
      }
    }
    HttpEntity<Object> httpEntity = new HttpEntity<>(request, httpHeaders);

    CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(new SSLContextBuilder().loadTrustMaterial(null, TrustAllStrategy.INSTANCE).build()).setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
    requestFactory.setHttpClient(httpClient);
    RestTemplate restTemplate = new RestTemplate(requestFactory);
    String error;
    try {
      if (method == HttpMethod.GET) {
        return restTemplate.exchange(url, HttpMethod.GET, httpEntity, ResponseDTO.class).getBody();
      } else if (method == HttpMethod.POST) {
        return restTemplate.exchange(url, HttpMethod.POST, httpEntity, ResponseDTO.class).getBody();
      } else if (method == HttpMethod.PUT) {
        return restTemplate.exchange(url, HttpMethod.PUT, httpEntity, ResponseDTO.class).getBody();
      } else {
        return null;
      }
    }catch (HttpStatusCodeException ex) {
      error = ex.getResponseBodyAsString();
      try {
        return mapper.readValue(error, ResponseDTO.class);
      } catch (JsonProcessingException e) {
        throw new IllegalArgumentException(e);
      }
    }
  }

  @Override
  public ResponseDTO getApiBodyFormData(HttpMethod method, String url, MultiValueMap<String, Object> request, Map<String, String> headers) {
    HttpHeaders httpHeaders = new HttpHeaders();
    if (headers != null) {
      for (Map.Entry<String, String> entry : headers.entrySet()) {
        String key = entry.getKey();
        String value = entry.getValue();
        httpHeaders.set(key, value);
      }
    }
    httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(request, httpHeaders);

    CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
    requestFactory.setHttpClient(httpClient);
    RestTemplate restTemplate = new RestTemplate(requestFactory);
    String error;
    try {
      if (method == HttpMethod.GET) {
        return restTemplate.exchange(url, HttpMethod.GET, requestEntity, ResponseDTO.class).getBody();
      } else if (method == HttpMethod.POST) {
        return restTemplate.exchange(url, HttpMethod.POST, requestEntity, ResponseDTO.class).getBody();
      } else if (method == HttpMethod.PUT) {
        return restTemplate.exchange(url, HttpMethod.PUT, requestEntity, ResponseDTO.class).getBody();
      } else {
        return null;
      }
    } catch (Exception ex) {
      throw ex;
    }
  }

}
