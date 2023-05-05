package com.example.tutorial.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;

public class JsonNodeStringConverter implements AttributeConverter<JsonNode, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public String convertToDatabaseColumn(JsonNode jsonNode) {
        if (jsonNode == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("The given JSON object value cannot be transformed into a String: " + e, e);
        }
    }

    @Override
    public JsonNode convertToEntityAttribute(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readTree(s);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
