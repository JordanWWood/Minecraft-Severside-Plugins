package network.marble.dataaccesslayer.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NonNull;
import network.marble.dataaccesslayer.base.DataAccessLayer;
import network.marble.dataaccesslayer.entities.TokenResponse;
import network.marble.dataaccesslayer.managers.TimerManager;
import okhttp3.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.*;

public class Context {
    static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    static final MediaType wwwFormUrlencoded = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

    private static Context instance = null;

    @Getter
    private static OkHttpClient client = new OkHttpClient();//.newBuilder().addInterceptor(new LoggingInterceptor()).build();

    private static Future<String> accessToken = null;
    
    public String getBaseUrl(){
        return DataAccessLayer.getEnvironmentalVariable("APIAddress");
    }

    public boolean authenticationEnabled(){
        String result = DataAccessLayer.getEnvironmentalVariable("AUTHENTICATION");
        if (result == null || result.isEmpty()) result = "true";
        return Boolean.parseBoolean(result);
    }

    private Request.Builder getRequestBase() {
        if (authenticationEnabled()) {
            return new Request.Builder().header("Authorization", "Bearer "+getAccessToken());
        } else {
            return new Request.Builder();
        }
    }

    public Request getRequest(@NonNull String urlEndPoint) {
        String url = this.getBaseUrl() + urlEndPoint;
        return getRequestBase().url(url).build();
    }

    public Request putRequest(@NonNull String urlEndPoint, @NonNull String jsonData) {
        String url = this.getBaseUrl() + urlEndPoint;
        RequestBody body = RequestBody.create(JSON, jsonData);
        return getRequestBase().url(url).put(body).build();
    }

    public Request postRequest(@NonNull String urlEndPoint, @NonNull String jsonData) {
        String url = this.getBaseUrl() + urlEndPoint;
        RequestBody body = RequestBody.create(JSON, jsonData);
        return getRequestBase().url(url).post(body).build();
    }

    public Request deleteRequest(@NonNull String urlEndPoint) {
        String url = this.getBaseUrl() + urlEndPoint;
        return getRequestBase().url(url).delete().build();
    }

    public String executeRequest(Request request) {
        try (Response response = Context.client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            DataAccessLayer.instance.logger.severe("Content: executeRequest: "+e.getMessage());
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
                String url = DataAccessLayer.getEnvironmentalVariable("IDSURL");
                String id = DataAccessLayer.getEnvironmentalVariable("ClientId");
                String secret = DataAccessLayer.getEnvironmentalVariable("ClientSecret");
                String scope = DataAccessLayer.getEnvironmentalVariable("ClientScope");
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
                TokenResponse response = new ObjectMapper().readValue(strResponse, TokenResponse.class);
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
