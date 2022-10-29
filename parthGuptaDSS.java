import java.io.*;
import java.util.HashMap;
import java.util.Scanner;

// AUTHOR: Parth Gupta

public class parthGuptaDSS {
    public static void main(String[] args) {
        String fileName = "shots_data.csv";

        printCalculations(fileName, "Team A", "Team B");

    }

    public static void printCalculations(String fileName, String team1Name, String team2Name) {
        try {
            // creating file and scanner for file
            File theFile = new File(fileName);
            // note: file must be in same directory as .java file
            Scanner reader = new Scanner(theFile);
            HashMap<String, HashMap<String, Integer>> teamsData = new HashMap<>();
            HashMap<String, Integer> team1Data = new HashMap<>();
            HashMap<String, Integer> team2Data = new HashMap<>();

            // adding individaul team maps to teamsData maps
            teamsData.put(team1Name, team1Data);
            teamsData.put(team2Name, team2Data);

            reader.nextLine(); // reader does not count the labels
            while (reader.hasNextLine()) {
                String[] elements = reader.nextLine().split(","); // splits line into elements delimeted by ","
                // {team name, x, y, 0 or 1}

                double x = Double.valueOf(elements[1]);
                double y = Double.valueOf(elements[2]);
                HashMap<String, Integer> currentTeam = teamsData.get(elements[0]); // current team map of current line
                currentTeam.merge("FGA", 1, Integer::sum);
                boolean shotMade = false;
                if (Integer.valueOf(elements[3]) == 1)
                    shotMade = true;

                int zone = getZone(x, y); // 1 for 2PT, 2 for NC3, 3 for C3
                switch (zone) {
                    case 1:
                        currentTeam.merge("2PT_Total", 1, Integer::sum);
                        if (shotMade)
                            currentTeam.merge("2PT_Made", 1, Integer::sum);
                        break;
                    case 2:
                        currentTeam.merge("NC3_Total", 1, Integer::sum);
                        currentTeam.merge("3PT_Total", 1, Integer::sum);
                        if (shotMade)
                            currentTeam.merge("NC3_Made", 1, Integer::sum);
                        currentTeam.merge("3PT_Made", 1, Integer::sum);
                        break;
                    case 3:
                        currentTeam.merge("C3_Total", 1, Integer::sum);
                        currentTeam.merge("3PT_Total", 1, Integer::sum);
                        if (shotMade)
                            currentTeam.merge("C3_Made", 1, Integer::sum);
                        currentTeam.merge("3PT_Made", 1, Integer::sum);
                        break;
                }
            }

            // distribution calculations
            double team1_2PTShotDistribution = (double) team1Data.get("2PT_Total") / (double) team1Data.get("FGA");
            double team1_NC3ShotDistribution = (double) team1Data.get("NC3_Total") / (double) team1Data.get("FGA");
            double team1_C3ShotDistribution = (double) team1Data.get("C3_Total") / (double) team1Data.get("FGA");

            double team2_2PTShotDistribution = (double) team2Data.get("2PT_Total") / (double) team2Data.get("FGA");
            double team2_NC3ShotDistribution = (double) team2Data.get("NC3_Total") / (double) team2Data.get("FGA");
            double team2_C3ShotDistribution = (double) team2Data.get("C3_Total") / (double) team2Data.get("FGA");

            // eFG calculations
            double team1_2PT_eFG = ((double) team1Data.get("2PT_Made")
                    + (0.5 * (double) team1Data.get("3PT_Made")) / (double) team1Data.get("2PT_Total"));
            double team1_NC3_eFG = ((double) team1Data.get("NC3_Made")
                    + (0.5 * (double) team1Data.get("3PT_Made")) / (double) team1Data.get("NC3_Total"));
            double team1_C3_eFG = ((double) team1Data.get("C3_Made")
                    + (0.5 * (double) team1Data.get("3PT_Made")) / (double) team1Data.get("C3_Total"));

            double team2_2PT_eFG = ((double) team2Data.get("2PT_Made")
                    + (0.5 * (double) team2Data.get("3PT_Made")) / (double) team2Data.get("2PT_Total"));
            double team2_NC3_eFG = ((double) team2Data.get("NC3_Made")
                    + (0.5 * (double) team2Data.get("3PT_Made")) / (double) team2Data.get("NC3_Total"));
            double team2_C3_eFG = ((double) team1Data.get("C3_Made")
                    + (0.5 * (double) team2Data.get("3PT_Made")) / (double) team2Data.get("C3_Total"));

            // printing distribution and efg for team 1
            System.out.println(team1Name + " 2PT Shot Distribution: " + team1_2PTShotDistribution);
            System.out.println(team1Name + " 2PT eFG: " + team1_2PT_eFG);
            System.out.println(team1Name + " NC3 Shot Distribution: " + team1_NC3ShotDistribution);
            System.out.println(team1Name + " NC3 eFG: " + team1_NC3_eFG);
            System.out.println(team1Name + " C3 Shot Distribution: " + team1_C3ShotDistribution);
            System.out.println(team1Name + " C3 eFG: " + team1_C3_eFG);

            System.out.println();

            // printing distribution for team 2
            System.out.println(team2Name + " 2PT Shot Distribution: " + team2_2PTShotDistribution);
            System.out.println(team2Name + " 2PT eFG: " + team2_2PT_eFG);
            System.out.println(team2Name + " NC3 Shot Distribution: " + team2_NC3ShotDistribution);
            System.out.println(team2Name + " NC3 eFG: " + team2_NC3_eFG);
            System.out.println(team2Name + " C3 Shot Distribution: " + team2_C3ShotDistribution);
            System.out.println(team2Name + " C3 eFG: " + team2_C3_eFG);

            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    // returns 1 for 2pt, 2 for NC3, 3 for C3
    private static int getZone(double x, double y) {
        x = Math.abs(x); // only need absolute value because left and right half are identical
        double y_3PTLine = Math.sqrt(Math.pow(23.75, 2) - Math.pow(x, 2)); // y of three pt line at given x

        if (y <= 7.8) { // Guarenteed 3 pointer
            if (x > 22) // Corner 3 pointer
                return 3;
            else
                return 1; // 2 pointer
        } else if (y > y_3PTLine)
            return 2;
        else
            return 1;
    }

}