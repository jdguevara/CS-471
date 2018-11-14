package com.jonathanaquino.gittimelapseview.helpers;

import com.google.common.collect.ImmutableMap;
import com.jonathanaquino.gittimelapseview.Diff;
import difflib.DiffException;
import difflib.DiffRow;
import difflib.DiffRowGenerator;
import difflib.myers.MyersDiff;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Utility functions for diffing files.
 */
public class DiffHelper {

    private static final String TAG = "span";

    private static final Map<DiffRow.Tag, String> TAG_CLASS = ImmutableMap.<DiffRow.Tag, String>builder()
            .put(DiffRow.Tag.INSERT, "add")
            .put(DiffRow.Tag.DELETE, "remove")
            .put(DiffRow.Tag.CHANGE, "change")
            .build();

    /**
     * Returns a diff of two text files
     *
     * @param leftFileContents  the contents of the first file
     * @param rightFileContents  the contents of the second file
     * @param showDifferencesOnly  whether to hide identical lines
     * @param wordDiff
     * @return  the lines that differ
     */
    public static Diff diff(String leftFileContents, String rightFileContents, boolean showDifferencesOnly, boolean wordDiff) throws DiffException {
        String[] leftFileLines = split(leftFileContents);
        String[] rightFileLines = split(rightFileContents);
        List leftLineNumbers = lineNumbers(leftFileLines);
        List rightLineNumbers = lineNumbers(rightFileLines);

        DiffRowGenerator generator = new DiffRowGenerator.Builder()
                .showInlineDiffs(wordDiff)
                .ignoreWhiteSpaces(true)
                .diffAlgorithm(new MyersDiff<>())
                .inlineRevisedInsertTag(TAG)
                .inlineRevisedInsertCssClass(TAG_CLASS.get(DiffRow.Tag.INSERT))
                .inlineOriginDeleteTag(TAG)
                .inlineOriginDeleteTag(TAG_CLASS.get(DiffRow.Tag.DELETE))
                .build();

        //compute the differences for two test texts.
        List<DiffRow> diff = generator.generateDiffRows(
                Arrays.asList(leftFileLines),
                Arrays.asList(rightFileLines));

        List<DiffRowIndexed> rows = IntStream.range(0, diff.size())
                .mapToObj(i -> new DiffRowIndexed(diff.get(i), i + 1))
                .collect(Collectors.toList());

        List<DiffRowIndexed> changedRows = rows.stream()
                .filter(x -> x.getRow().getTag() != DiffRow.Tag.EQUAL)
                .collect(Collectors.toList());

        if (showDifferencesOnly) {
            rows = changedRows;
        }

        return new Diff(
                "<pre>" + renderLines(rows, wordDiff, DiffRow::getOldLine) + "</pre>",
                "<pre>" + renderLines(rows, wordDiff, DiffRow::getNewLine) + "</pre>",
                leftFileContents,
                rightFileContents,
                leftLineNumbers,
                rightLineNumbers,
                changedRows.stream().map(DiffRowIndexed::getLineNum).collect(Collectors.toList()));
    }

    private static String formatLineDiff(DiffRow row, Function<DiffRow, String> getText) {
        String text = getText.apply(row);
        String cssClass = TAG_CLASS.get(row.getTag());

        if (cssClass != null) {
            return "<span class=\"" + cssClass + "\">" + text + "</span>";
        } else {
            return text;
        }
    }

    /**
     * Renders diff lines to an HTML document
     */
    private static String renderLines(List<DiffRowIndexed> rows, boolean wordDiff, Function<DiffRow, String> formatRow) {
        int lineNumberWidth = rows.isEmpty()
                ? 0
                : rows.get(rows.size() - 1).lineNum % 10 + 2;

        Function<DiffRow, String> formatter = wordDiff ?
                formatRow
                : x -> formatLineDiff(x, formatRow);

        return rows.stream()
                .map(r -> StringUtils.rightPad(String.valueOf(r.getLineNum()), lineNumberWidth) +
                        formatter.apply(r.getRow()))
                .collect(Collectors.joining("\n"));
    }

    private static class DiffRowIndexed {
        private final DiffRow row;
        private final int lineNum;

        public DiffRowIndexed(DiffRow row, int lineNum) {
            this.row = row;
            this.lineNum = lineNum;
        }

        public DiffRow getRow() {
            return row;
        }

        public int getLineNum() {
            return lineNum;
        }
    }

    /**
     * Line numbers for the file
     *
     * @param lines  the lines of the text file
     * @return  an array of line-number strings
     */
    private static List lineNumbers(String[] lines) {
        List lineNumbers = new ArrayList();
        for (int i = 1; i <= lines.length; i++) {
            lineNumbers.add(String.valueOf(i));
        }
        return lineNumbers;
    }

    /**
     * Splits the string at \r, \n, or \r\n.
     *
     * @param fileContents  the contents of a text file
     * @return  the lines of the text file
     */
    protected static String[] split(String fileContents) {
        return fileContents.split("\r\n|\r|\n");
    }

}
