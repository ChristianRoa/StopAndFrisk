import java.util.ArrayList;

/**
 * The StopAndFrisk class represents stop-and-frisk data, provided by
 * the New York Police Department (NYPD), that is used to compare
 * during when the policy was put in place and after the policy ended.
 * 
 * @author Tanvi Yamarthy
 * @author Vidushi Jindal
 */
public class StopAndFrisk {

    /*
     * The ArrayList keeps track of years that are loaded from CSV data file.
     * Each SFYear corresponds to 1 year of SFRecords. 
     * Each SFRecord corresponds to one stop and frisk occurrence.
     */ 
    private ArrayList<SFYear> database; 

    /*
     * Constructor creates and initializes the @database array
     * 
     * DO NOT update nor remove this constructor
     */
    public StopAndFrisk () {
        database = new ArrayList<>();
    }

    /*
     * Getter method for the database.
     * *** DO NOT REMOVE nor update this method ****
     */
    public ArrayList<SFYear> getDatabase() {
        return database;
    }

    /**
     * This method reads the records information from an input csv file and populates 
     * the database.
     * 
     * Each stop and frisk record is a line in the input csv file.
     * 
     * 1. Open file utilizing StdIn.setFile(csvFile)
     * 2. While the input still contains lines:
     *    - Read a record line (see assignment description on how to do this)
     *    - Create an object of type SFRecord containing the record information
     *    - If the record's year has already is present in the database:
     *        - Add the SFRecord to the year's records
     *    - If the record's year is not present in the database:
     *        - Create a new SFYear 
     *        - Add the SFRecord to the new SFYear
     *        - Add the new SFYear to the database ArrayList
     * 
     * @param csvFile
     */
    public void readFile ( String csvFile ) {

        // DO NOT remove these two lines
        StdIn.setFile(csvFile); // Opens the file
        StdIn.readLine();       // Reads and discards the header line
        while(!StdIn.isEmpty()){
            String[] recordEntries = StdIn.readLine().split(",");
            int year = Integer.parseInt(recordEntries[0]);
            String caseDescription = recordEntries[2];
            boolean arrested = recordEntries[13].equals("Y");
            boolean frisked = recordEntries[16].equals("Y");
            String gender = recordEntries[52];
            String race = recordEntries[66];
            String location = recordEntries[71];
            SFRecord record = new SFRecord(caseDescription, arrested, frisked, gender, race, location);
            boolean yearFound = false;
            for (SFYear sfYear : database) {
                if (sfYear.getcurrentYear() == year) {
                    sfYear.addRecord(record);
                    yearFound = true;
                    break;
                }
            }
    
            if(!yearFound){
                SFYear newYear = new SFYear(year);
                newYear.addRecord(record);
                database.add(newYear);
            }
        }
        // WRITE YOUR CODE HERE

    }

    /**
     * This method returns the stop and frisk records of a given year where 
     * the people that was stopped was of the specified race.
     * 
     * @param year we are only interested in the records of year.
     * @param race we are only interested in the records of stops of people of race. 
     * @return an ArrayList containing all stop and frisk records for people of the 
     * parameters race and year.
     */

    public ArrayList<SFRecord> populationStopped ( int year, String race ) {
        ArrayList<SFRecord> result = new ArrayList<>();
        for(int i = 0; i < database.size(); i++){
            SFYear currentYear = database.get(i);
            if (currentYear.getcurrentYear() == year){
                ArrayList<SFRecord> yearRecords = currentYear.getRecordsForYear();
                for(int j = 0; j < yearRecords.size(); j++){
                    SFRecord record = yearRecords.get(j);
                    if (record.getRace().equals(race)){
                        result.add(record);
                    }
                }
            }

        }
        return result;

        // WRITE YOUR CODE HERE

    }

    /**
     * This method computes the percentage of records where the person was frisked and the
     * percentage of records where the person was arrested.
     * 
     * @param year we are only interested in the records of year.
     * @return the percent of the population that were frisked and the percent that
     *         were arrested.
     */
    public double[] friskedVSArrested ( int year ) {
        ArrayList<SFRecord> yearRecords = getRecordsForYear(year);
        double friskedCount = 0;
        double arrestedCount = 0;
        for (SFRecord record : yearRecords) {
            if (record.getFrisked()) {
                friskedCount++;
            }

            if (record.getArrested()) {
                arrestedCount++;
            }
        }

        double totalRecords = yearRecords.size();
        double friskedPercentage = (friskedCount / totalRecords) * 100;
        double arrestedPercentage = (arrestedCount / totalRecords) * 100;

        return new double[]{friskedPercentage, arrestedPercentage};
    }

    // Helper method to get records for a specific year
    private ArrayList<SFRecord> getRecordsForYear(int year) {
        for (SFYear sfYear : database) {
            if (sfYear.getcurrentYear() == year) {
                return sfYear.getRecordsForYear();
            }
        }
        return new ArrayList<>(); // Return an empty list if the year is not found
    }

    /**
     * This method keeps track of the fraction of Black females, Black males,
     * White females and White males that were stopped for any reason.
     * Drawing out the exact table helps visualize the gender bias.
     * 
     * @param year we are only interested in the records of year.
     * @return a 2D array of percent of number of White and Black females
     *         versus the number of White and Black males.
     */
    public double[][] genderBias ( int year ) {
        ArrayList<SFRecord> yearRecords = getRecordsForYear(year);
        int blackCount = 0;
        int whiteCount = 0;
        int blackFemaleCount = 0;
        int blackMaleCount = 0;
        int whiteFemaleCount = 0;
        int whiteMaleCount = 0;
        for (SFRecord record : yearRecords) {
            String gender = record.getGender();
            String race = record.getRace();
            if ("B".equalsIgnoreCase(race)) {
                blackCount++;
                if ("F".equalsIgnoreCase(gender)) {
                    blackFemaleCount++;
                } else if ("M".equalsIgnoreCase(gender)) {
                    blackMaleCount++;
                }
            } else if ("W".equalsIgnoreCase(race)) {
                whiteCount++;
                if ("F".equalsIgnoreCase(gender)) {
                    whiteFemaleCount++;
                } else if ("M".equalsIgnoreCase(gender)) {
                    whiteMaleCount++;
                }
            }
        }
        double[][] result = new double[2][3];
        result[0][0] = (blackFemaleCount / (double) blackCount) *.5 * 100;
        result[0][1] = (whiteFemaleCount / (double) whiteCount) *.5 * 100;
        result[0][2] = ((blackFemaleCount / (double) blackCount) *.5 * 100) + ((whiteFemaleCount / (double) whiteCount) * .5 * 100);
        result[1][0] = (blackMaleCount / (double) blackCount) * .5 * 100;
        result[1][1] = (whiteMaleCount / (double) whiteCount) * .5 * 100;
        result[1][2] = ((blackMaleCount / (double) blackCount) *.5 * 100) + ((whiteMaleCount / (double) whiteCount) * .5 * 100);
    
        return result;
    }    // WRITE YOUR CODE HERE


    /**
     * This method checks to see if there has been increase or decrease 
     * in a certain crime from year 1 to year 2.
     * 
     * Expect year1 to preceed year2 or be equal.
     * 
     * @param crimeDescription
     * @param year1 first year to compare.
     * @param year2 second year to compare.
     * @return 
     */
    public double crimeIncrease ( String crimeDescription, int year1, int year2 ) {
        int crimeCounter1 = 0;
        int crimeCounter2 = 0;
        ArrayList<SFRecord> yearRecords1 = getRecordsForYear(year1);
        ArrayList<SFRecord> yearRecords2 = getRecordsForYear(year2);
        for (SFRecord record : yearRecords1){
            if (record.getDescription().indexOf(crimeDescription) != -1){
                crimeCounter1++;
            }
        }
        for (SFRecord record2 : yearRecords2){
            if (record2.getDescription().indexOf(crimeDescription) != -1){
                crimeCounter2++;
            }
        }
        double percentYear1 = ((double) crimeCounter1 / yearRecords1.size()) * 100;
        double percentYear2 = ((double) crimeCounter2 / yearRecords2.size()) * 100;
        double crimeRateChange = percentYear2 - percentYear1;
        return crimeRateChange;
        // update the return value
    }

    /**
     * This method outputs the NYC borough where the most amount of stops 
     * occurred in a given year. This method will mainly analyze the five 
     * following boroughs in New York City: Brooklyn, Manhattan, Bronx, 
     * Queens, and Staten Island.
     * 
     * @param year we are only interested in the records of year.
     * @return the borough with the greatest number of stops
     */
    public String mostCommonBorough ( int year ) {
        ArrayList<SFRecord> yearRecords = getRecordsForYear(year);
        int brooklynCounter = 0;
        int manhattanCounter = 0;
        int bronxCounter = 0;
        int queensCounter = 0;
        int statenIslandCounter = 0;
        for (SFRecord record : yearRecords) {
            String location = record.getLocation();
            if ("BROOKLYN".equalsIgnoreCase(location)) {
                brooklynCounter++;
            }
            else if ("MANHATTAN".equalsIgnoreCase(location)){
                manhattanCounter++;
            }
            else if ("BRONX".equalsIgnoreCase(location)){
                bronxCounter++;
            }
            else if ("QUEENS".equalsIgnoreCase(location)){
                queensCounter++;
            }
            else if ("STATEN ISLAND".equalsIgnoreCase(location)){
                statenIslandCounter++;
            }
        }
        int maxCounter = 0;
        String mostCommonBorough = null;

        if (brooklynCounter > maxCounter) {
            maxCounter = brooklynCounter;
            mostCommonBorough = "Brooklyn";
        }

        if (manhattanCounter > maxCounter) {
            maxCounter = manhattanCounter;
            mostCommonBorough = "Manhattan";
        }

        if (bronxCounter > maxCounter) {
            maxCounter = bronxCounter;
            mostCommonBorough = "Bronx";
        }

        if (queensCounter > maxCounter) {
            maxCounter = queensCounter;
            mostCommonBorough = "Queens";
        }

        if (statenIslandCounter > maxCounter) {
            mostCommonBorough = "Staten Island";
        }

        return mostCommonBorough; // update the return value
    }
}


