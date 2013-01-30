package net.kkolyan.utils.benchme;

import net.kkolyan.utils.benchme.core.FileSystemRecordStore;
import net.kkolyan.utils.benchme.core.RecordStore;
import net.kkolyan.utils.benchme.core.ResultProcessor;
import net.kkolyan.utils.benchme.core.SuiteConfig;
import net.kkolyan.utils.benchme.core.SuiteContext;
import net.kkolyan.utils.benchme.core.Task;
import net.kkolyan.utils.benchme.core.TaskListFactory;

import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

//TODO remove out system "Iterations" mechanism
public class Launcher {

	public static final String RESULT_FILE_APPEND_DATE_PROPERTY = "result.filename.appendDate";

	public static void main(String[] args) throws Exception {
		if (args.length < 1)
			throw new IllegalArgumentException("usage: benchme %className%");

		Class<?> suite = Class.forName(args[0]);

		File reportFolder = new File("reports");

		String fileNameBase;
		if (System.getProperty(RESULT_FILE_APPEND_DATE_PROPERTY) != null) {
			fileNameBase = suite.getSimpleName() + " " + new SimpleDateFormat("dd.MM.yyyy_HH-mm-ss").format(new Date());
		} else {
			fileNameBase = suite.getSimpleName();
		}

		File htmlReport = 		new File(reportFolder, fileNameBase+".report.html").getAbsoluteFile();
		File resultHistory = 	new File(reportFolder, fileNameBase+".report.csv").getAbsoluteFile();
		File configTemp = 		new File(reportFolder, fileNameBase+".config.tmp").getAbsoluteFile();

		if (configTemp.exists()) {
			recoverTemp(htmlReport, resultHistory, configTemp);

		} else {
			doBenchmark(suite, htmlReport, resultHistory, configTemp);
		}
	}

	private static void recoverTemp(File htmlReport, File resultHistory, File configTemp) throws Exception {
		System.out.println("temp data from last failed benchmark found. trying to generate report. " +
				"please remove it manually later ("+configTemp+")");
		SuiteConfig config;
		try {
			ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(configTemp)));
			config = (SuiteConfig) ois.readObject();
			if (config == null) {
				throw new NullPointerException("something is wrong... temp file contains no data");
			}
			ois.close();
			
			RecordStore resultStore = FileSystemRecordStore.loadFinished(resultHistory);
			generateReport(htmlReport, config, resultStore);
			System.out.println("see generated report at: "+htmlReport);

			try {
				Desktop.getDesktop().browse(htmlReport.toURI());
			} catch (Exception e) {
				// desktop is not supported on current JVM or something else... don't care
			}
			
		} catch (Exception e) {
			System.out.println("unable to read temp data of last failed benchmark ("+e+"). please remove it manually ("+configTemp+")");
		}

	}

	private static void doBenchmark(Class<?> suite, File htmlReport, File resultHistory, File configTemp) throws Exception {

		RecordStore resultStore = FileSystemRecordStore.createBlank(resultHistory);

		final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

		try {

			SuiteConfig config = new SuiteConfig(suite);
			ObjectOutputStream oos = new ObjectOutputStream(new PrintStream(configTemp));
			oos.writeObject(config);
			oos.close();
			SuiteContext context = new SuiteContext(config, executor, resultStore);

            if (context.getScenarios().isEmpty()) {
                System.out.println("no scenarios found in "+suite.getName());
                removeWithWarning(configTemp);
                return;
            }

			final List<Task> taskList = new ArrayList<Task>();
			TaskListFactory.run(context, taskList);

			for (Task task: taskList) {
				task.run();
			}

			resultStore.finishRecording();

			generateReport(htmlReport, config, resultStore);

            removeWithWarning(configTemp);

		} finally {
			executor.shutdownNow();
		}

		System.out.println("benchmark complete. see generated report at: "+htmlReport);

		try {
			Desktop.getDesktop().browse(htmlReport.toURI());
		} catch (Exception e) {
			// desktop is not supported on current JVM or something else... don't care
		}
	}

    private static void removeWithWarning(File file) {
        boolean tempConfigRemoved = file.delete();
        if (!tempConfigRemoved)
            System.out.println("unable to remove temporary file " + file +
                    ", please remove it manually, or setup JVM permissions to enable it");
    }

	private static void generateReport(File file, SuiteConfig config, RecordStore resultStore) throws Exception {
		ResultProcessor resultProcessor = new ResultProcessor();
		resultProcessor.setFile(file);
		resultProcessor.setConfig(config);
		resultProcessor.setResultStore(resultStore);
		resultProcessor.run();
	}
}
