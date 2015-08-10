package fr.inria.defuse.spoon.processors.dataflow;

import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtElement;

import static fr.inria.defuse.spoon.processors.dataflow.DefuseTuple.Access.READ;
import static fr.inria.defuse.spoon.processors.dataflow.DefuseTuple.Access.WRITE;

/**
 *
 * A procesor to create def-use tuples
 *
 * Created by marodrig on 01/04/2015.
 */
public class VarAccessTupleBuilder extends DefuseTupleBuilder<CtVariableAccess> {
    @Override
    public void process(CtVariableAccess variableAccess) {
        CtElement e = variableAccess.getParent();
        if ( e != null && e instanceof CtAssignment) {
            CtAssignment assignment = (CtAssignment)e;
            assignment.getAssigned().equals(variableAccess);
            newTuple(variableAccess.getVariable().getSimpleName(), variableAccess, WRITE);
        } else
            newTuple(variableAccess.getVariable().getSimpleName(), variableAccess, READ);


    }
}
