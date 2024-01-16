package com.crowdcoin.newwork.names;

public class Table extends AliasedName {

    public Table(String name, String alias) {
        super(name, alias);
    }

    public Table(String name) {
        super(name, "");
    }

    /**
     * Gets the Table name formatted for a SELECT query (FROM)
     * @return a {@link String} object
     */
    public String getTableAsQuery() {
        return super.getAlias().isEmpty() ? super.getName() : super.getName() + " " + super.getAlias();
    }

}
