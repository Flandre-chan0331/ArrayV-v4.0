package sorts.hybrid;

import java.util.Comparator;
import java.util.TreeSet;

import main.ArrayVisualizer;
import sorts.templates.Sort;

/*

Coded for ArrayV by Haruki
in collaboration with aphitorite

+---------------------------+
| Sorting Algorithm Scarlet |
+---------------------------+

 */

/**
 * @author Haruki (Ayako-chan)
 * @author aphitorite
 *
 */
public class EulaSort extends Sort {
    public EulaSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        this.setSortListName("Eula");
        this.setRunAllSortsName("Eula Sort");
        this.setRunSortName("Eulasort");
        this.setCategory("Hybrid Sorts");
        this.setComparisonBased(true);
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }

    protected int binSearch(int[] array, int a, int b, int val, boolean left) {
        while (a < b) {
            int m = a + (b - a) / 2;
            Highlights.markArray(2, m);
            Delays.sleep(0.25);
            int c = Reads.compareValues(val, array[m]);
            if (c < 0 || (left && c == 0)) b = m;
            else a = m + 1;
        }
        return a;
    }

    public void sort(int[] array, int a, int b) {
        int len = b - a;
        if (len < 2) return;
        // TreeSets allow for me to take all the uniques without dupes.
        // It *also* allows me to check if there are dupes at all. AT THE SAME TIME!
        TreeSet<Integer> uSet = new TreeSet<>(new Comparator<Integer>(){
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return Reads.compareValues(lhs, rhs);
            }
        });

        // u: the number of unique items found
        int u = 0;

        //arrayVisualizer.setExtraHeading(" / Collecting uniques...");
        for (int i = a; i < b; i++) {

            // Treat this as an aux write.
            Writes.changeAuxWrites(1);

            // Decorative marking position.
            Highlights.markArray(1, i);
            Delays.sleep(1);

            // And here's where that dupe check matters. If it's a new item, increment the
            // space.
            if (uSet.add(array[i])) {
                Writes.changeAllocAmount(1);
                u++;
            }
        }

        // If there is only one unique element, the list is already sorted.
        if (u == 1) {
            // I don't wanna play with you anymore...
            // *tosses TreeSet into a trashbin*
            uSet.clear();
            Writes.changeAllocAmount(-u);

            // *tosses code after this into a trashbin*
            return;
        }

        // Convert the TreeSet to something comparable with a binary search, and take
        // its place.
        // Same space before and after, so no need to change the allocation or writes.
        // Just treat this solely as a conversion.
        int[] uniques = Writes.createExternalArray(u);
        int idx = 0;
        for (int val : uSet)
            Writes.write(uniques, idx++, val, 0, false, true);

        // Guess what, TreeSet? Yep, forget you! I have what I want now!
        uSet.clear();
        Writes.changeAllocAmount(-u);

        int[] ptrs = Writes.createExternalArray(u + 1);
        for (int i = a; i < b; i++) {

            // Decorative marking position.
            Highlights.markArray(1, i);

            // Search where the item corresponds to a unique element the the uniques array
            // and save it.
            idx = binSearch(uniques, 0, u, array[i], true);
            Writes.write(ptrs, idx, ptrs[idx] + 1, 1, false, true);
        }

        for (int i = 1; i <= u; i++) // Do a prefix sum to find locations
            Writes.write(ptrs, i, ptrs[i] + ptrs[i - 1], 0, false, true);

        for (int i = 0, j = 0; i < u; i++) {
            int cur = ptrs[i];
            int loc = binSearch(uniques, i, u, array[a + j], true);
            while (j < cur) {
                if (loc == i) {
                    j++;
                    loc = binSearch(uniques, i, u, array[a + j], true);
                } else {
                    Writes.write(ptrs, loc, ptrs[loc] - 1, 1, false, true);
                    int dest = ptrs[loc];
                    while (true) {
                        int newLoc = binSearch(uniques, i, u, array[a + dest], true);

                        if (newLoc != loc) {
                            loc = newLoc;
                            break;
                        }
                        Writes.write(ptrs, loc, ptrs[loc] - 1, 1, false, true);
                        dest--;
                    }
                    Writes.swap(array, a + j, a + dest, 1, true, false);
                }
            }
            j = binSearch(array, a, b, uniques[i], false);
        }
        Writes.deleteExternalArrays(ptrs, uniques);
    }

    @Override
    public void runSort(int[] array, int sortLength, int bucketCount) {
        sort(array, 0, sortLength);
    }
}
