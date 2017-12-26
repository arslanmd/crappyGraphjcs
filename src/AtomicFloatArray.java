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
