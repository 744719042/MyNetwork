package com.example.imagefetcher.disk;

import com.example.imagefetcher.utils.CollectionUtils;
import com.example.imagefetcher.utils.LogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DiskLruCache {
    private static final String TAG = "DiskLruCache";
    private long mMaxSize;
    private File mDir;
    private DiskFileName mDiskFileName;

    private DiskLruCache(long maxSize, File dir, DiskFileName diskFileName) {
        this.mMaxSize = maxSize;
        this.mDir = dir;
        this.mDiskFileName = diskFileName;
    }

    public void initDiskLruCache() {
        File[] files = mDir.listFiles();
        if (!CollectionUtils.isEmpty(files)) {
            insertSort(files);
            long curSize = 0;
            int i = 0;
            for (; i < files.length; i++) {
                if (curSize > mMaxSize) {
                    break;
                }
                curSize += files[i].length();
            }

            i = i == 0 ? 0 : i - 1;
            for (; i < files.length; i++) {
                files[i].delete();
            }
        }
    }

    /**
     * 按照修改时间从大到小排列，也就是后使用的排在最前面
     * @param files
     * @return
     */
    private static File[] insertSort(File[] files) {
        try {
            for (int i = 1; i < files.length; i++) {
                File cur = files[i];
                long temp = files[i].lastModified();
                int j = i;
                while (j > 0 && files[j - 1].lastModified() < temp) {
                    files[j] = files[j - 1];
                    --j;
                }
                files[j] = cur;
            }
            return files;
        } catch (Exception e) {
            LogUtils.printException(TAG, e);
        }
        return files;
    }

    public static DiskLruCache open(long maxSize, File dir) {
        return open(maxSize, dir, new MD5DiskFileName());
    }

    public static DiskLruCache open(long maxSize, File dir, DiskFileName diskFileName) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("Disk Cache max size > 0");
        }

        if (dir == null) {
            throw new IllegalArgumentException("Disk cache dir should not be null");
        }

        if (diskFileName == null) {
            diskFileName = new MD5DiskFileName();
        }
        return new DiskLruCache(maxSize, dir, diskFileName);
    }

    public synchronized OutputStream newOutputStream(String url) throws IOException {
        File file = getFile(getFileName(url));
        if (file.exists()) {
            file.delete();
        } else {
            file = getFile(getTmpFileName(url));
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        }
        return new FileOutputStream(file);
    }

    public synchronized void commitOutputStream(String url) {
        File file = getFile(getTmpFileName(url));
        if (file.exists()) {
            file.renameTo(getFile(getFileName(url)));
        }
    }

    public synchronized void clearOutputStream(String url) {
        try {
            File file = getFile(getTmpFileName(url));
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized InputStream newInputStream(String url) throws FileNotFoundException {
        File file = getFile(getFileName(url));
        return new FileInputStream(file);
    }


    private String getTmpFileName(String url) {
        return mDiskFileName.getName(url) + ".tmp";
    }

    private String getFileName(String url) {
        return mDiskFileName.getName(url);
    }

    private File getFile(String name) {
        return new File(mDir, name);
    }

    public synchronized boolean isFileExist(String url) {
        File file = getFile(getFileName(url));
        return file.exists();
    }

}
