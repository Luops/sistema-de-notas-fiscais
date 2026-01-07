package dev.ellyon.sistemanotas.dto.generics;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuccessResponseDTO {
    private int status;
    private String message;
    private Object data;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime timestamp;

    public SuccessResponseDTO() {
        this.timestamp = LocalDateTime.now();
    }

    public SuccessResponseDTO(int status, String message) {
        this();
        this.status = status;
        this.message = message;
    }

    public SuccessResponseDTO(int status, String message, Object data) {
        this();
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // Getters e Setters
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
