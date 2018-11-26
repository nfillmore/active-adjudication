package demo.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.Unique;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Created by cengruilin on 2018/4/27.
 */
@RestController
@RequestMapping("/api/mgr/console")
public class ConsoleRestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static CellProcessor[] getProcessors() {

//        final String emailRegex = "[a-z0-9\\._]+@[a-z0-9\\.]+"; // just an example, not very robust!
//        StrRegEx.registerMessage(emailRegex, "must be a valid email address");

        final CellProcessor[] processors = new CellProcessor[]{
                new Unique(), // id (must be unique)
                new Optional(), // LabChemSummaryDateTime
                new Optional(), // LabChemTestSID
                new Optional(), // LabChemTestName
                new Optional(), // LabChemPrintTestName
                new Optional(), // Units
//                new Optional(), // LOINCSID
                new Optional(), // LOINC
                new Optional(), // Component
//                new Optional(), // TopSID
                new Optional(), // Topography
                new Optional(), // p1
                new Optional(), // p5
                new Optional(), // p10
                new Optional(), // p25
                new Optional(), // p50
                new Optional(), // p75
                new Optional(), // p90
                new Optional(), // p95
                new Optional(), // p99
                new Optional(), // n
                new Optional(), // min
                new Optional() // max
        };

        return processors;
    }

    public static String tryParseDouble(String str) {
        if ("NA".equals(str)) {
            return "NULL";
        } else {
            return str;
        }
    }

    public static String tryParseInteger(String str) {
        if ("NA".equals(str)) {
            return "NULL";
        } else {
            return str;
        }
    }

    public static String wrapString(String str) {
//        String result = null;
        str = str.replaceAll("\"", "'");
        return "\"" + str + "\"";
    }

    @GetMapping("/convertCsv2Sqlite")
    @ResponseBody
    public String convertCsv2Sqlite() {
        File csvFile = new File("data.csv");
        if (!csvFile.exists()) {
            return "There is no data.csv";
        }
        jdbcTemplate.execute("DELETE FROM result_set;");
        try {
            ICsvListReader listReader = new CsvListReader(new FileReader("data.csv"), CsvPreference.STANDARD_PREFERENCE);
            listReader.getHeader(true);
            final CellProcessor[] processors = getProcessors();

            List<Object> exampleDataList;
            while ((exampleDataList = listReader.read(processors)) != null) {
//                System.out.println(String.format("lineNo=%s, rowNo=%s, customerList=%s", listReader.getLineNumber(),
//                        listReader.getRowNumber(), exampleDataList));
                StringBuilder sb = new StringBuilder(1024);
                sb.append("INSERT INTO result_set(id, LabChemTestSID, LabChemTestName,Units,LOINC,Component,Topography,p1,p5,p10,p25,p50,p75,p90,p95,p99,n,min,max) VALUES(");
                sb.append(exampleDataList.get(0)).append(",");
                sb.append(wrapString(exampleDataList.get(2).toString())).append(",");
                sb.append(wrapString(exampleDataList.get(3).toString())).append(",");
                sb.append(wrapString(exampleDataList.get(5).toString())).append(",");
                sb.append(wrapString(exampleDataList.get(6).toString())).append(",");
                sb.append(wrapString(exampleDataList.get(7).toString())).append(",");
                sb.append(wrapString(exampleDataList.get(8).toString())).append(",");
                sb.append(tryParseDouble(exampleDataList.get(9).toString())).append(",");
                sb.append(tryParseDouble(exampleDataList.get(10).toString())).append(",");
                sb.append(tryParseDouble(exampleDataList.get(11).toString())).append(",");
                sb.append(tryParseDouble(exampleDataList.get(12).toString())).append(",");
                sb.append(tryParseDouble(exampleDataList.get(13).toString())).append(",");
                sb.append(tryParseDouble(exampleDataList.get(14).toString())).append(",");
                sb.append(tryParseDouble(exampleDataList.get(15).toString())).append(",");
                sb.append(tryParseDouble(exampleDataList.get(16).toString())).append(",");
                sb.append(tryParseDouble(exampleDataList.get(17).toString())).append(",");
                sb.append(tryParseInteger(exampleDataList.get(18).toString())).append(",");
                sb.append(tryParseDouble(exampleDataList.get(19).toString())).append(",");
                sb.append(tryParseDouble(exampleDataList.get(20).toString()));

                sb.append(")");

                jdbcTemplate.execute(sb.toString());

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return e.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return "Done";
    }
}
