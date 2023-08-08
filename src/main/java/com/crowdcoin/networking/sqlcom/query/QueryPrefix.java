package com.crowdcoin.networking.sqlcom.query;

public enum QueryPrefix {

    getall("SELECT * FROM "),informationSchemaColumnName("COLUMN_NAME"),informationSchemaDataType("DATA_TYPE"),informationSchemaOrdinalPosition("ORDINAL_POSITION"),getColumnData("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = ");

    private String prefix;

    QueryPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return this.prefix;
    }
}
