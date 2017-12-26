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

package gl;

/* MORE RANTING
 *  If Java generics weren't a cruel joke, this entire mess of a 'class' would be less than ~=200 lines. But alas, the 
 *  force feeding of object orientation gets in the way of sane programming once again. I know Java was never designed 
 *  to to this, that's why I chose it. I might actually try and port this project over to Kotlin.
 */
class Geometry {
//VECTORS///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    static class Vec2i {
        int x, y;

        Vec2i(int x, int y) {
            this.x = x;
            this.y = y;
        }

        Vec2i() {
            this.x = 0;
            this.y = 0;
        }

        Vec2i mul(Vec2i rhs) {
            return new Vec2i(x * rhs.x, y * rhs.y);
        }

        Vec2i mul(float scalar) {
            return new Vec2i((int) ((float) x * scalar), (int) ((float) y * scalar));
        }

        Vec2i div(Vec2i rhs) {
            return new Vec2i(x / rhs.x, y / rhs.y);
        }

        Vec2i add(Vec2i rhs) {
            return new Vec2i(x + rhs.x, y + rhs.y);
        }

        Vec2i sub(Vec2i rhs) {
            return new Vec2i(x - rhs.x, y - rhs.y);
        }

        Vec2f toVec2f() {
            return new Vec2f((float) this.x, (float) this.y);
        }

        int mag() {
            return (int) Math.sqrt(x * x + y * y);
        }

        int dot(Vec2i rhs) {
            return this.x * rhs.x + this.y * rhs.y;
        }

        @Override
        public String toString() {
            return "V2i[ " + x + ", " + y + " ]";
        }

        void setDim(int idx, int val) {
            switch (idx) {
                case 0:
                    this.x = val;
                case 1:
                    this.y = val;
            }
        }

        void setAllDim(int xval, int yval) {
            this.x = xval;
            this.y = yval;
        }

        int dim(int idx) {
            switch (idx) {
                case 0:
                    return this.x;
                case 1:
                    return this.y;
            }

            return 0;
        }
    }

    static class Vec2f {
        float x, y;

        Vec2f(float x, float y) {
            this.x = x;
            this.y = y;
        }

        Vec2f() {
            this.x = 0;
            this.y = 0;
        }

        Vec2f mul(Vec2f rhs) {
            return new Vec2f(x * rhs.x, y * rhs.y);
        }

        Vec2f mul(float scalar) {
            return new Vec2f(x * scalar, y * scalar);
        }

        Vec2f div(Vec2f rhs) {
            return new Vec2f(x / rhs.x, y / rhs.y);
        }

        Vec2f add(Vec2f rhs) {
            return new Vec2f(x + rhs.x, y + rhs.y);
        }

        Vec2f sub(Vec2f rhs) {
            return new Vec2f(x - rhs.x, y - rhs.y);
        }

        Vec2f norm() {
            return this.mul(1 / this.mag());
        }

        Vec2i toVec2i() {
            return new Vec2i((int) this.x, (int) this.y);
        }

        float mag() {
            return (float) Math.sqrt(x * x + y * y);
        }

        float dot(Vec2f rhs) {
            return this.x * rhs.x + this.y * rhs.y;
        }

        @Override
        public String toString() {
            return "V2f[ " + x + ", " + y + " ]";
        }

        void setDim(int idx, float val) {
            switch (idx) {
                case 0:
                    this.x = val;
                case 1:
                    this.y = val;
            }
        }

        void setAllDim(float xval, float yval) {
            this.x = xval;
            this.y = yval;
        }

        float dim(int idx) {
            switch (idx) {
                case 0:
                    return this.x;
                case 1:
                    return this.y;
            }

            return 0;
        }
    }

    static class Vec3i {
        int x, y, z;

        Vec3i(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        Vec3i() {
            this.x = 0;
            this.y = 0;
            this.z = 0;
        }

        Vec3i mul(Vec3i rhs) {
            return new Vec3i(x * rhs.x, y * rhs.y, z * rhs.z);
        }

        Vec3i mul(float scalar) {
            return new Vec3i((int) (x * scalar), (int) (y * scalar), (int) (z * scalar));
        }

        Vec3i div(Vec3i rhs) {
            return new Vec3i(x / rhs.x, y / rhs.y, z / rhs.z);
        }

        Vec3i add(Vec3i rhs) {
            return new Vec3i(x + rhs.x, y + rhs.y, z + rhs.z);
        }

        Vec3i sub(Vec3i rhs) {
            return new Vec3i(x - rhs.x, y - rhs.y, z - rhs.z);
        }

        Vec3f v3f() {
            return new Vec3f((float) this.x, (float) this.y, (float) this.z);
        }

        Vec3i cross(Vec3i rhs) {
            return new Vec3i(this.y * rhs.z - this.z * rhs.y,
                             this.z * rhs.x - this.x * rhs.z,
                             this.x * rhs.y - this.y * rhs.x);
        }

        float mag() {
            return (int) Math.sqrt(x * x + y * y + z * z);
        }

        float dot(Vec3i rhs) {
            return this.x * rhs.x + this.y * rhs.y + this.z * rhs.z;
        }

        @Override
        public String toString() {
            return "V3i[ " + x + ", " + y + ", " + z + " ]";
        }

        int dim(int idx) {
            switch (idx) {
                case 0:
                    return this.x;
                case 1:
                    return this.y;
                case 2:
                    return this.z;
            }

            return 0;
        }

        void setAllDim(int xval, int yval, int zval) {
            this.x = xval;
            this.y = yval;
            this.z = zval;
        }

        void setDim(int idx, int val) {
            switch (idx) {
                case 0:
                    this.x = val;
                case 1:
                    this.y = val;
                case 2:
                    this.z = val;
            }
        }
    }

    static class Vec3f {
        float x, y, z;

        Vec3f(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        Vec3f() {
            this.x = 0;
            this.y = 0;
            this.z = 0;
        }

        Vec3f mul(Vec3f rhs) {
            return new Vec3f(x * rhs.x, y * rhs.y, z * rhs.z);
        }

        Vec3f mul(float scalar) {
            return new Vec3f(x * scalar, y * scalar, z * scalar);
        }

        Vec3f div(Vec3f rhs) {
            return new Vec3f(x / rhs.x, y / rhs.y, z / rhs.z);
        }

        Vec3f add(Vec3f rhs) {
            return new Vec3f(x + rhs.x, y + rhs.y, z + rhs.z);
        }

        Vec3f sub(Vec3f rhs) {
            return new Vec3f(x - rhs.x, y - rhs.y, z - rhs.z);
        }

        Vec3f normalize() {
            return this.mul(1 / this.mag());
        }

        Vec3i v3i() {
            return new Vec3i((int)(this.x+0.5), (int)(this.y+0.5), (int)(this.z+0.5));
        }

        Vec3f cross(Vec3f rhs) {
            return new Vec3f(this.y * rhs.z - this.z * rhs.y,
                             this.z * rhs.x - this.x * rhs.z,
                             this.x * rhs.y - this.y * rhs.x);
        }

        float mag() {
            return (float) Math.sqrt(x * x + y * y + z * z);
        }

        float dot(Vec3f rhs) {
            return this.x * rhs.x + this.y * rhs.y + this.z * rhs.z;
        }

        @Override
        public String toString() {
            return "V3f[ " + x + ", " + y + ", " + z + " ]";
        }

        void setDim(int idx, float val) {
            switch (idx) {
                case 0:
                    this.x = val;
                case 1:
                    this.y = val;
                case 2:
                    this.z = val;
            }
        }

        MatF mf41() {
            MatF result = new MatF(4,1);

            result.matrix[0][0] = this.x;
            result.matrix[1][0] = this.y;
            result.matrix[2][0] = this.z;
            result.matrix[3][0] = 1;

            return result;
        }

        void setAllDim(float xval, float yval, float zval) {
            this.x = xval;
            this.y = yval;
            this.z = zval;
        }

        float dim(int idx) {
            switch (idx) {
                case 0:
                    return this.x;
                case 1:
                    return this.y;
                case 2:
                    return this.z;
            }

            return 0;
        }
    }

    static class Vec4f {
        float x, y, z, t;

        Vec4f(float x, float y, float z, float t) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.t = t;
        }

        Vec4f() {
            this.x = 0;
            this.y = 0;
            this.z = 0;
            this.t = 0;
        }

        Vec4f mul(Vec4f rhs) {
            return new Vec4f(x * rhs.x, y * rhs.y, z * rhs.z, t * rhs.t);
        }

        Vec4f mul(float scalar) {
            return new Vec4f(x * scalar, y * scalar, z * scalar, t * scalar);
        }

        Vec4f div(Vec4f rhs) {
            return new Vec4f(x / rhs.x, y / rhs.y, z / rhs.z, t / rhs.t);
        }

        Vec4f add(Vec4f rhs) {
            return new Vec4f(x + rhs.x, y + rhs.y, z + rhs.z, t + rhs.t);
        }

        Vec4f sub(Vec4f rhs) {
            return new Vec4f(x - rhs.x, y - rhs.y, z - rhs.z, t - rhs.t);
        }

        float dim(int idx) {
            switch (idx) {
                case 0:
                    return this.x;
                case 1:
                    return this.y;
                case 2:
                    return this.z;
                case 3:
                    return this.t;
            }

            return 0;
        }

        void setDim(int idx, float val) {
            switch (idx) {
                case 0:
                    this.x = val;
                case 1:
                    this.y = val;
                case 2:
                    this.z = val;
                case 3:
                    this.t = val;
            }
        }

        void setAllDim(float xval, float yval, float zval, float tval) {
            this.x = xval;
            this.y = yval;
            this.z = zval;
            this.t = tval;
        }

        @Override
        public String toString() {
            return "V4f[ " + x + ", " + y + ", " + z + ", " + t + " ]";
        }
    }

//MATRICES//////////////////////////////////////////////////////////////////////////////////////////////////////////////
    static class MatF {
        // Screw getters and setters, direct access to these fields makes more sense here.
        float[][] matrix;
        int nCols, nRows;

        MatF(int nRowsIn, int nColsIn) {
            nRows = nRowsIn;
            nCols = nColsIn;
            matrix = new float[nRows][nCols];
        }

        MatF(MatF in) {
            nRows = in.nRows;
            nCols = in.nCols;
            matrix = new float[nRows][nCols];
            for (int i = 0; i < nRows; i++)
                for (int j = 0; j < nCols; j++)
                    matrix[i][j] = in.matrix[i][j];
        }

        static MatF identity(int dims) {
            MatF result = new MatF(dims, dims);

            for (int i = 0; i < dims; i++)
                for (int j = 0; j < dims; j++) 
                    result.matrix[i][j] = (i == j ? 1 : 0);

            return result;
        }

        MatF transpose() {
            MatF result = new MatF(this.nCols, this.nRows);

            for(int i = 0; i < result.nRows; i++)
                for(int j = 0; j < result.nCols; j++)
                    result.matrix[i][j] = matrix[j][i];

            return result; // may return null
        }

        // When we begin to call it thousands of times a second, performance starts to matter.
        // I should really look up better ways to multiply matrices. TODO: Optimized Mat44 multiplication method
        MatF mul(MatF matIn) {
            if (nCols != matIn.nRows)
                throw new IllegalArgumentException("Cannot multiply these matrices.");

            MatF result = new MatF(nRows, matIn.nCols);

            // O(n^3) complexity, efficiency at its finest.
            // Let's hope the JVM has good loop optimization...
            for (int i = 0; i < nRows; i++) {
                for (int j = 0; j < matIn.nCols; j++) {
                    result.matrix[i][j] = 0;
                    for (int k = 0; k < nCols; k++)
                        result.matrix[i][j] += matrix[i][k] * matIn.matrix[k][j];
                }
            }

            return result;
        }

        MatF mul(float scalar) {
            MatF result = new MatF(nRows, nCols);

            for (int j = 0; j < nCols; j++)
                for (int i = 0; i < nRows; i++)
                    result.matrix[i][j] = matrix[i][j] * scalar;

            return result;
        }

        static MatF luDecompose(MatF matIn, int[] perm, float tol) {
            if (matIn.nRows != matIn.nCols)
                throw new IllegalArgumentException("Must be a square matrix.");

            int i, j, k, imax,
                n = matIn.nRows;
            float maxA, absA;
            float[] tmpRow;

            if (perm.length != n+1)
                throw new IllegalArgumentException("Size of permutation matrix must be N+1.");

            for (i = 0; i <= n; i++) perm[i] = i;

            for (i = 0; i < n; i++) {
                maxA = (float) 0.0;
                imax = i;

                for (k = i; k < n; k++) {
                    if ((absA = Math.abs(matIn.matrix[k][i])) > maxA) {
                        maxA = absA;
                        imax = k;
                    }
                }

                if (maxA < tol)
                    throw new IllegalArgumentException("Matrix is degenerate.");

                if (imax != i) {
                    j = perm[i];
                    perm[i] = perm[imax];
                    perm[imax] = j;

                    tmpRow = matIn.matrix[i];
                    matIn.matrix[i] = matIn.matrix[imax];
                    matIn.matrix[imax] = tmpRow;
                    perm[n]++;
                }

                for (j = i+1; j < n; j++) {
                    matIn.matrix[j][i] /= matIn.matrix[i][i];
                    for (k=i+1; k < n; k++)
                        matIn.matrix[j][k] -= matIn.matrix[j][i]
                                          * matIn.matrix[i][k];
                }
            }

            return matIn;
        }

        static MatF inv(MatF matIn) {
            if (matIn.nRows != matIn.nCols)
                throw new IllegalArgumentException("Must be a square matrix.");

            int n = matIn.nRows;
            int[] perm = new int[n+1];

            MatF result = new MatF(n, n);
            matIn = luDecompose(matIn, perm, 0.000001f);

            for (int j = 0; j < n; j++) {
                for (int i = 0; i < n; i++) {
                    result.matrix[i][j] = perm[i] == j ? 1.0f : 0.0f;

                    for (int k = 0; k < i; k++)
                        result.matrix[i][j] -= matIn.matrix[i][k] * result.matrix[k][j];
                }

                for (int i = n-1; i >= 0; i--) {
                    for (int k = i+1; k < n; k++)
                        result.matrix[i][j] -= matIn.matrix[i][k] * result.matrix[k][j];

                    result.matrix[i][j] = result.matrix[i][j] / matIn.matrix[i][i];
                }
            }

            return result;
        }

        static float det(MatF matIn) {
            if (matIn.nRows != matIn.nCols)
                throw new IllegalArgumentException("Must be a square matrix.");
            
            int n = matIn.nRows;
            int[] perm = new int[n+1];
            
            matIn = luDecompose(matIn, perm, 0.000001f);
           
            float det = matIn.matrix[0][0];
            for (int i = 1; i < n; i++)
                det *= matIn.matrix[i][i];
            
            return ((perm[n]-n) % 2 == 0) ? det : -det;
        }

        Vec3f v3f() {
            Vec3f result = new Vec3f();

            try {
                result.setAllDim(matrix[0][0]/matrix[3][0],
                                 matrix[1][0]/matrix[3][0],
                                 matrix[2][0]/matrix[3][0]);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        public String toString() {
            StringBuilder matStr = new StringBuilder("MatF ");
            for (float[] row : matrix) {
                matStr.append("[ ");
                for (float el : row)
                    matStr.append(Float.toString(el)).append(" ");
                matStr.append("]\n     ");
            }

           return matStr.toString();
        }
    }
}