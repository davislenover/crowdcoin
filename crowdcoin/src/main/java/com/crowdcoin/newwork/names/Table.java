package com.crowdcoin.newwork.names;

public class Table extends AliasedName {

    public Table(String name, String alias) {
        super(name, alias);
    }

    public Table(String name) {
        super(name, "");
    }

    public String getTableAsQuery() {
        return super.getAlias().isEmpty() ? super.getName() : super.getName() + " " + super.getAlias();
    }

}
