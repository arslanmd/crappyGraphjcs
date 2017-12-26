package gl;

import java.util.BitSet;

enum DataMode {
    rgb,
    mono;
}

public class ColorBits {
    BitSet[] data;
    byte mode;

    ColorBits(DataMode modeIn, int bitspp) {
        if (bitspp <= 0 || bitspp > 32)
            throw new IllegalArgumentException("Too many bits.");

        switch (modeIn) {
            case rgb:
                mode = 0x3;
                data = new BitSet[mode];
                break;
            case mono:
                mode = 0x1;
                data = new BitSet[mode];
                break;
            default:
                throw new IllegalArgumentException("Bad mode.");
        }

        for (int i = 0; i < mode; i++) {
            data[i] = new BitSet(bitspp);
        }
    }

    byte[] getAsByteAry() {
        byte[] byteAry = new byte[mode];

        for (int i = 0; i < mode; i++) {
            for (int j = data[i].nextSetBit(0);
                 i >= 0; i = data[i].nextSetBit(j + 1)) {
                if (j >= 8) break;
                byteAry[i] |= (0b1 << i);
            }
        }

        return byteAry;
    }

    int[] getAsIntAry() {
        int[] intAry = new int[mode];

        for (int i = 0; i < mode; i++) {
            for (int j = data[i].nextSetBit(0);
                 j >= 0; j = data[i].nextSetBit(j + 1)) {
                if (j >= 32) break;
                intAry[i] += (0b1 << i);
            }
        }

        return intAry;
    }

    void setFromByteAry(byte[] aryIn) {
        for (int i = 0; i < mode; i++) {
            data[i].clear();
            for (int j = 0; j < 8; j++) {
                data[i].set(j, (aryIn[i] >> j) & 1);
            }
        }
    }

    void setFromIntAry(int[] aryIn) {
        long[] temp = new long[1];
        for (int i = 0; i < mode; i++) {
            data[i].clear();
            temp[0] = aryIn[i];
            data[i].or(BitSet.valueOf(temp).get(0, 31));
        }
    }
}
