package org.aksw.simba.squirrel.analyzer.compress.enums;

public enum MimeTypeEnum {

    TAR("application/x-tar"),
    TAR_GZ("application/gzip"),
    ZIP("application/zip"),
    F7Z("application/x-7z-compressed"),
    BZ2("application/x-bzip2");

    private String mime_type;

    private MimeTypeEnum(String mime_type) {
        this.mime_type = mime_type;
    }

    public String mime_type() {
        return mime_type;
    }


}
