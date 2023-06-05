package sorts.merge;

import main.ArrayVisualizer;
import sorts.templates.Sort;

/*
 * 
The MIT License (MIT)

Copyright (c) 2021 aphitorite
Copyright (c) 2021 Yuri-chan

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

/**
 * @author aphitorite
 * @author Yuri-chan
 *
 */
public final class BottomUpIndexMergeSort extends Sort {

    public BottomUpIndexMergeSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        this.setSortListName("Bottom-up Index Merge");
        this.setRunAllSortsName("Bottom-up Index Merge Sort");
        this.setRunSortName("Bottom-up Index Mergesort");
        this.setCategory("Merge Sorts");
        this.setComparisonBased(true);
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }

    private void indexSort(int[] array, int[] idx, int a, int b) {
        while (a < b) {
            Highlights.markArray(2, a);

            if (Reads.compareOriginalValues(a, idx[a]) != 0) {
                int t = array[a];
                int i = a, nxt = idx[a];

                do {
                    Writes.write(array, i, array[nxt], 0, true, false);
                    Writes.write(idx, i, i, 0.5, false, true);

                    i = nxt;
                    nxt = idx[nxt];
                } while (Reads.compareOriginalValues(nxt, a) != 0);

                Writes.write(array, i, t, 0, true, false);
                Writes.write(idx, i, i, 0.5, false, true);
            }
            a++;
        }
    }

    private void merge(int[] array, int[] idx, int a, int m, int b) {
        int i = a, j = m, c = a;
        Highlights.clearAllMarks();

        while (i < m && j < b) {
            if (Reads.compareValues(array[i], array[j]) <= 0) {
                Highlights.markArray(1, i);
                Writes.write(idx, c++, i++, 0.5, false, true);
            } else {
                Highlights.markArray(2, j);
                Writes.write(idx, c++, j++, 0.5, false, true);
            }
        }

        while (i < m) {
            Highlights.markArray(1, i);
            Writes.write(idx, c++, i++, 0.5, false, true);
        }
        while (j < b) {
            Highlights.markArray(2, j);
            Writes.write(idx, c++, j++, 0.5, false, true);
        }

        this.indexSort(array, idx, a, b);
    }

    @Override
    public void runSort(int[] array, int length, int bucketCount) throws Exception {
        int[] idx = Writes.createExternalArray(length);
        int n = length, d = 1 << (int) (Math.log(n - 1) / Math.log(2) + 1);
        while (d > 1) {
            int i = 0, dec = 0;
            while (i < n) {
                int j = i;
                dec += n;
                while (dec >= d) {
                    dec -= d;
                    j++;
                }
                int k = j;
                dec += n;
                while (dec >= d) {
                    dec -= d;
                    k++;
                }
                merge(array, idx, i, j, k);
                i = k;
            }
            d /= 2;
        }
        Writes.deleteExternalArray(idx);

    }

}
