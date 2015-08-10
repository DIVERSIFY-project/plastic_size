package fr.inria.defuse.spoon.processors.dataflow;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLoop;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marodrig on 01/04/2015.
 */
public abstract class DefuseTupleBuilder<E extends CtElement> extends AbstractProcessor<E> {

    private List<DefuseTuple> tuples;

    /**
     * Find how deep in the control flow is an element with respect to a method declaration
     * <p/>
     * For example:
     * <p/>
     * void foo()
     * a = 0; <- Level 0
     * if ( a ... ) <- Level 1
     * a = ... <- Level 2
     * while (...)
     * read(a) <- Level 2
     * a = ... <- Level 0 again
     *
     * @param e Element to calculate
     * @return Depth of the element, method is 0
     */
    public int level(E e) {
        int result = -1;
        CtElement p = e.getParent();
        while (!(p == null || p instanceof CtClass)) {
            if (p instanceof CtExecutable || p instanceof CtIf || p instanceof CtLoop || p instanceof CtBlock)
                result++;

            CtElement parent = p.getParent();
            //Special cases when we have a loop or if with a single statement
            if (!(p instanceof CtBlock) && parent != null) {
                if (parent instanceof CtIf) {
                    CtIf ctIf = (CtIf) parent;
                    if (ctIf.getThenStatement().equals(p)) result++;
                    else if (ctIf.getElseStatement() != null && ctIf.getElseStatement().equals(p)) result++;
                } else if (parent instanceof CtLoop) {
                    CtLoop loop = (CtLoop) parent;
                    if (loop.getBody().equals(p)) result++;
                }
            }
            p = parent;
        }
        return result;
    }

    protected void newTuple(String varName, E e, DefuseTuple.Access access) {
        getTuples().add(buildTuple(varName, e, access));
    }

    /**
     * Builds a tuple from an element
     *
     * @param varName Name fo the variable
     * @param e       Element to build tuple from
     * @param access  Write/Read Access of the element
     * @return
     */
    protected DefuseTuple buildTuple(String varName, E e, DefuseTuple.Access access) {
        DefuseTuple result = new DefuseTuple();
        result.setName(varName);
        result.setAccess(access);
        result.setLevel(level(e));
        if (!varName.equals("STATEMENT")) {
            try {
                result.setContext(e.toString());
            } catch (Exception ex) {
                result.setContext(e.getSignature());
            }
        } else result.setContext("ST");
        result.setUnitName(e.getPosition().getCompilationUnit().getMainType().getQualifiedName());
        result.setLine(e.getPosition().getLine());
        CtExecutable executable = e.getParent(CtExecutable.class);
        if (executable != null) result.setMethod(executable.getSignature());
        else result.setMethod("None");
        return result;
    }

    public List<DefuseTuple> getTuples() {
        if (tuples == null) tuples = new ArrayList<>();
        return tuples;
    }

}
