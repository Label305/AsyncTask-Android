package com.label305.stan.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.Map;

public interface PostExecutor {

    /**
     * Execute a POST request on the url configured
     *
     * @return response data
     */
    public HttpResponse post(final String url, final Map<String, Object> headerData, final HttpEntity postDataEntity) throws IOException;

}
