package net.kkolyan.utils.benchme.core;

import net.kkolyan.utils.benchme.api.Defaults;
import net.kkolyan.utils.benchme.api.Measurement;
import net.kkolyan.utils.benchme.api.Title;
import net.kkolyan.utils.benchme.api.View;
import net.kkolyan.utils.benchme.api.Views;
import net.kkolyan.utils.benchme.api.Signal;
import net.kkolyan.utils.benchme.api.Signals;
import net.kkolyan.utils.benchme.api.WarmingUp;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({"ClassExplicitlyAnnotation"})
public class SuiteConfig implements Serializable {

	//==========================================================================

	private static final long serialVersionUID = 1L;

	//==========================================================================
	
	private WarmingUp warmingUp;
	private Measurement measurement;
	private List<View> reportViews = new ArrayList<View>();
	private List<Signal> signals = new ArrayList<Signal>();
	private Class<?> suiteClass;
	private int maxSignalIndex;
	private Title title;

	//==========================================================================

	public SuiteConfig(Class<?> suiteClass) {
		this.suiteClass = suiteClass;

		warmingUp = suiteClass.getAnnotation(WarmingUp.class);
		measurement = suiteClass.getAnnotation(Measurement.class);
		title = suiteClass.getAnnotation(Title.class);

		// default implementations

		if (warmingUp == null)
			warmingUp = new DefaultWarmingUp();

		if (title == null)
			title = new DefaultTitle();

		if (measurement == null)
			measurement = new DefaultMeasurement();

		// list annotations

		if (suiteClass.isAnnotationPresent(View.class))
			reportViews.add(suiteClass.getAnnotation(View.class));

		if (suiteClass.isAnnotationPresent(Views.class))
			reportViews.addAll(Arrays.asList(suiteClass.getAnnotation(Views.class).value()));

		if (suiteClass.isAnnotationPresent(Signal.class))
			signals.add(suiteClass.getAnnotation(Signal.class));

		if (suiteClass.isAnnotationPresent(Signals.class))
			signals.addAll(Arrays.asList(suiteClass.getAnnotation(Signals.class).value()));

		maxSignalIndex = Collections.max(signals, new SignalIndexComparator()).index();
	}

	//==========================================================================

	private static class SignalIndexComparator implements Comparator<Signal> {
		@Override
		public int compare(Signal o1, Signal o2) {
			return o1.index() - o2.index();
		}
	}

	//==========================================================================

	public WarmingUp getWarmingUp() {
		return warmingUp;
	}

	public Measurement getMeasurement() {
		return measurement;
	}

	public List<View> getReportViews() {
		return reportViews;
	}

	public List<Signal> getSignals() {
		return signals;
	}

	public Class<?> getSuiteClass() {
		return suiteClass;
	}

	public int getMaxSignalIndex() {
		return maxSignalIndex;
	}

	public Title getTitle() {
		return title;
	}

	//==========================================================================

	private class DefaultWarmingUp implements WarmingUp, Serializable {
		@Override
		public long duration() {
			return Defaults.WARMING_UP_DURATION;
		}

		@Override
		public TimeUnit timeUnit() {
			return Defaults.TIME_UNIT;
		}

		@Override
		public Class<? extends Annotation> annotationType() {
			return WarmingUp.class;
		}
	}

	//==========================================================================

	private class DefaultTitle implements Title, Serializable {

		@Override
		public String value() {
			return suiteClass.getName();
		}

		@Override
		public Class<? extends Annotation> annotationType() {
			return Title.class;
		}
	}

	//==========================================================================

	private class DefaultMeasurement implements Measurement, Serializable {

		@Override
		public long duration() {
			return Defaults.MEASUREMENT_DURATION;
		}

		@Override
		public TimeUnit timeUnit() {
			return Defaults.TIME_UNIT;
		}

		@Override
		public Class<? extends Annotation> annotationType() {
			return Measurement.class;
		}
	}

	//==========================================================================
}
