package com.reliaquest.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Generic wrapper for API responses from the mock server.
 * <p>
 * The mock API returns responses in this format:
 * <pre>
 * {
 *   "status": "success",
 *   "data": { ... actual data here ... }
 * }
 * </pre>
 * <p>
 * This class extracts just the "data" field and ignores everything else.
 *
 * @param <T> the type of data contained in the response
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponse<T> {

    @JsonProperty("data")
    private T data;

    public T getData() {
        return data;
    }

    public void setData(final T data) {
        this.data = data;
    }
}
