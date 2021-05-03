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
                System.out.println("Server: СЕРВЕР ЗАПУЩЕН");
                Socket server = serverSocket.accept();

//                for (int i = 0; i < 100; i++) {
//                    System.out.println("Server: " + i + " : " + Math.sqrt(Math.sqrt(Math.sqrt(Double.parseDouble(String.valueOf(i + 20))))));
//                }
                System.out.println("Server: ДОЖДАЛИСЬ ПОДКЛЮЧЕНИЯ");
                BufferedReader in = new BufferedReader(new InputStreamReader(server.getInputStream()));
                System.out.println("Server: ЧЕ-ТО ТАМ СЧИТАЛИ");
                System.out.println(in);
                System.out.println("Server: И ЧТО ТУТ У НАС?");
                HttpRequest request = new HttpRequest(in);
                System.out.println(request.getHeaders());
                System.out.println("Server: И ПОПЫТАЛИСЬ СОЗДАТЬ РЕКВЕСТ");
                System.out.println("Server: REQUEST version: " + request.getVersion());
                System.out.println("Server: REQUEST path with params: " + request.getPath(true));
                System.out.println("Server: REQUEST path without params: " + request.getPath(false));
                System.out.println("Server: REQUEST method : " + request.getMethod());

                NewsApp app;
                if (request.getHeaders().containsKey("UnitTest")) {
                    System.out.println("Server: Выполняется ветвь для тестов");
                    this.dbPool = new DBPool(
                            request.getHeaders().get("UrlPostgres").trim(),
                            request.getHeaders().get("UserPostgres").trim(),
                            request.getHeaders().get("PasswordPostgres").trim()
                            );
                    app = new NewsApp(request, this.dbPool);
                    System.out.println("Ветвь для тестов. Посде создания приложения");
                } else {
                    System.out.println("Server: Выполняется ветвь без тестов");
                    app = new NewsApp(request);
                }
                /*if (dbPool != null) {
                    System.out.println("Server: Выполняется ветвь для тестов");
                    app = new NewsApp(request, this.dbPool);
                } else {
                    System.out.println("Server: Выполняется ветвь без тестов");
                    app = new NewsApp(request);
                }*/
                String httpResponse = app.getResponse().getRawResponse();
                System.out.println("httpResponse: \n" + httpResponse);
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
