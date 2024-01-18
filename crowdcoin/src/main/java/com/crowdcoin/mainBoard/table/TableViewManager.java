package com.crowdcoin.mainBoard.table;

import com.crowdcoin.exceptions.modelClass.*;
import com.crowdcoin.exceptions.network.FailedQueryException;
import com.crowdcoin.exceptions.table.InvalidRangeException;
import com.crowdcoin.mainBoard.table.modelClass.ModelClass;
import com.crowdcoin.mainBoard.table.modelClass.ModelClassFactory;
import com.crowdcoin.newwork.QueryResult;
import com.crowdcoin.newwork.QueryResultReader;
import com.crowdcoin.newwork.Tuple;
import com.ratchet.observe.ModifyEvent;
import com.ratchet.observe.ModifyEventType;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import com.crowdcoin.networking.sqlcom.data.SQLTableReader;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles access of rows in an SQL Table in an ordered/iterative fashion for application to TableView objects
 */
public class TableViewManager extends QueryResultReader {

    private ColumnContainer columnContainer;
    private ModelClassFactory factory;

    /**
     * Handles access of rows in an SQL Table in an ordered/iterative fashion for application to TableView objects.
     * @param queryResult        a queryResult to read SQL data from
     * @param columnContainer the columns for the TableView object
     * @param factory         the factory to produce clones of the ModelClass
     * @throws FailedQueryException
     * @throws SQLException
     * @throws InvalidRangeException
     */
    public TableViewManager(QueryResult queryResult, ColumnContainer columnContainer, ModelClassFactory factory) {
        super(queryResult,10);
        this.columnContainer = columnContainer;
        this.factory = factory;
    }

    public List<ModelClass> getCurrentModelClassSet() throws InvalidAbstractColumnReturnTypeException, InvalidAnnotationUsageException, MultipleVariableMethodsException, NotZeroArgumentException, InvalidVariableMethodParameterTypeException, InvalidVariableMethodParameterCount {
        List<ModelClass> klasses = new ArrayList<>();
        for (Tuple row : this.getCurrentSet()) {
            klasses.add(factory.build(row));
        }
        return klasses;
    }

    public List<ModelClass> getModelClassRows(int currentRowIndex, int lastRowIndex) throws InvalidRangeException, InvalidAbstractColumnReturnTypeException, InvalidAnnotationUsageException, MultipleVariableMethodsException, NotZeroArgumentException, InvalidVariableMethodParameterTypeException, InvalidVariableMethodParameterCount {
        List<ModelClass> klasses = new ArrayList<>();
        for (Tuple row : super.getRows(currentRowIndex,lastRowIndex)) {
            klasses.add(factory.build(row));
        }
        return klasses;
    }

    /**
     * Shifts the next set of rows to get forwards, then applys that shifted list to the given destination Table. Only the tables items are cleared and not the columns.
     * @param destinationTable the TableView object to apply the rows to
     * @throws NotZeroArgumentException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public void applyNextRows(TableView destinationTable) throws NotZeroArgumentException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, MultipleVariableMethodsException, InvalidVariableMethodParameterTypeException, InvalidVariableMethodParameterCount, InvalidAbstractColumnReturnTypeException, InvalidAnnotationUsageException {

        // Clear current data and columns
        destinationTable.getItems().clear();

        destinationTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // Get rows of data
        List<Tuple> rows = super.next();

        for (Tuple row : rows) {
            // Basically, each table column doesn't know how to get data to display it other than that it is using a ModelClass type as input and Object type as output
            // How to get data is specified by setting the cell value factory of each column, where in this case, it is set to get the ModelClass object and invoke the corresponding method
            // Note that it is NOT a specific ModelClass, it simply specifies that once it receives a specific instance, it will do what it was told to do with any other ModelClass
            // The corresponding method is at the same index as the current size of the column data list (at the time) thus if a ModelClass has 4 invokable methods, the first column will get data from method 1
            // second from method 2 and so on
            // Thus when we add a ModelClass here, to display the data we are simply invoking the corresponding method within the specific instance of the ModelClass at the given index
            // This cloned ModelClass houses the row data from the table within the database and utilizes the specified methods (via @TableReadable annotation) to retrieve the data
            destinationTable.getItems().add(this.factory.build(row));
        }

    }

    /**
     * Shifts the next set of rows to get backwards, then applys that shifted list to the given destination Table. Only the tables items are cleared and not the columns.
     * @param destinationTable the TableView object to apply the rows to
     * @throws NotZeroArgumentException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public void applyPreviousRows(TableView destinationTable) throws NotZeroArgumentException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, MultipleVariableMethodsException, InvalidVariableMethodParameterTypeException, InvalidVariableMethodParameterCount, InvalidAbstractColumnReturnTypeException, InvalidAnnotationUsageException {

        // Clear current data and columns
        destinationTable.getItems().clear();

        destinationTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // Get rows of data
        List<Tuple> rows = super.previous();

        for (Tuple row : rows) {
            destinationTable.getItems().add(this.factory.build(row));
        }

        destinationTable.getSelectionModel().select(0);

    }

    /**
     * Applys the current rows stored to the given destination Table (no list shifting). The destination table has BOTH it's columns and items cleared before applying the current row (useful for remembering rows when switching Tabs).
     * Resizes row height to fit all rows within the height of the destination TableView object
     * @param destinationTable the TableView object to apply the rows to
     * @throws NotZeroArgumentException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public void applyCurrentRows(TableView destinationTable) throws NotZeroArgumentException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, MultipleVariableMethodsException, InvalidVariableMethodParameterTypeException, InvalidVariableMethodParameterCount, InvalidAbstractColumnReturnTypeException, InvalidAnnotationUsageException {

        // Clear current data and columns
        destinationTable.getItems().clear();
        destinationTable.getColumns().clear();

        destinationTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // Load columns within Tab to table
        for (TableColumn column : this.columnContainer) {
            destinationTable.getColumns().add(column);
        }

        // Get rows of data
        List<Tuple> rows = super.getCurrentSet();

        for (Tuple row : rows) {
            destinationTable.getItems().add(this.factory.build(row));
        }

        // Set Table cell height to fit all items
        destinationTable.setRowFactory(view -> {
            // Set the row factory to set each rows height such that all rows just fit in the total height of the TableView object
            TableRow<ModelClass> row = new TableRow<>();
            row.heightProperty().addListener(changeListener -> {
                row.setPrefHeight(destinationTable.getHeight()/destinationTable.getItems().size());
            });
            return row;
        });

        destinationTable.getSelectionModel().select(0);

    }

    public void applyPrevNextButtons(TableView destinationTable, Button previous, Button next) {

        // Set button enable/disable (in case their state was changed by a different TableViewManager)
        next.setDisable(super.hasNext());
        previous.setDisable(super.isAtStart());

        // Set previous and next button functionalities to invoke methods in this TableViewManager instance on action
        previous.setOnAction(actionEvent -> {
            try {
                this.applyPreviousRows(destinationTable);
                // After changing the rows displayed, check if any first row or last row booleans were asserted and update button states accordingly
                next.setDisable(super.hasNext());
                previous.setDisable(super.isAtStart());

                this.notifyObservers(new ModifyEvent(ModifyEventType.APPLIED_NEW_VIEW));

            } catch (NotZeroArgumentException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (MultipleVariableMethodsException e) {
                throw new RuntimeException(e);
            } catch (InvalidVariableMethodParameterTypeException e) {
                throw new RuntimeException(e);
            } catch (InvalidVariableMethodParameterCount e) {
                throw new RuntimeException(e);
            } catch (InvalidAbstractColumnReturnTypeException e) {
                throw new RuntimeException(e);
            } catch (InvalidAnnotationUsageException e) {
                throw new RuntimeException(e);
            }
        });

        next.setOnAction(actionEvent -> {
            try {
                this.applyNextRows(destinationTable);

                next.setDisable(super.hasNext());
                previous.setDisable(super.isAtStart());

                this.notifyObservers(new ModifyEvent(ModifyEventType.APPLIED_NEW_VIEW));

            } catch (NotZeroArgumentException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (MultipleVariableMethodsException e) {
                throw new RuntimeException(e);
            } catch (InvalidVariableMethodParameterTypeException e) {
                throw new RuntimeException(e);
            } catch (InvalidVariableMethodParameterCount e) {
                throw new RuntimeException(e);
            } catch (InvalidAbstractColumnReturnTypeException e) {
                throw new RuntimeException(e);
            } catch (InvalidAnnotationUsageException e) {
                throw new RuntimeException(e);
            }
        });


    }
}
