package my;

import com.opencsv.bean.CsvBindByName;

public class CsvLine {

        @CsvBindByName(required = true)
        public String summary;

        @CsvBindByName(required = true)
        public String assignee;

}
