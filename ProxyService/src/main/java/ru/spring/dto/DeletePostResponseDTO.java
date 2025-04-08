package ru.spring.dto;

import lombok.Data;


public class DeletePostResponseDTO {
    private boolean success;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
}
