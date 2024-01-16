package com.crowdcoin.newwork.names;

public class Column extends AliasedName {

    public Column(String name, String alias) {
        super(name, alias);
    }

    public Column(String name) {
        super(name, "");
    }

    public String getColumnAsQuery() {
        return super.getAlias().isEmpty() ? super.getName() : super.getName() + " AS " + super.getAlias();
    }

}
