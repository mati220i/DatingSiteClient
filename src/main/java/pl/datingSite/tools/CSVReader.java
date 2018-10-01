package pl.datingSite.tools;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CSVReader {

    private FileReader file;
    private String line = "";
    private String splitBy = ",";
    private List<String[]> data;

    public CSVReader() {
        data = new ArrayList<>();
    }

    public CSVReader(File file) {
        try {
            this.file = new FileReader(file);
            data = new ArrayList<>();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public CSVReader(File file, String splitBy) {
        try {
            this.file = new FileReader(file);
            this.splitBy = splitBy;
            data = new ArrayList<>();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public List<String[]> read() {
        BufferedReader reader = new BufferedReader(this.file);
        return readFile(reader);
    }

    public List<String[]> read(File file) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return readFile(reader);
    }

    private List<String[]> readFile(BufferedReader reader) {
        line = "";
        data = new LinkedList<>();

        try {
            while ((line = reader.readLine()) != null) {
                data.add(line.split(splitBy));
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

//    public void prepare() {
//        try {
//            BufferedReader reader = new BufferedReader(new FileReader(new File("src/main/resources/otherData/namesPl.csv")));
//            FileWriter fileWriter = new FileWriter("src/main/resources/otherData/namesPlK.csv");
//            PrintWriter writer = new PrintWriter(fileWriter);
//            while ((line = reader.readLine()) != null) {
//                line = line.replaceAll("'", "");
//                String[] data = line.split(splitBy);
//                data[0] = data[0].toLowerCase();
//                String first = String.valueOf(data[0].charAt(0)).toUpperCase();
//                System.out.println(first);
//                data[0] = data[0].replaceFirst(String.valueOf(data[0].charAt(0)), first);
//                writer.println(data[0] + "," + data[1]);
//            }
//            fileWriter.close();
//            reader.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}