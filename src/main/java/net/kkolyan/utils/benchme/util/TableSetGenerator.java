package net.kkolyan.utils.benchme.util;

import net.kkolyan.utils.benchme.api.View;

import java.io.Writer;
import java.util.*;

public class TableSetGenerator {

    //=========================================================

    private Writer writer;
    private List<? extends Record> data;
	private View reportConfig;

	//=========================================================

    public TableSetGenerator() {
    }

    //=========================================================

    public void generateTable() throws Exception {
        Map<String,Map<String,Map<String,List<String>>>> tables = new LinkedHashMap<String, Map<String, Map<String, List<String>>>>();

		String cellKey = reportConfig.cellKey();
		String rowKey = reportConfig.rowsBy();
		String columnKey = reportConfig.columnsBy();
		String tableKey = reportConfig.tablesBy();

        for (Record unit : data) {
            String cellValue = unit.getValue(cellKey);
            String tableValue = unit.getValue(tableKey);
            String rowValue = unit.getValue(rowKey);
            String columnValue = unit.getValue(columnKey);

            Map<String,Map<String,List<String>>> table = getValue(tableValue, LinkedHashMap.class, tables);
            Map<String,List<String>> row = getValue(rowValue, LinkedHashMap.class, table);
            List<String> cellValueList = row.get(columnValue);
            if (cellValueList == null) {
                cellValueList = new ArrayList<String>();
            }
            cellValueList.add(cellValue);
            row.put(columnValue, cellValueList);
        }
		
		{
			writer.append("<h3>");
			writer.append(reportConfig.displayName());
			writer.append("</h3>");
		}
		for (Map.Entry<String,Map<String,Map<String,List<String>>>> table: tables.entrySet()) {
			if (tableKey != null && !tableKey.isEmpty()) {
				writer.append("<h4>");
				writer.append(tableKey).append(": ").append(table.getKey());
				writer.append("</h4>");
			}
			writer.append("<table>");
			{
				writer.append("<tr>");
				{
					writer.append("<td class=\"keyComment\">");
					writer.append(rowKey);
					if (columnKey != null && !columnKey.isEmpty()){
						writer.append(" \\ ").append(columnKey);
					}
					writer.append("</td>");
				}
				for (Map.Entry<String,List<String>> cell: table.getValue().entrySet().iterator().next().getValue().entrySet()) {
					writer.append("<td class=\"key\">");
					writer.append(cell.getKey());
					writer.append("</td>");
				}
				writer.append("</tr>");
			}
			for (Map.Entry<String,Map<String,List<String>>> row: table.getValue().entrySet()) {
				writer.append("<tr>");
				{
					writer.append("<td class=\"key\">");
					writer.append(row.getKey());
					writer.append("</td>");
				}
				for (Map.Entry<String,List<String>> cell: row.getValue().entrySet()) {
					writer.append("<td class=\"value\">");
					for (String value: cell.getValue()) {
						writer.append("<p>");
						writer.append(value);
						writer.append("</p>");
					}
					writer.append("</td>");

				}
				writer.append("</tr>");
			}
			writer.append("</table>");
		}
    }

	//=========================================================

    public interface Record {
        String getValue(String key);
    }

    //=========================================================

    public void setData(List<? extends Record> data) {
        this.data = data;
    }

	public void setReportConfig(View reportConfig) {
		this.reportConfig = reportConfig;
	}

	public void setWriter(Writer writer) {
        this.writer = writer;
    }

	//=========================================================

    @SuppressWarnings({"unchecked"})
	private static <K,V> V getValue(K key, Class<?> valueClass, Map<K,V> map) throws IllegalAccessException, InstantiationException {
        V value = map.get(key);
        if (value == null) {
            value = (V) valueClass.newInstance();
            map.put(key, value);
        }
        return value;
    }

    //=========================================================
}
