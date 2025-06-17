package io.github.kyuubiran.ezxhelper.sample.data;

public class ExampleJavaClass {
    private static int staticField = 42;

    private String type;

    public String getType() {
        return type;
    }

    public ExampleJavaClass() {
        System.out.println("Constructor0 called");
        type = "empty";
    }

    public ExampleJavaClass(int a) {
        System.out.println("Constructor1(int) called with a: " + a);
        type = "int";
    }

    public ExampleJavaClass(long a) {
        System.out.println("Constructor1(long) called with a: " + a);
        type = "long";
    }

    private static String staticMethod(int i, CharSequence charSequence) {
        return "Static method called with i: " + i + ", charSequence: " + charSequence;
    }

    private int method1(int a, int b) {
        System.out.println("Method1(int) called with a: " + a + ", b: " + b);
        return 1;
    }

    private void method1(long a, long b) {
        System.out.println("Method1(long) called with a: " + a + ", b: " + b);
    }
}
