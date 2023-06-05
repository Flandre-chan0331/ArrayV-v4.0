package sorts.hybrid;

import sorts.templates.Sort;
import sorts.insert.InsertionSort;

import main.ArrayVisualizer;

/*
 * 
MIT License

Copyright (c) 2021 aphitorite

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 *
 */

final public class MedianHeapSort extends Sort {
    public MedianHeapSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Median Heap");
        this.setRunAllSortsName("Median Heapsort");
        this.setRunSortName("Median Heapsort");
        this.setCategory("Hybrid Sorts");
        this.setComparisonBased(true);
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
	
	private InsertionSort insSort;
	
	private void medianOfThree(int[] array, int a, int b) {
		int m = a+(--b-a)/2;
		
		if(Reads.compareIndices(array, a, m, 1, true) == 1)
			Writes.swap(array, a, m, 1, true, false);
		
		if(Reads.compareIndices(array, m, b, 1, true) == 1) {
			Writes.swap(array, m, b, 1, true, false);
			
			if(Reads.compareIndices(array, a, m, 1, true) == 1)
				return;
		}
		
		Writes.swap(array, a, m, 1, true, false);
	}
	
	//lite version
	private void medianOfMedians(int[] array, int a, int b, int s) {
		int end = b, start = a, i, j;
		boolean ad = true;
		
		while(end - start > 1) {
			j = start;
			Highlights.markArray(2, j);
			for(i = start; i+2*s <= end; i+=s) {
				this.insSort.customInsertSort(array, i, i+s, 0.25, false);
				Writes.swap(array, j++, i+s/2, 1, false, false);
				Highlights.markArray(2, j);
			}
			if(i < end) {
				this.insSort.customInsertSort(array, i, end, 0.25, false);
				Writes.swap(array, j++, i+(end-(ad ? 1 : 0)-i)/2, 1, false, false);
				Highlights.markArray(2, j);
				if((end-i)%2 == 0) ad = !ad;
			}
			end = j;
		}
	}
	
	public int partition(int[] array, int a, int b, int p) {
        int i = a - 1;
        int j = b;
		Highlights.markArray(3, p);
		
        while(true) {
			do {
				i++;
                Highlights.markArray(1, i);
                Delays.sleep(0.5);
			}
			while(i < j && Reads.compareIndices(array, i, p, 0, false) == 1);
			
			do {
				j--;
                Highlights.markArray(2, j);
                Delays.sleep(0.5);
			}
            while(j >= i && Reads.compareIndices(array, j, p, 0, false) == -1);
				
            if(i < j) Writes.swap(array, i, j, 1, true, false);
            else      return j;
        }
    }
	
	private void siftDown(int[] array, boolean max, int p, int n, int r) {
		int t = array[p+r];
		int cmp = max ? 1 : -1;
		
		while(2*r+2 < n) {
			int nxt = p+2*r+1;
			nxt = (Reads.compareValues(array[nxt+1], array[nxt]) == cmp) ? nxt+1 : nxt;
			
			if(Reads.compareValues(array[nxt], t) == cmp) {
				Writes.write(array, p+r, array[nxt], 0.5, true, false);
				r = nxt-p;
			}
			else {
				Writes.write(array, p+r, t, 0.5, true, false);
				return;
			}
		}
		int nxt = p+2*r+1;
			
		if(nxt-p < n && Reads.compareValues(array[nxt], t) == cmp) {
			Writes.write(array, p+r, array[nxt], 0.5, true, false);
			r = nxt-p;
		}
		Writes.write(array, p+r, t, 0.5, true, false);
	}
	
	private void siftDownEasy(int[] array, boolean max, int p, int n, int t) {
		int r = 0;
		int cmp = max ? 1 : -1;
		
		while(2*r+2 < n) {
			int nxt = p+2*r+1;
			nxt = (Reads.compareValues(array[nxt+1], array[nxt]) == cmp) ? nxt+1 : nxt;
			
			Writes.write(array, p+r, array[nxt], 0.5, true, false);
			r = nxt-p;
		}
		int nxt = p+2*r+1;
			
		if(nxt-p < n) {
			Writes.write(array, p+r, array[nxt], 0.5, true, false);
			r = nxt-p;
		}
		Writes.write(array, p+r, t, 0.5, true, false);
	}
	
	private void heapSort(int[] array, boolean max, int a, int b, int p) {
		int n = b-a;
		
		for(int i = (n-1)/2; i >= 0; i--)
			this.siftDown(array, max, a, n, i);
		
		if(max) {
			for(int i = 0; i < n; i++) {
				int t = array[--p];
				Highlights.markArray(2, p);
				Writes.write(array, p, array[a], 1, false, false);
				this.siftDownEasy(array, max, a, n, t);
			}
		}
		else {
			for(int i = 0; i < n; i++) {
				int t = array[p];
				Highlights.markArray(2, p);
				Writes.write(array, p++, array[a], 1, false, false);
				this.siftDownEasy(array, max, a, n, t);
			}
		}
	}
	
	private void medianHeapSort(int[] array, int a, int b) {
		int start = a, end = b;
		boolean badPartition = false, mom = false;
		
		while(end-start > 16) {
			if(badPartition) {
				this.medianOfMedians(array, start, end, 5);
				mom = true;
			}
			else this.medianOfThree(array, start, end);
			
			int p = this.partition(array, start+1, end, start);
			Writes.swap(array, start, p, 1, true, false);
			Highlights.clearAllMarks();
			
			int left  = p-start;
			int right = end-(p+1);
			badPartition = !mom && ((left == 0 || right == 0) || (left/right >= 16 || right/left >= 16));
			
			if(left <= right) {
				this.heapSort(array, true, start, p, end);
				end -= left;
				Writes.swap(array, --end, p, 1, true, false);
			}
			else {
				this.heapSort(array, false, p+1, end, start);
				start += right;
				Writes.swap(array, start++, p, 1, true, false);
			}
		}
		this.insSort.customInsertSort(array, start, end, 0.25, false);
	}
    
    @Override
    public void runSort(int[] array, int length, int bucketCount) {
		this.insSort = new InsertionSort(this.arrayVisualizer);
		this.medianHeapSort(array, 0, length);
    }
}