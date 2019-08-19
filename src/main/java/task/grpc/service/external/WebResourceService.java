package task.grpc.service.external;

import okhttp3.Response;
import src.main.java.task.grpc.service.WebResourceInfo;
import task.grpc.exception.WebResourceException;
import task.grpc.http.OkHttpWorker;


public class WebResourceService {
    private static WebResourceService ourInstance = new WebResourceService();

    public static WebResourceService gI() {
        return ourInstance;
    }

    private WebResourceService() {
    }

    public WebResourceInfo getWebResourceInfo(String webResourceUrl) throws WebResourceException {
        StringBuilder logInfo = new StringBuilder("Sending request to ").append(webResourceUrl).append("...");

        try (Response response = sendRequestToWebResource(webResourceUrl)) {
            int code = response.code();
            long executionTime = response.receivedResponseAtMillis() - response.sentRequestAtMillis();
            logInfo.append("\nReceived response from ").append(webResourceUrl)
                    .append(", code=").append(code)
                    .append(", time=").append(executionTime)
                    .append("\n");

            return WebResourceInfo.newBuilder()
                    .setCode(code)
                    .setTime(executionTime)
                    .build();
        } finally {
            System.out.println(logInfo.toString());
        }
    }

    private Response sendRequestToWebResource(String webResourceUrl) throws WebResourceException {
        try {
            return OkHttpWorker.gI().get(webResourceUrl);
        } catch (Exception e) {
            throw new WebResourceException("Error communication with webResource.", e);
        }
    }


}
