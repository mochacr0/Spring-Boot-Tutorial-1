package com.example.tutorial.config;

import lombok.Getter;
import lombok.Setter;
import org.passay.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "security.password-policy")
@Getter
@Setter
public class UserPasswordPolicyConfiguration {
    private boolean whitespacesAllowed;
    private int minimumLength;
    private int minimumLowerCharacters;
    private int minimumUpperCharacters;
    private int minimumSpecialCharacters;
    private int maxFailedLoginAttempts;
    private int passwordReuseFrequencyDays;

    public List<Rule> getPasswordRules () {
        List<Rule> passwordRules = new ArrayList<>();
        if (this.getMinimumLength() > 0) {
            passwordRules.add(new LengthRule(this.getMinimumLength(), Integer.MAX_VALUE));
        }
        if (!this.isWhitespacesAllowed()) {
            passwordRules.add(new WhitespaceRule());
        }
        passwordRules.addAll(getPasswordCharacterRules());
        return passwordRules;
    }

    public List<CharacterRule> getPasswordCharacterRules() {
        List<CharacterRule> passwordCharacterRules = new ArrayList<>();
        if (this.getMinimumLowerCharacters() > 0) {
            passwordCharacterRules.add(new CharacterRule(EnglishCharacterData.LowerCase, this.getMinimumLowerCharacters()));
        }
        if (this.getMinimumUpperCharacters() > 0) {
            passwordCharacterRules.add(new CharacterRule(EnglishCharacterData.UpperCase, this.getMinimumUpperCharacters()));
        }
        if (this.getMinimumSpecialCharacters() > 0) {
            passwordCharacterRules.add(new CharacterRule(EnglishCharacterData.Special, this.getMinimumSpecialCharacters()));
        }
        return passwordCharacterRules;
    }
    //activateTokenExpiryTime
    //resetPasswordTokenExpiryTime;

}
