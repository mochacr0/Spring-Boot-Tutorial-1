package com.example.tutorial.security;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JwtAccessToken implements JwtToken {
    private String value;

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
