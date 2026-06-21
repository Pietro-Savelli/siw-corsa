package it.uniroma3.siw.exception;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;

import org.springframework.ui.Model;

import org.springframework.web.bind.MissingServletRequestParameterException;

import org.springframework.web.bind.annotation.ControllerAdvice;

import org.springframework.web.bind.annotation.ExceptionHandler;

import org.springframework.web.bind.annotation.ResponseStatus;

import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import org.springframework.web.server.ResponseStatusException;



import jakarta.servlet.http.HttpServletResponse;



@ControllerAdvice

public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDenied(AccessDeniedException e, Model model) {

        model.addAttribute("errorMessage", e.getMessage());

        return "error/403";

    }


    @ExceptionHandler(ResponseStatusException.class)
    public String handleResponseStatusException(ResponseStatusException e, Model model, HttpServletResponse response) {

        int statusCode = e.getStatusCode().value();

        response.setStatus(statusCode);

        model.addAttribute("errorMessage", e.getReason() != null ? e.getReason() : e.getMessage());



        if (statusCode == HttpStatus.BAD_REQUEST.value()) {

            return "error/400";

        }

        if (statusCode == HttpStatus.FORBIDDEN.value()) {

            return "error/403";

        }

        if (statusCode == HttpStatus.NOT_FOUND.value()) {

            return "error/404";

        }

        return "error/500";

    }


    @ExceptionHandler({

            MethodArgumentTypeMismatchException.class,

            MissingServletRequestParameterException.class

    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequest(Exception e, Model model) {

        model.addAttribute("errorMessage", "Bad request");

        return "error/400";

    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception e, Model model) {

        model.addAttribute("errorMessage", "Internal server error: ");

        logger.error("An unexpected error occurred:", e);

        return "error/500";

    }

}