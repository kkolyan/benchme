package net.kkolyan.utils.benchme.core;

import net.kkolyan.utils.benchme.api.Defaults;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class FileSystemRecordStore implements RecordStore {
	
	private Writer writer;
	private List<Map<String, String>> records = new ArrayList<Map<String, String>>();
	private File file;
	private boolean finished;

	private FileSystemRecordStore() {
	}

	public static FileSystemRecordStore createBlank(File file) throws FileNotFoundException, UnsupportedEncodingException {
		FileSystemRecordStore store = new FileSystemRecordStore();
		store.file = file;
		//noinspection ResultOfMethodCallIgnored
		file.getParentFile().mkdirs();

		store.writer = new PrintWriter(file, Defaults.ENCODING);
		return store;
	}

	public static FileSystemRecordStore loadFinished(File file) throws FileNotFoundException, UnsupportedEncodingException {
		FileSystemRecordStore store = new FileSystemRecordStore();
		store.file = file;

		if (file.exists())
			store.loadFromDisk();
		
		store.finished = true;
		return store;
	}

	@Override
	public void addRecord(Map<String, String> result) throws Exception {
		if (finished)
			throw new IllegalStateException("already finished");

		for (Map.Entry<String, String> entry: result.entrySet()) {
			String key = escape(entry.getKey());
			String value = escape(entry.getValue());
			writer.append(key).append(": ").append(value).append(", ");
		}
		writer.append(System.getProperty("line.separator"));
		writer.flush();
	}

	private String escape(String s) {
		if (s.contains("\n")) {
			throw new IllegalStateException("line breaks is not supported! string: ["+s+"]");
		}
		s = s.replace(":", "\\:");
		s = s.replace(",", "\\,");
		return s;
	}

	private String unEscape(String s) {
		s = s.replace("\\:", ":");
		s = s.replace("\\,", ",");
		return s;
	}

	@Override
	public void finishRecording() throws Exception {
		writer.flush();
		writer.close();
		loadFromDisk();
		finished = true;
	}

	private static List<String> splitBy(String s, char delimiter, List<String> list) {
		StringBuilder sb = new StringBuilder();
		char last = '\0';
		for (int i = 0; i < s.length(); i ++) {
			char ch = s.charAt(i);
			if (ch == delimiter && last != '\\') {
				list.add(sb.toString());
				sb = new StringBuilder();
			} else {
				sb.append(ch);
			}
			last = ch;
		}
		list.add(sb.toString());
		return list;
	}

	public static void main(String[] args) {
		String s = "jf240tk:f5qgte;d25gtr:d24tgrt;";
		List<String> list = new ArrayList<String>();
		splitBy(s, ';', list);
		System.out.println(list);
	}

	private void loadFromDisk() throws FileNotFoundException {
		Scanner scanner = new Scanner(file, Defaults.ENCODING);
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			line = line.trim();

			if (line.isEmpty())
				continue;

			List<String> entryList = splitBy(line, ',', new ArrayList<String>());
			Map<String,String> record = new LinkedHashMap<String, String>();

			for (String entry: entryList) {
				entry = entry.trim();

				if (entry.isEmpty())
					continue;

				List<String> kv = splitBy(entry, ':', new ArrayList<String>(2));
				if (kv.size() != 2)
					throw new IllegalStateException(entryList+"");

				String key = kv.get(0).trim();
				String value = kv.get(1).trim();
                key = unEscape(key);
                value = unEscape(value);
				record.put(key, value);
			}

			records.add(record);
		}
	}

	@Override
	public List<Map<String, String>> getRecords() {
		if (!finished)
			throw new IllegalStateException("not finished yet");
		
		return Collections.unmodifiableList(records);
	}
}
