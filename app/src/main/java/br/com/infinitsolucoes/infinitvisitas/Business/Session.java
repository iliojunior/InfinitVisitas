package br.com.infinitsolucoes.infinitvisitas.Business;

import br.com.infinitsolucoes.infinitvisitas.Models.FiltroListaPrincipal;
import br.com.infinitsolucoes.infinitvisitas.Models.Usuario;

public final class Session {
    private static final FiltroListaPrincipal FILTRO_LISTA_PRINCIPAL = new FiltroListaPrincipal();
    private static Usuario USUARIO_LOGADO;

    public static FiltroListaPrincipal getFiltroListaPrincipal() {
        return FILTRO_LISTA_PRINCIPAL;
    }

    public static Usuario getUsuarioLogado() {
        return USUARIO_LOGADO;
    }

    public static void setUsuarioLogado(Usuario usuarioLogado) {
        USUARIO_LOGADO = usuarioLogado;
    }
}
