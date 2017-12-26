package gl;

import gl.Geometry.*;
import gl.TGA.TGAColor;

class Light {
    enum lType { basic, directional }
    static class BasicLight {
        final static lType kind = lType.basic;
        TGAColor color;
        Vec3f position;
        float intensity;

        BasicLight(Vec3f position, float intensity, TGAColor color) {
            this.color = color;
            this.position = position;
            this.intensity = intensity;
        }
    }

    static class DirectionalLight extends BasicLight {
        final static lType kind = lType.directional;
        Vec3f direction;
        DirectionalLight(Vec3f position, Vec3f direction, float intensity, TGAColor color) {
            super(position, intensity, color);
            this.direction = direction;
        }
    }
}
