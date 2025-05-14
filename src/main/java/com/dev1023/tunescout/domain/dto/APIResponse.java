package com.dev1023.tunescout.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
//@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class APIResponse<T> {
    private T content;

    public APIResponse(T content) {
        this.content = content;
    }
}
