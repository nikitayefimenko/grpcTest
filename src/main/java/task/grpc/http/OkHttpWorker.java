package task.grpc.http;

import okhttp3.*;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;


public class OkHttpWorker {

    private OkHttpClient defaultHttpClient;


    private OkHttpWorker() {
        init();
    }

    public static OkHttpWorker gI() {
        return OkHttpWorkerHolder.INSTANCE;
    }

    private void init() {
        defaultHttpClient = new OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .retryOnConnectionFailure(false)
                .connectTimeout(2, TimeUnit.SECONDS)
                .protocols(Collections.singletonList(Protocol.HTTP_1_1))
                .readTimeout(2, TimeUnit.SECONDS)
                .writeTimeout(2, TimeUnit.SECONDS).build();

    }

    private static class OkHttpWorkerHolder {

        private static final OkHttpWorker INSTANCE = new OkHttpWorker();
    }


    public OkHttpClient getDefaultHttpClient() {
        return defaultHttpClient;
    }


    public Response get(String url) throws IOException {
        Request req = new Request.Builder().url(url).get().build();
        return defaultHttpClient.newCall(req).execute();
    }

}
