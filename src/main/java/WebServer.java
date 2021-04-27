import news.NewsApp;
import news.dto.CategorySerializer;
import news.web.http.HttpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class WebServer {

    public void run() {
        try {
            final int PORT = 5000;
            ServerSocket serverSocket = new ServerSocket(PORT);

            while (true) {
                Socket server = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
                HttpRequest request = new HttpRequest(in);
                Thread app = new NewsApp(request);


                CategorySerializer categorySerializer = new CategorySerializer(request.getBody());
                System.out.println(request.getBody());
                System.out.println(categorySerializer.toObject());
                String httpResponse = "" +
                        "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/html\r\n\r\n" +
                        "<!DOCTYPE>\n" +
                        "<html>\n" +
                        "<head>\n" +
                        "<title>Работает!</title>\n" +
                        "<meta charset=\"utf-8\">\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<h1>Ответ пришел</h1>\n" +
                        "<p>Your browser sent a request that this server could not understand.</p>\n" +
                        "<p>The request line contained invalid characters following the protocol string.</p>\n" +
                        "</body>\n" +
                        "</html>";
                PrintWriter out = new PrintWriter(server.getOutputStream(), true);
                out.println(httpResponse);
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
