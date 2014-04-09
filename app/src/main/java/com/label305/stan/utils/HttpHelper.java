package com.label305.stan.utils;

import android.net.http.AndroidHttpClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A helper class to execute GET, POST and PUT requests.
 * @deprecated use {@link com.label305.stan.http.HttpHelper} instead.
 */
@Deprecated
public class HttpHelper {

    private static final String ANDROID = "Android";

    private final AndroidHttpClient mHttpClient = AndroidHttpClient.newInstance(ANDROID);

    public static List<NameValuePair> convert(final Map<String, Object> data) {
        List<NameValuePair> results = new ArrayList<NameValuePair>();

        for (final Map.Entry<String, Object> stringObjectEntry : data.entrySet()) {
            results.add(new BasicNameValuePair(stringObjectEntry.getKey(), stringObjectEntry.getValue().toString()));
        }

        return results;
    }

    /**
     * Execute a POST request on the url configured
     *
     * @return response data
     */
    public HttpResponse post(final String url, final Map<String, Object> headerData, final HttpEntity postDataEntity) throws IOException {
        HttpPost httpPost = new HttpPost(url);

        Iterator<String> keys = headerData.keySet().iterator();
        Iterator<Object> values = headerData.values().iterator();

        while (keys.hasNext() && values.hasNext()) {
            httpPost.setHeader(keys.next(), values.next().toString());
        }

        if (postDataEntity != null) {
            httpPost.setEntity(postDataEntity);
        }

        AndroidHttpClient.modifyRequestToAcceptGzipResponse(httpPost);

        return mHttpClient.execute(httpPost);
    }

    /**
     * Execute a PUT request on the url configured
     *
     * @return response data
     */
    public HttpResponse put(final String url, final Map<String, Object> headerData, final HttpEntity putDataEntity) throws IOException {
        HttpPut httpPut = new HttpPut(url);
        Iterator<String> keys = headerData.keySet().iterator();
        Iterator<Object> values = headerData.values().iterator();

        while (keys.hasNext() && values.hasNext()) {
            httpPut.setHeader(keys.next(), values.next().toString());
        }

        if (putDataEntity != null) {
            httpPut.setEntity(putDataEntity);
        }

        AndroidHttpClient.modifyRequestToAcceptGzipResponse(httpPut);

        return mHttpClient.execute(httpPut);
    }

    /**
     * Execute a DELETE request with body on the url configured
     *
     * @return response data
     */
    public HttpResponse delete(final String url, final Map<String, Object> headerData, final HttpEntity deleteDataEntity) throws IOException {
        HttpDeleteWithBody httpDelete = new HttpDeleteWithBody(url);
        Iterator<String> keys = headerData.keySet().iterator();
        Iterator<Object> values = headerData.values().iterator();

        while (keys.hasNext() && values.hasNext()) {
            httpDelete.setHeader(keys.next(), values.next().toString());
        }

        if (deleteDataEntity != null) {
            httpDelete.setEntity(deleteDataEntity);
        }

        AndroidHttpClient.modifyRequestToAcceptGzipResponse(httpDelete);
        return mHttpClient.execute(httpDelete);
    }

    /**
     * Execute a GET request on the url configured
     *
     * @return response data
     */
    public HttpResponse get(final String url, final Map<String, Object> headerData) throws IOException {
        HttpUriRequest httpGet = new HttpGet(url);
        Iterator<String> keys = headerData.keySet().iterator();
        Iterator<Object> values = headerData.values().iterator();

        while (keys.hasNext() && values.hasNext()) {
            httpGet.setHeader(keys.next(), values.next().toString());
        }

        AndroidHttpClient.modifyRequestToAcceptGzipResponse(httpGet);
        return mHttpClient.execute(httpGet);
    }

    public void close() {
        mHttpClient.close();
    }
}
