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

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidParameterException;

import gl.Geometry.*;
import gl.TGA.*;
//import gl.Vertex.*;
// FIXME: This class is a mess.
class Model {
//    VertexGroup vg;

    private ArrayList<Vec3f> vertices = new ArrayList<>();
    private ArrayList<Vec3f> norms = new ArrayList<>();
    private ArrayList<Vec2f> uv = new ArrayList<>();
    private ArrayList<ArrayList<Vec3i>> faces = new ArrayList<>(); // f idx_v/[idx_vt]/[idx_vn]/
    private TGAImage diffuseMap = new TGAImage();

    Model(String filename) {
        if (filename.endsWith(".obj")) loadObjModel(filename);
        else throw new InvalidParameterException("Bad filename.");
    }

    // TODO?? Generate vertex normals if nonexistent
    // TODO: VBO Indexing to reuse shared vertices
    // FIXME: use regex
    private void loadObjModel(String filename) {
        BufferedReader in = null;
        FileReader file = null;
        try {
            file = new FileReader(filename);
            in = new BufferedReader(file);
            String sCurrentLine;
            // FIXME: Maybe do an initial search to avoid appending empty lists (i.e. obj doesn't have vt) to faces.
            while ((sCurrentLine = in.readLine()) != null) {
                if (sCurrentLine.startsWith("v ")) { // Vertices
                    sCurrentLine = sCurrentLine.substring(2, sCurrentLine.length());
                    String[] vertices = sCurrentLine.split("\\s",3);
                    float[] vertex = new float[3];
                    for (int i = 0; i < vertices.length; i++) vertex[i] = Float.valueOf(vertices[i]);
                    this.vertices.add(new Vec3f(vertex[0],
                                                vertex[1],
                                                vertex[2]));
                } else if (sCurrentLine.startsWith("vt ")) { // UV (vertex texture coordinates)
                    sCurrentLine = sCurrentLine.substring(4, sCurrentLine.length());
                    String[] uvc = sCurrentLine.split("\\s",3);
                    uv.add(new Vec2f(Float.valueOf(uvc[0]),
                                     Float.valueOf(uvc[1])));
                } else if (sCurrentLine.startsWith("vn ")) { // Vertex Normals
                    sCurrentLine = sCurrentLine.substring(4, sCurrentLine.length());
                    String[] normals = sCurrentLine.split("\\s");
                    norms.add(new Vec3f(Float.valueOf(normals[0]),
                                        Float.valueOf(normals[1]),
                                        Float.valueOf(normals[2])));
                } else if (sCurrentLine.startsWith("f ")) { // Faces (v/vt/vn)
                    sCurrentLine = sCurrentLine.substring(2, sCurrentLine.length());
                    String[] allFaceParts = sCurrentLine.split("\\s");
                    String[][] faceParts = new String[3][3];
                    ArrayList<Vec3i> facePartsList = new ArrayList<>();
                    for (int i = 0; i < 3; i++) faceParts[i] = allFaceParts[i].split("/", 3);
                    for (int i = 0; i < 3; i++) facePartsList.add(new Vec3i(Integer.valueOf(faceParts[0][i]) - 1,
                                                                            Integer.valueOf(faceParts[1][i]) - 1,
                                                                            Integer.valueOf(faceParts[2][i]) - 1));
                    faces.add(facePartsList);
                }
            }
            loadTexture(filename,"_diffuse.tga", diffuseMap);
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) in.close();
                if (file != null) file.close();
                System.out.println("Successfully loaded model with " + this.nFaces() +
                                   " faces and " + this.nVertices() + " vertices.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Note: 'tex' has to be initialized prior to calling loadTexture.
    private void loadTexture(String filename, String suffix, TGAImage tex) {
        String texFile = filename.substring(0, filename.lastIndexOf(".obj")) + suffix;
        try {
            tex.readTGAFile(texFile);
            tex.flipVertically();
            System.out.println( "Loaded texture: "+ texFile);
        } catch (IOException | NullPointerException e) {
            System.out.println( "Failed to load: "+ texFile);
            e.printStackTrace();
        }
    }

    // Should be changed to getTexturePixel(int id, Vec2i uv) once we support more sophisticated materials.
    TGAColor getDiffuse(Vec2i uv) {
        return diffuseMap.get(uv.x, uv.y);
    }

    Vec2i getUV(int iFace, int nVert) {
        int idx =  faces.get(iFace).get(1).dim(nVert);
        return new Vec2f(uv.get(idx).x * diffuseMap.width,
                         uv.get(idx).y * diffuseMap.height).toVec2i();
    }

    int nVertices() { return vertices.size(); }

    int nFaces() { return faces.size(); }

    Vec3f vertex(int idx) { return vertices.get(idx); }

    Vec3f norm(int idx) { return norms.get(idx); }

    int[] getFaceVertexIndices(int idx) {
        return new int[] { faces.get(idx).get(2).dim(0),
                           faces.get(idx).get(2).dim(1),
                           faces.get(idx).get(2).dim(2) };
    }
}
