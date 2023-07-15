package com.crowdcoin.mainBoard.export.ExportBehaviour;

import com.crowdcoin.mainBoard.Interactive.InteractivePane;
import com.crowdcoin.mainBoard.Interactive.input.InputField;
import com.crowdcoin.mainBoard.Interactive.input.InteractiveTextField;
import com.crowdcoin.mainBoard.Interactive.input.validation.LengthValidator;
import com.crowdcoin.mainBoard.table.ModelClass;
import com.crowdcoin.mainBoard.table.TableViewManager;
import com.crowdcoin.mainBoard.window.PopWindow;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import javafx.scene.control.TableView;

import java.util.List;

public class FullExport implements ExportBehaviour {

    private InteractivePane pane;
    private PopWindow window;
    private SQLTable table;

    public FullExport(InteractivePane pane, PopWindow window, SQLTable table) {
        this.pane = pane;
        this.window = window;
        this.table = table;
    }

    @Override
    public List<String> getColumns() {
        return table.getColumnNames();
    }

    @Override
    public List<List<String>> getEntries() {



        return null;
    }

    @Override
    public void applyInputFieldsOnWindow() {
        InputField field = new InteractiveTextField("Filename","The name to be given to the exported file",(event, field1, pane1) -> {return;});
        field.addValidator(new LengthValidator(1));
    }
}
