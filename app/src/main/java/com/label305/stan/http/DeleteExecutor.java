package com.label305.stan.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.Map;

public interface DeleteExecutor {

    /**
     * Execute a DELETE request with body on the url configured
     *
     * @return response data
     */
    public HttpResponse delete(final String url, final Map<String, Object> headerData, final HttpEntity deleteDataEntity) throws IOException;
}
