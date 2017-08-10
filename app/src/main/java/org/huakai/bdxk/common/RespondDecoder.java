package org.huakai.bdxk.common;

/**
 * Created by Young on 2017/8/6.
 */

public class RespondDecoder {

    public String srchex="";
    public int[] srchexint;

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

    public String getRequestId(){
        return srchex.substring(4,6);
    }

    public String getIdentifier(){
        try{
            return srchex.substring(12,28);
        }catch (IndexOutOfBoundsException ex){
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "";
    }

    public String getModel(){
        try{
            String modestr = "";
            for(int i=14;i<22;i++)
                modestr += String.valueOf((char)srchexint[i]);
            return modestr;
        }catch (IndexOutOfBoundsException ex){
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "";
    }

    public String getProductionDate(){
        try{
            return srchex.substring(44,50);
        }catch (IndexOutOfBoundsException ex){
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "";
    }

    public int[] getType(){
        String type = Integer.toBinaryString(srchexint[25]);
        return new int[]{Integer.parseInt(type.substring(type.length()-4),2),
                Integer.parseInt(type.substring(0,type.length()-4),2)};
    }

    public int getPPM(){
        return Integer.parseInt(srchex.substring(52,56),16);
    }

    public int get0hz(){
        return Integer.parseInt(srchex.substring(56,60),16);
    }

    public String getProductionUnit(){
        try{
            String unit = "";
            for(int i=30;i<34;i++){
                if(srchexint[i]>=33 && srchexint[i]<127)
                    unit += String.valueOf((char)srchexint[i]);
            }
            return unit.trim();
        }catch (IndexOutOfBoundsException ex){
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "";
    }

    public int getInterval(){
        return Integer.parseInt(srchex.substring(68,72),16);
    }

    public String getSetIntervalDate(){
        return srchex.substring(72,78);
    }

    public String getSetIdentifierDate(){
        return srchex.substring(78,90);
    }

    public String getIdentifierSetting(){
        try{
            String result = "";
            for(int i=45;i<61;i++)
                if(srchexint[i]!=0xFF)
                    result += String.valueOf((char)srchexint[i]);
            return result;
        }catch (IndexOutOfBoundsException ex){
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "";
    }

    public String getProductionDesc(){
        try{
            String result = "";
            for(int i=61;i<167;i++)
                if(srchexint[i]!=0xFF)
                    result += String.valueOf((char)srchexint[i]);
            return result.trim();
        }catch (IndexOutOfBoundsException ex){
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "";
    }

    public int getMaxSaving(){
        return Integer.parseInt(srchex.substring(167*2,169*2),16);
    }



    /////////////////////  测量数据解析 ////////////////////////////////
    public String getMeasurementDate(){
        return srchex.substring(28,40);
    }

    public float getTemperature(){
        return Integer.parseInt(srchex.substring(40,44),16)*0.1f;
    }

    public float getStrainValue(){
        return (float)(Integer.parseInt(srchex.substring(44,48),16)*0.1);
    }

    public float getOffsetVaule(){
        return (float)(Integer.parseInt(srchex.substring(48,52),16)*0.1);
    }

    public int getStrainHz(){
        return Integer.parseInt(srchex.substring(52,56),16);
    }

    public int getRedressHz(){
        return Integer.parseInt(srchex.substring(56,60),16);
    }

    public String getStrainUnit(){
        try{
            String unit = "";
            for(int i=25;i<29;i++){
                if(srchexint[i]>=33 && srchexint[i]<127)
                    unit += String.valueOf((char)srchexint[i]);
            }

            return unit.trim();
        }catch (IndexOutOfBoundsException ex){
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "";
    }

    public int[] get10Type(){
        String type = Integer.toBinaryString(srchexint[34]);
        return new int[]{Integer.parseInt(type.substring(type.length()-4),2),Integer.parseInt(type.substring(0,type.length()-4),2)};
    }

    public String get10IdentifierSetting(){
        try{
            String result = "";
            for(int i=35;i<51;i++)
                if(srchexint[i]!=0xFF)
                    result += String.valueOf((char)srchexint[i]);
            return result;
        }catch (IndexOutOfBoundsException ex){
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "";
    }

    public String get10Model(){
        try{
            String modestr = "";
            for(int i=51;i<59;i++)
                modestr += String.valueOf((char)srchexint[i]);
            return modestr;
        }catch (IndexOutOfBoundsException ex){
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "";
    }

    public String get10Str(){
        String result= "";
        result += "传感器编号：" +getIdentifier()+"\n";
        result += "测量日期："+getMeasurementDate()+"\n";
        result += "温度："+getTemperature()+"\n";
        result += "应变值："+getStrainValue()+"\n";
        result += "偏移值："+getOffsetVaule()+"\n";
        result += "应变频率："+getStrainHz()+"\n";
        result += "补偿频率："+getRedressHz()+"\n";
        result += "传感器应变单位："+getStrainUnit()+"\n";
        int[] data = get10Type();
        result += "传感器类型："+data[0]+"; 小数点位数："+data[1]+"\n";
        result += "传感器自编号："+get10IdentifierSetting()+"\n";
        result += "传感器型号："+get10Model()+"\n";
        return result;
    }

    public String get01Str(){
        String result= "";
        result += "传感器编号：" + getIdentifier()+"\n";
        result += "传感器型号："+getModel()+"\n";
        result += "生产日期："+getProductionDate()+"\n";
        int[] data = getType();
        result += "传感器类型："+data[0]+"; 小数点位数："+data[1]+"\n";
        result += "温度补偿系数："+getPPM()+"ppm/℃\n";
        result += "调零点频率："+get0hz()+"Hz\n";
        result += "传感器应变单位："+getProductionUnit()+"\n";
        result += "自动测量时间："+getInterval()+"min\n";
        result += "自动测量设置日期："+getSetIntervalDate()+"\n";
        result += "自动测量设置日期："+getSetIdentifierDate()+"\n";
        result += "传感器自编号："+getIdentifierSetting()+"\n";
        result += "注释信息："+getProductionDesc()+"\n";
        result += "最大保存个数："+getMaxSaving()+"\n";
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
