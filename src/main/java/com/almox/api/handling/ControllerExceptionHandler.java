package com.almox.api.handling;

import com.almox.exceptions.EntidadeNaoEncontradaException;
import com.almox.model.dto.ErroPadraoDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class ControllerExceptionHandler {

    public static final String ERRO_ENTIDADE_NAO_ENCONTRADA = "Entidade não encontrada";
    public static final String ERRO_ENTIDADE_METHOD_ARGUMENT_NOT_VALID = "Parâmetro(s) inválido";

    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ResponseEntity<ErroPadraoDTO> resourceNotFound(EntidadeNaoEncontradaException e, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        ErroPadraoDTO erroPadraoDTO = new ErroPadraoDTO(httpStatus, ERRO_ENTIDADE_NAO_ENCONTRADA, request.getRequestURI(), e.getMessage());
        return ResponseEntity.status(httpStatus).body(erroPadraoDTO);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErroPadraoDTO> resourceNotFound(EntityNotFoundException e, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        ErroPadraoDTO erroPadraoDTO = new ErroPadraoDTO(httpStatus, ERRO_ENTIDADE_NAO_ENCONTRADA, request.getRequestURI(), e.getMessage());
        return ResponseEntity.status(httpStatus).body(erroPadraoDTO);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroPadraoDTO> resourceNotFound(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        List<String> mensagensErro = e.getAllErrors().stream()//
                .map(ObjectError::getDefaultMessage)// Mensagem definida no messages.properties
                .collect(Collectors.toList());

        ErroPadraoDTO erroPadraoDTO = new ErroPadraoDTO(httpStatus, ERRO_ENTIDADE_METHOD_ARGUMENT_NOT_VALID, request.getRequestURI(), mensagensErro);
        return ResponseEntity.status(httpStatus).body(erroPadraoDTO);
    }
}
