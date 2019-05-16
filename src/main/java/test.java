public class test {
    D[] nums = new D[]{ };
    double e = 0.5;

    public void test() {
        D f = getD();

        if (f == D.X) {
            System.out.println("x");
        }
    }

    public D getD() {
        return D.F;
    }

    enum D {
        F, X
    }
}
