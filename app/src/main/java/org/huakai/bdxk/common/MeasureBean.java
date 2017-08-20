package org.huakai.bdxk.common;

/**
 * Created by Administrator on 2017/8/15.
 */

public class MeasureBean {

    private int whichplate;
    private String sensorName;
    private String identifier;
    private String measurementDate;
    private float temperature;
    private float offsetValue;

    public MeasureBean(String _identifier, String _sensorName, String _measurementDate, float _temperature, float _offsetValue){
        this.identifier=_identifier;
        this.sensorName = _sensorName;
        this.measurementDate = _measurementDate;
        this.temperature = (float) (Math.round(_temperature * 100)) / 100;
        this.offsetValue = (float) (Math.round(_offsetValue * 100)) / 100;
    }

    public String getIdentifier(){
        return this.identifier;
    }

    public String getMeasurementDate(){
        return this.measurementDate;
    }

    public float getTemperature(){
        return temperature;
    }

    public float getOffsetValue(){
        return offsetValue;
    }

    public String getSensorName(){return sensorName;}

    public String toString(){
        return sensorName+","+measurementDate+","+temperature+","+offsetValue;
    }

    public void setWhichplate(int value){
        whichplate = value;
    }

    public int getWhichplate(){
        return whichplate;
    }
}
