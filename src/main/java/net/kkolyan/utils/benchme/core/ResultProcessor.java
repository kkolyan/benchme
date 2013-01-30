package net.kkolyan.utils.benchme.core;

import net.kkolyan.utils.benchme.api.Defaults;
import net.kkolyan.utils.benchme.util.ReportGenerator;
import net.kkolyan.utils.benchme.util.TableSetGenerator;

import java.io.File;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResultProcessor {
	
	private File file;
	private RecordStore resultStore;
	private SuiteConfig config;

	//=======================================================================

	public void run() throws Exception {

		Map<String,String> contextMap = new LinkedHashMap<String, String>();

		contextMap.put("Measurement", String.format(
				"%s %s per case",
				config.getMeasurement().duration(),
				config.getMeasurement().timeUnit().toString().toLowerCase()));

		contextMap.put("Warming up", String.format(
				"%s %s per case",
				config.getWarmingUp().duration(),
				config.getWarmingUp().timeUnit().toString().toLowerCase()));

		contextMap.put("Total case count", resultStore.getRecords().size() +"");
		

		//noinspection ResultOfMethodCallIgnored
		file.getParentFile().mkdirs();
		
		Writer writer = new PrintWriter(file, Defaults.ENCODING);

		List<TableSetGenerator.Record> data = new ArrayList<TableSetGenerator.Record>();
		for (Map<String,String> result: resultStore.getRecords()) {
			data.add(new MapDataUnit(result));
		}
		ReportGenerator generator = new ReportGenerator();
		generator.setConfig(config);
		generator.setContextAttributes(contextMap);
		generator.setData(data);
		generator.setWriter(writer);
		generator.generate();

		writer.flush();
		writer.close();
	}


    //==========================================================================

	private static class MapDataUnit implements TableSetGenerator.Record {
		private Map<String,String> map;

		private MapDataUnit(Map<String, String> map) {
			this.map = map;
		}

		@Override
		public String getValue(String key) {
			if (key.isEmpty()) {
				return "";
			}
			String value = map.get(key);
			if (value == null) {
				return "#{"+key+"}";
			}
			return value;
		}
	}

	//=============================================================

	public void setFile(File file) {
		this.file = file;
	}

	public void setResultStore(RecordStore resultStore) {
		this.resultStore = resultStore;
	}

	public void setConfig(SuiteConfig config) {
		this.config = config;
	}
	
	//=============================================================
}
