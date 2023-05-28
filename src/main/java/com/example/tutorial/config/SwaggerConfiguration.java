package com.example.tutorial.config;

import com.example.tutorial.exception.TutorialErrorResponse;
import com.example.tutorial.security.JwtAccessToken;
import com.example.tutorial.security.LoginRequest;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.List;

import static com.example.tutorial.controller.ControllerConstants.AUTH_LOGIN_ENDPOINT;

@Slf4j
@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .servers(getServers())
                .externalDocs(getGlobalExternalDocs())
                .info(getApiInfo());
    }

    public List<Server> getServers() {
        return Collections.singletonList(new Server()
                .description("Default server url")
                .url("http://localhost:5000"));
    }

    public ExternalDocumentation getGlobalExternalDocs() {
        return new ExternalDocumentation()
                .description("Find out more about Swagger")
                .url("http://swagger.io");
    }

    private Info getApiInfo() {
        return new Info()
                .title("Tutorial APIs")
                .description("APIs for Tutorial Application")
                .summary("This is a summary")
                .contact(new Contact().email("nthai2001cr@gmail.com"))
                .termsOfService("http://swagger.io/terms/")
                .license(new License().name("Apache 2.0").url("http://www.apache.org/licenses/LICENSE-2.0.html"))
                .version("1.0.0");
    }

    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> {
            customizeGlobalDefaultErrorResponses(openApi);
            customizeLoginEndpoint(openApi);
        };
    }

    private void customizeGlobalDefaultErrorResponses(OpenAPI openApi) {
        openApi.getComponents().getSchemas().putAll(ModelConverters.getInstance().read(TutorialErrorResponse.class));
        openApi.getPaths().forEach((s, pathItem) -> {
            pathItem.readOperations().forEach(operation -> {
                ApiResponses responses = operation.getResponses();
                responses.put("400", getApiErrorResponse(HttpStatus.BAD_REQUEST, "Unauthorized"));
                responses.put("401", getApiErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid UUID string"));
                responses.put("403", getApiErrorResponse(HttpStatus.FORBIDDEN, "You don't have permission to perform this action"));
                responses.put("404", getApiErrorResponse(HttpStatus.NOT_FOUND, "Requested item wasn't found"));
                responses.put("500", getApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"));
            });
        });
    }

    private ApiResponse getApiErrorResponse(HttpStatus status, String message) {
        TutorialErrorResponse errorResponse = new TutorialErrorResponse(status, message);
        ApiResponse apiErrorResponse = new ApiResponse()
                .description(status.getReasonPhrase())
                .content(new Content()
                        .addMediaType(MediaType.APPLICATION_JSON_VALUE,
                                new io.swagger.v3.oas.models.media.MediaType()
                                        .example(errorResponse)
                                        .schema(new Schema()
                                                .$ref(TutorialErrorResponse.class.getSimpleName()))));
        return apiErrorResponse;
    }

    private void customizeLoginEndpoint(OpenAPI openApi) {
        openApi.getComponents().getSchemas().putAll(ModelConverters.getInstance().read(LoginRequest.class));
        openApi.getComponents().getSchemas().putAll(ModelConverters.getInstance().read(JwtAccessToken.class));
        PathItem loginEndpoint = openApi().getPaths().get(AUTH_LOGIN_ENDPOINT);
        if (loginEndpoint != null) {
            return;
        }
        loginEndpoint = new PathItem();
        openApi.addTagsItem(new Tag().name("Login Endpoint").description("Login Endpoint"));
        loginEndpoint.post(new Operation()
                .addTagsItem("Login Endpoint")
                .summary("Login method to get user JWT token data")
                .description("Login method used to authenticate user and get JWT token data." +
                        "\n\n Value of the response **token** field can be used as **Authorization** header value: `Authorization: Bearer $JWT_TOKEN_VALUE`.")
                .requestBody(new RequestBody()
                        .content(new Content()
                                .addMediaType(MediaType.APPLICATION_JSON_VALUE,
                                        new io.swagger.v3.oas.models.media.MediaType()
                                                .schema(new Schema()
                                                        .$ref(LoginRequest.class.getSimpleName())))))
                .responses(getLoginResponses()));
        openApi.path(AUTH_LOGIN_ENDPOINT, loginEndpoint);
    }

    private ApiResponses getLoginResponses() {
        ApiResponses responses = new ApiResponses();
        responses.addApiResponse("200", getLoginSuccessResponse());
        responses.addApiResponse("400", getApiErrorResponse(HttpStatus.BAD_REQUEST, "Invalid username or password"));
        responses.addApiResponse("401", getApiErrorResponse(HttpStatus.UNAUTHORIZED, "Unauthorized"));
        responses.addApiResponse("500", getApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"));
        return responses;
    }

    private ApiResponse getLoginSuccessResponse() {
        return new ApiResponse().description(HttpStatus.OK.getReasonPhrase())
                .content(new Content()
                        .addMediaType(MediaType.APPLICATION_JSON_VALUE,
                                new io.swagger.v3.oas.models.media.MediaType()
                                        .schema(new Schema()
                                                .$ref(JwtAccessToken.class.getSimpleName()))));
    }
}
