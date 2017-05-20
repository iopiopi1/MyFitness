package MyFitness;

/**
 * Created by iopiopi on 4/28/17.
 */

public class Measurement {
    private float value;
    private String timestamp;
    private int categoryId;
    private int measureId;
    private int uqId;

    public Measurement(float value, String timestamp, int categoryId, int measureId, int uqId) {
        this.value = value;
        this.timestamp = timestamp;
        this.categoryId = categoryId;
        this.measureId = measureId;
        this.uqId = uqId;
    }

    public void setValue(float value){
        this.value = value;
    }

    public float getValue(){
        return this.value;
    }

    public void setTimestamp(String timestamp){
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public void setCategoryId(int categoryId){
        this.categoryId = categoryId;
    }

    public int getCategoryId(){
        return this.categoryId;
    }

    public void setMeasureId(int measureId){
        this.measureId = measureId;
    }

    public int getMeasureId() {
        return this.measureId;
    }

    public void setUqId(int uqId){
        this.uqId = uqId;
    }

    public int getUqId() {
        return this.uqId;
    }

}
