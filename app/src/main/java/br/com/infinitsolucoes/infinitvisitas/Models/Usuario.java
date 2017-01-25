package br.com.infinitsolucoes.infinitvisitas.Models;

import org.jetbrains.annotations.NotNull;

public class Usuario extends InfinitOdin {
    @NotNull
    String nomeCompleto = "";
    @NotNull
    String email = "";
    @NotNull
    String usuario = "";

    public Usuario(@NotNull String nomeCompleto
            , @NotNull String email
            , @NotNull String usuario) {
        setEmail(email);
        setUsuario(usuario);
        setNomeCompleto(nomeCompleto);
    }

    @NotNull
    public String getUsuario() {
        if (usuario != null)
            return usuario;
        usuario = "";
        return usuario;
    }

    public void setUsuario(@NotNull String usuario) {
        this.usuario = usuario;
    }

    @NotNull
    public String getNomeCompleto() {
        if (nomeCompleto != null)
            return nomeCompleto;
        nomeCompleto = "";
        return nomeCompleto;
    }

    public void setNomeCompleto(@NotNull String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    @NotNull
    public String getEmail() {
        if (email != null)
            return email;
        email = "";
        return email;
    }

    public void setEmail(@NotNull String email) {
        this.email = email;
    }
}
