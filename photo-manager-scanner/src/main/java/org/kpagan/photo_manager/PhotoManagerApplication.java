package org.kpagan.photo_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PhotoManagerApplication {

    static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(PhotoManagerApplication.class, args)));
    }

}
