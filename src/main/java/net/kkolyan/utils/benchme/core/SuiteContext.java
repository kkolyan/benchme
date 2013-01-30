package net.kkolyan.utils.benchme.core;

import net.kkolyan.utils.benchme.api.Ignore;
import net.kkolyan.utils.benchme.api.Scenario;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;

public class SuiteContext {

	//==========================================================================

	private Random random;
	private RecordStore resultStore;
	private List<Scenario> scenarios = new ArrayList<Scenario>();
	private ScheduledExecutorService executor;
	private SuiteConfig config;

	//==========================================================================

	public SuiteContext(SuiteConfig config, ScheduledExecutorService executor, RecordStore resultStore) throws Exception {
		this.config = config;
		this.executor = executor;
		this.resultStore = resultStore;
		random = new Random();

        if (config.getSuiteClass().isAnnotationPresent(Ignore.class)) {
            System.out.println("ignoring scenario suite: \"" + config.getSuiteClass().getName() + "\"");
            return;
        }

		Object suite = config.getSuiteClass().newInstance();

		for (Method method: config.getSuiteClass().getDeclaredMethods()) {
			if (method.getParameterTypes().length != 0)
				continue;

			if (!Modifier.isPublic(method.getModifiers()))
				continue;

			if (!Scenario.class.isAssignableFrom(method.getReturnType()))
				continue;

            if (method.isAnnotationPresent(Ignore.class)) {
                System.out.println("ignoring scenario: \"" + method + "\"");
                continue;
            }

			Scenario scenario = (Scenario) method.invoke(suite);
			scenarios.add(scenario);
		}
	}

	public ScheduledExecutorService getExecutor() {
		return executor;
	}

    //==========================================================================

	public Random getRandom() {
		return random;
	}

	public RecordStore getResultStore() {
		return resultStore;
	}

	public List<Scenario> getScenarios() {
		return scenarios;
	}

	public SuiteConfig getConfig() {
		return config;
	}

	//==========================================================================
}
