package com.galaxium.booking.dto;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Result type for service layer operations.
 * Mimics Python's Union[ModelOut, ErrorResponse] pattern.
 * 
 * @param <T> The success type
 */
public sealed interface Result<T> permits Result.Success, Result.Failure {
    
    /**
     * Success result containing a value.
     */
    record Success<T>(T value) implements Result<T> {
        public boolean isSuccess() {
            return true;
        }
        
        public boolean isFailure() {
            return false;
        }
    }
    
    /**
     * Failure result containing an error.
     */
    record Failure<T>(ErrorResponse error) implements Result<T> {
        public boolean isSuccess() {
            return false;
        }
        
        public boolean isFailure() {
            return true;
        }
    }
    
    /**
     * Check if this result is a success.
     */
    default boolean isSuccess() {
        return this instanceof Success;
    }
    
    /**
     * Check if this result is a failure.
     */
    default boolean isFailure() {
        return this instanceof Failure;
    }
    
    /**
     * Get the success value or throw if failure.
     */
    default T getValue() {
        if (this instanceof Success<T> success) {
            return success.value();
        }
        throw new IllegalStateException("Cannot get value from failure result");
    }
    
    /**
     * Get the error or throw if success.
     */
    default ErrorResponse getError() {
        if (this instanceof Failure<T> failure) {
            return failure.error();
        }
        throw new IllegalStateException("Cannot get error from success result");
    }
    
    /**
     * Map the success value to another type.
     */
    default <U> Result<U> map(Function<T, U> mapper) {
        if (this instanceof Success<T> success) {
            return new Success<>(mapper.apply(success.value()));
        }
        return new Failure<>(((Failure<T>) this).error());
    }
    
    /**
     * Execute action if success.
     */
    default Result<T> ifSuccess(Consumer<T> action) {
        if (this instanceof Success<T> success) {
            action.accept(success.value());
        }
        return this;
    }
    
    /**
     * Execute action if failure.
     */
    default Result<T> ifFailure(Consumer<ErrorResponse> action) {
        if (this instanceof Failure<T> failure) {
            action.accept(failure.error());
        }
        return this;
    }
    
    /**
     * Create a success result.
     */
    static <T> Result<T> success(T value) {
        return new Success<>(value);
    }
    
    /**
     * Create a failure result.
     */
    static <T> Result<T> failure(ErrorResponse error) {
        return new Failure<>(error);
    }
    
    /**
     * Create a failure result with error details.
     */
    static <T> Result<T> failure(String error, String errorCode, String details) {
        return new Failure<>(new ErrorResponse(error, errorCode, details));
    }
    
    /**
     * Create a failure result without details.
     */
    static <T> Result<T> failure(String error, String errorCode) {
        return new Failure<>(new ErrorResponse(error, errorCode));
    }
}

// Made with Bob
