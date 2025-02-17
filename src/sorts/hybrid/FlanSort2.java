package sorts.hybrid;

import java.util.Random;

import main.ArrayVisualizer;
import sorts.templates.Sort;

/*
 *
MIT License

Copyright (c) 2021-2024 aphitorite, edited by Flanlaina (a.k.a. Ayako-chan)

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

public final class FlanSort2 extends Sort {
	public FlanSort2(ArrayVisualizer arrayVisualizer) {
		super(arrayVisualizer);

		this.setSortListName("Flan II");
		this.setRunAllSortsName("Flan Sort");
		this.setRunSortName("Flansort");
		this.setCategory("Hybrid Sorts");
		this.setBucketSort(false);
		this.setRadixSort(false);
		this.setUnreasonablySlow(false);
		this.setUnreasonableLimit(0);
		this.setBogoSort(false);
	}
	
	/**
		ultimate sorting algorithm:
		
		unstable and probabilistic sorting algorithm performing an average of
		n log n + ~4.4 n comparisons and O(n) moves (~12.1 n) in O(1) space
		
		makes the fewest comparisons among the sorts of its kind
		achieves the ultimate goal of a n log n + O(n) comps in place O(n) moves sort
		
		implements a modified library sort where the original algorithm is proven to be O(n log n)
		from http://www.cs.sunysb.edu/~bender/newpub/BenderFaMo06-librarysort.pdf
		
		@author aphitorite
	*/
	
	private final int MIN_INSERT = 32;

	private final int G = 7; // gap size (power of 2 minus 1 is best)
	private final int R = 3; // rebalancing factor
	
	private Random rng;

	private void shiftBW(int[] array, int a, int m, int b) {
		while(m > a) Writes.swap(array, --b, --m, 1, true, false);
	}

	private int randGapSearch(int[] array, int a, int b, int val) {
		// specialized gap search for empty gaps with high probability
		// can handle lots of equal elements using randomization
		
		int s = G+1;
		int randCnt = 0;

		while(a < b) {
			int m = a+(((b-a)/s)/2)*s;
			Highlights.markArray(3, m);
			Delays.sleep(0.25);
			
			int cmp = Reads.compareValues(val, array[m]);
			
			if(cmp < 0)      b = m;
			else if(cmp > 0) a = m+s;
			
			else if(randCnt++ < 1) { // if we hit an equal element then pick a random gap within range
			                         // we can randomize a constant number of times but one seems enough
									 
				m = a+this.rng.nextInt((b-a)/s)*s;
				Highlights.markArray(3, m);
				Delays.sleep(0.25);
				
				cmp = Reads.compareValues(val, array[m]);
				
				if(cmp < 0)      b = m; // continue with binary search as usual with the random mid index
				else if(cmp > 0) a = m+s;
				else { a = m; break; } // if the random gap is equal again that means 
				                       // we successfully picked a random equal gap so we can just return m
			}
			else { a = m; break; } // if we already randomized enough and hit another equal gap: return m
		}
		Highlights.clearMark(3);
		return a;
	}

	private int rightBinSearch(int[] array, int a, int b, int val, boolean bw) {
		int cmp = bw ? 1 : -1;

		while(a < b) {
			int m = a+(b-a)/2;
			Highlights.markArray(3, m);
			Delays.sleep(0.25);

			if(Reads.compareValues(val, array[m]) == cmp)
				b = m;
			else
				a = m+1;
		}
		Highlights.clearMark(3);
		return a;
	}
	
	private void insertTo(int[] array, int tmp, int a, int b) {
		//Writes.swap(array, 0, 0, 0, false, false); // number of swaps helps track total data moves
		//Writes.changeWrites(-2);                   // comment out these two lines to remove this unnecessary self-swapping
		
		Highlights.clearMark(2);
		while(a > b) Writes.write(array, a, array[--a], 0.5, true, false);
		Writes.write(array, b, tmp, 0.5, true, false);
	}

	private void binaryInsertion(int[] array, int a, int b) {
		for(int i = a+1; i < b; i++)
			this.insertTo(array, array[i], i, this.rightBinSearch(array, a, i, array[i], false));
	}
	
	////////////////////
	//                //
	//  LIBRARY SORT  //
	//                //
	////////////////////

	private void retrieve(int[] array, int b, int p, int pEnd, int bsv, boolean bw) {
		// retrieves elements from library data structure
		
		int j = b-1, m;

		for(int k = pEnd-(G+1); k > p+G;) {
			m = this.rightBinSearch(array, k-G, k, bsv, bw)-1;
			k -= G+1;

			while(m >= k) Writes.swap(array, j--, m--, 1, true, false);
		}

		m = this.rightBinSearch(array, p, p+G, bsv, bw)-1;
		while(m >= p) Writes.swap(array, j--, m--, 1, true, false);
	}
	private void rescatter(int[] array, int i, int p, int pEnd, int bsv, boolean bw) {
		// takes a library and spaces gaps sized G between each individual element
		
		int j = i-2*(G+1), m;

		for(int k = pEnd-(G+1); k > p+G;) {
			m = this.rightBinSearch(array, k-G, k, bsv, bw)-1;
			k -= G+1;

			while(m >= k) {
				Writes.swap(array, j, m--, 1, true, false);
				j -= G+1;
			}
		}

		m = this.rightBinSearch(array, p, p+G, bsv, bw)-1;
		while(m >= p) {
			Writes.swap(array, j, m--, 1, true, false);
			j -= G+1;
		}
	}
	private void rebalance(int[] array, int a, int b, int p, int pEnd, int pb, int bsv, boolean bw) {
		// takes all elements in a library and spreads them out as evenly as possible
		// across gaps within the buffer range p and pb
		
		this.retrieve(array, b, p, pEnd, bsv, bw);
		
		int gCnt     = (pb-p)/(G+1);
		int gapElems = (b-a)-gCnt+1;
		int baseCnt  = gapElems/gCnt;
		int extra    = gapElems - gCnt*baseCnt;
		
		for(int k = p+G;; k += G+1) {
			int iter = baseCnt + (extra-- > 0 ? 1 : 0);
			
			for(int j = 0; j < iter; j++)
				Writes.swap(array, a++, k-G+j, 1, true, false);
			
			if(k < pb-(G+1))
				Writes.swap(array, a++, k, 1, true, false);
			
			else break;
		}
	}
	
	// recommended buffer size ~ N * (G+1)/R
	private void librarySort(int[] array, int a, int b, int p, int pb, int bsv, boolean bw) {
		int len = b-a;

		if(len <= this.MIN_INSERT) {
			this.binaryInsertion(array, a, b);
			return;
		}

		int s = len;
		while(s > this.MIN_INSERT) s = (s-1)/R + 1;

		int i = a+s, j = a+R*s, pEnd = p + (s+1)*(G+1)+G;
		this.binaryInsertion(array, a, i);
		for(int k = 0; k < s; k++) // scatter elements to make G sized gaps b/w them
			Writes.swap(array, a+k, p + k*(G+1)+G, 1, true, false);

		while(i < b) {
			if(i == j) { // rebalancing: scatter elements evenly
				s = i-a;
				int pEndNew = p + (s+1)*(G+1)+G;
				
				if(pEndNew > pb) { // handle accordingly if not enough buffer space for a rescatter
					this.rebalance(array, a, i, p, pEnd, pb, bsv, bw);
					pEnd = pb;
					j = a;
				}
				else {
					this.rescatter(array, pEndNew, p, pEnd, bsv, bw);
					pEnd = pEndNew;
					j = a+(j-a)*R;
				}
			}

			int bLoc = this.randGapSearch(array, p+G, pEnd-(G+1), array[i]); // search gap location (exclude last key)
			                                                                 // in the case of equal valued keys perform randomized binary search
																			 
			int loc  = this.rightBinSearch(array, bLoc-G, bLoc, bsv, bw);	 // search next empty space in gap

			if(loc == bLoc) { // if there is no empty space filled elements in gap are split
			                  // dont increment i since no elements are inserted in this case
			
				do bLoc += G+1;
				while(bLoc < pEnd && this.rightBinSearch(array, bLoc-G, bLoc, bsv, bw) == bLoc);

				if(bLoc == pb) // weve reached the end of buffer: force a rebalance
					this.rebalance(array, a, i, p, pEnd, pb, bsv, bw);
					
				else if(bLoc == pEnd) { // otherwise: append chunk at last gap
					int rotS = G/2 + 1; // any amount such that 1 <= rotS <= G
					this.shiftBW(array, loc-rotS, bLoc-(G+1), bLoc-(G+1)+rotS);
					pEnd += G+1;
				}
				else { // if a gap is full find next non full gap to the right & shift the space down
					int rotP = this.rightBinSearch(array, bLoc-G, bLoc, bsv, bw);
					int rotS = bLoc - Math.max(rotP, bLoc - (G+1)/2); // ceiling division so that G == 1 works
					this.shiftBW(array, loc-rotS, bLoc-rotS, bLoc);
				}
			}
			else {
				int t = array[i];
				Writes.write(array, i++, array[loc], 1, true, false);
				this.insertTo(array, t, loc, this.rightBinSearch(array, bLoc-G, loc, t, false));
			}
		}
		this.retrieve(array, b, p, pEnd, bsv, bw);
	}
	
	////////////////////
	//                //
	//  PARTITIONING  //
	//                //
	////////////////////
	
	private int medianOfThree(int[] array, int a, int m, int b) {
		if(Reads.compareValues(array[m], array[a]) > 0) {
			if(Reads.compareValues(array[m], array[b]) < 0)
				return m;
			if(Reads.compareValues(array[a], array[b]) > 0)
				return a;
			else
				return b;
		}
		else {
			if(Reads.compareValues(array[m], array[b]) > 0)
				return m;
			if(Reads.compareValues(array[a], array[b]) < 0)
				return a;
			else
				return b;
		}
	}
	// when shuffled the first 9 and 27 items will be accessed instead respectively
	private int ninther(int[] array, int a, int b) {
		int s = (b-a)/9;

		int a1 = this.medianOfThree(array, a,     a+  s, a+2*s);
		int m1 = this.medianOfThree(array, a+3*s, a+4*s, a+5*s);
		int b1 = this.medianOfThree(array, a+6*s, a+7*s, a+8*s);

		return this.medianOfThree(array, a1, m1, b1);
	}
	private int medianOfThreeNinthers(int[] array, int a, int b) {
		int s = (b-a)/3;

		int a1 = this.ninther(array, a, a+s);
		int m1 = this.ninther(array, a+s, a+2*s);
		int b1 = this.ninther(array, a+2*s, b);

		return this.medianOfThree(array, a1, m1, b1);
	}
	
	////////////////
	//            //
	//  FLANSORT  //
	//            //
	////////////////
	
	private void quickLibrarySort(int[] array, int a, int b, int p, int pb, int minSize, int bsv, boolean bw) {
		if(b-a <= minSize) {
			this.librarySort(array, a, b, p, pb, bsv, bw);
			return;
		}
		
		int piv = array[this.medianOfThreeNinthers(array, a, b)];
		int i = a-1, j = b;
		
		do {
			do {
				i++;
				Highlights.markArray(1, i);
				Delays.sleep(0.5);
			}
			while(i < j && Reads.compareIndexValue(array, i, piv, 0, false) < 0);
			
			do {
				j--;
				Highlights.markArray(2, j);
				Delays.sleep(0.5);
			}
			while(j >= i && Reads.compareIndexValue(array, j, piv, 0, false) > 0);
				
			if(i < j) Writes.swap(array, i, j, 1, true, false);
			else break;
		}
		while(true);
		
		// average recursion depth is constant!
		
		this.quickLibrarySort(array, a, i, p, pb, minSize, bsv, bw);
		this.quickLibrarySort(array, i, b, p, pb, minSize, bsv, bw);
	}

	@Override
	public void runSort(int[] array, int length, int bucketCount) {// to benefit from average case O(n log n) comparisons & O(n) moves
	                                                               // we would normally shuffle the array before sorting
	                                                               // but for the sake of demonstration this step is omitted
		this.rng = new Random();

		int a = 0, b = length;
		
		// shuffle to almost always guarantee O(n log n) performance 
		
		//for(int i = a+1; i < b; i++)
		//	Writes.swap(array, i, a+this.rng.nextInt(i-a+1), 1, true, false);

		while(b-a > this.MIN_INSERT) {
			int piv = array[this.medianOfThreeNinthers(array, a, b)];

			// partition -> [a][E < piv][i][E == piv][j][E > piv][b]
			int i1 = a, i = a-1, j = b, j1 = b;

			for(;;) {
				while(++i < j) {
					int cmp = Reads.compareIndexValue(array, i, piv, 0.5, true);
					if(cmp == 0) Writes.swap(array, i1++, i, 1, true, false);
					else if(cmp > 0) break;
				}
				Highlights.clearMark(2);

				while(--j > i) {
					int cmp = Reads.compareIndexValue(array, j, piv, 0.5, true);
					if(cmp == 0) Writes.swap(array, --j1, j, 1, true, false);
					else if(cmp < 0) break;
				}
				Highlights.clearMark(2);

				if(i < j) {
					Writes.swap(array, i, j, 1, true, false);
					Highlights.clearMark(2);
				}
				else {
					if(i1 == b) return;
					else if(j < i) j++;

					while(i1 > a) Writes.swap(array, --i, --i1, 1, true, false);
					while(j1 < b) Writes.swap(array, j++, j1++, 1, true, false);

					break;
				}
			}

			int left = i-a, right = b-j;

			if(left <= right) { // sort the smaller partition using larger partition as space
				right -= (right+1)%(G+1) + (G+1);
				left  =  Math.max(right/(G+1) * R, this.MIN_INSERT);
				
				this.quickLibrarySort(array, a, i, j, j+right, left, piv, false);
				a = j;
			}
			else {
				left  -= (left+1)%(G+1) + (G+1);
				right =  Math.max(left/(G+1) * R, this.MIN_INSERT);
				
				this.quickLibrarySort(array, j, b, a, a+left, right, piv, true);
				b = i;
			}
		}
		this.binaryInsertion(array, a, b);
	}
}