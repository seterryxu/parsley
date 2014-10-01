package org.seterryxu.navigator.freemarker


import org.seterryxu.navigator.core.parse.AbstractParser

import freemarker.template.Configuration
import freemarker.template.Template;

class TemplateParser extends AbstractParser {

	@Override
	public Object load(Object view) {
		Configuration conf=new Configuration()
		Template t=conf.getTemplate("")
		return null;
	}
}
