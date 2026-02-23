package com.example.documenpro.model_reader;

public class ToolsModel {

    private int padding_toolModel;
    private int toolType_toolModel;
    private int icRes_toolModel;
    private int nameTool_toolModel;

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

    public ToolsModel(int nameTool_toolModel,
                      int icRes_toolModel,
                      int toolType_toolModel,
                      int padding_toolModel) {

        this.nameTool_toolModel = nameTool_toolModel;
        this.icRes_toolModel = icRes_toolModel;
        this.toolType_toolModel = toolType_toolModel;
        this.padding_toolModel = padding_toolModel;
    }
}