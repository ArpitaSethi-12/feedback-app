package com.devops.feedback;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FeedbackApp {

    public static void main(String[] args) throws IOException {
        int port = 8081;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", FeedbackApp::showForm);
        server.createContext("/submit", FeedbackApp::submit);
        server.start();
        System.out.println("Feedback app running on port " + port);
    }

    // GET / -> shows the HTML feedback form
    static void showForm(HttpExchange ex) throws IOException {
        try (InputStream in = FeedbackApp.class.getResourceAsStream("/static/index.html")) {
            send(ex, 200, new String(in.readAllBytes(), StandardCharsets.UTF_8));
        }
    }

    // POST /submit -> reads the form fields and shows a thank-you page
    static void submit(HttpExchange ex) throws IOException {
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) {
            send(ex, 405, "Use POST");
            return;
        }
        String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> f = parseForm(body);
        System.out.println("Feedback received: " + f);
        String html = "<html><body style='font-family:Arial;text-align:center;padding:40px'>"
                + "<h2>Thank you, " + escape(f.get("name")) + "!</h2>"
                + "<p>Category: " + escape(f.get("category")) + " | Rating: " + escape(f.get("rating")) + "/5</p>"
                + "<a href='/'>Submit another response</a></body></html>";
        send(ex, 200, html);
    }

    static Map<String, String> parseForm(String body) {
        Map<String, String> map = new HashMap<>();
        if (body == null || body.isEmpty()) return map;
        for (String pair : body.split("&")) {
            String[] kv = pair.split("=", 2);
            String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
            String val = kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "";
            map.put(key, val);
        }
        return map;
    }

    static String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static void send(HttpExchange ex, int code, String html) throws IOException {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        ex.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }
}
