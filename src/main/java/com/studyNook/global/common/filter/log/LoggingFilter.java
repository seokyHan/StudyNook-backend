package com.studyNook.global.common.filter.log;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.JsonElement;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import org.springframework.http.HttpStatus.Series;
import com.studyNook.global.common.exception.RestResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static java.net.InetAddress.getLocalHost;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;
import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpStatus.Series.SUCCESSFUL;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoggingFilter extends OncePerRequestFilter {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private final ObjectMapper objectMapper;
    private static final List<String> SENSITIVE_PROPERTIES = List.of(
            "password"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        long traceId = new SecureRandom().nextLong();
        long abs = Math.abs(traceId);
        MDC.put("traceId", String.valueOf(abs));

        if(isAsyncDispatch(request)){
            filterChain.doFilter(request, response);
        }else{
            doFilterWrapped(
                    String.valueOf(abs),
                    isVisible(request.getContentType()) ? new RequestWrapper(request) : request,
                    new ResponseWrapper(response),
                    filterChain);
        }
        MDC.clear();

    }

    protected void doFilterWrapped(String traceId, HttpServletRequest request, ResponseWrapper response, FilterChain filterChain) throws ServletException, IOException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        var startedAt = LocalDateTime.now().format(dateTimeFormatter);
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e){
            log.info("filterWrapped err : {}", e.getStackTrace());
        } finally {
            stopWatch.stop();
            logRest(request, response, traceId, startedAt, stopWatch.getTotalTimeMillis());
            response.copyBodyToResponse();
        }
    }

    private String replaceSensitiveValue(String json) {
        JsonElement jsonElement = JsonParser.parseString(json);
        JsonObject asJsonObject = jsonElement.getAsJsonObject();

        for(String sensitiveProperty : SENSITIVE_PROPERTIES){
            asJsonObject.remove(sensitiveProperty);
            asJsonObject.addProperty(sensitiveProperty, "*******");
        }

        return asJsonObject.toString();
    }

    private String logPayloadRequest(String contentType, InputStream inputStream) throws IOException {
        if (isVisible(contentType)) {
            byte[] content = StreamUtils.copyToByteArray(inputStream);
            if (content.length > 0) {
                return new String(content);
            }
        }
        return "";
    }

    private String logPayloadResponse(InputStream inputStream) throws IOException {
        byte[] content = StreamUtils.copyToByteArray(inputStream);
        if(content.length > 0){
            return new String(content);
        }

        return "";
    }

    private boolean isVisible(String mediaTypeString){
        MediaType mediaType = MediaType.valueOf(mediaTypeString == null ? "application/json" : mediaTypeString);

        final List<MediaType> visibleTypes =Arrays.asList(MediaType.valueOf("text/*"),
                MediaType.APPLICATION_FORM_URLENCODED,
                MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
                MediaType.MULTIPART_FORM_DATA,
                MediaType.valueOf("application/*+json"),
                MediaType.valueOf("application/*+xml"));

        return visibleTypes.stream().anyMatch(visibleType -> visibleType.includes(mediaType));
    }

    private RestResponse getResponseObject(String responseJson){
        try{
            return objectMapper.readValue(responseJson, RestResponse.class);
        } catch (JsonProcessingException e){
            return new RestResponse("", "");
        }
    }

    private void logRest(HttpServletRequest request, ResponseWrapper response, String traceId, String startedAt, long durationMillis) {
        String clientIp = request.getRemoteAddr();
        String requestUri = request.getRequestURI();
        String agent = request.getHeader(USER_AGENT);
        String authorization = request.getHeader(AUTHORIZATION);
        String queryString = defaultIfBlank(request.getQueryString(), "");

        try {

            String requestPayload = logPayloadRequest(request.getContentType(), request.getInputStream());
            String body = replaceSensitiveValue(requestPayload);

            String responsePayload = logPayloadResponse(response.getContentInputStream());
            int status = response.getStatus();
            RestResponse responseObject = getResponseObject(responsePayload);

            Request.Header header = new Request.Header(StringUtils.replace(authorization, "Bearer ", ""));
            Request requestLog = new Request(requestUri, request.getMethod(), header, body, queryString);
            Response responseLog = new Response(status,
                    defaultIfBlank(responseObject.getCode(), ""),
                    defaultIfBlank(responseObject.getMessage(), ""), durationMillis, responsePayload);

            RestLog restLog = new RestLog(
                    traceId,
                    startedAt,
                    clientIp,
                    getLocalHost().getHostAddress(),
                    request.getLocalPort(),
                    agent,
                    request.getHeader(REFERER),
                    requestLog,
                    responseLog
            );

            log.info(objectMapper.writeValueAsString(restLog));
        } catch (IOException e) {
            log.error("e = [{}]", getStackTrace(e));
        }

    }


    record RestLog(String traceId, String timestamp, String clientIp, String hostIp, int hostPort, String userAgent, String referer, Request request, Response response) {}
    record Request(String url, String method, Header header, String body, String queryString) {
        record Header(@JsonProperty("Authorization") String authorization) {}
    }
    record Response(int status, String errorCode, String errorMessage, long duration, String payload) {}
}
