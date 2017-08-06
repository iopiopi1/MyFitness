package MyFitness;

/**
 * Created by iopiopi on 8/4/17.
 */

public class KeyValueList {

    private String key;
    private String value;

    public KeyValueList(String kKey, String vValue){
        key = kKey;
        value = vValue;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }



}
