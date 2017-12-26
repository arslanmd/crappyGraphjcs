/*********************************************************************************************************************** 
 *  (C) 2016-2017 Dorukhan Arslan. Released under the GPL.
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/
 **********************************************************************************************************************/

//THIS CLASS IS EXPERIMENTAL!///////////////////////////////////////////////////////////////////////////////////////////

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
