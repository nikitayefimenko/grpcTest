package task.grpc;

import io.grpc.ManagedChannel;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import src.main.java.task.grpc.service.WebResourceInfo;
import src.main.java.task.grpc.service.WebResourceInfoServiceGrpc;
import src.main.java.task.grpc.service.WebResourceRequest;
import task.grpc.service.WebResourceInfoServiceCustom;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;


public class MainServerTest {

    private static final List<String> WEB_RESOURCES = Arrays.asList("https://www.google.com/",
            "http://www.youtube.com/", "http://www.yahoo.com/", "http://www.msn.com/", "http://www.amazon.com/",
            "https://www.linkedin.com/nhome/", "http://www.ebay.com/", "https://twitter.com/", "http://www.bing.com/",
            "http://westernmass.craigslist.org/", "http://wordpress.org/", "http://www.aol.com/", "http://www.ask.com/",
            "http://www.webmonkey.com/", "http://www.webdesignerdepot.com/", "http://www.w3schools.com/", "http://www.wikipedia.org/",
            "http://www.internet101.org/", "http://www.teachersfirst.com/");

    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();


    private ManagedChannel channel;

    @Before
    public void setUp() throws IOException {
        String serverName = InProcessServerBuilder.generateName();

        grpcCleanup.register(InProcessServerBuilder.forName(serverName).directExecutor()
                .addService(new WebResourceInfoServiceCustom()).build().start());

        channel = grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build());
    }


    @Test
    public void webResourceInfoServiceTest_singleBlocking() {
        WebResourceInfoServiceGrpc.WebResourceInfoServiceBlockingStub blockingStub = WebResourceInfoServiceGrpc.newBlockingStub(channel);

        WEB_RESOURCES.forEach(resource -> {
            WebResourceInfo reply = blockingStub.getWebResourceInfo(WebResourceRequest.newBuilder()
                    .setWebResource(resource)
                    .build());

            assertNotNull(reply);
        });
    }

    @Test
    public void webResourceInfoServiceTest_singleNonBlocking() {
        WebResourceInfoServiceGrpc.WebResourceInfoServiceStub nonBlockingStub = WebResourceInfoServiceGrpc.newStub(channel);

        StreamObserver<WebResourceInfo> streamObserver = createStreamObserver();
        WEB_RESOURCES.forEach(resource -> {
            nonBlockingStub.getWebResourceInfo(WebResourceRequest.newBuilder()
                    .setWebResource(resource)
                    .build(), streamObserver);
        });
    }


    @Test
    public void webResourceInfoServiceTest_multithreadingBlocking() {
        WebResourceInfoServiceGrpc.WebResourceInfoServiceBlockingStub blockingStub = WebResourceInfoServiceGrpc.newBlockingStub(channel);

        CompletableFuture[] asyncWebRequests = new CompletableFuture[WEB_RESOURCES.size()];
        for (int i = 0; i < WEB_RESOURCES.size(); i++) {
            String resource = WEB_RESOURCES.get(i);
            asyncWebRequests[i] = CompletableFuture.runAsync(() -> {
                WebResourceInfo reply = blockingStub.getWebResourceInfo(WebResourceRequest.newBuilder()
                        .setWebResource(resource)
                        .build());

                assertNotNull(reply);
            });
        }

        CompletableFuture<Void> mainFuture = CompletableFuture.allOf(asyncWebRequests);
        mainFuture.join();
    }

    @Test
    public void webResourceInfoServiceTest_multithreadingNonBlocking() {
        WebResourceInfoServiceGrpc.WebResourceInfoServiceStub nonBlockingStub = WebResourceInfoServiceGrpc.newStub(channel);

        StreamObserver<WebResourceInfo> streamObserver = createStreamObserver();

        CompletableFuture[] asyncWebRequests = new CompletableFuture[WEB_RESOURCES.size()];
        for (int i = 0; i < WEB_RESOURCES.size(); i++) {
            String resource = WEB_RESOURCES.get(i);
            asyncWebRequests[i] = CompletableFuture.runAsync(() -> {
                nonBlockingStub.getWebResourceInfo(WebResourceRequest.newBuilder()
                        .setWebResource(resource)
                        .build(), streamObserver);
            });
        }

        CompletableFuture<Void> mainFuture = CompletableFuture.allOf(asyncWebRequests);
        mainFuture.join();
    }

    private StreamObserver<WebResourceInfo> createStreamObserver() {
        return new StreamObserver<WebResourceInfo>() {
            @Override
            public void onNext(WebResourceInfo webResourceInfo) {
                System.out.println("Response from server ready to send. Response: " + webResourceInfo);
                assertNotNull(webResourceInfo);
            }

            @Override
            public void onError(Throwable throwable) {
                fail();
            }

            @Override
            public void onCompleted() {
                System.out.println("Finished with response\n");
            }
        };
    }
}
