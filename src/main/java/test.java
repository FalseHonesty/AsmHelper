public class test {
    D[] nums = new D[]{ };
    double e = 0.5;

    public void test() {
        D f = getD();
        D z = getD();

        if (f == D.X && z == D.X) {
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
