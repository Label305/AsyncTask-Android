package com.label305.stan.http;

import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.Map;

public interface GetExecutor {

    /**
     * Execute a GET request on the url configured
     *
     * @return response data
     */
    public HttpResponse get(String url, Map<String, Object> headerData) throws IOException;

}
