import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class DataViz extends PApplet {

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


public void setup() {
  
  /*load in the file
    Create a county for every row in the State data set, instantiating a new county object every time
    
    make the window as big as our State image
    set the background to the State counties image
  */
 //frameRate();
  //String _name, String _file_Name, String _image_Name, int _window_Length, int _window_Width, float _lat_Start, float _lat_End, float _lon_Start, float _lon_End
  Texas = new State("Texas", "Texas_Set.csv", "Texas.jpg", 1678, 1447, 25.8f, 36.5f, 93.6f, 106.3f, 162306, 2811, 4525519); 
  Colorado = new State("Colorado", "Colorado_Set.csv", "Colorado.jpg", 320, 232, 37, 41, 102, 109, 31753, 628, 1010420);
  Georgia = new State("Georgia","Georgia_Set.csv","Georgia.jpg",298,342,30.3f,35,80.5f,85.76f, 19878, 452, 678467); 
  selected_State = Texas;
  
  scaler = new Drag_Circle_Scale(displayWidth*.035f, displayHeight * .3f, 30, width*.045f, width*.15f, 0, selected_State.max_Cases, selected_State.max_Cases, selected_State.max_Deaths, selected_State.max_Population);
}

boolean circle_selected = false;

public void mouse_clicked_circle(County circle_check, int option) {
  if(circle_check.mouse_over_circle(option) && ((!circle_selected && !circle_check.hold_info_box) || circle_check.hold_info_box)) {
    circle_check.turn_info_box();
    circle_selected = circle_check.hold_info_box;
  }
}

public void year_box_clicked() {
  if(settings.mouse_over_box()) {
    settings.switch_year();
  }
}

public void mouseClicked() {year_box_clicked();}

public void keyPressed() {
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

public void draw() {
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
  text("**INSTRUCTIONS\n\n"+instructions + instructions2 + instructions3, displayWidth*.9f, displayHeight*.04f, displayWidth*.1f, displayHeight);
}

//to do

//Do a couple other states and be able to switch in between them
class County {
    String name;
    float x,y;
    float lat, lon;
    float cases, deaths;
    float red_2016, red_2020;
    //float blue_2016 = 1 - red_2016;
    float population;
    int county_size;
    int very_stinky;
    float scatter_x, scatter_y;
    
    float map_TransitionX, map_TransitionY;
    
    County(String _name, float _lat, float _lon, float _cases, float _deaths, float _red_2016, float _red_2020, float _population, float lat_Start, float lat_End, float lon_Start, float lon_End, int map_StartX, int map_EndX) {
         name = _name;
         lat = _lat;
         lon = _lon*-1;
         cases = _cases;
         deaths = _deaths;
         red_2016 = _red_2016;
         red_2020 = _red_2020;
         population = _population;
         x = map(lon, lon_Start, lon_End, map_EndX, map_StartX)+5;
         y = map(lat, lat_Start, lat_End, displayHeight, 0);
         county_size = PApplet.parseInt (getSize());
    }
    
    public float getSize() {
      //option 1 and 2 = population
      //option 3 = cases 
      return map(cases/population, 0, 1, 20, displayWidth * .25f);
    }
    
    boolean hold_info_box = false;
    
    public boolean mouse_over_circle(int option) {
     float _x, _y;
      if(option == 1) { //map
        _x = x; 
        _y = y;
      } else if(option == 2) { //scatter
        _x = scatter_x;
        _y = scatter_y;
      } else { //transition
        _x = map_TransitionX;
        _y = map_TransitionY;
      }
      
     float diffX =  _x - mouseX; 
     float diffY = _y - mouseY;
     if(diffX*diffX+diffY*diffY < (county_size*county_size)/4) return true; else return false;
    }
    
    public boolean pass_Filter(float value, int filterBy, boolean greater_than, boolean option) {
      /* 1-> cases
         2 -> deaths 
         3 -> % of republican votes
         4 -> population
      */
      
      if(filterBy == 1) {
        return (greater_than == cases >= value);
      } else if (filterBy == 2) {
        return (greater_than == deaths >= value);
      } else if (filterBy == 3) {
        float year;
        
        if(option) year = red_2016; else year = red_2020;
        return (greater_than == year >= value);
      } else if (filterBy == 4) {
        return (greater_than == population >= value);
      } else return true;
  }
    
    public void turn_info_box() {hold_info_box = !hold_info_box;}
    
    public void getColor(boolean option) {
      float year;
      if(option) year = red_2016; else year = red_2020;
      
      if(year > .5f) { //if red won
        very_stinky = lerpColor(color(255, 0, 0, 0), color(255, 0, 0, 200), year);
      } else { //blue won
        very_stinky = lerpColor(color(0, 0, 255, 0), color(0, 0, 255, 200), year);
      }
    }
    
    public void Map_Display(boolean option, boolean circle_selected) {
      //option 1 = 2016 voting
      //option 2 = 2020 voting
      getColor(option);
      float displayed_vote; 
      if(option) displayed_vote = red_2016; else displayed_vote = red_2020;
      if(hold_info_box || (mouse_over_circle(1) && !circle_selected)) {
        fill(255);
        stroke(0);
        float rectX = displayWidth * .06f;
        float rectY = displayHeight * .8f;
        float rectW = displayWidth * .1f;
        float rectH = displayHeight * .14f;
        rect(rectX, rectY, rectW, rectH);
        strokeWeight(5);
        fill(0);
        textSize(30);
        text("County: "+name+"\nPopulation: "+str (PApplet.parseInt (population))+"\nCovid-19 Cases: "+str (PApplet.parseInt (cases))+"\nCovid-19 Deaths: "+str (PApplet.parseInt (deaths))+"\nRepublican Vote: "+str (PApplet.parseInt (100*displayed_vote))+"%\nDemocratic Vote: "+str (PApplet.parseInt ((1 - displayed_vote)*100))+'%', rectX * 1.1f, rectY * 1.01f, rectW * 1.2f, rectH * 1.2f);
      } else noStroke();
      fill(very_stinky);
      ellipse(x, y, county_size, county_size);
       //println(x);
      //println(y);
    }
    
    public void Scatter_Display(boolean option, int scatterX_Begin, int scatterX_End, int scatterY_Begin, int scatterY_End) {
     getColor(option);
     float year;
      
     if(option) year = red_2016; else year = red_2020;
      
     noStroke();
     scatter_x = map(year, 0, 1, scatterX_Begin, scatterX_End);
     scatter_y = map(cases/population, 0, .1f, scatterY_Begin, scatterY_End);
     
     float scatter_size = getSize();

     if(hold_info_box || (mouse_over_circle(2) && !circle_selected)) {
        fill(255);
        stroke(0);
        float rectX = displayWidth * .06f;
        float rectY = displayHeight * .8f;
        float rectW = displayWidth * .1f;
        float rectH = displayHeight * .14f;
        rect(rectX, rectY, rectW, rectH);
        strokeWeight(5);
        fill(0);
        textSize(30);
        text("County: "+name+"\nPopulation: "+str (PApplet.parseInt (population))+"\nCovid-19 Cases: "+str (PApplet.parseInt (cases))+"\nCovid-19 Deaths: "+str (PApplet.parseInt (deaths))+"\nRepublican Vote: "+str (PApplet.parseInt (100*year))+"%\nDemocratic Vote: "+str (PApplet.parseInt ((1 - year)*100))+'%', rectX * 1.1f, rectY * 1.01f, rectW * 1.2f, rectH * 1.2f);
      } else noStroke();
      fill(very_stinky);
      ellipse(scatter_x, scatter_y, scatter_size, scatter_size);
    }
    
    public void transition_Display(boolean option, boolean direction, float transition) {
      getColor(option); 
      noStroke();
      
      float transition_size = getSize();
       float year;
      
       if(option) year = red_2016; else year = red_2020;
       
       if(!direction) {//towards scatter plot
         map_TransitionX = lerp(x, scatter_x, transition/100);
         map_TransitionY = lerp(y, scatter_y, transition/100);
       } else {//towards map
         map_TransitionX = lerp(scatter_x, x, transition/100);
         map_TransitionY = lerp(scatter_y, y, transition/100);
       }
       if(hold_info_box || (mouse_over_circle(3) && !circle_selected)) {
        fill(255);
        stroke(0);
        float rectX = displayWidth * .06f;
        float rectY = displayHeight * .8f;
        float rectW = displayWidth * .1f;
        float rectH = displayHeight * .14f;
        rect(rectX, rectY, rectW, rectH);
        strokeWeight(5);
        fill(0);
        textSize(30);
        text("County: "+name+"\nPopulation: "+str (PApplet.parseInt (population))+"\nCovid-19 Cases: "+str (PApplet.parseInt (cases))+"\nCovid-19 Deaths: "+str (PApplet.parseInt (deaths))+"\nRepublican Vote: "+str (PApplet.parseInt (100*year))+"%\nDemocratic Vote: "+str (PApplet.parseInt ((1 - year)*100))+'%', rectX * 1.1f, rectY * 1.01f, rectW * 1.2f, rectH * 1.2f);
      } else noStroke();
      fill(very_stinky);
      ellipse(map_TransitionX, map_TransitionY, transition_size, transition_size);
    }
}
class Drag_Circle_Scale {
  float x,y,d;
  float offX;
  float offY;
  float left_Bound, right_Bound;
  float scale_Left, scale_Right, max_Cases, max_Deaths, max_Population;
  boolean dragging = false;
  boolean hovering = false;
  int regularColor = color(0);
  int highlightColor = color(128);
  boolean firstPress = false;
  boolean isPressed = false;
  
  int filter = 1;
  boolean greater_than = true;
  
  Drag_Circle_Scale(float _x, float _y, float _d, float left, float right, float sLeft, float sRight, float mcases, float mdeaths, float mpopulation) {
    x = left; y = _y; d = _d; left_Bound = left; right_Bound = right; scale_Left = sLeft; scale_Right = sRight; max_Cases = mcases; max_Deaths = mdeaths; max_Population = mpopulation; 
  }
  
  public void reconfigure(float mcases, float mdeaths, float mpopulation) {
    max_Cases = mcases; max_Deaths = mdeaths; max_Population = mpopulation;
  }
  
  public void flip_Greater_Than() {
   greater_than = !greater_than; 
  }
  
  public void change_Filter(int choice) {
    if(choice == 1) {
        scale_Right = max_Cases;
      } else if (choice == 2) {
          scale_Right = max_Deaths;
      } else if (choice == 3) {
         scale_Right = 100;
      } else if (choice == 4) {
          scale_Right = max_Population;
      } else scale_Right = 0;
    
    filter = choice; 
  }
  
  public float display() {
    fill(255);
    stroke(0);
    strokeWeight(5);
    rect(left_Bound - 20, y - 30, right_Bound - left_Bound + 40, d*4);
    line(left_Bound, y, right_Bound, y);
    
    fill(0);
    //If the mouse is not pressed, stop dragging
    
    
    
    if(!mousePressed) {
      dragging = false;
      firstPress = false;
      isPressed = false;
    }
    //If this is the first time the mouse has been pressed, mark
    //the firstPress event
    else {
      if(isPressed) {
        firstPress = false;
      } else {
        firstPress = true;
        isPressed = true;
      }
    }
    
    //If the item was already being dragged
    if(dragging) {
      x = mouseX + offX;
      if(x > right_Bound) x = right_Bound; else if(x < left_Bound) x = left_Bound; 
    } else { //Otherwise, item not already being dragged...
      //So, if the mouse is now over the item...
      //We used the dist() function in class, this way is faster because
      //it doesn't involve taking a square root, which is a pretty slow operation
      float diffX = x-mouseX;
      float diffY = y-mouseY;
      if(diffX*diffX+diffY*diffY < d*d/4) {
        //If this is the first press of the mouse, then start dragging
        if(firstPress) {
          dragging = true;
          offX = x - mouseX;
        } else { //otherwise, this is just a hover
          hovering = true;
        }
      }
      else { //If the mouse is not over the item, then don't hover
        hovering = false;
      }
    }
    //Now, actually draw the circle
    //noStroke();
    //if(dragging || hovering) fill(highlightColor);
    //else fill(regularColor);
    //ellipse(x,y,d,d);
    
    /* 1-> cases
         2 -> deaths
         3 -> % of republican votes
         4 -> population
      */
      
   fill(0);
   textSize(30);
   if(filter == 1) {
        text("Cases",left_Bound, y + d * 1.5f);
      } else if (filter == 2) {
          text("Deaths",left_Bound, y + d * 1.5f);
      } else if (filter == 3) {
          text("% Republican",left_Bound, y + d * 1.5f);
      } else if (filter == 4) {
          text("Population",left_Bound, y + d * 1.5f);
      } else text("No Filter", left_Bound, y + d * 1.5f);
    
    if(greater_than) {
       text('>', left_Bound + 150, y + d * 2.5f); 
    } else {
      text('<', left_Bound + 150, y + d * 2.5f);
    }
    
    text(PApplet.parseInt (map(x, left_Bound, right_Bound, scale_Left, scale_Right)), x - 50, y - 50);
    
    noStroke();
    fill(0);
    ellipse(x,y,d,d);
    float ret = map(x, left_Bound, right_Bound, scale_Left, scale_Right);;
    if(filter == 3) ret /= 100;
    
    return ret;
  }
}
class State {
  String name;
  String file_Name, image_Name;
  int image_Length, image_Height;
  County[] counties;
  float lat_Start, lat_End, lon_Start, lon_End;
  PImage img;
  float max_Cases, max_Deaths, max_Population;
  int image_xx, image_Print_xx;
  
  int scatterX_StartX, scatterX_EndX, scatterX_Y, scatterY_X, scatterY_StartY, scatterY_EndY;
  
 State(String _name, String _file_Name, String _image_Name, int _image_Length, int _image_Height, float _lat_Start, float _lat_End, float _lon_Start, float _lon_End, float mcases, float mdeaths, float mpopulation) {
   name = _name;
   file_Name = _file_Name;
   image_Name = _image_Name;
   image_Height = _image_Height;
   image_Length = _image_Length;
   lat_Start = _lat_Start;
   lat_End = _lat_End;
   lon_Start = _lon_Start;
   lon_End = _lon_End;
   
   max_Cases = mcases; max_Deaths = mdeaths; max_Population = mpopulation;
   
   image_xx = ((image_Length * displayHeight)/image_Height);
   image_Print_xx = (displayWidth/2) - (image_xx/2);
   
   scatterX_StartX = PApplet.parseInt ((.01f * image_xx) + image_Print_xx);
   scatterX_Y = PApplet.parseInt (.93f * displayHeight);
   scatterX_EndX = PApplet.parseInt ((.98f * image_xx) + image_Print_xx);
   
   scatterY_X = PApplet.parseInt (.03f * image_xx) + image_Print_xx;
   scatterY_StartY = PApplet.parseInt (.95f * displayHeight);
   scatterY_EndY = PApplet.parseInt (.03f * displayHeight);
   
     county_data = loadTable(file_Name, "header");
    int county_index = 0; int rowCount = county_data.getRowCount();
    counties = new County[rowCount];
    img = loadImage(image_Name);
    //println("debug");
    //println(county_data.getRowCount());
    for(TableRow row : county_data.rows()) {
     counties[county_index] = new County(row.getString("name"),
                                         row.getFloat("lat"),
                                         row.getFloat("long"),
                                         row.getFloat("cases"),
                                         row.getFloat("deaths"),
                                         row.getFloat("red_votes_2016"),
                                         row.getFloat("red_votes_2020"),
                                         row.getFloat("population"),
                                         lat_Start,
                                         lat_End,
                                         lon_Start,
                                         lon_End,
                                         image_Print_xx,
                                         image_Print_xx + image_xx);
     county_index += 1; 
   }
 }
 
 public void display_Image() {
   background(255);
   image(img, image_Print_xx, 0, image_xx, displayHeight);
 }
 
 public void display_Scatter() {
    background(255);
    //origin at (50, 50)
    stroke(0);
    strokeWeight(5);
    line(scatterX_StartX, scatterX_Y, scatterX_EndX, scatterX_Y);
    line(scatterY_X, scatterY_StartY, scatterY_X, scatterY_EndY);
    textSize(PApplet.parseInt (displayWidth * .01f));
    fill(0);
    
    text("% Voted Republican", displayWidth/2, displayHeight * 96);
    text("% Cases / Population", scatterX_StartX, displayHeight * .014f);
    //x vals
    for(int r = 0; r < 100; r += 5) {
      text(r, map(r, 0, 100, scatterY_X, scatterX_EndX) + (displayWidth * .0013f), scatterX_Y * 1.02f);
    }
    //y vals
    int compensate_for_Y = PApplet.parseInt (displayHeight * .01f);
    for(int c = 0; c < 15; c += 5) {
      text(c, scatterY_X - (displayWidth * 0.015f), map(c, 0, 10, scatterX_Y - compensate_for_Y, compensate_for_Y + scatterY_EndY));
    }
    
    text("% Voted Republican", displayWidth/2, displayHeight * .97f);
    text("% Cases / Population", scatterX_StartX, displayHeight * .014f);
    
    boolean year = settings.Display();
    
    for(County data_point : selected_State.counties) {
      if(data_point.pass_Filter(scaler.display(), scaler.filter, scaler.greater_than, year)) data_point.Scatter_Display(year, scatterY_X, scatterX_EndX, scatterX_Y, scatterY_EndY);
      if(mousePressed) mouse_clicked_circle(data_point, 2);
    } 
 }
}
class User_Settings {
  boolean option_selected;
  boolean scatter_plot;
  
  int rectX, rectY, rectW, rectH;
  
  int rectUm;
  public boolean Display() {
    textSize(40);
    fill(255);
    stroke(0);
    strokeWeight(5);
    rectX = PApplet.parseInt (displayWidth * .035f);
    rectY = PApplet.parseInt (displayHeight * .11f);   
    rectH = PApplet.parseInt (displayHeight * .035f);
    
    rect(rectX, rectY, rectX, rectH);
    
    fill(0);
    if(option_selected) {
      text("2016", rectX * 1.05f, rectY * 1.05f, 150, 350);
      return true;
    } else {
      text("2020", rectX * 1.05f, rectY * 1.05f, 150, 350);
      return false;
    }
  }
  
  public boolean mouse_over_box() {
    return (((150 < mouseX) && (300 > mouseX)) && ((200  < mouseY) && (500 > mouseY)));
  }
  
  public void switch_year() {option_selected = !option_selected;}
  
  public void switch_graph() {scatter_plot = !scatter_plot;}
  
  User_Settings() {
    option_selected = false;
    scatter_plot = true;
  }
}
  public void settings() {  fullScreen(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "DataViz" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
