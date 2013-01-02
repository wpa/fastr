package r.data;

import java.util.*;

import r.*;
import r.Convert.*;
import r.data.internal.*;

public interface RComplex extends RArray {
    String TYPE_STRING = "complex";

    ComplexImpl EMPTY = (ComplexImpl) RArrayUtils.markShared(RComplexFactory.getUninitializedArray(0));
    ScalarComplexImpl BOXED_NA = (ScalarComplexImpl) RArrayUtils.markShared(RComplexFactory.getScalar(RDouble.NA, RDouble.NA));
    Complex COMPLEX_BOXED_NA = new Complex(RDouble.NA, RDouble.NA);
    ScalarComplexImpl BOXED_ZERO = (ScalarComplexImpl) RArrayUtils.markShared(RComplexFactory.getScalar(0, 0));

    ComplexImpl EMPTY_NAMED_NA = (ComplexImpl) RArrayUtils.markShared(RComplexFactory.getFor(new double[] {}, null, Names.create(new RSymbol[] {RSymbol.NA_SYMBOL})));
    ComplexImpl NA_NAMED_NA = (ComplexImpl) RArrayUtils.markShared(RComplexFactory.getFor(new double[] {RDouble.NA, RDouble.NA}, null, Names.create(new RSymbol[] {RSymbol.NA_SYMBOL})));

    RComplex set(int i, double real, double imag);
    double getReal(int i);
    double getImag(int i);
    RComplex materialize();

    public final class Complex {
        public static final Complex NA = new Complex(RDouble.NA, RDouble.NA);

        private final double real;
        private final double imag;

        public Complex(double real, double imag) {
            this.real = real;
            this.imag = imag;
        }

        public double realValue() {
            return real;
        }

        public double imagValue() {
            return imag;
        }
    }

    public class RComplexUtils {
        public static boolean eitherIsNA(double real, double imag) {
            return RDouble.RDoubleUtils.isNA(real) || RDouble.RDoubleUtils.isNA(imag);
        }
        public static boolean eitherIsNAorNaN(double real, double imag) {
            return RDouble.RDoubleUtils.isNAorNaN(real) ||  RDouble.RDoubleUtils.isNAorNaN(imag);
        }
        public static RDouble complex2double(RComplex value, ConversionStatus warn) { // eager to keep error semantics eager
            int size = value.size();
            double[] content = new double[size];
            for (int i = 0; i < size; i++) {
                double real = value.getReal(i);
                double imag = value.getImag(i);
                content[i] = Convert.complex2double(real, imag, warn);
            }
            return RDouble.RDoubleFactory.getFor(content, value.dimensions(), value.names());
        }
        public static RInt complex2int(RComplex value, ConversionStatus warn) { // eager to keep error semantics eager
            int size = value.size();
            int[] content = new int[size];
            for (int i = 0; i < size; i++) {
                double real = value.getReal(i);
                double imag = value.getImag(i);
                content[i] = Convert.complex2int(real, imag, warn);
            }
            return RInt.RIntFactory.getFor(content, value.dimensions(), value.names());
        }
        public static RRaw complexToRaw(RComplex value, ConversionStatus warn) { // eager to keep error semantics eager
            int size = value.size();
            byte[] content = new byte[size];
            for (int i = 0; i < size; i++) {
                double real = value.getReal(i);
                double imag = value.getImag(i);
                content[i] = Convert.complex2raw(real, imag, warn);
            }
            return RRaw.RRawFactory.getFor(content, value.dimensions(), value.names());
        }
    }

    public class RComplexFactory {
        public static ScalarComplexImpl getScalar(double real, double imag) {
            return new ScalarComplexImpl(real, imag);
        }
        public static ScalarComplexImpl getScalar(Complex value) {
            return new ScalarComplexImpl(value.realValue(), value.imagValue());
        }
        public static RComplex getScalar(double real, double imag, int[] dimensions) {
            if (dimensions == null) {
                return new ScalarComplexImpl(real, imag);
            } else {
                return getFor(new double[] {real, imag}, dimensions, null);
            }
        }
        public static RComplex getArray(double... values) {
            if (values.length == 2) {
                return new ScalarComplexImpl(values[0], values[1]);
            }
            return new ComplexImpl(values);
        }
        public static RComplex getArray(double[] values, int[] dimensions) {
            if (dimensions == null && values.length == 2) {
                return new ScalarComplexImpl(values[0], values[1]);
            }
            return new ComplexImpl(values, dimensions);
        }
        public static RComplex getArray(double[] realValues, double[] imagValues, int[] dimensions) {
            if (dimensions == null && realValues.length == 1) {
                return new ScalarComplexImpl(realValues[0], imagValues[0]);
            }
            return new ComplexImpl(realValues, imagValues, dimensions);
        }
        public static RComplex getUninitializedArray(int size) {
            if (size == 1) {
                return new ScalarComplexImpl(0, 0);
            }
            return new ComplexImpl(size);
        }
        public static RComplex getUninitializedNonScalarArray(int size) {
            return new ComplexImpl(size);
        }
        public static RComplex getUninitializedArray(int size, int[] dimensions) {
            if (size == 1 && dimensions == null) {
                return new ScalarComplexImpl(0, 0);
            }
            return new ComplexImpl(new double[2 * size], dimensions, null, false);
        }
        public static RComplex getNAArray(int size) {
            return getNAArray(size, null);
        }
        public static RComplex getNAArray(int size, int[] dimensions) {
            if (size == 1 && dimensions == null) {
                return BOXED_NA;
            }
            double[] content = new double[2 * size];
            Arrays.fill(content, RDouble.NA);
            return new ComplexImpl(content, dimensions, null, false);
        }
        public static ComplexImpl getMatrixFor(double[] values, int m, int n) {
            return new ComplexImpl(values, new int[] {m, n}, null, false);
        }
        public static RComplex copy(RComplex c) {
            if (c.size() == 1 && c.dimensions() == null && c.names() == null) {  // FIXME: rely instead on scalarization ?
                return new ScalarComplexImpl(c.getReal(0), c.getImag(0));
            }
            return new ComplexImpl(c, false);
        }
        public static RComplex strip(RComplex v) {
            if (v.size() == 1) {
                return new ScalarComplexImpl(v.getReal(0), v.getImag(0));
            }
            return new ComplexImpl(v, true);
        }
        public static RComplex getFor(double[] values) { // re-uses values!
            return getFor(values, null, null);
        }
        public static RComplex getFor(double[] values, int[] dimensions, Names names) {  // re-uses values!
            if (values.length == 2 && dimensions == null && names == null) {
                return new ScalarComplexImpl(values[0], values[1]);
            }
            return new ComplexImpl(values, dimensions, names, false);
        }
        public static RComplex getEmpty(boolean named) {
            return named ? EMPTY_NAMED_NA : EMPTY;
        }
        public static RComplex getNA(boolean named) {
            return named ? NA_NAMED_NA : BOXED_NA;
        }
        public static RComplex exclude(int excludeIndex, RComplex orig) {
            Names names = orig.names();
            if (names == null) {
                return new RComplexExclusion(excludeIndex, orig);
            }
            int size = orig.size();
            int nsize = size - 1;
            double[] content = new double[2 * nsize];
            for (int i = 0; i < excludeIndex; i++) {
                content[2 * i] = orig.getReal(i);
                content[2 * i + 1] = orig.getImag(i);
            }
            for (int i = excludeIndex; i < nsize; i++) {
                content[2 * i] = orig.getReal(i + 1);
                content[2 * i + 1] = orig.getImag(i + 1);
            }
            return RComplexFactory.getFor(content, null, names.exclude(excludeIndex));
        }
        public static RComplex subset(RComplex value, RInt index) {
            return new RComplexSubset(value, index);
        }
    }

    public static class RStringView extends View.RStringProxy<RComplex> implements RString {

        public RStringView(RComplex orig) {
            super(orig);
        }

        @Override
        public RComplex asComplex() {
            return orig;
        }

        @Override
        public RComplex asComplex(ConversionStatus warn) {
            return orig;
        }

        public String getString(int i) {
            return Convert.complex2string(orig.getReal(i), orig.getImag(i));
        }
    }

    public static class RLogicalView extends View.RLogicalProxy<RComplex> implements RLogical {

        public RLogicalView(RComplex orig) {
            super(orig);
        }

        @Override
        public int getLogical(int i) {
            return Convert.complex2logical(orig.getReal(i), orig.getImag(i));
        }
    }

    public static class RComplexExclusion extends View.RComplexView implements RComplex {

        final RComplex orig;
        final int excludeIndex;
        final int size;

        public RComplexExclusion(int excludeIndex, RComplex orig) {
            this.orig = orig;
            this.excludeIndex = excludeIndex;
            this.size = orig.size() - 1;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public double getReal(int i) {
            Utils.check(i < size, "bounds check");
            Utils.check(i >= 0, "bounds check");

            if (i < excludeIndex) {
                return orig.getReal(i);
            } else {
                return orig.getReal(i + 1);
            }
        }

        @Override
        public double getImag(int i) {
            Utils.check(i < size, "bounds check");
            Utils.check(i >= 0, "bounds check");

            if (i < excludeIndex) {
                return orig.getImag(i);
            } else {
                return orig.getImag(i + 1);
            }
        }

        @Override
        public boolean isSharedReal() {
            return orig.isShared();
        }

        @Override
        public void ref() {
            orig.ref();
        }
    }

    // indexes must all be positive
    //   but can be out of bounds ==> NA's are returned in that case
    public static class RComplexSubset extends View.RComplexView implements RComplex {

        final RComplex value;
        final int vsize;
        final RInt index;
        final int isize;

        public RComplexSubset(RComplex value, RInt index) {
            this.value = value;
            this.index = index;
            this.isize = index.size();
            this.vsize = value.size();
        }

        @Override
        public int size() {
            return isize;
        }

        @Override
        public double getReal(int i) {
            int j = index.getInt(i);
            if (j > vsize) {
                return RDouble.NA;
            } else {
                return value.getReal(j - 1);
            }
        }

        @Override
        public double getImag(int i) {
            int j = index.getInt(i);
            if (j > vsize) {
                return RDouble.NA;
            } else {
                return value.getImag(j - 1);
            }
        }

        @Override
        public boolean isSharedReal() {
            return value.isShared() || index.isShared();
        }

        @Override
        public void ref() {
            value.ref();
            index.ref();
        }
    }

}