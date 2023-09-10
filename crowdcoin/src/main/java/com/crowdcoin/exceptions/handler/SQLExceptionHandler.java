package com.crowdcoin.exceptions.handler;

import com.ratchet.exceptions.handler.GuardianHandler;
import com.ratchet.exceptions.handler.strategies.ExceptionStrategy;
import com.ratchet.exceptions.handler.strategies.WarningException;

import java.sql.SQLException;

public class SQLExceptionHandler implements GuardianHandler<SQLException> {

    // SQLException Codes and their corresponding error messages to display
    private enum SQLExceptionCode {

        ACCESS_DENIED(1045,"Request failed due to insufficient privileges, if you believe this to be a mistake please contact an administrator"),
        SYNTAX_ERROR(1064,"Request failed due to a bad query, please check that you are using the most up to date version of this software then try again"),
        NO_DATA_RETURNED(5000,"No data was able to be retrieved for this query, if you believe this to be a mistake please contact an administrator");

        private int code;
        private String message;

        SQLExceptionCode(int code,String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return this.code;
        }

        public String getMessage() {
            return this.message;
        }

    }

    @Override
    public void handleException(SQLException exception) {
        int errCode = exception.getErrorCode();
        for (SQLExceptionCode SQLExc : SQLExceptionCode.values()) {
            if (errCode == SQLExc.getCode()) {
                ExceptionStrategy<SQLException> exceptionStrategy = new WarningException<>(SQLExc.getMessage());
                exceptionStrategy.handleException(exception);
                return;
            }
        }

        ExceptionStrategy<SQLException> exceptionStrategy = new WarningException<>("Exception code: " + exception.getErrorCode() + ". If this continues to occur, please contact an administrator");
        exceptionStrategy.handleException(exception);

    }
}
