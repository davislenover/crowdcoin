package com.crowdcoin.newwork;

import com.crowdcoin.networking.sqlcom.data.filter.Filter;
import com.crowdcoin.networking.sqlcom.data.filter.GeneralFilter;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.GeneralFilterOperators;
import com.crowdcoin.mainBoard.table.Column;
import com.crowdcoin.newwork.names.Table;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SelectQueryTest {

    @Test
    public void testSelectQuery() {

        String expectedResult = "SELECT testColumn AS testAlias, testColumn2 AS testAlias2 FROM testTable, testTable2 WHERE testColumn2 = '12';";

        SelectQuery testQuery = new SelectQuery();
        testQuery.addColumn(new Column("testColumn","testAlias",null,null));
        testQuery.addColumn(new Column("testColumn2","testAlias2",null,null));

        testQuery.addTable(new Table("testTable"));
        testQuery.addTable(new Table("testTable2"));

        Filter equalFilter = new GeneralFilter("testColumn2", GeneralFilterOperators.EQUAL, 12);
        testQuery.addFilter(equalFilter);

        System.out.println(testQuery.getQuery());
        assertEquals(expectedResult,testQuery.getQuery());

    }

}