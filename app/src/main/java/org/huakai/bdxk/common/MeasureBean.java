package org.huakai.bdxk.common;

/**
 * Created by Administrator on 2017/8/15.
 */

public class MeasureBean {

    private String identifier;
    private String measurementDate;
    private float temperature;
    private float offsetVaule;

    public MeasureBean(String _identifier, String _measurementDate, float _temperature, float _offsetVaule){
        this.identifier=_identifier;
        this.measurementDate = _measurementDate;
        this.temperature = _temperature;
        this.offsetVaule = _offsetVaule;
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

    public float getOffsetVaule(){
        return offsetVaule;
    }
}
