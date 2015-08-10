package fr.inria.defuse.spoon.processors.dataflow;

import spoon.reflect.declaration.CtVariable;

import static fr.inria.defuse.spoon.processors.dataflow.DefuseTuple.Access.WRITE;

/**
 *
 * A processor to create def-use tuples
 *
 * Created by marodrig on 01/04/2015.
 */
public class VarDefTupleBuilder extends DefuseTupleBuilder<CtVariable> {
    @Override
    public void process(CtVariable variable) {
        newTuple(variable.getSimpleName(), variable, WRITE);
    }
}
