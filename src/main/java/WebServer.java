import news.NewsApp;
import news.web.http.HttpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class WebServer {

    public void run() {
        try {
            final int PORT = 5000;
            ServerSocket serverSocket = new ServerSocket(PORT);

            while (true) {
                Socket server = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
                HttpRequest request = new HttpRequest(in);
                NewsApp app = new NewsApp(request);
                String httpResponse = app.getResponse().getRawResponse();
                //System.out.println(httpResponse);

                PrintWriter out = new PrintWriter(server.getOutputStream(), true);
                out.println(httpResponse);
                out.close();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}
