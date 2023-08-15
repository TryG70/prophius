package com.trygod.prophiusassessment.dto.response;

import lombok.Data;

/*
    Dynoform compatible storage API response
 */
@Data
public class StorageResponse {
    String key;
    String name;
    String url;
    Long size;
}
