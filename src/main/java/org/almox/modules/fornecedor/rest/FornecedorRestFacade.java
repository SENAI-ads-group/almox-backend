package org.almox.modules.fornecedor.rest;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.almox.core.rest.RestCollection;
import org.almox.core.rest.RestInterface;
import org.almox.modules.auditoria.FiltroStatusAuditoria;
import org.almox.modules.fornecedor.model.FornecedorDTO;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@Tag(name = "Fornecedores", description = "Operações relacionadas aos recursos de fornecedores")
@RequestMapping("/fornecedores")
public interface FornecedorRestFacade extends RestInterface {

    @GetMapping
    @Parameters({
            @Parameter(
                    in = ParameterIn.QUERY,
                    name = "cnpj",
                    description = "CNPJ da pessoa associada ao fornecedor",
                    schema = @Schema(type = "string")
            ),
            @Parameter(
                    in = ParameterIn.QUERY,
                    name = "nome",
                    description = "Nome da pessoa associada ao fornecedor",
                    schema = @Schema(type = "string")
            ),
            @Parameter(
                    in = ParameterIn.QUERY,
                    name = "statusAuditoria",
                    description = "Status de auditoria do fornecedor",
                    schema = @Schema(implementation = FiltroStatusAuditoria.class)
            )
    })
    @PageableAsQueryParam
    ResponseEntity<RestCollection<FornecedorDTO>> buscar(
            @RequestParam(required = false) String cnpj,
            @RequestParam(required = false, defaultValue = "") String nome,
            @RequestParam(required = false, defaultValue = "APENAS_ATIVOS") FiltroStatusAuditoria.Tipo statusAuditoria,
            @RequestParam(required = false) Optional<Integer> page,
            @RequestParam(required = false) Optional<Integer> size,
            @RequestParam(required = false, defaultValue = "id") String[] sort
    );

    @GetMapping("/{id}")
    ResponseEntity<FornecedorDTO> buscarPorId(@PathVariable("id") UUID id);

    @PostMapping
    ResponseEntity<Void> criar(@RequestBody FornecedorDTO dto);

    @PutMapping("/{id}")
    ResponseEntity<FornecedorDTO> atualizar(@PathVariable("id") UUID id, @RequestBody FornecedorDTO dto);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> excluir(@PathVariable("id") UUID id);
}
