package org.fhms;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class TestConnector {
    public static void main(String[] args) throws IOException, SQLException, InterruptedException {

        String server = "wi-cluster01";
        String port = "1000";
        String database = "default";
        Integer startPoint = 100;

        if (args.length > 0) {
            try {
                System.out.println("start with params: " + args[0] + ", " + args[1] + ", " + args[2]+ ", " + args[3]);
                server = args[0];
                port = args[1];
                database = args[2];
                startPoint = Integer.parseInt(args[3]);

                System.out.println("start running");
                ArrayList<Integer> skipQueries  = new ArrayList<>();
                skipQueries.addAll(Arrays.asList(10,16,23,35,41,44,69,71,83));
                String path = HiveConnector.runTPCDS(server, port, database, startPoint, skipQueries);
                System.out.println(path);

            } catch (NumberFormatException e) {
                System.err.println("Argument" + args[0] + " must be an String.");
                System.err.println("Argument" + args[1] + " must be an String.");
                System.err.println("Argument" + args[2] + " must be an Integer.");
                System.exit(1);
            }
        }
    }
}
