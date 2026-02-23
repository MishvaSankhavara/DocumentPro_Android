package com.example.documenpro.model_reader;

public class LanguageModel {

    private int imgResource_LanModel;
    private String nameLanguage_LanModel;
    private String keyLanguage_LanModel;

    public String getNameLanguage_LanModel() {
        return nameLanguage_LanModel;
    }

    public String getKeyLanguage_LanModel() {
        return keyLanguage_LanModel;
    }

    public int getImgResource_LanModel() {
        return imgResource_LanModel;
    }

    public LanguageModel(String keyLanguage_LanModel,
                         String nameLanguage_LanModel,
                         int imgResource_LanModel) {

        this.keyLanguage_LanModel = keyLanguage_LanModel;
        this.nameLanguage_LanModel = nameLanguage_LanModel;
        this.imgResource_LanModel = imgResource_LanModel;
    }
}