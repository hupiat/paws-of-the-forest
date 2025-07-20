package org.warriorcats.pawsOfTheForest.utils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class HttpServerUtils {

    public static final int RESOURCES_PACK_PORT = 8175;

    private static HttpServer httpServer;

    public static void start(int port, Path file, String route) {
        try {
            if (httpServer != null) {
                stop();
            }

            httpServer = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
            httpServer.createContext(route, new FileHandler(file));
            httpServer.setExecutor(null);
            httpServer.start();

            Bukkit.getLogger().info("HTTP server started on port " + port + " serving: " + route);

        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to start HTTP server", e);
        }
    }

    public static void stop() {
        if (httpServer != null) {
            httpServer.stop(0);
            httpServer = null;
            Bukkit.getLogger().info("HTTP server stopped.");
        }
    }

    private static class FileHandler implements HttpHandler {
        private final Path file;

        public FileHandler(Path file) {
            this.file = file;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!Files.exists(file)) {
                exchange.sendResponseHeaders(404, -1);
                return;
            }

            byte[] content = Files.readAllBytes(file);
            exchange.getResponseHeaders().add("Content-Type", "application/zip");
            exchange.sendResponseHeaders(200, content.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(content);
            }
        }
    }
}
