package net.kkolyan.utils.benchme.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MemoryRecordStore implements RecordStore {
	private List<Map<String, String>> list = new ArrayList<Map<String, String>>();
	private volatile boolean finished;

	@Override
	public void addRecord(Map<String, String> result) {
		if (finished)
			throw new IllegalStateException("already finished");
		
		list.add(result);
	}

	@Override
	public void finishRecording() {
		finished = true;
	}

	@Override
	public List<Map<String, String>> getRecords() {
		if (!finished)
			throw new IllegalStateException("not finished yet");

		return Collections.unmodifiableList(list);
	}
}
