package br.com.infinitsolucoes.infinitvisitas.Models;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Comparator;

import br.com.infinitsolucoes.infinitvisitas.Interfaces.Column;
import br.com.infinitsolucoes.infinitvisitas.Interfaces.Id;
import br.com.infinitsolucoes.infinitvisitas.Interfaces.Models;

public class FollowUp implements Models {
    @Id
    @Column
    private final int _followUpId;
    @NotNull
    @Column
    private Calendar data;
    @Column
    private int retornoTempo;
    @Column
    private String retornoMedida;
    @Column
    private int empresaId;
    @NotNull
    private Empresas empresa;
    @Column
    private boolean naoRetornarMais;
    @Column
    @NotNull
    private String observacao;
    @Column
    private int nivelInteresse;

    public static final Comparator<FollowUp> SORT_DATE_DESC = (o1, o2) -> {
        if (o1.getData().after(o2.getData()))
            return -1;
        else if (o1.getNivelInteresse() > o2.getNivelInteresse()) {
            return -1;
        }
        return 1;
    };


    public FollowUp(Empresas empresa) {
        this(0, empresa);
    }

    public FollowUp(final int followUpId, Empresas empresa) {
        this._followUpId = followUpId;
        this.empresa = empresa;
    }


    @Override
    public Field getIdColumn() throws NoSuchFieldException {
        return this.getClass().getDeclaredField("_followUpId");
    }

    public String getRetornoMedida() {
        return retornoMedida;
    }

    public void setRetornoMedida(String retornoMedida) {
        this.retornoMedida = retornoMedida;
    }

    public int get_followUpId() {
        return _followUpId;
    }

    @NotNull
    public Calendar getData() {
        return data;
    }

    public void setData(@NotNull Calendar data) {
        this.data = data;
    }

    @NotNull
    public Empresas getEmpresa() {
        return empresa;
    }

    public int getEmpresaId() {
        return getEmpresa().get_empresaId();
    }

    public boolean isNaoRetornarMais() {
        return naoRetornarMais;
    }

    public void setNaoRetornarMais(boolean naoRetornarMais) {
        this.naoRetornarMais = naoRetornarMais;
    }

    public int getRetornoTempo() {
        return retornoTempo;
    }

    public void setRetornoTempo(int retornoTempo) {
        this.retornoTempo = retornoTempo;
    }

    @NotNull
    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(@NotNull String observacao) {
        this.observacao = observacao;
    }

    public int getNivelInteresse() {
        return nivelInteresse;
    }

    public void setNivelInteresse(int nivelInteresse) {
        this.nivelInteresse = nivelInteresse;
    }
}
