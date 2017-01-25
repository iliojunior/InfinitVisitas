package br.com.infinitsolucoes.infinitvisitas.Models;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;

import br.com.infinitsolucoes.infinitvisitas.Business.Enumerador;

public class FiltroListaPrincipal {
    private Enumerador.OrdenacaoColunaFiltro ordenacaoColuna = Enumerador.OrdenacaoColunaFiltro.DATA_HORA;
    private static final Date minDate = new Date(Long.MIN_VALUE);
    private static final Date maxDate = new Date(Long.MAX_VALUE);
    private Calendar dataInitial = Calendar.getInstance();
    private Calendar dataFinal = Calendar.getInstance();
    private Enumerador.RamosAtividade ramoAtividade = Enumerador.RamosAtividade.TODOS;

    @NotNull
    private String sistemaAtual = "";

    @NotNull
    public String getSistemaAtual() {
        if (sistemaAtual != null)
            return sistemaAtual;
        sistemaAtual = "";
        return sistemaAtual;
    }

    public void setSistemaAtual(@NotNull String sistemaAtual) {
        this.sistemaAtual = sistemaAtual;
    }

    public FiltroListaPrincipal() {
        dataInitial.setTime(minDate);
        dataFinal.setTime(maxDate);
    }

    public Calendar getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Calendar dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Calendar getDataInitial() {

        return dataInitial;
    }

    public void setDataInitial(Calendar dataInitial) {
        this.dataInitial = dataInitial;
    }

    public Enumerador.OrdenacaoColunaFiltro getOrdenacaoColuna() {
        return ordenacaoColuna;
    }

    public void setOrdenacaoColuna(Enumerador.OrdenacaoColunaFiltro ordenacaoColuna) {
        this.ordenacaoColuna = ordenacaoColuna;
    }

    public Enumerador.RamosAtividade getRamoAtividade() {
        return ramoAtividade;
    }

    public void setRamoAtividade(Enumerador.RamosAtividade ramoAtividade) {
        this.ramoAtividade = ramoAtividade;
    }
}
