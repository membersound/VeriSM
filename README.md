VeriSM - a Verilog state machine designer
====================

### Author: Daniel Kotyk

This project has been developed as part of my diploma thesis at<br></br>
<a href="http://kt.uni-due.de">Kommunikationstechnik - Uni Duisburg</a>, Prof. Dr.-Ing. habil. Peter Jung.

## Purpose
- graphical designer for finite state machines (FSM)
- automatic Verilog code generation by a single click (one-hot encoded)

## Screenshot
![Alt text](/VeriSM/deployment/example.png)

## Deployment
You can find the full working application on Google App Engine (GAE) here: http://verismde.appspot.com
Only restriction: no database <anbindung>. It's the full app, just without support of user login and project database saving. But you can download the project instead (Export > Project) and reimport it lateron.

You might also use the war file in "deployment" folder to directly install it to your local webserver. Or checkout the project and use the installation manual in the same directory to compile and deploy it manually.

## Sub features
- verification of all input data
- verification of the FSM before export to Verilog
- application-wide renaming of any signal, state or transition
- prevents slips by validating every user action, eg an input signal cannot be deleted as long as it is used in any condition of other transitions
- project import and export (JSON format)
- user registration and database persistence for the created projects
- drag & zoom support

## Development
Feel free to stress the app, file bugs, add suggestions, and contribute if you like.
