package com.skylab.superapp.core.utilities.mail.skymail;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class SendSingleMailRequest {

    @JsonProperty("template_id")
    private String templateId;

    @JsonProperty("recipient_email")
    private String recipientEmail;

    @JsonProperty("recipient_full_name")
    private String recipientFullName;

    @JsonProperty("body_variables")
    private Map<String, Object> bodyVariables;
}
