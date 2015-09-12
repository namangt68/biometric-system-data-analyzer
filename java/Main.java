import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.Integer;
import java.lang.Math;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;

class Match {
    public int x,y,xBest,yBest;
    public double matching;
    public Match(int x, int y, int xBest, int yBest, double matching) {
        this.x = x;
        this.y = y;
        this.xBest = xBest;
        this.yBest = yBest;
        this.matching = matching;
    }
}

class Main {

    private static final int RESOLUTION = 1000000 + 1;
    private static final String FILE_NAME = "/home/swap/Documents/btech/sem5/CS_307/assign1/L4.txt";
    private static ArrayList<Match> MatchList = new ArrayList<Match>();
    private static int tfrr = 0, tfar = 0;
    /**
     * @param x
     * @param y
     * @param xBest
     * @param yBest
     * @param xWorst
     * @param yWorst
     * @param bestMatching
     * @param worstMatching
     * @param bestMatchIsGenuine
     * @return
     */
    public static String crrFormat(int x, int y,
                            int xBest, int yBest,
                            int xWorst, int yWorst,
                            Double bestMatching, Double worstMatching,
                            int bestMatchIsGenuine) {
        return x + " " + y + "\t" +
                "[(" + xBest + "," + yBest + ")" + bestMatching + "]\t" +
                "[(" + xWorst + "," + yWorst + ")" + worstMatching + "]\t" +
                bestMatching/worstMatching + "\t" + bestMatchIsGenuine;

    }

    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(FILE_NAME));
        PrintWriter pw = new PrintWriter(System.out);
        PrintWriter far_frr = new PrintWriter(new FileWriter("FRR_FAR.dat"));
        int[] genuine = new int[RESOLUTION];
        int[] imposter = new int[RESOLUTION];
        int[] genuineSum = new int[RESOLUTION];
        int[] imposterSum = new int[RESOLUTION];
        String line = br.readLine();
        int lineCount = 1;

        while (line != null) {
            String[] words = line.split("[\t ]");
            boolean isGenuine = (words[0].equals(words[2]));
            int slot = 0;
            try {

                slot = (int) (Double.parseDouble(words[5]) * 1000000);
            } catch (java.lang.NumberFormatException ex) {
                System.out.println(line);
                System.out.println(lineCount);
                br.readLine();
            }
            if (isGenuine) {
                genuine[slot]++;
            } else {
                imposter[slot]++;
            }
            line = br.readLine();
            lineCount++;
        }

        for (int i = 1; i < genuine.length; i++) {
            genuineSum[i] = genuineSum[i - 1] + genuine[i];
            imposterSum[i] = imposterSum[i - 1] + imposter[i];
        }

        int totalImposters = imposterSum[RESOLUTION - 1];
        int totalGenuine = genuineSum[RESOLUTION - 1];
        double err = 0, minDiff = 100, efrr = 0, efar = 0;
        double errThreshold = 0;
        double meanGenuine = 0;
        BigDecimal varGenuine = new BigDecimal("0.0");
        int countGenuine = 0;
        double meanImposter = 0;
        BigDecimal varImposter = new BigDecimal("0.0");
        int countImposter = 0;
        PrintWriter gHist = new PrintWriter(new FileWriter("G_Hist.dat"));
        PrintWriter iHist = new PrintWriter(new FileWriter("I_Hist.dat"));
        for (int i = 0; i < genuine.length; i++) {
            double far = (imposterSum[i] * 1.0 / totalImposters);
            double frr = (totalGenuine - genuineSum[i]) / (1.0 * totalGenuine);
            if (Math.abs(far - frr) < minDiff) {
                minDiff = Math.abs(far - frr);
                err = (far + frr) / 2;
                errThreshold = i * 1.0 / 1000000;
                efar = far;
                efrr = frr;
            }
            far_frr.println(i + "\t" + genuineSum[i] + "\t" + (frr * 100) + "\t\t" + i + "\t" + imposterSum[i] + "\t" + far * 100);

            if (100 * (genuine[i] / (1.0 * totalGenuine)) != 0) {
                gHist.println(i + "\t" + (100 * (genuine[i] / (1.0 * totalGenuine))));
                meanGenuine += i * (genuine[i] / (1.0 * totalGenuine * Math.pow(10, 6)));
                countGenuine += 1;
            }

            if (100 * (imposter[i] / (1.0 * totalImposters)) != 0) {
                iHist.println(i + "\t" + (100 * (imposter[i] / (1.0 * totalImposters))));
                meanImposter += i * (imposter[i] / (1.0 * totalImposters * Math.pow(10, 6)));
                countImposter += 1;
            }
        }
        pw.println("Average Genuine: " + meanGenuine);
        pw.println("Average Imposter: " + meanImposter);
        pw.println("Equal Error Rate: " + err);
        pw.println("ERR Threshold : " + errThreshold);
        pw.println("FAR at ERR Threshold : " + efar);
        pw.println("FRR at ERR Threshold : " + efrr);
        pw.println("Difference between FAR and FRR at ERR : " + Math.abs(efrr - efar));
        far_frr.close();
        br.close();
        gHist.close();
        iHist.close();

        BufferedReader gReadHist = new BufferedReader(new FileReader("G_Hist.dat"));
        line = gReadHist.readLine();
        while (line != null) {
            String[] words = line.split("[\t ]");
            int i = Integer.parseInt(words[0]);
            double j = Double.parseDouble(words[1]);
            varGenuine = varGenuine.add(BigDecimal.valueOf((Math.pow((i/Math.pow(10, 6)) - meanGenuine, 2) * j)/100));
            line = gReadHist.readLine();
        }
        gReadHist.close();
        pw.println("Variance Genuine: " + varGenuine.toString());

        BufferedReader iReadHist = new BufferedReader(new FileReader("I_Hist.dat"));
        line = iReadHist.readLine();
        while (line != null) {
            String[] words = line.split("[\t ]");
            int i = Integer.parseInt(words[0]);
            double j = Double.parseDouble(words[1]);
            varImposter = varImposter.add(BigDecimal.valueOf((Math.pow((i/Math.pow(10, 6)) - meanImposter, 2) * j)/100));
            line = iReadHist.readLine();
        }
        iHist.close();
        pw.println("Variance Imposter: " + varImposter.toString());
        pw.close();

        br = new BufferedReader(new FileReader(FILE_NAME));
        PrintWriter fr = new PrintWriter(new FileWriter("test.FR"));
        PrintWriter fa = new PrintWriter(new FileWriter("test.FA"));
        line = br.readLine();
        while (line != null) {

            String[] words = line.split("[\t ]");
            boolean isGenuine = (words[0].equals(words[2]));
            if (isGenuine && Double.parseDouble(words[5]) > errThreshold) {
                fr.println(line);
                tfrr++;
            } else if (!isGenuine && Double.parseDouble(words[5]) < errThreshold) {
                fa.println(line);
                tfar++;
            }
            line = br.readLine();
        }
        fa.close();
        fr.close();
        br.close();
        br = new BufferedReader(new FileReader(FILE_NAME));
        PrintWriter fcrr = new PrintWriter(new FileWriter("CRR.dat"));
        line = br.readLine();
        int x = 0, y = 0, xBest = 0, yBest = 0,xWorst = 0,yWorst = 0;
        Double bestMatching = 2.0, worstMatching = -1.0;
        int bestMatchingIsGenuine = 0;
        int crrTotal = 0, crrTrue = 0;
        while (line != null) {
            String[] words = line.split("[\t ]");
            int s1id = Integer.parseInt(words[0]);
            int p1id = Integer.parseInt(words[1]);
            int s2id = Integer.parseInt(words[2]);
            int p2id = Integer.parseInt(words[3]);
            boolean isGenuine = (words[0].equals(words[2]));
            double matching = Double.parseDouble(words[5]);
            if (x != s1id || y != p1id) {
                if(x!=0) { fcrr.println(crrFormat(x,y,
                        xBest, yBest,
                        xWorst,yWorst,
                        bestMatching,worstMatching,bestMatchingIsGenuine));
                if(bestMatchingIsGenuine==0) {
                    Match m = new Match(x,y,xBest,yBest,bestMatching);
                    MatchList.add(m);
                }
                else crrTrue++;
                }
                crrTotal++;
                x = s1id;
                y = p1id;
                xBest = s2id;
                yBest = p2id;
                xWorst = s2id;
                yWorst = p2id;
                bestMatching = matching;
                worstMatching = matching;
                bestMatchingIsGenuine = isGenuine ? 1:0;
            }
            else {
                if (matching < bestMatching) {
                    if (isGenuine) bestMatchingIsGenuine = 1;
                    else bestMatchingIsGenuine = 0;
                    bestMatching = matching;
                    xBest = s2id;
                    yBest = p2id;
                }
                if (matching > worstMatching) {
                    xWorst = s2id;
                    yWorst = p2id;
                    worstMatching = matching;
                }
            }
            line = br.readLine();
        }
        fcrr.close();

        // Printing to .sts file
        PrintWriter statsFile = new PrintWriter(new FileWriter("Stats.sts"));
        String star = "**************************************************************************************************************************************";
//        for (int i = 0; i < 6; i++) {
//            star += star;
//        }
        String equalLine = "===========================";
//        for (int i = 0; i < 3; i++) {
//            equalLine += equalLine;
//        }
        String plusLine = "+++++++++++++++++++++++++++++++++++++++++++++++++++++";
//        for (int i = 0; i < 4; i++) {
//            plusLine += plusLine;
//        }

        statsFile.println(star);
        statsFile.println("Genuine");
        statsFile.println(equalLine);
        statsFile.println(countGenuine + " data points");
        statsFile.println(meanGenuine + " average");
        double stdDeviationGenuine = Math.sqrt(varGenuine.doubleValue());
        statsFile.println(stdDeviationGenuine  + " standard deviation");
        statsFile.println(stdDeviationGenuine/(Math.sqrt(totalGenuine + totalImposters)) + " standard error");
        statsFile.println(star);
        statsFile.println("Imposter");
        statsFile.println(equalLine);
        statsFile.println(countImposter + " data points");
        statsFile.println(meanImposter + " average");
        double stdDeviationImposter = Math.sqrt(varImposter.doubleValue());
        statsFile.println(stdDeviationImposter + " standard deviation");
        statsFile.println(stdDeviationImposter/(Math.sqrt(totalGenuine + totalImposters)) + " standard error");
        statsFile.println(star);
        statsFile.println("Performance Parameters");
        statsFile.println(equalLine);
        double dIndex = Math.abs(meanGenuine - meanImposter)/Math.sqrt((varGenuine.doubleValue() + varImposter.doubleValue())/2);
        statsFile.println("Decidability Index\t\t\t(DI) =\t" + dIndex);
        double cRRate = (crrTrue*1.0/crrTotal)*100;
        statsFile.println("Correct Recognition Rate\t\t\t(CRR) =\t" + cRRate);
        statsFile.println("Equal Error Rate\t\t\t(ERR) (" + errThreshold + ") " + err + " with Difference = " + Math.abs(efrr - efar));
        statsFile.println(star);
        statsFile.println("Failed Subject in identification (CRR Failure)");
        for(Match m: MatchList) {
            statsFile.println(m.x+"\t"+m.y+"\t"+m.xBest+"\t"+m.yBest+"\t"+m.matching);
        }
        statsFile.println(equalLine);
        statsFile.println(plusLine);
        statsFile.println("Total Failed Subject =   " + MatchList.size() + " out of " + crrTotal);
        statsFile.println(plusLine);
        statsFile.println("Total No. of Falsely Reject Matching =   " + tfrr + " out of total " + totalGenuine + " genuine matching");
        statsFile.println(plusLine);
        statsFile.println("Total No. of Falsely Accept Matching =   " + tfar + " out of total " + totalImposters + " imposter matching");
        statsFile.println(star);
        statsFile.println("Actual Performance Parameters");
        statsFile.println(equalLine);
        statsFile.println("False Acceptance Rate\t\t(FAR) = \t" + efar);
        statsFile.println("False Rejection Rate\t\t(FAR) = \t" + efrr);
        statsFile.close();
    }
}