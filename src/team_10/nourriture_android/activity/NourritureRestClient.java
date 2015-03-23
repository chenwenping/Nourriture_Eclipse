package team_10.nourriture_android.activity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class NourritureRestClient {
    //private static final String BASE_URL = "http://5.196.19.84:1337/api/";
    private static final String BASE_URL = "http://176.31.191.185:1337/api/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void addHeader(String param) {
        client.addHeader("Authorization", param);
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void getWithLogin(String url, RequestParams params, String username, String password, AsyncHttpResponseHandler responseHandler) {
        client.setBasicAuth(username, password);
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void postWithLogin(String url, RequestParams params, String username, String password, AsyncHttpResponseHandler responseHandler) {
        client.setBasicAuth(username, password);
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void put(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.put(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void putWithLogin(String url, RequestParams params, String username, String password, AsyncHttpResponseHandler responseHandler) {
        client.setBasicAuth(username, password);
        client.put(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void delete(String url, AsyncHttpResponseHandler responseHandler) {
        client.delete(getAbsoluteUrl(url), responseHandler);
    }

    public static void deleteWithLogin(String url, String username, String password, AsyncHttpResponseHandler responseHandler) {
        client.setBasicAuth(username, password);
        client.delete(getAbsoluteUrl(url), responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}