package br.com.infinitsolucoes.infinitvisitas.Finds;

import android.content.Context;
import android.text.TextUtils;

import java.util.List;

import br.com.infinitsolucoes.infinitvisitas.Business.Data.CRUD.EmpresaCRUD;
import br.com.infinitsolucoes.infinitvisitas.Interfaces.Findable;

public class FindOportunidadeByEmpresaName implements Findable {
    private final Context context;
    private final String nameSearch;

    public FindOportunidadeByEmpresaName(Context context, String nameSearch) {
        this.context = context;
        this.nameSearch = nameSearch;
    }

    @Override
    public String[] getColumnsFind() {
        return new String[0];
    }

    @Override
    public String getWhereClause() {
        final List<Integer> empresaIds = new EmpresaCRUD(context)
                .findIds(new FindEmpresasIdByName(nameSearch));
        return "empresaId in (" + TextUtils.join(",", empresaIds) + ")";
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
