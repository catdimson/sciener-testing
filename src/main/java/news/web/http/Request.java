package news.web.http;

import java.util.Map;

public interface Request {

    String getMethod(String method);

    String getVersion(String version);

    String getPath(String path);

    Map<String, String> getHeaders();

    Map<String, String> getParams();

    String getBody();

}
