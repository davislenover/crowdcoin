package com.crowdcoin.newwork.names;

public class AliasedName {

    private String name;
    private String alias;

    public AliasedName(String name, String alias) {
        if (name == null || alias == null) {
            throw new IllegalArgumentException("name and alias must not be null");
        }
        this.name = name;
        this.alias = alias;
    }

    public String getName() {
        return this.name;
    }

    public String getAlias() {
        return this.alias;
    }

    public boolean equals(Object o) {
        try {
            AliasedName testObj = (AliasedName) o;
            return this.getAlias() == null ? testObj.getAlias() == null && this.getName().equals(testObj.getName()) : this.getAlias().equals(testObj.getAlias()) && this.getName().equals(testObj.getName());
        } catch (Exception e) {
            return false;
        }
    }

}
