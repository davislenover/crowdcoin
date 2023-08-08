package com.crowdcoin.networking.sqlcom.query;

public class GrantPermissionsQuery implements QueryBuilder {

    private String username;
    private String schemaName;
    private String[] permissions;

    public GrantPermissionsQuery(String username, String schemaName, String ... permissions) {
        this.username = username;
        this.schemaName = schemaName;
        this.permissions = permissions;
    }


    @Override
    public String getQuery() {
        String query = "GRANT";
        for (int index = 0; index < this.permissions.length; index++) {
            if (index != this.permissions.length - 1) {
                query+=" " + this.permissions[index] + ",";
            } else {
                query+=" " + this.permissions[index] + " ";
            }
        }
        query+="on " + this.schemaName +".* TO " + "'" + this.username + "'" + "@" + "'%'";
        return query;
    }
}
