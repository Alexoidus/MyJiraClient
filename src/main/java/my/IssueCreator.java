package my;

import com.opencsv.bean.CsvToBeanBuilder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;

public class IssueCreator {

    public void create(String fileName) {
        try {
            Reader file = new BufferedReader(new FileReader(fileName));
            List<CsvLine> csvLines = new CsvToBeanBuilder<CsvLine>(file).withType(CsvLine.class).build().parse();
            for (CsvLine line : csvLines) {
                System.out.println("line.summary = " + line.summary);
                System.out.println("line.assignee = " + line.assignee);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
