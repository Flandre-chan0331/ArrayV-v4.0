package sorts.quick;

import main.ArrayVisualizer;
import sorts.templates.Sort;

public class StableQuickSortMiddlePivotSB extends Sort {
    public StableQuickSortMiddlePivotSB(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);

        this.setSortListName("Stable Quick (Middle Pivot, Single Buffer)");
        this.setRunAllSortsName("Stable Quick Sort (Middle Pivot, Single Buffer)");
        this.setRunSortName("Stable Quicksort (Middle Pivot, Single Buffer)");
        this.setCategory("Quick Sorts");
        this.setComparisonBased(true);
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }

    int partition(int[] array, int[] buf, int left, int right, int pivIdx) {
        Highlights.clearMark(2);
        int a = left, b = right;
        int piv = array[pivIdx];
        // determines which elements do not need to be moved
        for (; a < pivIdx; a++) {
            Highlights.markArray(1, a);
            Delays.sleep(0.25);
            if (Reads.compareValues(array[a], piv) > 0) break;
        }
        for (; b > pivIdx + 1; b--) {
            Highlights.markArray(1, b - 1);
            Delays.sleep(0.25);
            if (Reads.compareValues(array[b - 1], piv) < 0) break;
        }
        // partitions the list stably
        int j = a, k = 0;
        for (int i = a; i < pivIdx; i++) {
            Highlights.markArray(2, j);
            if (Reads.compareIndexValue(array, i, piv, 0, true) <= 0)
                Writes.write(array, j++, array[i], 0.5, false, false);
            else Writes.write(buf, k++, array[i], 0.5, false, true);
        }
        for (int i = pivIdx + 1; i < b; i++) {
            Highlights.markArray(2, j);
            if (Reads.compareIndexValue(array, i, piv, 0, true) < 0)
                Writes.write(array, j++, array[i], 0.5, false, false);
            else Writes.write(buf, k++, array[i], 0.5, false, true);
        }
        // write the pivot at its correct location
        Writes.write(array, j, piv, 0.5, true, false);
        Writes.arraycopy(buf, 0, array, j + 1, k, 0.5, true, false);
        return j;
    }

    private void sortHelper(int[] array, int[] buf, int start, int end) {
        while (end - start > 1) {
            int mid = partition(array, buf, start, end, start + (end - start) / 2);
            if (end - (mid + 1) < mid - start) {
                sortHelper(array, buf, mid + 1, end);
                end = mid;
            } else {
                sortHelper(array, buf, start, mid);
                start = mid + 1;
            }
        }
    }

    public void quickSort(int[] array, int left, int right) {
        int[] buf = Writes.createExternalArray(right - left);
        sortHelper(array, buf, left, right);
        Writes.deleteExternalArray(buf);
    }

    @Override
    public void runSort(int[] array, int sortLength, int bucketCount) {
        quickSort(array, 0, sortLength);
    }
}
