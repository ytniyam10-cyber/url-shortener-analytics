//package com.example.url_shortener.Exception;
//
//import com.example.url_shortener.DTO.ErrorResponse;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.time.Instant;
//
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(ResponseStatusException.class)
//    public ResponseEntity<ErrorResponse> handleResponseStatusException(
//            ResponseStatusException ex, HttpServletRequest request) {
//
//        ErrorResponse error = new ErrorResponse(
//                ex.getStatusCode().value(),
//                ex.getStatusCode().toString(),
//                ex.getReason(),
//                request.getRequestURI(),
//                Instant.now().toString()
//        );
//
//        return ResponseEntity.status(ex.getStatusCode()).body(error);
//    }
//
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleGenericException(
//            Exception ex, HttpServletRequest request) {
//
//        ErrorResponse error = new ErrorResponse(
//                HttpStatus.INTERNAL_SERVER_ERROR.value(),
//                "Internal Server Error",
//                "Something went wrong. Please try again later.",
//                request.getRequestURI(),
//                Instant.now().toString()
//        );
//
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
//    }
//}
