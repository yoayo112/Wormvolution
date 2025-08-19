# VIEWS

Everything in this folder is cosmetic/superficial only. Nothing happening in here should ever effect the behavior or outcome of the simulation. 

### Window.java
This is a custom jframe that holds everything. It handles close, minimize, fullscreen, resize window, and darkmode. NOTE: While this panel may hold controls for the simulation, that code is elsewhere. 

### Theme.java
This just holds color, font, size, and general formatting information. This is essentially what I would call a stylesheet. 

### MenuBar.java
This holds the various dynamic information for the dropdown menus across the top. 
Behavior allowed in this file:
    - window behavior (close, minimize, fullscreen, theme)
    - interfacing with the operating system
    - interfaces to add or control buttons/inputs from the controller or model.
Behavior NOT allowed in this file:
    - anything to to do with the simulation. 
    - controls for the simulation should be added from a controller object.

### Board.java
This is a JPanel object responsible for the visual depiction, size, drawing and painting of the virtual world/environment. This file should contain no information about the simulation itself or environmental variables pertaining to the outcome or behavior of the worms. 