package fr.inria.defuse.spoon.processors.dataflow;

import spoon.reflect.code.CtStatement;

import static fr.inria.defuse.spoon.processors.dataflow.DefuseTuple.Access.NOTHING;

/**
 *
 * A procesor to create def-use tuples
 *
 * Created by marodrig on 01/04/2015.
 */
public class StatementTupleBuilder extends DefuseTupleBuilder<CtStatement> {
    @Override
    public void process(CtStatement statement) {

            newTuple("STATEMENT", statement, NOTHING);


    }
}
