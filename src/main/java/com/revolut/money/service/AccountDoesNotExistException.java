package com.revolut.money.service;

class AccountDoesNotExistException extends RuntimeException {
    AccountDoesNotExistException(String message) {
        super(message);
    }
}