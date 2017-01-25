package br.com.infinitsolucoes.infinitvisitas.Interfaces;

public interface Findable {


    /**
     * Specifying which columns to select.
     *
     * @return Return 'NULL' or 'EMPTY' to get all the columns.
     */
    String[] getColumnsFind();

    /**
     * Specification of the 'Where' clause filter.
     *
     * @return Return 'NULL' for nothing WHERE
     */
    String getWhereClause();

    /**
     * Specification of the 'Where' args filter
     *
     * @return Return 'NULL' or 'EMPTY' for nothing WHERE
     */
    String[] getWhereArgs();

    /**
     * Specification of the 'Group By' clause filter
     *
     * @return
     */
    String getGroupByClause();

    /**
     * Specification of the 'Having' clause filter
     *
     * @return
     */
    String getHavingClause();

    /**
     * Specification of the 'Order By' clause filter
     *
     * @return
     */
    String getOrderByClause();
}
