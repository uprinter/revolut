package com.revolut.money.service;

class NotEnoughMoneyException extends RuntimeException {
    NotEnoughMoneyException(String message) {
        super(message);
    }
}