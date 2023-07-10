package com.crowdcoin.networking.sqlcom.Generation;

import com.crowdcoin.networking.sqlcom.data.SQLTable;

import java.util.List;
import java.util.Random;

/**
 * Generate a id (integer) which is unique to all other id's within a given SQL table column
 */
public class IDGenerator implements Generator<Integer, SQLTable> {

    private int defaultMax = 1000000000;
    private String columnWithIds;
    private int indexOfIdColumn;

    /**
     * Creates an IDGenerator
     * @param columnWithIds the column within the SQL table (NOT object) where all the ids are kept.
     */
    public IDGenerator(String columnWithIds) {
        this.columnWithIds = columnWithIds;
    }

    @Override
    public Integer generateValue(SQLTable parameter) {

        this.indexOfIdColumn = parameter.getColumnNames().indexOf(this.columnWithIds);
        boolean isUnique = false;
        int generatedNumber = 0;

        while (!isUnique) {

            // Create Random object with current time as seed
            Random rand = new Random(System.currentTimeMillis());
            // Generate a number between 0 and one less than the default value
            generatedNumber = rand.nextInt(this.defaultMax);
            // Check if the number is unique, if not, regenerate until unique
            isUnique = isNumberUnique(generatedNumber,parameter);

        }

        return generatedNumber;
    }

    // Method checks if the generated id already exists
    private boolean isNumberUnique(int number, SQLTable table) {

        try {
            List<List<Object>> result = table.getSpecificRows(this.indexOfIdColumn,String.valueOf(number),1,this.indexOfIdColumn,table.getNumberOfColumns()-1);

            if (result.isEmpty()) {
                return true;
            }

            return false;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }

    }

}
