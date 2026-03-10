package guru.qa.rangiffler.service;

import com.google.protobuf.Empty;
import guru.qa.rangiffler.grpc.Country;
import guru.qa.rangiffler.grpc.CountryResponse;
import guru.qa.rangiffler.grpc.RangifflerCountryServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class GrpcCountryService extends RangifflerCountryServiceGrpc.RangifflerCountryServiceImplBase {

  @Autowired
  public GrpcCountryService() {
  }

  @Override
  public void getCountries(Empty request, StreamObserver<CountryResponse> responseObserver) {
    responseObserver.onNext(setCountryResponse());
    responseObserver.onCompleted();
  }

  private CountryResponse setCountryResponse() {
    return CountryResponse.newBuilder()
        .addAllCountries(Country.newBuilder()
            .setCode("af")
            .setName("Afghanistan")
            .setFlag(
                "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACgAAAAeCAMAAABpA6zvAAABj1BMVEVHcEynHxIAShsAAAAANhcCAgKQExN7JROyIhMBaDAAAAAAPhoCAgKqIRQASx0AAAABAQEAAAAARxoAViN9Ox55EAipHhCoHRC1KRioHRC2KBicFAqeEwqaEwmaEwpxIhACAgIBaTEAAAAEBAQMDAwBaDABazQCAgIAAAAAAAAAYysBZS4CZC4ARxoAAAAAZikAbCwAaSsAcDAAYycAczEAXyUAdjPMGg0AfTndVUkAeDQAUh/haV7XMiPXJxYAWSPXLBraSDsATBwAejbUOy/ZQjTdWE3bTkLib2TTIRLjd27lfXQARhngXVDgY1foj4dorYTuqaLfaF7ZOys2NzbqmpKFvZvUJxhvcG+Li4qiFAqYEwmRQiOzZljfYFW2JhacIhQ0jVnCFwsgICDjc2nniH+6VUfDJRW3Fguqe2QliE9WVlWpPzHxurX65+TNKRedVkODKhNPnnC/i3lVonXNh39GnGuampkUfD+QxKZcXFvNk4h4tpKAgH/219OmKR2RSSllJhBQUVA8k2BNTUx/JbMRAAAAYHRSTlMAQrz+CbwEH//J2DCuNqfLVceCVfX1Y69k1YXC+4VkmnzW6Yos6dmb8mjyq2nZ//////////////////////////////////////////////////////////////////4G/4BLAAAChUlEQVQ4y23U6VPaQBjA4YhQijrejrVja++73JYuJCHB3DEnEKACyqkg4DXeV68/vEkMSkJ/s7Mfdp55P72zEKTnnX7xfsLj8UysPH0y4n0EDeWe80HQm9djo36rg02jpcVB7x7/8vlTJAJ57pXeFobt7W1vbz42mn/n8/levg0FAoFgKAL5/Xaoh2JoTufz0bBZNB6PB1cdEEmTggDDJIKiaPG72dpaPP4t5IQkTKeRMiUIOGJB0zlhpgn0USQnYgguIkWTmS4aSg7CzOkxgmJpFKcRlEXon3fjDBcN2GC+0ERRANIAz6Uxlmyd3Ds7zORPdSgydUACgLPs0Xml78I2eFHLd9KA4WCeF2CBZUua3HfhyCDM52u3FKxwvAAoXkRyTU1uW84BC7u3N1mFb4gcA6sU1pHbh9E7F4wkbPC62esqMJ5jG5IoScUz+cpk4WBwdRBmdi9KPVIplxk2rZS7N0VZk81xQQf8/TfT6eYUDtCNnKLCvdbVuWy5kH1ioXCMsRJTZ1TQ5dheqfKnbTk7rF0XjtVcloRFhlfomFo6OTwM3rlAchD6dwuX+j7QlEpzPBEjjzStbblAMmVbiostvA4TLI2TgMbVS00+M5ixuXZYy5SyksTgCIFQ+n2kVfou4phY63DZLM9ROKXyWbVYCfedE/r38XqDEMsqV5eyfKsS7rshWKIAYFSSVgmpQe9HTWa41cS6HW7EYgRJEQQDAEG1Htz/oBlC6O3fs2G4sLNTreqwapyDB5dM9eHoq7GpmamxheUfZoavHhhMd8lEan3dpcMPEx/H3db/8cg78vzZ4tLyht6vhFFKV19nxyFoZto9/CmZfmXS5XJNTs7OeY2nf9aSCmceryMWAAAAAElFTkSuQmCC")
            .build())
        .addAllCountries(Country.newBuilder()
            .setCode("al")
            .setName("Albania")
            .setFlag(
                "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACgAAAAeCAMAAABpA6zvAAAAz1BMVEVHcEzKAwPYAgLEAADNAADTAQHNAADNAAC+AADSAQHHBAS/AADLAADLAQHQAQHAAADVAwPLAgLUAwPLAQHHCgrLAgLCAADJAQHOAQG+AAC5AADHAADDAAC/AAD/AAAAAADZAADPAADlAADhAADSAADeAADoAAD3AAC9AADWAADCAADMAABzAADHAADuAADrAACOAAAHAAD8AACZAABfAAAcAABnAAB+AABWAABrAADwAAC3AACjAABJAACFAACwAACeAAAqAAA1AABAAACqAADoBy1nAAAAHnRSTlMAL7zXzNjH7QeuIYZVmuSvikBkaB1FZI19ur79+bw4PidYAAABt0lEQVQ4y5XUeXOiMByA4ahVxGPsPfYiCQEDJGC4oRyi9vt/poXd1rIiO933z/DMj2NIAKi7X6yX8nA4lJfr24cZ6DZdTAAYXI8RMpHehEzT1LSxfH17M5t+ocGdLGEGhggRh3rKZ5Q6G4K0Jml8NbkaSxhjjegMIPWEWnnvtSfNTcjGoYqi1nCr/KAzSH8Kg8zJk2jX3Poc2sBsQRykvAz3lBaHxKKK1gtFEsOQ7WPfLURFIt4P8xhCI4AwYSHkFfu+Qv6Gtl+6sOnAIUyLrBfu4hR+5UZR2QtFIdyTjP2ziZvWRMHD08TUbz+jBbQWRFm0S/d/hoYVz3thdWS8eXHoBjwVgd8LbeHRwv0Iw0OQ8yg3+6BX8YD7AruhGiYOja3eicRUMo0fIIw+fN/nWS/8XZlAeEyRMKLM+ydUg+RYiuZJWl9YN7pQ2br7ipytXYRKcuz8wTXEXbjt7o7L8EL/C6mj6s0hgD43Zyeq1VAnGmNMGtW7fSS92HXY1DffZ4L3TrBhPAPMJPlucDo/ZjeP89Xo1aqzGcPMtgzDGM0HAEwW0wun0uzhcb5cPb09rZbrxX2z8gshAJYqhHqCfwAAAABJRU5ErkJggg==")
            .build())
        .addAllCountries(Country.newBuilder()
            .setCode("ru")
            .setName("Russian Federation")
            .setFlag(
                "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACgAAAAeCAMAAABpA6zvAAAA+VBMVEVHcEyVFQ7S0tKXFg/R0dGYGBCcGRLU1NTW1takHhSeGxLW1tamHxWbFxfKysqhGxPU1NTR0dHT09PU1NTV1dXT09OSFA3///8AMp7MIRbPIxjKHhQAOacANaLRJRkAN6XTJxsAMJzWKx0APKjHHBMAQKsARa7X2NoAPqoCQqzWLiAALpnUKRyfGBCUFA4ISa61IBbm5ubMzMyuJBve3t4CKYgBI3YALJbDKR6uGBBJcbxUL2j5+ftskc7g4+luLFfr6+u+IBYAQqHt7vCpHhVDNnymudyMqdkoOI2JJkOnLTgAOZAAPpQsWrQAPZ1FIVa6yuf09PRBW5RdfvtPAAAAF3RSTlMAueiE0Cal/7jPTITtBwhlH2agL0JV2eqn9WIAAAGcSURBVDjLxdRbV4JAFIbhyfOx0jIlCNM0pBCPlYlCJB7LrP7/j2nvaSYH5aKuei9Zz3yzFhdDCHRcSOcTWD6dOz0m+x0WEoScpEMa1Me0OBZJoD/k6CSXD3W7JKFp9uSjyBt9TDrMhyIYmG683+8SrTMq7ofeppfYnQmADsCn4i/6R2j/YTEOcKTN5mNsPnu3g35CcfZJ4tp82GpdQ7qu1+v1hr4ejuein8zGrbt7Aoo7ZI3GFaYoV631EFvrpVKpAvD1eeu2TFHKSrl8gaFjcGeOsrLIKrcU+uc48zkO/c7Hvh3Cl2fm9pjgHhC+/bhARh2D4BbTqWVZ0+niQmB8DrrfAFw4qxtatWoYxmC1dKxFxTe3WXomsVYMVdFd0lRVRe/AJY6z7MlS7dEkvQF3BnMqdo5JkCzJEIfCnOp3EnV8cTsnMNExuDMnMokyBn9cIKOOQUMduG2W60oBrtYE2HZ7nudlklimiXkeHKjJMnPuY9M0D0jPy8SOoln2fmSjqaNwLNkUMk3zLBwlJJnKBrxK1McOoFg4FcUvX2nLmUOxnjYdAAAAAElFTkSuQmCC")
            .build())
        .build();
  }
}
