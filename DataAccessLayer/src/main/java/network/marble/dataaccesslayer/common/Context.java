package network.marble.dataaccesslayer.common;

import lombok.Getter;
import okhttp3.*;

import java.io.IOException;

public class Context {
    static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static Context instance = null;

    @Getter
    private static OkHttpClient client = new OkHttpClient();//.newBuilder().addInterceptor(new LoggingInterceptor()).build();
    
    public String getBaseUrl(){
        return "http://api.marble.network/";
    }

    public Request getRequest(String urlEndPoint) {
        String url = this.getBaseUrl() + urlEndPoint;
        Request request = new Request.Builder().url(url).build();
        return request;
    }

    public Request putRequest(String urlEndPoint, String jsonData) {
        String url = this.getBaseUrl() + urlEndPoint;
        RequestBody body = RequestBody.create(JSON, jsonData);
        Request request = new Request.Builder().url(url).put(body).build();
        return request;
    }

    public Request postRequest(String urlEndPoint, String jsonData) {
        String url = this.getBaseUrl() + urlEndPoint;
        RequestBody body = RequestBody.create(JSON, jsonData);
        Request request = new Request.Builder().url(url).post(body).build();
        return request;
    }

    public Request deleteRequest(String urlEndPoint) {
        String url = this.getBaseUrl() + urlEndPoint;
        Request request = new Request.Builder().url(url).delete().build();
        return request;
    }

    public String executeRequest(Request request) {
        try (Response response = Context.client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            return null;
        }
    }

    public static Context getInstance(){
        if (instance == null) instance = new Context();
        return instance;
    }
}
