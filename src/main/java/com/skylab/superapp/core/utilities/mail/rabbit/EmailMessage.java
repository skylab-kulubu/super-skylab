package com.skylab.superapp.core.utilities.mail.rabbit;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EmailMessage {
    private String to;
    private String subject;
    private String body;

    public EmailMessage() {}

    @JsonCreator
    public EmailMessage(@JsonProperty("to") String to,
                        @JsonProperty("subject") String subject,
                        @JsonProperty("body") String body) {
        this.to = to;
        this.subject = subject;
        this.body = body;
    }

    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
}
