package com.crowdcoin.mainBoard.table;

import com.crowdcoin.exceptions.modelClass.NotZeroArgumentException;
import javafx.scene.control.TableView;

public class Tab {

    private TableInformation tableInfo;
    private ModelClass modelClass;
    private ModelClassFactory factory;

    public Tab(Object modelClass) throws NotZeroArgumentException {
        this.tableInfo = new TableInformation();
        this.factory = new ModelClassFactory();
        this.modelClass = this.factory.build(modelClass);
    }



    public void loadTab(TableView destinationTable) {

    }

}
