package net.kkolyan.utils.benchme.api;

import java.util.Map;
import java.util.Random;

public abstract class Scenario {

    public abstract void doSingleOperation(Random random, Sensor sensor) throws Exception;

    public abstract String getName();

    public void beforeScenario(Map<String, Integer> conditionValues) throws Exception {}

    public void afterScenario() throws Exception {}

}
