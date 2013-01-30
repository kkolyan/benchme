package net.kkolyan.utils.benchme.examples;

import net.kkolyan.utils.benchme.api.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Parameters({
    @Parameter(name = "Iteration", values = {1,2,3,4,5,6,7,8,9,10}, warmUp = {1}),
    @Parameter(name = "Fake input pool size", values = {1000})
})
@Signals({
		@Signal(name = "Rate", index = 0, type = SignalType.EVENT_FREQUENCY)
})
@WarmingUp(duration = 0, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(duration = 30, timeUnit = TimeUnit.MILLISECONDS)
@View(
		displayName = "SerializationDirectVsHashMap",
		rowsBy = "Iteration",
		columnsBy = Parameter.SCENARIO,
		cellKey = "Rate"
)
public class SerializationDirectVsHashMap {

    //===============================================================================================================

    public Scenario writeViaHashMap() throws IOException {
        return new WriteScenario() {
            @Override
            public void doSingleOperation(Random random, Sensor sensor) throws Exception {
                ObjectOutputStream stream = new ObjectOutputStream(new DummyOutputStream());
                TestEntity entity = new TestEntity();
                entity.generate(random);
                Map<String,Object> map = new HashMap<String,Object>();
                map.put("1", entity.getField1());
                map.put("2", entity.getField2());
                map.put("3", entity.getField3());
                map.put("4", entity.getField4());
                stream.writeObject(map);
                stream.flush();
                sensor.onEventSignal(0);
            }

            @Override
            public String getName() {
                return "Write via hashmap";
            }
        };
    }

    //===============================================================================================================

    public Scenario writeDirectly() throws IOException {
        return new WriteScenario() {
            
            @Override
            public void doSingleOperation(Random random, Sensor sensor) throws Exception {
                ObjectOutputStream stream = new ObjectOutputStream(new DummyOutputStream());
                TestEntity entity = new TestEntity();
                entity.generate(random);
                stream.writeObject(entity);
                stream.flush();
                sensor.onEventSignal(0);
            }

            @Override
            public String getName() {
                return "Write directly";
            }
        };
    }

    //===============================================================================================================

    public Scenario readDirectly() {
        return new ReadScenario() {
            @Override
            Object generateObject(Random random) {
                TestEntity testEntity = new TestEntity();
                testEntity.generate(random);
                return testEntity;
            }

            @Override
            Object parse(Object read) {
                TestEntity entity = (TestEntity) read;
                Map<String,Object> map = new HashMap<String,Object>();
                map.put("1", entity.getField1());
                map.put("2", entity.getField2());
                map.put("3", entity.getField3());
                map.put("4", entity.getField4());
                return map;
            }

            @Override
            public String getName() {
                return "Read Directly";
            }
        };
    }

    //===============================================================================================================

    public Scenario readViaHashMap() {
        return new ReadScenario() {
            @Override
            Object generateObject(Random random) {
                TestEntity entity = new TestEntity();
                entity.generate(random);
                Map<String,Object> map = new HashMap<String,Object>();
                map.put("1", entity.getField1());
                map.put("2", entity.getField2());
                map.put("3", entity.getField3());
                map.put("4", entity.getField4());
                return map;
            }

            @Override
            Object parse(Object read) {
                Map<String,Object> map = (Map<String, Object>) read;
                TestEntity entity = new TestEntity();
                entity.setField1((Integer) map.get("1"));
                entity.setField2((Long) map.get("2"));
                entity.setField3((String) map.get("3"));
                entity.setField4((Double) map.get("4"));
                return entity;
            }

            @Override
            public String getName() {
                return "Read via HashMap";
            }
        };
    }

    //===============================================================================================================

    static class DummyOutputStream extends OutputStream {
        @Override
        public void write(int b) throws IOException {
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
        }

        @Override
        public void write(byte[] b) throws IOException {
        }
    }

    static class TestEntity implements Serializable {
        private int field1;
        private long field2;
        private String field3;
        private double field4;

        public int getField1() {
            return field1;
        }

        public void generate(Random random) {
            field1 = random.nextInt();
            field2 = random.nextLong();
            field3 = random.nextLong()+"";
            field4 = random.nextDouble();
        }

        public void setField1(int field1) {
            this.field1 = field1;
        }

        public long getField2() {
            return field2;
        }

        public void setField2(long field2) {
            this.field2 = field2;
        }

        public String getField3() {
            return field3;
        }

        public void setField3(String field3) {
            this.field3 = field3;
        }

        public double getField4() {
            return field4;
        }

        public void setField4(double field4) {
            this.field4 = field4;
        }
    }

    private abstract static class WriteScenario extends Scenario {
    }

    private abstract static class ReadScenario extends Scenario {
        ByteArrayInputStream inputStream;

        @Override
        public void beforeScenario(Map<String, Integer> conditionValues) throws Exception {
            int poolSize = conditionValues.get("Fake input pool size");

            Random random = new Random();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            for (int i = 0; i < poolSize; i ++) {
                ObjectOutputStream outputStream = new ObjectOutputStream(baos);
                Object o = generateObject(random);
                outputStream.writeObject(o);
                outputStream.flush();
            }
            inputStream = new ByteArrayInputStream(baos.toByteArray());
        }

        abstract Object generateObject(Random random);

        abstract Object parse(Object read);

        @Override
        public void doSingleOperation(Random random, Sensor sensor) throws Exception {
            ObjectInputStream stream = new ObjectInputStream(inputStream);
            stream.readObject();
            if (inputStream.available() == 0) {
                inputStream.reset();
            }
            sensor.onEventSignal(0);
        }

        @Override
        public void afterScenario() throws Exception {
            inputStream = null;
        }
    }
}
