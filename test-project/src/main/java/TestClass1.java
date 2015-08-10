import java.awt.*;
import java.util.Collection;

/**
 * Created by marodrig on 26/03/2015.
 */
public class TestClass1 {

    private String field1 = "yes";

    public String foo() {
        String localVar1 = "Hello";

        if (field1.endsWith(localVar1)) {
            localVar1 = "WithBlock";
        }

        if (field1.endsWith(localVar1)) localVar1 = "Without block";


        AbstractClass1 a;
        a = new ConcreteClass();

        if (a != null) return localVar1;
        else return "F";
    }

}
