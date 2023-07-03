package com.crowdcoin.mainBoard.table.permissions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PermissionTest {

    @Test
    public void testName() {

        Permission isReadable = new IsReadable(false);
        assertEquals(false,isReadable.getPermissionValue());
        assertEquals("IsReadable",isReadable.getPermissionName());

    }



}