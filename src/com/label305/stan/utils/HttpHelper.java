package com.label305.stan.utils;

import android.net.http.AndroidHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A helper class to execute GET, POST and PUT requests.
 */
public class HttpHelper {

    AndroidHttpClient mHttpClient = AndroidHttpClient.newInstance("Android");

    /**
     * Execute a POST request on the url configured
     *
     * @return response data
     */
    public HttpResponse post(String url, Map<String, String> headerData, List<NameValuePair> postData) throws IOException {
        HttpPost httpPost = new HttpPost(url);

        Iterator<String> keys = headerData.keySet().iterator();
        Iterator<String> values = headerData.values().iterator();

        while (keys.hasNext() && values.hasNext()) {
            httpPost.setHeader(keys.next(), values.next());
        }

        if (postData != null) {
            httpPost.setEntity(new UrlEncodedFormEntity(postData));
        }

        AndroidHttpClient.modifyRequestToAcceptGzipResponse(httpPost);

        return mHttpClient.execute(httpPost);
    }

    /**
     * Execute a PUT request on the url configured
     *
     * @return response data
     */
    public HttpResponse put(String url, Map<String, String> headerData, List<NameValuePair> putData) throws IOException {
        HttpPut httpPut = new HttpPut(url);
        Iterator<String> keys = headerData.keySet().iterator();
        Iterator<String> values = headerData.values().iterator();

        while (keys.hasNext() && values.hasNext()) {
            httpPut.setHeader(keys.next(), values.next());
        }

        if (putData != null) {
            httpPut.setEntity(new UrlEncodedFormEntity(putData));
        }

        AndroidHttpClient.modifyRequestToAcceptGzipResponse(httpPut);

        return mHttpClient.execute(httpPut);
    }

    /**
     * Execute a GET request on the url configured
     *
     * @return response data
     */
    public HttpResponse get(String url, Map<String, String> headerData) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        Iterator<String> keys = headerData.keySet().iterator();
        Iterator<String> values = headerData.values().iterator();

        while (keys.hasNext() && values.hasNext()) {
            httpGet.setHeader(keys.next(), values.next());
        }

        AndroidHttpClient.modifyRequestToAcceptGzipResponse(httpGet);
        return mHttpClient.execute(httpGet);
    }

    public void close() {
        mHttpClient.close();
    }

}