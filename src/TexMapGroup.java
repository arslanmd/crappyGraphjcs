package gl;

import gl.TGA.TGAImage;
import gl.TexMap.*;

import java.util.HashMap;

class TexMapGroup {
    private HashMap<String, TexMap> texMaps;
    private String[] texKeys;

    TexMapGroup(String[][] texNames, TGAImage[] texMapsIn) {
        if (texNames.length != texMapsIn.length)
            throw new IllegalArgumentException("");
    }
}
