package com.igorgorbunov3333.timer.backend.service.message.telegram;

import com.igorgorbunov3333.timer.backend.config.properties.TelegramProperties;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class TelegramHttpApiCaller {

    private final TelegramProperties telegramProperties;

    @SneakyThrows
    public void send(String message) {
        try {
            sendMessage(message);
        } catch (Exception e) {
            log.error("Exception while sending a message", e);
            throw e;
        }
    }

    private void sendMessage(String message) throws IOException, URISyntaxException {
        if (StringUtils.isBlank(message)) {
            log.warn("Message for sending is null or blank!");
            return;
        }

        HttpURLConnection connection = buildHttpURLConnection();

        connection.setRequestMethod("GET");
        connection.setDoOutput(true);

        addParametersToConnection(connection, message);

        connection.connect();

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        in.close();

        connection.disconnect();
    }

    private HttpURLConnection buildHttpURLConnection() throws URISyntaxException, IOException {
        URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setScheme("https");
        uriBuilder.setHost("api.telegram.org");

        String token = telegramProperties.getToken();
        uriBuilder.setPathSegments("bot" + token, "sendMessage");

        URI uri = uriBuilder.build();

        URLConnection conn = uri.toURL().openConnection();

        return (HttpURLConnection) conn;
    }

    private void addParametersToConnection(HttpURLConnection connection, String message) throws IOException {
        DataOutputStream out = new DataOutputStream(connection.getOutputStream());

        String paramsString = getParamsString(message);
        out.writeBytes(paramsString);
        out.flush();
        out.close();
    }

    private String getParamsString(String message) {
        Map<String, String> params = getParameters(message);

        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            result.append("&");
        }

        String resultString = result.toString();
        return resultString.length() > 0 ? resultString.substring(0, resultString.length() - 1) : resultString;
    }

    private Map<String, String> getParameters(String message) {
        Map<String, String> parameters = new HashMap<>();
        String chatId = telegramProperties.getChatId();
        parameters.put("chat_id", chatId);
        parameters.put("text", message);

        return parameters;
    }

}
