package org.huakai.bdxk.common;

/**
 * Created by Young on 2017/8/6.
 */

public class RespondDecoder {

    private String srchex="";
    private int[] srchexint;

    public RespondDecoder(String rhexstr){
        int[] hexStr = ByteUtils.hexStringToInt(rhexstr);
        int org = 0;
        for(int i=0;i<hexStr.length-1;i++)
            org = org^hexStr[i];

        int a = Integer.parseInt(rhexstr.substring(rhexstr.length()-2),16);
        if(a==org) {
            srchex = rhexstr;
            srchexint = ByteUtils.hexStringToInt(rhexstr);
        }
    }

    private String getIdentifier(){
        try{
            return "传感器编号："+srchex.substring(12,28);
        }catch (IndexOutOfBoundsException ex){
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "";
    }

    private String getModel(){
        try{
            String modestr = "";
            for(int i=14;i<22;i++)
                modestr += String.valueOf((char)srchexint[i]);
            return "传感器型号："+modestr;
        }catch (IndexOutOfBoundsException ex){
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "";
    }

    private String getProductionDate(){
        try{
            return "生产日期："+srchex.substring(44,50);
        }catch (IndexOutOfBoundsException ex){
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "";
    }

    private String getType(){
        String type = Integer.toBinaryString(srchexint[25]);
        return "传感器类型："+Integer.parseInt(type.substring(type.length()-4),2)+"; 小数点位数："+Integer.parseInt(type.substring(0,type.length()-4),2);
    }

    private String getPPM(){
        return "温度补偿系数："+Integer.parseInt(srchex.substring(52,56),16)+"ppm/℃";
    }

    private String get0hz(){
        return "调零点频率："+Integer.parseInt(srchex.substring(56,60),16)+"Hz";
    }

    private String getProductionUnit(){
        try{
            String unit = "";
            for(int i=30;i<34;i++){
                if(srchexint[i]!=0xFF)
                    unit += String.valueOf((char)srchexint[i]);
            }

            return "传感器应变单位："+unit.trim();
        }catch (IndexOutOfBoundsException ex){
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "";
    }

    private String getInterval(){
        return "自动测量时间："+Integer.parseInt(srchex.substring(68,72),16)+"min";
    }

    private String getSetIntervalDate(){
        return "自动测量设置日期："+srchex.substring(72,78);
    }

    private String getSetIdentifierDate(){
        return "自动测量设置日期："+srchex.substring(78,90);
    }

    private String getIdentifierSetting(){
        try{
            String result = "";
            for(int i=45;i<61;i++)
                if(srchexint[i]!=0xFF)
                    result += String.valueOf((char)srchexint[i]);
            return "传感器自编号："+result;
        }catch (IndexOutOfBoundsException ex){
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "";
    }

    private String getProductionDesc(){
        try{
            String result = "";
            for(int i=61;i<167;i++)
                if(srchexint[i]!=0xFF)
                    result += String.valueOf((char)srchexint[i]);
            return "注释信息："+result.trim();
        }catch (IndexOutOfBoundsException ex){
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "";
    }

    private String getMaxSaving(){
        return "最大保存个数："+Integer.parseInt(srchex.substring(167*2,169*2),16);
    }



    /////////////////////  测量数据解析 ////////////////////////////////
    private String getMeasurementDate(){
        return "测量日期："+srchex.substring(28,40);
    }

    private String getTemperature(){
        return "温度："+Integer.parseInt(srchex.substring(40,44),16)*0.1+"℃";
    }

    private String getStrainValue(){
        return "应变值："+Integer.parseInt(srchex.substring(44,48),16)*0.1;
    }

    private String getOffsetVaule(){
        return "偏移值："+Integer.parseInt(srchex.substring(48,52),16)*0.1;
    }

    private String getStrainHz(){
        return "应变频率："+Integer.parseInt(srchex.substring(52,56),16)*0.1;
    }

    private String getRedressHz(){
        return "补偿频率："+Integer.parseInt(srchex.substring(56,60),16)*0.1;
    }

    private String getStrainUnit(){
        try{
            String unit = "";
            for(int i=25;i<29;i++){
                if(srchexint[i]!=0xFF)
                    unit += String.valueOf((char)srchexint[i]);
            }

            return "传感器应变单位："+unit.trim();
        }catch (IndexOutOfBoundsException ex){
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "";
    }

    private String get10Type(){
        String type = Integer.toBinaryString(srchexint[34]);
        return "传感器类型："+Integer.parseInt(type.substring(type.length()-4),2)+"; 小数点位数："+Integer.parseInt(type.substring(0,type.length()-4),2);
    }

    private String get10IdentifierSetting(){
        try{
            String result = "";
            for(int i=35;i<51;i++)
                if(srchexint[i]!=0xFF)
                    result += String.valueOf((char)srchexint[i]);
            return "传感器自编号："+result;
        }catch (IndexOutOfBoundsException ex){
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "";
    }

    private String get10Model(){
        try{
            String modestr = "";
            for(int i=51;i<59;i++)
                modestr += String.valueOf((char)srchexint[i]);
            return "传感器型号："+modestr;
        }catch (IndexOutOfBoundsException ex){
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "";
    }

    private String get10Str(){
        String result= "";
        result += getIdentifier()+"\n";
        result += getMeasurementDate()+"\n";
        result += getTemperature()+"\n";
        result += getStrainValue()+"\n";
        result += getOffsetVaule()+"\n";
        result += getStrainHz()+"\n";
        result += getRedressHz()+"\n";
        result += getStrainUnit()+"\n";
        result += get10Type()+"\n";
        result += get10IdentifierSetting()+"\n";
        result += get10Model()+"\n";

        return result;
    }

    private String get01Str(){
        String result= "";
        result += getIdentifier()+"\n";
        result += getModel()+"\n";
        result += getProductionDate()+"\n";
        result += getType()+"\n";
        result += getPPM()+"\n";
        result += get0hz()+"\n";
        result += getProductionUnit()+"\n";
        result += getInterval()+"\n";
        result += getSetIntervalDate()+"\n";
        result += getSetIdentifierDate()+"\n";
        result += getIdentifierSetting()+"\n";
        result += getProductionDesc()+"\n";
        result += getMaxSaving()+"\n";
        return result;
    }


    public String getResult(){
        String result= "";
        if(!"".equals(srchex) && srchexint!=null) {
            switch (srchexint[2]){
                case 0x01:
                    result = get01Str();
                    break;
                case 0x10:
                    result = get10Str();
                    break;
            }
        }
        return result;
    }

}
