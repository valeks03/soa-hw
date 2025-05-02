package ru.spring.dto.commentservice;

public class ViewLikeResponseDto {
    private boolean success;
    private int count;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
