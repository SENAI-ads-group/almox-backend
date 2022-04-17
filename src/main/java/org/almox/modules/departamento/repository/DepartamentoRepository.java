package org.almox.modules.departamento.repository;

import org.almox.modules.departamento.model.Departamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, UUID> {

    String QUERY_TODOS_POR_DESCRICAO = "FROM Departamento as d WHERE LOWER(d.descricao) LIKE CONCAT('%', TRIM(LOWER(:descricao)), '%')";
    String QUERY_INATIVOS_POR_DESCRICAO = "FROM Departamento as d WHERE d.dataExclusao IS NOT NULL AND LOWER(d.descricao) LIKE CONCAT('%', TRIM(LOWER(:descricao)), '%')";
    String QUERY_ATIVOS_POR_DESCRICAO = "FROM Departamento as d WHERE d.dataExclusao IS NULL AND LOWER(d.descricao) LIKE CONCAT('%', TRIM(LOWER(:descricao)), '%')";

    String QUERY_ID_OPERADORES_ASSOCIADOS_AO_DEPARTAMENTO = "SELECT DISTINCT CAST(DPTO_ID AS VARCHAR) FROM DPTO_DEPARTAMENTO_OPERADORES WHERE CAST(DPTO_ID AS VARCHAR) = :idDepartamento";

    @Query(value = "select * from DPTO_DEPARTAMENTO d join produtos_departamentos p on p.DPTO_ID = d.DPTO_ID where p.prod_id = ?1",
            nativeQuery = true)
    List<Departamento> findAllByProduct(Long idProduto);

    @Query(value = "select\n" +
            "    *\n" +
            "from\n" +
            "    DPTO_DEPARTAMENTO dpto\n" +
            "where\n" +
            "    DPTO_ID not in (\n" +
            "    select\n" +
            "        pd.DPTO_ID\n" +
            "    from\n" +
            "        produtos_departamentos pd\n" +
            "    where\n" +
            "        pd.DPTO_ID = dpto.DPTO_ID\n" +
            "        and pd.prod_id = ?1);",
            nativeQuery = true)
    List<Departamento> findAllByNotProduct(Long idProduto);

    Optional<Departamento> findFirstByDescricaoEquals(String descricao);

    @Query(QUERY_TODOS_POR_DESCRICAO)
    List<Departamento> buscarPorDescricao(@Param("descricao") String descricao, Sort sort);
    @Query(QUERY_TODOS_POR_DESCRICAO)
    Page<Departamento> buscarPorDescricao(@Param("descricao") String descricao, Pageable pageable);

    @Query(QUERY_ATIVOS_POR_DESCRICAO)
    List<Departamento> buscarAtivosPorDescricao(@Param("descricao") String descricao, Sort sort);
    @Query(QUERY_ATIVOS_POR_DESCRICAO)
    Page<Departamento> buscarAtivosPorDescricao(@Param("descricao") String descricao, Pageable pageable);

    @Query(QUERY_INATIVOS_POR_DESCRICAO)
    List<Departamento> buscarExcluidosPorDescricao(@Param("descricao") String descricao, Sort sort);
    @Query(QUERY_INATIVOS_POR_DESCRICAO)
    Page<Departamento> buscarExcluidosPorDescricao(@Param("descricao") String descricao, Pageable pageable);

    @Query(nativeQuery = true, value = QUERY_ID_OPERADORES_ASSOCIADOS_AO_DEPARTAMENTO)
    Set<String> buscarIdOperadoresAssociadosAoDepartamento(String idDepartamento);
}
