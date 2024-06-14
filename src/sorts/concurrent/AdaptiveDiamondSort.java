package sorts.concurrent;

import main.ArrayVisualizer;
import sorts.templates.Sort;

public final class AdaptiveDiamondSort extends Sort {
    private final double DELAY = 0.05;
    public AdaptiveDiamondSort(ArrayVisualizer arrayVisualizer) {
        super(arrayVisualizer);
        this.setSortListName("Adaptive Diamond");
        this.setRunAllSortsName("Adaptive Diamond Sort");
        this.setRunSortName("Adaptive Diamondsort");
        this.setCategory("Concurrent Sorts");
        this.setComparisonBased(true);
        this.setBucketSort(false);
        this.setRadixSort(false);
        this.setUnreasonablySlow(false);
        this.setUnreasonableLimit(0);
        this.setBogoSort(false);
    }
	
	private boolean diamondMerge(int[] array, int a, int m, int b) {
		if(a == m || m == b) return true;
		
		if(m-a == 1 && b-m == 1) {
			if(Reads.compareIndices(array, a, b-1, DELAY, true) > 0) {
                Writes.swap(array, a, b-1, DELAY, false, false);
				return true;
			}
			return false;
		}
		int q = (Math.min(m-a, b-m)+1)/2;
		
		if(this.diamondMerge(array, m-q, m, m+q)) {
			this.diamondMerge(array, a, m-q, m);
			this.diamondMerge(array, m, m+q, b);
			this.diamondMerge(array, a+q, m, b-q);
			return true;
		}
		return false;
	}
	
	private void sort(int[] array, int a, int b) {
		if(b-a < 2) return;
		
		int m = (a+b)/2;
		
		this.sort(array, a, m);
		this.sort(array, m, b);
		this.diamondMerge(array, a, m, b);
	}

    @Override
    public void runSort(int[] arr, int length, int buckets) {
        this.sort(arr, 0, length);
    }
}