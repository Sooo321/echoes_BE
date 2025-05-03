package org.example.echoes_be.common;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ApiResponse<T> {
    //T : 제너릭 타입을 나타내는 표기법. 제네릭은 클래스나 메서드가 다양한 타입을 처리할 수 있도록 해줌.
    private boolean success;
    private T response;
    private String error;

    public ApiResponse() {
    }

    public ApiResponse(boolean success, T response, String error) {
        this.success = success;
        this.response = response;
        this.error = error;
    }

    public static <T> ApiResponse<T> success(T response) {
        return new ApiResponse<>(true, response, null);
    }

    public static <T> ApiResponse<T> error(String error) {
        return new ApiResponse<>(false, null, error);
    }

}