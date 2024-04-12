/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package loginsystem;

/**
 *
 * @author hhwl
 */
public class Sorting {

	public static void insertionSort(Comparable[] list) {
		for (int i = 1; i < list.length; i++) {
			Comparable element = list[i];
			int swapIndex = 0;
			// go back to find first smaller element, swap to one position greater than it
			for (int j = i - 1; j >= 0; j--) {
				if (list[j].compareTo(list[i]) < 0) {
					swapIndex = j + 1;
					break;
				}
			}

			// shift everything (using arraycopy bc netbeans told me to)
			System.arraycopy(list, swapIndex, list, swapIndex + 1, i - swapIndex);
			list[swapIndex] = element;
		}
	}

	// testing
	public static void main(String[] args) {
		Comparable[] arr = new Comparable[]{3, 1, 2, 4, 6, 9, 2, 6, 3, 7};
		insertionSort(arr);
		for (Comparable e : arr) {
			System.out.print(e + " ");
		}
		System.out.println();
	}
}
