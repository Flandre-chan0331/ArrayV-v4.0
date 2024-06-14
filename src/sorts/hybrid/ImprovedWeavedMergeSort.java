package sorts.hybrid;

import sorts.templates.Sort;
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

final public class ImprovedWeavedMergeSort extends Sort {
    public ImprovedWeavedMergeSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        
        this.setSortListName("Improved Weaved Merge");
        this.setRunAllSortsName("Improved Weaved Merge Sort");
        this.setRunSortName("Improved Weaved Mergesort");
        this.setCategory("Hybrid Sorts");
        this.setComparisonBased(true);
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
    
	private void merge(int[] array, int[] tmp, int a, int b, int g) {
		int p0 = 0, p1 = 0, g2 = 2*g;
		
		while(a+g < b) {
			if(Reads.compareIndices(array, a, a+g, 1, true) > 0) {
				Highlights.markArray(3, p0);
				Highlights.markArray(4, p1);
				
				int i = a+g;
				
				Highlights.markArray(2, i);
				Writes.write(tmp, p1++, array[a], 0.5, false, true);
				Highlights.markArray(4, p1);
				Writes.write(array, a, array[i], 1, true, false);
				
				i += g2;
				a += g;
				
				do {
					while(i < b && Reads.compareValues(array[i], tmp[p0]) < 0) {
						Highlights.markArray(2, i);
						Writes.write(tmp, p1++, array[i-g], 0.5, false, true);
						Highlights.markArray(4, p1);
						Writes.write(array, a, array[i], 1, true, false);
						
						i += g2;
						a += g;
					}
					Writes.write(array, a, tmp[p0++], 1, false, false);
					Highlights.markArray(3, p0);
					
					a += g;
				}
				while(p0 < p1);
				
				p0 = p1 = 0;
				
				Highlights.clearMark(3);
				Highlights.clearMark(4);
			}
			else a += g;
		}
	}
	
    @Override
    public void runSort(int[] array, int length, int bucketCount) {
		int[] tmp = Writes.createExternalArray(length/2);
		 
		int g = 1;
		while(g < (length+3)/4) g *= 2;
		
		for(int j = g; j < length; j++) {
			if(Reads.compareIndices(array, j-g, j, 0.5, true) > 0) {
				Highlights.clearMark(2);
				
				int t = array[j];
				int i = j;
			  
				do {
					Writes.write(array, i, array[i-g], 0.5, true, false);
					i -= g;
				}
				while(i-g >= 0 && Reads.compareValues(array[i-g], t) > 0);
			  
				Writes.write(array, i, t, 0.5, true, false);
			}
		}
		for(g /= 2; g > 0; g /= 2)
			for(int i = 0; i < g; i++)
				this.merge(array, tmp, i, length, g);
		
		Writes.deleteExternalArray(tmp);
    }
}