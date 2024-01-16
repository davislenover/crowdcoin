package com.crowdcoin.newwork.names;

public class Column extends AliasedName {

    public Column(String name, String alias) {
        super(name, alias);
    }

    public Column(String name) {
        super(name, "");
    }

    /**
     * Gets the column name formatted for a SELECT query (SELECT). If the Column has an alias, the name and the alias will be concatenated via: "name AS alias"
     * @return a {@link String} object
     */
    public String getColumnAsQuery() {
        return super.getAlias().isEmpty() ? super.getName() : super.getName() + " AS " + super.getAlias();
    }

}
