package org.zalando.riptide;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

import static com.google.common.base.MoreObjects.firstNonNull;

final class MockSetup {

    private static final List<HttpMessageConverter<?>> DEFAULT_CONVERTERS =
            Arrays.asList(new StringHttpMessageConverter(), defaultJsonConverter());

    private final String baseUrl;
    private final Iterable<HttpMessageConverter<?>> converters;
    private final RestTemplate template;
    private final MockRestServiceServer server;

    private static MappingJackson2HttpMessageConverter defaultJsonConverter() {
        final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(new ObjectMapper().findAndRegisterModules());
        return converter;
    }

    public MockSetup() {
        this("https://api.example.com", null);
    }

    public MockSetup(@Nullable final String baseUrl) {
        this(baseUrl, null);
    }

    public MockSetup(@Nullable final String baseUrl, @Nullable final Iterable<HttpMessageConverter<?>> converters) {
        this.baseUrl = baseUrl;
        this.converters = converters;
        this.template = new RestTemplate();
        this.server = MockRestServiceServer.createServer(template);
    }

    public MockRestServiceServer getServer() {
        return server;
    }

    public Http.ConfigurationStage getHttpBuilder() {
        return Http.builder()
                .executor(Executors.newSingleThreadExecutor())
                .requestFactory(template.getRequestFactory())
                .converters(firstNonNull(converters, DEFAULT_CONVERTERS))
                .baseUrl(baseUrl)
                .defaultPlugins();
    }
    public Http getHttp() {
        return getHttpBuilder().build();
    }

}
