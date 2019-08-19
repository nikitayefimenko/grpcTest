package task.grpc.service;

import io.grpc.stub.StreamObserver;
import src.main.java.task.grpc.service.WebResourceInfo;
import src.main.java.task.grpc.service.WebResourceInfoServiceGrpc;
import src.main.java.task.grpc.service.WebResourceRequest;
import task.grpc.exception.BaseException;
import task.grpc.exception.ValidationException;
import task.grpc.service.external.WebResourceService;

public class WebResourceInfoServiceCustom extends WebResourceInfoServiceGrpc.WebResourceInfoServiceImplBase {

    @Override
    public void getWebResourceInfo(WebResourceRequest request, StreamObserver<WebResourceInfo> responseObserver) {
        System.out.println("Request received from client. Request: " + request);

        try {
            validateRequest(request);
            WebResourceInfo webResourceInfo = WebResourceService.gI().getWebResourceInfo(request.getWebResource());
            responseObserver.onNext(webResourceInfo);
            responseObserver.onCompleted();

            System.out.println("Response sent to client. Response: " + webResourceInfo);
        } catch (BaseException e) {
            System.out.printf("Error proceeding the request: %s. ErrorMessage: %s", request, e.getMessage());
            responseObserver.onError(e);
        }
    }

    private void validateRequest(WebResourceRequest webResourceRequest) throws ValidationException {
        if (webResourceRequest == null || webResourceRequest.getWebResource() == null || webResourceRequest.getWebResource().isEmpty()) {
            throw new ValidationException("Incorrect WebResourceRequest, field webResource should be not null and not empty!");
        }
    }
}