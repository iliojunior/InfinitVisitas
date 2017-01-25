package br.com.infinitsolucoes.infinitvisitas.Finds;

public class FindLeadQualificadoByName extends FindEmpresasIdByName {
    public FindLeadQualificadoByName(String nomeSearch) {
        super(nomeSearch);
    }

    @Override
    public String getWhereClause() {
        return super.getWhereClause().concat(" AND nivelInteresse>0");
    }
}
