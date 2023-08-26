package com.drprog.sjournal.db.exception;

public class NotValidBackupDbException extends Exception {
    public NotValidBackupDbException(String cause) {
        super("File is not valid DB backup! Cause: " + cause);
    }
}
