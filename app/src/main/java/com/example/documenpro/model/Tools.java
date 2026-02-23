package com.example.documenpro.model;

public class Tools {
    private int nameTool;
    private int icRes;
    private int toolType;
    private int padding;

    public Tools(int nameTool, int icRes, int toolType, int padding) {
        this.nameTool = nameTool;
        this.icRes = icRes;
        this.toolType = toolType;
        this.padding = padding;
    }

    public int getNameTool() {
        return nameTool;
    }

    public void setNameTool(int nameTool) {
        this.nameTool = nameTool;
    }

    public int getIcRes() {
        return icRes;
    }

    public void setIcRes(int icRes) {
        this.icRes = icRes;
    }

    public int getToolType() {
        return toolType;
    }

    public void setToolType(int toolType) {
        this.toolType = toolType;
    }

    public int getPadding() {
        return padding;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }
}
