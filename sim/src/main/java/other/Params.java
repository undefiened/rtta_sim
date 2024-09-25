package other;

import java.io.Serializable;

/**
 * Created by undefiened on 5/25/17.
 */
public class Params implements Serializable{
    static final long serialVersionUID = 51L;
    public double pxPerKm, kmPerPx, mPerPx, speedMS, speedPxS, speedKmH;

    public double toleranceM = 0;
    public double tolerancePx;

    Params(double pixelsPerKm, double speedKmH){
        pxPerKm = pixelsPerKm;
        kmPerPx = 1/pxPerKm;
        mPerPx = kmPerPx*1000;

        this.speedKmH = speedKmH;
        speedPxS = this.speedKmH*pxPerKm/3600;
        speedMS = this.speedKmH*1000/3600;
        this.tolerancePx = convertMToPx(toleranceM);
    }

    Params(double pixelsPerKm, double speedKmH, double toleranceM){
        pxPerKm = pixelsPerKm;
        kmPerPx = 1/pxPerKm;
        mPerPx = kmPerPx*1000;

        this.speedKmH = speedKmH;
        speedPxS = this.speedKmH*pxPerKm/3600;
        speedMS = this.speedKmH*1000/3600;

        this.toleranceM = toleranceM;
        this.tolerancePx = convertMToPx(toleranceM);
    }

    public double convertMToPx(double m){
        return m/mPerPx;
    }

    public double convertPxToM(double px){
        return px*mPerPx;
    }
}
