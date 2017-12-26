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

import java.io.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import gl.Geometry.*;
import gl.TGA.*;

/***********************************************************************************************************************
 *  A software 3D renderer based on 'tinyrenderer' by Dmitry V. Sokolov which is an  OpenGL clone used for a
 *  3D graphics course. I wanted to try and apply the things I learned, challenge myself to implement it in
 *  Java (cuz I'm crazy), then extend it as an exercise in programming.
 ***********************************************************************************************************************
 *  TODO: Major refactoring
 *  TODO: Scene class
 *  TODO: Base 3D object class from which camera, light, model etc. inherit from
 *  TODO: Better memory management
 *  TODO: Multithreading (a few janky portions implemented)
 *  TODO: Handle non-square aspect ratios (easy, I'm just lazy)
 *  TODO: Shader class
 *  TODO: Material class (store shaders and texture maps)
 *  TODO: View frustrum culling
 *  TODO: Vertex attributes (working on better encoding)
 *  TODO: Cubemapping (for reflections, omni light shadow maps etc.)
 *  TODO: Antialiasing (multisampling, supersampling)
 *  TODO: Deferred shading (GBuffers and whatnot) -> implemented as shader
 *  TODO: Euler and quaternion rotations
 *  TODO: Mipmapping
 *  TODO: Raycasting
 *  TODO: Nearest neighbor (DONE), linear, and bilinear filtering
 *  TODO: Order independent transparency
 *  TODO: More image blending functions
 *  TODO: Gamma correction, color spaces, support for more bits per pixel
 **********************************************************************************************************************/

public class GL {
    private static final double EULER = 2.718281828459045;

    /**
     * Generate a viewport matrix
     * [ w/2   0     0    x+w/2 |
     * |  0   h/2    0    y+h/2 |
     * |  0    0   f-n/2  f-n/2 |
     * |  0    0     0      1   ]
     */
    private static MatF viewport(int x, int y,
                                 int w, int h,
                                 int n, int f) {
        MatF m = MatF.identity(4);

        m.matrix[0][3] = x + w/2;
        m.matrix[1][3] = y + h/2;
        m.matrix[2][3] = f-n/2;

        m.matrix[0][0] = w/2;
        m.matrix[1][1] = h/2;
        m.matrix[2][2] = f-n/2;

        return m;
    }

    /** 
     *  [x|   [O'x|       [x'|    [x'|           ([x|   [O'x|)
     *  |y| = |O'y| + M * |y'| => |y'| = M_inv * (|x| - |O'x|)
     *  |z]   |O'z]       |z']    |z']           (|x]   |O'x])
     */
    private static MatF lookAt(Vec3f eye, Vec3f center, Vec3f up) {
        Vec3f z = (eye.sub(center)).normalize();
        Vec3f x = up.cross(z).normalize();
        Vec3f y = z.cross(x).normalize();

        MatF invM = MatF.identity(4);
        MatF transM = MatF.identity(4);

        // Create the change of basis matrix
        for (int i = 0; i < 3; i++) {
            invM.matrix[0][i] = x.dim(i);
            invM.matrix[1][i] = y.dim(i);
            invM.matrix[2][i] = z.dim(i);
            transM.matrix[i][3] = - center.dim(i);
        }

        return invM.mul(transM);
    }

    private static TGAImage createImage(int width, int height, TGAColor bg) {
        TGAImage img = new TGAImage(width, height, 3);

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                img.set(i, j, bg);
            }
        }

        return img;
    }

    private static void saveImage(TGAImage img, String filename, boolean flip) {
        try {
            if (flip) img.flipVertically();
            img.writeTGAFile(filename, false);
            System.out.println(filename + " has been saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Implement a more robust line drawing algorithm.
    private void line(Vec2i p0, Vec2i p1, TGAImage img, TGAColor color) {
        boolean isSteep = false;

        if (Math.abs(p0.x - p1.x) < Math.abs(p0.y - p1.y)) {
            p0 = new Vec2i(p0.y, p0.x);
            p1 = new Vec2i(p1.y, p1.x);
            isSteep = true;
        }

        if (p0.x > p1.x) {
            Vec2i pTemp = p0;
            p0 = p1;
            p1 = pTemp;
        }

        int dx = p1.x - p0.x, dy = p1.y - p0.y,
            dError2 = Math.abs(dy) * 2,
            error2 = 0,
            y = p0.y;

        for (int x = p0.x; x <= p1.x; x++) {
            if (isSteep) img.set(y, x, color);
            else img.set(x, y, color);
            error2 += dError2;
            if (error2 > dx) {
                y += (p1.y > p0.y ? 1 : -1);
                error2 -= dx * 2;
            }
        }
    }

    // To prevent every call from creating unnecessary instances, we keep these everything as private fields.
    private static class Triangle {
        private Vec3f sx = new Vec3f(0, 0, 0),
                      sy = new Vec3f(0, 0, 0);

        private void barycentric(Vec3i[] pts, Vec3i P, Vec3f bc) {
            sy.setAllDim(pts[2].y - pts[0].y,
                         pts[1].y - pts[0].y,
                         pts[0].y - P.y);
            sx.setAllDim(pts[2].x - pts[0].x,
                         pts[1].x - pts[0].x,
                         pts[0].x - P.x);

            Vec3f su = sx.cross(sy);
            if (Math.abs(su.z) > 1e-2)
                bc.setAllDim((1.f-(su.x+su.y)/su.z),su.y/su.z,su.x/su.z);
            else bc.setAllDim(-1, 1, 1); // Outside of triangle, discard.
        }

        // Need to parallelize this too, imagine a getFaceVertexIndices that covers a large portion of the viewport
        // (like a wall) then all rendering suddenly becomes sequential! Maybe split scanlines between threads and
        // parallelize if the triangle bounding box covers a certain percentage of the screen to overcome cost
        // of spawning threads.
        private Vec2i bboxMin = new Vec2i(0, 0),
                      bboxMax = new Vec2i(0, 0),
                      bboxLim = new Vec2i(0, 0);
        private Vec3i currPoint = new Vec3i(0, 0, 0);
        private Vec3f bcScreen = new Vec3f(0, 0, 0);
        private TGAColor clr;

        private void triangle(Vec3i[] pts,
                              TGAImage img,
                              Shader inputShader) {
            bboxMin.setAllDim(Integer.MAX_VALUE, Integer.MIN_VALUE);
            bboxMax.setAllDim(Integer.MAX_VALUE, Integer.MIN_VALUE);
            bboxLim.setAllDim(img.width - 1, img.height - 1);

            for (int i = 0; i < 3; i++) {
                bboxMin.setAllDim(Math.max(0, Math.min(bboxMin.x, pts[i].x)),
                        Math.max(0, Math.min(bboxMin.y, pts[i].y)));
                bboxMax.setAllDim(Math.min(bboxLim.x, Math.max(bboxMax.x, pts[i].x)),
                        Math.min(bboxLim.y, Math.max(bboxMax.y, pts[i].y)));
            }

            for (currPoint.x = bboxMin.x; currPoint.x <= bboxMax.x; currPoint.x++) {
                for (currPoint.y = bboxMin.y; currPoint.y <= bboxMax.y; currPoint.y++) {
                    // Check if point lies in triangle[pts[1], pts[2], pts[3]] using the barycentric coordinate system.
                    barycentric(pts, currPoint, bcScreen);
                    clr = inputShader.fragment(currPoint, bcScreen);
                    if (clr == null) continue;
                    img.set(currPoint.x, currPoint.y, clr);
                } // end for y
            } // end for x
        } // end triangle
    }

    private void wireframe(Model m, TGAImage img, TGAColor color) {
        int x0, y0, x1, y1;
        int[] face;
        Vec3f v0, v1;

        for (int i = 0; i < m.nFaces(); i++) {
            face = m.getFaceVertexIndices(i);

            for (int j = 0; j < 3; j++) {
                v0 = m.vertex(face[j]);
                v1 = m.vertex(face[(j + 1) % 3]);

                x0 = (int) ((v0.x + 1.) * (img.width / 2.));
                y0 = (int) ((v0.y + 1.) * (img.height / 2.));
                x1 = (int) ((v1.x + 1.) * (img.width / 2.));
                y1 = (int) ((v1.y + 1.) * (img.width / 2.));

                try {
                    line(new Vec2i(x0, y0), new Vec2i(x1, y1), img, color);
                } catch (ArrayIndexOutOfBoundsException
                        | IllegalArgumentException ignored) {}
            } // end for j
        } // end for i
    } 

    // When the Scene class is completed, we should pass that instead of a single 
    // model (with all model coords already transformed into world/scene coords).
    private void renderScene(Model model, TGAImage img) {
        short[] zbuffer = new short[img.width*img.height];
//        zbuffer = new AtomicFloatArray(img.width*img.height);
        for (short s : zbuffer) s = Short.MIN_VALUE; // signed 2s complement byte equiv 0 unsigned char
//        AtomicFloatArray zbuffer;
//        zbuffer = new AtomicFloatArray(img.width*img.height);
//        zbuffer.initValAll(-Float.MAX_VALUE);
        // We assign each thread an equal number of faces, split the workload among them.
        // Should implement work stealing.
        // https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Executors.html#newWorkStealingPool
        final int processors =  Runtime.getRuntime().availableProcessors(),
                  facesToRender = model.nFaces()/processors;

        AtomicInteger thrNum = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(processors);

        Runnable renderThread = () -> {
            final int tn = thrNum.incrementAndGet();
            int endIdx = tn*facesToRender, startIdx = endIdx-facesToRender;
            System.out.println("Thread: "+tn+" | Faces: "+startIdx+"->"+endIdx);

            GauroudShader gr = new GauroudShader(img, model, zbuffer);

            try {
                for (int i = startIdx; i < endIdx; i++) gr.vertex(i);
            } catch (Exception e) {
                e.printStackTrace();
                latch.countDown();
                return;
            }

            latch.countDown();
            System.out.println("Rendering on thread "+tn+" done.");

        };

        // Fire up all threads.
        for (int i = 1; i <= processors; i++) {
            Thread thread = new Thread(renderThread);
            thread.start();
        }

        // Wait for all threads to finish.
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void exec() {
        int width = 800, height = 800;
        long start, end, totmulti;
        TGAImage img  = createImage(width, height, TGA.BLACK);
        Model m = new Model(System.getProperty("user.dir") + "/gl/src/gl/obj/d3.obj");
        
        start = System.currentTimeMillis();
        renderScene(m, img);
        end = System.currentTimeMillis();
        
//        saveImage(img[0], "gbuffer_multi.tga", true);
        saveImage(img, "dbuffer_multi.tga", true);
//        saveImage(img[2], "zbuffer_multi.tga", true);
        
        totmulti = end-start;
        System.out.println("Render Time[ms]: "+totmulti);
    }

    // Example shader pipeline: deferred shading
    // (0) ZBuffer ->(1) GBuffer ->(2) Diffuse ->(3) Specular ->(4) Reflect ->(5) Shadow-mapping ->(6) SSAO
    // ^0-4 all have same vertexShader (project viewport, transform scene, etc.)

    class GauroudShader implements Shader {
        private int[] faceVertexIndices;
        TGAImage img;
        Model model;
        volatile short[] zbuffer;
        private Triangle innerFrag = new Triangle();
        private int w, h, maxDepth = 255, minDepth = 0;
        private float fov = -1; // [Rads]

        private Vec3f  lightDir = new Vec3f(-1,0,-1), // directional
                       camera = new Vec3f(0, -1,3),
                       center = new Vec3f(0, 0,0),
                       up = new Vec3f(0, 1,0);

        private MatF Projection, ViewPort, ModelView;

        // No need to recompute these as long as the camera stays put
        // ^^ obviously not a realtime renderer :(
        private MatF vertexTransform, vtcpy, normalTransform;

        // input buffers
        private Vec3i[] screenCoords = { new Vec3i(0,0,0),
                                         new Vec3i(0,0,0),
                                         new Vec3i(0,0,0) };

        private Vec3f[] normCoords = { new Vec3f(0,0,0),
                                       new Vec3f(0,0,0),
                                       new Vec3f(0,0,0) };

        private Vec2i[] uv = new Vec2i[3];
        private Vec3f v, vn;

        private float[] ctints = new float[3];

        GauroudShader(TGAImage imgIn, Model modelIn, short[] zbufferIn) {
            img = imgIn;
            model = modelIn;
            zbuffer = zbufferIn;
            w = imgIn.width;
            h = imgIn.height;
            Projection = MatF.identity(4);
            Projection.matrix[3][2] = fov/(camera.sub(center).mag());
            ViewPort = viewport(w/8, h/8, w*3/4, h*3/4, minDepth, maxDepth);
            ModelView = lookAt(camera, center, up);
            vtcpy = new MatF(Projection.mul(ModelView));
            normalTransform = (MatF.inv(vtcpy)).transpose();
            vertexTransform = ViewPort.mul(Projection).mul(ModelView);
            lightDir = normalTransform.mul(lightDir.mf41()).v3f().normalize();
        }

        public void vertex(int faceIndex) {
            // Vertex Shader
            faceVertexIndices = model.getFaceVertexIndices(faceIndex);
            for (int j = 0; j < 3; j++) {
                v = model.vertex(faceVertexIndices[j]);
                vn = model.norm(faceVertexIndices[j]);
                // FULL COORDINATE TRANSFORMATION CHAIN:
                //      ScreenCoord = Viewport * Projection * View * Model * ModelVertex
                screenCoords[j] = vertexTransform.mul(v.mf41()).v3f().v3i();
                normCoords[j] = normalTransform.mul(vn.mf41()).v3f().normalize();
            }

            for (int j = 0; j < normCoords.length; j++)
                ctints[j] = -(normCoords[j].dot(lightDir)*2);

            for (int k = 0; k < 3; k++)
                uv[k] = model.getUV(faceIndex, k);

            innerFrag.triangle(screenCoords, img, this);
        }

        private TGAColor toSet = TGA.BLACK;
        private float cpint;
        private Vec2i uvpos = new Vec2i();

        public TGAColor fragment(Vec3i currPoint, Vec3f bcScreen) {
            if (bcScreen.x < 0 || bcScreen.y < 0 || bcScreen.z < 0) return null;

            currPoint.z = 0;
            for (int i = 0; i < 3; i++)
                currPoint.z += screenCoords[i].z * bcScreen.dim(i);

            uvpos = (uv[0].mul(bcScreen.x))
                .add(uv[1].mul(bcScreen.y))
                .add(uv[2].mul(bcScreen.z));

            // Gauroud Shading:
            // linear interpolation between light intensities at each face' vertex normals
            cpint = (ctints[0] * bcScreen.x)
                  + (ctints[1] * bcScreen.y)
                  + (ctints[2] * bcScreen.z);

            if (zbuffer[currPoint.x + currPoint.y * w] < currPoint.z) {
                zbuffer[currPoint.x + currPoint.y * w] = (short) currPoint.z;
                toSet = model.getDiffuse(uvpos).multiply(cpint);
                return toSet;
            }

            return null;
        }

    }

    private static float sigmoid(float t) {
        return (float) (1 / (1 + Math.pow(EULER, -t)));
    }

    public static void main(String[] args) {

       new GL().exec();
        // ColorBits clr = new ColorBits(DataMode.rgb,8);
        // int[] intary = new int[]{123,456,789};
        // clr.setFromIntAry(intary);
        // intary = clr.getAsIntAry();
        // for (int i : intary) System.out.println(i);
    }
}