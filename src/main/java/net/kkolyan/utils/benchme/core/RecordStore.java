package net.kkolyan.utils.benchme.core;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface RecordStore {
	void addRecord(Map<String, String> result) throws Exception;
	void finishRecording() throws Exception;
	List<Map<String,String>> getRecords();
}
