package logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@SuppressWarnings("unused")
public class Boxer {

    //                               0   1     2     3     4     5     6     7     8     9     10
    public final static char[] c = {'╝','╗',  '╔',  '╚',  '╣',  '╩',  '╦',  '╠',  '═',  '║',  '╬'};
    public final static int[] i = {9565, 9559, 9556, 9562, 9571, 9577, 9574, 9568, 9552, 9553, 9580};
    private final char[] corners = {'╔', '╗', '╝', '╚'};
    private boolean boxBuilt = false;
    private String output;
    private Object content;
    //title
    private boolean hasTitle = false;
    private String title;
    private Alignment titleAlignment;
    //title
    private boolean hasFooter = false;
    private String footer;
    private Alignment titleFooter;
    //line wrapper
    private boolean useLineWrap = false;
    private int lineWrapLimit = 20;
    //column builder
    private final List<Object> objects;


    public enum Alignment {
        CENTER,
        TOP_LEFT,
        TOP,
        TOP_RIGHT,
        RIGHT,
        BOTTOM_RIGHT,
        BOTTOM,
        BOTTOM_LEFT,
        LEFT
    }

    public Boxer() {
        this.output = null;
        this.content = null;
        this.objects = null;
    }

    public Boxer(Object content) {
        this.output = null;
        this.content = content;
        this.objects = null;
    }

    public Boxer(List<Object> content) {
        this.output = null;
        this.content = null;
        this.objects = content;
    }

    public Boxer setContent(String content) {
        this.content = content;
        return this;
    }

    public Object getContent() {
        return this.content;
    }

    public String getOutput() {
        if (this.content == null)
            this.buildColumn();
        else if (this.objects == null)
            this.buildBox();
        return this.output;
    }

    public void write() {
        if (this.content == null)
            this.buildColumn();
        else if (this.objects == null)
            this.buildBox();
        System.out.println(this.output);
    }

    public void buildBox() {
        if (this.content == null) throw new IllegalArgumentException("Box is missing content.");

        //add line wrapping
        if (useLineWrap) {
            List<String> lines = new ArrayList<>();
            Collections.addAll(lines, this.content.toString().split("\n"));
            StringBuilder sb = new StringBuilder();
            for (String line : lines) {
                for (int i = 0; i < line.length()/lineWrapLimit + 1; i++) {
                    String s;
                    if ((i + 1) * lineWrapLimit > line.length())
                        s = line.substring(i * lineWrapLimit);
                    else
                        s = line.substring(i * lineWrapLimit, (i + 1) * lineWrapLimit);
                    int j = s.lastIndexOf(" ");
                    boolean bool = s.lastIndexOf(" ") >= lineWrapLimit/2;
                    if (j >= lineWrapLimit/2)
                        sb.append(s, 0, j).append("\n").append(s.substring(j + 1));
                    else
                        sb.append(s);
                }
                sb.append("\n");
            }
            this.setContent(sb.toString());
        }

        //format string into a usable form:
        List<String> lines = new ArrayList<>();
        if (this.content.toString().contains("\n")) {
            for (String l : this.content.toString().split("\n")) {
                if (l.length() % 2 != 0) {
                    l = l + " ";
                }
                lines.add(" " + l + " ");
            }
        } else lines.add(" " + this.content + " ");

        int len;
        if (!this.content.toString().contains("\n"))
            len = this.content.toString().length() + 2;
        else {
            String longest = "";
            for (String line : lines)
                if (line.length() > longest.length())
                    longest = line;
            len = longest.length();
        }

        StringBuilder top = new StringBuilder();
        List<StringBuilder> body = new ArrayList<>();
        StringBuilder bottom = new StringBuilder();

        //add title:
        if (hasTitle) {
            String title = this.title.trim();
            if (title.length() > len)
                len = title.length();
            switch (titleAlignment) {
                case TOP_LEFT -> top.append(this.corners[0])
                        .append(title)
                        .append(String.valueOf(c[8]).repeat(len - title.length()))
                        .append(this.corners[1])
                        .append("\n");
                case TOP -> {
                    if (title.length() % 2 != 0)
                        title = title + c[8];
                    top.append(this.corners[0])
                            .append(String.valueOf(c[8]).repeat((len - title.length())/2))
                            .append(title)
                            .append(String.valueOf(c[8]).repeat((len - title.length())/2))
                            .append(this.corners[1])
                            .append("\n");
                }
                case TOP_RIGHT -> top.append(this.corners[0])
                        .append(String.valueOf(c[8]).repeat(len - title.length()))
                        .append(title)
                        .append(this.corners[1])
                        .append("\n");
                default -> throw new IllegalArgumentException("Title cannot be on bottom row.");
            }
        } else top.append(this.corners[0]).append(String.valueOf(c[8]).repeat(len)).append(this.corners[1]).append("\n");

        //make footer:
        if (hasFooter) {
            String footer = this.footer.trim();
            if (footer.length() > len)
                len = footer.length();
            switch (titleFooter) {
                case BOTTOM_LEFT -> bottom.append(this.corners[3])
                        .append(footer)
                        .append(String.valueOf(c[8]).repeat(len - footer.length()))
                        .append(this.corners[2]);
                case BOTTOM -> {
                    if (footer.length() % 2 != 0)
                        footer = footer + c[8];
                    bottom.append(this.corners[3])
                            .append(String.valueOf(c[8]).repeat((len - footer.length())/2))
                            .append(footer)
                            .append(String.valueOf(c[8]).repeat((len - footer.length())/2))
                            .append(this.corners[2]);
                }
                case BOTTOM_RIGHT -> bottom.append(this.corners[3])
                        .append(String.valueOf(c[8]).repeat(len - footer.length()))
                        .append(footer)
                        .append(this.corners[2]);
                default -> throw new IllegalArgumentException("Footer cannot be on top row.");
            }
        } else bottom.append(this.corners[3]).append(String.valueOf(c[8]).repeat(len)).append(this.corners[2]);

        //make body:
        for (String line : lines) {
            final String padding = " ".repeat((len - line.length()) / 2);
            StringBuilder sb = new StringBuilder().append(c[9])
                    .append(padding)
                    .append(line)
                    .append(padding)
                    .append(c[9])
                    .append("\n");
            body.add(sb);
        }

        //combine components into 1 string:
        StringBuilder output = new StringBuilder(top);
        for (StringBuilder sb : body)
            output.append(sb);
        output.append(bottom);
        this.output = output.toString();
        this.boxBuilt = true;
    }

    public Boxer addTitle(String title, Alignment alignment) {
        this.title = title;
        this.titleAlignment = alignment;
        this.hasTitle = true;
        return this;
    }

    public Boxer addTitle(String title) {
        this.title = title;
        this.titleAlignment = Alignment.TOP_LEFT;
        this.hasTitle = true;
        return this;
    }

    public Boxer addFooter(String footer, Alignment alignment) {
        this.footer = footer;
        this.titleFooter = alignment;
        this.hasFooter = true;
        return this;
    }

    public Boxer addFooter(String footer) {
        this.footer = footer;
        this.titleFooter = Alignment.BOTTOM_LEFT;
        this.hasFooter = true;
        return this;
    }

    public Boxer disableLineWrap() {
        this.useLineWrap = false;
        this.lineWrapLimit = 0;
        return this;
    }

    public Boxer enableLineWrap(int limit) {
        this.useLineWrap = true;
        this.lineWrapLimit = limit;
        return this;
    }

    public Boxer enableLineWrap() {
        this.useLineWrap = true;
        this.lineWrapLimit = 20;
        return this;
    }

    public static void staticStringBox(String s) {
        //format string into a usable form:
        List<String> lines = new ArrayList<>();
        if (s.contains("\n")) {
            for (String l : s.split("\n")) {
                if (l.length() % 2 != 0) {
                    l = l + " ";
                }
                lines.add(" " + l + " ");
            }
        } else lines.add(" " + s + " ");

        int len;
        if (!s.contains("\n"))
            len = s.length() + 2;
        else {
            String longest = "";
            for (String line : lines)
                if (line.length() > longest.length())
                    longest = line;
            len = longest.length();
        }

        //make top border
        StringBuilder sb = new StringBuilder(c[2] + String.valueOf(c[8]).repeat(len) + c[1] + "\n");

        //make text
        for (String line : lines) {
            sb.append(c[9]);
            final String padding = " ".repeat((len - line.length()) / 2);
            sb.append(padding)
                    .append(line)
                    .append(padding)
                    .append(c[9])
                    .append("\n");
        }

        //make bottom border
        sb.append(c[3]).append(String.valueOf(c[8]).repeat(len)).append(c[0]);
        System.out.println(sb);
    }

    public boolean isBoxBuilt() {
        return this.boxBuilt;
    }

    //Table building methods
    public char[] getCorners() {
        return this.corners;
    }

    public Boxer setCorner(char newCorner, Alignment alignment) {
        switch (alignment) {
            case TOP_LEFT -> this.corners[0] = newCorner;
            case TOP_RIGHT -> this.corners[1] = newCorner;
            case BOTTOM_RIGHT -> this.corners[2] = newCorner;
            case BOTTOM_LEFT -> this.corners[3] = newCorner;
            default -> throw new IllegalArgumentException("The alignment provided is not a corner alignment");
        }
        return this;
    }

    public static class BoxNotBuiltException extends Exception {
        public BoxNotBuiltException() {
            super();
        }
        public BoxNotBuiltException(String message) {
            super(message);
        }
    }

    public static class NoContentException extends Exception {
        public NoContentException() {
            super();
        }
        public NoContentException(String message) {
            super(message);
        }
    }

    @Override
    public String toString() {
        this.buildBox();
        return this.output;
    }

    public static String addSpace(String s, int length) {
        if (s.length() < length) {
            System.out.println("true");
            if (s.length() % 2 != 0)
                s = s + " ";
            StringBuilder sBuilder = new StringBuilder(s);
            int reps = length - sBuilder.length();
            for (int j = 0; j < length - sBuilder.length(); j++) {
                System.out.println(j);
                System.out.println(reps + " | " + (length - sBuilder.length()) + " | " + sBuilder.length());
                sBuilder = new StringBuilder(" " + sBuilder + " ");
            }
            s = sBuilder.toString();
            return s;
        }
        return s;
    }

    public void buildColumn() {
        StringBuilder head = new StringBuilder();
        StringBuilder body = new StringBuilder();
        StringBuilder tail = new StringBuilder();


    }
}
