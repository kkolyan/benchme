package net.kkolyan.utils.benchme.core;

import net.kkolyan.utils.benchme.api.Parameter;
import net.kkolyan.utils.benchme.api.Scenario;
import net.kkolyan.utils.benchme.api.Signal;
import net.kkolyan.utils.benchme.api.SignalType;
import net.kkolyan.utils.benchme.util.StringLengthUtil;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class Task {

	//=====================================================================

	private static final StringLengthUtil stringLengthUtil = new StringLengthUtil(40);
	private static final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss-dd.MM.yyyy");
	private static final NumberFormat numberFormat = NumberFormat.getNumberInstance();

	//=====================================================================

	private boolean warmingUp;
	private Scenario scenario;
	private final Map<String,Integer> parameterValues = new LinkedHashMap<String, Integer>();
	private SuiteContext context;

	//=====================================================================

	public Task() {
	}

	//=====================================================================

	public void run() throws Exception {

		final long duration;
		final TimeUnit timeUnit;
		final BaredSensor sensor;

		if (warmingUp) {
			duration = context.getConfig().getWarmingUp().duration();
			timeUnit = context.getConfig().getWarmingUp().timeUnit();
			sensor = new SensorDummy();
		} else {
			duration = context.getConfig().getMeasurement().duration();
			timeUnit = context.getConfig().getMeasurement().timeUnit();
			sensor = new SensorImpl(context.getConfig().getMaxSignalIndex());
		}

		final AtomicBoolean on = new AtomicBoolean();

        scenario.beforeScenario(parameterValues);


		//================================================================
        String till = dateFormat.format(new Date(System.currentTimeMillis() + duration));
        String now = dateFormat.format(new Date(System.currentTimeMillis()));
        System.out.print(now + stringLengthUtil.ensureLength("  " + this + "  ")+" waiting till "+till+"...");
		//================================================================

        on.set(true);
        context.getExecutor().schedule(new SetBooleanTask(on, false), duration, timeUnit);
		Random random = context.getRandom();
        while (on.get()) {
            scenario.doSingleOperation(random, sensor);
        }
        scenario.afterScenario();

		//================================================================
        System.out.println("done");
		//================================================================


		if (warmingUp)
			return;

		// Result computations

		Map<String,String> result = new LinkedHashMap<String, String>();

		// signals taking

		for (Signal signal: context.getConfig().getSignals()) {
			if (signal.type() == SignalType.EVENT_FREQUENCY) {
				long count = sensor.getCounter(signal.index());

				long intervalMilliseconds = context.getConfig().getMeasurement().timeUnit().toMillis(
						context.getConfig().getMeasurement().duration());

				double intervalSeconds =  ((double)intervalMilliseconds) / 1000.0;

				double rate = ((double)count) / intervalSeconds;
				String s = numberFormat.format(rate) + " op/s";
				result.put(signal.name(), s);
			} else {
				throw new IllegalArgumentException("unsupported signal type: "+signal.type());
			}
		}

		// input parameters taking

		for (Map.Entry<String, Integer> entry: parameterValues.entrySet()) {
			if (result.containsKey(entry.getKey()))
				throw new IllegalStateException("attempt to replace value for key: " + entry.getKey());

			result.put(entry.getKey(), entry.getValue()+"");
		}

		for (String reservedKey: Arrays.asList(Parameter.SCENARIO)) {
			if (result.containsKey(reservedKey))
				throw new IllegalStateException("key " + reservedKey + " is system built-in key.");
		}

		result.put(Parameter.SCENARIO, scenario.getName());

		context.getResultStore().addRecord(result);
	}

	//=====================================================================

	@Override
	public String toString() {
		return "Task{" +
				(warmingUp ? "warmingUp" : "measurement") +
				", parameterValues=" + parameterValues +
				", scenario.name=" + scenario.getName() +
				'}';
	}

	//=====================================================================

	public void setWarmingUp(boolean warmingUp) {
		this.warmingUp = warmingUp;
	}

	public void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}

	public void setContext(SuiteContext context) {
		this.context = context;
	}

	//=====================================================================

	public boolean isWarmingUp() {
		return warmingUp;
	}

	public Scenario getScenario() {
		return scenario;
	}

	public Map<String, Integer> getParameterValues() {
		return parameterValues;
	}
}
