package net.kkolyan.utils.benchme.util;

import net.kkolyan.utils.benchme.api.Defaults;
import net.kkolyan.utils.benchme.api.View;
import net.kkolyan.utils.benchme.core.SuiteConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ReportGenerator {

    //=========================================================
	
    private Writer writer;
	private Map<String,String> contextAttributes;
	private List<? extends TableSetGenerator.Record> data;
	private SuiteConfig config;

    //=========================================================

	public ReportGenerator() {
	}

	//=========================================================

	public void generate() throws Exception {
		String css = getDefaultCss();

        writer.append("<html>");
        {
            writer.append("<head>");
            {
                writer.append("<title>");
                writer.append(config.getTitle().value());
                writer.append("</title>");
				writer.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=").append(Defaults.ENCODING).append("\"/>");
            }
            if (css != null) {
                writer.append("<style type=\"text/css\">");
                writer.append(css);
                writer.append("</style>");
            }
            writer.append("</head>");
        }
        {
            writer.append("<body>");
			if (contextAttributes.entrySet() != null && !contextAttributes.isEmpty()) {
				writer.append("<h2>");
				writer.append("Context Attributes");
				writer.append("</h2>");
				writer.append("<ul>");
				for (Map.Entry<String, String> entry: contextAttributes.entrySet()) {
					writer.append("<li>");
					writer.append("<strong>").append(entry.getKey()).append(": ").append("</strong>").append(entry.getValue());
					writer.append("</li>");
				}
				writer.append("</ul>");
			}
			writer.append("<h2>");
			writer.append("Contents");
			writer.append("</h2>");
			writer.append("<ul>");
			for (View view: config.getReportViews()) {
				writer.append("<li>");
				writer.append("<a href=\"#").append(view.displayName()).append("\">").append(view.displayName()).append("</a>");
				writer.append("</li>");
			}
			writer.append("</ul>");
			for (View report: config.getReportViews()) {
				writer.append("<div class=\"reportView\" id=\"").append(report.displayName()).append("\">");
				TableSetGenerator generator = new TableSetGenerator();
				generator.setData(data);
				generator.setWriter(writer);
				generator.setReportConfig(report);
				generator.generateTable();
				writer.append("</div>");
			}
            writer.append("</body>");
        }
        writer.append("</html>");
        writer.flush();
	}

	public void setWriter(Writer writer) {
		this.writer = writer;
	}

	public void setContextAttributes(Map<String, String> contextAttributes) {
		this.contextAttributes = contextAttributes;
	}

	public void setData(List<? extends TableSetGenerator.Record> data) {
		this.data = data;
	}

	public void setConfig(SuiteConfig config) {
		this.config = config;
	}

	//=========================================================

    private String getDefaultCss() throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(Defaults.REPORT_STYLES_CLASSPATH_LOCATION);
        Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            sb.append(line);
			sb.append(' ');
        }
        inputStream.close();
        return sb.toString();
    }

    //=========================================================
}
