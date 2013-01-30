package net.kkolyan.utils.benchme.core;

import net.kkolyan.utils.benchme.api.Sensor;

interface BaredSensor extends Sensor {
	long getCounter(int index);
}
