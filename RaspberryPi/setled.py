#!/usr/bin/python
import json
from sense_emu import SenseHat

sense = SenseHat()

filename = "leddata.json";

if filename:
    with open(filename, 'r') as f:
        ledDisplayArray=json.load(f);
        
for led in ledDisplayArray:
    # schemat led: y x R G B
    sense.set_pixel(led[1], led[0], led[2], led[3], led[4]);