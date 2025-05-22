package com.benorim.ridepally.exception;

import com.benorim.ridepally.enums.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RidepallyException extends RuntimeException {
    private final ErrorCode errorCode;

    public RidepallyException(ErrorCode errorCode) {
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }

    public RidepallyException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public RidepallyException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

}
