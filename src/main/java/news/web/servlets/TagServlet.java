package news.web.servlets;

import news.dao.connection.DBPool;
import news.di.container.BeanFactory;
import news.web.controllers.TagController;
import news.web.http.HttpRequest;
import news.web.http.HttpResponse;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name="TagServlet", urlPatterns={"/tag/*", "/tag/"})
public class TagServlet extends HttpServlet {

    protected BeanFactory beanFactory;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. Воссоздадим стартовую строку для кастомного HttpRequest из HttpServletRequest
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        String requestURI = request.getRequestURI();
        System.out.println("FROM requestURl: " + requestURI);
        Pattern p = Pattern.compile("^/blg_kotik_dmitry_war(?<path>(.+))");
        Matcher m = p.matcher(requestURI);
        m.find();
        requestURI = m.group("path");
        System.out.println("FROM PATTERN: " + requestURI);
        String startingLine = String.format("%s %s %s\n",
                request.getMethod(),
                requestURI + (request.getQueryString() == null ? "" : "?" + request.getQueryString()),
                request.getScheme().toUpperCase());
        // 2. Воссоздаем заголовки для кастомного HttpRequest из HttpServletRequest
        String headers = "";
        Enumeration<String> headersKeys = request.getHeaderNames();
        while (headersKeys.hasMoreElements()) {
            String key = headersKeys.nextElement();
            headers += key + ": " + request.getHeader(key) + "\n";
        }
        headers += "\n";
        // 3. Воссоздадим тело заголовка для кастомного HttpRequest из HttpServletRequest
        String body = "";
        Scanner scan = new Scanner(request.getInputStream(), StandardCharsets.UTF_8).useDelimiter("\\A");
        body = (scan.hasNext() ? scan.next() + "\n" : "\n");
        // 4. Воссоздаем BufferedReader для кастомного HttpRequest
        String requestString = startingLine + headers + body;
        System.out.println("REQUEST STRING: \n" + requestString);
        Reader inputString = new StringReader(requestString);
        BufferedReader rawRequest = new BufferedReader(inputString);
        // 5. ВОЛНИТЕЛЬНЫЙ МОМЕНТ
        HttpRequest customHttpRequest = new HttpRequest(rawRequest);

        if (request.getHeader("UnitTest") == null) {
            URL path = getClass().getClassLoader().getResource("applicationContext.xml");
            BeanFactory.setSettings(new DBPool(), customHttpRequest, path.getPath());
        } else {
            BeanFactory.setSettings(
                    new DBPool(
                            request.getHeader("UrlPostgres"),
                            request.getHeader("UserPostgres"),
                            request.getHeader("PasswordPostgres")
                    ),
                    customHttpRequest, "src/main/resources/applicationContext.xml");
        }
        beanFactory = BeanFactory.getInstance();

        try {
            TagController tagController = beanFactory.getBean(TagController.class);
            tagController.buildResponse();
            HttpResponse customHttpResponse = tagController.getResponse();
            // устанавливает код статуса
            response.setStatus(customHttpResponse.getStatusCode());
            // устанавливаем заголовки
            Map<String, String> customHeaders = customHttpResponse.getHeaders();
            for (Map.Entry<String, String> pair: customHeaders.entrySet()) {
                response.setHeader(pair.getKey(), pair.getValue());
            }
            // устанавливаем тело
            PrintWriter pr = response.getWriter();
            pr.write(customHttpResponse.getBody());

        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //System.out.println("HELLO WORLD");
        //BufferedReader rawRequest = new BufferedReader(new InputStreamReader(request.getInputStream()));
        //Scanner sc = new Scanner()
        /*System.out.println("1. Method: " + request.getMethod());
        System.out.println("2. Uri: " + request.getRequestURI());
        System.out.println("3. Scheme: " + request.getScheme());
        System.out.println("4. ServletContext: " + request.getServletContext());
        System.out.println("5. getQueryString: " + request.getQueryString());
        System.out.println("6. : " + request.getPathInfo());
        System.out.println("7. : " + request.getContextPath());
        System.out.println("8. : " + request.getServletPath());
        System.out.println("rawRequest всё таки получили");*/
        /*Scanner s = new Scanner(request.getInputStream(), StandardCharsets.UTF_8).useDelimiter("\\A");
        while (s.hasNext()) {
            System.out.println("Read from scanner: " + s.next());
        }*/
        //HttpRequest customHttpRequest = new HttpRequest(rawRequest);

        // 1. Воссоздадим стартовую строку для кастомного HttpRequest из HttpServletRequest
        String startingLine = String.format("%s %s %s\n",
                request.getMethod(),
                request.getRequestURI() + (request.getQueryString() == null ? "" : "?" + request.getQueryString()),
                request.getScheme().toUpperCase());
        //System.out.println("RESULT: " + startingLine);
        // 2. Воссоздаем заголовки для кастомного HttpRequest из HttpServletRequest
        String headers = "";
        Enumeration<String> headersKeys = request.getHeaderNames();
        while (headersKeys.hasMoreElements()) {
            String key = headersKeys.nextElement();
            headers += key + ": " + request.getHeader(key) + "\n";
        }
        headers += "\n";
        // 3. Воссоздадим тело заголовка для кастомного HttpRequest из HttpServletRequest
        String body = "";
        Scanner scan = new Scanner(request.getInputStream(), StandardCharsets.UTF_8).useDelimiter("\\A");
        body = (scan.hasNext() ? scan.next() + "\n" : "\n");
        // 4. Воссоздаем BufferedReader для кастомного HttpRequest
        String requestString = startingLine + headers + body;
        System.out.println("REQUEST STRING: \n" + requestString);
        Reader inputString = new StringReader(requestString);
        BufferedReader rawRequest = new BufferedReader(inputString);
        // 5. ВОЛНИТЕЛЬНЫЙ МОМЕНТ
        HttpRequest httpRequest = new HttpRequest(rawRequest);
        System.out.println("---------------------------- FROM CUSTOM REQUEST ----------------------------------");
        System.out.println("version: " + httpRequest.getVersion());
        System.out.println("method: " + httpRequest.getMethod());
        System.out.println("body: " + httpRequest.getBody());
        System.out.println("version: " + httpRequest.getVersion());






        /*if (request.getHeader("UnitTest") == null) {
            BeanFactory.setSettings(new DBPool(), request, "src/main/resources/applicationContext.xml");
        } else {
            BeanFactory.setSettings(
                    new DBPool(
                            request.getHeader("UrlPostgres"),
                            request.getHeader("UserPostgres"),
                            request.getHeader("PasswordPostgres")
                    ),
                    request, "src/main/resources/applicationContext.xml");
        }
        beanFactory = BeanFactory.getInstance();

        try {
            TagController tagController = beanFactory.getBean(TagController.class);
            tagController.buildResponse();
            HttpResponse customHttpResponse = tagController.getResponse();
            // устанавливает код статуса
            response.setStatus(customHttpResponse.getStatusCode());
            // устанавливаем заголовки
            Map<String, String> customHeaders = customHttpResponse.getHeaders();
            for (Map.Entry<String, String> pair: customHeaders.entrySet()) {
                response.setHeader(pair.getKey(), pair.getValue());
            }
            // устанавливаем тело

        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | InvocationTargetException | SQLException e) {
            e.printStackTrace();
        }*/


        /*PrintWriter pw = response.getWriter();

        pw.println("{\n");
        pw.println("\t\"id\":2,\n");
        pw.println("\t\"title\":\"hello world 2!\"\n");
        pw.println("\t\"Переданный параметр\":\"\"" + request.getHeader("NotExistHeader") + "\n");
        pw.println("\t\"Connection\":\"\"" + request.getHeader("Connection") + "\n");
        pw.println("}\n");*/
    }
}
