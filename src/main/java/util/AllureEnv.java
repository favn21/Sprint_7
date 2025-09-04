package util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Properties;

public class AllureEnv {
    public static void write(String baseUri, String environment) {
        try {
            File dir = new File("target/allure-results");
            if (!dir.exists()) dir.mkdirs();
            File file = new File(dir, "environment.properties");
            Properties p = new Properties();
            p.setProperty("baseUri", baseUri);
            p.setProperty("environment", environment);
            p.setProperty("java.version", System.getProperty("java.version"));
            p.setProperty("os.name", System.getProperty("os.name"));
            try (FileOutputStream fos = new FileOutputStream(file)) {
                p.store(fos, "Allure environment");
            }
        } catch (IOException e) {

        }
    }
}
