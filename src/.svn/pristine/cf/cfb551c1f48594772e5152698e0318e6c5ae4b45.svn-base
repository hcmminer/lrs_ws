package com.viettel.base.cms.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
class TestResponseBodyAdvice<T> implements ResponseBodyAdvice<T> {


    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public T beforeBodyWrite(T body, MethodParameter returnType, MediaType selectedContentType,
                             Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
//        if (DataUtils.traceLogs != null && DataUtils.traceLogs.size() > 0){
//            traceLogsRepo.saveAll(DataUtils.traceLogs);
////            for (TraceLogs traceLog : DataUtils.traceLogs){
////                traceLogsRepo.save(traceLog);
////            }
//            response.getHeaders().set("trace-id",DataUtils.traceLogs.get(0).getTraceId().toString());
//        }

        return body;
    }

}
