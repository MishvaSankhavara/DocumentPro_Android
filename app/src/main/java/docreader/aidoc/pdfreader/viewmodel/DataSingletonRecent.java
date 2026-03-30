package docreader.aidoc.pdfreader.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import docreader.aidoc.pdfreader.model_reader.DocumentModel;

import java.util.ArrayList;

public class DataSingletonRecent {
    private static DataSingletonRecent instance;
    private final MutableLiveData<ArrayList<DocumentModel>> recentDocumentsLiveData = new MutableLiveData<>();
    private final ArrayList<DocumentModel> recentDocuments = new ArrayList<>();

    private DataSingletonRecent() {
    }

    public static DataSingletonRecent getInstance() {
        if (instance == null) {
            instance = new DataSingletonRecent();
        }
        return instance;
    }
    public LiveData<ArrayList<DocumentModel>> getRecentDocumentsLiveData() {
        return recentDocumentsLiveData;
    }

    public void removeFromRecent(DocumentModel document) {
        recentDocuments.remove(document);
        recentDocumentsLiveData.setValue(recentDocuments);
    }

    public void addToRecent(DocumentModel document) {
        recentDocuments.add(document);
        recentDocumentsLiveData.setValue(recentDocuments);
    }

    public void refresh(android.content.Context context) {
        recentDocuments.clear();
        recentDocuments.addAll(docreader.aidoc.pdfreader.database.DatabaseHelper.getInstance(context).getRecentDocuments_DatabaseHelper());
        recentDocumentsLiveData.setValue(recentDocuments);
    }
}
