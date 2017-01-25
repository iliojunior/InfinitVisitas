package br.com.infinitsolucoes.infinitvisitas.Finds;

import br.com.infinitsolucoes.infinitvisitas.Interfaces.Findable;

public class FindEmpresasIdByName implements Findable {
    private final String nomeSearch;

    public FindEmpresasIdByName(final String nomeSearch) {
        this.nomeSearch = nomeSearch;
    }

    protected final String getNomeSearch() {
        return nomeSearch;
    }

    @Override
    public String[] getColumnsFind() {
        return new String[]{
                "_empresaId"
        };
    }

    @Override
    public String getWhereClause() {
        return "(nome LIKE ? OR nome LIKE ?)";
    }

    @Override
    public String[] getWhereArgs() {
        return new String[]{
                nomeSearch + "%",
                " " + nomeSearch + "%"
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
