package br.com.infinitsolucoes.infinitvisitas.Finds;

public class FindLeadIdByName extends FindEmpresasIdByName {
    public FindLeadIdByName(String nomeSearch) {
        super(nomeSearch);
    }

    @Override
    public String getWhereClause() {
        String where = super.getWhereClause();
        where = where.concat(" AND nivelInteresse=0");
        return where;
    }
}
