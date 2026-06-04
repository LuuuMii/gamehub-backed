package com.cmc.enums.file;


public enum FileCategory {
    ARTICLE_IMG("articleImg"),
    AVATAR("avatar"),
    COVER_IMG("coverImg"),
    VIDEO("video"),;

    private final String fileFolder;

    FileCategory(String fileFolder){
        this.fileFolder = fileFolder;;
    }

    public String getFileFolder(){
        return fileFolder;
    }


}
