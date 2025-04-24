//package com.TodayTask.Admin.Panel.Exceptions;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import org.springframework.web.context.request.WebRequest;
//
//import java.time.LocalDateTime;
//
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//    // Handle generic exception
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<String> handleGenericException(Exception ex) {
//        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//
////    // Handle validation exception
////    @ExceptionHandler(MethodArgumentNotValidException.class)
////    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) {
////        return new ResponseEntity<>("Validation failed: " + ex.getBindingResult().toString(), HttpStatus.BAD_REQUEST);
////    }
//
//    // Handle specific exception
//    @ExceptionHandler(ResourceNotFoundException.class)
//    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException ex) {
//        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
//    }
//}
////