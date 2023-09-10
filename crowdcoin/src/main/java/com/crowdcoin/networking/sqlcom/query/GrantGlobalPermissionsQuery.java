package com.crowdcoin.networking.sqlcom.query;

public class GrantGlobalPermissionsQuery implements QueryBuilder {

    private String username;
    private String[] permissions;

    public GrantGlobalPermissionsQuery(String username, String ... permissions) {
        this.username = username;
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
        query+="on " + "*.* TO " + "'" + this.username + "'" + "@" + "'%'";
        return query;
    }
}
