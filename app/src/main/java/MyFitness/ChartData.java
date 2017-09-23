package MyFitness;

/**
 * Created by iopiopi on 4/30/17.
 */

public class ChartData {
    private float dataX;
    private float dataY;

    public ChartData(float dX, float dY){
        dataX = dX;
        dataY = dY;
    }

    public void setDataY(float dataY){
        this.dataY = dataY;
    }

    public float getDataY(){
        return this.dataY;
    }

    public void setDataX(Long date) {
        this.dataX = date;

    }

    public float getDataX(){
        return this.dataX;
    }

}
