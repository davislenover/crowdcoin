package com.crowdcoin.mainBoard.export;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CSVExporterTest {

    @Test
    public void CSVExportTest() throws IOException {

        CSVExporter exporter = new CSVExporter("Desktop","MyTestFolder");

        List<String> columnNames = new ArrayList<>() {{
            add("Name");
            add("Age");
            add("Phone Number");
            add("Address");
            add("Extra");
        }};

        List<String> entry1 = new ArrayList<>() {{
            add("Davis");
            add("55");
            add("10001000");
            add("MyHomeAddress");
            //add("Hello world!");
        }};

        List<String> entry2 = new ArrayList<>() {{
            add("Seth");
            add("552");
            add("100010r23");
            //add("MyHomeAddress33");
            //add("Hello world from Canada!");
        }};

        List<String> entry3 = new ArrayList<>() {{
            add("William");
            add("600");
            add("dwqd");
            add("gergrerg");
            add("Yes!");
        }};

        List<List<String>> entries = new ArrayList<>() {{
            add(entry1);
            add(entry2);
            add(entry3);
        }};

        exporter.writeToFile(columnNames,entries,"MyCSVFile");

    }

}