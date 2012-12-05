package r.data.internal;

import r.*;
import r.Convert.NAIntroduced;
import r.data.*;


public final class ScalarDoubleImpl extends ArrayImpl implements RDouble {

    double value;

    public ScalarDoubleImpl(double value) {
        this.value = value;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public Object get(int i) {
        Utils.check(i == 0);
        return  get();
    }

    public Object get() {
        return value;
    }

    @Override
    public RAny boxedGet(int i) {
        Utils.check(i == 0);
        return boxedGet();
    }

    public RAny boxedGet() {
        return this;
    }

    @Override
    public RArray set(int i, Object val) {
        Utils.check(i == 0);
        return set(val);
    }

    public RArray set(Object val) {
        return set(((Double) val).doubleValue());
    }

    @Override
    public RDouble setDimensions(int[] dimensions) {
        return RDouble.RDoubleFactory.getFor(new double[] {value}, dimensions);
    }

    @Override
    public boolean isNAorNaN(int i) {
        Utils.check(i == 0);
        return isNAorNaN();
    }

    public boolean isNAorNaN() {
        return RDouble.RDoubleUtils.isNAorNaN(value);
    }

    public boolean isNA() {
        return RDouble.RDoubleUtils.isNA(value);
    }

    @Override
    public String pretty() {
        return Convert.pretty(Convert.double2string(value));
    }

    @Override
    public RLogical asLogical() {
        return RLogical.RLogicalFactory.getScalar(Convert.double2logical(value));
    }

    @Override
    public RLogical asLogical(NAIntroduced naIntroduced) {
        return asLogical();
    }

    @Override
    public RInt asInt() {
        return RInt.RIntFactory.getScalar(Convert.double2int(value));
    }

    @Override
    public RInt asInt(NAIntroduced naIntroduced) {
        return RInt.RIntFactory.getScalar(Convert.double2int(value, naIntroduced));
    }

    @Override
    public RDouble asDouble() {
        return this;
    }

    @Override
    public RDouble asDouble(NAIntroduced naIntroduced) {
        return this;
    }

    @Override
    public ScalarDoubleImpl materialize() {
        return this;
    }

    @Override
    public RString asString() {
        return RString.RStringFactory.getScalar(Convert.double2string(value));
    }

    @Override
    public RString asString(NAIntroduced naIntroduced) {
        return asString();
    }

    @Override
    public RDouble set(int i, double val) {
        Utils.check(i == 0);
        return set(val);
    }

    public RDouble set(double val) {
        value = val;
        return this;
    }

    @Override
    public double getDouble(int i) {
        Utils.check(i == 0);
        return getDouble();
    }

    public double getDouble() {
        return value;
    }
}
