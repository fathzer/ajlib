package com.fathzer.soft.ajlib.utilities;

import java.util.List;

/** Utilities dealing with lists.
 */
public abstract class ListUtils {

	private ListUtils() {
		super();
	}

	/** Moves some indexes of a list up or down.
	 * @param <T> The type of list elements
	 * @param list The list where to move the elements
	 * @param indexes The indexes of the elements to move (should be sorted in ascending order).
	 * @param offset the offset of the elements in the final list (example 1 to move elements 1 position down, -1 for one position up).
	 */
	public static <T> void move(List<T> list, int[] indexes, int offset) {
		if (offset>0) {
			for (int i = indexes.length-1; i >= 0 ; i--) {
				move(list,indexes[i],offset);
			}
		} else if (offset<0) {
			for (int i : indexes) {
				move(list,i,offset);
			}
		}
	}

	/** Moves a list element with a specified offset.
	 * @param <T> The type of list elements
	 * @param list The list where to move the elements
	 * @param index The index of the element to move.
	 * @param offset the offset of the elements in the final list (example 1 to move elements 1 position down, -1 for one position up).
	 */
	public static <T> void move(List<T> list, int index, int offset) {
		T element = list.get(index);
		if (offset<0) {
			for (int i = index; i > index+offset; i--) {
				list.set(i, list.get(i-1));
			}
		} else if (offset>0) {
			for (int i = index; i < index+offset; i++) {
				list.set(i, list.get(i+1));
			}
		}
		list.set(index+offset, element);
	}
}
