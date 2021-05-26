package news.service.bank;

import news.model.bank.CourseValute;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class BankService {
    private final RestTemplate restTemplate;
    private final String URL_BANK;

    public BankService(RestTemplateBuilder restTemplateBuilder, @Value("${bankurl}") String URL_BANK) {
        this.restTemplate = restTemplateBuilder.build();
        this.URL_BANK = URL_BANK;
    }

    public CourseValute getCourseValute() {
        CourseValute courseValute;

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.ALL));
        messageConverters.add(converter);
        restTemplate.setMessageConverters(messageConverters);

        try {
            courseValute = restTemplate.getForObject(URL_BANK, CourseValute.class);
        } catch (RestClientException e) {
            courseValute = null;
        }

        return courseValute;
    }
}
