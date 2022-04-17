package org.almox.modules.departamento.service;

import lombok.RequiredArgsConstructor;
import org.almox.core.config.validation.ValidatorAutoThrow;
import org.almox.core.exceptions.EntidadeNaoEncontradaException;
import org.almox.core.exceptions.ForbiddenException;
import org.almox.core.exceptions.RegraNegocioException;
import org.almox.modules.departamento.model.Departamento;
import org.almox.modules.departamento.dto.FiltroDepartamento;
import org.almox.modules.departamento.repository.DepartamentoRepository;
import org.almox.modules.operador.model.Operador;
import org.almox.modules.operador.service.OperadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DepartamentoServiceImpl implements DepartamentoService {

    private final DepartamentoRepository repository;
    private final ValidatorAutoThrow validator;
    private final Operador operadorLogado;
    private final OperadorService operadorService;

    @Override
    public Departamento criar(Departamento departamento) {
        validator.validate(departamento);
        repository.findFirstByDescricaoEquals(departamento.getDescricao()).ifPresent(departamentoComMesmaDescricao -> {
            throw new RegraNegocioException("${descricao_ja_cadastrada}");
        });
        Set<Operador> operadoresValidados = departamento.getOperadores().stream()
                .map(Operador::getId)
                .map(operadorService::buscarPorId)
                .collect(Collectors.toSet());

        departamento.setOperadores(operadoresValidados);
        return repository.save(departamento);
    }

    @Override
    public Departamento buscarPorId(UUID id) {
        Departamento departamentoEncontrado = repository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("${departamento_nao_encontrado}"));

        if (!operadorService.isAdministrador(operadorLogado)) {
            Set<String> idOperadoresAssociadoAoDepartamentoEncontrado = repository.buscarIdOperadoresAssociadosAoDepartamento(departamentoEncontrado.getId().toString());
            if (!idOperadoresAssociadoAoDepartamentoEncontrado.contains(operadorLogado.getId().toString())) {
                throw new ForbiddenException("${operador_logado_nao_pertence_departamento}");
            }
        }
        return departamentoEncontrado;
    }

    @Override
    public List<Departamento> buscar(FiltroDepartamento filtro, Sort sort) {
        validator.validate(filtro);

        boolean isAdministrador = operadorService.isAdministrador(operadorLogado);
        if (isAdministrador) {
            if (isConsiderarTodos(filtro))
                return repository.buscarPorDescricao(filtro.descricao, sort);
            else if (isConsiderarApenasExcluidos(filtro))
                return repository.buscarExcluidosPorDescricao(filtro.descricao, sort);
        }
        return repository.buscarAtivosPorDescricao(filtro.descricao, sort);
    }

    @Override
    public Page<Departamento> buscarPaginado(FiltroDepartamento filtro, Pageable pageable) {
        validator.validate(filtro);
        boolean isAdministrador = operadorService.isAdministrador(operadorLogado);

        if (isAdministrador) {
            if (isConsiderarTodos(filtro))
                return repository.buscarPorDescricao(filtro.descricao, pageable);
            else if (isConsiderarApenasExcluidos(filtro))
                return repository.buscarExcluidosPorDescricao(filtro.descricao, pageable);
        }
        return repository.buscarAtivosPorDescricao(filtro.descricao, pageable);
    }

    @Transactional
    @Override
    public Departamento atualizar(UUID id, Departamento departamento) {
        Departamento departamentoEncontrado = buscarPorId(id);
        departamento.setId(id);
        departamento.setCriadoPor(departamentoEncontrado.getCriadoPor());
        departamento.setDataCriacao(departamentoEncontrado.getDataCriacao());

        validator.validate(departamento);
        Departamento departamentoAtualizado = repository.save(departamento);
        return departamentoAtualizado;
    }

    @Override
    public void excluir(UUID id) {
        Departamento departamentoASerExcluido = buscarPorId(id);
        setExclusaoAuditoria(departamentoASerExcluido, operadorLogado);
        repository.save(departamentoASerExcluido);
    }
}
