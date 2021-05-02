
package news.web.http;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse implements Response {
    int statusCode;
    String statusText;
    String version;
    String body;
    String response;
    private final Map<String, String> headers = new HashMap<>();

    @Override
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public void setHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String getRawResponse() {
        String requestLine = String.format("%s %s %s\n", version, statusCode, statusText);
        StringBuilder headersLines = new StringBuilder();
        for (Map.Entry<String, String> pair : headers.entrySet()) {
            headersLines.append(pair.getKey()).append(": ").append(pair.getValue()).append("\n");
        }
        String responseBody = body;
        if (responseBody != null) {
            headersLines.append("\n");
            response = requestLine + headersLines + responseBody;
        } else {
            response = requestLine + headersLines;
        }
        return response;
    }
}

