#!/usr/bin/python
import json
from sense_emu import SenseHat
print("IN");
sense = SenseHat();
filename = "leddata.json";
f=open(filename, 'r');
ledDisplayArray=json.load(f);
f.close();        
for led in ledDisplayArray:
    # schemat led: y x R G B
    sense.set_pixel(led[1], led[0], led[2], led[3], led[4]);
    print("1");