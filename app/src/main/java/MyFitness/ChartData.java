package MyFitness;

import com.example.iopiopi.myfitness.DBHelper;
import android.content.Context;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;

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
