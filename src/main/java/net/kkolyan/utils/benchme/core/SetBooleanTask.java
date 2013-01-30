package net.kkolyan.utils.benchme.core;

import java.util.concurrent.atomic.AtomicBoolean;

class SetBooleanTask implements Runnable {
	private AtomicBoolean var;
	private boolean value;

	SetBooleanTask(AtomicBoolean var, boolean value) {
		this.var = var;
		this.value = value;
	}

	@Override
	public void run() {
		var.set(value);
	}
}
