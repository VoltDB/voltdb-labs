package com.voltdb.demographicanalytics.vo.logentry;

public class AdLogEntry {

    String firstName;
    String lastName;
    int ageActual;
    int incomeActual;
    Age age;
    Sex sex;
    MaritalStatus maritalStatus;
    Income income;
    Education education;
    Occupation occupation;
    String network;
    int cost;
    boolean conversion;

    public AdLogEntry(String firstName, String lastName, Age age,
            int ageActual, Sex sex, MaritalStatus maritalStatus, Income income,
            int incomeActual, Education education, Occupation occupation,
            String network, int cost, boolean conversion) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age=age;
        this.ageActual = ageActual;
        this.sex = sex;
        this.maritalStatus = maritalStatus;
        this.incomeActual = incomeActual;
        this.income = income;
        this.education = education;
        this.occupation = occupation;
        this.network = network;
        this.cost = cost;
        this.conversion = conversion;
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName
     *            the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName
     *            the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the ageActual
     */
    public int getAgeActual() {
        return ageActual;
    }

    /**
     * @param ageActual
     *            the ageActual to set
     */
    public void setAgeActual(int ageActual) {
        this.ageActual = ageActual;
    }

    /**
     * @return the incomeActual
     */
    public int getIncomeActual() {
        return incomeActual;
    }

    /**
     * @param incomeActual
     *            the incomeActual to set
     */
    public void setIncomeActual(int incomeActual) {
        this.incomeActual = incomeActual;
    }

    /**
     * @return the age
     */
    public Age getAge() {
        return age;
    }

    /**
     * @param age
     *            the age to set
     */
    public void setAge(Age age) {
        this.age = age;
    }

    /**
     * @return the sex
     */
    public Sex getSex() {
        return sex;
    }

    /**
     * @param sex
     *            the sex to set
     */
    public void setSex(Sex sex) {
        this.sex = sex;
    }

    /**
     * @return the maritalStatus
     */
    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    /**
     * @param maritalStatus
     *            the maritalStatus to set
     */
    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    /**
     * @return the income
     */
    public Income getIncome() {
        return income;
    }

    /**
     * @param income
     *            the income to set
     */
    public void setIncome(Income income) {
        this.income = income;
    }

    /**
     * @return the education
     */
    public Education getEducation() {
        return education;
    }

    /**
     * @param education
     *            the education to set
     */
    public void setEducation(Education education) {
        this.education = education;
    }

    /**
     * @return the occupation
     */
    public Occupation getOccupation() {
        return occupation;
    }

    /**
     * @param occupation
     *            the occupation to set
     */
    public void setOccupation(Occupation occupation) {
        this.occupation = occupation;
    }

    /**
     * @return the network
     */
    public String getNetwork() {
        return network;
    }

    /**
     * @param network
     *            the network to set
     */
    public void setNetwork(String network) {
        this.network = network;
    }

    /**
     * @return the conversion
     */
    public boolean isConversion() {
        return conversion;
    }

    /**
     * @param conversion
     *            the conversion to set
     */
    public void setConversion(boolean conversion) {
        this.conversion = conversion;
    }

    /**
     * @return the cost
     */
    public int getCost() {
        return cost;
    }

    /**
     * @param cost
     *            the cost to set
     */
    public void setCost(int cost) {
        this.cost = cost;
    }

    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s %s,%s %s,%s,%s", firstName,
                lastName, ageActual, incomeActual, age, sex, maritalStatus,
                income, education, occupation, network, cost, conversion);
    }
}
