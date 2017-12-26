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
