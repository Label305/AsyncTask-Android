package com.label305.stan.utils;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;

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
     * Execute a DELETE request on the url configured
     *
     * @return response data
     */
    public HttpResponse delete(String url, Map<String, String> headerData) throws IOException {
    	HttpDelete httpDelete = new HttpDelete(url);
        Iterator<String> keys = headerData.keySet().iterator();
        Iterator<String> values = headerData.values().iterator();

        while (keys.hasNext() && values.hasNext()) {
            httpDelete.setHeader(keys.next(), values.next());
        }

        AndroidHttpClient.modifyRequestToAcceptGzipResponse(httpDelete);
        return mHttpClient.execute(httpDelete);
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
    
    /* Checks if we have a valid Internet Connection on the device.
    * 
    * @param cxt
    * @return True if device has internet
    * 
    *         Code from: http://www.androidsnippets.org/snippets/131/
    */
    public static boolean hasInternet(Context cxt) {

    	NetworkInfo info = (NetworkInfo) ((ConnectivityManager) cxt.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

    	if (info == null || !info.isConnected()) {
    		return false;
    	}
    	if (info.isRoaming()) {
    		// here is the roaming option you can change it if you want to
    		// disable internet while roaming, just return false
    		return true;
    	}
    	return true;
    }

}
