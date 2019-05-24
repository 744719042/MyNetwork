package com.example.imagefetcher.disk;


import com.example.imagefetcher.utils.MD5Utils;

public class MD5DiskFileName implements DiskFileName {
    @Override
    public String getName(String url) {
        return MD5Utils.MD5(url);
    }
}
