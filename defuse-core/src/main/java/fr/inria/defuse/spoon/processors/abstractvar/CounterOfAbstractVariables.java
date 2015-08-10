package fr.inria.defuse.spoon.processors.abstractvar;

import org.apache.log4j.Logger;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtTypeReference;

/**
 * Count the abstract variables
 * <p/>
 * Created by marodrig on 27/03/2015.
 */
public class CounterOfAbstractVariables extends AbstractProcessor<CtVariable> {

    final static Logger logger = Logger.getLogger(CounterOfAbstractVariables.class);

    private final CounterProcessingReport report;

    public CounterOfAbstractVariables(String p) {
        this();
        report.setProjectName(p);
    }

    public CounterProcessingReport getReport() {
        return report;
    }

    public static class CounterProcessingReport {

        private int totalCount = 0;

        public String getProjectName() {
            return projectName;
        }

        public void setProjectName(String projectName) {
            this.projectName = projectName;
        }

        private String projectName;

        public int getInterfaceCount() {
            return interfaceCount;
        }

        private int interfaceCount = 0;

        public int getCount() {
            return count;
        }

        private int count = 0;

        public int getFailedCount() {
            return failedCount;
        }

        public void incFailedCount() {
            this.failedCount = failedCount++;
        }

        private int failedCount = 0;

        public void incCount() {
            count++;
        }

        @Override
        public String toString() {
            return getProjectName() + ". Abstract variables: " + Integer.toString(count) +
                    " Interface: " + Integer.toString(interfaceCount) +
                    " Failed: " + Integer.toString(failedCount);
        }

        public void incInterfaceCount() {
            interfaceCount++;
        }

        public String toCVSLine() {
            return getProjectName() + "," + Integer.toString(count) +
                    "," + Integer.toString(interfaceCount) +
                    "," + Integer.toString(failedCount) +
                    "," + Integer.toString(totalCount);
        }

        public void incTotal() {

            totalCount++;
        }
    }

    public CounterOfAbstractVariables() {
        this.report = new CounterProcessingReport();
    }

    @Override
    public void process(CtVariable ctVariable) {
        try {
            report.incTotal();
            CtTypeReference r = ctVariable.getType();
            if (r == null) r = ctVariable.getDefaultExpression().getType();
            if (r == null) logger.error("Unable to obtain type of Variable " + ctVariable);


            if (r.getDeclaration() == null) {
                report.incFailedCount();
            } else if (r.getDeclaration().getModifiers().contains(ModifierKind.ABSTRACT)) {
                report.incCount();
            } else if (r.isInterface()) {
                report.incInterfaceCount();
            }

        } catch (NullPointerException e) {
            logger.warn("Unable to obtain modifiers of : " + ctVariable);
            report.incFailedCount();
        }
    }
}
