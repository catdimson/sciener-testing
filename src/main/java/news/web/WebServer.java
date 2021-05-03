package news.web;

import news.NewsApp;
import news.dao.connection.DBPool;
import news.web.http.HttpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WebServer extends Thread {
    List<NewsApp> newsAppsList = new ArrayList<>();
    DBPool dbPool;

    public void setConnections(DBPool dbPool) {
        this.dbPool = dbPool;
    }

    public void run() {
        try {
            final int PORT = 5000;
            ServerSocket serverSocket = new ServerSocket(PORT);

            while (true) {
                System.out.println("СЕРВЕР ЗАПУЩЕН");
                Socket server = serverSocket.accept();
                System.out.println("ДОЖДАЛИСЬ ПОДКЛЮЧЕНИЯ");
                BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
                System.out.println("ЧЕ-ТО ТАМ СЧИТАЛИ");
                HttpRequest request = new HttpRequest(in);
                System.out.println(request.getHeaders());
                System.out.println("И ПОПЫТАЛИСЬ СОЗДАТЬ РЕКВЕСТ");
                System.out.println("REQUEST version: " + request.getVersion());
                System.out.println("REQUEST path with params: " + request.getPath(true));
                System.out.println("REQUEST path without params: " + request.getPath(false));
                System.out.println("REQUEST method : " + request.getMethod());
                NewsApp app;
                if (request.getHeaders().containsKey("UnitTest")) {
                    app = new NewsApp(request, dbPool);
                } else {
                    app = new NewsApp(request);
                }
                newsAppsList.add(app);
                String httpResponse = app.getResponse().getRawResponse();
                //System.out.println(httpResponse);

                PrintWriter out = new PrintWriter(server.getOutputStream(), true);
                out.println(httpResponse);
                in.close();
                out.close();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}
