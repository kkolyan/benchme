package net.kkolyan.utils.benchme.examples;

import net.kkolyan.utils.benchme.api.Parameter;
import net.kkolyan.utils.benchme.api.Measurement;
import net.kkolyan.utils.benchme.api.Parameters;
import net.kkolyan.utils.benchme.api.SignalType;
import net.kkolyan.utils.benchme.api.Title;
import net.kkolyan.utils.benchme.api.View;
import net.kkolyan.utils.benchme.api.Views;
import net.kkolyan.utils.benchme.api.Scenario;
import net.kkolyan.utils.benchme.api.Sensor;
import net.kkolyan.utils.benchme.api.Signal;
import net.kkolyan.utils.benchme.api.WarmingUp;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Signal(name = "Rate", index = 0, type = SignalType.EVENT_FREQUENCY)
@Parameters({
	@Parameter(name = "Map Size", values = {200, 500000}),
	@Parameter(name = "Iteration", values = {1, 2, 3, 4, 5})
})
@WarmingUp(duration = 0, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(duration = 50, timeUnit = TimeUnit.MILLISECONDS)
@Views({
	@View(displayName = "Tables by scenario",
			rowsBy = "Iteration", tablesBy = Parameter.SCENARIO, columnsBy = "Map Size", cellKey = "Rate"),
	@View(displayName = "Tables by map size",
			rowsBy = "Iteration", tablesBy = "Map Size", columnsBy = Parameter.SCENARIO, cellKey = "Rate")
})
@Title("Read rate comparison of java.util.Map implementations")
public class JdkMapReadExample {

    //===============================================================================================================

    public Scenario hashMap() throws IOException {
		return new MapReadScenario(HashMap.class);
    }

    //===============================================================================================================

    public Scenario treeMap() throws IOException {
		return new MapReadScenario(TreeMap.class);
    }

    //===============================================================================================================

    public Scenario concurrentHashMap() throws IOException {
		return new MapReadScenario(ConcurrentHashMap.class);
    }

    //===============================================================================================================

    public Scenario linkedHashMap() throws IOException {
		return new MapReadScenario(LinkedHashMap.class);
    }

    //===============================================================================================================

    public static final int KEY_K = 10;

    //===============================================================================================================

	private class MapReadScenario extends Scenario {
		private Map<Object,Object> map;
		private Class<Map<Object,Object>> mapClass;
		private long sleep = 10;
    	private int mapSize;
		private int keyOffset;

		@SuppressWarnings({"unchecked"})
		public MapReadScenario(Class mapClass) {
			this.mapClass = mapClass;
		}

		@Override
		public void beforeScenario(Map<String, Integer> conditionValues) throws Exception {
			map = mapClass.newInstance();
			mapSize = conditionValues.get("Map Size");
			keyOffset = mapSize;

			for (int i = 0; i < mapSize; i += 1) {
				map.put(keyOffset + i * KEY_K, new Object());
			}
		}

		@Override
		public void doSingleOperation(Random random, Sensor sensor) throws Exception {
			boolean hit = random.nextBoolean();
			Integer key;
			if (hit) {
				key = keyOffset + KEY_K * random.nextInt(mapSize);
			} else {
				key = keyOffset + KEY_K * random.nextInt(mapSize) + 5;
			}
			Object value = map.get(key);
			if (value == null && hit) {
				throw new IllegalStateException();
			}
			if (value != null && !hit) {
				throw new IllegalStateException();
			}
			sensor.onEventSignal(0);
//			Thread.sleep(sleep);
		}

		@Override
		public String getName() {
			return mapClass.getName();
		}
	}

    //===============================================================================================================
}
