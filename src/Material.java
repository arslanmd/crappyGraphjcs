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

    // implement mtl shader
    Shader[] shaders;
    TexMapGroup[] texMaps;
}
