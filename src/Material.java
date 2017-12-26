package gl;
import gl.TGA.*;

// replicate structure of an mtl file
class Material {
    TGAColor kAmbient, kDiffuse, kSpecular;
    float specularExp, ior;

    enum illum {
        c1a0 (0),
        c1a1 (1),
        h1 (2),
        g1rt1 (4),
        f1rt1 (5),
        rf1f0rt1 (6),
        rf1f1rt1 (7),
        f1rt1rt0 (8),
        g1rt0 (9),
        sis1 (10);
        illum(int i) {}
    }

    // displacement maps apply to vertices
    // bump applies to fragments

    // implement mtl shader
    Shader[] shaders;
    TexMapGroup[] texMaps;
}
