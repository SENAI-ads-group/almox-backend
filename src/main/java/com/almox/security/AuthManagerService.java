package com.almox.security;

import com.almox.exceptions.ApplicationRuntimeException;
import com.almox.model.dto.ErroPadraoDTO;
import com.almox.model.entidades.Usuario;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AuthManagerService {

    private final WebClient webClientAuthServer;

    @Autowired
    public AuthManagerService(WebClient webClientAuthServer) {
        this.webClientAuthServer = webClientAuthServer;
    }

    public Usuario criarUsuario(Usuario usuario) {
        return webClientAuthServer.post()
                .body(BodyInserters.fromValue(usuario))
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, res -> {
                    try {
                        String jsonStr = new ObjectMapper().writeValueAsString(usuario);
                        Mono<ErroPadraoDTO> monoErro = res.bodyToMono(ErroPadraoDTO.class);
                        monoErro.subscribe(err -> {
                            System.err.println("ERROR RESPONSE MESSAGE");
                        });
                        throw new ApplicationRuntimeException(HttpStatus.BAD_REQUEST);
                    } catch (JsonProcessingException e) {
                        throw new ApplicationRuntimeException(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }).bodyToMono(Usuario.class).block();
    }
}