package guru.qa.rangiffler;

import guru.qa.rangiffler.service.PropertiesLogger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RangifflerPhotoApplication {

  public static void main(String[] args) {
    SpringApplication springApplication = new SpringApplication(RangifflerPhotoApplication.class);
    springApplication.addListeners(new PropertiesLogger());
    springApplication.run(args);
  }
}
