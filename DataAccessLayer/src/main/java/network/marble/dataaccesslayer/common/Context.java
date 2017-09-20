package network.marble.dataaccesslayer.common;

import com.google.gson.Gson;
import lombok.Getter;
import network.marble.dataaccesslayer.base.DataAccessLayer;
import network.marble.dataaccesslayer.entities.TokenResponse;
import network.marble.dataaccesslayer.interceptors.LoggingInterceptor;
import network.marble.dataaccesslayer.managers.TimerManager;
import okhttp3.*;
import org.apache.commons.lang.CharEncoding;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.concurrent.*;

public class Context {
    static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    static final MediaType wwwFormUrlencoded = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

    private static Context instance = null;
    private boolean dev = System.getProperty("TESTNET") != null;

    @Getter
    private static OkHttpClient client = new OkHttpClient();//.newBuilder().addInterceptor(new LoggingInterceptor()).build();

    private static Future<String> accessToken = null;
    
    public String getBaseUrl(){
        return dev ? System.getProperty("APIAddress") : System.getenv("APIAddress");
    }

    private Request.Builder getRequestBase() {
        //return new Request.Builder().header("Authorization", "Bearer "+(dev ? System.getProperty("AccessToken") : System.getenv("AccessToken")));
        return new Request.Builder();//.header("Authorization", "Bearer "+getAccessToken());
    }

    public Request getRequest(String urlEndPoint) {
        String url = this.getBaseUrl() + urlEndPoint;
        Request request = getRequestBase().url(url).build();
        return request;
    }

    public Request putRequest(String urlEndPoint, String jsonData) {
        String url = this.getBaseUrl() + urlEndPoint;
        RequestBody body = RequestBody.create(JSON, jsonData);
        Request request = getRequestBase().url(url).put(body).build();
        return request;
    }

    public Request postRequest(String urlEndPoint, String jsonData) {
        String url = this.getBaseUrl() + urlEndPoint;
        RequestBody body = RequestBody.create(JSON, jsonData);
        Request request = getRequestBase().url(url).post(body).build();
        return request;
    }

    public Request deleteRequest(String urlEndPoint) {
        String url = this.getBaseUrl() + urlEndPoint;
        Request request = getRequestBase().url(url).delete().build();
        return request;
    }

    public String executeRequest(Request request) {
        try (Response response = Context.client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            return null;
        }
    }

    private String getAccessToken() {
        if (accessToken == null) clientCreditentalFlow();
        try {
            return accessToken.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            DataAccessLayer.instance.logger.severe("Failed to retrieve access token due to: "+e.getMessage());
            return "";
        }
    }

    private void clientCreditentalFlow() {
        accessToken = new CompletableFuture<>();
        Executors.newCachedThreadPool().submit(() -> {
            try {

                String url = dev ? System.getProperty("IDSURL") : System.getenv("IDSURL");
                String id = dev ? System.getProperty("ClientId") : System.getenv("ClientId");
                String secret = dev ? System.getProperty("ClientSecret") : System.getenv("ClientSecret");
                String scope = dev ? System.getProperty("ClientScope") : System.getenv("ClientScope");
                String strBody;
                try {
                    strBody = "client_id=" + id + "&client_secret=" + URLEncoder.encode(secret, "UTF-8") + "&grant_type=client_credentials&scope=" + scope;
                } catch (UnsupportedEncodingException e) {
                    DataAccessLayer.instance.logger.severe("Failed to get encode client secret with error: " + e.getMessage());
                    ((CompletableFuture)accessToken).completeExceptionally(new Exception("Failed to get encode client secret with error: " + e.getMessage()));
                    return null;
                }
                RequestBody body = RequestBody.create(wwwFormUrlencoded, strBody);
                Request request = new Request.Builder().url(url).post(body).build();
                String strResponse = executeRequest(request);
                DataAccessLayer.instance.logger.info("got token response: "+strResponse);
                TokenResponse response = (new Gson()).fromJson(strResponse, TokenResponse.class);
                if (response.getError() != null && response.getError().length() > 0) {
                    DataAccessLayer.instance.logger.severe("Failed to get access token with error: " + response.getError());
                    ((CompletableFuture)accessToken).completeExceptionally(new Exception("Failed to get access token with error: " + response.getError()));
                } else {
                    TimerManager.getInstance().runIn((timer, thing) -> clientCreditentalFlow(), response.getExpiresIn() - 10, TimeUnit.SECONDS);
                    DataAccessLayer.instance.logger.info("token: "+accessToken);
                    ((CompletableFuture)accessToken).complete(response.getAccessToken());
                }
            } catch(Exception e) {
                DataAccessLayer.instance.logger.severe("clientCreditentalFlow broke with "+ e.getMessage());
                ((CompletableFuture)accessToken).completeExceptionally(new Exception("clientCreditentalFlow broke with "+ e.getMessage()));
            }
            return null;
        });
    }

    public static Context getInstance(){
        if (instance == null) instance = new Context();
        return instance;
    }
}
