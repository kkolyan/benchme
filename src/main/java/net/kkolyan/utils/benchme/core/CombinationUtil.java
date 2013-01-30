package net.kkolyan.utils.benchme.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CombinationUtil {
	public static void main(String[] args) {
		test(new int[][] {{12, 13, 14},{24, 25, 26},{67,68,69,70}});
		test(new int[][] {{12, 13},{24, 25, 26},{67,68,69,70}});
		test(new int[][] {{12, 13},{24, 25, 26},{67,68,69,70},{0,1,2,3,4,5}});

		testGeneric(new Object[][]{{12, 13, 14}, {24, 25, 26}, {67, 68, 69, 70}});
		testGeneric(new Object[][]{{12, 13}, {24, 25, 26}, {67, 68, 69, 70}});
		testGeneric(new Object[][]{{12, 13}, {24, 25, 26}, {67, 68, 69, 70}, {0, 1, 2, 3, 4, 5}});
	}

	//==============================================================================

	private static void test(int[][] input) {

		int length = 1;
		for (int[] inputRow: input) {
			length *= inputRow.length;
		}

		int[][] out = computeCombinations(input);

		Set<Key> set = new LinkedHashSet<Key>();
		for (int[] array : out) {
			if (array == null)
				throw new AssertionError();

			Key key = new Key(array);
			set.add(key);
		}

		for (Key key: set) {
			System.out.println(Arrays.toString(key.array));
		}

		if (set.size() != length)
			throw new AssertionError();

		System.out.println("===================================================");
	}

	private static <T> void testGeneric(T[][] input) {

		int length = 1;
		for (T[] inputRow: input) {
			length *= inputRow.length;
		}

		List<List<T>> inputList = new ArrayList<List<T>>();
		for (T[] inputRow: input) {
			List<T> list = Arrays.asList(inputRow);
			inputList.add(list);
		}

		List<List<T>> out = new ArrayList<List<T>>();

		computeUniqueCombinations(inputList, out);

		Set<ListKey<T>> set = new LinkedHashSet<ListKey<T>>();
		for (List<T> array : out) {
			if (array == null)
				throw new AssertionError();

			ListKey<T> key = new ListKey<T>(array);
			set.add(key);
		}

		for (ListKey<T> key: set) {
			System.out.println(key.array);
		}

		if (set.size() != length)
			throw new AssertionError();

		System.out.println("===================================================");
	}

	//==============================================================================

	private static int[][] computeCombinations(int[][] in) {
		int length = 1;
		for (int[] set: in)
			length *= set.length;

		int[][] out = new int[length][];

		//==============================================================================

		final int vectorSize = in.length;

		int[] mul = new int[vectorSize];
		Arrays.fill(mul, 1);
		for (int i = 0; i < vectorSize; i ++) {
			for (int j = i + 1; j < vectorSize; j ++) {
				mul[i] *= in[j].length;
			}
		}

		for (int vectorNumber = 0; vectorNumber < length; vectorNumber ++) {
			out[vectorNumber] = new int[vectorSize];
			for (int position = 0; position < vectorSize; position ++) {
				int index = (vectorNumber / mul[position])  % in[position].length;
				out[vectorNumber][position] = in[position][index];
			}
		}

		//==============================================================================
		return out;
	}

	//==============================================================================

	private static final class Key {
		final int[] array;

		private Key(int[] array) {
			this.array = array;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof Key)) return false;

			Key arrayKey = (Key) o;

			return Arrays.equals(array, arrayKey.array);
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(array);
		}
	}

	//==============================================================================

	private static final class ListKey<T> {
		final List<T> array;

		private ListKey(List<T> array) {
			this.array = array;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof ListKey)) return false;

			ListKey arrayKey = (ListKey) o;

			return Arrays.equals(array.toArray(), arrayKey.array.toArray());
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(array.toArray());
		}
	}

	//==============================================================================

	/**
	 *
	 * @param valueSets each element of this list is set of values of one dimension
	 * @param output each element of this list had to be an unique vector
	 */
	public static <T> void computeUniqueCombinations(List<List<T>> valueSets, List<List<T>> output) {
		int length = 1;
		for (List<T> valueSet: valueSets) {
			length *= valueSet.size();
		}

		int dimensionality = valueSets.size();

		int[] dividers = new int[valueSets.size()];

		Arrays.fill(dividers, 1);
		for (int i = 0; i < dimensionality; i ++) {
			for (int j = i + 1; j < dimensionality; j ++) {
				dividers[i] *= valueSets.get(j).size();
			}
		}

		for (int n = 0; n < length; n ++) {
			List<T> combination = new ArrayList<T>();
			for (int d = 0; d < dimensionality; d ++) {
				int index = (n / dividers[d]) % valueSets.get(d).size();
				T value = valueSets.get(d).get(index);
				combination.add(value);
			}
			output.add(combination);
		}
	}

	//==============================================================================
}
