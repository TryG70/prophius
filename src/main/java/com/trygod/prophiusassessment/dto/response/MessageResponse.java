package com.trygod.prophiusassessment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse<T> {

    private String msg = "Successful";
    private T result;
}
