package meg.util.imp;


public interface Placeholder {

	void setField(int i, Object object);
	
	void setField(String key, Object field);

	Object getField(int i);

	Object getField(String fromFieldTag);
}
