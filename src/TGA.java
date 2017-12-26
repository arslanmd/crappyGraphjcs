package gl;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

class TGA {
    static final TGAColor RED = new TGAColor(255, 0, 0, 255),
                          GREEN = new TGAColor(0, 255, 0, 255),
                          BLUE = new TGAColor(0, 0, 255, 255),
                          BLACK = new TGAColor(0, 0, 0, 255),
                          WHITE = new TGAColor(255, 255, 255, 255);

    private static final int GRAYSCALE = 1,
                             RGB = 3,
                             RGBA = 4;

    static class TGAColor {
        byte[] bgra = new byte[4];

        TGAColor(int R, int G, int B, int A) {
            bgra[0] = clamp8bpp(B);
            bgra[1] = clamp8bpp(G);
            bgra[2] = clamp8bpp(R);
            bgra[3] = clamp8bpp(A);
        }

        TGAColor(int v) {
            for (int i = 0; i < 4; i++) bgra[i] = 0;
            bgra[0] = clamp8bpp(v);
        }

        TGAColor(byte[] p, int bytespp) {
            System.arraycopy(p, 0, bgra, 0, bytespp);
            for (int i = bytespp; i < 4; i++) bgra[i] = 0;
        }

        TGAColor multiply(float intensity) {
            for (int i = 0; i < 4; i++) bgra[i] = clamp8bpp((int) ((bgra[i] & 0xFF) * intensity));
            return this;
        }

        TGAColor multiply(TGAColor clrin) {
            for (int i = 0; i < 4; i++) bgra[i] = clamp8bpp((bgra[i] * clrin.bgra[i]));
            return this;
        }

        TGAColor divide(TGAColor clrin) {
            for (int i = 0; i < 4; i++) bgra[i] = clamp8bpp((bgra[i] / clrin.bgra[i]));
            return this;
        }

        TGAColor add(TGAColor clrin) {
            for (int i = 0; i < 4; i++) bgra[i] = clamp8bpp((bgra[i] + clrin.bgra[i]));
            return this;
        }

        TGAColor subtract(TGAColor clrin) {
            for (int i = 0; i < 4; i++) bgra[i] = clamp8bpp((bgra[i] - clrin.bgra[i]));
            return this;
        }

        @Override
        public String toString() {
            return "rgb(" + (bgra[2] & 0xFF) + ", " + (bgra[1] & 0xFF) + ", " + (bgra[0] & 0xFF) + ")";
        }
    }

    /*******************************************************************************************************************
     * TGAImage Class:
     *      Adapted from the TGA format specification at: http://www.paulbourke.net/dataformats/tga/
     ******************************************************************************************************************/
    static class TGAImage {
        static final byte[] DEV_AREA_REF = {0x00, 0x00, 0x00, 0x00},
                            EXT_AREA_REF = {0x00, 0x00, 0x00, 0x00},
                            FOOTER = {0x54, 0x52, 0x55, 0x45, 0x56, 0x49, 0x53, 0x49, 0x4F,
                                      0x4E, 0x2D, 0x58, 0x46, 0x49, 0x4C, 0x45, 0x2E, 0x00};
        byte[] tgaHead = {0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 0x20}, data;

        int width, height, bytespp, nBytes;
        private int idLength,
                    dataTypeCode,
                    colormapLength,
                    bytesPerPixel,
                    imageDescriptor;

        TGAImage(int w, int h, int inBytespp) {
            if (w <= 0 || h <= 0 ||
               (inBytespp != GRAYSCALE
             && inBytespp != RGB
             && inBytespp != RGBA))
                throw new IllegalArgumentException("Invalid width/height or color depth value.");


            width = w;
            height = h;
            bytespp = inBytespp;
            nBytes = width * height * bytespp;
            data = new byte[nBytes];
        }

        TGAImage() {
            width = 0;
            height = 0;
            bytespp = 0;
            nBytes = 0;
            data = new byte[1];
        }

        boolean readTGAFile(String filename) throws IOException {
            FileInputStream fIn = new FileInputStream(filename);

            // Read header
            byte[] header = new byte[18];
            fIn.read(header);
            idLength = header[0];
            colormapLength = header[1];
            dataTypeCode = header[2];
            width = ((header[13] & 0xff) << 8) | (header[12] & 0xff); // convert to short (sort of)
            height = ((header[15] & 0xff) << 8) | (header[14] & 0xff);
            bytesPerPixel = header[16];
            bytespp = bytesPerPixel >> 3;
            imageDescriptor = header[17];

            // Set up image
            fIn.skip(idLength + colormapLength);
            nBytes = width * height * bytespp;
            data = new byte[nBytes];

            // Sanity checks
            if (width <= 0 || height <= 0 || (bytespp != GRAYSCALE && bytespp != RGB && bytespp != RGBA)) {
                fIn.close();
                throw new IllegalArgumentException("Invalid width/height or color depth value.");
            }

            // Read data
            if (dataTypeCode == 3 || dataTypeCode == 2) { // raw byte files
                fIn.read(data);
            } else if (dataTypeCode == 10 || dataTypeCode == 11) { // run length encoded files
                if (!decodeRLEData(fIn)) throw new IOException("Failed to load RLE data.");
            } else {
                fIn.close();
                throw new IllegalArgumentException("Unknown file format " + dataTypeCode + ".");
            }

            // Flip vertically if origin in lower lh corner
            if (((imageDescriptor >> 5) & 1) == 0)
                this.flipVertically();

            fIn.close();
            return true;
        }

        void writeTGAFile(String filename, boolean rle) throws IOException {
            FileOutputStream fOut = new FileOutputStream(filename);
            tgaHead[16] = (byte) (bytespp << 3);
            tgaHead[12] = (byte) (width & 0xFF);
            tgaHead[13] = (byte) ((width >> 8) & 0xFF);
            tgaHead[14] = (byte) (height & 0xFF);
            tgaHead[15] = (byte) ((height >> 8) & 0xFF);
            tgaHead[2] = bytespp == GRAYSCALE
                                    ? (rle ? (byte) 11 : 3)
                                    : (rle ? (byte) 10 : 2);
            fOut.write(tgaHead);

            if (!rle)
                fOut.write(data);
            else if (!encodeRLEData(fOut))
                throw new IOException("Failed to write RLE data.");

            fOut.write(DEV_AREA_REF);
            fOut.write(EXT_AREA_REF);
            fOut.write(FOOTER);
            fOut.close();
        }

        boolean decodeRLEData(FileInputStream fIn) throws IOException {
            int pcount = width * height, currpix = 0, currbyte = 0;
            TGAColor cbuffer = new TGAColor(0, 0, 0, 255);

            byte chunkHeader;
            chunkHeader = (byte) fIn.read();

            if ((chunkHeader & 0xFF) < 128) {
                chunkHeader+=1;

                for (int i = 0; i < (chunkHeader & 0xFF); i++) {
                    fIn.read(cbuffer.bgra, 0, bytespp);

                    for (int t = 0; t < bytespp; t++) data[currbyte++] = cbuffer.bgra[t];
                    currpix+=1;
                    if (currpix > pcount)
                        throw new IOException("Too many pixels read.");
                }
            } else {
                chunkHeader -= clamp8bpp(127);
                fIn.read(cbuffer.bgra, 0, bytespp);

                for (int i = 0; i < (chunkHeader & 0xFF); i++) {
                    for (int t = 0; t < bytespp; t++)
                        data[currbyte++] = cbuffer.bgra[t];
                    currpix+=1;
                    if (currpix > pcount)
                        throw new IOException("Too many pixels read.");
                }
            }

            while (currpix < pcount) {
                chunkHeader = (byte) fIn.read();
                if ((chunkHeader & 0xFF) < 128) {
                    chunkHeader += 1;

                    for (int i = 0; i < (chunkHeader & 0xFF); i++) {
                        fIn.read(cbuffer.bgra, 0, bytespp);

                        for (int t = 0; t < bytespp; t++)
                            data[currbyte++] = cbuffer.bgra[t];
                        currpix += 1;
                        if (currpix > pcount)
                            throw new IOException("Too many pixels read.");
                    }
                } else {
                    chunkHeader -= clamp8bpp(127);
                    fIn.read(cbuffer.bgra, 0, bytespp);

                    for (int i = 0; i < (chunkHeader & 0xFF); i++) {
                        for (int t = 0; t < bytespp; t++)
                            data[currbyte++] = cbuffer.bgra[t];
                        currpix += 1;
                        if (currpix > pcount)
                            throw new IOException("Too many pixels read.");
                    }
                }
            }

            return true;
        }

        boolean encodeRLEData(FileOutputStream fOut) throws IOException {
            final int maxChunkLength = 128;
            int nPix = width * height, currPix = 0;

            while (currPix < nPix) {
                int chunkStart = currPix * bytespp,
                    currByte = currPix * bytespp,
                    runLength = 1;
                boolean raw = true;

                while (currPix + runLength < nPix && runLength < maxChunkLength) {
                    boolean encodeSuccess = true;

                    for (int t = 0; encodeSuccess && t < bytespp; t++)
                        encodeSuccess = (data[currByte + t] == data[currByte + t + bytespp]);

                    currByte += bytespp;

                    if (runLength == 1)
                        raw = !encodeSuccess;

                    if (raw && encodeSuccess) {
                        runLength--;
                        break;
                    } else if (!raw && !encodeSuccess) {
                        break;
                    } else {
                        runLength++;
                    }
                }

                currPix += runLength;
                fOut.write(raw ? runLength - 1 : runLength + 127);
                fOut.write(data, chunkStart, (raw ? runLength * bytespp : bytespp));
            }

            return true;
        }

        void flipVertically() {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height / 2; y++) {
                    TGAColor pix1 = get(x, y),
                             pix2 = get(x, height - 1 - y);

                    set(x, y, pix2);
                    set(x, height - 1 - y, pix1);
                }
            }
        }

        TGAColor get(int x, int y) {
            if (data == null || x < 0 || y < 0 || x >= width || y >= height)
                throw new IllegalArgumentException("Bad input.");
            byte[] pix;
            pix = Arrays.copyOfRange(data, ((x + y * width) * bytespp),
                               ((x + y * width) * bytespp) + bytespp);
            return new TGAColor(pix, bytespp);
        }

        void set(int x, int y, TGAColor c) {
            if (data == null || x < 0 || y < 0 || x >= width || y >= height)
                throw new IllegalArgumentException("Bad input.");

            int clr = 0, pred= ((x + y * width) * bytespp) + bytespp;

            for (int i = ((x + y * width) * bytespp); i < pred; i++) {
                this.data[i] = c.bgra[clr];
                clr++;
            }
        }
    }

    static void scale(TGAImage in, TGAImage out,
                      char filterMode, boolean multi) {
       if (multi) {
           final int processors = Runtime.getRuntime().availableProcessors(),
                     windowX = out.width / processors /*, windowY = out.height / processors*/;

           AtomicInteger thrNum = new AtomicInteger(0);
           CountDownLatch latch = new CountDownLatch(processors);

           Runnable scaleWindow = () -> {
               int tn = thrNum.incrementAndGet(),
                        wxEnd = tn * windowX;
               double ncx, ncy;
               int cxi, cyi;
               for (int i = wxEnd - windowX; i < wxEnd; i++) {
                   for (int j = 0; j < out.height; j++) {
                       // normalized pixel coordinates
                       ncx = (double) i / out.width; ncy = (double) j / out.height;
                       cxi = (int) (ncx * in.width); cyi = (int) (ncy * in.height);
                       try {
                           out.set(i, j, in.get(cxi, cyi));
                       } catch (IllegalArgumentException e) {
                           out.set(i, j, BLACK);
                       }
                   }
               }
               latch.countDown();
           };
           // start all threads
           for (int i = 1; i <= processors; i++) {
               Thread thread = new Thread(scaleWindow);
               thread.start();
           }
           // wait for all threads to finish
           try {
               latch.await();
           } catch (InterruptedException e) {
               e.printStackTrace();
           }
       } else {
           double ncx, ncy;
           int cxi, cyi;
           for (int i = 0; i < out.width; i++) {
               for (int j = 0; j < out.height; j++) {
                   // normalized pixel coordinates
                   ncx = (double) i / out.width; ncy = (double) j / out.height;
                   cxi = (int) (ncx * in.width); cyi = (int) (ncy * in.height);
                   try {
                       out.set(i, j, in.get(cxi, cyi));
                   } catch (IllegalArgumentException e) {
                       out.set(i, j, BLACK);
                   }
               }
           }
       }
    }

    static byte clamp8bpp(int in) {
        if (in > 127 && in < 256) return (byte) (in % 256);
        else if (in >= 0 && in <= 127) return (byte) in;
        else if (in > 255) return -1;
        return 0;
    }
}
