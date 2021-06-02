package news.web.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpRequest implements Request {
    private String method;
    private String version;
    private String pathWithoutParams;
    private String path;
    private StringBuilder body = new StringBuilder();
    private String requestLine;
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> params = new HashMap<>();

    public HttpRequest(BufferedReader rawRequest) throws IOException {

        // получаем данные строки запроса
        requestLine = rawRequest.readLine();
        System.out.println(requestLine);
        String[] requestPaths = requestLine.split(" ");
        method = requestPaths[0];
        version = requestPaths[2];
        requestPaths[1] = java.net.URLDecoder.decode(requestPaths[1], StandardCharsets.UTF_8);

        // извлекаем параметры, если они есть
        if (requestPaths[1].contains("?")) {
            Pattern p = Pattern.compile("(?<path>(.+))\\?(?<param>(.+))");
            Matcher m = p.matcher(requestPaths[1]);
            m.find();
            pathWithoutParams = m.group("path") + "/";
            path = requestPaths[1];
            String[] paramsPair = m.group("param").split("&");
            for (String pair : paramsPair) {
                String[] keyValue = pair.split("=");
                params.put(keyValue[0], keyValue[1]);
            }
        } else {
            pathWithoutParams = requestPaths[1];
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
        while (rawRequest.ready()) {
            String line = rawRequest.readLine();
            if (line == null) {
                break;
            }
            body.append(line).append("\n");
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
    public String getPath(boolean withParams) {
        if (withParams) {
            return path;
        } else {
            return pathWithoutParams;
        }
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
