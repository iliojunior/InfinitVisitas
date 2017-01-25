package br.com.infinitsolucoes.infinitvisitas.Finds;

import android.content.Context;
import android.text.TextUtils;

import java.util.List;

import br.com.infinitsolucoes.infinitvisitas.Business.Data.CRUD.EmpresaCRUD;
import br.com.infinitsolucoes.infinitvisitas.Interfaces.Findable;

public class FindAgendaByEmpresaName implements Findable {
    private final String nameSearch;
    private final Context context;

    public FindAgendaByEmpresaName(String nameSearch, Context context) {
        this.nameSearch = nameSearch;
        this.context = context;
    }

    @Override
    public String[] getColumnsFind() {
        return new String[0];
    }

    @Override
    public String getWhereClause() {
        final List<Integer> ids = new EmpresaCRUD(context)
                .findIds(
                        new FindEmpresasIdByName(nameSearch)
                );
        return " empresaId IN (" + TextUtils.join(",", ids) + ")";
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
