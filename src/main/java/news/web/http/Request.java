package news.web.http;

import java.util.Map;

public interface Request {

    String getMethod();

    String getVersion();

    String getPath(boolean withParams);

    Map<String, String> getHeaders();

    Map<String, String> getParams();

    String getBody();

}