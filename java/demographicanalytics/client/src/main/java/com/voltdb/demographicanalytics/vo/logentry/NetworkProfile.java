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

import java.util.Arrays;

/**
 * Creates a network profile based upon the JSON config file.
 * @author awilson
 *
 */
public class NetworkProfile {

    protected String network;
    protected int cost;
    protected int lastBatch;
    protected int ageMin;
    protected int ageMax;
    protected int maleToFemalePercentage;
    protected int[] maritalStatusPercentages;
    protected int[] incomePercentages;
    protected int[] educationPercentages;
    protected int[] occupationPercentages;
    protected int slopeRange;
    protected int conversionRange;
    public transient int lastConversions;

    public NetworkProfile() {
    }

    public NetworkProfile(String network, int cost, int ageMin, int ageMax,
            int maleToFemalePercentage, int[] maritalStatusPercentages,
            int[] incomePercentages, int[] educationPercentages,
            int[] occupationPercentages, int slopeRange, int conversionRange) {
        this.network = network;
        this.ageMin = ageMin;
        this.ageMax = ageMax;
        this.maleToFemalePercentage = maleToFemalePercentage;
        this.maritalStatusPercentages =maritalStatusPercentages;
        this.incomePercentages = incomePercentages;
        this.educationPercentages = educationPercentages;
        this.occupationPercentages = occupationPercentages;
        this.slopeRange = slopeRange;
        this.conversionRange = conversionRange;
    }

    /**
     * @return the network
     */
    public String getNetwork() {
        return network;
    }

    /**
     * @param network the network to set
     */
    public void setNetwork(String network) {
        this.network = network;
    }

    /**
     * @return the ageMin
     */
    public int getAgeMin() {
        return ageMin;
    }

    /**
     * @param ageMin the ageMin to set
     */
    public void setAgeMin(int ageMin) {
        this.ageMin = ageMin;
    }

    /**
     * @return the ageMax
     */
    public int getAgeMax() {
        return ageMax;
    }

    /**
     * @param ageMax the ageMax to set
     */
    public void setAgeMax(int ageMax) {
        this.ageMax = ageMax;
    }

    /**
     * @return the maleToFemalePercentage
     */
    public int getMaleToFemalePercentage() {
        return maleToFemalePercentage;
    }

    /**
     * @param maleToFemalePercentage the maleToFemalePercentage to set
     */
    public void setMaleToFemalePercentage(int maleToFemalePercentage) {
        this.maleToFemalePercentage = maleToFemalePercentage;
    }

    /**
     * @return the maritalStatusPercentages
     */
    public int[] getMaritalStatusPercentages() {
        return maritalStatusPercentages;
    }

    /**
     * @param maritalStatusPercentages the maritalStatusPercentages to set
     */
    public void setMaritalStatusPercentages(int[] maritalStatusPercentages) {
        this.maritalStatusPercentages = maritalStatusPercentages;
    }

    /**
     * @return the incomePercentages
     */
    public int[] getIncomePercentages() {
        return incomePercentages;
    }

    /**
     * @param incomePercentages the incomePercentages to set
     */
    public void setIncomePercentages(int[] incomePercentages) {
        this.incomePercentages = incomePercentages;
    }

    /**
     * @return the educationPercentages
     */
    public int[] getEducationPercentages() {
        return educationPercentages;
    }

    /**
     * @param educationPercentages the educationPercentages to set
     */
    public void setEducationPercentages(int[] educationPercentages) {
        this.educationPercentages = educationPercentages;
    }

    /**
     * @return the occupationPercentages
     */
    public int[] getOccupationPercentages() {
        return occupationPercentages;
    }

    /**
     * @param occupationPercentages the occupationPercentages to set
     */
    public void setOccupationPercentages(int[] occupationPercentages) {
        this.occupationPercentages = occupationPercentages;
    }

    /**
     * @return the slopeRange
     */
    public int getSlopeRange() {
        return slopeRange;
    }

    /**
     * @param slopeRange the slopeRange to set
     */
    public void setSlopeRange(int slopeRange) {
        this.slopeRange = slopeRange;
    }

    /**
     * @return the conversionRange
     */
    public int getConversionRange() {
        return conversionRange;
    }

    /**
     * @param conversionRange the conversionRange to set
     */
    public void setConversionRange(int conversionRange) {
        this.conversionRange = conversionRange;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "NetworkProfile { network=" + network + ", ageMin=" + ageMin
                + ", ageMax=" + ageMax + ", maleToFemalePercentage="
                + maleToFemalePercentage + ", maritalStatusPercentages="
                + Arrays.toString(maritalStatusPercentages)
                + ", incomePercentages=" + Arrays.toString(incomePercentages)
                + ", educationPercentages="
                + Arrays.toString(educationPercentages)
                + ", occupationPercentages="
                + Arrays.toString(occupationPercentages) + ", slopeRange="
                + slopeRange + ", conversionRange=" + conversionRange + "}";
    }

    /**
     * @return the cost
     */
    public int getCost() {
        return cost;
    }

    /**
     * @param cost the cost to set
     */
    public void setCost(int cost) {
        this.cost = cost;
    }


}
