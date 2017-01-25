package br.com.infinitsolucoes.infinitvisitas.Controllers;

import br.com.infinitsolucoes.infinitvisitas.Models.Usuario;

public abstract class UsuarioController {
    public boolean ValidarUsuario(Usuario usuario) {
        if (usuario == null)
            return false;


        return true;
    }
}
