/* This file is part of VoltDB.
 * Copyright (C) 2008-2012 VoltDB Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package com.voltdb.demographicanalytics.vo.logentry;

/**
 * Creates a log entry for a person visiting a site and viewing some kind of ad 
 * or content that can go through a conversion.
 * @author awilson
 *
 */
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
