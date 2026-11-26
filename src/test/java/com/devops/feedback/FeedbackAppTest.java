package com.devops.feedback;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FeedbackAppTest {

    @Test
    void parsesFormFields() {
        Map<String, String> f = FeedbackApp.parseForm("name=Riya+Sharma&rating=5&category=Faculty");
        assertEquals("Riya Sharma", f.get("name"));
        assertEquals("5", f.get("rating"));
        assertEquals("Faculty", f.get("category"));
    }

    @Test
    void escapesHtml() {
        assertEquals("&lt;b&gt;", FeedbackApp.escape("<b>"));
    }
}
