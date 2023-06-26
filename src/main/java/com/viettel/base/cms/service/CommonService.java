/*
 * Unitel WebView, Mobile web applications system.
 * Copyright (C) 2021 Unitel, Star Telecom Co.Ltd.
 * mailto: unitellao AT unitel DOT com DOT la
 *
 * This interface is to define some abstract common methods.
 */
package com.viettel.base.cms.service;

import com.viettel.base.cms.dto.ResponseDTO;
import java.util.Map;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;


public interface CommonService {
    ResponseDTO getApiBase(HttpMethod method, String url, Object request, Map<String, String> headers)throws Exception;
    ResponseDTO getApiBodyFormData(HttpMethod method, String url, MultiValueMap<String, Object> request, Map<String, String> headers);
}
