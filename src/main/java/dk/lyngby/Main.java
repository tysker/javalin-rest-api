package dk.lyngby;

import dk.lyngby.config.ApplicationConfig;
import io.javalin.Javalin;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        ApplicationConfig.startServer(Integer.parseInt(ApplicationConfig.getProperty("javalin.port")));
    }
}
