package news.web.servlets;

import news.dao.connection.DBPool;
import news.di.container.BeanFactory;
import news.web.controllers.TagController;
import news.web.http.HttpRequest;
import news.web.http.HttpResponse;

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

    private HttpRequest convertToCustomHttpRequest(HttpServletRequest request) throws IOException {
        // 1. Воссоздадим стартовую строку для кастомного HttpRequest из HttpServletRequest
        request.setCharacterEncoding("UTF-8");
        String requestURI = request.getRequestURI();
        System.out.println("FROM requestURl: " + requestURI);
        Pattern p = Pattern.compile("^/blg_kotik_dmitry_war(?<path>(.+))");
        Matcher m = p.matcher(requestURI);
        m.find();
        requestURI = m.group("path");
        System.out.println("-----FROM PATTERN STARTING LINE: \n" + requestURI);
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
        System.out.println("-----FROM PATTERN HEADERS: \n" + headers);
        // 3. Воссоздадим тело заголовка для кастомного HttpRequest из HttpServletRequest
        String body = "";
        Scanner scan = new Scanner(request.getInputStream(), StandardCharsets.UTF_8).useDelimiter("\\A");
        body = (scan.hasNext() ? scan.next() + "\n" : "");
        /*System.out.println("scan.next(): " + scan.next());
        System.out.println("scan.next(): " + scan.next());
        System.out.println("scan.next(): " + scan.next());
        System.out.println("scan.next(): " + scan.next());
        System.out.println("scan.next(): " + scan.next());*/
        System.out.println("-----FROM PATTERN BODY: \n" + body);

        //String t = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        //String t = IOUtils.toString(request.getReader());
        //System.out.println("-----HZ CHTO: \n" + t);



        // 4. Воссоздаем BufferedReader для кастомного HttpRequest
        String requestString = startingLine + headers + body;
        System.out.println("-----REQUEST STRING: \n" + requestString);
        Reader inputString = new StringReader(requestString);
        BufferedReader rawRequest = new BufferedReader(inputString);
        // 5. ВОЛНИТЕЛЬНЫЙ МОМЕНТ
        HttpRequest customHttpRequest = new HttpRequest(rawRequest);
        return  customHttpRequest;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        HttpRequest customHttpRequest = convertToCustomHttpRequest(request);

        URL path = getClass().getClassLoader().getResource("applicationContext.xml");
        if (request.getHeader("UnitTest") == null) {
            BeanFactory.setSettings(new DBPool(), customHttpRequest, path.getPath());
        } else {
            BeanFactory.setSettings(
                    new DBPool(
                            request.getHeader("UrlPostgres"),
                            request.getHeader("UserPostgres"),
                            request.getHeader("PasswordPostgres")
                    ),
                    customHttpRequest, path.getPath());
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        HttpRequest customHttpRequest = convertToCustomHttpRequest(request);

        URL path = getClass().getClassLoader().getResource("applicationContext.xml");
        if (request.getHeader("UnitTest") == null) {
            BeanFactory.setSettings(new DBPool(), customHttpRequest, path.getPath());
        } else {
            BeanFactory.setSettings(
                    new DBPool(
                            request.getHeader("UrlPostgres"),
                            request.getHeader("UserPostgres"),
                            request.getHeader("PasswordPostgres")
                    ),
                    customHttpRequest, path.getPath());
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

        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        HttpRequest customHttpRequest = convertToCustomHttpRequest(request);

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

        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        HttpRequest customHttpRequest = convertToCustomHttpRequest(request);

        URL path = getClass().getClassLoader().getResource("applicationContext.xml");
        if (request.getHeader("UnitTest") == null) {
            BeanFactory.setSettings(new DBPool(), customHttpRequest, path.getPath());
        } else {
            BeanFactory.setSettings(
                    new DBPool(
                            request.getHeader("UrlPostgres"),
                            request.getHeader("UserPostgres"),
                            request.getHeader("PasswordPostgres")
                    ),
                    customHttpRequest, path.getPath());
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

        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
