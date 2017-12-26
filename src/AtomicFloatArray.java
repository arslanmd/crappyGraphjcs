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

public class AtomicFloatArray {
    private AtomicFloat[] afAry;
    final int sz;

    public AtomicFloatArray(final int szIn) {
        sz = szIn;
        afAry = new AtomicFloat[sz];
    }

    public void initValAll(float val) {
        for (int i = 0; i < sz; i++) {
            afAry[i] = new AtomicFloat();
            afAry[i].set(val);
        }
    }

    public final float getAt(int idx) {
        if (idx >= sz && 0 > idx) return -Float.MAX_VALUE;
        return afAry[idx].get();
    }

    public final void setAt(int idx, float newValue) {
        if (idx >= sz && 0 > idx) return;
        afAry[idx].set(newValue);
    }
}
