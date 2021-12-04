package com.hodur.remote.compress;

import com.hodur.common.extension.SPI;

@SPI
public interface Compress {
    byte[] compress(byte[] bytes);
    byte[] decompress(byte[] bytes);
}
