package com.almox.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

public enum StatusOrcamento implements IEnum {
    PLANEJADO("Planejado"),
    ABERTO("Aberto"),
    FECHADO("Fechado");

    @Getter
    private String descricao;

    StatusOrcamento(String descricao) {
        this.descricao = descricao;
    }

    @JsonCreator
    public static StatusOrcamento deserialize(@JsonProperty("type") String type) {
        return IEnum.fromType(values(), type);
    }

    @Override
    public String getType() {
        return name();
    }
}
