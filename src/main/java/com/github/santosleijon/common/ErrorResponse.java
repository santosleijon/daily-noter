package com.github.santosleijon.common;

public record ErrorResponse(String description) {

    // TODO: Replace with a JSON serializer like Jackson ObjectMapper
    public String toJson() {
        return String.format("{ \"error\": \"%s\" }", description);
    }
}
