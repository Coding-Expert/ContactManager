package com.basicphones.contacts;

public interface ImageCompressionListener {
    void onStart();

    void onCompressed(String filePath);
}
