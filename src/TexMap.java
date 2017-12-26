package gl;

import gl.TGA.*;
import java.io.IOException;

class TexMap {
    TGAImage texture;

    TexMap (TexMapParameters params) {
        this.texture = new TGAImage();
        try {
            texture.readTGAFile(params.filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class MtlTexMap extends TexMap {
    enum texType { difs, spec, bump, disp, alph, refl }
    boolean blendU = true, blendV = true;
    float boostMipMapSharpness = 0;

    MtlTexMap(TexMapParameters params) {
        super(params);
    }
}

// this is processed in order
class TexMapParameters {
    int[] enums;
    boolean[] bools;
    float[] floats;
    String paramID, filename;
}