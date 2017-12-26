package gl;

// A very high level definition of a shader
interface Shader {
    void vertex(int faceIndex);
    TGA.TGAColor fragment(Geometry.Vec3i pos, Geometry.Vec3f barycentric);
}