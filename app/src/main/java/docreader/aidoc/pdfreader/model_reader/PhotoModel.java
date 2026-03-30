package docreader.aidoc.pdfreader.model_reader;

public class PhotoModel {

    int id_PhotoModel;
    String filePath_PhotoModel;

    public int getId_PhotoModel() {
        return id_PhotoModel;
    }

    public String getFilePath_PhotoModel() {
        return filePath_PhotoModel;
    }

    public PhotoModel(String filePath_PhotoModel, int id_PhotoModel) {
        this.filePath_PhotoModel = filePath_PhotoModel;
        this.id_PhotoModel = id_PhotoModel;
    }
}