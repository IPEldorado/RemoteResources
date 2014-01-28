RemoteResources
===============

The proposal of Remote Resources is to provide a way to remotely interact with Android devices using a computer with Internet access. 
Summarizing, it is drawn with an architectural standard of client and server. The server is the computer where the Android device is physically connected and the client can be any other computer that interacts remotely with this device.

The interface between client and server is provided by exchange of control messages (control exchange) and data (information exchange).

The same computer can assume the client and server roles. In this case, any physics connected device will be treated as local device, automatically recognized by the tool.
