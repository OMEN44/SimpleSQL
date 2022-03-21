package logger;

import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("unused")
public class Boxer {

    //                               0   1     2     3     4     5     6     7     8     9     10
    public final static char[] c = {'╝','╗',  '╔',  '╚',  '╣',  '╩',  '╦',  '╠',  '═',  '║',  '╬'};
    public final static int[] i = {9565, 9559, 9556, 9562, 9571, 9577, 9574, 9568, 9552, 9553, 9580};
    private String output;
    private String content;
    //title
    private boolean hasTitle = false;
    private String title;
    private Alignment titleAlignment;
    //title
    private boolean hasFooter = false;
    private String footer;
    private Alignment titleFooter;


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
    }

    public Boxer(String content) {
        this.output = null;
        this.content = content;
    }

    public Boxer setContent(String content) {
        this.content = content;
        return this;
    }

    public String getContent() {
        return this.content;
    }

    public String getOutput() throws NoContentException {
        this.buildBox();
        return this.output;
    }

    public void write() throws NoContentException {
        this.buildBox();
        System.out.println(this.output);
    }

    public void buildBox() throws NoContentException {
        if (this.content == null)
            throw new NoContentException();

        //format string into a usable form:
        List<String> lines = new ArrayList<>();
        if (this.content.contains("\n")) {
            for (String l : this.content.split("\n")) {
                if (l.length() % 2 != 0) {
                    l = l + " ";
                }
                lines.add(" " + l + " ");
            }
        } else lines.add(" " + this.content + " ");

        int len;
        if (!this.content.contains("\n"))
            len = this.content.length() + 2;
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
                case TOP_LEFT -> top.append(c[2])
                        .append(title)
                        .append(String.valueOf(c[8]).repeat(len - title.length()))
                        .append(c[1])
                        .append("\n");
                case TOP -> {
                    if (title.length() % 2 != 0)
                        title = title + c[8];
                    top.append(c[2])
                            .append(String.valueOf(c[8]).repeat((len - title.length())/2))
                            .append(title)
                            .append(String.valueOf(c[8]).repeat((len - title.length())/2))
                            .append(c[1])
                            .append("\n");
                }
                case TOP_RIGHT -> top.append(c[2])
                        .append(String.valueOf(c[8]).repeat(len - title.length()))
                        .append(title)
                        .append(c[1])
                        .append("\n");
                default -> throw new IllegalArgumentException("Title cannot be on bottom row.");
            }
        } else top.append(c[2]).append(String.valueOf(c[8]).repeat(len)).append(c[1]).append("\n");

        //make footer:
        if (hasFooter) {
            String footer = this.footer.trim();
            if (footer.length() > len)
                len = footer.length();
            switch (titleFooter) {
                case BOTTOM_LEFT -> bottom.append(c[3])
                        .append(footer)
                        .append(String.valueOf(c[8]).repeat(len - footer.length()))
                        .append(c[0])
                        .append("\n");
                case BOTTOM -> {
                    if (footer.length() % 2 != 0)
                        footer = footer + c[8];
                    bottom.append(c[3])
                            .append(String.valueOf(c[8]).repeat((len - footer.length())/2))
                            .append(footer)
                            .append(String.valueOf(c[8]).repeat((len - footer.length())/2))
                            .append(c[0])
                            .append("\n");
                }
                case BOTTOM_RIGHT -> bottom.append(c[3])
                        .append(String.valueOf(c[8]).repeat(len - footer.length()))
                        .append(footer)
                        .append(c[0])
                        .append("\n");
                default -> throw new IllegalArgumentException("Footer cannot be on top row.");
            }
        } else bottom.append(c[3]).append(String.valueOf(c[8]).repeat(len)).append(c[0]);

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

    public static class NoContentException extends Exception {
        public NoContentException() {
            super();
        }

        public NoContentException(String message) {
            super(message);
        }
    }
}
