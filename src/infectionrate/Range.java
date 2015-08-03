package infectionrate;

public class Range {
    private int min;
    private int max;
    private int type;

    /**
     *
     * @param min - lower bound of the range.
     * @param max - upper bound of the range.
     * @param type - type of distribution (linear or Gaussian; default is linear)
     * @throws Exception 
     */
    public Range(int min, int max, int... type) throws Exception {
        this.min = min;
        this.max = max;
        if (type.length > 0) {
            this.type = type[0];
            if (type[0] == Constants.RANGE_GAUSSIAN) {
                throw new Exception();
            }
        } else {
            this.type = Constants.RANGE_LINEAR;
        }
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getType() {
        return type;
    }
}