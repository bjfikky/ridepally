package com.benorim.ridepally.exception;

import com.benorim.ridepally.enums.ErrorCode;

public class DataOwnershipException extends BaseRidepallyException {

    public DataOwnershipException(ErrorCode errorCode) {
        super(errorCode);
    }

    public DataOwnershipException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public DataOwnershipException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}
