package docreader.aidoc.pdfreader.clickListener;

public interface SortingListener {

    void onSortingFileSizeDown();

    void onSortingFileSizeUp();

    void onSortingZtoA();

    void onSortingAtoZ();

    void onSortingByDateNewest();

    void onSortingDateOldest();
}