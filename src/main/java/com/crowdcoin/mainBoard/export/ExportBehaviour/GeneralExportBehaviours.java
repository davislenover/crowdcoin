package com.crowdcoin.mainBoard.export.ExportBehaviour;

public enum GeneralExportBehaviours implements ExportBehaviours {
    FULLEXPORT(FullExport.class);

    private Class<? extends ExportBehaviour> exportClass;
    GeneralExportBehaviours(Class<? extends ExportBehaviour> exportClass) {
        this.exportClass = exportClass;
    }
    @Override
    public Class getExportBehaviourClass() {
        return this.exportClass;
    }
}
