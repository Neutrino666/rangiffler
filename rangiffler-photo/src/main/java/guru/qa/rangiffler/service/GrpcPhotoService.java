package guru.qa.rangiffler.service;

import com.google.protobuf.util.Timestamps;
import guru.qa.rangiffler.grpc.PhotoRequest;
import guru.qa.rangiffler.grpc.PhotoResponse;
import guru.qa.rangiffler.grpc.RangifflerPhotoServiceGrpc;
import io.grpc.stub.StreamObserver;
import java.util.Date;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class GrpcPhotoService extends RangifflerPhotoServiceGrpc.RangifflerPhotoServiceImplBase {

  @Autowired
  public GrpcPhotoService() {
  }

  @Override
  public void updatePhoto(PhotoRequest request, StreamObserver<PhotoResponse> responseObserver) {
    responseObserver.onNext(setPhotoResponse(request));
    responseObserver.onCompleted();
  }

  private PhotoResponse setPhotoResponse(PhotoRequest request) {
    return PhotoResponse.newBuilder()
        .setId("21c6b81f-2a87-4a82-8e5b-cac0bc05535b")
        .setDescription(request.getSrc())
        .setCountry("af")
        .setUser(request.getUser())
        .setCreationDate(Timestamps.fromDate(new Date()))
        .build();
  }
}
