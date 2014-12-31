package testcases;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.terryxu.parsleyframework.facet.freemarker.FreemarkerFacet;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class TestFtl {

	@Test
	public void test() {

		Configuration c = new Configuration();

		try {
			FileTemplateLoader fl = new FileTemplateLoader(
					new File(
							"D:\\workspace\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\TestParsley\\WEB-INF/resource-files/"));
			// FileTemplateLoader fl = new FileTemplateLoader(new File(
			// "D:/workspace/TestFreemarker/WebContent/ftl"));
			ClassTemplateLoader cl = new ClassTemplateLoader(
					FreemarkerFacet.class, "/components");
			TemplateLoader[] ldrs = new TemplateLoader[] { fl, cl };
			MultiTemplateLoader l = new MultiTemplateLoader(ldrs);
			c.setTemplateLoader(l);

			Template t = c.getTemplate("/bt/index.ftl");

			Map root = new HashMap();
			root.put("title", "this is a test panel");
			root.put("content", "this is a test");
			root.put("bootstrap-lib-path", "this is a test lib path");

			Writer out = new OutputStreamWriter(System.out);
			// Writer out = new OutputStreamWriter(h.getOutputStream());
			t.process(root, out);
			// Template t=c.getTemplate("button.ftl");
			// t.process(root, out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
