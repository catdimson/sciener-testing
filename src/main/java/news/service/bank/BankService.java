package news.service.bank;

import news.model.bank.CourseValute;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BankService {
    private final RestTemplate restTemplate;
    private final String URL_BANK;

    public BankService(RestTemplateBuilder restTemplateBuilder, String url) {
        this.restTemplate = restTemplateBuilder.build();
        this.URL_BANK = url; // "https://www.cbr-xml-daily.ru/daily_json.js";
    }

    public CourseValute getCourseValute() {
        CourseValute courseValute;

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        messageConverters.add(converter);
        restTemplate.setMessageConverters(messageConverters);
        courseValute = restTemplate.getForObject(URL_BANK, CourseValute.class);

        return courseValute;
    }
}
