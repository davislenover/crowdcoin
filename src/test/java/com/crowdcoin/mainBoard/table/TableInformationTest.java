package com.crowdcoin.mainBoard.table;


import javafx.scene.control.TableColumn;
import org.apache.logging.log4j.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

class TableInformationTest {

    static Logger log = LogManager.getLogger();

    @Test
    public void checkTypes() {

        TableInformation test = new TableInformation();
        test.addColumn(new TableColumn<String,String>());


    }

}