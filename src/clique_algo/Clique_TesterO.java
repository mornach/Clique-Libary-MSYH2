package clique_algo;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Clique_TesterO {

  public static int minQ = 18, maxQ = 28;
    public static double TH =  0.69;
    public static String in_file = "test1.csv";
    public static String out_file = null;
    public static boolean Debug = false;
    public static int MAX_CLIQUE = 100000;
    public static boolean Convert = true;

    public static void main(String[] args) {

        try {
            // test1.csv_DG.txt  0.8 5 7
            if (args == null || args.length < 3) {
                help();
            } else {
                parse(args);
            }
            long t0 = new Date().getTime();
            Graph G = new Graph(in_file, TH);
            long t1 = new Date().getTime();
            System.out.println("Init Graph: " + (t1 - t0) + "  ms");

           PrintWriter writer;
            writer = new PrintWriter("outPutOriginal.txt", "UTF-8");
            writer.println(TH + " " + minQ + " " + maxQ);
            double sum=0;
            int n=20;
            for (int i = 0; i < n; i++) {
                long t2 = new Date().getTime();
                if (out_file == null) {
                    out_file = in_file + "_" + TH + "_" + minQ + "_" + maxQ + ".csv";
                }
                G.All_Cliques_DFS(out_file, minQ, maxQ);
                long t3 = new Date().getTime();
                sum+=t3-t2;
                writer.println(t3 - t2);
            }
            double avgT = sum/n;
            writer.println("avg: "+avgT);
            
            writer.close();

        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            Logger.getLogger(Clique_TesterO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static void help() {
       // System.out.println("Wrong Parameters! should use: java -jar All_Cliques <input file> <round value> <min clique> <max clique> <output file> <max_cliques> <Graph convert flag>");
       // System.out.println("Wrong Parameters! should use: java -jar All_Cliques test1.csv 0.7 5 7 test1_out.txt 10000 true");
    }

    static void parse(String[] a) {
        try {
            in_file = a[0];
            TH = new Double(a[1]);
            minQ = new Integer(a[2]);
            maxQ = new Integer(a[3]);
            if (a.length > 4) {
                out_file = a[4];
            }
            if (a.length > 5) {
                MAX_CLIQUE = new Integer(a[5]);
            }
            if (a.length > 6) {
                Convert = Boolean.valueOf(a[6]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            help();
        }
    }
}
