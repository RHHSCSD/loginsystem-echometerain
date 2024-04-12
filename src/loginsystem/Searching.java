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

	public static int binarySearch(Comparable term, Comparable[] list) {
		int lo = 0;
		int hi = list.length - 1;
		while (lo <= hi) {
			int mid = (lo + hi) >> 1;
			if (list[mid].equals(term)) {
				return mid;
			} else if (term.compareTo(list[mid]) > 0) {
				lo = mid + 1;
			} else {
				hi = mid - 1;
			}
		}
		return -1;
	}

	public static int seqSearch(Comparable term, Comparable[] list) {
		for (int i = 0; i < list.length; i++) {
			if (list[i].equals(term)) {
				return i;
			}
		}
		return -1;
	}

	public static void main(String[] args) {
		Comparable[] arr = new Comparable[]{1, 3, 5, 6, 8, 9, 23};
		System.out.println(binarySearch(9, arr));
		System.out.println(seqSearch(9, arr));
	}
}
