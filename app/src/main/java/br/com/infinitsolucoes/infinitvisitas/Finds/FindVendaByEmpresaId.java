package br.com.infinitsolucoes.infinitvisitas.Finds;

import br.com.infinitsolucoes.infinitvisitas.Interfaces.Findable;

public class FindVendaByEmpresaId implements Findable {
    private final int empresaId;

    public FindVendaByEmpresaId(int empresaId) {
        this.empresaId = empresaId;
    }

    @Override
    public String[] getColumnsFind() {
        return new String[]{
                "_vendaId"
        };
    }

    @Override
    public String getWhereClause() {
        return "empresaId=?";
    }

    @Override
    public String[] getWhereArgs() {
        return new String[]{
                String.valueOf(empresaId)
        };
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
