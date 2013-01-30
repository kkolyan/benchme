package net.kkolyan.utils.benchme.api;

import java.util.concurrent.TimeUnit;

public final class Defaults {
	public static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
	public static final long MEASUREMENT_DURATION = 30;
	public static final long WARMING_UP_DURATION = 0;
	public static final String ENCODING = "UTF-8";
	public static final String REPORT_STYLES_CLASSPATH_LOCATION = "report.css";

	private Defaults() {
	}
}
