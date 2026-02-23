package com.example.documenpro.model_reader;

public class CodeColorModel {

    public boolean isWhite_ColorModel() {
        return isWhite_ColorModel;
    }

    public boolean isWhite_ColorModel;
    String codeColor_ColorModel;
    int idSourceBg_ColorModel;

    public String getCodeColor_ColorModel() {
        return codeColor_ColorModel;
    }

    public int getIdSourceBg_ColorModel() {
        return idSourceBg_ColorModel;
    }

    public CodeColorModel(int idSourceBg_ColorModel,
                          String codeColor_ColorModel,
                          boolean isWhite_ColorModel) {

        this.idSourceBg_ColorModel = idSourceBg_ColorModel;
        this.codeColor_ColorModel = codeColor_ColorModel;
        this.isWhite_ColorModel = isWhite_ColorModel;
    }
}