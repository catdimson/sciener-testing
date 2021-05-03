package news.web.http;

import news.NewsApp;
import news.dao.connection.DBPool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class WebServer extends Thread {
    DBPool dbPool = null;

    public WebServer(DBPool connectionPool) throws IOException {
        this.dbPool = connectionPool;
    }

    public WebServer() {};

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
                System.out.println(in);
                System.out.println("И ЧТО ТУТ У НАС?");
                HttpRequest request = new HttpRequest(in);
                System.out.println(request.getHeaders());
                System.out.println("И ПОПЫТАЛИСЬ СОЗДАТЬ РЕКВЕСТ");
                System.out.println("REQUEST version: " + request.getVersion());
                System.out.println("REQUEST path with params: " + request.getPath(true));
                System.out.println("REQUEST path without params: " + request.getPath(false));
                System.out.println("REQUEST method : " + request.getMethod());

                NewsApp app;
                if (dbPool != null) {
                    System.out.println("Выполняется ветвь для тестов");
                    app = new NewsApp(request, this.dbPool);
                } else {
                    System.out.println("Выполняется ветвь без тестов");
                    app = new NewsApp(request);
                }
                String httpResponse = app.getResponse().getRawResponse();

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
