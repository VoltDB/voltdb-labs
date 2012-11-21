package com.voltdb.demographicanalytics.vo.logentry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LogEntryFactory {

    public final static void main(String[] args) {
        LogEntryFactory factory = new LogEntryFactory();
        NetworkProfile profile = new NetworkProfile();
        profile.ageMin = 5;
        profile.ageMax = 123;
        profile.maleToFemalePercentage = 60;
        profile.maritalStatusPercentages = new int[] { 5, 25, 60, 7, 3 };
        profile.incomePercentages = new int[] { 20, 15, 22, 7, 19, 17 };
        profile.educationPercentages = new int[] { 4, 10, 22, 35, 29 };
        profile.occupationPercentages = new int[] { 25, 10, 25, 5, 35 };
        profile.network = "NY Times";
        profile.slopeRange = 200;
        profile.conversionRange = 210;

        factory.addProfile(profile);

        createBatchs(factory);
    }

    protected static void createBatchs(LogEntryFactory factory) {
        for (int i = 0; i < 500; i++) {
            List<AdLogEntry> entries = factory.getNextBatch();
            for (AdLogEntry entry : entries) {
                System.out.println(String.format(
                        "%d,%d,%s,%s,%s,%d,%s,%s,%s,%d,%s,%s,%s,%s", i,
                        entry.getNetwork(), entry.getCost(),
                        entry.getFirstName(), entry.getLastName(),
                        entry.getAgeActual(), entry.getAge(), entry.getSex(),
                        entry.getMaritalStatus(), entry.getIncomeActual(),
                        entry.getIncome(), entry.getEducation(),
                        entry.getOccupation(), entry.isConversion()));
            }
        }
    }

    public final static String[] MALE_NAMES = new String[] { "Abel", "Ariel",
            "Chaviv", "Dan", "Ethan", "Ezra", "Mattan", "Nisan", "Omar",
            "Alexis", "Christoph", "Bevis", "Boris", "Brock", "Calvert",
            "Carrick", "Chester", "Christian", "Corwin", "Jack", "Thomas",
            "Joshua", "William", "Daniel", "Matthew", "James", "Joseph",
            "Harry", "Samuel" };

    public final static String[] FEMALE_NAMES = new String[] { "Emily",
            "Chloe", "Megan", "Jessica", "Emma", "Sarah", "Elizabeth",
            "Sophie", "Olivia", "Lauren", "Amber", "Angelica", "April",
            "Bertina", "Bianca", "Blanche", "Bonnie", "Salena", "Sandra",
            "Shana", "Talia", "Tanya", "Thelma", "Tracy", "Trista", "Valerie",
            "Vera", "Victoria", "Vivian" };

    public final static String[] LAST_NAMES = new String[] { "Smith", "Brown",
            "Lee", "Wilson", "Patel", "Martin", "Taylor", "Wong", "Campbell",
            "Williams", "Thompson", "Jones", "Moore", "White", "Harris",
            "Lewis", "Walker", "Hall", "Allen", "Wright", "Scott", "Baker",
            "Roberts", "Torres", "Flores", "Rogers", "Ward", "Cruz", "Foster" };

    List<NetworkProfile> profiles = new ArrayList<NetworkProfile>();
    Random rand;

    public LogEntryFactory() {
        rand = new Random(7);
    }

    public void addProfile(NetworkProfile profile) {
        if (profile != null) {
            this.profiles.add(profile);
        }
    }

    public List<AdLogEntry> getNextBatch() {
        List<AdLogEntry> results = new ArrayList<AdLogEntry>();

        for (NetworkProfile profile : this.profiles) {
            int batchSize = profile.lastBatch;
            batchSize = Math.min(batchSize, rand.nextInt(5000));
            int slopeDirection = ((float)rand.nextInt(profile.slopeRange) > profile.slopeRange/2.0 ? 1:-1);
            batchSize += (slopeDirection * rand.nextInt(profile.slopeRange))*rand.nextInt(7);
            //System.out.printf("\t\t%s %d %d%n", profile.network, slopeDirection, batchSize);
            batchSize = (batchSize > 0 ? batchSize : 1); // no zero size batches'
            profile.lastBatch = batchSize;

            for (int i = 0; i < batchSize; i++) {
                boolean male = getSex(profile);
                Sex sex = Sex.values()[male ? 0 : 1];

                String firstName = getFirstName(male);
                String lastName = getLastName();

                int ageActual = getAge(profile);
                Age age = Age.getAge(ageActual);

                Income income = getIncome(profile);
                int incomeActual = getActualIncome(income);

                MaritalStatus maritalStatus = getMaritalStatus(profile);
                Education education = getEducation(profile);

                Occupation occupation = getOccupation(profile);
                boolean conversion = rand.nextInt(profile.conversionRange) >= rand
                        .nextInt(batchSize);

                results.add(new AdLogEntry(firstName, lastName, age, ageActual,
                        sex, maritalStatus, income, incomeActual, education,
                        occupation, profile.network, profile.getCost(),
                        conversion));
            }
        }
       // System.out.printf("%d %d %.2f%n", totalEntries, totalConversions,
       //         ((float)totalConversions / (float)totalEntries));
        return results;
    }

    private String getFirstName(boolean male) {
        String name = male ? MALE_NAMES[rand.nextInt(MALE_NAMES.length)]
                : FEMALE_NAMES[rand.nextInt(FEMALE_NAMES.length)];
        return name;
    }

    private String getLastName() {
        return LAST_NAMES[rand.nextInt(LAST_NAMES.length)];
    }

    private boolean getSex(NetworkProfile profile) {
        boolean results = profile.maleToFemalePercentage <= rand.nextInt(100);
        return results;
    }

    private int getAge(NetworkProfile profile) {
        int dif = profile.ageMax - profile.ageMin;
        int age = profile.ageMin + rand.nextInt(dif);
        return age;
    }

    private Income getIncome(NetworkProfile profile) {
        int range = getRangePreference(profile.incomePercentages);
        return Income.getIncomeByIndex(range);
    }

    private int getActualIncome(Income income) {
        int results = rand.nextInt(income.getRange()) + income.getMin();
        return results;
    }

    private MaritalStatus getMaritalStatus(NetworkProfile profile) {
        int preference = getRangePreference(profile.maritalStatusPercentages);
        MaritalStatus results = MaritalStatus.values()[preference];
        return results;
    }

    private Education getEducation(NetworkProfile profile) {
        int preference = getRangePreference(profile.educationPercentages);
        Education results = Education.values()[preference];
        return results;
    }

    private Occupation getOccupation(NetworkProfile profile) {
        int preference = getRangePreference(profile.occupationPercentages);
        Occupation results = Occupation.values()[preference];
        return results;
    }

    private int getRangePreference(int[] preferences) {
        int results = 0;
        int percentage = rand.nextInt(101);
        int maxVal = 0;
        for (int i = 0; i < preferences.length; i++) {
            maxVal += preferences[i];
            if (percentage <= maxVal) {
                results = i;
                break;
            }
        }

        return results;
    }

}
