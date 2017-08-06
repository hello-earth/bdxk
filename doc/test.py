# -*- coding: utf8 -*-

'''
hexint = []
hexs="557A0100A3002841008500000004594830303230202005092620012C1F02206D6D20000005010105092707581047464B442D59482D4347512D30303031D2F8BAD3D6C7C4DCB4ABB8D0C6F732202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020202020200190C4"
for i in xrange(len(hexs)/2):
    hexint.append(int(hexs[i*2:(i+1)*2], 16))

print u"传感器编号"+hexs[12:28]

print u"传感器型号",
i=14
while(i<22):
    print chr(hexint[i]),
    i+=1
print
print u"生产日期"+hexs[22*2:50]
model = bin(hexint[25]).replace("0b","")
print u"小数点位数:%d"%int(model[:-4], 2)
print u"传感器类型:%d"%int(model[-4:], 2)
print u"温度补偿系数:%dppm/℃"%int(hexs[26*2:28*2],16)
print u"调零点频率: %dHz"%int(hexs[28*2:30*2],16)
print u"传感器应变单位: ",
i=30
while i<34:
    print chr(hexint[i]),
    i+=1
print
print u"自动测量时间 %d min"%int(hexs[34*2:36*2],16)
print u"自动测量设置日期 "+hexs[36*2:39*2]
print u"自编号设置时间 "+hexs[39*2:45*2]
print u"传感器自编号",
i=45
while i<61:
    if(hexint[i]!=0xFF):
        print chr(hexint[i]),
    i+=1
print
print u"注释信息",
i=61
while i<167:
    if(hexint[i]!=0xFF):
        print chr(hexint[i]),
    i+=1
print
print u"最大保存个数%d"%int(hexs[167*2:169*2],16)
print hexs[169*2:]
'''

hexint = []
hexs="557A100035002820946508000034170806094018015418E6FFFC18E618E6A1E320201FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF42442D444358313502"
for i in xrange(len(hexs)/2):
    hexint.append(int(hexs[i*2:(i+1)*2], 16))

print u"传感器编号"+hexs[12:28]
print u"测量日期 "+hexs[14*2:20*2]
print u"温度 %.2f ℃"%(int(hexs[20*2:22*2],16)*0.1)
print u"应变值 %.2f"%(int(hexs[22*2:24*2],16)*0.1)
print u"偏移值 %.2f"%(int(hexs[24*2:26*2],16)*0.1)
print u"应变频率 %.2f"%(int(hexs[26*2:28*2],16)*0.1)
print u"补偿频率 %.2f"%(int(hexs[28*2:30*2],16)*0.1)
print u"应变单位",
i=25
while i<29:
    if(hexint[i]!=0xFF):
        print chr(hexint[i]),
    i+=1
print

model = bin(hexint[34]).replace("0b","")
print u"小数点位数:%d"%int(model[:-4], 2)
print u"传感器类型:%d"%int(model[-4:], 2)


print u"传感器自编号",
i=35
while i<51:
    if(hexint[i]!=0xFF):
        print chr(hexint[i]),
    i+=1
print

print u"传感器型号",
i=51
while i<59:
    if(hexint[i]!=0xFF):
        print chr(hexint[i]),
    i+=1
print
print hexs[59*2:]
