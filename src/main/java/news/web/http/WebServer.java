package news.web.http;

import news.NewsApp;
import news.dao.connection.ConnectionPool;
import news.dao.connection.DBPool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class WebServer extends Thread {
    ConnectionPool dbPool;

    public WebServer() {};

    public void run() {
        try {
            final int PORT = 5000;
            ServerSocket serverSocket = new ServerSocket(PORT);

            while (true) {
                Socket server = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
                System.out.println(in);
                HttpRequest request = new HttpRequest(in);

                NewsApp app;
                if (request.getHeaders().containsKey("UnitTest")) {
                    this.dbPool = new DBPool(
                            request.getHeaders().get("UrlPostgres").trim(),
                            request.getHeaders().get("UserPostgres").trim(),
                            request.getHeaders().get("PasswordPostgres").trim()
                            );
                    app = new NewsApp(request, this.dbPool);
                } else {
                    app = new NewsApp(request);
                }
                String httpResponse = app.getResponse().getRawResponse();
                PrintWriter out = new PrintWriter(server.getOutputStream(), true);
                out.println(httpResponse);
                in.close();
                out.close();
            }
        } catch (IOException | SQLException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException | ClassNotFoundException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
