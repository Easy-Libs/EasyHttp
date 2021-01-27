package com.easylibs.utils;

/**
 * Created by sachin on 13/9/17.
 */
public class ContentType {

    public static final String HEADER_ContentType = "Content-Type";

    private static final String SEPARATOR = ";";
    private static final String PREFIX_charset = "charset=";

    public static final ContentType JSON = new ContentType("application/json", "utf-8");

    private String mimeType;
    private String charset;

    public ContentType(String contentType) {
        mimeType = contentType;
    }

    public ContentType(String mimeType, String charset) {
        this.mimeType = mimeType;
        this.charset = charset;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getCharset() {
        return charset;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        ContentType other = null;
        if (obj instanceof ContentType) {
            other = (ContentType) obj;
        }
        if (other == null) {
            return false;
        }
        return EasyUtils.equals(this.mimeType, other.mimeType)
                && EasyUtils.equals(this.charset, other.charset);
    }

    @Override
    public String toString() {
        if (mimeType == null) {
            return null;
        }
        if (EasyUtils.isBlank(charset)) {
            return mimeType;
        }
        return String.format("%s; " + PREFIX_charset + "%s", mimeType, charset);
    }
}
