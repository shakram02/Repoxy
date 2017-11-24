package tests.utils;

public class LineBasedStringBuilder {
    private final StringBuilder sb;

    public LineBasedStringBuilder() {
        this.sb = new StringBuilder();
    }

    public void appendTabbedLine(String s) {
        this.sb.append("\n");
        this.appendLine(s);

    }

    public void appendTabbedLine(int i) {
        this.sb.append("\t");
        this.appendLine(i);
    }

    public void appendTabbedLine(Object o) {
        this.sb.append("\t");
        this.appendLine(o);
    }


    public void appendLine(String s) {
        this.sb.append(s);
        this.sb.append("\n");
    }

    public void appendLine(int i) {
        this.sb.append(i);
        this.sb.append("\n");
    }

    public void appendLine(Object o) {
        this.sb.append(o.toString());
        this.sb.append("\n");
    }

    @Override
    public String toString() {
        return this.sb.toString();
    }
}
