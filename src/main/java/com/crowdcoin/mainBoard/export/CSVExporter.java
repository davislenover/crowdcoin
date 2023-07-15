package com.crowdcoin.mainBoard.export;

import java.io.*;
import java.util.List;

/**
 * Used for exporting data in a .csv format
 */
public class CSVExporter {

    private String path = "";
    private String fileExtension = ".csv";
    private String home = "user.home";
    private String lineSeparator = "line.separator";

    /**
     * Creates a new CSVExporter object.
     * @param filePathDirs the names of the directories which make up the entire path to the file (Ex: "Desktop","MyFolder" would translate to C:\Users\(userName)\Desktop\MyFolder in Windows).
     */
    public CSVExporter(String ... filePathDirs) {

        path+=System.getProperty(this.home);

        for (int index = 0; index < filePathDirs.length; index++) {
            path+= File.separator+filePathDirs[index];
        }
    }

    /**
     * Write data to a new csv file. If a csv file within the same directory with the same name exists, it will be overridden
     * @param columnNames The names of each column to display at the top of the csv file
     * @param entries each entry per row of the csv file (each entry list must match the length of the column list for correct data positioning within the csv file)
     * @param fileName the name of the file without the file extension
     * @throws IOException if the write operation fails
     */
    public void writeToFile(List<String> columnNames,List<List<String>> entries,String fileName) throws IOException {

        File newCSVFile = new File(this.path+File.separator+fileName+this.fileExtension);
        newCSVFile.getParentFile().mkdirs();
        newCSVFile.createNewFile();

        FileWriter writer = new FileWriter(newCSVFile);

        // Write columns
        for(int index = 0; index < columnNames.size(); index++){
            if (index != columnNames.size() - 1) {
                writer.write(columnNames.get(index)+",");
            } else {
                writer.write(columnNames.get(index)+System.getProperty(this.lineSeparator));
            }
        }

        // Write entries
        for (List<String> entry : entries) {
            for (int index = 0; index < entry.size(); index++) {
                if (index != entry.size() - 1) {
                    writer.write(entry.get(index)+",");
                } else {
                    writer.write(entry.get(index)+System.getProperty(this.lineSeparator));
                }
            }
        }

        writer.close();

    }

}
