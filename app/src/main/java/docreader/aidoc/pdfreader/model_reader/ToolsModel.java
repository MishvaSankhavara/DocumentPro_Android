package docreader.aidoc.pdfreader.model_reader;

public class ToolsModel {

    private int padding_toolModel;
    private int toolType_toolModel;
    private int icRes_toolModel;
    private int nameTool_toolModel;
    private int backgroundColor_toolModel;

    public int getPadding_toolModel() {
        return padding_toolModel;
    }

    public int getToolType_toolModel() {
        return toolType_toolModel;
    }

    public int getIcRes_toolModel() {
        return icRes_toolModel;
    }

    public int getNameTool_toolModel() {
        return nameTool_toolModel;
    }

    public int getBackgroundColor_toolModel() {
        return backgroundColor_toolModel;
    }

    public ToolsModel(int nameTool_toolModel,
                      int icRes_toolModel,
                      int toolType_toolModel,
                      int padding_toolModel,
                      int backgroundColor_toolModel) {

        this.nameTool_toolModel = nameTool_toolModel;
        this.icRes_toolModel = icRes_toolModel;
        this.toolType_toolModel = toolType_toolModel;
        this.padding_toolModel = padding_toolModel;
        this.backgroundColor_toolModel = backgroundColor_toolModel;
    }
}