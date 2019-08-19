package task.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import task.grpc.service.WebResourceInfoServiceCustom;

import java.io.IOException;

public class MainServer {
    public static void main(String[] args) {
        try {
            Server server = start();
            server.awaitTermination();
        } catch (IOException | InterruptedException e) {
            System.out.println("Error with server. " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Server start() throws IOException {
        int port = 8080;

        Server server = ServerBuilder
                .forPort(port)
                .addService(new WebResourceInfoServiceCustom()).build()
                .start();
        System.out.println("Server started on port: " + port);
        registerShutdownHook(server);

        return server;
    }

    private static void registerShutdownHook(Server server) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("*** shutting down gRPC server since JVM is shutting down");
            if (server != null) {
                server.shutdown();
            }
            System.out.println("*** server shut down");
        }));
    }
}
