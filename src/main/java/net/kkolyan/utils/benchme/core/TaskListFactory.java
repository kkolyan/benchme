package net.kkolyan.utils.benchme.core;

import net.kkolyan.utils.benchme.api.Parameter;
import net.kkolyan.utils.benchme.api.Parameters;
import net.kkolyan.utils.benchme.api.Scenario;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class TaskListFactory {

    private static class NamedValue {
        String name;
        int value;
    }

    private static List<NamedValue> createNamedValues(int[] values, String name) {
        List<NamedValue> parameterSet = new ArrayList<NamedValue>();
        for (int value: values) {
            NamedValue namedValue = new NamedValue();
            namedValue.name = name;
            namedValue.value = value;
            parameterSet.add(namedValue);
        }
        return parameterSet;
    }

	public static void run(SuiteContext context, List<Task> taskList) {
		final List<Parameter> parameters = new ArrayList<Parameter>();

		if (context.getConfig().getSuiteClass().isAnnotationPresent(Parameter.class))
			parameters.add(context.getConfig().getSuiteClass().getAnnotation(Parameter.class));

		if (context.getConfig().getSuiteClass().isAnnotationPresent(Parameters.class))
			parameters.addAll(Arrays.asList(context.getConfig().getSuiteClass().getAnnotation(Parameters.class).value()));

        // for comparison needs
		final Set<String> parameterKeys = new LinkedHashSet<String>();
        

		List<List<NamedValue>> parameterSetsForAssess = new ArrayList<List<NamedValue>>();
		List<List<NamedValue>> parameterSetsForWarmUp = new ArrayList<List<NamedValue>>();
		for (Parameter condition: parameters) {
            List<NamedValue> namedValues = createNamedValues(condition.values(), condition.name());
            parameterSetsForAssess.add(namedValues);

            if (condition.warmUp().length == 0) {
                parameterSetsForWarmUp.add(namedValues);
            } else {
                parameterSetsForWarmUp.add(createNamedValues(condition.warmUp(), condition.name()));
            }
		}


		final List<List<NamedValue>> combinationsForAssess = new ArrayList<List<NamedValue>>();
		final List<List<NamedValue>> combinationsForWarmUp = new ArrayList<List<NamedValue>>();
		CombinationUtil.computeUniqueCombinations(parameterSetsForAssess, combinationsForAssess);
		CombinationUtil.computeUniqueCombinations(parameterSetsForWarmUp, combinationsForWarmUp);


		if (combinationsForAssess.isEmpty())
			combinationsForAssess.add(Collections.<NamedValue>emptyList());

		if (combinationsForWarmUp.isEmpty())
			combinationsForWarmUp.add(Collections.<NamedValue>emptyList());


		for (Scenario scenario: context.getScenarios()) {
            createTasksByCombinationsAndScenario(combinationsForAssess, context, false, parameterKeys, scenario, taskList);
            createTasksByCombinationsAndScenario(combinationsForWarmUp, context, true, parameterKeys, scenario, taskList);
		}

		Collections.sort(taskList, new TaskComparator(parameterKeys));
	}

    //==========================================================================

    private static void createTasksByCombinationsAndScenario(List<List<NamedValue>> combinations,
                                                             SuiteContext context,
                                                             boolean warmingUp,
                                                             Set<String> parameterKeys,
                                                             Scenario scenario,
                                                             List<Task> taskList) {
        
        for (List<NamedValue> combination: combinations) {

            if (warmingUp && context.getConfig().getWarmingUp().duration() == 0) {
                continue;
            }
            if (!warmingUp && context.getConfig().getMeasurement().duration() == 0) {
                continue;
            }

            Task task = new Task();
            task.setContext(context);
            task.setScenario(scenario);
            task.setWarmingUp(warmingUp);

            for (NamedValue namedValue: combination) {
                task.getParameterValues().put(namedValue.name, namedValue.value);
                parameterKeys.add(namedValue.name);
            }

            taskList.add(task);
        }
    }

    //==========================================================================
}
