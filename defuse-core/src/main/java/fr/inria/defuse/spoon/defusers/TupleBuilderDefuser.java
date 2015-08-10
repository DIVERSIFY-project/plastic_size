package fr.inria.defuse.spoon.defusers;

import fr.inria.defuse.Defuser;
import fr.inria.defuse.spoon.processors.dataflow.DefuseTuple;
import fr.inria.defuse.spoon.processors.dataflow.StatementTupleBuilder;
import fr.inria.defuse.spoon.processors.dataflow.VarAccessTupleBuilder;
import fr.inria.defuse.spoon.processors.dataflow.VarDefTupleBuilder;
import org.apache.log4j.Logger;
import spoon.processing.AbstractProcessor;

import java.util.*;

import static fr.inria.defuse.spoon.processors.dataflow.DefuseTuple.Access.NOTHING;
import static fr.inria.defuse.spoon.processors.dataflow.DefuseTuple.Access.READ;
import static fr.inria.defuse.spoon.processors.dataflow.DefuseTuple.Access.WRITE;

/**
 * Created by marodrig on 01/04/2015.
 */
public class TupleBuilderDefuser extends AbstractDefuser implements Defuser {

    /**
     * A report on the defuse process
     */
    public static class Report {
        int moved;
        int total;
    }

    HashMap<String, Report> reports;


    final static Logger logger = Logger.getLogger(TupleBuilderDefuser.class);

    ArrayList<VarAccessTupleBuilder> readAccess;
    ArrayList<VarDefTupleBuilder> writeAccess;
    ArrayList<StatementTupleBuilder> statements;

    @Override
    protected void initProcesors() {
        readAccess = new ArrayList<>();
        writeAccess = new ArrayList<>();
        statements = new ArrayList<>();
        projectDescriptions = new ArrayList<>();
    }

    @Override
    protected int iterationCount() {
        return 3;
    }

    @Override
    protected AbstractProcessor<?> nextProcessor(int iteration, String projectName) {
        switch (iteration) {
            case 0:
                readAccess.add(new VarAccessTupleBuilder());
                return readAccess.get(readAccess.size() - 1);
            case 1:
                writeAccess.add(new VarDefTupleBuilder());
                return writeAccess.get(writeAccess.size() - 1);
            case 2:
                statements.add(new StatementTupleBuilder());
                return statements.get(statements.size() - 1);
        }
        throw new RuntimeException("Invalid iteration " + iteration);
    }


    @Override
    public void outputResults() {

        Iterator<VarAccessTupleBuilder> reads = readAccess.iterator();
        Iterator<VarDefTupleBuilder> writes = writeAccess.iterator();
        Iterator<StatementTupleBuilder> states = statements.iterator();

        reports = new HashMap<>();

        int i = 0;
        while (reads.hasNext()) {
            ArrayList<DefuseTuple> tuples = new ArrayList<>();
            tuples.addAll(reads.next().getTuples());
            tuples.addAll(writes.next().getTuples());
            Report r = new Report();
            r.total = tuples.size();
            tuples.addAll(states.next().getTuples());
            Collections.sort(tuples);

            System.out.println("********************************************");
            System.out.println("********************************************");
            System.out.println("Name@Access@Line@Level@Contex@Method@Class");


            reports.put(projectDescriptions.get(i), r);

            if (outputOptions.equals("write-down")) r.moved = moveWritesDown(tuples);
            else if (outputOptions.equals("write-up")) r.moved = moveWritesUp(tuples);
            else if (outputOptions.equals("write-moved")) r.moved = moveWritesUp(tuples) + moveWritesDown(tuples);
            else for (DefuseTuple t : tuples) System.out.println(t.toString());
            i++;
        }

        String SEP = "@";
        System.out.println("********************************************");
        System.out.println("REPORTS ");
        System.out.println("********************************************");
        for (Map.Entry<String, Report> e : reports.entrySet()) {
            System.out.println(e.getKey() + SEP + e.getValue().moved + SEP + e.getValue().total);
        }
    }

    @Override
    public void setOutputOptions(String outputOptions) {
        this.outputOptions = outputOptions;
    }

    private int moveWritesUp(ArrayList<DefuseTuple> tuples) {
        int tuplesMoved = 0;
        DefuseTuple lastRead = null;
        int inBetween = 0;
        for (DefuseTuple t : tuples) {
            if (!t.getAccess().equals(READ)) {
                lastRead = t;
                inBetween = 0;
            } else if (isStatementInBetween(t, lastRead)) {
                inBetween++;
            } else if (t.getAccess().equals(WRITE) && lastRead != null) {
                if (canSwitch(t, lastRead, inBetween)) {
                    //We can push down the writing
                    System.out.println(lastRead.toString());
                    System.out.println(t.toString());
                    tuplesMoved++;
                } else lastRead = null;
            }
        }
        return tuplesMoved;
    }

    private boolean isStatementInBetween(DefuseTuple t1, DefuseTuple t2) {
        return t1.getAccess().equals(NOTHING) &&
                t2 != null && t2.getLine() != t1.getLine() &&
                t2.getLevel() == t1.getLevel();
    }

    private boolean canSwitch(DefuseTuple t1, DefuseTuple t2, int inBetween) {
        boolean result = !t1.getName().equals(t2.getName());
        result |= (t1.getLine() - t2.getLine() > 0) &&
                inBetween > 0 &&
                t1.getLevel() == t2.getLevel() &&
                t1.getMethod().equals(t2.getMethod()) &&
                t1.getUnitName().equals(t2.getUnitName());
        return result;
    }

    private int moveWritesDown(ArrayList<DefuseTuple> tuples) {

        int tuplesMoved = 0;
        DefuseTuple lastWrite = null;
        int inBetween = 0;
        for (DefuseTuple t : tuples) {
            if (t.getAccess().equals(READ) && lastWrite != null) {
                if (canSwitch(t, lastWrite, inBetween)) {
                    //We can push down the writing
                    System.out.println(lastWrite.toString());
                    System.out.println(t.toString());
                    tuplesMoved++;
                } else lastWrite = null;
            } else if (t.getAccess().equals(WRITE)) {
                lastWrite = t;
                inBetween = 0;
            } else if (t.getAccess().equals(NOTHING) &&
                    lastWrite != null && lastWrite.getLine() != t.getLine() &&
                    lastWrite.getLevel() == t.getLevel()) {
                inBetween++;
            } else inBetween = 0;
        }
        return tuplesMoved;
    }

}
