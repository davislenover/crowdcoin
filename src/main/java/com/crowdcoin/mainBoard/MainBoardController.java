package com.crowdcoin.mainBoard;

import com.crowdcoin.mainBoard.table.TableInformation;
import com.crowdcoin.mainBoard.table.TableReadable;
import com.crowdcoin.networking.sqlcom.SQLData;
import com.crowdcoin.networking.sqlcom.SQLDefaultQueries;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class MainBoardController {

    // Toolbar menu button declarations

    // File
    @FXML private MenuButton fileMenuButton;
    @FXML private MenuItem fileMenuExit;

    // Edit
    @FXML private MenuButton editMenuButton;
    @FXML private MenuItem editMenuNewEntry;

    @FXML private TableView mainTable;

    // Method to initialize coin list on startup
    public void initializeList() throws Exception {

        TableInformation tableTest = new TableInformation();

        double totalWidth = 0;

        mainTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // Get all column names in table
        ResultSetMetaData resultSet = SQLData.getSqlConnection().sendQueryGetMetaData(SQLDefaultQueries.getColumnNames);

        // Loop through each column
        for (int i = 1; i <= resultSet.getColumnCount(); i++) {
            // Create a new column with the specified name
            TableColumn<TableReadable<String>,String> column = new TableColumn<>(resultSet.getColumnName(i));
            // Set the minimum width of the given column by calculating the width of the given text
            Text columnText = new Text(column.getText());
            columnText.setFont(column.getCellFactory().call(column).getFont());
            // -2 is for border purposes
            double widthValue = columnText.prefWidth(-1)+columnText.getText().length()-2;
            column.setMinWidth(widthValue);
            column.setPrefWidth(widthValue);
            totalWidth+=widthValue;
            column.setReorderable(false);
            tableTest.addColumn(column);
        }

        // Width may not max out the table, thus we distribute the rest of the width to all columns
        // Note that if width is greater than the table, we NEED to handle this!
        double additionalWidth = (mainTable.getWidth() - totalWidth)/ (resultSet.getColumnCount());

        for (TableColumn curColumn : tableTest) {
            curColumn.setPrefWidth(curColumn.getMinWidth()+(additionalWidth));
            // Add column to table
            mainTable.getColumns().add(curColumn);
        }

    }

    // Events

    // Exit Button Action
    @FXML
    protected void onMenuItemExitAction() {

        Platform.exit();

    }

    // New Entry into database
    @FXML
    protected void onNewEntry() {




    }


}
