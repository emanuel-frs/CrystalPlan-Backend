package com.project.crystalplan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.awt.*;
import java.net.URI;

@SpringBootApplication
public class CrystalPlanApplication {
    public static void main(String[] args) {
        SpringApplication.run(CrystalPlanApplication.class, args);
        openSwaggerUI();
    }
    private static void openSwaggerUI() {
        try {
            String swaggerUrl = "http://localhost:8080/api/swagger-ui/index.html";
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(swaggerUrl));
            } else {
                System.out.println("Acesse o Swagger UI manualmente: " + swaggerUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}