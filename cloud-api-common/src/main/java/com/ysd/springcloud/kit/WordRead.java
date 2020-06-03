package com.ysd.springcloud.kit;

import org.apache.poi.xwpf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;


public class WordRead {


  public  void replaceInWord(Map<String, String> replacements, XWPFDocument doc, File outfile) throws IOException {
    long count1 = 0;
    long count2 = 0;
    List<XWPFParagraph> paragraphs = doc.getParagraphs();
    List<XWPFTable> tables = doc.getTables();
    count1 = replaceInParagraphs(replacements, paragraphs, false);
    count2 = replaceInTables(replacements, tables);
    doc.write(new FileOutputStream(outfile));
    System.out.println("段落替换数量累计：" + count1);
    System.out.println("表格替换数量累计：" + count2);
  }

  /**
   * 1.替换段落中的文本
   *
   * @param replacements
   * @param flag
   * @param paragraphs
   * @return
   */
  private long replaceInParagraphs(Map<String, String> replacements, List<XWPFParagraph> paragraphs, boolean flag) {
    long count = 0;
    String pString = flag ? "表格" + "段落清晰：" : "段落：";
    for (XWPFParagraph paragraph : paragraphs) {
      System.out.println(pString + paragraph.getText());
      List<XWPFRun> runs = paragraph.getRuns();
      for (Map.Entry<String, String> replPair : replacements.entrySet()) {
        String oldText = replPair.getKey();
        String newText = replPair.getValue();
        // 获取文本段
        TextSegement tSegement = paragraph.searchText(oldText, new PositionInParagraph());
        if (tSegement != null) {
          int beginRun = tSegement.getBeginRun();
          int endRun = tSegement.getEndRun();
          System.out.println(beginRun + " " + endRun);
          count++;
          if (beginRun == endRun) {
            // whole search string is in one Run
            XWPFRun run = runs.get(beginRun);
            String runText = run.getText(0);
            System.out.println("runText:" + runText);
            String replaced = runText.replace(oldText, newText);
            run.setText(replaced, 0);
          } else {
            // The search string spans over more than one Run
            // Put the Strings together
            StringBuilder b = new StringBuilder();
            for (int runPos = beginRun; runPos <= endRun; runPos++) {
              XWPFRun run = runs.get(runPos);
              b.append(run.getText(0));
            }
            String connectedRuns = b.toString();
            String replaced = connectedRuns.replace(oldText, newText);

            // The first Run receives the replaced String of all
            // connected Runs
            XWPFRun partOne = runs.get(beginRun);
            partOne.setText(replaced, 0);
            // Removing the text in the other Runs.
            for (int runPos = beginRun + 1; runPos <= endRun; runPos++) {
              XWPFRun partNext = runs.get(runPos);
              partNext.setText("", 0);
            }
          }
        }
      }
    }
    return count;
  }

  /**
   * 1.替换表格中的文本
   *
   * @param replacements
   * @param tables
   * @return
   */
  private long replaceInTables(Map<String, String> replacements, List<XWPFTable> tables) {

    long count = 0;
    for (XWPFTable table : tables) {
      for (XWPFTableRow row : table.getRows()) {
        for (XWPFTableCell cell : row.getTableCells()) {
          List<XWPFParagraph> paragraphs = cell.getParagraphs();
          count += replaceInParagraphs(replacements, paragraphs, true);
          
        }
      }
    }
    return count;
  }


  }

