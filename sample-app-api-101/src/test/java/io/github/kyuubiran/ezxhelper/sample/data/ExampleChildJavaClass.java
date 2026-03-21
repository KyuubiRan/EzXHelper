package io.github.kyuubiran.ezxhelper.sample.data;

public class ExampleChildJavaClass extends ExampleJavaClass {
    public ExampleChildJavaClass() {
        super();
        type = "child_empty";
    }

    public int childMethod(int a, int b) {
        System.out.println("Child method called with a: " + a + ", b: " + b);
        return a + b;
    }
}
