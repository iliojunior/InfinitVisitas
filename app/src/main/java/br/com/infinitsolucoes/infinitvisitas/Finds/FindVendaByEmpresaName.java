package br.com.infinitsolucoes.infinitvisitas.Finds;

import android.content.Context;
import android.text.TextUtils;

import java.util.List;

import br.com.infinitsolucoes.infinitvisitas.Business.Data.CRUD.EmpresaCRUD;
import br.com.infinitsolucoes.infinitvisitas.Finds.FindEmpresasIdByName;
import br.com.infinitsolucoes.infinitvisitas.Interfaces.Findable;

public final class FindVendaByEmpresaName implements Findable {
    private final String nameSearch;
    private final Context context;

    public FindVendaByEmpresaName(final String nameSearch, final Context context) {
        this.nameSearch = nameSearch;
        this.context = context;
    }

    @Override
    public String[] getColumnsFind() {
        return null;
    }

    @Override
    public String getWhereClause() {
        final List<Integer> empresas = new EmpresaCRUD(context)
                .findIds(new FindEmpresasIdByName(nameSearch));
        return "empresaId IN (" + TextUtils.join(",", empresas) + ")";
    }

    @Override
    public String[] getWhereArgs() {
        return new String[0];
    }

    @Override
    public String getGroupByClause() {
        return null;
    }

    @Override
    public String getHavingClause() {
        return null;
    }

    @Override
    public String getOrderByClause() {
        return null;
    }
}
