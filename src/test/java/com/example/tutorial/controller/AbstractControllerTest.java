package com.example.tutorial.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.List;

@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractControllerTest {
    @LocalServerPort
    private int port;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    protected final MediaType CONTENT_TYPE = MediaType.APPLICATION_JSON;
    protected final int NEGATIVE_INT_VALUE = Integer.MIN_VALUE;
    protected final int ZERO_INT_VALUE = 0;
    protected final String STRING_VALUE = "string";
    protected final String INVALID_SORT_DIRECTION = "invalidDirection";
    protected final String INVALID_SORT_PROPERTY = "theresNoWayThatAPropertyCanBeLikeThis";
    protected final String DEFAULT_USER_EMAIL = "defaultuser@gmail.com";
    protected final String DEFAULT_USER_NAME = "defaultuser";
    protected final String DEFAULT_PASSWORD = "Defaultpassword";



//    @SuppressWarnings("rawtypes")
//    private HttpMessageConverter mappingJackson2HttpMessageConverter;
//    @SuppressWarnings("rawtypes")
//    private HttpMessageConverter stringHttpMessageConverter;

//    @Autowired
//    void setConverters(List<HttpMessageConverter> converters) {
//        mappingJackson2HttpMessageConverter = converters
//                .stream()
//                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
//                .findAny()
//                .get();
//        stringHttpMessageConverter = converters
//                .stream()
//                .filter(hmc -> hmc instanceof StringHttpMessageConverter)
//                .findAny()
//                .get();
//        Assert.notNull(this.mappingJackson2HttpMessageConverter, "JSON message convert can not be null");
//    }

    void setup() {
    }

    public <T> T performGet(String urlTemplate, Class<T> responseClass, Object... urlVariables) throws Exception {
        return readResponse(performGet(urlTemplate, urlVariables), responseClass);
    }

    public <T> T performGetWithReferencedType(String urlTemplate, TypeReference<T> type, Object... urlVariables) throws Exception {
        return readResponse(performGet(urlTemplate, urlVariables), type);
    }

    public ResultActions performGet(String urlTemplate, Object... urlVariables) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(urlTemplate, urlVariables);
        return mockMvc.perform(builder);
    }

    public <T, D> T performPost(String urlTemplate, Class<T> responseClass, D content, Object... urlVariables) throws Exception {
        return readResponse(performPost(urlTemplate, content, urlVariables), responseClass);
    }

    public <T> ResultActions performPost(String urlTemplate, T content, Object...urlVariables) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(urlTemplate, urlVariables);
        String contentJsonString;
        if (content != null) {
            try {
                contentJsonString = objectMapper.writeValueAsString(content);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            builder.content(contentJsonString).contentType(CONTENT_TYPE);
        }
        return mockMvc.perform(builder);
    }

    public ResultActions performDelete(String urlTemplate, Object... urlVariables) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.delete(urlTemplate, urlVariables);
        return mockMvc.perform(builder);
    }

    <T> T readResponse(ResultActions result, Class<T> responseClass) throws IOException {
        byte[] content = result.andReturn().getResponse().getContentAsByteArray();
        return objectMapper.readValue(content, responseClass);
    }

    <T> T readResponse(ResultActions result, TypeReference<T> type) throws IOException {
        byte[] content = result.andReturn().getResponse().getContentAsByteArray();
        return objectMapper.readValue(content, type);
    }
}
