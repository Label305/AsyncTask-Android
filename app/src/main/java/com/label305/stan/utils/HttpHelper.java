package com.label305.stan.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.message.BasicNameValuePair;

import android.net.http.AndroidHttpClient;

/**
 * A helper class to execute GET, POST and PUT requests.
 */
public class HttpHelper {

	AndroidHttpClient mHttpClient = AndroidHttpClient.newInstance("Android");

    @Deprecated
    public HttpResponse post(String url, Map<String, Object> headerData, Map<String, Object> postData) throws IOException {
        UrlEncodedFormEntity postDataEntity = null;
        if(postData != null) {
            postDataEntity = new UrlEncodedFormEntity(convert(postData));
        }
        return post(url, headerData, postDataEntity);
    }

	/**
	 * Execute a POST request on the url configured
	 * 
	 * @return response data
	 */
	public HttpResponse post(String url, Map<String, Object> headerData, AbstractHttpEntity postDataEntity) throws IOException {
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

	@Deprecated
	public HttpResponse put(String url, Map<String, Object> headerData, Map<String, Object> putData) throws IOException {
        UrlEncodedFormEntity putDataEntity = null;
        if(putData != null) {
            putDataEntity = new UrlEncodedFormEntity(convert(putData));
        }
		return put(url, headerData, putDataEntity);
	}

    /**
     * Execute a PUT request on the url configured
     *
     * @return response data
     */
    public HttpResponse put(String url, Map<String, Object> headerData, AbstractHttpEntity putDataEntity ) throws IOException {
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
	 * Execute a DELETE request on the url configured
	 * 
	 * @return response data
	 */
	public HttpResponse delete(String url, Map<String, Object> headerData) throws IOException {
		HttpDelete httpDelete = new HttpDelete(url);
		Iterator<String> keys = headerData.keySet().iterator();
		Iterator<Object> values = headerData.values().iterator();

		while (keys.hasNext() && values.hasNext()) {
			httpDelete.setHeader(keys.next(), values.next().toString());
		}

		AndroidHttpClient.modifyRequestToAcceptGzipResponse(httpDelete);
		return mHttpClient.execute(httpDelete);
	}

    @Deprecated
    public HttpResponse delete(String url, Map<String, Object> headerData, Map<String, Object> deleteData) throws IOException {
        UrlEncodedFormEntity deleteDataEntity = null;
        if(deleteData != null) {
            deleteDataEntity = new UrlEncodedFormEntity(convert(deleteData));
        }
        return delete(url, headerData, deleteDataEntity);
    }

	/**
	 * Execute a DELETE request with body on the url configured
	 * 
	 * @return response data
	 */
	public HttpResponse delete(String url, Map<String, Object> headerData, AbstractHttpEntity deleteDataEntity) throws IOException {
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
	public HttpResponse get(String url, Map<String, Object> headerData) throws IOException {
		HttpGet httpGet = new HttpGet(url);
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

	public static List<NameValuePair> convert(Map<String, Object> data) {
		List<NameValuePair> results = new ArrayList<NameValuePair>();

		for (String key : data.keySet()) {
			results.add(new BasicNameValuePair(key, data.get(key).toString()));
		}

		return results;
	}
}
