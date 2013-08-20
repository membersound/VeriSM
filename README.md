Author: Daniel Kotyk
====================

This project has been developed as part of my diploma thesis at <a href="http://kt.uni-due.de">Kommunikationstechnik - Uni Duisburg</a>, Prof. Dr.-Ing. habil. Peter Jung, supervised by Ernest Scheiber.


### Features
============
- graphical design for finite state machines (FSM), including drag & zoom support
- automatic Verilog code generation by a single click (one-hot encoded)
- verification of all input data
- verification of the FSM before export to verilog
- application-wide renaming of any signal, state or transition
- prevents slips by validating every user action, eg an input signal cannot be deleted as long as it is used in any condition of the automata
- import and export of your project
- user registration and database persistence for the created projects


### Screenshot
==============

### Deployment
==============
You can find the full working application on Google App Engine (GAE) here: http://verismde.appspot.com
Only restriction: no database <anbindung>. It's the full app, just without support of user login and project database saving. But you can download the project instead (Export > Project) and reimport it lateron.

You might also use the war file in "deployment" folder to directly install it to your local webserver. Or checkout the project and use the installation manual in the same directory to compile and deploy it manually.


# Development
=============
Feel free to stress the app, file bugs, add suggestions, and contribute if you like.
