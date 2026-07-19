package com.pfm.application.common;

public interface QueryHandler<Q, R> {
    R handle(Q query);
}