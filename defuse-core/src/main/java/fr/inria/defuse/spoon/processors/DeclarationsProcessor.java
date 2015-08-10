package fr.inria.defuse.spoon.processors;

import fr.inria.defuse.Chain;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.declaration.CtVariable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marodrig on 26/03/2015.
 */
public class DeclarationsProcessor extends AbstractProcessor<CtTypedElement> {

    List<Chain> chains;

    public DeclarationsProcessor() {
        chains = new ArrayList<Chain>();
    }

    @Override
    public void process(CtTypedElement element) {
        if (element instanceof CtVariable) {
            CtVariable v = (CtVariable) element;
            System.out.println("DEF: " + v.getReference().getSimpleName() + " VAR: " + v.getSimpleName());
        } else if (element instanceof CtAssignment) {
            CtAssignment a = (CtAssignment)element;
        } else if (element instanceof CtVariableAccess) {
            CtVariableAccess va = (CtVariableAccess) element;
            System.out.println("USE: " + va + " AT: " + element.getParent().toString() +
                    " DEFINED: " + va.getVariable().getSimpleName());
        }

    }
}
