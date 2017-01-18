//import sun.awt.image.ImageWatched;

import javax.print.DocFlavor;
import java.io.*;
import java.lang.reflect.Array;
import java.nio.Buffer;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;
import java.util.Scanner;
/**
 * Created by Nazli on 2017-01-11.
 */

/**
 * Algorithm:
 * Add all datasets into one dataset then sort the new list
 * Create hashtable of new list in descending order
 * check if every list has the key add the value
 * otherwise add null
 * */

/***
 * README:
 * To run the code download 3 input .csv files into the "/Users/username/Downloads"
 * This will create a file under under /Desktop) as mergeData.csv, orderedList.csv also lists retrived years of input data
 * in order of the most recent to oldest
 * It takes time for running (downside) - run time is long for the scope of program
 * Meantime reading datasets, it outputs in standard output
 *
 * Methods:===========
 * mergeData: first merges all datasets into one linkedlist
 *            then sorts the final linkedlist and returns the linkedlist
 * merge: checks each element from mergeData into each original dataset
 *        if it exsits adds the value otherwise adds 0
 * */

public class Quandl {

//    Every dataset is considered as an object of class "node"

    public class Node {
        String date;
        String value;
        public Node(String date, String value) {
            this.date = date;
            this.value = value;
        }

    }

    public Quandl() {}

    // Merge data sets based on decreasing order of dates
    private LinkedList<String> mergeData (LinkedList<String> d1,LinkedList<String> d2, LinkedList<String> d3) throws IOException {

        LinkedList<String> allDates = new LinkedList<String>();

        ArrayList<String> dates = new ArrayList<String>();

        /* take date as string and compare strings
            sort arrayLists of dates in descending order
            ideally sort strings by encoding strings */

        for (int i = 0; i < d1.size(); i++) {
            if (!dates.contains(d1.get(i)))
//                allDates.add(d1.get(i));
                dates.add(d1.get(i));
//                System.out.println(d1.get(i));
        }
        for (int i = 0; i < d2.size(); i++) {
            if (!dates.contains(d2.get(i)))
//                   allDates.add(d2.get(i));
                dates.add(d2.get(i));
//            System.out.println(d2.get(i));

        }
        for (int i = 0; i < d3.size(); i++) {
            if (!dates.contains(d3.get(i)))
//                 allDates.add(d3.get(i));
                dates.add(d3.get(i));

        }

        System.out.println("Dates arrayList ===> " + dates);
        LinkedList<String> sorted = new LinkedList<>();
        sorted = sortDates(dates);
        System.out.println("Sorted List ===> " + sorted);

        try {
            writeFile(sorted);
        }catch (IOException e ){System.out.print(e);}
        return sorted;
    }

    private LinkedList<String> sortDates ( ArrayList<String> date) {

        LinkedList<String> sorted = new LinkedList<String>();

        if (date.isEmpty()) return sorted;
        String[] year = new String[date.size()];
//        String split = "-";

        for (int i = 0; i < date.size(); i++){
            year[i] = date.get(i);
        }
        Arrays.sort(year, Collections.reverseOrder());

//        for (int start =0; start+1< year.length; start++){
////            for (int i = start; i+1 < year.length; i++){
//                if (year[start].compareTo(year[start + 1]) <  0 ) {
//                    String temp = year[start];
//                    year[start] = year[start + 1];
//                    year[start+1] = temp;
//                }
////            }
//        }
        for (int index = 0; index < year.length; index++){
            sorted.add(index, year[index]);
        }
        return sorted;
    }

    private LinkedHashMap<String, LinkedList<String>> merge(LinkedList<String> d1, LinkedList<String> d2, LinkedList<String> d3,
                                                       LinkedList<Node> data1, LinkedList<Node> data2, LinkedList<Node> data3,
                                                       LinkedList<String> sorted) {
        /* Merge 3 lists into 1 then sort using sort method
         * Add to final table as records */

        LinkedHashMap<String, LinkedList<String>> records = new LinkedHashMap<>();
        String[] keyHash = new String[sorted.size()];
        String[] keys = new String[sorted.size()];

        int ind1 = 0, ind2 = 0, ind3 = 0;
        for (int index = 0; index < sorted.size(); index++){


            keyHash[index] = sorted.get(index);
            LinkedList<String> values = new LinkedList<String>();

            if (ind1 < data1.size()) {
                Node node1 = data1.get(ind1);
                String nodeDate1 = (String) node1.date;
                String nodeVal1 = (String) node1.value;
                if (nodeDate1.split("-")[0].equals(keyHash[index])) {
                    keys[index] = nodeDate1;
                    values.add(nodeVal1);
                    ind1++;
                }
                else {
                    values.add("0");
                }
            }
            else {
                values.add("0");
            }

            if (ind2 < data1.size()) {
                Node node2  = data2.get(ind2);
//                System.out.print(node2.date + " " + node2.value);
                String nodeDate2 = (String) node2.date;
                String nodeVal2 = (String) node2.value;
                if (nodeDate2.split("-")[0].equals(keyHash[index])) {
                    keys[index] = nodeDate2;
                    values.add(nodeVal2);
                    ind2++;
                }
                else {
                    values.add("0");
                }
            }
            else {
                values.add("0");
            }

            if (ind3 < data3.size()) {
                Node node3  = data3.get(ind3);
                String nodeDate3 = (String) node3.date;
                String nodeVal3 = (String) node3.value;
                if (nodeDate3.split("-")[0].equals(keyHash[index])) {
                    keys[index] = nodeDate3;
                    values.add(nodeVal3);
                    ind3++;
                }
                else {
                    values.add("0");
                }
            }
            else {
                values.add("0");
            }

            System.out.println("KEY===== " + keys[index] + "?" + sorted.get(index));
            records.put(keys[index], values);
            System.out.println(keys[index]);

            for (String s : values){
                System.out.println(s + " ");
            }
        }
        return records;
    }


    private LinkedList<String> readFile(String fileName, LinkedList<Node> dataset) throws IOException {

        File file = new File(fileName);
        String splitter = ",";
        String line = null;

        // list is list of dates from every dataset
//        Map<String, String> list = new HashMap<>();
        LinkedList<String> list = new LinkedList<>();
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            Scanner scan = new Scanner(bufferedReader);
//            System.out.print(scan.nextLine());
            scan.nextLine();
            while (scan.hasNextLine()) {
                line = scan.nextLine();
//                line = bufferedReader.readLine();
                System.out.println(line);
                String[] r = line.split(splitter);
                list.add(r[0].split("-")[0]);
                Node node = new Node(r[0], r[1]);
                dataset.add(node);
            }
            bufferedReader.close();
        }catch (NullPointerException e ) {System.out.print(e);}

//        scan.close();
        return list;
    }

    private void writeFile(LinkedHashMap<String, LinkedList<String>> entire) throws IOException {

        StringBuilder sb = new StringBuilder();
        String spliter = ",";
        String spliterKey = "--";

        String filename = "mergeData.csv";
        Charset charset = Charset.forName("US-ASCII");
        String homeAddress = System.getProperty("user.home");
        String outputFolder = homeAddress + File.separator + "Desktop" + File.separator + filename;
        Path path = FileSystems.getDefault().getPath(outputFolder);

        File file = new File(outputFolder);
        BufferedWriter bufferedWriter1 = null;
        try{
            FileWriter fileWriter = new FileWriter(file);
            bufferedWriter1 = new BufferedWriter(fileWriter);

            Set<String> keys = entire.keySet();
            Iterator<String> itr = keys.iterator();

            for (String s : keys){
                sb.append(s);
                sb.append(spliter);
                sb.append(entire.get(s));
                sb.append('\n');
            }
//            FileOutputStream fos = new FileOutputStream(filename);
//            ObjectOutputStream outputStream = new ObjectOutputStream(fos);
//            outputStream.writeObject(entire);

            System.out.println(sb.toString());
            bufferedWriter1.write(sb.toString());
        }catch (IOError e ){
            System.out.println(e);
        }
        bufferedWriter1.flush();
        bufferedWriter1.close();

    }


    private void writeFile(LinkedList<String> list) throws IOException {

        StringBuilder sb = new StringBuilder();
        String spliter = ",";

        String filename = "orderedList.csv";
        Charset charset = Charset.forName("US-ASCII");
        String homeAddress = System.getProperty("user.home");
        String outputFolder = homeAddress + File.separator + "Desktop" + File.separator + filename;
        Path path = FileSystems.getDefault().getPath(outputFolder);

        File file = new File(outputFolder);
        BufferedWriter bufferedWriter1 = null;

        try{
            FileWriter fileWriter = new FileWriter(file);
            bufferedWriter1 = new BufferedWriter(fileWriter);
            for (String key : list) {
                sb.append(key);
                sb.append('\n');
            }
            System.out.println("Write on file orderedList");
            System.out.println(sb.toString());
            bufferedWriter1.write(sb.toString());
        }catch (IOError e ){
            System.out.println(e);
        }
        bufferedWriter1.flush();
        bufferedWriter1.close();
    }

    public static void main (String[] args) throws IOException {

        String homeAddress = System.getProperty("user.dir");
        String outputFolder = homeAddress + File.pathSeparator + "Desktop" + File.pathSeparator + "inter_mergData";
        LinkedHashMap<String, LinkedList<String>> hash = new LinkedHashMap<>();
        String address = System.getProperty("user.home") + "/Downloads/";


        LinkedList<Node> data1 = new LinkedList<>();
        LinkedList<Node> data2 = new LinkedList<>();
        LinkedList<Node> data3 = new LinkedList<>();

        String fileName1 = address + "SERGEI-INTER_1.csv";
        String fileName2 = address + "SERGEI-INTER_2.csv";
        String fileName3 = address + "SERGEI-INTER_3.csv";

        Quandl q = new Quandl();

        try {
            LinkedList<String> d1 = q.readFile(fileName1, data1);
            LinkedList<String> d2 = q.readFile(fileName2, data2);
            LinkedList<String> d3 = q.readFile(fileName3, data3);
            LinkedList<String> sorted = q.mergeData(d1, d2, d3);
            System.out.print(sorted);

            hash = q.merge(d1, d2, d3, data1, data2, data3, sorted);

            q.writeFile(hash);
            System.out.print("End of program ... ");

        }catch (FileNotFoundException e){System.out.print(e);}
    }
}
