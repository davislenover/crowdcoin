package com.crowdcoin.mainBoard.export.ExportBehaviour;

import java.util.ArrayList;
import java.util.List;

public enum GeneralExportBehaviours implements ExportBehaviours {
    FULLEXPORT(FullExport.class),RANGEEXPORT(RangeExport.class),TABVIEW(TabViewExport.class);

    private Class<? extends ExportBehaviour> exportClass;
    GeneralExportBehaviours(Class<? extends ExportBehaviour> exportClass) {
        this.exportClass = exportClass;
    }
    @Override
    public Class getExportBehaviourClass() {
        return this.exportClass;
    }

    public static List<String> getNames() {

        List<String> returnList = new ArrayList<>();

        for (GeneralExportBehaviours behaviour : values()) {
            returnList.add(behaviour.toString());
        }

        return returnList;
    }
}
