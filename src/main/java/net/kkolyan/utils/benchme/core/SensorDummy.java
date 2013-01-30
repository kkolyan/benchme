package net.kkolyan.utils.benchme.core;

class SensorDummy implements BaredSensor {
	@Override
	public void onEventSignal(int signalIndex) {
			// nothing
	}

	@Override
	public long getCounter(int index) {
		throw new UnsupportedOperationException();
	}
}
