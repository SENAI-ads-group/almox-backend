package org.almox.modules.operador.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.almox.modules.common.EntidadePadrao;
import org.almox.modules.pessoa.model.PessoaFisica;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "ope_operador")
@Builder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Operador implements EntidadePadrao, UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ope_id")
    private UUID id;

    @NotNull(message = "{Operador.idPessoa.NotNull}")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pess_id")
    private PessoaFisica pessoa;

    @NotBlank(message = "${Operador.login.NotBlank}")
    @Column(name = "ope_login")
    private String login;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "ope_operador_funcoes",
            joinColumns = @JoinColumn(name = "ope_id"),
            inverseJoinColumns = @JoinColumn(name = "func_id")
    )
    private Set<Funcao> funcoes;

    @NotBlank(message = "${Operador.senha.NotBlank}")
    @Column(name = "ope_senha")
    private String senha;

    @Override
    public boolean isNew() {
        return id == null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return funcoes.stream()
                .map(Funcao::getNome)
                .map(SimpleGrantedAuthority::new).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // TODO implementar campo booleano
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}