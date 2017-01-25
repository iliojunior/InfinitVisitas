package br.com.infinitsolucoes.infinitvisitas.Interfaces;

public interface IInfinitActivity {
    /**
     * Inicialização dos componentes, fields das activity's
     */
    void initializeComponents();

    /**
     * Inicialização dos eventos dos componentes.
     */
    void initializeListeners();


    /**
     * Inicialização de valores dos componentes.
     */
    void initializeValues();
}
