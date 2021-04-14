PImage img;
Table county_data;
County[] counties;
User_Settings settings = new User_Settings();
Drag_Circle_Scale scaler;

float transition = 0;

boolean in_transition = false;

String instructions = "**Hover over a county to see its information\nClick on county with mouse1 to hold info, click again to free it (be slow and gentle)\n\n**Click and hold the circle on the slider to change the filter value\nPress '1' for cases\nPress '2' for deaths\nPress '3' for voter %\nPress'4' for population\nPress '0' for no filter\n\n";
String instructions2 = "**Press 'g' to switch filter between greater than (>) or less than (<) the specified value\n\n**Press spacebar to go in between the map and the scatter plot\n\n**Click on the box with the year to flip the voting years between 2016 and 2020\n\n";
String instructions3 = "**Press 'q' to see Texas\nPress 'w' to see Georgia\nPress 'e' to see Colorado\n\n**Circle size depends on cases/population\nColor is who the majority voted for\n\n**You always start on scatter plot, even when you switch states";

State Texas, Georgia, Colorado, selected_State;


void setup() {
  fullScreen();
  /*load in the file
    Create a county for every row in the State data set, instantiating a new county object every time
    
    make the window as big as our State image
    set the background to the State counties image
  */
 //frameRate();
  //String _name, String _file_Name, String _image_Name, int _window_Length, int _window_Width, float _lat_Start, float _lat_End, float _lon_Start, float _lon_End
  Texas = new State("Texas", "Texas_Set.csv", "Texas.jpg", 1678, 1447, 25.8, 36.5, 93.6, 106.3, 162306, 2811, 4525519); 
  Colorado = new State("Colorado", "Colorado_Set.csv", "Colorado.jpg", 320, 232, 37, 41, 102, 109, 31753, 628, 1010420);
  Georgia = new State("Georgia","Georgia_Set.csv","Georgia.jpg",298,342,30.3,35,80.5,85.76, 19878, 452, 678467); 
  selected_State = Texas;
  
  scaler = new Drag_Circle_Scale(displayWidth*.035, displayHeight * .3, 30, width*.045, width*.15, 0, selected_State.max_Cases, selected_State.max_Cases, selected_State.max_Deaths, selected_State.max_Population);
}

boolean circle_selected = false;

void mouse_clicked_circle(County circle_check, int option) {
  if(circle_check.mouse_over_circle(option) && ((!circle_selected && !circle_check.hold_info_box) || circle_check.hold_info_box)) {
    circle_check.turn_info_box();
    circle_selected = circle_check.hold_info_box;
  }
}

void year_box_clicked() {
  if(settings.mouse_over_box()) {
    settings.switch_year();
  }
}

void mouseClicked() {year_box_clicked();}

void keyPressed() {
  if(key == ' ') {
      in_transition = true;
  } else if(key == '1') {
    scaler.change_Filter(1);
  } else if(key == '2') {
    scaler.change_Filter(2);
  } else if(key == '3') {
    scaler.change_Filter(3);
  } else if(key == '4') {
    scaler.change_Filter(4);
  } else if(key == '0') {
    scaler.change_Filter(-1);
  } else if(key == 'g') {
    scaler.flip_Greater_Than();
  }  else if(key == 'q') {
    selected_State = Texas;
    circle_selected = false;
    settings.scatter_plot = true;
  } else if(key == 'w') {
    selected_State = Georgia;
    circle_selected = false;
    settings.scatter_plot = true;
  } else if(key == 'e') {
    selected_State = Colorado;
    circle_selected = false;
    settings.scatter_plot = true;
  } 
  scaler.reconfigure(selected_State.max_Cases, selected_State.max_Deaths, selected_State.max_Population);
}

void draw() {
  //if user settings is on map
  if(settings.scatter_plot && !in_transition){
    selected_State.display_Scatter();    
  } else if (!in_transition){
    selected_State.display_Image();
    boolean year = settings.Display();
    for(County data_point : selected_State.counties) {
       if(data_point.pass_Filter(scaler.display(), scaler.filter, scaler.greater_than, year)) data_point.Map_Display(year, circle_selected);
     if(mousePressed) mouse_clicked_circle(data_point, 1);
    }//if user settings is on scatter plot
  } else {
      background(255);
      boolean year = settings.Display();
      if(mousePressed) {
        for(County data_point : selected_State.counties) mouse_clicked_circle(data_point, 3);
      }
      for(County data_point : selected_State.counties) {
         if(data_point.pass_Filter(scaler.display(), scaler.filter, scaler.greater_than, year)) data_point.transition_Display(year, settings.scatter_plot, transition); 
      }
    if(transition <= 100) {
       transition += 1; 
    } else {
     settings.switch_graph();
     in_transition = false;
     transition = 0;
    }
  }
  fill(0);
  text("**INSTRUCTIONS\n\n"+instructions + instructions2 + instructions3, displayWidth*.9, displayHeight*.04, displayWidth*.1, displayHeight);
}

//to do

//Do a couple other states and be able to switch in between them
