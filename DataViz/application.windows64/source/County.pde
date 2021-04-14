class County {
    String name;
    float x,y;
    float lat, lon;
    float cases, deaths;
    float red_2016, red_2020;
    //float blue_2016 = 1 - red_2016;
    float population;
    int county_size;
    color very_stinky;
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
         county_size = int (getSize());
    }
    
    float getSize() {
      //option 1 and 2 = population
      //option 3 = cases 
      return map(cases/population, 0, 1, 20, displayWidth * .25);
    }
    
    boolean hold_info_box = false;
    
    boolean mouse_over_circle(int option) {
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
    
    boolean pass_Filter(float value, int filterBy, boolean greater_than, boolean option) {
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
    
    void turn_info_box() {hold_info_box = !hold_info_box;}
    
    void getColor(boolean option) {
      float year;
      if(option) year = red_2016; else year = red_2020;
      
      if(year > .5) { //if red won
        very_stinky = lerpColor(color(255, 0, 0, 0), color(255, 0, 0, 200), year);
      } else { //blue won
        very_stinky = lerpColor(color(0, 0, 255, 0), color(0, 0, 255, 200), year);
      }
    }
    
    void Map_Display(boolean option, boolean circle_selected) {
      //option 1 = 2016 voting
      //option 2 = 2020 voting
      getColor(option);
      float displayed_vote; 
      if(option) displayed_vote = red_2016; else displayed_vote = red_2020;
      if(hold_info_box || (mouse_over_circle(1) && !circle_selected)) {
        fill(255);
        stroke(0);
        float rectX = displayWidth * .06;
        float rectY = displayHeight * .8;
        float rectW = displayWidth * .1;
        float rectH = displayHeight * .14;
        rect(rectX, rectY, rectW, rectH);
        strokeWeight(5);
        fill(0);
        textSize(30);
        text("County: "+name+"\nPopulation: "+str (int (population))+"\nCovid-19 Cases: "+str (int (cases))+"\nCovid-19 Deaths: "+str (int (deaths))+"\nRepublican Vote: "+str (int (100*displayed_vote))+"%\nDemocratic Vote: "+str (int ((1 - displayed_vote)*100))+'%', rectX * 1.1, rectY * 1.01, rectW * 1.2, rectH * 1.2);
      } else noStroke();
      fill(very_stinky);
      ellipse(x, y, county_size, county_size);
       //println(x);
      //println(y);
    }
    
    void Scatter_Display(boolean option, int scatterX_Begin, int scatterX_End, int scatterY_Begin, int scatterY_End) {
     getColor(option);
     float year;
      
     if(option) year = red_2016; else year = red_2020;
      
     noStroke();
     scatter_x = map(year, 0, 1, scatterX_Begin, scatterX_End);
     scatter_y = map(cases/population, 0, .1, scatterY_Begin, scatterY_End);
     
     float scatter_size = getSize();

     if(hold_info_box || (mouse_over_circle(2) && !circle_selected)) {
        fill(255);
        stroke(0);
        float rectX = displayWidth * .06;
        float rectY = displayHeight * .8;
        float rectW = displayWidth * .1;
        float rectH = displayHeight * .14;
        rect(rectX, rectY, rectW, rectH);
        strokeWeight(5);
        fill(0);
        textSize(30);
        text("County: "+name+"\nPopulation: "+str (int (population))+"\nCovid-19 Cases: "+str (int (cases))+"\nCovid-19 Deaths: "+str (int (deaths))+"\nRepublican Vote: "+str (int (100*year))+"%\nDemocratic Vote: "+str (int ((1 - year)*100))+'%', rectX * 1.1, rectY * 1.01, rectW * 1.2, rectH * 1.2);
      } else noStroke();
      fill(very_stinky);
      ellipse(scatter_x, scatter_y, scatter_size, scatter_size);
    }
    
    void transition_Display(boolean option, boolean direction, float transition) {
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
        float rectX = displayWidth * .06;
        float rectY = displayHeight * .8;
        float rectW = displayWidth * .1;
        float rectH = displayHeight * .14;
        rect(rectX, rectY, rectW, rectH);
        strokeWeight(5);
        fill(0);
        textSize(30);
        text("County: "+name+"\nPopulation: "+str (int (population))+"\nCovid-19 Cases: "+str (int (cases))+"\nCovid-19 Deaths: "+str (int (deaths))+"\nRepublican Vote: "+str (int (100*year))+"%\nDemocratic Vote: "+str (int ((1 - year)*100))+'%', rectX * 1.1, rectY * 1.01, rectW * 1.2, rectH * 1.2);
      } else noStroke();
      fill(very_stinky);
      ellipse(map_TransitionX, map_TransitionY, transition_size, transition_size);
    }
}
