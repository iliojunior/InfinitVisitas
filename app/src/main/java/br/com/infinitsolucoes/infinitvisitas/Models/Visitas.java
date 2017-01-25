package br.com.infinitsolucoes.infinitvisitas.Models;

import org.jetbrains.annotations.NotNull;

import java.text.Collator;
import java.util.Calendar;
import java.util.Comparator;

import br.com.infinitsolucoes.infinitvisitas.Interfaces.Column;
import br.com.infinitsolucoes.infinitvisitas.Interfaces.Id;

public class Visitas extends InfinitOdin {
    @Id
    @Column
    private final int _visitaId;
    @Column
    @NotNull
    private String observacao = "";
    @NotNull
    @Column
    private Calendar data;
    @Column
    private int empresaId;
    @NotNull
    private Empresas empresa;

    public Visitas(final int _visitaId,
                   @NotNull Empresas empresa) {
        this._visitaId = _visitaId;
        this.empresa = empresa;
        data = Calendar.getInstance();
    }

    public Visitas(@NotNull Empresas empresa) {
        setEmpresa(empresa);
        data = Calendar.getInstance();
        this._visitaId = 0;
    }

    public int getEmpresaId() {
        return getEmpresa().get_empresaId();
    }

    public int get_visitaId() {

        return _visitaId;
    }

    public void setObservacao(@NotNull String observacao) {
        this.observacao = observacao;
    }

    @NotNull
    public Calendar getData() {
        return data;
    }

    @NotNull
    public Empresas getEmpresa() {
        return empresa;
    }

    @NotNull
    public String getObservacao() {
        return observacao;
    }

    public void setEmpresa(@NotNull Empresas empresa) {
        if (empresa == null)
            throw new NullPointerException("Empresa não pode ser Nulo!");
        this.empresa = empresa;

    }

    public void setData(@NotNull Calendar data) {
        if (data == null)
            throw new NullPointerException("Data não pode ser Nulo!");
        this.data = data;
    }

    public static Comparator<Visitas> SortOportunidade = (o1, o2) -> {
        final Collator collator = Collator.getInstance();
        final int result = o1.getData().compareTo(o2.getData());
        if (result == 0) {
            return collator.compare(o1.getEmpresa().getNome(), o2.getEmpresa().getNome());
        }
        return result;
    };

    public static Comparator<Visitas> SortAgenda = (o1, o2) -> {
        final Collator collator = Collator.getInstance();
        if (o1.getData().after(o2.getData())) {
            return 0;
        } else {
            return collator.compare(o1.getEmpresa().getNome(), o2.getEmpresa().getNome());
        }
    };
}
