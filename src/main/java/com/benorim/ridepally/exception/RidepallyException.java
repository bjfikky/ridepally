package com.benorim.ridepally.exception;

import com.benorim.ridepally.enums.ErrorCode;

public class RidepallyException extends BaseRidepallyException {
    public RidepallyException(ErrorCode errorCode) {
        super(errorCode);
    }

    public RidepallyException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public RidepallyException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
