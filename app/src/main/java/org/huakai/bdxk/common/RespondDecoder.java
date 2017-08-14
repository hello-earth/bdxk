package org.huakai.bdxk.common;

/**
 * Created by Young on 2017/8/6.
 */

public class RespondDecoder {

    public String srchex="";
    public int[] srchexint;

    public boolean initData(String rhexstr){
        int[] hexStr = ByteUtils.hexStringToInt(rhexstr);
        int org = 0;
        for(int i=0;i<hexStr.length-1;i++)
            org = org^hexStr[i];

        int a = Integer.parseInt(rhexstr.substring(rhexstr.length()-2),16);
        if(a==org) {
            srchex = rhexstr;
            srchexint = ByteUtils.hexStringToInt(rhexstr);
            return true;
        }
        return false;
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

    public String get0FIdentifier(){
        try{
            return srchex.substring(28,44);
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

    public int[] get0FType(){
        String type = Integer.toBinaryString(srchexint[28]);
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

    public String get0FMeasurementDate(){
        return srchex.substring(44,56);
    }

    public float getTemperature(){
        int temp = Integer.parseInt(srchex.substring(40,44),16);
        if(temp>10000){
            String binstr = Integer.toBinaryString(temp ^ (temp>>1));
            temp = 0-(Integer.parseInt(binstr.substring(1), 2)+1);
        }
        float tmp = temp*0.1f;
        tmp = (float) (Math.round(tmp * 100)) / 100;
        return tmp;
    }
    public float get0FTemperature(){
        int temp = Integer.parseInt(srchex.substring(58,62),16);
        if(temp>10000){
            String binstr = Integer.toBinaryString(temp ^ (temp>>1));
            temp = 0-(Integer.parseInt(binstr.substring(1), 2)+1);
        }
        float tmp = temp*0.1f;
        tmp = (float) (Math.round(tmp * 100)) / 100;
        return tmp;
    }


    public float getStrainValue(){
        return (float)(Integer.parseInt(srchex.substring(44,48),16)*0.1);
    }

    public float get0FStrainValue(){
        return (float)(Integer.parseInt(srchex.substring(94,98),16)*0.1);
    }


    public float getOffsetVaule(){
        int temp = Integer.parseInt(srchex.substring(48,52),16);
        if(temp>10000){
            String binstr = Integer.toBinaryString(temp ^ (temp>>1));
            temp = 0-(Integer.parseInt(binstr.substring(1), 2)+1);
        }
        return temp*0.1f;
    }

    public float get0FOffsetVaule(){
        int temp = Integer.parseInt(srchex.substring(98,102),16);
        if(temp>10000){
            String binstr = Integer.toBinaryString(temp ^ (temp>>1));
            temp = 0-(Integer.parseInt(binstr.substring(1), 2)+1);
        }
        return temp*0.1f;
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

    public String get0FStrainUnit(){
        try{
            String unit = "";
            for(int i=51;i<55;i++){
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

    public String get0FModel(){
        try{
            String modestr = "";
            for(int i=6;i<14;i++)
                modestr += String.valueOf((char)srchexint[i]);
            return modestr;
        }catch (IndexOutOfBoundsException ex){
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "";
    }

    public float String1Hz(){
        return (float)(Integer.parseInt(srchex.substring(62,66),16)*0.1);
    }
    public float String2Hz(){
        return (float)(Integer.parseInt(srchex.substring(66,70),16)*0.1);
    }
    public float String3Hz(){
        return (float)(Integer.parseInt(srchex.substring(70,74),16)*0.1);
    }
    public float String4Hz(){
        return (float)(Integer.parseInt(srchex.substring(74,78),16)*0.1);
    }
    public float String5Hz(){
        return (float)(Integer.parseInt(srchex.substring(78,82),16)*0.1);
    }
    public float String6Hz(){
        return (float)(Integer.parseInt(srchex.substring(82,86),16)*0.1);
    }
    public float StringHz(){
        return (float)(Integer.parseInt(srchex.substring(86,90),16)*0.1);
    }
    public float StringRedressHz(){
        return (float)(Integer.parseInt(srchex.substring(90,94),16)*0.1);
    }


    public String get0FStr(){
        String result= "";
        result += "传感器型号："+get0FModel()+"\n";
        result += "传感器编号：" +get0FIdentifier()+"\n";
        result += "测量日期："+get0FMeasurementDate()+"\n";
        int[] data = get0FType();
        result += "传感器类型："+data[0]+"；小数点位数："+data[1]+"\n";
        result += "温度："+get0FTemperature()+"℃\n";
        result += "弦一频率："+String1Hz()+"Hz\n";
        result += "弦二频率："+String2Hz()+"Hz\n";
        result += "弦三频率："+String3Hz()+"Hz\n";
        result += "弦四频率："+String4Hz()+"Hz\n";
        result += "弦五频率："+String5Hz()+"Hz\n";
        result += "弦六频率："+String6Hz()+"Hz\n";
        result += "弦平均频率："+StringHz()+"Hz\n";
        result += "弦平均频率（温度补偿后）："+StringRedressHz()+"Hz\n";
        result += "应变值："+get0FStrainValue()+"\n";
        result += "偏移值："+get0FOffsetVaule()+"\n";
        result += "应变单位："+get0FStrainUnit()+"\n";
        return result;
    }

    public String get10Str(){
        String result= "";
        result += "传感器编号：" +getIdentifier()+"\n";
        result += "测量日期："+getMeasurementDate()+"\n";
        result += "温度："+getTemperature()+"℃\n";
        result += "应变值："+getStrainValue()+"\n";
        result += "偏移值："+getOffsetVaule()+"\n";
        result += "应变频率："+getStrainHz()+"Hz\n";
        result += "补偿频率："+getRedressHz()+"Hz\n";
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
        result += "传感器类型："+data[0]+"；小数点位数："+data[1]+"\n";
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

    public String get10SavingStr(){
        String result= "";
        result += getIdentifier()+","+getMeasurementDate()+","+getTemperature()+","+getOffsetVaule()+","+get10IdentifierSetting();
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
                case 0x0F:
                    result = get0FStr();
                    break;
            }
        }
        return result;
    }

}
