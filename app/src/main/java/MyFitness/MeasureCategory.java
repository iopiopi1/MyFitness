package MyFitness;

/**
 * Created by iopiopi on 4/28/17.
 */

public class MeasureCategory {
    private int categoryId;
    private String categoryName;

    public MeasureCategory(int categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public void setCategoryId(int categoryId){
        this.categoryId = categoryId;
    }

    public int getCategoryId(){
        return this.categoryId;
    }

    public void setCategoryName(String categoryName){
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return this.categoryName;
    }

}
