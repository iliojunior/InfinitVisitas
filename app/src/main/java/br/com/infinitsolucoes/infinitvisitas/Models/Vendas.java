package br.com.infinitsolucoes.infinitvisitas.Models;

import java.text.Collator;
import java.util.Calendar;
import java.util.Comparator;

import br.com.infinitsolucoes.infinitvisitas.Interfaces.Column;
import br.com.infinitsolucoes.infinitvisitas.Interfaces.Id;

public final class Vendas {
    @Id
    @Column
    private final int _vendaId;
    @Column
    private int empresaId;

    private final Empresas mEmpresa;
    @Column
    private Calendar dataVenda;


    //region Constructor
    public Vendas(final Visitas visita) {
        this(Calendar.getInstance(), visita);
    }

    public Vendas(final Calendar dataVenda, final Visitas visita) {
        this(visita.getEmpresa());
        this.setDataVenda(dataVenda);
    }

    public Vendas(final int _vendaId, final Empresas empresa) {
        this._vendaId = _vendaId;
        this.mEmpresa = empresa;
    }

    public Vendas(final Empresas empresa) {
        this(0, empresa);
    }
    //endregion


    //region Getter's Setter's
    public Empresas getEmpresa() {
        return mEmpresa;
    }

    public Calendar getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(Calendar dataVenda) {
        this.dataVenda = dataVenda;
    }

    public int get_vendaId() {
        return _vendaId;
    }

    public int getEmpresaId() {
        return getEmpresa().get_empresaId();
    }
    //endregion


    //region Sort's
    public static Comparator<Vendas> sortComparator = (o1, o2) -> {
        final Collator collator = Collator.getInstance();
        if (o1.getDataVenda().after(o2.getDataVenda()))
            return -1;
        else
            return collator.compare(o1.getEmpresa().getNome(), o2.getEmpresa().getNome());
    };
    //endregion
}
