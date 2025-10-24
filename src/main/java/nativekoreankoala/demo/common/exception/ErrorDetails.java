package nativekoreankoala.demo.common.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorDetails {
    private String code;
    private String field;
    private LocalDateTime timestamp;

    public static ErrorDetails of(String code) {
        return ErrorDetails.builder()
                .code(code)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ErrorDetails of(String code, String field) {
        return ErrorDetails.builder()
                .code(code)
                .field(field)
                .timestamp(LocalDateTime.now())
                .build();
    }
}