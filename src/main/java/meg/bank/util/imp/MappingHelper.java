package meg.bank.util.imp;


public interface MappingHelper {

	Object instantiateObject(String objectname);

	void doManualMapping(Object mapped, Placeholder placeholder);

}
