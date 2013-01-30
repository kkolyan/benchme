package net.kkolyan.utils.benchme.core;

import java.util.Collection;
import java.util.Comparator;

public class TaskComparator implements Comparator<Task> {
	private final Collection<String> conditionNames;

	public TaskComparator(Collection<String> conditionNames) {
		this.conditionNames = conditionNames;
	}

	@Override
	public int compare(Task o1, Task o2) {
		if (o1.isWarmingUp() && !o2.isWarmingUp())
			return -1;

		if (!o1.isWarmingUp() && o2.isWarmingUp())
			return 1;

		if (o1.getScenario() != o2.getScenario()) {
			// preserve natural order of scenarios
		}


		for (String conditionName: conditionNames) {
			Integer v1 = o1.getParameterValues().get(conditionName);
			Integer v2 = o2.getParameterValues().get(conditionName);

			if (!v1.equals(v2))
				return v1.compareTo(v2);
		}

		return 0;
	}
}
