package news.web.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpRequest implements Request {
    private String method;
    private String version;
    private String path;
    private StringBuilder body = new StringBuilder();
    private String requestLine;
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> params = new HashMap<>();

    public HttpRequest(BufferedReader rawRequest) throws IOException {

        // получаем данные строки запроса
        requestLine = rawRequest.readLine();
        String[] requestPaths = requestLine.split(" ");
        method = requestPaths[0];
        version = requestPaths[2];

        // извлекаем параметры, если они есть
        if (requestPaths[1].contains("?")) {
            Pattern p = Pattern.compile(".+\\?(.+)");
            Matcher m = p.matcher(requestPaths[1]);
            m.find();
            String[] paramsPair = m.group(1).split("&");

            for (String pair : paramsPair) {
                String[] keyValue = pair.split("=");
                params.put(keyValue[0], keyValue[1]);
            }
        } else {
            path = requestPaths[1];
        }

        // получаем заголовки
        while (true) {
            String line = rawRequest.readLine();
            if (line.equals("")) {
                break;
            }
            String[] keyValue = line.split(":", 2);
            headers.put(keyValue[0], keyValue[1]);
        }

        // получаем тело запроса
        if (!method.equals("GET") && !method.equals("DELETE")) {
            while (rawRequest.ready()) {
                String line = rawRequest.readLine();
                body.append(line).append("\n");
            }
        }
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

    @Override
    public String getBody() {
        return body.toString();
    }
}
