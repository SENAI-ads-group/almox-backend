package com.almox.model.dto;

import com.almox.model.enums.StatusRequisicao;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FiltroRequisicaoDTO {
    private StatusRequisicao status;
}
