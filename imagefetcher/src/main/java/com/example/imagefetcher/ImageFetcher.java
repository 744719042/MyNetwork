package com.example.imagefetcher;

import java.io.File;

public class ImageFetcher {


    private ImageFetcher(int memMaxSize, long diskMaxSize, File diskCacheDir) {
    }

    public static class Builder {
        private int memMaxSize;
        private long diskMaxSize;
        private File diskCacheDir;

        public Builder() {

        }

        public Builder memMaxSize(int memMaxSize) {
            this.memMaxSize = memMaxSize;
            return this;
        }

        public Builder diskMaxSize(long diskMaxSize) {
            this.diskMaxSize = diskMaxSize;
            return this;
        }

        public Builder diskCacheDir(File diskCacheDir) {
            this.diskCacheDir = diskCacheDir;
            return this;
        }

        public ImageFetcher build() {
            return new ImageFetcher(memMaxSize, diskMaxSize, diskCacheDir);
        }
    }
}
