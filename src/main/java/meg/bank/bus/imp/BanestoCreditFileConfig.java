package meg.bank.bus.imp;

import meg.bank.util.imp.FieldFormat;
import meg.bank.util.imp.FileConfig;


public class BanestoCreditFileConfig implements FileConfig {

	public String getFieldDelim() {
		return "\t";
	}

	public int getFileType() {
		return FileConfig.FileType.Delimited;
	}

	public int getStartLine() {
		return 2;
	}

	public FieldFormat[] getFieldFormats() {
		FieldFormat[] formats = new FieldFormat[5];

		formats[0] = new FieldFormat();
		formats[1] = new FieldFormat();
		formats[2] = new FieldFormat();
		formats[3] = new FieldFormat();

		formats[0].setFieldType(FieldFormat.Type.DateTime);
		formats[0].setFieldTag("field1");
		formats[0].setInputPattern("dd-MM-yyyy");

		formats[1].setFieldType(FieldFormat.Type.Ignore);

		formats[2].setFieldType(FieldFormat.Type.Double);
		formats[2].setFieldTag("field3");
		formats[2].setTextToRemove("Euro.");
		formats[2].setInputPattern("###,###.##");
		formats[2].setLocale("Locale.FRA");

		formats[3].setFieldType(FieldFormat.Type.String);
		formats[3].setFieldTag("field4");

		return formats;

	}

}
