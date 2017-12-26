package gl;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

// This is probably the jankiest piece of code I've ever written in Java (that's not saying much).
// I feel dirty just looking at it...Thanks crappy Java generics.
class Vertex {
    // EXAMPLE: vtxGroupSize = 3, attrKeys = {"coords", "norms"}, types = {Vec3i.class, Vec3f.class}
    // VALUES: Vec3i[c1-1,c1-2,c1-3, c2-1,c2-2,c2-3], Vec3f[n1-1,n1-2,n1-3, n2-1,n2-2,n2-3]
    // KEYS:        |------------coords------------|       |-------------norms------------|
    // TYPES:       |------------Vec3i-------------|       |-------------Vec3f------------|
    // GROUPS:      {------g1------}  {------g2----}       {------g1-----} {------g2------}
    //               ^-_______________^___________________-^               ^
    //                                |____vtxGroupSize*attrKeys.length____|
    //                   * [c2,n2]
    //                  / \
    //                 /   \
    //                /     \
    //       [c1,n1] *-------* [c3,n3]

    static class VertexGroup {
        private String[] attrKeys;
        private int nAttrs, vtxGroupSize;
        private HashMap<String, ArrayList<Object>> vertexAttributes;
        private HashMap<String, Class<?>> expectedAttrTypes;

        VertexGroup(int vtxGroupSizeIn, String[] attrKeysIn, Class<?>[] types) {
            if (attrKeysIn.length != types.length)
                throw new IllegalArgumentException("Length of types and attrKeys must be equal.");

            attrKeys = attrKeysIn;
            nAttrs = attrKeys.length;
            vtxGroupSize = vtxGroupSizeIn;

            vertexAttributes = new HashMap<>();
            expectedAttrTypes = new HashMap<>();

            for (int i = 0; i < attrKeys.length; i++) {
                vertexAttributes.put(attrKeys[i], new ArrayList<>());
                expectedAttrTypes.put(attrKeys[i], types[i]);
            }
        }

        <T> T[] getVertexAttrGroup(String key, int idx, Class<T> cls) {
            int grpSrtIdx = idx * vtxGroupSize,
                grpEndIdx = grpSrtIdx + (vtxGroupSize - 1);

            if (!vertexAttributes.containsKey(key))
                throw new IllegalArgumentException("Given attrKey does not exist.");
            if (vertexAttributes.get(key).size() < grpEndIdx + 1 || grpSrtIdx < 0)
                throw new IllegalArgumentException("Invalid index.");
            if (!cls.equals(expectedAttrTypes.get(key)))
                throw new IllegalArgumentException("Invalid expectedAttrType.");

            // ***HERE BE DRAGONS***
            T[] result = (T[]) Array.newInstance(cls, vtxGroupSize);
            for (int i = grpSrtIdx; i < grpEndIdx + 1; i++)
                result[i-grpSrtIdx] = (T) vertexAttributes.get(key).get(i);

            return result;
        }

        void addToAttrGroup(String key, Object[] attrGroupIn) {
            if (vertexAttributes.containsKey(key))
                throw new IllegalArgumentException("Given attrKey does not exist.");
            if (attrGroupIn.length != vtxGroupSize)
                throw new IllegalArgumentException("attrGroupIn must be same the length as vtxGroupSize.");
            Class<?> type = expectedAttrTypes.get(key);

            for (int i = 0; i < attrGroupIn.length; i++) {
                Object attrGroupInEl = attrGroupIn[i];
                if (!attrGroupInEl.getClass().equals(expectedAttrTypes.get(key)))
                    throw new IllegalArgumentException("Invalid expectedAttrType in given attrGroup at "+ i +".");
                vertexAttributes.get(key).add(attrGroupInEl);
            }

            // sanity check
            if (vertexAttributes.get(key).size() % vtxGroupSize != 0)
                System.out.println("Something went horribly wrong.");
        }
    }
}
