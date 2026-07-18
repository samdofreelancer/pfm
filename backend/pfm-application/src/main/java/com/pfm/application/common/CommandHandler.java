package com.pfm.application.common;

public interface CommandHandler<C, R> {
    R handle(C command);
}