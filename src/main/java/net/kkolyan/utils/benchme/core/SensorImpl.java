package net.kkolyan.utils.benchme.core;

import java.util.concurrent.atomic.AtomicLong;

class SensorImpl implements BaredSensor {
	private AtomicLong[] counters;

	SensorImpl(int maxIndex) {
		AtomicLong[] counters = new AtomicLong[maxIndex + 1];
		for (int i = 0; i < counters.length; i ++)
			counters[i] = new AtomicLong();
		this.counters = counters;
	}

	public long getCounter(int index) {
		return counters[index].get();
	}

	@Override
	public void onEventSignal(int signalIndex) {
		counters[signalIndex].incrementAndGet();
	}
}
