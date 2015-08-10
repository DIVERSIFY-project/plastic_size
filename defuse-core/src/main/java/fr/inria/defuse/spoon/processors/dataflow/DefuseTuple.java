package fr.inria.defuse.spoon.processors.dataflow;

/**
 * Created by marodrig on 01/04/2015.
 */
public class DefuseTuple implements Comparable<DefuseTuple> {

    /**
     * Name of the variable being accessed
     */
    private String name;


    public enum Access {
        WRITE(0), READ(1), NOTHING(2);
        private final int value;
        private Access(int value) {
            this.value = value;
        }
        public int getValue() {
            return value;
        }
    }

    ;

    /**
     * Context in which the variable was found
     */
    private String context;

    /**
     * Level on the control flow where the access was found
     */
    private int level;

    /**
     * Line where the variable access was found
     */
    private int line;

    /**
     * Kind of access
     */
    private Access access;

    /**
     * Method where the access was found
     */
    private String method;

    /**
     * Name of the compilation unit where the variable was found
     */
    private String unitName;

    @Override
    public String toString() {
        String SEP = "@";
        StringBuilder sb = new StringBuilder();
        
        
        sb.append(getName()).append(SEP)
                .append(getAccess()).append(SEP)
                .append(getLine()).append(SEP)
                .append(getLevel()).append(SEP)
                .append(getContext()).append(SEP)
                .append(getUnitName()).append(SEP)
                .append(getMethod());
        return sb.toString();
    }

    @Override
    public int compareTo(DefuseTuple o) {
        int result = o.getUnitName().compareTo(getUnitName());
        if (result == 0) result = o.getMethod().compareTo(getMethod());
        if (result == 0) result = getLine() - o.getLine();
        if (result == 0) result = getLevel() - o.getLevel();
        if ( result == 0 ) result = getAccess().getValue() - o.getAccess().getValue();
        return result;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
