/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package loginsystem;

/**
 *
 * @author hhwl
 */
public class Searching {

	public static int binarySearch(String term, String[] list) {
		int lo = 0;
		int hi = list.length;
		while (lo != hi) {
			int mid = (lo + hi) >> 1;
			if (list[mid].equals(term)) {
				return mid;
			} else if (term.compareTo(list[mid]) > 1) {
				lo = mid + 1;
			} else {
				hi = mid - 1;
			}
		}
		return -1;
	}

	public static int seqSearch(String term, String[] list) {
		for (int i = 0; i < list.length; i++) {
			if (list[i].equals(term)) {
				return i;
			}
		}
		return -1;
	}
}
