package demo.web.rest;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import demo.ml.impl.TFMachineLearningCalculator;
import demo.service.ExampleService;
import demo.service.LabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by cengruilin on 2017/11/2.
 */
@RestController
@RequestMapping("")
public class DatatableRestContoller {
    public final static int COLUMNS_LENGTH = 24;
    private Map<Long, String> _labelData = new HashMap<>();
    private Map<Long, String> currentLabelData;
    @Autowired
    private ExampleService exampleService;
    @Autowired
    private LabelService labelService;

    public Map<Long, String> getCurrentLabelData() {
        return currentLabelData;
    }

    public void setCurrentLabelData(Map<Long, String> currentLabelData) {
        this.currentLabelData = currentLabelData;
    }

    @RequestMapping(value = "/datatables_data_head", method = RequestMethod.GET)
    public Object datatables_data_head() {
        List<String> columns = new ArrayList<>();
        columns.add("User");
        columns.add("Pred");
        columns.add("P(yes)");
        columns.add("Margin");
        columns.add("RowID");
        columns.addAll(exampleService.getColumns());
        return columns;
    }

    private String generateLabelLink(long rowId, Map<Long, String> labelData) {
        if (labelData.containsKey(rowId)) {
            String val = labelData.get(rowId);
            if (val.equals("yes")) {
                return String.format("yes&nbsp;<a href='javascript:label_example(%s, \"no\")'>no</a>&nbsp;<a href='javascript:label_example(%s, \"-\")'>-</a>", rowId, rowId);
            } else {
                return String.format("<a href='javascript:label_example(%s, \"yes\")'>yes</a>&nbsp;no&nbsp;<a href='javascript:label_example(%s, \"-\")'>-</a>", rowId, rowId);
            }
        } else {
            return String.format("<a href='javascript:label_example(%s, \"yes\")'>yes</a>&nbsp;<a href='javascript:label_example(%s, \"no\")'>no</a>&nbsp;-", rowId, rowId);
        }
    }

    private String fixSortColumnName(String columnName) {
        if (columnName.equalsIgnoreCase("RowId")) {
            return "id";
        } else {
            return columnName;
        }
    }

    @RequestMapping(value = "/datatables_data", method = RequestMethod.GET)
    public Object datatables_data(int draw, int start, int length, Long taskId, boolean readonly,
                                  HttpServletRequest request) {

        Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            if (name.indexOf("columns") > -1) {

            }
        }

        List<Map<String, Object>> queryResult = null;
        Map<Long, String> labelData = null;

        if (null != taskId) {
            String orderColumnIndexStr = request.getParameter("order[0][column]");
            String where = "";
            int columnIndex = 4;
            while (columnIndex < COLUMNS_LENGTH) {
                String columnName = request.getParameter(String.format("columns[%s][name]", columnIndex));
                if ("RowID".equals(columnName)) {
                    columnName = "id";
                }
                String searchValue = request.getParameter(String.format("columns[%s][search][value]", columnIndex));
                if (!StringUtils.isEmpty(searchValue)) {
                    if (searchValue.startsWith(">=") || searchValue.startsWith("<=")) {
                        if (searchValue.length() > 2) {
                            where += " AND a." + columnName + searchValue + " ";
                        }

                    } else if (searchValue.startsWith(">") || searchValue.startsWith("<")) {
                        if (searchValue.length() > 1) {
                            where += " AND a." + columnName + searchValue + " ";
                        }
                    } else {
                        where += " AND a." + columnName + " like '%" + searchValue + "%' ";
                    }
                }
                columnIndex++;
            }

            if (!Strings.isNullOrEmpty(orderColumnIndexStr)) {
                String sortDirection = request.getParameter("order[0][dir]");
                String columnName = request.getParameter(String.format("columns[%s][name]", orderColumnIndexStr));
                if (!Strings.isNullOrEmpty(columnName)) {
                    queryResult = exampleService.findByTaskId(taskId, where, fixSortColumnName(columnName), sortDirection);
                }
            }

            if (null == queryResult)
                queryResult = exampleService.findAll(taskId, where);

            labelData = labelService.findLabelValues(taskId);
        } else {
            String orderColumnIndexStr = request.getParameter("order[0][column]");
            if (!Strings.isNullOrEmpty(orderColumnIndexStr)) {
                String sortDirection = request.getParameter("order[0][dir]");
                String columnName = request.getParameter(String.format("columns[%s][name]", orderColumnIndexStr));
                if (!Strings.isNullOrEmpty(columnName)) {
                    queryResult = exampleService.findAllWithSort(fixSortColumnName(columnName), sortDirection);
                }
            }

            if (null == queryResult) {
                queryResult = exampleService.findAll();
            }

            labelData = _labelData;
        }
        setCurrentLabelData(labelData);
        boolean containsLabel = !getCurrentLabelData().isEmpty();
//        queryResult.stream().forEach(item -> {
//            long rowId = Long.parseLong(item.get("id").toString());
//            if (containsLabel && getCurrentLabelData().containsValue("yes") && getCurrentLabelData().containsValue("no")) {
//                String pred = null;
//                double pYes = 0;
//                if (getCurrentLabelData().containsKey(rowId)) {
//                    pred = getCurrentLabelData().get(rowId);
//                    if (pred.equals("yes")) {
//                        pYes = new BigDecimal(100 - new Random().nextInt(49)).divide(new BigDecimal(100)).doubleValue();
//                    } else {
//                        pYes = new BigDecimal(new Random().nextInt(49)).divide(new BigDecimal(100)).doubleValue();
//                    }
//
//                } else {
//                    pYes = new BigDecimal(new Random().nextInt(100)).divide(new BigDecimal(100)).doubleValue();
//                    pred = "yes";
//                    if (pYes < 0.5) {
//                        pred = "no";
//                    }
//                }
//                item.put("Pred", pred);
//                item.put("P(yes)", pYes);
//                item.put("Margin", "" + new BigDecimal(new Random().nextInt(100)).divide(new BigDecimal(100)));
//
//            } else {
//                item.put("Pred", "-");
//                item.put("P(yes)", "nan");
//                item.put("Margin", "nan");
//            }
//
//            item.put("User", generateLabelLink(rowId, getCurrentLabelData()));
//
//            item.put("RowID", rowId);
//        });
        queryResult.stream().forEach(item -> {
            long rowId = Long.parseLong(item.get("id").toString());
            if (containsLabel && getCurrentLabelData().containsKey(rowId)) {
                item.put("Pred", getCurrentLabelData().get(rowId));
                item.put("PredByUser", getCurrentLabelData().get(rowId));
            }
            item.put("RowID", rowId);
        });
//        queryResult = new MockMachineLearningCalculator().calculate(queryResult);
        queryResult = new TFMachineLearningCalculator().calculate(queryResult);

        queryResult.removeIf(item -> {
            boolean hasFilter = false;
            boolean filterValue = true;
            int filterColumnIndex = 1;
            while (filterColumnIndex < 4) {
                String columnName = request.getParameter(String.format("columns[%s][name]", filterColumnIndex));
                String searchValue = request.getParameter(String.format("columns[%s][search][value]", filterColumnIndex));
                if (!StringUtils.isEmpty(searchValue)) {
                    hasFilter = true;
                    switch (columnName) {
                        case "Pred":
                            if (item.get("Pred").equals(searchValue.toLowerCase())) {
                                filterValue = filterValue && true;
                            } else {
                                filterValue = false;
                            }
                            break;
                        case "P(yes)":
                            double pyes = Double.parseDouble(item.get("P(yes)").toString());
                            filterValue = filterValue && compare(pyes, searchValue);
                            break;
                        case "Margin":
                            double margin = Double.parseDouble(item.get("Margin").toString());
                            filterValue = filterValue && compare(margin, searchValue);
                            break;
                    }
                }
                filterColumnIndex++;
            }
            if (hasFilter) {
                return !filterValue;
            } else {
                return false;
            }
        });

        if (readonly) {
            for (Map<String, Object> item : queryResult) {
                item.put("User", labelData.get(Long.parseLong(item.get("id").toString())));
            }
        }

        int recordsTotal = queryResult.size();
        int recordsFiltered = recordsTotal;
        Object data = queryResult;

        Map<String, Object> result = Maps.newHashMap();
        result.put("data", data);
        result.put("draw", draw);
        result.put("recordsTotal", recordsTotal);
        result.put("recordsFiltered", recordsFiltered);


        return result;
    }

    private boolean compare(double value, String value2Compare) {
        if (value2Compare.startsWith(">=")) {
            if (value2Compare.length() == 2) {
                return true;
            }
            return value >= Double.parseDouble(value2Compare.substring(2, value2Compare.length()));
        } else if (value2Compare.startsWith("<=")) {
            if (value2Compare.length() == 2) {
                return true;
            }
            return value <= Double.parseDouble(value2Compare.substring(2, value2Compare.length()));
        } else if (value2Compare.startsWith(">")) {
            if (value2Compare.length() == 1) {
                return true;
            }
            return value > Double.parseDouble(value2Compare.substring(1, value2Compare.length()));
        } else if (value2Compare.startsWith("<")) {
            if (value2Compare.length() == 1) {
                return true;
            }
            return value < Double.parseDouble(value2Compare.substring(1, value2Compare.length()));
        } else {
            return value == Double.parseDouble(value2Compare);
        }
    }

    @RequestMapping("/datatables_ft")
    public Object datatables_ft() {
        return "{\"ftCoefs\":[[\"percentile__ks_stat\",0.0],[\"n__n\",0.0],[\"min__min\",0.0],[\"p1__p1\",0.0],[\"p5__p5\",0.0],[\"p10__p10\",0.0],[\"p25__p25\",0.0],[\"p50__p50\",0.0],[\"p75__p75\",0.0],[\"p90__p90\",0.0],[\"p95__p95\",0.0],[\"p99__p99\",0.0],[\"max__max\",0.0],[\"TestName__height\",0.0],[\"TestName__ht\",0.0],[\"TestName__stature\",0.0],[\"Component__digital\",0.0],[\"Component__height\",0.0],[\"Component__reported\",0.0],[\"Component__rod\",0.0],[\"Component__self\",0.0],[\"Component__stadiometer\",0.0],[\"Topography__adult\",0.0],[\"Topography__body\",0.0],[\"Topography__full\",0.0],[\"Topography__height\",0.0],[\"Units__feet\",0.0],[\"Units__ft\",0.0],[\"LOINC__1754-1\",0.0],[\"LOINC__6793-4\",0.0],[\"LOINC__6793-4\\r\\n\",0.0]]}\n";
    }

    @RequestMapping("/datatables_stats")
    public Object datatables_stats() {
        return "{\"ftCoefs\":{\"Component__digital\":0.0,\"Component__height\":0.0,\"Component__reported\":0.0,\"Component__rod\":0.0,\"Component__self\":0.0,\"Component__stadiometer\":0.0,\"LOINC__1754-1\":0.0,\"LOINC__6793-4\":0.0,\"LOINC__6793-4\\r\\n\":0.0,\"TestName__height\":0.0,\"TestName__ht\":0.0,\"TestName__stature\":0.0,\"Topography__adult\":0.0,\"Topography__body\":0.0,\"Topography__full\":0.0,\"Topography__height\":0.0,\"Units__feet\":0.0,\"Units__ft\":0.0,\"max__max\":0.0,\"min__min\":0.0,\"n__n\":0.0,\"p10__p10\":0.0,\"p1__p1\":0.0,\"p25__p25\":0.0,\"p50__p50\":0.0,\"p5__p5\":0.0,\"p75__p75\":0.0,\"p90__p90\":0.0,\"p95__p95\":0.0,\"p99__p99\":0.0,\"percentile__ks_stat\":0.0},\"trainAccuracy\":0.0,\"trainPrecision\":0.0,\"trainRecall\":0.0}\n";
    }

    @PostMapping("/datatables_label_examples")
    public Object datatables_label_examples(long row_ids, String label, Long taskId) {
        if (null == taskId) {
            if ("-".equals(label)) {
                _labelData.remove(row_ids);
            } else {
                _labelData.put(row_ids, label);
            }
        } else {
            labelService.updateLabelValue(taskId, row_ids, label);
        }

        return "{}";
    }
}
