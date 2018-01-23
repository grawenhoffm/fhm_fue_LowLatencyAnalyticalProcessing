package org.fhms;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import java.sql.ResultSet;

public class HiveConnector {
    private static String driverName = "org.apache.hive.jdbc.HiveDriver";
    private static String server = "wi-cluster01"; //"wi-cluster01.fh-muenster.de";
    private static String port = "10000";
    private static String dbSchema = "orc_1";


    /**
     * @param args
     * @throws SQLException
     */
    public static void main(String[] args) throws IOException, SQLException, InterruptedException {
        ArrayList<Integer> skipQueries  = new ArrayList<>();
        skipQueries.addAll(Arrays.asList(10,16,23,35,41,44,69,71,83));

        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(1);
        }

        //initCSVTables();
        //initBigTable();
        //initORCTables();


        //runTPCDS("wi-cluster01","10000", "orc", 100, skipQueries); // 1 = all
    }

    static public String runTPCDS(String server, String port, String database, int startPoint, ArrayList<Integer> skipQueries) throws InterruptedException, SQLException, IOException {

        Connection con = DriverManager.getConnection("jdbc:hive2://"+ server +":"+ port +"/" + database, "hadoop", "hadoop");
        Map<Integer, String> queries = getQueries("queries_99");
        ArrayList<String> output  = new ArrayList<>();
        String outputPath = "";
        try {
            fireStatement("use " + database, "");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (int i= startPoint; i <queries.size()+1; i++){
            //System.out.println(query);
                if(skipQueries.contains(i)){}
                else {
                    String query = queries.get(i);
                    ArrayList<Long> rs = new ArrayList<Long>();
                    for (int qc = 0; qc < 5; qc++) {
                        try {
                            rs.add(fireQuery(con, query));

                        } catch (SQLException se) {
                            rs.add(0L);
                        }
                    }
                    int queryNumber = i;
                    String newLine = "" + queryNumber + "," + rs.get(0) + "," + rs.get(1) + "," + rs.get(2) + "," + rs.get(3) + "," + rs.get(4);
                    System.out.println(newLine);
                    output.add(newLine);
                }
                outputPath =  writeFile(output);
            }
        con.close();
        return outputPath;
    }

    public static void initCSVTables() throws IOException, SQLException {
        String createStatements_fact = readFile("queries_create/tpcds.sql", "utf-8");
        String createStatements_dim = readFile("queries_create/tpcds_source.sql", "utf-8");
        String data_load = readFile("queries_create/copyData_wicluster.sql", "utf-8");


        List<String> fact = StringToQuery(createStatements_fact);
        List<String> dim = StringToQuery(createStatements_dim);
        List<String> data = StringToQuery(data_load);

        try{
            fireStatement("drop database  default cascade", "");
        }
        catch(Exception e){
            e.printStackTrace();
        }


        for (String query:fact){
            fireStatement(query, "default");
        }
        for (String query:dim){
            fireStatement(query, "default");
        }
        for (String query:data) {
            fireStatement(query, "default");
        }
    }

    public static void initORCTables() throws IOException, SQLException {
        Map<Integer, String> stms = getQueries("queries_create/createORC.sql");

        for(String stm:stms.values()){
            System.out.println(stm);
            fireStatement(stm, "orc");
        }
    }

    public static boolean initBigTable()throws IOException, SQLException{

        String bigTable = readFile("queries_create/createBigTable.sql", "utf-8");


        List<String> bigTabale = StringToQuery(bigTable);


        for (String query:bigTabale){
            fireStatement(query, "default");
        }

        return true;
    }

    public static void test() throws IOException, SQLException, InterruptedException {
        Connection con = DriverManager.getConnection("jdbc:hive2://"+ server +":"+ port +"/" + dbSchema, "hadoop", "hadoop");

        Long time = fireQuery(con,"select * from call_center");
        System.out.println(time);
    }

    /*
    Method to read the HiveQL Statements
     */
    static String readFile(String path, String encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }


    static List StringToQuery(String stringInput){
        List<String> queries = Arrays.asList(stringInput.split(";"));
        return queries;
    }

    /*
    Method to write the results
     */
    private static String writeFile(ArrayList<String> output){
        BufferedWriter writer = null;
        String ret = "Error";
        try {
            //create a temporary file
            //String timeLog = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            File logFile = new File("testFile.txt");

            // This will output the full path where the file will be written to...
            ret = logFile.getCanonicalPath();

            writer = new BufferedWriter(new FileWriter(logFile));

            for(String out:output){
                writer.write(out);
                writer.write("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the writer regardless of what happens...
                writer.close();
            } catch (Exception e) {
            }
            return ret;
        }
    }

    static boolean fireStatement(String query, String db) throws SQLException {
        if (query.length() > 1) {
            //System.out.println(query);
            Connection con = DriverManager.getConnection("jdbc:hive2://"+ server +":"+ port, "hadoop", "hadoop");
            Statement stmt = con.createStatement();
            System.out.println("ok");

            Boolean ret = stmt.execute(query);
            con.close();
            return ret;
        }
        return false;
    }

    static long fireQuery(Connection con, String query) throws SQLException, InterruptedException {
        if (query.length() > 1) {
            long start = System.currentTimeMillis();
            Statement stmt = con.createStatement();
            ResultSet res = stmt.executeQuery(query);
            long stop = System.currentTimeMillis();
            return stop - start;
        }
        return (long) 0.00;
    }

    static Map<Integer, String> getQueries(String directoryPath) throws IOException {
        //File folder = new File(directoryPath);
        File[] listOfFiles = new File(directoryPath).listFiles(new FilenameFilter() { @Override public boolean accept(File dir, String name) { return name.endsWith(".sql"); } });
        //List<String> queries = new ArrayList();
        Map<Integer, String> queries = new HashMap<Integer, String>();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                Integer queryNo = Integer.parseInt(listOfFiles[i].getName().replace("query", "").replace(".sql", ""));

                queries.put(queryNo, readFile(directoryPath +"/"+ listOfFiles[i].getName(), "utf-8").replace(";",""));
            } else if (listOfFiles[i].isDirectory()) {
                //System.out.println("Directory " + listOfFiles[i].getName());
            }
        }
        return queries;
    }

    public static void readBashScript(String size, String format) {
        try {
            Process proc = Runtime.getRuntime().exec("benchmark/generate_orc.sh "+ size + " " + format); //Whatever you want to execute
            BufferedReader read = new BufferedReader(new InputStreamReader(
                    proc.getInputStream()));
            try {
                proc.waitFor();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
            while (read.ready()) {
                System.out.println(read.readLine());
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
