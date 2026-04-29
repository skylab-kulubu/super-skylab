package com.skylab.superapp.core.constants;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class Messages {
    private static MessageSource messageSource;

    public Messages(MessageSource messageSource) {
        Messages.messageSource = messageSource;
    }

    public static String get(String key) {
        if (key == null) return null;
        try {
            return messageSource.getMessage(key, null, key, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            return key;
        }
    }
}
