package news.web.servlets;

import news.HibernateUtil;
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

    private String extractPath(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        Pattern p = Pattern.compile("^/blg_kotik_dmitry_war(?<path>(.+))");
        Matcher m = p.matcher(requestURI);
        m.find();
        requestURI = m.group("path");
        String startingLine = String.format("%s %s %s\n", request.getMethod(),
                requestURI + (request.getQueryString() == null ? "" : "?" + request.getQueryString()),
                request.getScheme().toUpperCase());
        return startingLine;
    }

    private String extractHeaders(HttpServletRequest request) {
        StringBuilder headers = new StringBuilder();
        Enumeration<String> headersKeys = request.getHeaderNames();
        while (headersKeys.hasMoreElements()) {
            String key = headersKeys.nextElement();
            headers.append(key).append(": ").append(request.getHeader(key)).append("\n");
        }
        headers.append("\n");
        return headers.toString();
    }

    private String extractBody(HttpServletRequest request) throws IOException {
        Scanner scan = new Scanner(request.getInputStream(), StandardCharsets.UTF_8).useDelimiter("\\A");
        return (scan.hasNext() ? scan.next() + "\n" : "");
    }

    private HttpRequest convertToCustomHttpRequest(HttpServletRequest request) throws IOException {
        request.setCharacterEncoding("UTF-8");
        // 1. Воссоздадим стартовую строку для кастомного HttpRequest из HttpServletRequest
        String startingLine = extractPath(request);
        // 2. Воссоздаем заголовки для кастомного HttpRequest из HttpServletRequest
        String headers = extractHeaders(request);
        // 3. Воссоздадим тело заголовка для кастомного HttpRequest из HttpServletRequest
        String body = extractBody(request);
        // 4. Воссоздаем BufferedReader для кастомного HttpRequest
        String requestString = startingLine + headers + body;
        Reader inputString = new StringReader(requestString);
        BufferedReader rawRequest = new BufferedReader(inputString);
        // 5. Создаем кастомный HttpRequest
        return new HttpRequest(rawRequest);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        HttpRequest customHttpRequest = convertToCustomHttpRequest(request);

        URL path = getClass().getClassLoader().getResource("applicationContext.xml");
        BeanFactory.setSettings(customHttpRequest, path.getPath());
        if (request.getHeader("UnitTest") != null) {
            try {
                HibernateUtil.setConnectionProperties(
                        request.getHeader("UrlPostgres"),
                        request.getHeader("UserPostgres"),
                        request.getHeader("PasswordPostgres"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        beanFactory = BeanFactory.getInstance();

        try {
            TagController tagController = beanFactory.getBean(TagController.class);
            HttpResponse customHttpResponse = tagController.getResponse();
            tagController.buildResponse(customHttpRequest);
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
        BeanFactory.setSettings(customHttpRequest, path.getPath());
        if (request.getHeader("UnitTest") != null) {
            try {
                HibernateUtil.setConnectionProperties(
                        request.getHeader("UrlPostgres"),
                        request.getHeader("UserPostgres"),
                        request.getHeader("PasswordPostgres"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        beanFactory = BeanFactory.getInstance();

        try {
            TagController tagController = beanFactory.getBean(TagController.class);
            HttpResponse customHttpResponse = tagController.getResponse();
            tagController.buildResponse(customHttpRequest);
            // устанавливает код статуса
            response.setStatus(customHttpResponse.getStatusCode());
            // устанавливаем заголовки
            Map<String, String> customHeaders = customHttpResponse.getHeaders();
            for (Map.Entry<String, String> pair: customHeaders.entrySet()) {
                response.setHeader(pair.getKey(), pair.getValue());
            }
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        HttpRequest customHttpRequest = convertToCustomHttpRequest(request);

        URL path = getClass().getClassLoader().getResource("applicationContext.xml");
        BeanFactory.setSettings(customHttpRequest, path.getPath());
        if (request.getHeader("UnitTest") != null) {
            try {
                HibernateUtil.setConnectionProperties(
                        request.getHeader("UrlPostgres"),
                        request.getHeader("UserPostgres"),
                        request.getHeader("PasswordPostgres"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        beanFactory = BeanFactory.getInstance();

        try {
            TagController tagController = beanFactory.getBean(TagController.class);
            HttpResponse customHttpResponse = tagController.getResponse();
            tagController.buildResponse(customHttpRequest);
            // устанавливает код статуса
            response.setStatus(customHttpResponse.getStatusCode());
            // устанавливаем заголовки
            Map<String, String> customHeaders = customHttpResponse.getHeaders();
            for (Map.Entry<String, String> pair: customHeaders.entrySet()) {
                response.setHeader(pair.getKey(), pair.getValue());
            }
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        HttpRequest customHttpRequest = convertToCustomHttpRequest(request);

        URL path = getClass().getClassLoader().getResource("applicationContext.xml");
        BeanFactory.setSettings(customHttpRequest, path.getPath());
        if (request.getHeader("UnitTest") != null) {
            try {
                HibernateUtil.setConnectionProperties(
                        request.getHeader("UrlPostgres"),
                        request.getHeader("UserPostgres"),
                        request.getHeader("PasswordPostgres"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        beanFactory = BeanFactory.getInstance();

        try {
            TagController tagController = beanFactory.getBean(TagController.class);
            HttpResponse customHttpResponse = tagController.getResponse();
            tagController.buildResponse(customHttpRequest);
            // устанавливает код статуса
            response.setStatus(customHttpResponse.getStatusCode());
            // устанавливаем заголовки
            Map<String, String> customHeaders = customHttpResponse.getHeaders();
            for (Map.Entry<String, String> pair: customHeaders.entrySet()) {
                response.setHeader(pair.getKey(), pair.getValue());
            }
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
