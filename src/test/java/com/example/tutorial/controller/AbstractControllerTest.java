package com.example.tutorial.controller;

import com.example.tutorial.common.data.RegisterUserRequest;
import com.example.tutorial.common.data.User;
import com.example.tutorial.security.JwtAccessToken;
import com.example.tutorial.security.JwtToken;
import com.example.tutorial.security.LoginRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import javax.xml.transform.Result;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static com.example.tutorial.controller.ControllerTestConstants.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    @Value("${security.maxFailedLoginAttempts}")
    protected int maxFailedLoginAttempts;
    @Value("${security.failedLoginLockExpirationMillis}")
    protected long failedLoginLockExpirationMillis;

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

    public <T, V> T performPost(String urlTemplate, Class<T> responseClass, V content, Object... urlVariables) throws Exception {
        return readResponse(performPost(urlTemplate, content, urlVariables), responseClass);
    }

    public <V> ResultActions performPost(String urlTemplate, V content, Object...urlVariables) throws Exception {
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

    public <T> T performPostWithEmptyBody(String urlTemplate, Class<T> responseClass, Object... urlVariables) throws Exception {
        return readResponse(performPostWithEmptyBody(urlTemplate, urlVariables), responseClass);
    }

    public <V> ResultActions performPostWithEmptyBody(String urlTemplate, Object...urlVariables) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(urlTemplate, urlVariables);
        return mockMvc.perform(builder);
    }

//    public ResultActions performPost(String urlTemplate, Object...urlVariables) throws Exception {
//        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(urlTemplate, urlVariables);
//        String contentJsonString;
//        return mockMvc.perform(builder);
//    }

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

    public JwtToken loginAndReturnTokenPair (String username, String password) throws Exception {
        ResultActions result = performPost("/auth/login", new LoginRequest(username, password)).andExpect(status().isOk());
        return readResponse(result, JwtAccessToken.class);
    }

    public ResultActions login (String username, String password) throws Exception {
        return performPost("/auth/login", new LoginRequest(username, password));
    }

    protected User createUser(String name, String email, String password, String confirmPassword) throws Exception {
        RegisterUserRequest request = new RegisterUserRequest();
        request.setName(name);
        request.setEmail(email);
        request.setPassword(password);
        request.setConfirmPassword(confirmPassword);
        return performPost(REGISTER_USER_ROUTE, User.class, request);
    }

    protected User createUser(RegisterUserRequest request) throws Exception {
        return performPost(REGISTER_USER_ROUTE, User.class, request);
    }

    protected void deleteUser(UUID userId) throws Exception {
        performDelete(DELETE_USER_BY_ID_ROUTE, userId.toString()).andExpect(status().isOk());
    }
}
